package io.gamioo.benchmark;


import org.jctools.queues.MpscChunkedArrayQueue;
import org.jctools.queues.MpscUnboundedArrayQueue;

import java.util.Collection;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.*;

/**
 * 比较队列的消耗情况。
 *
 * @author hmilyylimh
 * ^_^
 * @version 0.0.1
 * ^_^
 * @date 2018/3/30
 */
public class CompareQueueCosts {

    /**
     * 生产者数量
     */
    private static int COUNT_OF_PRODUCER = 2;

    /**
     * 消费者数量
     */
    private static final int COUNT_OF_CONSUMER = 1;

    /**
     * 准备添加的任务数量值
     */
    private static final int COUNT_OF_TASK = 1 << 20;

    /**
     * 线程池对象
     */
    private static ExecutorService executorService;

    public static void main(String[] args) throws Exception {

        for (int j = 1; j < 7; j++) {
            COUNT_OF_PRODUCER = (int) Math.pow(2, j);
            executorService = Executors.newFixedThreadPool(COUNT_OF_PRODUCER * 2);

            int basePow = 8;
            int capacity = 0;
            for (int i = 1; i <= 3; i++) {
                capacity = 1 << (basePow + i);
                System.out.print("Producers: " + COUNT_OF_PRODUCER + "\t\t");
                System.out.print("Consumers: " + COUNT_OF_CONSUMER + "\t\t");
                System.out.print("Capacity: " + capacity + "\t\t");
              //  System.out.print("LinkedBlockingQueue: " + testQueue(new LinkedBlockingQueue<Integer>(capacity), COUNT_OF_TASK) + "s" + "\t\t");
                // System.out.print("ArrayList: " + testQueue(new ArrayList<Integer>(capacity), COUNT_OF_TASK) + "s" + "\t\t");
                // System.out.print("LinkedList: " + testQueue(new LinkedList<Integer>(), COUNT_OF_TASK) + "s" + "\t\t");
                System.out.print("MpscUnboundedArrayQueue: " + testQueue(new MpscUnboundedArrayQueue<Integer>(capacity), COUNT_OF_TASK) + "s" + "\t\t");
               // System.out.print("MpscChunkedArrayQueue: " + testQueue(new MpscChunkedArrayQueue<Integer>(capacity), COUNT_OF_TASK) + "s" + "\t\t");
             //   System.out.println();
            }


            executorService.shutdown();
        }
    }

    private static Double testQueue(final Collection<Integer> queue, final int taskCount) throws Exception {
        CompletionService<Long> completionService = new ExecutorCompletionService<Long>(executorService);

        long start = System.currentTimeMillis();
        for (int i = 0; i < COUNT_OF_PRODUCER; i++) {
            executorService.submit(new Producer(queue, taskCount / COUNT_OF_PRODUCER));
        }
        for (int i = 0; i < COUNT_OF_CONSUMER; i++) {
            completionService.submit((new Consumer(queue, taskCount / COUNT_OF_CONSUMER)));
        }

        for (int i = 0; i < COUNT_OF_CONSUMER; i++) {
         //   System.out.println("take start");
            completionService.take().get();
       //     System.out.println("take end");
        }

        long end = System.currentTimeMillis();
        return Double.parseDouble("" + (end - start)) / 1000;
    }

    private static class Producer implements Runnable {
        private Collection<Integer> queue;
        private int taskCount;

        public Producer(Collection<Integer> queue, int taskCount) {
            this.queue = queue;
            this.taskCount = taskCount;
        }

        @Override
        public void run() {
            // Queue队列
            if (this.queue instanceof Queue) {
                Queue<Integer> tempQueue = (Queue<Integer>) this.queue;
                while (this.taskCount > 0) {
                    if (tempQueue.offer(this.taskCount)) {
                        this.taskCount--;
                    } else {
                        // System.out.println("Producer offer failed.");
                    }
                }
            }
//            // List列表
//            else if (this.queue instanceof List) {
//                List<Integer> tempList = (List<Integer>) this.queue;
//                while (this.taskCount > 0) {
//                    if (tempList.add(this.taskCount)) {
//                        this.taskCount--;
//                    } else {
//                        // System.out.println("Producer offer failed.");
//                    }
//                }
//            }
        }
    }

    private static class Consumer implements Callable<Long> {
        private Collection<Integer> queue;
        private int taskCount;

        public Consumer(Collection<Integer> queue, int taskCount) {
            this.queue = queue;
            this.taskCount = taskCount;
        }

        @Override
        public Long call() {
            // Queue队列
            if (this.queue instanceof Queue) {
                Queue<Integer> tempQueue = (Queue<Integer>) this.queue;
                while (this.taskCount > 0) {
                    if ((tempQueue.poll()) != null) {
                        this.taskCount--;
                    }
                }
            }
//            // List列表
//            else if (this.queue instanceof List) {
//                List<Integer> tempList = (List<Integer>) this.queue;
//                while (this.taskCount > 0) {
//                    if (!tempList.isEmpty() && (tempList.remove(0)) != null) {
//                        this.taskCount--;
//                    }
//                }
//            }
            return 0L;
        }
    }
}
