package star5;

import net.imglib2.FinalInterval;
import net.imglib2.Interval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.NativeType;
import net.imglib2.type.Type;
import star5.callbacks.Callback;


import java.util.ArrayList;
import java.util.List;

/**
 * Base class for {@link StorageDescriptor} implementations.
 *
 * Uses the {@link InternalPartition} inner class to reduce the
 */
public abstract class AbstractStorageDescriptor implements StorageDescriptor {

    private String header;
    private String partitionPattern;
    private String datasetPattern; // naming!
    private long[] partitionSizes;
    private int[] chunkSizes;
    private Object filters;

    public AbstractStorageDescriptor(String header,
                              String partitionPattern, String datasetPattern,
                              long[] partitionSizes,
                              int[] chunkSizes, Object filters) {
        this.header = header;
        this.partitionPattern = partitionPattern;
        this.datasetPattern = datasetPattern;
        this.partitionSizes = partitionSizes;
        this.chunkSizes = chunkSizes;
        this.filters = filters;
    }

    /**
     * {@link Partition} implementation which has access to all of the inner variables of the containing
     * {@link AbstractStorageDescriptor} instance.
     */
     private class InternalPartition implements Partition {

        private Interval interval;

        InternalPartition(Interval interval) {
            this.interval = interval;
        }

        @Override
        public int[] getChunkSizes() {
            return chunkSizes;
        }

        @Override
        public String getPath() {
            ArrayList<Long> intervals = new ArrayList<>();
            for (int d = 0; d < interval.numDimensions(); d++) {
                intervals.add(interval.min(d));
                intervals.add(interval.max(d));
            }

            return String.format(partitionPattern, intervals.toArray());
        }

        @Override
        public long[] getDimensions() {
            long[] dimensions = new long[interval.numDimensions()];
            interval.dimensions(dimensions);
            return dimensions;
        }

        @Override
        public Interval getInterval() {
            return interval;
        }

        @Override
        public String toString() {
            return getPath();
        }

    }

    @Override
    public <T extends Type<T>> List<Partition> getPartitions(RandomAccessibleInterval<T> rai) {
        int n = rai.numDimensions();
        long[] dimensions = new long[n];
        rai.dimensions(dimensions);

        long[] numberOfPartitions = new long[n];
        for (int d = 0; d < n; d++) {
            numberOfPartitions[d] = Double.valueOf(Math.ceil((double) dimensions[d] / partitionSizes[d])).longValue();
        }

        long[] partitionIndex = new long[n];
        List<Partition> partitions = new ArrayList<>();
        for (int d = 0; d < n; ) {

            long[] min = new long[n];
            long[] max = new long[n];
            for (int d2 = 0; d2 < n; d2++) {
                min[d2] = partitionIndex[d2] * partitionSizes[d2];
                max[d2] = (partitionIndex[d2] + 1) * partitionSizes[d2] - 1;
            }
            Interval i = new FinalInterval(min, max);
            partitions.add(new InternalPartition(i));

            for (d = 0; d < n; ++d) {
                partitionIndex[d] += 1;
                if (partitionIndex[d] < numberOfPartitions[d])
                    break;
                else
                    partitionIndex[d] = 0;
            }
        }
        return partitions;
    }


    @Override
    public abstract <T extends NativeType<T>> void saveRAI(RandomAccessibleInterval<T> rai, Callback... callbacks) throws Exception;

}
