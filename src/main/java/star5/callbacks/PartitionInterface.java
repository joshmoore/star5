package star5.callbacks;

import star5.Partition;

/**
 *
 */
public interface PartitionInterface extends Callback {

    void beforePartition(Partition partition);

    void afterPartition(Partition partition);

    void failedPartition(Partition partition, Throwable throwable); // TODO add exception


}
