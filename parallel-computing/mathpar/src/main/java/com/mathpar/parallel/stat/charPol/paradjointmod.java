/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mathpar.parallel.stat.charPol;

import java.io.IOException;
import java.util.Random;
import com.mathpar.matrix.MatrixS;
import mpi.MPI;
import mpi.MPIException;
import mpi.Request;
import mpi.Status;
import com.mathpar.number.Element;
import com.mathpar.number.NumberZp32;
import com.mathpar.number.Ring;
import com.mathpar.parallel.utils.MPITransport;
import com.mathpar.polynom.Polynom;


/**
 *
 * @author andy
 */
public class paradjointmod {

    public static void main(String[] args) throws IOException, ClassNotFoundException, CloneNotSupportedException {

        try{
           
            MPI.Init(new String[0]);
            int rank = MPI.COMM_WORLD.getRank();
            int mpiSize = MPI.COMM_WORLD.getSize();

            if(rank==0){
                
                long t1=System.currentTimeMillis();
                
             int size=4;
             int mdens=100;
             String r_str="Z[x,y,z]";
             String rp_str="Zp32[x,y,z]";
             int pdens=100;
             int vars=2;
             int cbits=8;
             int deg=2;
              
                
                for(int i=0; i<args.length; i++){
                 System.out.println("args[i]="+args[i]);
                 if(args[i].contains("size"))  size=Integer.parseInt(args[i].replace("size=",""));
                 if(args[i].contains("mdens")) mdens=Integer.parseInt(args[i].replace("mdens=",""));
                 if(args[i].contains("ring"))  {r_str=(args[i].replace("ring=","")); rp_str=r_str.replace("Z","Zp32"); }
                 if(args[i].contains("deg"))   deg=Integer.parseInt(args[i].replace("deg=",""));
                 if(args[i].contains("pdens")) pdens=Integer.parseInt(args[i].replace("pdens=",""));
                 if(args[i].contains("vars"))  vars=Integer.parseInt(args[i].replace("vars=",""));
                 if(args[i].contains("cbits")) cbits=Integer.parseInt(args[i].replace("cbits=",""));
                 if(args[i].contains("size"))  size=Integer.parseInt(args[i].replace("size=",""));
               }
             
                 Ring r = new Ring(r_str);
                 Ring ring = new Ring(rp_str);
                int[] polArr=new int[vars+2];
                for(int i=0; i<vars; i++) polArr[i]=deg;
                polArr[polArr.length-2]=pdens;
                polArr[polArr.length-1]=cbits;
                
                System.out.println("ring="+r.toString()+"  "+ring.toString());
             
             
              MatrixS a = new MatrixS(size, size, mdens, polArr, new Random(), r.numberONE(), r);
               // System.out.println("m="+a.toString(r));
                //MatrixS a1=a.adjoint(r);
               // System.out.println("m="+a1.toString(r));
                
              //рассылка колец
              r.cleanRing();
                  ring.cleanRing();
              Object[] rings=new Object[]{r,ring};
              //MPI.COMM_WORLD.Bcast(rings, 0, 2, MPI.OBJECT, 0);
              MPITransport.bcastObjectArray(rings, 2, 0);
              
              //рассылка входной матрицы
              Object[] t=new Object[]{a};
              //MPI.COMM_WORLD.Bcast(t, 0, 1, MPI.OBJECT, 0);
              MPITransport.bcastObjectArray(t, 1, 0);
              
              // вычисляем необходимое кол-во моделей по каждой 
              //переменной. в последнюю записываем кол для чисел
              int[] mod_count=a.adamar(r); 
              int gen_mod_count=1;
              for (int i=0; i<mod_count.length; i++){
                  gen_mod_count=gen_mod_count*mod_count[i];
                  System.out.println("modcount="+mod_count[i]);
              }
              
              //заполняем массив со значениями модулей
              int[][] data=setOfAllModules(mod_count,mpiSize, rank);
              //метод вычисления рез.  по каждому модулю  a - -исзодная матрица, modules_res[] - результаты,
              MatrixS[] modules_res=eachProcCalcModules(mod_count, data, a, ring, mpiSize, rank);
              
              int gen_matrix_element_count=0;
              for (int i=0; i<modules_res[0].M.length; i++) gen_matrix_element_count+=modules_res[0].M[i].length;
              int matrix_element_count_pp=gen_matrix_element_count/mpiSize;
              int mecpp_r=gen_matrix_element_count%mpiSize;
              if (rank<mecpp_r) matrix_element_count_pp++;
              Element [][] allRResult=new Element[gen_mod_count][];   
       MPI.COMM_WORLD.barrier();
              for(int i=0; i<modules_res.length; i++){
               sendMatrixElements(modules_res[i],i, gen_matrix_element_count,rank, mpiSize, r, allRResult);
              }
       MPI.COMM_WORLD.barrier();
              for(int i=0; i<(gen_mod_count-modules_res.length); i++){
              // System.out.println("recv="+i);
               recvMatrixElements(gen_matrix_element_count,rank, mpiSize, r, allRResult);
              }
              
       MPI.COMM_WORLD.barrier();
         System.out.println("GOOOOOD");
                
             //  for(int i=0; i<allRResult.length; i++){
                  // System.out.print("drank="+drank);
                 //  for(int j=0; j<allRResult[i].length; j++)
                //   System.out.print(" "+allRResult[i][j].toString(r));
               //    System.out.println("");
               //  }
              // System.out.println("GOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOD");
              
               // System.out.println("res="+modules_res[0].toString(r)); 
               
               
               
                Element[][] recovery=new Element[data[0].length][allRResult[0].length];
               for(int rp=0; rp<allRResult[0].length; rp++){
                for (int number = 0; number < data[0].length; number++) {
                    ring.setMOD32(data[0][number]);
                    Element [][] rem=new Element[gen_mod_count/data[data.length-1].length][data[data.length-1].length];
                    int k=0;
                    for(int i=0; i<rem.length; i++){
                        for(int j=0; j<rem[i].length; j++){
                            rem[i][j]=(Polynom)allRResult[k][0];
                            k++;
                        }
                    }
                    int var=0;
                    for (int p=2; p<data.length+1; p++){
                      Element[] temp = new Element[rem.length];
                       // System.out.println("var"+var);
                      for (int l=0; l<rem.length; l++){
                         // for(int d=0; d<rem[l].length; d++ ) System.out.println("rem="+rem[l][d]);
                          temp[l]=(Polynom.recoveryOfLin(rem[l], 0, var, data[0][number], ring))[0];
                         // System.out.println("temp========="+temp[l].toString(ring));
                      }
                      var++;
                      rem=new Element[rem.length/data[data.length-p].length][data[data.length-p].length];
                      k=0;
                      for(int i=0; i<rem.length; i++){
                          for(int j=0; j<rem[i].length; j++){
                              rem[i][j]=temp[k];
                              k++;
                              
                          }
                      }
                      
                    }
                    recovery[number][rp]=(Element) rem[0][0].clone();
//                    for (int i=0; i<rem.length; i++) rem[i]=(Polynom)allRResult[i][0];
//                    for (int i = data.length - 2; i > 0; i--) {
//                        System.out.println("i==========" + i);
//                        Polynom[] temp = new Polynom[i+1];
//
//                        for (int polmod = 0; polmod < temp.length; polmod++) {
//                            temp[polmod] = (polynom.Polynom.recoveryOfLin(rem, 0, i, data[0][number], ring))[0];
//                            System.out.println("f=" + temp[polmod].toString(ring));
//                        }
//                        rem = new Polynom[temp.length];
//                        for (int k = 0; k < rem.length; k++) {
//                            rem[k] = temp[k];
//                        }
//                    }
//                    recovery[number]=rem[0];
//                    System.out.println("rem=" + rem.length);
//                    System.out.println("pol=" + rem[0].toString(ring));
//                    System.out.println("data[0]=" + data[0].length);
                    
                }
               }
                //for(int ll=0; ll<recovery[0].length; ll++) System.out.println("recovery="+recovery[0][ll].toString(ring));
//               for(int i=0; i<48; i++){
//               Status s=MPI.COMM_WORLD.Probe(MPI.ANY_SOURCE, MPI.ANY_TAG);
//                 int n=s.Get_count(MPI.BYTE);
//                // System.out.println("probe good");
//                 Object[] buf=new Object[1];
////                  System.out.println("source="+s.source);
////                  System.out.println("tag="+s.tag);
//                 // System.out.println("recv");
//                  buf[0]=((Object[])recvObject(s.source, s.tag,n))[0];
//                   System.out.println("recv");
//               }
            //  recvFinal(recovery, mpiSize, r);
               
               Element[][] recv=new Element[mpiSize][];
              recv[0]=recovery[0];
//               for(int i=1; i<mpiSize; i++){
//                     Status s=MPI.COMM_WORLD.iProbe(MPI.ANY_SOURCE,MPI.ANY_TAG);
//                 // recv[i]=recvArrElement(i,100+i);
//                   recv[i]=(Element[]) MPITransport.recvObject(s.getSource(), s.getCount(MPI.BYTE));
//                }
               for(int i =1 ; i< mpiSize; i++){
                  recv[i] = (Element[]) MPITransport.recvObject(i, 100);
                  }
               long t2=System.currentTimeMillis();
                System.out.println("**************************************************");
                System.out.println("*************************************time="+(t2-t1));
                System.out.println("**************************************************");
               for (int i=0; i<recv.length; i++){
                   System.out.println("recv[i]="+recv[i].length);
               }
               
            }else{
             // Ring r = new Ring("Z[x,y]");
              Object[] rings=new Object[2];
              //MPI.COMM_WORLD.Bcast(rings, 0, 2, MPI.OBJECT, 0);
              MPITransport.bcastObjectArray(rings, 2, 0);
              Ring r =(Ring)rings[0];
              Ring ring =(Ring)rings[1];
              
              
              Object[] t=new Object[1];
              //MPI.COMM_WORLD.Bcast(t, 0, 1, MPI.OBJECT, 0);
              MPITransport.bcastObjectArray(t, 1, 0);
              MatrixS a=(MatrixS)t[0];
            
              int[] mod_count=a.adamar(r);
              int gen_mod_count=1;
              for (int i=0; i<mod_count.length; i++){
                  gen_mod_count=gen_mod_count*mod_count[i];
              }
              //заполняем массив со значениями модулей
              int[][] data=setOfAllModules(mod_count,mpiSize, rank);
              //метод вычисления рез.  по каждому модулю  a - -исзодная матрица, modules_res[] - результаты,
              MatrixS[] modules_res=eachProcCalcModules(mod_count, data, a, ring, mpiSize, rank);
              
              int gen_matrix_element_count=0;
              for (int i=0; i<modules_res[0].M.length; i++) gen_matrix_element_count+=modules_res[0].M[i].length;
              int matrix_element_count_pp=gen_matrix_element_count/mpiSize;
              int mecpp_r=gen_matrix_element_count%mpiSize;
              if (rank<mecpp_r) matrix_element_count_pp++;
              Element [][] allRResult=new Element[gen_mod_count][];    
         MPI.COMM_WORLD.barrier();
              for(int i=0; i<modules_res.length; i++){
               sendMatrixElements(modules_res[i],i, gen_matrix_element_count,rank, mpiSize, r, allRResult);
              }
         MPI.COMM_WORLD.barrier();
              for(int i=0; i<(gen_mod_count-modules_res.length); i++){
               recvMatrixElements(gen_matrix_element_count,rank, mpiSize, r, allRResult);
              }
         MPI.COMM_WORLD.barrier();
         System.out.println("GOOOOOD");
         
         
//              for(int i=0; i<allRResult.length; i++){
//                  // System.out.print("drank="+drank);
//                   for(int j=0; j<allRResult[i].length; j++)
//                   System.out.print(" "+allRResult[i][j].toString(r));
//                   System.out.println("");
//                 }
           //     System.out.println("GOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOD");
//              for(int i=0; i<modules_res.length; i++){
//               recvMatrixElements(gen_matrix_element_count,rank, mpiSize, r, allRResult);
//              }
//               for(int i=0; i<allRResult.length; i++){
//                  // System.out.print("drank="+drank);
//                   System.out.println(" r="+allRResult[i]);
//                 }
                System.out.println("allresLENGTH="+allRResult.length);
                 // Ring ring  = new Ring("Zp32[x,y]");
                Element[][] recovery=new Element[data[0].length][allRResult[0].length];
               for(int rp=0; rp<allRResult[0].length; rp++){
                for (int number = 0; number < data[0].length; number++) {
                    ring.setMOD32(data[0][number]);
                    Element [][] rem=new Element[gen_mod_count/data[data.length-1].length][data[data.length-1].length];
                    int k=0;
                    for(int i=0; i<rem.length; i++){
                        for(int j=0; j<rem[i].length; j++){
                            rem[i][j]=allRResult[k][0];
                            k++;
                        }
                    } 
                    int var=0;
                    for (int p=2; p<data.length+1; p++){
                      Element[] temp = new Element[rem.length];
                       // System.out.println("var"+var);
                      for (int l=0; l<rem.length; l++){
                         // for(int d=0; d<rem[l].length; d++ ) System.out.println("rem="+rem[l][d]);
                          temp[l]=(Polynom.recoveryOfLin(rem[l], 0, var, data[0][number], ring))[0];
                         // System.out.println("temp========="+temp[l].toString(ring));
                      }
                      var++;
                      rem=new Element[rem.length/data[data.length-p].length][data[data.length-p].length];
                      k=0;
                      for(int i=0; i<rem.length; i++){
                          for(int j=0; j<rem[i].length; j++){
                              rem[i][j]=temp[k];
                              k++;
                              
                          }
                      }
                      
                    }
                    recovery[number][rp]=(Element) rem[0][0].clone();
//                    for (int i=0; i<rem.length; i++) rem[i]=(Polynom)allRResult[i][0];
//                    for (int i = data.length - 2; i > 0; i--) {
//                        System.out.println("i==========" + i);
//                        Polynom[] temp = new Polynom[i+1];
//
//                        for (int polmod = 0; polmod < temp.length; polmod++) {
//                            temp[polmod] = (polynom.Polynom.recoveryOfLin(rem, 0, i, data[0][number], ring))[0];
//                            System.out.println("f=" + temp[polmod].toString(ring));
//                        }
//                        rem = new Polynom[temp.length];
//                        for (int k = 0; k < rem.length; k++) {
//                            rem[k] = temp[k];
//                        }
//                    }
//                    recovery[number]=rem[0];
//                    System.out.println("rem=" + rem.length);
//                    System.out.println("pol=" + rem[0].toString(ring));
//                    System.out.println("data[0]=" + data[0].length);
                    
                }
               }
               
           
               // System.out.println("recovery:ength="+recovery[0][0].toString(r));
//             for(int i=0; i<recovery[0].length; i++){
//               Object[] o=new Object[]{recovery[0][0]};
//              sendObject(o, 0, rank);
//             }
                System.out.println("iam rank="+rank);
               //sendFinal(recovery, rank);
                
                Element[] buf=new Element[recovery[0].length];
                for(int i=0; i<buf.length; i++){
                    buf[i]=(Element) recovery[0][i].clone();
                }
           
               // sendArrElement(buf, 0, 100+rank);
                MPITransport.sendObject(recovery[0], 0, 100);
            }
            

        MPI.Finalize();
        } catch (MPIException e) { System.out.println("error="+e.toString()+"  "+e.getMessage());
        }

    }
    
    
    
     
     
//     
//    public static Element[] getElements(MatrixS m, int start, int count,){
//        int r=0;
//        int c=0;
//        for(int i=0; i<m.M.length; i++){
//            c=start-m.M[i].length;
//            if(c<0) break;
//        }
//            
//            
//        
//        return null;
//    }
    
    
  
