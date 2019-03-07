package star5.callbacks;

import star5.Partition;

import java.util.ArrayList;
import java.util.List;

public class CallbackHelper implements PartitionInterface {

    private List<PartitionCallback> partitions = new ArrayList<>();

    public CallbackHelper(Callback...callbacks) {
        for (Callback cb : callbacks) {
            cb.registerWith(this);
        }
    }

    public void add(PartitionCallback cb) {
        this.partitions.add(cb);
    }

    @Override
    public void beforePartition(Partition partition) {
        for (PartitionCallback cb : partitions) {
            cb.beforePartition(partition);
        }
    }

    @Override
    public void afterPartition(Partition partition) {
        for (PartitionCallback cb : partitions) {
            cb.afterPartition(partition);
        }
    }


    public void failedPartition(Partition partition, Throwable throwable) {
        for (PartitionCallback cb : partitions) {
            cb.failedPartition(partition, throwable);
        }
    }

    @Override
    public void registerWith(CallbackHelper helper) {
        throw new RuntimeException("Recursion");
    }

}
