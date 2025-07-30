package test;

import java.util.Arrays;

public class test {
    public static void main(String[] args) {
        double i = (float)1.0;
        int[] arr = {1,2};
        int var = 0;
//        arr[var] = ++var;
        arr[var++] = ++var;
        System.out.printf("%s , %d",Arrays.toString(arr), var);
    }
}
