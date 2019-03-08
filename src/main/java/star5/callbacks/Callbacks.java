package star5.callbacks;

import star5.Partition;

import java.util.ArrayList;
import java.util.List;

public class Callbacks implements PartitionInterface {

    private List<PartitionCallback> partitionCallbacks = new ArrayList<>();

    public Callbacks(Callback...callbacks) {
        for (Callback cb : callbacks) {
            cb.registerWith(this);
        }
    }

    public void add(PartitionCallback cb) {
        this.partitionCallbacks.add(cb);
    }

    @Override
    public void beforePartition(Partition partition) {
        for (PartitionCallback cb : partitionCallbacks ) {
            cb.beforePartition(partition);
        }
    }

    @Override
    public void afterPartition(Partition partition) {
        for (PartitionCallback cb : partitionCallbacks ) {
            cb.afterPartition(partition);
        }
    }


    public void failedPartition(Partition partition, Throwable throwable) {
        for (PartitionCallback cb : partitionCallbacks ) {
            cb.failedPartition(partition, throwable);
        }
    }

    @Override
    public void registerWith(Callbacks callbacks) {
        throw new RuntimeException("Recursion");
    }

}
