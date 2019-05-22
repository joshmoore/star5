package star5.callbacks;

import star5.Partition;
import star5.StorageDescriptor;

/**
 * Callback called on the writing of each {@link Partition}
 * as opposed to each chunk.
 */
public class FilesetCallback implements Callback {

    void beforeFileset(StorageDescriptor sd) {
        // no-op TODO logging?
    }

    public void afterFileset(StorageDescriptor sd) {

    }

    public void failedFileset(StorageDescriptor sd, Throwable throwable) {

    }

    @Override
    public void registerWith(Callbacks helper) {
        helper.add(this);
    }

}