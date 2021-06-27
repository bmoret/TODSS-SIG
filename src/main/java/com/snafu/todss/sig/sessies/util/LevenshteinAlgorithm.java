package com.snafu.todss.sig.sessies.util;

import java.util.Locale;

public class LevenshteinAlgorithm {
    public static int calculateLevenshteinDistance(String val1, String val2) {
        if (val1.isBlank() || val2.isBlank()) {
            throw new IllegalArgumentException("deze waarde mag niet leeg zijn");
        }
        char[] str1 = val1.toLowerCase(Locale.ROOT).toCharArray();
        char[] str2 = val2.toLowerCase(Locale.ROOT).toCharArray();
        int[][] temp = new int[str1.length+1][str2.length+1];

        for(int i=0; i < temp[0].length; i++) {
            temp[0][i] = i;
        }

        for(int i=0; i < temp.length; i++) {
            temp[i][0] = i;
        }

        for(int i=1;i <=str1.length; i++) {
            for(int j=1; j <= str2.length; j++) {
                if(str1[i-1] == str2[j-1]) {
                    temp[i][j] = temp[i-1][j-1];
                } else {
                    temp[i][j] = 1 + Math.min(Math.min(temp[i-1][j-1], temp[i-1][j]), temp[i][j-1]);
                }
            }
        }

        return temp[str1.length][str2.length];
    }
}