    public static void sendMatrixElements(MatrixS m,int index, int count, int rank, int mpiSize, Ring ring, Element[][] allRResult) throws MPIException, IOException{
        int mecpp_r=count%mpiSize;
         int r=0;
         int c=0;
         
        
         for (int drank=mpiSize-1; drank>-1; drank--){
           int mec_pp=count/mpiSize;
           if(drank<mecpp_r) mec_pp++;
           Element[] buf=new Element[mec_pp];
           for (int i=0; i<mec_pp; i++){
               buf[i]=m.M[r][c];
               c++;
               if(c>m.M[r].length-1) {c=0; r++;}
               
           }
           //после заполнения буфера - делаем посылку,  если попадаем на свой процессор, то не посылаем а сразу записываем
             if (drank != rank) {
                 MPITransport.iSendObject(buf, drank, index);
                 //MPI.COMM_WORLD.Isend(buf, 0, buf.length-1, MPI.OBJECT, drank, index);
                 //send
             } else {
                 int cfp = allRResult.length / mpiSize;
                 int rfp = allRResult.length % mpiSize;
                 int step = 0;
                 if (rank <= rfp) {
                     step = rank * ( cfp + 1 ) ;
                 } else {
                     step = ( cfp + 1 ) * rfp + ( rank - rfp ) * cfp;
                 }
                 allRResult[step+index]=buf;
                 
                
             }
         }
        
    }
    
    
     public static void recvMatrixElements(int count, int rank, int mpiSize, Ring ring, Element[][] allRResult) throws MPIException, IOException, ClassNotFoundException{
        int mecpp_r=count%mpiSize;
         int r=0;
         int c=0;
        // for (int k=0; k<mpiSize-1; k++){
        //     System.out.println("k="+k);
//           for (int i=0; i<mec_pp; i++){
//               buf[i]=m.M[r][c];
//               c++;
//               if(c>m.M[r].length-1) {c=0; r++;}
//               
//           }
           //после заполнения буфера - делаем посылку,  если попадаем на свой процессор, то не посылаем а сразу записываем
            // if (k != rank) {
                                 
                 //System.out.println("probe");
                 Status s=MPI.COMM_WORLD.probe(MPI.ANY_SOURCE, MPI.ANY_TAG);
                 int n=s.getCount(MPI.BYTE);
                // System.out.println("probe good");
                  int mec_pp=count/mpiSize;
                  if(s.getSource()<mecpp_r) mec_pp++;
                  Element[] buf;
//                  System.out.println("source="+s.source);
//                  System.out.println("tag="+s.tag);
                 // System.out.println("recv");
                  buf=(Element[])MPITransport.recvObject(s.getSource(), s.getTag());
                // MPI.COMM_WORLD.Recv(buf, 0, buf.length-1, MPI.OBJECT, s.source, s.tag);
                // System.out.println("recv good");
                 int cfp = allRResult.length / mpiSize;
                 int rfp = allRResult.length % mpiSize;
                 int step = 0;
                 if (s.getSource() <= rfp) {
                     step = s.getSource() * ( cfp + 1 ) ;
                 } else {
                     step = ( cfp + 1 ) * rfp + ( s.getSource() - rfp ) * cfp;
                 }
                 //System.out.println("step="+(step+s.tag));
                 allRResult[step+s.getTag()]=buf;
                 
                 //send
           //  } else {
               //  System.out.println("no");
//                 int cfp = allRResult.length / mpiSize;
//                 int rfp = allRResult.length % mpiSize;
//                 int step = 0;
//                 if (rank <= rfp) {
//                     step = rank * ( cfp + 1 ) ;
//                 } else {
//                     step = ( cfp + 1 ) * rfp + ( rank - rfp ) * cfp;
//                 }
//                 System.out.println("step="+(step+index));
//                 allRResult[step+index]=buf;
                 
                
            // }
       //  }
        
    }
     
     
      
     
        
