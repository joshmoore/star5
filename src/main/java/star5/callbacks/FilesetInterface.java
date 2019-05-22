package star5.callbacks;

import star5.Partition;
import star5.StorageDescriptor;

/**
 *
 */
public interface FilesetInterface extends Callback {

    void beforeFileset(StorageDescriptor sd);

    void afterFileset(StorageDescriptor sd);

    void failedFileset(StorageDescriptor sd, Throwable throwable); // TODO add exception

}
