/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mathpar.matrix.LDU;

import com.mathpar.matrix.LDU.TO.ETDpTO;
import com.mathpar.matrix.MatrixS;
import com.mathpar.number.Element;
import com.mathpar.number.NFunctionZ32;
import com.mathpar.number.Newton;
import com.mathpar.number.NumberZ;
import com.mathpar.number.Ring;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import mpi.MPI;
import mpi.MPIException;


/**
 *
 * @author ridkeim
 */
public class ETDmpi {
    public static long time = 0;
    public static long timeExchAndRecoveryAndBack = 0;
    //mpirun -np 4 java -cp /home/ridkeim/NetBeansProjects/mathpar/target/classes:/home/ridkeim/.m2/repository/org/slf4j/slf4j-api/1.7.8/slf4j-api-1.7.8.jar com.mathpar.matrix.LSU.ETDmpi
    
    public static MatrixS[] ETDParralel(MatrixS T, int result_id) throws MPIException, IOException, ClassNotFoundException{
        int myrank = MPI.COMM_WORLD.getRank();
        int size = MPI.COMM_WORLD.getSize();
        int root = 0;   
        int decomposition_count = 0;
        NumberZ[] my_primes = getMyPrimes(generateAndExchangePrimes(T,root));
        if(myrank == root){
            System.out.print("REQUESTED PRIMES COUNT = "+(my_primes.length*size)+"\n");
        }
        Map<Track,List<ETDpTO>> tracks = new HashMap<>();
        Map<Track,Map<NumberZ,NumberZ>> dets = new HashMap<>();
        int q = (int) Math.round(Math.sqrt(my_primes.length*size)/size);
        int position = 0;
        int check_position = position+q;
        Track track = null;
        while((track==null) && position<my_primes.length){
            Ring ring = new Ring(new int[]{Ring.Zp}, new int[]{}, new String[]{});
            ring.setMOD(my_primes[position]);
            ETDp etdp = new ETDp(T, ring);
            etdp.getLdu();
            if(ETDmpiUtils.isLogging())System.out.print("decomposition found on "+myrank+"  #"+(size*myrank+position)+"\n");
            List<ETDpTO> p = tracks.get(etdp.getTrack());
            Map<NumberZ, NumberZ> d = dets.get(etdp.getTrack());
            if(p==null){
                p = new ArrayList<>();                    
                tracks.put(etdp.getTrack(), p);                                
            }
            if(d==null){
                d = new HashMap<>();
                dets.put(etdp.getTrack(), d);
            }
            p.add(etdp.getTO(result_id));
            d.put(etdp.getMod(), etdp.getDet());
            
            if(check_position==position || position == my_primes.length-1){
                long timeNow = System.currentTimeMillis();
                track = checkDetsMaps(dets, root); 
                if(track==null){
                    if(position==my_primes.length-1){
                        my_primes = getMyPrimes(extendPrimes(2*size*my_primes.length,root));
                        if(myrank==0){
                            System.out.print("generating new primes"+"\n");
                        }
                    }
                    check_position = position+q;                    
                }
                time +=System.currentTimeMillis()-timeNow;
            }   
            decomposition_count++;
            position++;
        }
        System.out.print("founded decompositons on proc "+myrank+" is "+ decomposition_count+"\n");
        long timeRecovery = System.currentTimeMillis();
        List<ETDpTO> etd_for_restore = tracks.get(track);
        if (etd_for_restore == null) {
            etd_for_restore = new ArrayList<>();
        }
        
        List<ETDpTO> parts = exchangeParts(etd_for_restore);
        checkParts(parts);
        ETDpTO restored_part = ETDpTO.getInstance(result_id);
        restored_part.restore(parts, myrank);
        ETDpTO[] restored_parts = sandBackToRoot(restored_part,root);
        if(myrank==root){
            
            boolean flag = true;
            for (int i = 0; flag && i < restored_parts.length; i++) {
                if(restored_parts[i] == null){
                    flag = false;
                }
            }
        }
        ETDpTO result = joinOnRoot(restored_parts,root,result_id);
        timeExchAndRecoveryAndBack+=System.currentTimeMillis()-timeRecovery;
        if(result == null){
            return null;
        } 
        return result.generateResult(T.size,track);
    }
    
    private static void checkParts(List<ETDpTO> parts) throws MPIException{
        boolean flag = true;
        int myrank = MPI.COMM_WORLD.getRank();
        for (ETDpTO part : parts) {
            if(!flag){
                return;
            }
            if(part.getPartNumber() != myrank){
                flag = false;
            }            
        }
    }
    
