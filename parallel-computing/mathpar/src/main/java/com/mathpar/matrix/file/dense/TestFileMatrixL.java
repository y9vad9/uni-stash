
package com.mathpar.matrix.file.dense;

import java.util.*;
import java.io.*;

import com.mathpar.matrix.file.dm.MatrixL;
import com.mathpar.matrix.file.ops.MatrixOpsL;
import com.mathpar.matrix.file.utils.BaseMatrixDir;

import com.mathpar.number.Ring;

/**
 * <p>Title: ParCA</p>
 *
 * <p>Description: ParCA - parallel computer algebra system</p>
 *
 * <p>Copyright: Copyright (c) ParCA Tambov, 2005,2006,2007</p>
 *
 * <p>Company: ParCA Tambov</p>
 *
 * @author Yuri Valeev
 * @version 0.5
 */
public class TestFileMatrixL {
    public static void main(String[] args) throws IOException{
        long mod=123456;
        Random rnd=new Random(12345);

        //очистить базовую директорию
        BaseMatrixDir.clearMatrixDir();

        //тест equals
        testEquals(mod);

        //тест copy
        testCopy(rnd,mod);

        //тест delete
        testDelete(rnd,mod);

        //тест +,-,*
        testAllOpsAddSubMul(rnd,mod);

        //тест keep, конструктора из root
        testKeepConstr(rnd,mod);

        //тест ZERO,ONE
        testZERO_ONE();

        //тест negate
        testNegate(rnd,mod);

        //тест JoinCopyNullNotNull
        testJoinCopyNullNotNull(rnd,mod);

        //тест JoinMoveNullNotNull
        testJoinMoveNullNotNull(rnd,mod);

        //тест SplitCopyMove
        testSplitCopyMove(rnd,mod);

        //тест save/restore
        testSaveRestore(rnd,mod);

        System.gc();
    }


    private static void testEquals(long mod)
        throws IOException{
        System.out.println("====================================");
        System.out.println("=========== Test equals ==========");
        System.out.println("====================================");
        //fm1=fm2
        testEquals1(512,512,5000,12345,0,
                    512,512,5000,12345,0,
                    mod, true);
        testEquals1(512,512,5000,12345,1,
                    512,512,5000,12345,1,
                    mod, true);
        testEquals1(512,512,5000,12345,3,
                    512,512,5000,12345,3,
                    mod, true);
        //fm1!=fm2
        //depth1!=depth2
        testEquals1(512,512,5000,12345,1,
                    512,512,5000,12345,3,
                    mod, false);
        //depth1=depth2
        //size1!=size2
        testEquals1(512,512,9000,12345,3,
                    1024,512,9000,12345,3,
                    mod, false);
        //size1=size2, els1!=els2
        testEquals1(512,512,1000,12345,0,
                    512,512,9000,12346,0,
                    mod, false);
        testEquals1(512,512,1000,12345,1,
                    512,512,9000,12346,1,
                    mod, false);
        testEquals1(512,512,1000,12345,3,
                    512,512,9000,12346,3,
                    mod, false);
        System.gc();
    }


    private static void testEquals1(int m1, int n1, int den1,long seed1, int depth1,
                                    int m2, int n2, int den2,long seed2, int depth2,
                                    long mod, boolean rightRes)
        throws IOException{  Ring ring=new Ring("Zp32[]"); ring.setMOD32(mod);
        //Если объекты Random равны (с одинаковыми начальными значениями),
        //то fm1=fm2
        Random rnd=new Random(seed1);
        FileMatrixL fm1 = new FileMatrixL(depth1, m1, n1, den1, rnd, mod);
        rnd=new Random(seed2);
        FileMatrixL fm2 = new FileMatrixL(depth2, m2, n2, den2, rnd, mod);

        //проверить, что результат сравнения fm1 и fm2 равен rightRes
        boolean eq=fm1.equals(fm2,mod,ring);
        System.out.printf("equals: fm1->'%s', fm2->'%s', fm1=fm2 -- %s, rightRes=%s\n",
                          fm1.getRoot(), fm2.getRoot(), eq, rightRes);
        if (eq!=rightRes) {
            throw new RuntimeException("Error????!!!");
        }
    }




    private static void testCopy(Random rnd, long mod)
        throws IOException{
        System.out.println("====================================");
        System.out.println("=========== Test copy ==========");
        System.out.println("====================================");
        testCopy1(512,512,7000,0,rnd,mod);
        testCopy1(512,512,7000,1,rnd,mod);
        testCopy1(512,512,7000,3,rnd,mod);
        System.gc();
    }