    public static int [] indexFor(int proc,int count_proc, int[][] data){
        int g_count=1;
        for (int i=0; i<data.length; i++) g_count=g_count*data[i].length;
        
        int cfp=g_count/count_proc;
        int rfp=g_count%count_proc;
        int step=0;
        if(proc<=rfp){
         step=proc*(cfp+1)-1;
        }else{
            step=(cfp+1)*rfp+(proc-rfp)*cfp;
        }
        int[] indexes=new int[data.length];
        int k=data.length-1;
        for (int i=data.length-1; i>0; i--){
            if(i==data.length-1){
              int temp=step;
              if(proc<=rfp) temp++;
              indexes[i-1]=temp/data[k].length;
              indexes[i]=temp%data[k].length;
            }else{
              indexes[i-1]=indexes[i]/data[k].length;
              indexes[i]=indexes[i]%data[k].length;  
            }
            k--;
        }
      //  for (int i=0; i<indexes.length; i++) System.out.println("indexes["+i+"]="+indexes[i]);
        
        return indexes;
    }
    
    //вычисление матриц по указанным модулям 
    public static MatrixS[] calcModules(MatrixS m, int[][] data, int[] indexes, int counts, Ring r) {
        MatrixS[] res = new MatrixS[counts];
        for (int k = 0; k < counts; k++) {
            //Ring rm = new Ring("Zp32[x,y]");
            r.setMOD32(data[0][indexes[0]]);
            res[k] = (MatrixS) m.toNewRing(r.algebra[0], r);
            NumberZp32[] vars = new NumberZp32[indexes.length - 1];
            for (int h = 0; h < vars.length; h++) {
                vars[h] = new NumberZp32(data[h + 1][indexes[h + 1]]);
            }

            for (int i = 0; i < m.M.length; i++) {
                for (int j = 0; j < m.M[i].length; j++) {
                    res[k].M[i][j] = new Polynom(((Polynom) res[k].M[i][j]).value(vars, r));
                }
            }
            res[k]=res[k].adjoint(r);
            
            nextIndexes(data, indexes);

        }


        return res;
    }
    
