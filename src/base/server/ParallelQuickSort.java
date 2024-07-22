package base.server;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Implements parallel quick sort algorithm.
 */
public class ParallelQuickSort implements AutoCloseable {
    private final static int MAX_SIZE = 1024;
    private final static SequentialQuickSort sequentialQuickSort = new SequentialQuickSort();
    private final AtomicInteger runningThreads = new AtomicInteger(0);
    private final Object lock = new Object();
    private final ExecutorService executorService;

    public ParallelQuickSort(int nThreads) {
        executorService = Executors.newFixedThreadPool(nThreads);
    }

    @Override
    public void close() {
        executorService.shutdown();
    }

    /**
     * Class that sorts array.
     */
    private class ParallelQuickSortRunnable implements Runnable {
        private final int[] arr;
        private final int l;
        private final int r;
        private final Random random;

        public ParallelQuickSortRunnable(int[] arr, int l, int r) {
            this.arr = arr;
            this.l = l;
            this.r = r;
            random = new Random();
        }

        /**
         * Partitions the array choosing random pivot and then creates new threads to
         * partition the 2 parts.
         */
        @Override
        public void run() {
            if (r - l < MAX_SIZE) {
                sequentialQuickSort.sort(arr, l, r);
            } else if (l < r) {
                int cur = Utils.partition(arr, l, r, random.nextInt(l, r + 1));
                runningThreads.addAndGet(2);
                executorService.submit(new ParallelQuickSortRunnable(arr, l, cur - 1));
                executorService.submit(new ParallelQuickSortRunnable(arr, cur + 1, r));
            }
            runningThreads.decrementAndGet();
            synchronized (lock) {
                if (runningThreads.get() == 0) {
                    lock.notifyAll();
                }
            }
        }
    }

    /**
     * Sorts array using quick sort.
     *
     * @param arr Array that is sorted.
     * @return Sorted array.
     */
    public int[] sort(int[] arr) throws InterruptedException {
        int n = arr.length;
        int[] sorted = new int[n];
        System.arraycopy(arr, 0, sorted, 0, n);
        runningThreads.incrementAndGet();
        executorService.submit(new ParallelQuickSortRunnable(sorted, 0, n - 1));
        synchronized (lock) {
            lock.wait();
        }
        return sorted;
    }
}
