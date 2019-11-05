package cecs429.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class MathOperations {

    public static int editDist(String str1, String str2, int strlen1, int strlen2) {
        // Imagine this as being our grid when we do it on paper
        int dp[][] = new int[strlen1 + 1][strlen2 + 1];

        // Fill our grid (d[][])  bottom up manner
        for (int i = 0; i <= strlen1; i++) {
            for (int j = 0; j <= strlen2; j++) {
                // If first string is empty, only option is to
                // insert all characters of second string
                if (i == 0) {
                    dp[i][j] = j;  // Min. operations = j
                }
                // If second string is empty, only option is to
                // remove all characters of second string
                else if (j == 0) {
                    dp[i][j] = i; // Min. operations = i
                }
                // If last characters are same, ignore last char
                // and recur for remaining string
                else if (str1.charAt(i - 1) == str2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                }
                // If the last character is different, consider all
                // possibilities and find the minimum
                else {
                    dp[i][j] = 1 + min(dp[i][j - 1],  // Insert
                            dp[i - 1][j],  // Remove
                            dp[i - 1][j - 1]); // Replace
                }
            }
        }

        return dp[strlen1][strlen2];
    }

    private static int min(int x, int y, int z) {
        if (x <= y && x <= z) return x;
        if (y <= x && y <= z) return y;
        else return z;
    }

    public static double roundUp(double input) {
        BigDecimal bd = new BigDecimal(Double.toString(input));
        bd = bd.setScale(5, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
