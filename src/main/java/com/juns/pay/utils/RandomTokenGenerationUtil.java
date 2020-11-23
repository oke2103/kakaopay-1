package com.juns.pay.utils;

import java.util.Random;

public class RandomTokenGenerationUtil {

    public static String generateToken(int length) {
        char[] charaters = {
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R',
            'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r',
            's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
        StringBuilder sb = new StringBuilder();
        Random rn = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(charaters[rn.nextInt(charaters.length)]);
        }
        return sb.toString();
    }
}
