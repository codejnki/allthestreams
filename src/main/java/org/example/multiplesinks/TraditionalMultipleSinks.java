package org.example.multiplesinks;

public class TraditionalMultipleSinks {
    public static void main(String[] args) throws InterruptedException {
        int itemsPerSecond = 10;
        int value = 0;
        while (true) {
            if (value % 2 == 0) {
                System.out.println("Value is present " + value);
            } else {
                System.out.println("Value is empty");
            }

            value++;
            Thread.sleep(1000 / itemsPerSecond);
        }

    }
}
