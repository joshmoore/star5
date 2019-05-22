package star5.callbacks;

import star5.Partition;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper registry of {@link Callback} instances.
 *
 * Methods which take a vararg of {@link Callback} instances should
 * pass all instances to {@link Callbacks} to have them type checked.
 *
 * Example:
 * <pre>
 *     void myMethod(Callback...callbacks) {
 *         Callbacks cb = new Callbacks(callbacks);
 *         for (Partition p : getPartitions(...)) {
 *             cb.beforePartition(p);
 *             try {
 *                 cb.afterPartition(p);
 *             } catch (Throwable t) {
 *                 cb.failedPartition(p, t);
 *             }
 *          }
 *     }
 * </pre>
 *
 */
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