    private static void testCopy1(int m, int n, int den,
                                  int depth, Random rnd, long mod)
        throws IOException{ Ring ring=new Ring("Zp32[]"); ring.setMOD32(mod);
        FileMatrixL fm1 = new FileMatrixL(depth, m, n, den, rnd, mod);
        //fm1-->fm2
        FileMatrixL fm2=fm1.copy();

        //fm1=?=fm2
        boolean eq=fm1.equals(fm2,mod,ring);
        System.out.printf("testCopy: fm1->'%s', fm2->'%s', depth=%d, status=%s\n",
                          fm1.getRoot(), fm2.getRoot(), fm1.getDepth(),
                          eq?"Ok":"Failed???!!!");
        if (!eq) {
            throw new RuntimeException("Error????!!!");
        }
    }


    private static void testDelete(Random rnd, long mod)
        throws IOException{
        System.out.println("====================================");
        System.out.println("=========== Test delete ==========");
        System.out.println("====================================");
        testDelete1(512,512,5000,0,rnd,mod);
        testDelete1(512,512,5000,1,rnd,mod);
        testDelete1(512,512,5000,3,rnd,mod);
        testDelete1(512,512,5000,5,rnd,mod);
        System.gc();
    }



    private static void testDelete1(int m, int n, int den,
                                    int depth, Random rnd, long mod)
        throws IOException{
        FileMatrixL fm=new FileMatrixL(depth,m,n,den,rnd,mod);
        File root=fm.getRoot();
        boolean delOk=fm.delete();
        boolean factOk=!root.exists();
        System.out.printf("delete: root='%s', depth=%d, delOk=%s, factOk=%s\n",
                          fm.getRoot(),fm.getDepth(),
                          delOk,factOk);
        if (!delOk || !factOk) {
            throw new RuntimeException("Error");
        }
    }


    private static void testAllOpsAddSubMul(Random rnd, long mod)
        throws IOException{
        System.out.println("====================================");
        System.out.println("=========== Test +,-,* ==========");
        System.out.println("====================================");
        //=========== Тесты с фиксированным именем результата ==========
        //1)==================== depth=0
        //+
        testAllOpsAddSubMul1(1000, 1000, 1000, 1000, 7000, mod, rnd, 0, 0, true,
                             new File(BaseMatrixDir.getMatrixDirFile(), "res1"));
        //-
        testAllOpsAddSubMul1(1000, 1000, 1000, 1000, 7000, mod, rnd, 0, 1, true,
                             new File(BaseMatrixDir.getMatrixDirFile(), "res2"));
        // *
        testAllOpsAddSubMul1(500, 400, 400, 600, 7000, mod, rnd, 0, 2, true,
                             new File(BaseMatrixDir.getMatrixDirFile(), "res3"));
        //2)==================== depth=1
        //+
        testAllOpsAddSubMul1(1000, 1000, 1000, 1000, 7000, mod, rnd, 1, 0, true,
                             new File(BaseMatrixDir.getMatrixDirFile(), "res4"));
        //-
        testAllOpsAddSubMul1(1000, 1000, 1000, 1000, 7000, mod, rnd, 1, 1, true,
                             new File(BaseMatrixDir.getMatrixDirFile(), "res5"));
        // *
        testAllOpsAddSubMul1(500, 400, 400, 600, 7000, mod, rnd, 1, 2, true,
                             new File(BaseMatrixDir.getMatrixDirFile(), "res6"));
        //3)==================== depth>1 (3)
        //+
        testAllOpsAddSubMul1(1000, 1000, 1000, 1000, 7000, mod, rnd, 3, 0, true,
                             new File(BaseMatrixDir.getMatrixDirFile(), "res7"));
        //-
        testAllOpsAddSubMul1(1000, 1000, 1000, 1000, 7000, mod, rnd, 3, 1, true,
                             new File(BaseMatrixDir.getMatrixDirFile(), "res8"));
        // *
        testAllOpsAddSubMul1(512, 400, 400, 600, 7000, mod, rnd, 3, 2, true,
                             new File(BaseMatrixDir.getMatrixDirFile(), "res9"));

        //=========== Тесты с автоматической генерацией имен результата ==========
        //1)==================== depth=0
        //+
        testAllOpsAddSubMul1(1000, 1000, 1000, 1000, 7000, mod, rnd, 0, 0, false, null);
        //-
        testAllOpsAddSubMul1(1000, 1000, 1000, 1000, 7000, mod, rnd, 0, 1, false, null);
        // *
        testAllOpsAddSubMul1(500, 400, 400, 600, 7000, mod, rnd, 0, 2, false, null);
        //2)==================== depth=1
        //+
        testAllOpsAddSubMul1(1000, 1000, 1000, 1000, 7000, mod, rnd, 1, 0, false, null);
        //-
        testAllOpsAddSubMul1(1000, 1000, 1000, 1000, 7000, mod, rnd, 1, 1, false, null);
        // *
        testAllOpsAddSubMul1(500, 400, 400, 600, 7000, mod, rnd, 1, 2, false, null);
        //3)==================== depth>1 (3)
        //+
        testAllOpsAddSubMul1(1000, 1000, 1000, 1000, 7000, mod, rnd, 3, 0, false, null);
        //-
        testAllOpsAddSubMul1(1000, 1000, 1000, 1000, 7000, mod, rnd, 3, 1, false, null);
        // *
        testAllOpsAddSubMul1(512, 400, 400, 600, 7000, mod, rnd, 3, 2, false, null);

        System.gc();
    }