    private static NumberZ[] generateAndExchangePrimes(MatrixS T,int root) throws MPIException, IOException, ClassNotFoundException {
        NumberZ[] primes = null;
        Integer primes_size = null;
        int myrank = MPI.COMM_WORLD.getRank();
        int size = MPI.COMM_WORLD.getSize();
        if(root== myrank){
            Element hadamard = T.adamarNumberNotZero(Ring.ringZxyz);    
            primes = NFunctionZ32.primesBigLimit(hadamard, size);
            primes_size = primes.length;
        }
        if(ETDmpiUtils.isLogging())System.out.print("Primes generated"+"\n");
        primes_size = ETDmpiUtils.bcastObject(primes_size, root);
        if(root!=myrank){
            primes = new NumberZ[primes_size];
        }
        ETDmpiUtils.bcastArray(primes, root);
        return primes;
    }
    
    @SuppressWarnings("unchecked")
    private static List<ETDpTO> exchangeParts(List<ETDpTO> etd_for_restore) throws MPIException, IOException, ClassNotFoundException{
        if(ETDmpiUtils.isLogging())System.out.print("Exchange parts metod\n");
        int myrank = MPI.COMM_WORLD.getRank();
        int size = MPI.COMM_WORLD.getSize();
        
        Map<Integer, List<ETDpTO>> parts= new HashMap<>();
        for (int i = 0; i < size; i++) {
            parts.put(i, new ArrayList<ETDpTO>());
        }
        for (ETDpTO etd_item : etd_for_restore) {
            if(etd_item == null){
                System.out.print("item is null"+"\n");
            }
            ETDpTO[] parts_t = etd_item.split_into_parts(size);
            for (int i = 0; i < parts_t.length; i++) {
                parts.get(i).add(parts_t[i]);
            }
        }
        if(ETDmpiUtils.isLogging())System.out.print("All parts splitted\n");
        return ETDmpiUtils.AlltoAllv(parts);
    }
     private static ETDpTO[] sandBackToRoot(ETDpTO restored_part, int root) throws IOException, MPIException, ClassNotFoundException {
        int myrank = MPI.COMM_WORLD.getRank();
        int size = MPI.COMM_WORLD.getSize();
        ETDpTO[] result = new ETDpTO[size];
        List<ETDpTO> list = ETDmpiUtils.gathervSeparated(restored_part, root);
        for (ETDpTO list1 : list) {
            result[list1.getPartNumber()] = list1;
        }
        return result;
    }

    private static ETDpTO joinOnRoot(ETDpTO[] restored_parts, int root,int result_id) throws MPIException {
        int myrank = MPI.COMM_WORLD.getRank();
        if(myrank!=root){
//            System.out.print("Do not generating result on proc "+ myrank+"\n");
            return null;
        }else{
//            System.out.print("Generating result on proc "+ myrank+"\n");
        }                
        if(restored_parts.length == 1){
            return restored_parts[0];
        }else{
            ETDpTO res = ETDpTO.getInstance(result_id);
            res.join(restored_parts);
            return res;
        }
    }

    private static NumberZ[] extendPrimes(int length, int root) throws MPIException, IOException, ClassNotFoundException {
        NumberZ[] primes = new NumberZ[length];
        int myrank = MPI.COMM_WORLD.getRank();
        int size = MPI.COMM_WORLD.getSize();
        if(root==myrank){
            primes = NFunctionZ32.primesBig(length);
            System.out.print("EXTENDING PRIMES"+"\n");
        }
        ETDmpiUtils.bcastArray(primes, root);
        return primes;
    }

    private static NumberZ[] getMyPrimes(NumberZ[] primes) throws MPIException {
        int size = MPI.COMM_WORLD.getSize();
        int rank = MPI.COMM_WORLD.getRank();
        NumberZ[] my_primes = new NumberZ[primes.length/size];
        for (int i = 0; i < my_primes.length; i++) {
            my_primes[i]= primes[i*size+rank];
        }
        return my_primes;
    }
    
    private static Track checkDetsMaps(Map<Track, Map<NumberZ, NumberZ>> dets, int root) throws MPIException {
        Track resTrack = null;
        int minSize = Integer.MAX_VALUE;
        int myrank = MPI.COMM_WORLD.getRank();
        Map<Track, Map<NumberZ, NumberZ>> map;
        map = mergeListOfMaps(ETDmpiUtils.gathervSeparated(dets, root));
        for (Map.Entry<Track,Map<NumberZ, NumberZ>> entrySet : map.entrySet()) {
            Track key = entrySet.getKey();
            Map<NumberZ, NumberZ> value = entrySet.getValue();
            if(recoverDetCheck(value) && value.size()<minSize){
                minSize = value.size();
                resTrack = key;
            }
        }
        resTrack = ETDmpiUtils.bcastObject(resTrack, root);
        return resTrack;
    }
    
