package org.example.simpleexample;

public class TraditionalSimpleExample {

    public static void main(String[] args) throws InterruptedException {
        int itemsPerSecond = 10;
        int value = 0;
        while (true) {
            if (value % 2 == 0) {
                System.out.println("Value is even " + value);
            }

            value++;
            Thread.sleep(1000 / itemsPerSecond);
        }
    }
}
