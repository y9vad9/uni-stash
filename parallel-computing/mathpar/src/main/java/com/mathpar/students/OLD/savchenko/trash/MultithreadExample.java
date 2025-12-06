package com.mathpar.students.OLD.savchenko.trash;

import com.mathpar.number.Array;

import java.util.ArrayList;
import java.util.List;

public class MultithreadExample {

    public static void main(String[] args) {

        String[] hello = new String[]{"Hello"};
        String[] world = new String[]{"World"};

        List<Thread> threads= new ArrayList<>();
        Thread myThread = new MyThread(hello, world);
        threads.add(myThread);

        for (Thread t : threads) {
            t.start();
        }

        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println(Array.toString(hello));
        System.out.println(Array.toString(world));
    }

}

class MyThread extends Thread {
    String[] input;
    String[] output;

    public MyThread(String[] input, String[] output) {
        this.input = input;
        this.output = output;
    }

    @Override
    public void run() {
        input[0] = "101";
        output[0] = "0101";
        System.out.println(Array.toString(input));
        System.out.println(Array.toString(output));
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