    private static Map<Track, Map<NumberZ, NumberZ>> mergeListOfMaps(List<Map<Track, Map<NumberZ, NumberZ>>> list){
        Map<Track, Map<NumberZ, NumberZ>> m = new HashMap<>();
        if(list == null || list.isEmpty()) return m;
        for (Map<Track, Map<NumberZ, NumberZ>> listEl : list) {
            for (Map.Entry<Track, Map<NumberZ, NumberZ>> entrySet : listEl.entrySet()) {
                Track key = entrySet.getKey();
                Map<NumberZ, NumberZ> value = entrySet.getValue();
                Map<NumberZ, NumberZ> getValue = m.get(key);
                if(getValue==null){
                    getValue = new HashMap<>();
                    m.put(key, getValue);
                }
                getValue.putAll(value);                
            }
        }
        return m;
    }

    private static boolean recoverDetCheck(Map<NumberZ, NumberZ> value){
        if(value.size()<2) return false;
        NumberZ[] mod = new NumberZ[value.size()-1];
        NumberZ[] rem = new NumberZ[value.size()-1];
//        NumberZ[] mod1 = new NumberZ[1];
//        NumberZ[] rem1 = new NumberZ[1];
        NumberZ[] mod01 = new NumberZ[value.size()];
        NumberZ[] rem01 = new NumberZ[value.size()];
        int t = 0;
        for (Iterator<Map.Entry<NumberZ, NumberZ>> iterator = value.entrySet().iterator(); iterator.hasNext();) {
            Map.Entry<NumberZ, NumberZ> next = iterator.next();
            if(iterator.hasNext()){
                mod[t] = next.getKey();
                rem[t] = next.getValue();
                mod01[t] = next.getKey();
                rem01[t] = next.getValue();
            }else{
//                mod1[0] = next.getKey();
//                rem1[0] = next.getValue();                
                mod01[mod01.length-1] = next.getKey();
                rem01[rem01.length-1] = next.getValue();
            }
            t++;
        }
        NumberZ recov0 = Newton.recoveryNewton(mod, rem);      
//          System.out.print("recovered 0 ="+recov0+"\n");
        NumberZ recov1 = Newton.recoveryNewton(mod01, rem01);      
//        NumberZ[] numbNewton = arrayOfNumbersForNewton(mod);
//        NumberZ[] recov = recoveryNewtonWithoutArr(mod, rem, numbNewton);        
//        NumberZ[] numbNewton1 = arrayOfNumbersForNewtonProceed(mod01, numbNewton, mod.length);        
//        NumberZ recov1 =  recoveryNewtonWithoutArrProceed(mod1, rem1, numbNewton1, recov);
        boolean result = recov1.subtract(recov0).isZero(Ring.ringZxyz);
        if(result) System.out.print("ENOUGH PRIMES COUNT = "+mod.length+"\n");
        return result;
    }
    public static void main(String[] args){
            parallelStart(args);
    }
   
