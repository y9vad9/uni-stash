
package com.mathpar.matrix.file.sparse;

import com.mathpar.matrix.file.dense.FileMatrixL;
import java.util.Random;
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
public class TestSFileMatrixL {
    public static void main(String[] args) throws Exception{
        long mod=123456;
        Random rnd=new Random(12345);

        //очистить базовую директорию
        BaseMatrixDir.clearMatrixDir();

        //==========================
        //==========================
        //Тест: SFileMatrixL(...): m x n
        //==========================
        //==========================
        testConstr_m_x_n(rnd,mod);

        //==========================
        //==========================
        //Тест: SFileMatrixL(paths, m,n,...)
        //==========================
        //==========================
        testConstr_paths_m_x_n(rnd,mod);

        //==========================
        //==========================
        //Тест: SFileMatrixL(paths, matrs,...)
        //==========================
        //==========================
        testConstr_paths_matrs(rnd,mod);

        //==========================
        //==========================
        //Тест: equals(sfm,mod)
        //==========================
        //==========================
        testEquals(rnd,mod);

        //==========================
        //==========================
        //Тест: copy(...)
        //==========================
        //==========================
        testCopy(rnd,mod);

        //==========================
        //==========================
        //Тест: negateThis()
        //==========================
        //==========================
        testNegateThis(rnd,mod);

        //==========================
        //==========================
        //Тест: negate()
        //==========================
        //==========================
        testNegate(rnd,mod);

        //==========================
        //==========================
        //Тест: delete()
        //==========================
        //==========================
        testDelete(rnd,mod);

        //==========================
        //==========================
        //Тест: add()
        //==========================
        //==========================
        testAdd(rnd,mod);

        //==========================
        //==========================
        //Тест: subtract()
        //==========================
        //==========================
        testSubtract(rnd,mod);

        //==========================
        //==========================
        //Тест: multCU()
        //==========================
        //==========================
        testMultCU(rnd,mod);

        //==========================
        //==========================
        //Тест: Save/Restore
        //==========================
        //==========================
        testSaveRestore(rnd,mod);

        //==========================
        //==========================
        //Тест: JoinCopy
        //==========================
        //==========================
        testJoinCopyNullNotNull(rnd,mod);

        //==========================
        //==========================
        //Тест: JoinMove
        //==========================
        //==========================
        testJoinMoveNullNotNull(rnd,mod);

        //==========================
        //==========================
        //Тест: SplitCopy/Move
        //==========================
        //==========================
        testSplitCopyMove(rnd,mod);

        //==========================
        //==========================
        //Тест: ONE
        //==========================
        //==========================
        testONE();

        //==========================
        //==========================
        //Тест: SFM(FM)
        //==========================
        //==========================
        testConstrSFM_FM(rnd,mod);

        //==========================
        //==========================
        //Тест: SFM(M)
        //==========================
        //==========================
        testConstrSFM_M(rnd,mod);
    }


    private static void testConstr_m_x_n(Random rnd, long mod)
        throws IOException{
        System.out.println("===========================================");
        System.out.println("===========================================");
        System.out.println("SFileMatrixL(...): m x n");
        System.out.println("===========================================");
        System.out.println("===========================================");
        SFileMatrixL sfm;
        //ZERO
        //den=0, depth=0, ZERO
        sfm=new SFileMatrixL(
            0,
            512,512,
            0,rnd,mod);
        System.out.printf("Created file matrix: isZERO()=%s\n", sfm.isZERO());
        assertEquals(sfm.isZERO(),true,"!=0");
        //den=0, depth>0, ZERO
        sfm=new SFileMatrixL(
            4,
            512,512,
            0,rnd,mod);
        System.out.printf("Created file matrix: isZERO()=%s\n", sfm.isZERO());
        assertEquals(sfm.isZERO(),true,"!=0");
        //==========================
        //den>0
        //==========================
        //depth=0, den=10%
        testConstr_m_x_n1(
            0,
            512,512,
            10,rnd,mod);
        //==========================
        //depth=1, den=100%
        testConstr_m_x_n1(
            1,
            512, 512,
            100, rnd, mod);
        //depth=1, den=50%
        testConstr_m_x_n1(
            1,
            512,512,
            50,rnd,mod);
        //depth=1, den=1%
        testConstr_m_x_n1(
            1,
            512,512,
            1,rnd,mod);
        //==========================
        //depth>1
        //==========================
        //depth=4, den=100%
        testConstr_m_x_n1(
            4,
            512,512,
            100,rnd,mod);
        //depth=4, den=70%
        testConstr_m_x_n1(
            4,
            512,512,
            70,rnd,mod);
        //depth=4, den=50%
        testConstr_m_x_n1(
            4,
            512,512,
            50,rnd,mod);
        //depth=4, den=30%
        testConstr_m_x_n1(
            4,
            512,512,
            30,rnd,mod);
        //depth=4, den=10%
        testConstr_m_x_n1(
            4,
            512,512,
            10,rnd,mod);
        //depth=4, den=1%
        testConstr_m_x_n1(
            4,
            512,512,
            1,rnd,mod);
        //depth=4, den=0.1%
        testConstr_m_x_n1(
            4,
            512,512,
            0.1,rnd,mod);
        //depth=4, den=0.01%
        testConstr_m_x_n1(
            4,
            512,512,
            0.01,rnd,mod);
        System.gc();
    }


    private static void testConstr_m_x_n1(int depth,
        int m,int n, double dden,Random rnd, long mod)
        throws IOException{
        //создать sfm
        SFileMatrixL sfm=new SFileMatrixL(depth,m,n,dden,rnd,mod);
        //проверить:
        //depth, m x n, dden
        int depthR=SFileMatrixL.findDepth(sfm.getRoot());
        int[] sizeR=sfm.getFullSize();
        double ddenR=testConstr_m_x_n2(sfm);
        System.out.printf(
        "testConstr_m_x_n: sfm->'%s', input: size=%d x %d, depth=%d, dden=%.10f%% ||||| real: size=%d x %d, depth=%d, dden=%.10f%%\n",
            sfm.getRoot(),
            m,n,depth,dden,
            sizeR[0],sizeR[1],depthR,ddenR);
        assertEquals(depth,depthR, "Depth");
        assertEquals(m,sizeR[0], "Rows");
        assertEquals(n,sizeR[1], "Cols");
        double EPS=0.2;
        if (Math.abs(dden-ddenR)>EPS) {
            throw new RuntimeException("Dden");
        }
    }


