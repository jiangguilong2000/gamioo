package io.gamioo.sandbox;

import org.apache.lucene.util.RamUsageEstimator;
import org.jctools.queues.MpscBlockingConsumerArrayQueue;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * some description
 *
 * @author Allen Jiang
 * @since 1.0.0
 */
public class MemoryTest {
    public static void main(String[] args) {

        ;
        System.out.println(new Date(1623417396472l));
        PlayerService service = new PlayerService();
        PlayerService service2 = new PlayerService();
        MpscBlockingConsumerArrayQueue<Runnable> queue = new MpscBlockingConsumerArrayQueue<>(1000000);


        RamUsageEstimator.shallowSizeOf(service2);

        List<ProtoTest> list = new ArrayList<>(100);
        //计算指定对象本身在堆空间的大小，单位字节
        long shallowSize = RamUsageEstimator.shallowSizeOf(queue);
        System.out.println(shallowSize);
        //计算指定对象及其引用树上的所有对象的综合大小，单位字节
        System.out.println(RamUsageEstimator.humanReadableUnits(RamUsageEstimator.sizeOfObject(queue)));
        System.out.println(RamUsageEstimator.sizeOfObject(RamUsageEstimator.shallowSizeOf(queue)));


    }
}

