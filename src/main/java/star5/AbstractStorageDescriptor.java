package star5;

import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.NativeType;
import star5.callbacks.Callback;
import star5.callbacks.Callbacks;

import java.io.IOException;

/**
 * Base class for {@link StorageDescriptor} implementations.
 */
public abstract class AbstractStorageDescriptor implements StorageDescriptor {

    protected String header;
    protected String datasetPattern; // naming!
    protected long[] partitionSizes;
    protected int[] chunkSizes;
    protected Object filters;

    public AbstractStorageDescriptor(String header,
                              String datasetPattern,
                              long[] partitionSizes,
                              int[] chunkSizes, Object filters) {
        this.header = header;
        this.datasetPattern = datasetPattern;
        this.partitionSizes = partitionSizes;
        this.chunkSizes = chunkSizes;
        this.filters = filters;
    }


    public <T extends NativeType<T>> void saveRAI(RandomAccessibleInterval<T> rai, Callback... callbacks) throws Exception {
        Callbacks cb = new Callbacks(callbacks);
        long[] offset = new long[rai.numDimensions()];
        for (Partition p : getPartitions(rai)) {
            cb.beforePartition(p);
            try {
                handlePartition(rai, offset, p);
                cb.afterPartition(p);
            } catch (Throwable t) {
                cb.failedPartition(p, t);
            }
        }
    }

    public abstract <T extends NativeType<T>> void handlePartition(RandomAccessibleInterval<T> rai, long[] offset, Partition p) throws IOException;

}
