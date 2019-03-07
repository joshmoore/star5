package star5.callbacks;

import star5.Partition;

/**
 * Callback called on the writing of each {@link Partition}
 * as opposed to each chunk.
 */
public class PartitionCallback implements Callback {



    public void beforePartition(Partition partition) {
        // no-op TODO logging?
    }

    public void afterPartition(Partition partition) {
        // no-op
    }

    public void failedPartition(Partition partition, Throwable throwable) {

    }

    @Override
    public void registerWith(CallbackHelper helper) {
        helper.add(this);
    }

}
