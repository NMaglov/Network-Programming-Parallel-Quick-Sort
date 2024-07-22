package base.server;

public class Utils {
    /**
     * Swap elements with given positions in array.
     *
     * @param arr  array whose elements will be swapped.
     * @param pos1 first element position.
     * @param pos2 second element position.
     */
    public static void swap(int[] arr, int pos1, int pos2) {
        int tmp = arr[pos1];
        arr[pos1] = arr[pos2];
        arr[pos2] = tmp;
    }

    /**
     * Partitions the array in 2 parts according to pivot.
     *
     * @param arr Array that is partitioned.
     * @param l   Left border of interval that is partitioned.
     * @param r   Right border of interval that is partitioned.
     * @return Position of pivot element is partitioned array.
     */
    public static int partition(int[] arr, int l, int r, int pivotPos) {
        swap(arr, pivotPos, r);
        pivotPos = r;
        int pos = l - 1;
        for (int i = l; i < r; i++) {
            if (arr[i] < arr[pivotPos]) {
                pos++;
                swap(arr, pos, i);
            }
        }
        pos++;
        swap(arr, pos, pivotPos);
        return pos;
    }
}
