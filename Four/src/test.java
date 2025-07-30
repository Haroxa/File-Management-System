import java.util.Arrays;

public class test {
    public static void main(String[] args) {
        int var = 1;
        int[] arr = {1,10};
        arr[var-1] = var = 2;
        System.out.println(Arrays.toString(arr));
    }
}