    //вычисляет плотность sfm в %
    private static double testConstr_m_x_n2(SFileMatrixL sfm)
        throws IOException{
        //sfm (SFML) --> matr (ML)
        MatrixL matr=sfm.toMatrixL();
        long[][] M=matr.M;
        int m=M.length;
        int n=M[0].length;
        //плотность = кол-во ненулевых/общее кол-во * 100% в matr
        int notZ=0;
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (M[i][j]!=0) {
                    notZ++;
                }
            }
        }
        int all=m*n;
        double den=((double)notZ/all)*100;
        return den;
    }


    private static void testConstr_paths_m_x_n(Random rnd, long mod)
        throws IOException{
        System.out.println("===========================================");
        System.out.println("===========================================");
        System.out.println("SFileMatrixL(paths, m,n,...)");
        System.out.println("===========================================");
        System.out.println("===========================================");
        //==========================
        File base=new File(BaseMatrixDir.getMatrixDirFile(), "sfmrnd_paths_m_x_n");
        base.mkdir();
        //==========================
        SFileMatrixL sfmrnd;

        //ZERO:
        //paths=null, ZERO
        sfmrnd = new SFileMatrixL(
            null,
            10, 10, 10000, rnd, mod,
            new File(base, "paths=null, ZERO"));
        System.out.printf("Created file matrix: isZERO()=%s\n", sfmrnd.isZERO());
        assertEquals(sfmrnd.isZERO(),true,"!=0");
        //paths.len=0, ZERO
        sfmrnd=new SFileMatrixL(
            new int[][]{},
            10,10,10000,rnd,mod,
            new File(base, "paths.len=0, ZERO"));
        System.out.printf("Created file matrix: isZERO()=%s\n", sfmrnd.isZERO());
        assertEquals(sfmrnd.isZERO(),true,"!=0");

        //depth=0, 20x10, den=100%
        sfmrnd=new SFileMatrixL(
            new int[][]{{}},
            20,10,10000,rnd,mod,
            new File(base, "depth=0, 20x10, den=100"));
        System.out.printf("Created file matrix: %s\n", sfmrnd.getRoot());
        sfmrnd.keep();

        //depth=1, 40x20, den=100%
        sfmrnd=new SFileMatrixL(
            new int[][]{{0},{2},{3}},
            40,20,10000,rnd,mod,
            new File(base, "depth=1, 40x20, den=100"));
        System.out.printf("Created file matrix: %s\n", sfmrnd.getRoot());
        sfmrnd.keep();

        //depth>1
        //depth=3, 20x20, den=50%
        sfmrnd = new SFileMatrixL(
            new int[][] { {0, 2, 1}, {0, 3, 2}, {1, 2, 1}, {3, 1, 1}
        }, 20, 20, 5000, rnd, mod,
            new File(base, "depth=3, 20x20, den=50")
            );
        System.out.printf("Created file matrix: %s\n", sfmrnd.getRoot());
        sfmrnd.keep();
    }



    private static void testConstr_paths_matrs(Random rnd, long mod)
        throws IOException{
        System.out.println("===========================================");
        System.out.println("===========================================");
        System.out.println("SFileMatrixL(paths, matrs,...)");
        System.out.println("===========================================");
        System.out.println("===========================================");
        //==========================
        File base=new File(BaseMatrixDir.getMatrixDirFile(), "sfmrnd_paths_matrs");
        base.mkdir();
        //==========================
        SFileMatrixL sfmrnd;
        Ring ring=new Ring("Zp[]");ring.setMOD32(mod);

        //ZERO
        //==========
        //paths=null, ZERO
        sfmrnd=new SFileMatrixL(
            null,
            new MatrixL[0],
            new File(base, "paths=null, ZERO"),ring);
        System.out.printf("Created file matrix: isZERO()=%s\n", sfmrnd.isZERO());
        assertEquals(sfmrnd.isZERO(),true,"!=0");
        //paths.len=0, ZERO
        sfmrnd=new SFileMatrixL(
            new int[][]{},
            new MatrixL[0],
            new File(base, "paths.len=0, ZERO"), ring);
        System.out.printf("Created file matrix: isZERO()=%s\n", sfmrnd.isZERO());
        assertEquals(sfmrnd.isZERO(),true,"!=0");


        //depth=0
        //=========
        sfmrnd = new SFileMatrixL(
            new int[][] { {}
        },
            new MatrixL[] {
            new MatrixL(new long[][] { {1, 3}, {3, 2}
        }),
        },
            new File(base, "depth=0"), ring);
        System.out.printf("Created file matrix: %s\n", sfmrnd.getRoot());
        sfmrnd.keep();
        //depth=1
        sfmrnd = new SFileMatrixL(
            new int[][] { {1}, {3}
        },
            new MatrixL[] {
            new MatrixL(new long[][] { {1, 3}, {3, 2}
        }),
            new MatrixL(new long[][] { {1, 3}, {3, 2}
        }),
        },
            new File(base, "depth=1"), ring);
        System.out.printf("Created file matrix: %s\n", sfmrnd.getRoot());
        sfmrnd.keep();
        //depth=3
        sfmrnd = new SFileMatrixL(
            new int[][] { {0, 2, 1}, {0, 3, 2}, {1, 2, 1}, {3, 1, 1}
        },
            new MatrixL[] {
            new MatrixL(new long[][] { {1, 5}, {7, 2}
        }),
            new MatrixL(new long[][] { {4, 3}, {1, 2}
        }),
            new MatrixL(new long[][] { {8, 4}, {3, 5}
        }),
            new MatrixL(new long[][] { {3, 9}, {8, 1}
        }),
        },
            new File(base, "depth=3"), ring);
        System.out.printf("Created file matrix: %s\n", sfmrnd.getRoot());
        sfmrnd.keep();
    }




    private static void testEquals(Random rnd, long mod)
        throws IOException{
        SFileMatrixL sfm1,sfm2;
        System.out.println("===========================================");
        System.out.println("===========================================");
        System.out.println("equals(sfm,mod)");
        System.out.println("===========================================");
        System.out.println("===========================================");
              Ring ring=new Ring("Zp[]");ring.setMOD32(mod);
        //ZERO=ZERO
        sfm1=SFileMatrixL.ZERO;
        sfm2=new SFileMatrixL(
            null,
            new MatrixL[0], ring);
        checkEquals(sfm1,sfm2,mod,true);

        //sfm1=ZERO, sfm2!=ZERO, sfm1!=sfm2
        sfm1=SFileMatrixL.ZERO;
        sfm2 = new SFileMatrixL(
            new int[][] { {}
        },
            new MatrixL[] {
            new MatrixL(new long[][] { {1, 5}, {7, 2}
        }),
        }, ring);
        checkEquals(sfm1,sfm2,mod,false);

        //sfm1!=ZERO, sfm2=ZERO, sfm1!=sfm2
        sfm1 = new SFileMatrixL(
            new int[][] { {}
        },
            new MatrixL[] {
            new MatrixL(new long[][] { {1, 5}, {7, 2}
        }),
        }, ring);
        sfm2=SFileMatrixL.ZERO;
        checkEquals(sfm1,sfm2,mod,false);

        //sfm1!=ZERO, sfm2!=ZERO
        //==========================
        //depth1!=depth2
        sfm1 = new SFileMatrixL(
            new int[][] { {0}, {2}, {3}
        },
            new MatrixL[] {
            new MatrixL(new long[][] { {1, 5}, {7, 2}
        }),
            new MatrixL(new long[][] { {4, 3}, {1, 2}
        }),
            new MatrixL(new long[][] { {8, 4}, {3, 5}
        }),
        }, ring);
        sfm2 = new SFileMatrixL(
            new int[][] { {0, 2, 1}, {0, 3, 2}, {1, 2, 1}, {3, 1, 1}
        },
            new MatrixL[] {
            new MatrixL(new long[][] { {1, 5}, {7, 2}
        }),
            new MatrixL(new long[][] { {4, 3}, {1, 2}
        }),
            new MatrixL(new long[][] { {8, 4}, {3, 5}
        }),
            new MatrixL(new long[][] { {3, 9}, {8, 1}
        }),
        }, ring);
        checkEquals(sfm1,sfm2,mod,false);

        //depth1=depth2
        //==========================
        //depth=0, sfm1=sfm2
        sfm1 = new SFileMatrixL(
            new int[][] { {}
        },
            new MatrixL[] {
            new MatrixL(new long[][] { {1, 5}, {7, 2}
        }),
        }, ring);
        sfm2 = new SFileMatrixL(
            new int[][] { {}
        },
            new MatrixL[] {
            new MatrixL(new long[][] { {1, 5}, {7, 2}
        }),
        }, ring);
        checkEquals(sfm1,sfm2,mod,true);

        //depth=0, sfm1!=sfm2
        sfm1 = new SFileMatrixL(
            new int[][] { {}
        },
            new MatrixL[] {
            new MatrixL(new long[][] { {1, 5}, {7, 2}
        }),
        }, ring);
        sfm2 = new SFileMatrixL(
            new int[][] { {}
        },
            new MatrixL[] {
            new MatrixL(new long[][] { {1, 5}, {6, 2}
        }),
        }, ring);
        checkEquals(sfm1,sfm2,mod,false);

        //depth=1, sfm1=sfm2
        sfm1 = new SFileMatrixL(
            new int[][] { {1}, {3}
        },
            new MatrixL[] {
            new MatrixL(new long[][] { {1, 5}, {7, 2}
        }),
            new MatrixL(new long[][] { {8, 4}, {3, 5}
        }),
        }, ring);
        sfm2 = new SFileMatrixL(
            new int[][] { {1}, {3}
        },
            new MatrixL[] {
            new MatrixL(new long[][] { {1, 5}, {7, 2}
        }),
            new MatrixL(new long[][] { {8, 4}, {3, 5}
        }),
        }, ring);
        checkEquals(sfm1,sfm2,mod,true);

        //depth=1, sfm1!=sfm2, struc1!=struc2
        sfm1 = new SFileMatrixL(
            new int[][] { {1}, {2}
        },
            new MatrixL[] {
            new MatrixL(new long[][] { {1, 5}, {7, 2}
        }),
            new MatrixL(new long[][] { {8, 4}, {3, 5}
        }),
        }, ring);
        sfm2 = new SFileMatrixL(
            new int[][] { {1}, {3}
        },
            new MatrixL[] {
            new MatrixL(new long[][] { {1, 5}, {7, 2}
        }),
            new MatrixL(new long[][] { {8, 4}, {3, 5}
        }),
        }, ring);
        checkEquals(sfm1,sfm2,mod,false);

        //depth=1, sfm1!=sfm2, struc1==struc2, на depth=0 не равны
        sfm1 = new SFileMatrixL(
            new int[][] { {1}, {3}
        },
            new MatrixL[] {
            new MatrixL(new long[][] { {1, 5}, {7, 2}
        }),
            new MatrixL(new long[][] { {8, 4}, {3, 5}
        }),
        }, ring);
        sfm2 = new SFileMatrixL(
            new int[][] { {1}, {3}
        },
            new MatrixL[] {
            new MatrixL(new long[][] { {1, 5}, {7, 2}
        }),
            new MatrixL(new long[][] { {8, 4}, {5, 5}
        }),
        }, ring);
        checkEquals(sfm1,sfm2,mod,false);

        //depth=3, sfm1=sfm2
        sfm1 = new SFileMatrixL(
            new int[][] { {1, 3, 1}, {1, 3, 2}, {2, 2, 1}, {2, 3, 3}
        },
            new MatrixL[] {
            new MatrixL(new long[][] { {1, 5}, {7, 2}
        }),
            new MatrixL(new long[][] { {4, 3}, {1, 2}
        }),
            new MatrixL(new long[][] { {8, 4}, {3, 5}
        }),
            new MatrixL(new long[][] { {3, 9}, {8, 1}
        }),
        }, ring);
        sfm2 = new SFileMatrixL(
            new int[][] { {1, 3, 1}, {1, 3, 2}, {2, 2, 1}, {2, 3, 3}
        },
            new MatrixL[] {
            new MatrixL(new long[][] { {1, 5}, {7, 2}
        }),
            new MatrixL(new long[][] { {4, 3}, {1, 2}
        }),
            new MatrixL(new long[][] { {8, 4}, {3, 5}
        }),
            new MatrixL(new long[][] { {3, 9}, {8, 1}
        }),
        }, ring);
        checkEquals(sfm1,sfm2,mod,true);
        //depth=3, sfm1!=sfm2, struc1!=struc2
        sfm1 = new SFileMatrixL(
            new int[][] { {1, 3, 1}, {1, 3, 2}, {2, 2, 1}, {2, 3, 3}
        },
            new MatrixL[] {
            new MatrixL(new long[][] { {1, 5}, {7, 2}
        }),
            new MatrixL(new long[][] { {4, 3}, {1, 2}
        }),
            new MatrixL(new long[][] { {8, 4}, {3, 5}
        }),
            new MatrixL(new long[][] { {3, 9}, {8, 1}
        }),
        }, ring);
        sfm2 = new SFileMatrixL(
            new int[][] { {1, 3, 1}, {1, 3, 3}, {2, 2, 1}, {2, 3, 3}
        },
            new MatrixL[] {
            new MatrixL(new long[][] { {1, 5}, {7, 2}
        }),
            new MatrixL(new long[][] { {4, 3}, {1, 2}
        }),
            new MatrixL(new long[][] { {8, 4}, {3, 5}
        }),
            new MatrixL(new long[][] { {3, 9}, {8, 1}
        }),
        }, ring);
        checkEquals(sfm1,sfm2,mod,false);
        //depth=3, sfm1!=sfm2, struc1!=struc2
        sfm1 = new SFileMatrixL(
            new int[][] { {1, 3, 1}, {1, 3, 2}, {2, 2, 1}, {2, 3, 3}
        },
            new MatrixL[] {
            new MatrixL(new long[][] { {1, 5}, {7, 2}
        }),
            new MatrixL(new long[][] { {4, 3}, {1, 2}
        }),
            new MatrixL(new long[][] { {8, 4}, {3, 5}
        }),
            new MatrixL(new long[][] { {3, 9}, {8, 1}
        }),
        }, ring);
        sfm2 = new SFileMatrixL(
            new int[][] { {0, 3, 1}, {0, 3, 2}, {2, 2, 1}, {2, 3, 3}
        },
            new MatrixL[] {
            new MatrixL(new long[][] { {1, 5}, {7, 2}
        }),
            new MatrixL(new long[][] { {4, 3}, {1, 2}
        }),
            new MatrixL(new long[][] { {8, 4}, {3, 5}
        }),
            new MatrixL(new long[][] { {3, 9}, {8, 1}
        }),
        }, ring);
        checkEquals(sfm1,sfm2,mod,false);

        sfm1=null;
        sfm2=null;
        System.gc();
    }




    private static void checkEquals(SFileMatrixL sfm1, SFileMatrixL sfm2,
                                    long mod, boolean resRight)
        throws IOException{
              Ring ring=new Ring("Zp[]");ring.setMOD32(mod);
        boolean eq=sfm1.equals(sfm2,mod, ring);
        System.out.printf("Compare file matrix: '%s' & '%s'. equals()=%s, resRight=%s\n\n",
                          sfm1.getRoot(),sfm2.getRoot(),
                          eq, resRight);
        if (eq!=resRight) {
            throw new RuntimeException("Error????");
        }
    }




    private static void testCopy(Random rnd, long mod) throws IOException {
        System.out.println("===========================================");
        System.out.println("===========================================");
        System.out.println("copy(...)");
        System.out.println("===========================================");
        System.out.println("===========================================");
        //ZERO
        testCopy1(null, -1, -1, -1, rnd, mod);

        //depth=0
        testCopy1(new int[][] { {}
        }, 10, 10, 10000, rnd, mod);
        //depth=1
        testCopy1(new int[][] { {0}, {2}, {3}
        }, 10, 10, 10000, rnd, mod);
        //depth=3
        testCopy1(new int[][] { {0, 2, 1}, {0, 3, 2}, {1, 2, 1}, {3, 1, 1}
        }, 10, 10, 10000, rnd, mod);
        System.gc();
    }



    private static void testCopy1(int[][] paths, int m, int n, int den, Random rnd,
                                  long mod)
        throws IOException{      Ring ring=new Ring("Zp[]");ring.setMOD32(mod);
        SFileMatrixL sfm1 = new SFileMatrixL(paths,m,n,den,rnd,mod);
        //sfm1-->sfm2
        SFileMatrixL sfm2=sfm1.copy();

        //sfm1=?=sfm2
        boolean eq=sfm1.equals(sfm2,mod, ring);
        System.out.printf("testCopy: sfm1->'%s', sfm2->'%s', depth=%d, status=%s\n",
                          sfm1.getRoot(), sfm2.getRoot(), sfm1.getDepth(),
                          eq?"Ok":"Failed???!!!");
        if (!eq) {
            throw new RuntimeException("Error????!!!");
        }
    }




    private static void testNegateThis(Random rnd, long mod)
        throws IOException{
        System.out.println("===========================================");
        System.out.println("===========================================");
        System.out.println("negateThis(...)");
        System.out.println("===========================================");
        System.out.println("===========================================");
        //ZERO
        SFileMatrixL sfm1=SFileMatrixL.ZERO;
        Ring r=Ring.ringR64xyzt;
        sfm1.negateThis(mod,r);
        System.out.printf("Negated file matrix: isZERO()=%s\n", sfm1.isZERO());
        assertEquals(sfm1.isZERO(),true,"!=0");

        //depth=0
        testNegateThis1(new int[][] { {}
        }, 10, 10, 5000, rnd, mod);
        //depth=1
        testNegateThis1(new int[][] { {1}, {3}
        }, 10, 10, 5000, rnd, mod);
        //depth=3
        testNegateThis1(new int[][] { {0, 1, 1}, {0, 2, 1}, {0, 2, 3}, {1, 3, 3}
        }, 10, 10, 5000, rnd, mod);
        System.gc();
    }




    private static void testNegateThis1(int[][] paths, int m, int n, int den,
                                        Random rnd, long mod) throws
        IOException {
        //sfm1!=ZERO
        SFileMatrixL sfm1 = new SFileMatrixL(paths,m,n,den,rnd, mod);
        //m1!=null
        MatrixL m1=sfm1.toMatrixL();
        Ring r=Ring.ringR64xyzt;
        sfm1.negateThis(mod,r);
        //m2!=null
        MatrixL m2=sfm1.toMatrixL();
        boolean ok=checkDiffSign(m1,m2,mod);
        System.out.printf("testNegateThis: sfm1->'%s', depth=%d, ok=%s\n",
                          sfm1.getRoot(), sfm1.getDepth(),
                          ok?"YES!":"No-o-o-o!");
        if (!ok) {
            throw new RuntimeException("Error????!!!");
        }
    }


    //m1,m2 !=null
    //если m1=-m2, то true, иначе -- false
    private static boolean checkDiffSign(MatrixL m1, MatrixL m2, long mod){
        return m1.add(m2,mod).isZERO();
    }


    private static void testNegate(Random rnd, long mod)
        throws IOException{
        System.out.println("===========================================");
        System.out.println("===========================================");
        System.out.println("negate(...)");
        System.out.println("===========================================");
        System.out.println("===========================================");
        //ZERO
        SFileMatrixL sfm1=SFileMatrixL.ZERO;
        SFileMatrixL sfm2=sfm1.negate(mod);
        System.out.printf("Negated file matrix: isZERO()=%s\n", sfm2.isZERO());
        assertEquals(sfm2.isZERO(),true,"!=0");

        //depth=0
        testNegate1(new int[][] { {}
        }, 10, 10, 5000, rnd, mod);
        //depth=1
        testNegate1(new int[][] { {1}, {3}
        }, 10, 10, 5000, rnd, mod);
        //depth=3
        testNegate1(new int[][] { {0, 1, 1}, {0, 2, 1}, {0, 2, 3}, {1, 3, 3}
        }, 10, 10, 5000, rnd, mod);
        System.gc();
    }



    private static void testNegate1(int[][] paths, int m, int n, int den,
                                    Random rnd, long mod) throws
        IOException {
        //sfm1!=ZERO
        SFileMatrixL sfm1 = new SFileMatrixL(paths,m,n,den,rnd, mod);
        //m1!=null
        MatrixL m1=sfm1.toMatrixL();
        SFileMatrixL sfm2=sfm1.negate(mod);
        //m2!=null
        MatrixL m2=sfm2.toMatrixL();
        boolean ok=checkDiffSign(m1,m2,mod);
        System.out.printf("testNegate: sfm1->'%s', sfm2->'%s', depth=%d, ok=%s\n",
                          sfm1.getRoot(), sfm2.getRoot(), sfm1.getDepth(),
                          ok?"YES!":"No-o-o-o!");
        if (!ok) {
            throw new RuntimeException("Error????!!!");
        }
    }



    private static void testDelete(Random rnd, long mod)
        throws IOException{
        System.out.println("===========================================");
        System.out.println("===========================================");
        System.out.println("testDelete(...)");
        System.out.println("===========================================");
        System.out.println("===========================================");
        //ZERO
        SFileMatrixL sfm1=SFileMatrixL.ZERO;
        boolean del=sfm1.delete();
        System.out.printf("Deleted file matrix: del=%s\n", del);
        assertEquals(del,true,"!=0");

        testDelete1(512,512,50.0,0,rnd,mod);
        testDelete1(512,512,50.0,1,rnd,mod);
        testDelete1(512,512,50.0,3,rnd,mod);
        testDelete1(512,512,50.0,5,rnd,mod);
        System.gc();
    }



    private static void testDelete1(int m, int n, double dden,
                                    int depth, Random rnd, long mod)
        throws IOException{
        //создать sfm
        SFileMatrixL sfm=new SFileMatrixL(depth,m,n,dden,rnd,mod);
        File root=sfm.getRoot();
        //удалить sfm
        boolean delOk=sfm.delete();
        boolean factOk=!root.exists();
        System.out.printf("testDelete: root='%s', depth=%d, delOk=%s, factOk=%s",
                          root,sfm.getDepth(),
                          delOk,factOk);
        if (delOk && factOk) {
            System.out.println("..................[OK]");
        } else {
            throw new RuntimeException("Error");
        }
    }





    final static String[] opNames={"+","-","*"};
    final static int ROWS=10;
    final static int COLS=10;
    final static int DEN=10000;

    /**
     * paths1 и paths2 !=null, len!=0. Это нужно, чтобы sfm1 и sfm2 !=0 и
     * m1,m2 !=null.
     *
     * @param paths1 int[][]
     * @param matrs1 MatrixL[]
     * @param paths2 int[][]
     * @param matrs2 MatrixL[]
     * @param operator int
     * @param rnd Random
     * @param mod long
     * @throws IOException
     */
    private static void testOperations(int[][] paths1, MatrixL[] matrs1,
                                       int[][] paths2, MatrixL[] matrs2,
                                       int operator, Random rnd, long mod)
        throws IOException{      Ring ring=new Ring("Zp[]");ring.setMOD32(mod);
        //Создать sfm1, sfm2 (SFileMatrixL)
        SFileMatrixL sfm1, sfm2;
        if (matrs1!=null) {
            sfm1=new SFileMatrixL(paths1,matrs1,ring);
            sfm2=new SFileMatrixL(paths2,matrs2,ring);
        } else {
            sfm1=new SFileMatrixL(paths1,ROWS,COLS,DEN,rnd,mod);
            sfm2=new SFileMatrixL(paths2,ROWS,COLS,DEN,rnd,mod);
        }

        //sfmres= sfm1 op sfm2
        SFileMatrixL sfmres;
        switch (operator) {
            case 0: //+
                sfmres = sfm1.add(sfm2, mod);
                break;
            case 1: //-
                sfmres = sfm1.subtract(sfm2, mod);
                break;
            case 2: //*
                sfmres = sfm1.multCU(sfm2, mod);
                break;
            default:
                throw new RuntimeException(String.format(
                    "Wrong operation: %d.", operator));
        }

        //sfm1, sfm2, sfmres (SFileMatrixL) --> m1,m2,mres (MatrixL)
        //m1,m2!=null, т.к. sfm1,sfm2!=0
        //mres может быть null
        MatrixL m1=sfm1.toMatrixL();
        MatrixL m2=sfm2.toMatrixL();
        MatrixL mres=sfmres.toMatrixL();

        //проверить, что m1 op m2 == mres
        MatrixL mresRight;
        switch (operator) {
            case 0: //+
                mresRight = m1.add(m2, mod);
                break;
            case 1: //-
                mresRight = m1.subtract(m2, mod);
                break;
            case 2: //*
                mresRight = m1.multCU(m2, mod);
                break;
            default:
                throw new RuntimeException(String.format(
                    "Wrong operation: %d.", operator));
        }

        boolean eq=eqMatrs(mres, mresRight, mod);
        System.out.printf("('%s' %s '%s' = '%s') depth=%d >>>>>  EQUALS: %s\n\n",
                          sfm1.getRoot(), opNames[operator], sfm2.getRoot(),
                          sfmres.getRoot(),
                          sfm1.getDepth(),
                          (eq?"YES!":"NO!??")
            );
        if (!eq) {
            throw new RuntimeException("Not equals???? No-o-o-o!!!!");
        }
    }


    /**
     * Сравнивает матрицы.
     * fileres =null(=0) или !=null(!=0), т.к. это результат toMatrixL() из
     * файловой матрицы.
     * res всегда !=null. Но res может быть =0.
     * @param fileres MatrixL
     * @param res MatrixL
     * @param mod long
     * @return boolean
     */
    private static boolean eqMatrs(MatrixL fileres, MatrixL res, long mod){
        if (fileres==null) {
            //fileres=0
            return res.isZERO();
        }

        return fileres.equals(res,mod);
    }


    private static void testAdd(Random rnd, long mod)
        throws IOException{      Ring ring=new Ring("Zp[]");ring.setMOD32(mod);
        System.out.println("===========================================");
        System.out.println("===========================================");
        System.out.println("add(...)");
        System.out.println("===========================================");
        System.out.println("===========================================");
        //==========================
        //ZERO
        //==========================
        SFileMatrixL sfm1,sfm2,sfmres;
        //sfm1=0, sfm2!=0
        sfm1=SFileMatrixL.ZERO;
        sfm2=new SFileMatrixL(2,100,100,10,rnd,mod);
        sfmres=sfm1.add(sfm2,mod);
        boolean eq=sfm2.equals(sfmres,mod, ring);
        System.out.printf("('%s' %s '%s' = '%s') depth=%d >>>>>  EQUALS: %s\n\n",
                          sfm1.getRoot(), opNames[0], sfm2.getRoot(),
                          sfmres.getRoot(),
                          sfmres.getDepth(),
                          eq?"YES!":"No-o-o-o!!!"
            );
        assertEquals(eq,true,"!=");
        //sfm1!=0, sfm2=0
        sfm1=new SFileMatrixL(2,100,100,10,rnd,mod);
        sfm2=SFileMatrixL.ZERO;
        sfmres=sfm1.add(sfm2,mod);
        eq=sfm1.equals(sfmres,mod, ring);
        System.out.printf("('%s' %s '%s' = '%s') depth=%d >>>>>  EQUALS: %s\n\n",
                          sfm1.getRoot(), opNames[0], sfm2.getRoot(),
                          sfmres.getRoot(),
                          sfmres.getDepth(),
                          eq?"YES!":"No-o-o-o!!!"
            );
        assertEquals(eq,true,"!=");
        //sfm1=0, sfm2=0
        sfm1=SFileMatrixL.ZERO;
        sfm2=SFileMatrixL.ZERO;
        sfmres=sfm1.add(sfm2,mod);
        boolean isZero=sfmres.isZERO();
        System.out.printf("('%s' %s '%s' = '%s') depth=%d >>>>>  ISZERO: %s\n\n",
                          sfm1.getRoot(), opNames[0], sfm2.getRoot(),
                          sfmres.getRoot(),
                          sfmres.getDepth(),
                          isZero?"YES!":"No-o-o-o!!!"
            );
        assertEquals(isZero,true,"!=0");
        //==========================
        //depth=0
        //==========================
        //m1+m2!=0
        testOperations(new int[][] { {}
        },
            new MatrixL[] {
            new MatrixL(new long[][] { {2, 3}, {1, 5}
        })
        },
            new int[][] { {}
        },
            new MatrixL[] {
            new MatrixL(new long[][] { {1, 7}, {3, 5}
        })
        },
            0, rnd, mod);
        //m1+m2=0
        testOperations(new int[][] { {}
        },
            new MatrixL[] {
            new MatrixL(new long[][] { {2, 3}, {1, 5}
        })
        },
            new int[][] { {}
        },
            new MatrixL[] {
            new MatrixL(new long[][] { {-2, -3}, {-1, -5}
        })
        },
            0, rnd, mod);
        //==========================
        //depth=1
        //==========================
        //m1+m2!=0
        //(r1,0)=0,(r2,0)=0
        //(r1,1)!=0,(r2,1)=0
        //(r1,2)=0,(r2,2)!=0
        //(r1,3)+(r2,3)!=0
        //    x        x
        //  1  3      2 3
        testOperations(new int[][] { {1},{3}
        },
            new MatrixL[] {
            new MatrixL(new long[][] { {2, 3}, {1, 5}
        }),
            new MatrixL(new long[][] { {1, -5}, {4, 3}
        }),
        },
            new int[][] { {2},{3}
        },
            new MatrixL[] {
            new MatrixL(new long[][] { {1, 7}, {3, 5}
        }),
            new MatrixL(new long[][] { {4, 3}, {2, -3}
        }),
        },
            0, rnd, mod);
        //m1+m2!=0
        //(r1,0)!=0,(r2,0)=0
        //(r1,1)=0,(r2,1)=0
        //(r1,2)+(r2,2)=0
        //(r1,3)=0,(r2,3)!=0
        //    x        x
        //  0 2       2 3
        testOperations(new int[][] { {0},{2}
        },
            new MatrixL[] {
            new MatrixL(new long[][] { {2, 3}, {1, 5}
        }),
            new MatrixL(new long[][] { {1, -5}, {4, 3}
        }),
        },
            new int[][] { {2},{3}
        },
            new MatrixL[] {
            new MatrixL(new long[][] { {-1, 5}, {-4, -3}
        }),
            new MatrixL(new long[][] { {4, 3}, {2, -3}
        }),
        },
            0, rnd, mod);
        //m1+m2=0
        //    x        x
        //  0 2       0 2
        testOperations(new int[][] { {0},{2}
        },
            new MatrixL[] {
            new MatrixL(new long[][] { {2, 3}, {1, 5}
        }),
            new MatrixL(new long[][] { {1, -5}, {4, 3}
        }),
        },
            new int[][] { {0},{2}
        },
            new MatrixL[] {
            new MatrixL(new long[][] { {-2, -3}, {-1, -5}
        }),
            new MatrixL(new long[][] { {-1, 5}, {-4, -3}
        }),
        },
            0, rnd, mod);



        //==========================
        //depth=3
        //==========================
        //m1+m2!=0
        //(r1,0)=0,(r2,0)=0
        //(r1,1)!=0,(r2,1)=0
        //(r1,2)=0,(r2,2)!=0
        //(r1,3)+(r2,3)!=0
        //        x                     x
        //   1         3          2           3
        // 0   2     1   2      1   2       1  2  3
        //12   01   02   3     0    13     01  1  12
        testOperations(new int[][] { {1,0,1},{1,0,2},{1,2,0},{1,2,1},
                       {3,1,0},{3,1,2},{3,2,3}
        },
            new MatrixL[] {
            new MatrixL(new long[][] { {2, 3}, {1, 5}
        }),
            new MatrixL(new long[][] { {2, 3}, {1, 5}
        }),
            new MatrixL(new long[][] { {2, 3}, {1, 5}
        }),
            new MatrixL(new long[][] { {2, 3}, {1, 5}
        }),
            new MatrixL(new long[][] { {2, 3}, {1, 5}
        }),
            new MatrixL(new long[][] { {2, 3}, {1, 5}
        }),
            new MatrixL(new long[][] { {2, 3}, {1, 5}
        }),
        },
            new int[][] { {2,1,0},{2,2,1},{2,2,3},{3,1,0},{3,1,1}, {3,2,1},
            {3,3,1},{3,3,2}
        },
            new MatrixL[] {
            new MatrixL(new long[][] { {2, 4}, {5, 6}
        }),
            new MatrixL(new long[][] { {2, 4}, {5, 6}
        }),
            new MatrixL(new long[][] { {2, 4}, {5, 6}
        }),
            new MatrixL(new long[][] { {2, 4}, {5, 6}
        }),
            new MatrixL(new long[][] { {2, 4}, {5, 6}
        }),
            new MatrixL(new long[][] { {2, 4}, {5, 6}
        }),
            new MatrixL(new long[][] { {2, 4}, {5, 6}
        }),
            new MatrixL(new long[][] { {2, 4}, {5, 6}
        }),
        },
            0, rnd, mod);



        //m1+m2!=0
        //(r1,0)!=0,(r2,0)=0
        //(r1,1)=0,(r2,1)=0
        //(r1,2)+(r2,2)=0
        //(r1,3)=0,(r2,3)!=0
        //             x                              x
        //    0                 2          2                     3
        //  1   3             1   2      1   2                 1   3
        // 23    1           01   13    01   13                0   1
        testOperations(new int[][] { {0,1,2},{0,1,3},{0,3,1},
                       {2,1,0},{2,1,1},{2,2,1},{2,2,3}
        },
            new MatrixL[] {
            new MatrixL(new long[][] { {2, 3}, {1, 5}
        }),
            new MatrixL(new long[][] { {2, 3}, {1, 5}
        }),
            new MatrixL(new long[][] { {2, 3}, {1, 5}
        }),
            new MatrixL(new long[][] { {1, 2}, {3, 4}
        }),
            new MatrixL(new long[][] { {1, 2}, {3, 4}
        }),
            new MatrixL(new long[][] { {1, 2}, {3, 4}
        }),
            new MatrixL(new long[][] { {1, 2}, {3, 4}
        }),
        },
            new int[][] { {2,1,0}, {2,1,1},{2,2,1},{2,2,3},
            {3,1,0},{3,3,1}
        },
            new MatrixL[] {
            new MatrixL(new long[][] { {-1, -2}, {-3, -4}
        }),
            new MatrixL(new long[][] { {-1, -2}, {-3, -4}
        }),
            new MatrixL(new long[][] { {-1, -2}, {-3, -4}
        }),
            new MatrixL(new long[][] { {-1, -2}, {-3, -4}
        }),
            new MatrixL(new long[][] { {2, 4}, {5, 6}
        }),
            new MatrixL(new long[][] { {2, 4}, {5, 6}
        }),
        },
            0, rnd, mod);

        //m1+m2=0
        testOperations(new int[][] { {0,1,2},{0,1,3},{0,3,1},{2,1,0},
                       {2,1,1},{2,2,1},{2,2,3}
        },
            new MatrixL[] {
            new MatrixL(new long[][] { {2, 3}, {1, 5}
        }),
            new MatrixL(new long[][] { {2, 3}, {1, 5}
        }),
            new MatrixL(new long[][] { {2, 3}, {1, 5}
        }),
            new MatrixL(new long[][] { {1, 2}, {3, 4}
        }),
            new MatrixL(new long[][] { {1, 2}, {3, 4}
        }),
            new MatrixL(new long[][] { {1, 2}, {3, 4}
        }),
            new MatrixL(new long[][] { {1, 2}, {3, 4}
        }),
        },
            new int[][] { {0,1,2},{0,1,3},{0,3,1},{2,1,0},
            {2,1,1},{2,2,1},{2,2,3}
        },
            new MatrixL[] {
            new MatrixL(new long[][] { {-2, -3}, {-1, -5}
        }),
            new MatrixL(new long[][] { {-2, -3}, {-1, -5}
        }),
            new MatrixL(new long[][] { {-2, -3}, {-1, -5}
        }),
            new MatrixL(new long[][] { {-1, -2}, {-3, -4}
        }),
            new MatrixL(new long[][] { {-1, -2}, {-3, -4}
        }),
            new MatrixL(new long[][] { {-1, -2}, {-3, -4}
        }),
            new MatrixL(new long[][] { {-1, -2}, {-3, -4}
        }),
        },
            0, rnd, mod);
        System.gc();
    }





    private static void testSubtract(Random rnd, long mod)
        throws IOException{      Ring ring=new Ring("Zp[]");ring.setMOD32(mod);
        System.out.println("===========================================");
        System.out.println("===========================================");
        System.out.println("subtract(...)");
        System.out.println("===========================================");
        System.out.println("===========================================");
        //==========================
        //ZERO
        //==========================
        SFileMatrixL sfm1,sfm2,sfmres;
        //sfm1=0, sfm2!=0
        sfm1=SFileMatrixL.ZERO;
        sfm2=new SFileMatrixL(2,100,100,10,rnd,mod);
        sfmres=sfm1.subtract(sfm2,mod);
        boolean eq=sfmres.add(sfm2,mod).isZERO();
        System.out.printf("('%s' %s '%s' = '%s') depth=%d >>>>>  EQUALS: %s\n\n",
                          sfm1.getRoot(), opNames[1], sfm2.getRoot(),
                          sfmres.getRoot(),
                          sfmres.getDepth(),
                          eq?"YES!":"No-o-o-o!!!"
            );
        assertEquals(eq,true,"!=");
        //sfm1!=0, sfm2=0
        sfm1=new SFileMatrixL(2,100,100,10,rnd,mod);
        sfm2=SFileMatrixL.ZERO;
        sfmres=sfm1.subtract(sfm2,mod);
        eq=sfm1.equals(sfmres,mod,ring);
        System.out.printf("('%s' %s '%s' = '%s') depth=%d >>>>>  EQUALS: %s\n\n",
                          sfm1.getRoot(), opNames[1], sfm2.getRoot(),
                          sfmres.getRoot(),
                          sfmres.getDepth(),
                          eq?"YES!":"No-o-o-o!!!"
            );
        assertEquals(eq,true,"!=");
        //sfm1=0, sfm2=0
        sfm1=SFileMatrixL.ZERO;
        sfm2=SFileMatrixL.ZERO;
        sfmres=sfm1.subtract(sfm2,mod);
        boolean isZero=sfmres.isZERO();
        System.out.printf("('%s' %s '%s' = '%s') depth=%d >>>>>  ISZERO: %s\n\n",
                          sfm1.getRoot(), opNames[1], sfm2.getRoot(),
                          sfmres.getRoot(),
                          sfmres.getDepth(),
                          isZero?"YES!":"No-o-o-o!!!"
            );
        assertEquals(isZero,true,"!=0");

        //==========================
        //depth=0
        //==========================
        //m1-m2!=0
        testOperations(new int[][] { {}
        },
            new MatrixL[] {
            new MatrixL(new long[][] { {2, 3}, {1, 5}
        })
        },
            new int[][] { {}
        },
            new MatrixL[] {
            new MatrixL(new long[][] { {1, 7}, {3, 5}
        })
        },
            1, rnd, mod);
        //m1-m2=0
        testOperations(new int[][] { {}
        },
            new MatrixL[] {
            new MatrixL(new long[][] { {2, 3}, {1, 5}
        })
        },
            new int[][] { {}
        },
            new MatrixL[] {
            new MatrixL(new long[][] { {2, 3}, {1, 5}
        })
        },
            1, rnd, mod);
        //==========================
        //depth=1
        //==========================
        //m1-m2!=0
        //(r1,0)=0,(r2,0)=0
        //(r1,1)!=0,(r2,1)=0
        //(r1,2)=0,(r2,2)!=0
        //(r1,3)-(r2,3)!=0
        //    x        x
        //  1  3      2 3
        testOperations(new int[][] { {1},{3}
        },
            new MatrixL[] {
            new MatrixL(new long[][] { {2, 3}, {1, 5}
        }),
            new MatrixL(new long[][] { {1, -5}, {4, 3}
        }),
        },
            new int[][] { {2},{3}
        },
            new MatrixL[] {
            new MatrixL(new long[][] { {1, 7}, {3, 5}
        }),
            new MatrixL(new long[][] { {4, 3}, {2, -3}
        }),
        },
            1, rnd, mod);
        //m1-m2!=0
        //(r1,0)!=0,(r2,0)=0
        //(r1,1)=0,(r2,1)=0
        //(r1,2)-(r2,2)=0
        //(r1,3)=0,(r2,3)!=0
        //    x        x
        //  0 2       2 3
        testOperations(new int[][] { {0},{2}
        },
            new MatrixL[] {
            new MatrixL(new long[][] { {2, 3}, {1, 5}
        }),
            new MatrixL(new long[][] { {1, -5}, {4, 3}
        }),
        },
            new int[][] { {2},{3}
        },
            new MatrixL[] {
            new MatrixL(new long[][] { {1, -5}, {4, 3}
        }),
            new MatrixL(new long[][] { {4, 3}, {2, -3}
        }),
        },
            1, rnd, mod);
        //m1-m2=0
        //    x        x
        //  0 2       0 2
        testOperations(new int[][] { {0},{2}
        },
            new MatrixL[] {
            new MatrixL(new long[][] { {2, 3}, {1, 5}
        }),
            new MatrixL(new long[][] { {1, -5}, {4, 3}
        }),
        },
            new int[][] { {0},{2}
        },
            new MatrixL[] {
            new MatrixL(new long[][] { {2, 3}, {1, 5}
        }),
            new MatrixL(new long[][] { {1, -5}, {4, 3}
        }),
        },
            1, rnd, mod);



        //==========================
        //depth=3
        //==========================
        //m1-m2!=0
        //(r1,0)=0,(r2,0)=0
        //(r1,1)!=0,(r2,1)=0
        //(r1,2)=0,(r2,2)!=0
        //(r1,3)-(r2,3)!=0
        //        x                     x
        //   1         3          2           3
        // 0   2     1   2      1   2       1  2  3
        //12   01   02   3     0    13     01  1  12
        testOperations(new int[][] { {1,0,1},{1,0,2},{1,2,0},{1,2,1},
                       {3,1,0},{3,1,2},{3,2,3}
        },
            new MatrixL[] {
            new MatrixL(new long[][] { {2, 3}, {1, 5}
        }),
            new MatrixL(new long[][] { {2, 3}, {1, 5}
        }),
            new MatrixL(new long[][] { {2, 3}, {1, 5}
        }),
            new MatrixL(new long[][] { {2, 3}, {1, 5}
        }),
            new MatrixL(new long[][] { {2, 3}, {1, 5}
        }),
            new MatrixL(new long[][] { {2, 3}, {1, 5}
        }),
            new MatrixL(new long[][] { {2, 3}, {1, 5}
        }),
        },
            new int[][] { {2,1,0},{2,2,1},{2,2,3},{3,1,0},{3,1,1}, {3,2,1},
            {3,3,1},{3,3,2}
        },
            new MatrixL[] {
            new MatrixL(new long[][] { {2, 4}, {5, 6}
        }),
            new MatrixL(new long[][] { {2, 4}, {5, 6}
        }),
            new MatrixL(new long[][] { {2, 4}, {5, 6}
        }),
            new MatrixL(new long[][] { {2, 4}, {5, 6}
        }),
            new MatrixL(new long[][] { {2, 4}, {5, 6}
        }),
            new MatrixL(new long[][] { {2, 4}, {5, 6}
        }),
            new MatrixL(new long[][] { {2, 4}, {5, 6}
        }),
            new MatrixL(new long[][] { {2, 4}, {5, 6}
        }),
        },
            1, rnd, mod);



        //m1-m2!=0
        //(r1,0)!=0,(r2,0)=0
        //(r1,1)=0,(r2,1)=0
        //(r1,2)-(r2,2)=0
        //(r1,3)=0,(r2,3)!=0
        //             x                              x
        //    0                 2          2                     3
        //  1   3             1   2      1   2                 1   3
        // 23    1           01   13    01   13                0   1
        testOperations(new int[][] { {0,1,2},{0,1,3},{0,3,1},
                       {2,1,0},{2,1,1},{2,2,1},{2,2,3}
        },
            new MatrixL[] {
            new MatrixL(new long[][] { {2, 3}, {1, 5}
        }),
            new MatrixL(new long[][] { {2, 3}, {1, 5}
        }),
            new MatrixL(new long[][] { {2, 3}, {1, 5}
        }),
            new MatrixL(new long[][] { {1, 2}, {3, 4}
        }),
            new MatrixL(new long[][] { {1, 2}, {3, 4}
        }),
            new MatrixL(new long[][] { {1, 2}, {3, 4}
        }),
            new MatrixL(new long[][] { {1, 2}, {3, 4}
        }),
        },
            new int[][] { {2,1,0}, {2,1,1},{2,2,1},{2,2,3},
            {3,1,0},{3,3,1}
        },
            new MatrixL[] {
            new MatrixL(new long[][] { {1, 2}, {3, 4}
        }),
            new MatrixL(new long[][] { {1, 2}, {3, 4}
        }),
            new MatrixL(new long[][] { {1, 2}, {3, 4}
        }),
            new MatrixL(new long[][] { {1, 2}, {3, 4}
        }),
            new MatrixL(new long[][] { {2, 4}, {5, 6}
        }),
            new MatrixL(new long[][] { {2, 4}, {5, 6}
        }),
        },
            1, rnd, mod);

        //m1-m2=0
        testOperations(new int[][] { {0,1,2},{0,1,3},{0,3,1},{2,1,0},
                       {2,1,1},{2,2,1},{2,2,3}
        },
            new MatrixL[] {
            new MatrixL(new long[][] { {2, 3}, {1, 5}
        }),
            new MatrixL(new long[][] { {2, 3}, {1, 5}
        }),
            new MatrixL(new long[][] { {2, 3}, {1, 5}
        }),
            new MatrixL(new long[][] { {1, 2}, {3, 4}
        }),
            new MatrixL(new long[][] { {1, 2}, {3, 4}
        }),
            new MatrixL(new long[][] { {1, 2}, {3, 4}
        }),
            new MatrixL(new long[][] { {1, 2}, {3, 4}
        }),
        },
            new int[][] { {0,1,2},{0,1,3},{0,3,1},{2,1,0},
            {2,1,1},{2,2,1},{2,2,3}
        },
            new MatrixL[] {
            new MatrixL(new long[][] { {2, 3}, {1, 5}
        }),
            new MatrixL(new long[][] { {2, 3}, {1, 5}
        }),
            new MatrixL(new long[][] { {2, 3}, {1, 5}
        }),
            new MatrixL(new long[][] { {1, 2}, {3, 4}
        }),
            new MatrixL(new long[][] { {1, 2}, {3, 4}
        }),
            new MatrixL(new long[][] { {1, 2}, {3, 4}
        }),
            new MatrixL(new long[][] { {1, 2}, {3, 4}
        }),
        },
            1, rnd, mod);
    }




    private static void testMultCU(Random rnd, long mod)
        throws IOException{
        System.out.println("===========================================");
        System.out.println("===========================================");
        System.out.println("multCU(...)");
        System.out.println("===========================================");
        System.out.println("===========================================");
        //==========================
        //ZERO
        //==========================
        SFileMatrixL sfm1, sfm2, sfmres;
        //sfm1=0, sfm2=0, sfmres=0
        sfm1=SFileMatrixL.ZERO;
        sfm2=SFileMatrixL.ZERO;
        sfmres=sfm1.multCU(sfm2,mod);
        boolean eq=sfmres.isZERO();
        System.out.printf("('%s' %s '%s' = '%s') depth=%d >>>>>  EQUALS: %s\n\n",
                          sfm1.getRoot(), opNames[2], sfm2.getRoot(),
                          sfmres.getRoot(),
                          sfmres.getDepth(),
                          eq?"YES!":"No-o-o-o!!!"
            );
        assertEquals(eq,true,"!=");

        //sfm1=0, sfm2!=0, sfmres=0
        sfm1=SFileMatrixL.ZERO;
        sfm2=new SFileMatrixL(2,100,100,10,rnd,mod);
        sfmres=sfm1.multCU(sfm2,mod);
        eq=sfmres.isZERO();
        System.out.printf("('%s' %s '%s' = '%s') depth=%d >>>>>  EQUALS: %s\n\n",
                          sfm1.getRoot(), opNames[2], sfm2.getRoot(),
                          sfmres.getRoot(),
                          sfmres.getDepth(),
                          eq?"YES!":"No-o-o-o!!!"
            );
        assertEquals(eq,true,"!=");

        //sfm1!=0, sfm2=0, sfmres=0
        sfm1=new SFileMatrixL(2,100,100,10,rnd,mod);
        sfm2=SFileMatrixL.ZERO;
        sfmres=sfm1.multCU(sfm2,mod);
        eq=sfmres.isZERO();
        System.out.printf("('%s' %s '%s' = '%s') depth=%d >>>>>  EQUALS: %s\n\n",
                          sfm1.getRoot(), opNames[2], sfm2.getRoot(),
                          sfmres.getRoot(),
                          sfmres.getDepth(),
                          eq?"YES!":"No-o-o-o!!!"
            );
        assertEquals(eq,true,"!=");


        //sfm1!=0, sfm2!=0
        //==========================
        //depth=0
        //==========================
        //m1*m2!=0
        testOperations(new int[][] { {}
        },
            new MatrixL[] {
            new MatrixL(new long[][] { {2, 3}, {1, 5}
        })
        },
            new int[][] { {}
        },
            new MatrixL[] {
            new MatrixL(new long[][] { {1, 7}, {3, 5}
        })
        },
            2, rnd, mod);

        //m1*m2=0
        testOperations(new int[][] { {}
        },
            new MatrixL[] {
            new MatrixL(new long[][] { {1, 2}, {2, 4}
        })
        },
            new int[][] { {}
        },
            new MatrixL[] {
            new MatrixL(new long[][] { {2, 2}, {-1, -1}
        })
        },
            2, rnd, mod);

        //==========================
        //depth=1
        //==========================
        //m1*m2!=0
        // 5 0      0 3        0 15
        // 0 A   *  B 0     =  0  0
        //
        //0 | 0x0 | x*0,    0*x       | 0+0=0
        //1 | 0x1 | x*x!=0, 0*0       | x+0=x
        //2 | 1x0 | 0*0,    x*x=A*B=0 | 0+0=0
        //3 | 1x1 | 0*x,    x*0       | 0+0=0
        testOperations(new int[][] { {0},{3}
        },
            new MatrixL[] {
            new MatrixL(new long[][] { {5, 0}, {0, 5}
        }),
            new MatrixL(new long[][] { {1, 2}, {2, 4}
        }),
        },
            new int[][] { {1},{2}
        },
            new MatrixL[] {
            new MatrixL(new long[][] { {3, 0}, {0, 3}
        }),
            new MatrixL(new long[][] { {0, 2}, {0, -1}
        }),
        },
            2, rnd, mod);
        //m1*m2!=0
        // 5 2      1 -2       11   0
        // 0 3   *  3  5     =  9  15
        //0  | 0x0      | x*x!=0,  x*x!=0 | x+x=x!=0
        //1  | 0x1      | x*x!=0,  x*x!=0 | x+x=0
        //2,3| 1x0,1x1  | 0*x,     x*x!=0 | 0+x=x
        testOperations(new int[][] { {0},{1},{3}
        },
            new MatrixL[] {
            new MatrixL(new long[][] { {5, 0}, {0, 5}
        }),
            new MatrixL(new long[][] { {2, 0}, {0, 2}
        }),
            new MatrixL(new long[][] { {3, 0}, {0, 3}
        }),
        },
            new int[][] { {0},{1},{2},{3}
        },
            new MatrixL[] {
            new MatrixL(new long[][] { {1, 0}, {0, 1}
        }),
            new MatrixL(new long[][] { {-2, 0}, {0, -2}
        }),
            new MatrixL(new long[][] { {3, 0}, {0, 3}
        }),
            new MatrixL(new long[][] { {5, 0}, {0, 5}
        }),
        },
            2, rnd, mod);
        //m1*m2==0
        // 1 2      0  2         0  0
        // 2 4   *  0  -1     =  0  0
        testOperations(new int[][] { {0},{1},{2},{3}
        },
            new MatrixL[] {
            new MatrixL(new long[][] { {1, 0}, {0, 1}
        }),
            new MatrixL(new long[][] { {2, 0}, {0, 2}
        }),
            new MatrixL(new long[][] { {2, 0}, {0, 2}
        }),
            new MatrixL(new long[][] { {4, 0}, {0, 4}
        }),
        },
            new int[][] { {1},{3}
        },
            new MatrixL[] {
            new MatrixL(new long[][] { {2, 0}, {0, 2}
        }),
            new MatrixL(new long[][] { {-1, 0}, {0, -1}
        }),
        },
            2, rnd, mod);

        //==========================
        //depth=2
        //==========================
        // {i}
        //a
        //  a
        //    a
        //      a
        //равно
        //{i,0},{i,3}
        // a      a
        //   a      a
        //m1*m2!=0
        // 5 0      0 3        0 15
        // 0 A   *  B 0     =  0  0
        //
        //0 | 0x0 | x*0,    0*x       | 0+0=0
        //1 | 0x1 | x*x!=0, 0*0       | x+0=x
        //2 | 1x0 | 0*0,    x*x=A*B=0 | 0+0=0
        //3 | 1x1 | 0*x,    x*0       | 0+0=0
        testOperations(new int[][] { {0,0},{0,3},{3,0},{3,3}
        },
            new MatrixL[] {
            new MatrixL(new long[][] { {5, 0}, {0, 5}
        }),
            new MatrixL(new long[][] { {5, 0}, {0, 5}
        }),
            new MatrixL(new long[][] { {1, 2}, {2, 4}
        }),
            new MatrixL(new long[][] { {1, 2}, {2, 4}
        }),
        },
            new int[][] { {1,0},{1,3},{2,0},{2,3}
        },
            new MatrixL[] {
            new MatrixL(new long[][] { {3, 0}, {0, 3}
        }),
            new MatrixL(new long[][] { {3, 0}, {0, 3}
        }),
            new MatrixL(new long[][] { {0, 2}, {0, -1}
        }),
            new MatrixL(new long[][] { {0, 2}, {0, -1}
        }),
        },
            2, rnd, mod);
        //m1*m2!=0
        // 5 2      1 -2       11   0
        // 0 3   *  3  5     =  9  15
        //0x0      | x*x,x*x | x+x=x!=0
        //0x1      | x*x,x*x | x+x=0
        //1x0,1x1  | 0*x,x*x | 0+x=x
        testOperations(new int[][] { {0,0},{0,3},{1,0},{1,3},{3,0},{3,3}
        },
            new MatrixL[] {
            new MatrixL(new long[][] { {5, 0}, {0, 5}
        }),
            new MatrixL(new long[][] { {5, 0}, {0, 5}
        }),
            new MatrixL(new long[][] { {2, 0}, {0, 2}
        }),
            new MatrixL(new long[][] { {2, 0}, {0, 2}
        }),
            new MatrixL(new long[][] { {3, 0}, {0, 3}
        }),
            new MatrixL(new long[][] { {3, 0}, {0, 3}
        }),
        },
            new int[][] { {0,0},{0,3},{1,0},{1,3},{2,0},{2,3},{3,0},{3,3}
        },
            new MatrixL[] {
            new MatrixL(new long[][] { {1, 0}, {0, 1}
        }),
            new MatrixL(new long[][] { {1, 0}, {0, 1}
        }),
            new MatrixL(new long[][] { {-2, 0}, {0, -2}
        }),
            new MatrixL(new long[][] { {-2, 0}, {0, -2}
        }),
            new MatrixL(new long[][] { {3, 0}, {0, 3}
        }),
            new MatrixL(new long[][] { {3, 0}, {0, 3}
        }),
            new MatrixL(new long[][] { {5, 0}, {0, 5}
        }),
            new MatrixL(new long[][] { {5, 0}, {0, 5}
        }),
        },
            2, rnd, mod);
        //m1*m2==0
        // 1 2      0  2         0  0
        // 2 4   *  0  -1     =  0  0
        testOperations(new int[][] { {0,0},{0,3},{1,0},{1,3},{2,0},{2,3},{3,0},{3,3}
        },
            new MatrixL[] {
            new MatrixL(new long[][] { {1, 0}, {0, 1}
        }),
            new MatrixL(new long[][] { {1, 0}, {0, 1}
        }),
            new MatrixL(new long[][] { {2, 0}, {0, 2}
        }),
            new MatrixL(new long[][] { {2, 0}, {0, 2}
        }),
            new MatrixL(new long[][] { {2, 0}, {0, 2}
        }),
            new MatrixL(new long[][] { {2, 0}, {0, 2}
        }),
            new MatrixL(new long[][] { {4, 0}, {0, 4}
        }),
            new MatrixL(new long[][] { {4, 0}, {0, 4}
        }),
        },
            new int[][] { {1,0},{1,3},{3,0},{3,3}
        },
            new MatrixL[] {
            new MatrixL(new long[][] { {2, 0}, {0, 2}
        }),
            new MatrixL(new long[][] { {2, 0}, {0, 2}
        }),
            new MatrixL(new long[][] { {-1, 0}, {0, -1}
        }),
            new MatrixL(new long[][] { {-1, 0}, {0, -1}
        }),
        },
            2, rnd, mod);
    }

    private static void testSaveRestore(Random rnd, long mod)
        throws IOException{
        System.out.println("===================================");
        System.out.println("====== Test save/restore ==========");
        System.out.println("===================================");
        testSaveRestore1(512,256,50.00,rnd,mod,0);
        testSaveRestore1(512,256,50.00,rnd,mod,1);
        testSaveRestore1(512,256,50.00,rnd,mod,3);
        System.gc();

        //оставил, чтобы посмотреть
        SFileMatrixL fm=new SFileMatrixL(3,512,256,50.0,rnd,mod);
        File dir=new File(BaseMatrixDir.getMatrixDirFile(), "save");
        fm.saveTo(dir);
    }

    private static void testSaveRestore1(int m,int n,double dden,Random rnd,
                                         long mod, int depth)
        throws IOException{      Ring ring=new Ring("Zp[]");ring.setMOD32(mod);
        System.out.printf("testSaveRestore: depth=%d", depth);
        //fm
        SFileMatrixL fm=new SFileMatrixL(depth,m,n,dden,rnd,mod);
        //сохранить fm ---> dir
        File dir=new File(BaseMatrixDir.getMatrixDirFile(), "save");
        fm.saveTo(dir);

        //восстановить с копированием в fm2. fm2=?=fm
        SFileMatrixL fm2=SFileMatrixL.restoreCopy(dir);
        //1)
        boolean eq1=fm.equals(fm2,mod,ring);

        //восстановить с перемещением в fm3. fm3=?=fm. dir не существует.
        SFileMatrixL fm3=SFileMatrixL.restoreMove(dir);
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



    private static void testJoinCopyNullNotNull(Random rnd, long mod)
        throws IOException{
        System.out.println("=================================================");
        System.out.println("============= Test JoinCopyNullNotNull ==========");
        System.out.println("=================================================");
        //нет null
        testJoinCopyNullNotNull1(512,512,50.0,rnd,mod, 0,
                                 new boolean[]{true,true,true,true});
        testJoinCopyNullNotNull1(512,512,50.0,rnd,mod, 1,
                                 new boolean[]{true,true,true,true});
        testJoinCopyNullNotNull1(512,512,50.0,rnd,mod, 3,
                                 new boolean[]{true,true,true,true});
        //есть null
        testJoinCopyNullNotNull1(512,512,50.0,rnd,mod, 0,
                                 new boolean[]{false,true,false,true});
        testJoinCopyNullNotNull1(512,512,50.0,rnd,mod, 1,
                                 new boolean[]{false,false,true,true});
        testJoinCopyNullNotNull1(512,512,50.0,rnd,mod, 3,
                                 new boolean[]{true,false,false,true});

        System.gc();
    }


    private static void testJoinCopyNullNotNull1(int m,int n,double dden,Random rnd, long mod,
                                                 int depth, boolean[] where)
        throws IOException{
        SFileMatrixL[] matrs=genSFMS(m,n,dden,rnd,mod,depth,where);
        SFileMatrixL res=SFileMatrixL.joinCopy(matrs);
        System.out.printf("testJoinCopyNullNotNull: depth=%d", depth);
        checkJoinSFMS(matrs,res);
        System.out.println("...............[OK]");
    }




    private static void testJoinMoveNullNotNull(Random rnd, long mod)
        throws IOException{
        System.out.println("=================================================");
        System.out.println("============= Test JoinMoveNullNotNull ==========");
        System.out.println("=================================================");
        //нет null
        testJoinMoveNullNotNull1(512,512,50.0,rnd,mod, 0,
                                 new boolean[]{true,true,true,true});
        testJoinMoveNullNotNull1(512,512,50.0,rnd,mod, 1,
                                 new boolean[]{true,true,true,true});
        testJoinMoveNullNotNull1(512,512,50.0,rnd,mod, 3,
                                 new boolean[]{true,true,true,true});
        //есть null
        testJoinMoveNullNotNull1(512,512,50.0,rnd,mod, 0,
                                 new boolean[]{false,true,false,true});
        testJoinMoveNullNotNull1(512,512,50.0,rnd,mod, 1,
                                 new boolean[]{false,false,true,true});
        testJoinMoveNullNotNull1(512,512,50.0,rnd,mod, 3,
                                 new boolean[]{true,false,false,true});
        System.gc();
    }


    private static void testJoinMoveNullNotNull1(int m,int n,double dden,Random rnd, long mod,
                                                 int depth, boolean[] where)
        throws IOException{
        SFileMatrixL[] matrs=genSFMS(m,n,dden,rnd,mod,depth,where);
        SFileMatrixL res=SFileMatrixL.joinMove(matrs);
        System.out.printf("testJoinMoveNullNotNull: depth=%d", depth);
        checkJoinSFMS(matrs,res);
        System.out.println("...............[OK]");
    }


    private static SFileMatrixL[] genSFMS(int m,int n,double dden,Random rnd, long mod,
                                          int depth, boolean[] where)
        throws IOException{
        SFileMatrixL[] matrs=new SFileMatrixL[4];
        for (int i = 0; i < 4; i++) {
            if (where[i]) {
                matrs[i]=new SFileMatrixL(depth,m,n,dden,rnd,mod);
            }
        }
        return matrs;
    }


    private static MatrixL joinSFMStoML(SFileMatrixL[] matrs)
        throws IOException{
        MatrixL[] mmatrs=new MatrixL[4];
        for (int i = 0; i < 4; i++) {
            if (matrs[i]!=null) {
                mmatrs[i] = matrs[i].toMatrixL();
            }
        }
        MatrixL res=(MatrixL)new MatrixOpsL().join(mmatrs);
        return res;
    }


    private static void checkJoinSFMS(SFileMatrixL[] matrs, SFileMatrixL res)
        throws IOException{
        MatrixL m1=joinSFMStoML(matrs);
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
        testSplitCopyMove1(512,512,70.0,rnd,mod,1,0);
        testSplitCopyMove1(512,512,70.0,rnd,mod,3,0);
        testSplitCopyMove1(512,512,70.0,rnd,mod,5,0);

        //move
        testSplitCopyMove1(512,512,70.0,rnd,mod,1,1);
        testSplitCopyMove1(512,512,70.0,rnd,mod,3,1);
        testSplitCopyMove1(512,512,70.0,rnd,mod,5,1);

        System.gc();
    }

    //type=0 - copy, =1 - move
    private static void testSplitCopyMove1(int m,int n,double dden,Random rnd,
                                           long mod, int depth, int type)
        throws IOException{
        System.out.printf("testSplitCopyMove: depth=%d", depth);
        //fm
        SFileMatrixL fm=new SFileMatrixL(depth,m,n,dden,rnd,mod);
        SFileMatrixL copy=fm.copy();

        //fm (SFML) --> fms (SFML[])
        SFileMatrixL[] fms;
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
        checkJoinSFMS(fms, copy);
        System.out.println("...............[OK]");
    }



    private static void testONE()
        throws IOException{
        System.out.println("====================================");
        System.out.println("============= Test ONE =============");
        System.out.println("====================================");
        //ONE
        testONE1(512,0);
        testONE1(512,1);
        testONE1(512,3);
        testONE1(512,5);
        System.gc();
    }


    private static void testONE1(int n,int depth)
        throws IOException{      Ring ring=new Ring("Zp[]");ring.setMOD32(113);
        //sfm=ONE (SFML)
        SFileMatrixL sfm=SFileMatrixL.ONE(depth,n,ring);

        //sfm (FML) --> matr (ML)
        MatrixL matr=sfm.toMatrixL();

        //matrRight=ONE (ML)
        MatrixL matrRight=MatrixL.ONE(n);

        //matr=?=matrRight
        boolean eq=matr.equals(matrRight);
        System.out.printf("testONE: root='%s', fm.getDepth()=%d, ok=%s\n",
                          sfm.getRoot(),sfm.getDepth(),
                          (eq?"Yeees!":"No-o-o-o???"));
        //выбросить ошибку в случае, когда не равны
        if (!eq) {
            throw new RuntimeException("Not equals????!!!");
        }
    }


    private static void testConstrSFM_FM(Random rnd,long mod)
        throws IOException{
        System.out.println("====================================");
        System.out.println("========== Test SFM(FM) ============");
        System.out.println("====================================");
        //ZERO
        //depth=0
        FileMatrixL fm=FileMatrixL.ZERO(512,256,0,Ring.ringR64xyzt);
        SFileMatrixL sfm=new SFileMatrixL(fm,mod);
        assertEquals(sfm.isZERO(),true,"!=0");
        //depth=1
        fm=FileMatrixL.ZERO(512,256,1,Ring.ringR64xyzt);
        sfm=new SFileMatrixL(fm,mod);
        assertEquals(sfm.isZERO(),true,"!=0");
        //depth=3
        fm=FileMatrixL.ZERO(512,256,3,Ring.ringR64xyzt);
        sfm=new SFileMatrixL(fm,mod);
        assertEquals(sfm.isZERO(),true,"!=0");

        //fm!=0
        testConstrSFM_FM1(512,256,3000,rnd,mod,0);
        testConstrSFM_FM1(512,256,3000,rnd,mod,1);
        testConstrSFM_FM1(512,256,3000,rnd,mod,3);
        testConstrSFM_FM1(512,256,3000,rnd,mod,5);
        System.gc();
    }


    private static void testConstrSFM_FM1(int m,int n,int den,Random rnd,
                                          long mod, int depth)
        throws IOException{
        //fm (FML)
        FileMatrixL fm=new FileMatrixL(depth,m,n,den,rnd,mod);
        //fm (FML) --> sfm (SFML)
        SFileMatrixL sfm=new SFileMatrixL(fm,mod);
        //fm,sfm-->m1,m2(ML). fm,sfm!=0, m2!=null
        MatrixL m1=fm.toMatrixL();
        MatrixL m2=sfm.toMatrixL();
        //m1=?=m2
        boolean eq=m1.equals(m2);
        System.out.printf("testConstrSFM_FM: fm='%s', sfm='%s', depth=%d, ok=%s\n",
                          fm.getRoot(),sfm.getRoot(),depth,
                          (eq?"Yeees!":"No-o-o-o???"));
        if (!eq){
            throw new RuntimeException("Not equals????!!!");
        }
    }



    private static void testConstrSFM_M(Random rnd,long mod)
        throws IOException{
        System.out.println("====================================");
        System.out.println("========== Test SFM(M) ============");
        System.out.println("====================================");
        //ZERO
        //depth=0
        MatrixL matr=new MatrixL(512,256);
        SFileMatrixL sfm=new SFileMatrixL(0,matr,mod);
        assertEquals(sfm.isZERO(),true,"!=0");
        //depth=1
        matr=new MatrixL(512,256);
        sfm=new SFileMatrixL(1,matr,mod);
        assertEquals(sfm.isZERO(),true,"!=0");
        //depth=3
        matr=new MatrixL(512,256);
        sfm=new SFileMatrixL(3,matr,mod);
        assertEquals(sfm.isZERO(),true,"!=0");

        //matr!=0
        testConstrSFM_M1(512,256,3000,rnd,mod,0);
        testConstrSFM_M1(512,256,3000,rnd,mod,1);
        testConstrSFM_M1(512,256,3000,rnd,mod,3);
        testConstrSFM_M1(512,256,3000,rnd,mod,5);
        System.gc();
    }



    private static void testConstrSFM_M1(int m,int n,int den,Random rnd,
                                         long mod, int depth)
        throws IOException{
        //создать matr(ML)
        MatrixL matr=new MatrixL(m,n,den,mod,rnd);
        //matr(ML) --> sfm (SFML)
        SFileMatrixL sfm=new SFileMatrixL(depth,matr,mod);
        //sfm (SFML) --> matr2 (ML)
        MatrixL matr2=sfm.toMatrixL();
        //matr=?=matr2
        boolean eq=matr.equals(matr2);
        System.out.printf("testConstrSFM_M: sfm='%s', depth=%d, ok=%s\n",
                          sfm.getRoot(),depth,
                          (eq?"Yeees!":"No-o-o-o???"));
        if (!eq){
            throw new RuntimeException("Not equals????!!!");
        }
    }


    private static void assertEquals(int i1, int i2, String msg){
        if (i1!=i2) {
            throw new RuntimeException(msg);
        }
    }


    private static void assertEquals(double d1, double d2, String msg){
        if (Math.abs(d1-d2)>1E-6) {
            throw new RuntimeException(msg);
        }
    }


    private static void assertEquals(boolean b1, boolean b2, String msg){
        if (b1!=b2) {
            throw new RuntimeException(msg);
        }
    }


}