    final static String[] opNames={"+","-","*"};

    /**
     * 0 : +
     * 1 : -
     * 2 : *
     * @param m int
     * @param n int
     * @param k int
     * @param den int
     * @param mod long
     * @param rnd Random
     * @param depth int
     * @param operator int
     * @param fixName boolean
     * @param resPath File
     * @throws IOException
     */
    public static void testAllOpsAddSubMul1(int rows1, int cols1,
                                            int rows2,int cols2,
                                            int den, long mod, Random rnd,
                                            int depth, int operator,
                                            boolean fixName, File resPath) throws
        IOException {
        //создать матрицы m1 и m2 типа MatrixL
        //m1 -- rows1 x cols1, m2 -- rows2 x cols2
        MatrixL m1=new MatrixL(rows1,cols1,den,mod,rnd);
        MatrixL m2=new MatrixL(rows2,cols2,den,mod,rnd);

        //m1,m2 --> fm1,fm2 типа FileMatrixL
        FileMatrixL fm1=new FileMatrixL(depth, m1);
        FileMatrixL fm2=new FileMatrixL(depth, m2);

        //fm1 op fm2 = fmres
        FileMatrixL fmres;
        long tf1=System.currentTimeMillis();
        switch (operator) {
            case 0: //+
                if (fixName) {
                    fmres=fm1.add(fm2,resPath,mod);
                } else {
                    fmres=fm1.add(fm2,mod);
                }
                break;
            case 1: //-
                if (fixName) {
                    fmres=fm1.subtract(fm2,resPath,mod);
                } else {
                    fmres=fm1.subtract(fm2,mod);
                }
                break;
            case 2: //*
                if (fixName) {
                    fmres=fm1.multCU(fm2,resPath,mod);
                } else {
                    fmres=fm1.multCU(fm2,mod);
                }
                break;
            default:
                throw new RuntimeException(String.format(
                    "Wrong operation: %d.",operator));
        }
        long tf2=System.currentTimeMillis();
        long timef=tf2-tf1;

        //fmres --> mres типа MatrixL
        MatrixL mres=fmres.toMatrixL();

        //проверить, что m1 op m2 == mres ?
        MatrixL mresRight;
        long t1=System.currentTimeMillis();
        switch (operator) {
            case 0: //+
                mresRight=m1.add(m2,mod);
                break;
            case 1: //-
                mresRight=m1.subtract(m2,mod);
                break;
            case 2: //*
                mresRight=m1.multCU(m2,mod);
                break;
            default:
                throw new RuntimeException(String.format(
                    "Wrong operation: %d.",operator));
        }
        long t2=System.currentTimeMillis();
        long time=t2-t1;

        boolean eq=mres.equals(mresRight, mod);
        System.out.printf("%s: (%dx%d) %s (%dx%d) ('%s' %s '%s' = '%s') depth=%d, filetime=%d ms, memtime=%d ms >>>>>  status: %s\n\n",
                          (fixName?"Fix name": "Auto name"),
                          rows1,cols1,opNames[operator], rows2,cols2,
                          fm1.getRoot(),opNames[operator], fm2.getRoot(),fmres.getRoot(),
                          depth,
                          timef,time,
                          (eq?"Ok!":"Error?? Not equals!")
            );

        //выбросить ошибку в случае, когда не равны
        if (!eq) {
            throw new RuntimeException("Not equals????!!!");
        }
    }


