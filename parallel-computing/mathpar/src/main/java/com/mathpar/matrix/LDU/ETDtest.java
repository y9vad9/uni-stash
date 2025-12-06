/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mathpar.matrix.LDU;

import com.mathpar.matrix.LDU.TO.ETDpTO;
import com.mathpar.matrix.MatrixS;
import com.mathpar.number.Element;
import com.mathpar.number.Ring;
import com.mathpar.parallel.webCluster.engine.QueryResult;
import com.mathpar.parallel.webCluster.engine.Tools;
import java.io.IOException;
import mpi.MPI;
import mpi.MPIException;

import static com.mathpar.matrix.LDU.ETDmpi.ETDParralel;
import static com.mathpar.matrix.LDU.ETDmpi.time;
import static com.mathpar.matrix.LDU.ETDmpi.timeExchAndRecoveryAndBack;

/**
 *
 * @author ridkeim
 */
public class ETDtest {
//    public static void main(String[] args) throws MPIException, IOException, ClassNotFoundException {
//        String[] Init = MPI.Init(args);
//        System.out.print("Starting ETD Algo\n");
//        QueryResult queryRes=Tools.getDataFromClusterRootNode(args);
//        Object []ar=queryRes.getData();   
//        ETDmpiUtils.resetTime();
//        int matrix_size = 123,zeroP = 66,resId = ETDpTO.RESULT_LDU;
//        int value_size = Integer.MAX_VALUE;
//        int loggin = 0;
//        if(ar != null && ar.length==4){
//            if(ar[0] instanceof Element){
//                matrix_size = ((Element)ar[0]).intValue();
//            }else{
//                matrix_size = (int) ar[0];
//            }
//            if(ar[1] instanceof Element){
//                zeroP = ((Element)ar[1]).intValue();
//            }else{
//                zeroP = (int) ar[1];
//            }
//            if(ar[2] instanceof Element){
//                resId = ((Element)ar[2]).intValue();
//            }else{
//                resId = (int) ar[2];
//            }
//            if(ar[3] instanceof Element){
//                loggin = ((Element)ar[3]).intValue();
//            }else{
//                loggin = (int) ar[3];
//            }
//        }
//        int tlo[] = new int[]{loggin};
//        MPI.COMM_WORLD.bcast(tlo, tlo.length, MPI.INT, 0);
//        loggin = tlo[0];
//        if(loggin == 1){
//           ETDmpiUtils.setLogging(true);
//        }
//        int myrank = MPI.COMM_WORLD.getRank();
//        int size = MPI.COMM_WORLD.getSize();
//        if(resId>ETDpTO.RESULT_PLDUQWDK || resId<ETDpTO.RESULT_LDU){
//            resId = ETDpTO.RESULT_LDU;
//        }
//        int[][] array = new int[][]{
//            {1780049273,  0,           -1182948631, 1850446829,  -1926744937, -1595715656,1929752249,-1896150967,-1114022780,-1865564329,0,1458501554,1332916898,1305940316,0,1763352987},
//            {0,           -1962573733, 1902711962,  -1268183908, 0,           1211314965,  -1777839015, -1906694299, 1169938253,  1526121315,  0,           1105389004,  -1276796666, 0,           0,           1397983341},
//            {0,           0,           0,           1392225534,  0,           -1167733477, -1125119209, -1411124283, 1339616575,  -1721781255, -1967219600, 1701022670,  1587475588,  0,           0,           1767593676 },
//            {1954173231,  -1810553400, -2053409545, -1377321371, 0,           1562647716,  1111113411,  -1284660568, 0,           -1559628245, -1160797129, 1907938856,  0,           -1091523193, 1439206745,  0          },
//            {1781645048,  1421600798,  0,           2024061477,  -1533133760, 0,           1885184863,  1955840796,  1727570870,  1216060567,  1647665527,  -1716970426, -1717082647, 0,           -1096613560, -1500628753},
//            {-1895314440, 0,           2142377343,  1218379474,  1933378523,  0,           -1202762094, 1348906029,  0,           -1221325235, 1832782511,  -1506413053, -1907106416, -2082336069, -1197961265, -1166538090},
//            {1088631712,  1194736642,  1157083751,  0,           1910523070,  0,           1628768195,  1608908635,  -2064007028, 0,           1129586397,  -2127652287, 1319674645,  -1667582967, -1944630929, -2045897296},
//            {0,           0,           1846776957,  -1794601051, -1427650892, -1214351921, -1900223264, 1237554038,  1284008662,  0,           -1831245265, 1382179607,  0,           0,           0,           1425144323 },
//            {0,           0,           -1242194983, 1642195695,  -1705152107, 0,           1385545609,  1254336365,  1994010091,  -1658720818, -2142365838, 1621296088,  -1971416251, -1178396807, -1338409965, 1861768830 },
//            {0,           -2020715987, 0,           -1436538744, 1584741993,  1612344894,  0,           1309560523,  0,           -1698333616, -1525678245, 1573513324,  1838095272,  -1256866736, 1829393699,  1468172888 },
//            {2094056609,  2026856085,  -2003135530, -1665014665, -1251648863, 1548947921,  0,           1773965664,  1827544973,  -1420347658, -1803083045, 1949253867,  -1444832361, -1326609270, 1970929767,  -1167647519},
//            {0,           -1918703715, -1644362618, -1430757932, -1253544882, 2032446533,  1896931859,  -1412633046, -1325806554, 2001228913,  1277266270,  2014577545,  0,           -1098877495, -1661648548, 0          },
//            {0,           1595611946,  -1413491453, -1436220368, 1292520817,  -1203631830, 1467244459,  -2006893086, -1866896701, 1521021529,  -1402900450, -1217326494, 1179570384,  0,           1662800384,  -1479470358},
//            {-2003002226, -1466308549, 1778591414,  -1911837103, -1917409775, 0,           0,           0,           -1125950913, 0,           1582469062,  0,           1181652652,  1558002161,  -2094207671, 0          },
//            {1998736237,  1340349056,  -1904811030, 1179986410,  1353503720,  -2012187934, -1653140739, 1279514954,  1863958076,  1421212507,  1657135024,  -1973258331, 1074551127,  1416779910,  -1396692448, -1425189254},
//            {0,           1632020836,  -2089702014, 2072532288,  -1868383304, 2030813572,  -1877956669, 1826043410,  0,           1176097503,  1572910854,  -1084571783, 1854309546,  0,           2005534936,  -1774033143}};
//        MatrixS T = new MatrixS(array, Ring.ringZxyz);
////        if(myrank == 0){
////            T =  ETDUtils.randomMatrixS(matrix_size, value_size, zeroP, Ring.ringZxyz);
////            System.out.print("Matrix size = "+matrix_size+"\n");
////            System.out.print("Zero elements = "+zeroP+"%"+"\n");
////            System.out.print("Proc size = "+size+"\n");
////        }
////        T = (MatrixS) ETDmpiUtils.bcastObject(T, 0);
//        long timeZero = System.currentTimeMillis();
//        MatrixS[] LDUParralel = ETDParralel(T,ETDpTO.RESULT_LDU);
//        long ldutime = System.currentTimeMillis()-timeZero;
//        System.out.print("ldu time on proc "+myrank+" is "+ldutime+"\n");
//        System.out.print("dets check time on proc "+myrank+" is "+time+"\n");
//        System.out.print("recovery time on proc "+myrank+" is "+timeExchAndRecoveryAndBack+"\n"); 
//        System.out.print("s/r time on proc "+myrank+" is "+ETDmpiUtils.getTime()+"\n"); 
////        Tools.sendFinishMessage(args);
//        MPI.Finalize();
//    }
}