    public static void nextIndexes(int data[][], int[] indexes){
        int k=data.length-1;
        for (int i=data.length-1; i>0; i--){
            if(i==data.length-1){
              indexes[i-1]=indexes[i-1]+(indexes[i]+1)/data[k].length; 
              indexes[i]=(indexes[i]+1)%data[k].length;
            }else{
              indexes[i-1]=indexes[i-1]+(indexes[i])/data[k].length; 
              indexes[i]=(indexes[i])%data[k].length;
            }
            k--;
        }
      //  for (int i=0; i<indexes.length; i++) System.out.println("indexes["+i+"]="+indexes[i]);
    }
    
    
     
  
     
    public static int[][] setOfAllModules(int[] mod_count, int proc_count, int rank){
          //заведем данные по модулям
              modDATA md=new modDATA();
              //кол-во числовых модулей
              int cmn=mod_count[mod_count.length-1];
              //заполняем массив простыми числовыми модулями модулями со сдвигом
              int[] num_modules=md.getModules(cmn,0);
            //  for (int i=0; i<num_modules.length; i++) System.out.println("rank="+rank+" | "+num_modules[i]);
              //двумерный массив - числовые модули + модули по каждой переменной
              int[][] data=new int[mod_count.length][];
              //в нулевую стоку записываем числовые модули (передаем ссылку)
              data[0]=num_modules;
              ///вычисляем кол-во модулей всего - сначала полиномиальных
              int generalcount=1;
              for (int i=0; i<mod_count.length-1; i++){
                  int m=1;
                  data[i+1]=new int[mod_count[i]];
                  generalcount=generalcount*mod_count[i];
                  for(int k=0; k<mod_count[i]; k++) {data[i+1][k]=m; m++;}
              }
        return data;
    }
    
    
    
    
    public static MatrixS[] eachProcCalcModules(int[] mod_count, int[][] data, MatrixS a, Ring r, int proc_count, int rank){
            
              // получаем общее кол-во модулей
              int generalcount=1;
              for (int i=0; i<data.length; i++){
                generalcount=generalcount*data[i].length;
              }
                
//              for (int i=0; i<data.length; i++){
//                  for(int k=0; k<data[i].length; k++) {System.out.print(" "+data[i][k]); }
//                  System.out.println("");
//              }
               //вычисляем стартовые позиции модулей в массиве всех модулей для каждого процессора
               int[] indexes=indexFor(rank,proc_count, data);
               
               //вычисляем сколько модулей надо посчитать на каждом процессоре
               int eachProcModCount=generalcount/proc_count;//целое число моделй
               int rr=generalcount%proc_count; // распределяем остаток модулей
               if (rank<rr) eachProcModCount++;
               System.out.println("calc count modules="+eachProcModCount);
               MatrixS[] res=calcModules(a, data, indexes, eachProcModCount, r);
               return res;
     }
     
     
//    public static void sendArrElement(Element[] a, int proc, int tag) throws IOException, MPIException{
//        
//        int[] number=new int[]{a.length};
//        MPI.COMM_WORLD.Send(number,0,1, MPI.INT,proc, tag);
//        
//        Object o[]=new Object[a.length];
//        for (int i=0; i<o.length; i++){
//            o[i]=a[i].clone();
//        }
//        System.out.println("send");
//        MPI.COMM_WORLD.Send(o, 0, o.length,MPI.OBJECT, proc, tag);
//        System.out.println("sended");
//    }
//    
//    public static Element[] recvArrElement(int proc, int tag) throws MPIException, IOException, ClassNotFoundException, CloneNotSupportedException{
//        
//        
//        int[] number=new int[1];
//        MPI.COMM_WORLD.Recv(number,0,1, MPI.INT,proc, tag);
//        System.out.println("nnnn="+number[0]);
//        Object[] o=new Object[number[0]];
//        Element[] a=new Element[number[0]];
//        MPI.COMM_WORLD.Recv(o, 0,o.length,MPI.OBJECT, proc, tag);
//     //   System.out.println("recv");
//        for (int i=0; i<o.length; i++){
//            a[i]=(Element)(o[i]);
//        }
//        return a;
//       // Status s=MPI.COMM_WORLD.Probe(proc,tag);
////        int n=5950;
////        System.out.println("nnnnnnn2="+n);
////        byte[] arr=new byte[n];// заводится байт массив с нужным числом элементов
////        MPI.COMM_WORLD.Recv(arr, 0, n,MPI.BYTE, proc, tag);//Recv = блокирующий прием массива из буффера ввода в массив arr
////        ByteArrayInputStream bais=new ByteArrayInputStream(arr);
////        ObjectInputStream is=new ObjectInputStream(bais);
////        System.out.println("test");
////        Element[] recv=new Element[number[0]]; 
////        for(int i=0; i<recv.length; i++){
////          recv[i]=(Element)is.readObject();
////        }
////        System.out.println("final");
////        return recv;
//    }
//    
//    
//      public static void isendObject(Object a, int proc, int tag) throws MPIException, IOException{
//                    /**MPIException нужен т.к. мы пользуемся в этом методе обращением к МPI
//                      *Object a = посылаемый объект, имеет интерфейс serilizeable, т.е. существует механизм превращающий его в массив байтов и обратно
//                      *int proc = номер процессора которому посылаем объект
//                      *int tag = таг который пошлем с этим объектом
//                      */
//          
//        byte[] temp=MPI.COMM_WORLD.Object_Serialize(new Object[]{a},0,1,MPI.OBJECT); //метод Object_Serialize() превращает массив объектов в массив байтов
//        MPI.COMM_WORLD.Isend(temp, 0, temp.length, MPI.BYTE, proc, tag);  //, MPI.BYTE= тип передаваемых данных
//        
//        /**Isend = неблокирующая пересылка
//                      *temp = массив который посылаем
//                      *0 = номер элемента массива с которого начинам
//                      *temp.length = число посылаемых элементов массива
//                      *MPI.BYTE = тип элементов в массиве
//                      *proc = номер процессора которыму посылаем сообщение
//                      *tag = таг сообщение
//                      */
//    }
//         public static void sendObject(Object a, int proc, int tag) throws MPIException, IOException{
//                    /**MPIException нужен т.к. мы пользуемся в этом методе обращением к МPI
//                      *Object a = посылаемый объект, имеет интерфейс serilizeable, т.е. существует механизм превращающий его в массив байтов и обратно
//                      *int proc = номер процессора которому посылаем объект
//                      *int tag = таг который пошлем с этим объектом
//                      */
//
//          
//        byte[] temp=MPI.COMM_WORLD.Object_Serialize(new Object[]{a},0,1,MPI.OBJECT); //метод Object_Serialize() превращает массив объектов в массив байтов
//        Request r=MPI.COMM_WORLD.Isend(temp, 0,  MPI.BYTE, proc, tag);  //, MPI.BYTE= тип передаваемых данных
//        r.wait();
//        /**Isend = неблокирующая пересылка, r.Wait - ждет конца послыки
//                      *temp = массив который посылаем
//                      *0 = номер элемента массива с которого начинам
//                      *temp.length = число посылаемых элементов массива
//                      *MPI.BYTE = тип элементов в массиве
//                      *proc = номер процессора которыму посылаем сообщение
//                      *tag = таг сообщение
//                      */
//    }
//
//    public static Object recvObject(int proc, int tag, int n) throws MPIException, IOException, ClassNotFoundException{
//                    /**proc = номер процессора от которого получаем объект
//                      *tag = таг который пришел с объектом
//                      */  
//       // Status s=MPI.COMM_WORLD.Probe(proc, tag);  //команда считывает статус буфера для приема сообщения от процессора proc с тагом tag  
//      // int n=s.Get_count(MPI.BYTE); //динамический метод класса статус который подсчитывает количество элементов в буфере (в данном случае MPI.BYTE)
//     //  Status s=MPI.COMM_WORLD.Probe(proc, tag);
//      // n=s.Get_count(MPI.BYTE);
//        byte[] arr=new byte[n];// заводится байт массив с нужным числом элементов
//        MPI.COMM_WORLD.recv(arr, 0, MPI.BYTE, proc, tag);//Recv = блокирующий прием массива из буффера ввода в массив arr
////         ByteArrayInputStream bais=new ByteArrayInputStream(arr);
////         ObjectInputStream is=new ObjectInputStream(bais);
////         Object a=is.readObject();
//          Object[] res=new Object[1]; // заводится массив объектов длинной 1
//         MPI.COMM_WORLD.Object_Deserialize(res, arr, 0, 1, MPI.OBJECT);//процедура обратная для Serialize
//        return res[0]; // передаем на выход процедуры полученный объект
//
//    }
    
//     public static void sendArrayOfObjects(Object[] a , int proc, int tag) throws MPIException{
//            for(int i=0;i<a.length;i++) sendObject(a[i], proc, tag+i);
//            System.out.println("New send 4 matrices to proc N"+ proc);
//    }
   
     
}