    private static void testKeepConstr(Random rnd, long mod)
        throws IOException{
        System.out.println("====================================");
        System.out.println("===== Test keep, FM(root) ==========");
        System.out.println("====================================");
        testKeepConstr1(512,512,5000,rnd,mod,0);
        testKeepConstr1(512,512,5000,rnd,mod,1);
        testKeepConstr1(512,512,5000,rnd,mod,3);
        testKeepConstr1(512,512,5000,rnd,mod,5);
        System.gc();
    }


    private static void testKeepConstr1(int m,int n,int den,Random rnd, long mod,
                                        int depth)
        throws IOException{
        File root=testKeepConstr2(m,n,den,rnd,mod,depth);
        //root-->fm
        FileMatrixL fm=new FileMatrixL(root);
        //проверить размеры, глубину
        int[] size=fm.getFullSize();
        boolean ok=((size[0]==m) && (size[1]==n) && fm.getDepth()==depth);
        System.out.printf("testKeepConstr: root='%s', fm.getDepth()=%d, depth=%d, ok=%s\n",
                          fm.getRoot(),fm.getDepth(),depth,
                          (ok?"Yeees!":"No-o-o-o???"));
        if (!ok) {
            throw new RuntimeException("Error");
        }
    }

    private static File testKeepConstr2(int m,int n,int den,Random rnd, long mod,
                                        int depth)
        throws IOException{
        FileMatrixL fm=new FileMatrixL(depth,m,n,den,rnd,mod);
        File root=fm.getRoot();

        //сохранить
        fm.keep();
        return root;
    }


    private static void testZERO_ONE()
        throws IOException{
        System.out.println("====================================");
        System.out.println("========== Test ZERO, ONE ==========");
        System.out.println("====================================");
        //ZERO
        testZERO_ONE1(512,512,0, 0);
        testZERO_ONE1(512,512,1, 0);
        testZERO_ONE1(512,512,3, 0);
        testZERO_ONE1(512,512,5, 0);
        //ONE
        testZERO_ONE1(512,512,0, 1);
        testZERO_ONE1(512,512,1, 1);
        testZERO_ONE1(512,512,3, 1);
        testZERO_ONE1(512,512,5, 1);
        System.gc();
    }


    //type=0 - ZERO, =1 - ONE
    private static void testZERO_ONE1(int m,int n,int depth, int type)
        throws IOException{
        //fm=ZERO или fm=ONE (FML)
        FileMatrixL fm;
        switch (type) {
            case 0:
                fm=FileMatrixL.ZERO(m,n,depth,Ring.ringR64xyzt);
                break;
            case 1:
                fm=FileMatrixL.ONE(m,depth);
                break;
            default:
                throw new RuntimeException(String.format(
                    "Wrong type: %d.",type));
        }

        //fm (FML) --> matr (ML)
        MatrixL matr=fm.toMatrixL();

        //matrRight=ZERO или matrRight=ONE (ML)
        MatrixL matrRight;
        switch (type) {
            case 0:
                matrRight=new MatrixL(m,n);
                break;
            case 1:
                matrRight=MatrixL.ONE(m);
                break;
            default:
                throw new RuntimeException(String.format(
                    "Wrong type: %d.",type));
        }

        //matr=?=matrRight
        boolean eq=matr.equals(matrRight);
        System.out.printf("testZERO_ONE: root='%s', fm.getDepth()=%d, ok=%s\n",
                          fm.getRoot(),fm.getDepth(),
                          (eq?"Yeees!":"No-o-o-o???"));
        //выбросить ошибку в случае, когда не равны
        if (!eq) {
            throw new RuntimeException("Not equals????!!!");
        }
    }


    private static void testNegate(Random rnd, long mod)
        throws IOException{
        System.out.println("====================================");
        System.out.println("============= Test Negate ==========");
        System.out.println("====================================");
        testNegate1(512,512,5000,rnd,mod,0);
        testNegate1(512,512,5000,rnd,mod,1);
        testNegate1(512,512,5000,rnd,mod,3);
        testNegate1(512,512,5000,rnd,mod,5);
        System.gc();
    }


