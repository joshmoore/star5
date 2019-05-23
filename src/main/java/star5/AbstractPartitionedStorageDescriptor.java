package star5;

import net.imglib2.FinalInterval;
import net.imglib2.Interval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.NativeType;
import net.imglib2.type.Type;
import star5.callbacks.Callback;
import star5.callbacks.Callbacks;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Base class for {@link StorageDescriptor} implementations which
 * take a pattern string to represent how multiple files should be written.
 */
public abstract class AbstractPartitionedStorageDescriptor extends AbstractStorageDescriptor {

    private String partitionPattern;

    public AbstractPartitionedStorageDescriptor(String header,
                                                String partitionPattern, String datasetPattern,
                                                long[] partitionSizes,
                                                int[] chunkSizes, Object filters) {
        super(header, datasetPattern, partitionSizes, chunkSizes, filters);
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

}
