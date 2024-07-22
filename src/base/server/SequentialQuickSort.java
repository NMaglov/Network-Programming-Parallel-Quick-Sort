package base.server;

import java.util.Random;

public class SequentialQuickSort {
    private final Random random = new Random();

    public void sort(int[] arr, int l, int r) {
        if (l >= r) {
            return;
        }
        int cur = Utils.partition(arr, l, r, random.nextInt(l, r + 1));
        sort(arr, l, cur - 1);
        sort(arr, cur + 1, r);
    }
}