    private static void testNegate1(int m,int n,int den,Random rnd, long mod,
                                    int depth)
        throws IOException{ Ring ring=new Ring("Zp32[]"); ring.setMOD32(mod);
        //создать fm1 (FML)
        FileMatrixL fm1=new FileMatrixL(depth,m,n,den,rnd,mod);
        //fm2=-fm1
        FileMatrixL fm2=fm1.negate(mod,ring);
        //fm3=fm1+fm2
        FileMatrixL fm3=fm1.add(fm2,mod);
        //fm3=?=zero
        FileMatrixL zero=FileMatrixL.ZERO(m,n,depth,ring);
        boolean eq=fm3.equals(zero,mod,ring);
        System.out.printf("testNegate: rootNeg='%s', neg.getDepth()=%d, ok=%s\n",
                          fm2.getRoot(),fm2.getDepth(),
                          (eq?"Yeees!":"No-o-o-o???"));
        //выбросить ошибку в случае, когда не равны
        if (!eq) {
            throw new RuntimeException("Not equals????!!!");
        }
    }


    private static void testJoinCopyNullNotNull(Random rnd, long mod)
        throws IOException{
        System.out.println("=================================================");
        System.out.println("============= Test JoinCopyNullNotNull ==========");
        System.out.println("=================================================");
        //нет null
        testJoinCopyNullNotNull1(512,512,5000,rnd,mod, 0,
                                 new boolean[]{true,true,true,true},
                                 false);
        testJoinCopyNullNotNull1(512,512,5000,rnd,mod, 1,
                                 new boolean[]{true,true,true,true},
                                 false);
        testJoinCopyNullNotNull1(512,512,5000,rnd,mod, 3,
                                 new boolean[]{true,true,true,true},
                                 false);
        //есть null
        testJoinCopyNullNotNull1(512,512,5000,rnd,mod, 0,
                                 new boolean[]{false,true,false,true},
                                 true);
        testJoinCopyNullNotNull1(512,512,5000,rnd,mod, 1,
                                 new boolean[]{false,false,true,true},
                                 true);
        testJoinCopyNullNotNull1(512,512,5000,rnd,mod, 3,
                                 new boolean[]{true,false,false,true},
                                 true);

        System.gc();
    }


    private static void testJoinCopyNullNotNull1(int m,int n,int den,Random rnd, long mod,
                                                 int depth, boolean[] where, boolean hasNulls)
        throws IOException{
        FileMatrixL[] matrs=genFMS(m,n,den,rnd,mod,depth,where);
        FileMatrixL res=FileMatrixL.joinCopy(matrs,hasNulls,Ring.ringR64xyzt);
        System.out.printf("testJoinCopyNullNotNull: depth=%d", depth);
        checkJoinFMS(matrs,res);
        System.out.println("...............[OK]");
    }




    private static void testJoinMoveNullNotNull(Random rnd, long mod)
        throws IOException{
        System.out.println("=================================================");
        System.out.println("============= Test JoinMoveNullNotNull ==========");
        System.out.println("=================================================");
        //нет null
        testJoinMoveNullNotNull1(512,512,5000,rnd,mod, 0,
                                 new boolean[]{true,true,true,true},
                                 false);
        testJoinMoveNullNotNull1(512,512,5000,rnd,mod, 1,
                                 new boolean[]{true,true,true,true},
                                 false);
        testJoinMoveNullNotNull1(512,512,5000,rnd,mod, 3,
                                 new boolean[]{true,true,true,true},
                                 false);
        //есть null
        testJoinMoveNullNotNull1(512,512,5000,rnd,mod, 0,
                                 new boolean[]{false,true,false,true},
                                 true);
        testJoinMoveNullNotNull1(512,512,5000,rnd,mod, 1,
                                 new boolean[]{false,false,true,true},
                                 true);
        testJoinMoveNullNotNull1(512,512,5000,rnd,mod, 3,
                                 new boolean[]{true,false,false,true},
                                 true);

        System.gc();
    }


    private static void testJoinMoveNullNotNull1(int m,int n,int den,Random rnd, long mod,
                                                 int depth, boolean[] where, boolean hasNulls)
        throws IOException{
        FileMatrixL[] matrs=genFMS(m,n,den,rnd,mod,depth,where);
        FileMatrixL res=FileMatrixL.joinMove(matrs,hasNulls,Ring.ringR64xyzt);
        System.out.printf("testJoinMoveNullNotNull: depth=%d", depth);
        checkJoinFMS(matrs,res);
        System.out.println("...............[OK]");
    }


    private static FileMatrixL[] genFMS(int m,int n,int den,Random rnd, long mod,
                                        int depth, boolean[] where)
        throws IOException{
        FileMatrixL[] matrs=new FileMatrixL[4];
        for (int i = 0; i < 4; i++) {
            if (where[i]) {
                matrs[i]=new FileMatrixL(depth,m,n,den,rnd,mod);
            }
        }
        return matrs;
    }