    private static void parallelStart(String[] args){        
        try {
            MPI.Init(args);
            int myrank = MPI.COMM_WORLD.getRank();
            int root = 0;
            int matrix_size = 16;
            int value_size = Integer.MAX_VALUE;
            int zeroP = 20;
            int resId = 1;
            int logging = 1;
            if(args.length == 4){
                matrix_size= Integer.valueOf(args[0]);
                zeroP = Integer.valueOf(args[1]);
                resId = Integer.valueOf(args[2]);
                logging = Integer.valueOf(args[3]);
            }
            int[][] array = new int[][]{
            {1780049273,  0,           -1182948631, 1850446829,  -1926744937, -1595715656,1929752249,-1896150967,-1114022780,-1865564329,0,1458501554,1332916898,1305940316,0,1763352987},
            {0,           -1962573733, 1902711962,  -1268183908, 0,           1211314965,  -1777839015, -1906694299, 1169938253,  1526121315,  0,           1105389004,  -1276796666, 0,           0,           1397983341},
            {0,           0,           0,           1392225534,  0,           -1167733477, -1125119209, -1411124283, 1339616575,  -1721781255, -1967219600, 1701022670,  1587475588,  0,           0,           1767593676 },
            {1954173231,  -1810553400, -2053409545, -1377321371, 0,           1562647716,  1111113411,  -1284660568, 0,           -1559628245, -1160797129, 1907938856,  0,           -1091523193, 1439206745,  0          },
            {1781645048,  1421600798,  0,           2024061477,  -1533133760, 0,           1885184863,  1955840796,  1727570870,  1216060567,  1647665527,  -1716970426, -1717082647, 0,           -1096613560, -1500628753},
            {-1895314440, 0,           2142377343,  1218379474,  1933378523,  0,           -1202762094, 1348906029,  0,           -1221325235, 1832782511,  -1506413053, -1907106416, -2082336069, -1197961265, -1166538090},
            {1088631712,  1194736642,  1157083751,  0,           1910523070,  0,           1628768195,  1608908635,  -2064007028, 0,           1129586397,  -2127652287, 1319674645,  -1667582967, -1944630929, -2045897296},
            {0,           0,           1846776957,  -1794601051, -1427650892, -1214351921, -1900223264, 1237554038,  1284008662,  0,           -1831245265, 1382179607,  0,           0,           0,           1425144323 },
            {0,           0,           -1242194983, 1642195695,  -1705152107, 0,           1385545609,  1254336365,  1994010091,  -1658720818, -2142365838, 1621296088,  -1971416251, -1178396807, -1338409965, 1861768830 },
            {0,           -2020715987, 0,           -1436538744, 1584741993,  1612344894,  0,           1309560523,  0,           -1698333616, -1525678245, 1573513324,  1838095272,  -1256866736, 1829393699,  1468172888 },
            {2094056609,  2026856085,  -2003135530, -1665014665, -1251648863, 1548947921,  0,           1773965664,  1827544973,  -1420347658, -1803083045, 1949253867,  -1444832361, -1326609270, 1970929767,  -1167647519},
            {0,           -1918703715, -1644362618, -1430757932, -1253544882, 2032446533,  1896931859,  -1412633046, -1325806554, 2001228913,  1277266270,  2014577545,  0,           -1098877495, -1661648548, 0          },
            {0,           1595611946,  -1413491453, -1436220368, 1292520817,  -1203631830, 1467244459,  -2006893086, -1866896701, 1521021529,  -1402900450, -1217326494, 1179570384,  0,           1662800384,  -1479470358},
            {-2003002226, -1466308549, 1778591414,  -1911837103, -1917409775, 0,           0,           0,           -1125950913, 0,           1582469062,  0,           1181652652,  1558002161,  -2094207671, 0          },
            {1998736237,  1340349056,  -1904811030, 1179986410,  1353503720,  -2012187934, -1653140739, 1279514954,  1863958076,  1421212507,  1657135024,  -1973258331, 1074551127,  1416779910,  -1396692448, -1425189254},
            {0,           1632020836,  -2089702014, 2072532288,  -1868383304, 2030813572,  -1877956669, 1826043410,  0,           1176097503,  1572910854,  -1084571783, 1854309546,  0,           2005534936,  -1774033143}};
            MatrixS T = new MatrixS(array, Ring.ringZxyz);
//            MatrixS T = null;
//            if(root==myrank){
//                T = ETDUtils.randomMatrixS(matrix_size, value_size, zeroP, Ring.ringZxyz);
//                System.out.print("matrix size="+matrix_size+"\n");
//                System.out.print("zeroP="+zeroP+"%\n");
//            }
            ETDmpiUtils.resetTime();
            ETDmpiUtils.setLogging(logging==1);
            T = (MatrixS) ETDmpiUtils.bcastObject(T, root);
//            MatrixS T = null;
            if(root==myrank){
                T = ETDUtils.randomMatrixS(matrix_size, value_size, zeroP, Ring.ringZxyz);
                System.out.println("matrix size=\n"+matrix_size);
                System.out.println("value size=\n"+value_size);
                System.out.println("zeroP=\n"+zeroP);
//                System.out.println(T);
            }
            T = (MatrixS) ETDmpiUtils.bcastObject(T, root);
            long timeZero = System.currentTimeMillis();
            MatrixS[] LDUParralel = ETDParralel(T,ETDpTO.RESULT_LDU);
            long ldutime = System.currentTimeMillis()-timeZero;
            System.out.print("ldu time on proc "+myrank+" is "+ldutime+"\n");
            System.out.print("dets check time on proc "+myrank+" is "+time+"\n");
            System.out.print("recovery time on proc "+myrank+" is "+timeExchAndRecoveryAndBack+"\n");
            System.out.print("s/r time on proc "+myrank+" is "+ETDmpiUtils.getTime()+"\n");
            MPI.Finalize();
        } catch (MPIException | IOException | ClassNotFoundException ex) {
            Logger.getLogger(ETDmpi.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
   
}
