package com.juns.pay.pay;

import java.util.Random;
import org.junit.Test;

public class RndomlyTest {

    @Test
    public void splitRandom() {
        double amount = 10000;
        int count = 10;
        while (count > 0) {
            double r = this.splitRandom(amount, count);
            System.out.println(" amount : " + amount + " r : " + r);
            amount -= (Double.parseDouble(String.format("%.2f", r)));
            count--;
            assert r >= 0L;
        }
    }

    private double splitRandom(double amount, int count) {
        Random random = new Random();
        if (count == 1 || amount < count) {
            return Double.parseDouble(String.format("%.3f", amount));
        }
        double equalyAmount = amount / count;
        double rn;
        if (random.nextInt() % 2 == 0 && amount - equalyAmount > 0) {
            rn = (random.nextInt((int) ((amount - equalyAmount) * 100)) / 100.0);
        } else if (equalyAmount > 0) {
            rn = (random.nextInt((int) ((equalyAmount) * 100)) / 100.0) * (-1);
        } else {
            return amount;
        }
        System.out.print("equalyAmount : " + equalyAmount + " rn : " + rn);
        double receiveAmount = equalyAmount + rn;
        return Double.parseDouble(String.format("%.2f", receiveAmount));
    }
}