    private static MatrixL joinFMStoML(FileMatrixL[] matrs)
        throws IOException{
        MatrixL[] mmatrs=new MatrixL[4];
        for (int i = 0; i < 4; i++) {
            if (matrs[i]!=null) {
                mmatrs[i]=matrs[i].toMatrixL();
            }
        }
        MatrixL res=(MatrixL)new MatrixOpsL().join(mmatrs);
        return res;
    }


    private static void checkJoinFMS(FileMatrixL[] matrs, FileMatrixL res)
        throws IOException{
        MatrixL m1=joinFMStoML(matrs);
        MatrixL m2=res.toMatrixL();
        if (!m1.equals(m2)){
            throw new RuntimeException("Not equals????!!!");
        }
    }


    private static void testSplitCopyMove(Random rnd, long mod)
        throws IOException{
        System.out.println("===================================");
        System.out.println("============= Test split ==========");
        System.out.println("===================================");
        //copy
        testSplitCopyMove1(512,512,7000,rnd,mod,1,0);
        testSplitCopyMove1(512,512,7000,rnd,mod,3,0);
        testSplitCopyMove1(512,512,7000,rnd,mod,5,0);

        //move
        testSplitCopyMove1(512,512,7000,rnd,mod,1,1);
        testSplitCopyMove1(512,512,7000,rnd,mod,3,1);
        testSplitCopyMove1(512,512,7000,rnd,mod,5,1);

        System.gc();
    }

    //type=0 - copy, =1 - move
    private static void testSplitCopyMove1(int m,int n,int den,Random rnd,
                                           long mod, int depth, int type)
        throws IOException{
        System.out.printf("testSplitCopyMove: depth=%d", depth);
        //fm
        FileMatrixL fm=new FileMatrixL(depth,m,n,den,rnd,mod);
        FileMatrixL copy=fm.copy();

        //fm (FML) --> fms (FML[])
        FileMatrixL[] fms;
        switch (type) {
            case 0:
                fms=fm.splitCopy();
                break;
            case 1:
                fms=fm.splitMove();
                break;
            default:
                throw new RuntimeException(String.format(
                    "Wrong type: %d.",type));
        }

        //проверить правильность разбиения
        //С fm сравнивать нельзя, т.к. splitMove портит fm.
        checkJoinFMS(fms,copy);
        System.out.println("...............[OK]");
    }


    private static void testSaveRestore(Random rnd, long mod)
        throws IOException{
        System.out.println("===================================");
        System.out.println("====== Test save/restore ==========");
        System.out.println("===================================");
        testSaveRestore1(512,256,5000,rnd,mod,0);
        testSaveRestore1(512,256,5000,rnd,mod,1);
        testSaveRestore1(512,256,5000,rnd,mod,3);
        System.gc();

        //оставил, чтобы посмотреть
        FileMatrixL fm=new FileMatrixL(3,512,256,5000,rnd,mod);
        File dir=new File(BaseMatrixDir.getMatrixDirFile(), "save");
        fm.saveTo(dir);
    }

    private static void testSaveRestore1(int m,int n,int den,Random rnd,
                                         long mod, int depth)
        throws IOException{ Ring ring=new Ring("Zp32[]"); ring.setMOD32(mod);
        System.out.printf("testSaveRestore: depth=%d", depth);
        //fm
        FileMatrixL fm=new FileMatrixL(depth,m,n,den,rnd,mod);
        //сохранить fm ---> dir
        File dir=new File(BaseMatrixDir.getMatrixDirFile(), "save");
        fm.saveTo(dir);

        //восстановить с копированием в fm2. fm2=?=fm
        FileMatrixL fm2=FileMatrixL.restoreCopy(dir);
        //1)
        boolean eq1=fm.equals(fm2,mod,ring);

        //восстановить с перемещением в fm3. fm3=?=fm. dir не существует.
        FileMatrixL fm3=FileMatrixL.restoreMove(dir);
        //2)
        boolean eq2=fm.equals(fm3,mod,ring);
        //3)
        boolean ex=!dir.exists();

        boolean ok=eq1 && eq2 && ex;
        if (ok) {
            System.out.println("...............[OK]");
        } else {
            System.out.println("...............[Failed]");
            throw new RuntimeException("Error????!!!");
        }
    }


}
