package star5.callbacks;

import star5.Partition;
import star5.StorageDescriptor;

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
public class Callbacks implements FilesetInterface, PartitionInterface {

    private List<FilesetCallback> filesetCallbacks = new ArrayList<>();

    private List<PartitionCallback> partitionCallbacks = new ArrayList<>();

    public Callbacks(Callback...callbacks) {
        for (Callback cb : callbacks) {
            cb.registerWith(this);
        }
    }

    public void add(PartitionCallback cb) {
        this.partitionCallbacks.add(cb);
    }

    public void add(FilesetCallback cb) {
        this.filesetCallbacks.add(cb);
    }

    @Override
    public void registerWith(Callbacks callbacks) {
        throw new RuntimeException("Recursion");
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

    @Override
    public void failedPartition(Partition partition, Throwable throwable) {
        for (PartitionCallback cb : partitionCallbacks ) {
            cb.failedPartition(partition, throwable);
        }
    }

    @Override
    public void beforeFileset(StorageDescriptor sd) {
        for (FilesetCallback cb : filesetCallbacks) {
            cb.beforeFileset(sd);
        }
    }

    @Override
    public void afterFileset(StorageDescriptor sd) {
        for (FilesetCallback cb : filesetCallbacks) {
            cb.afterFileset(sd);
        }
    }

    @Override
    public void failedFileset(StorageDescriptor sd, Throwable throwable) {
        for (FilesetCallback cb : filesetCallbacks) {
            cb.failedFileset(sd, throwable);
        }
    }

}
