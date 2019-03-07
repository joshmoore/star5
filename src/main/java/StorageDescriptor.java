import ncsa.hdf.hdf5lib.H5;
import net.imglib2.FinalInterval;
import net.imglib2.Interval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.img.basictypeaccess.array.ByteArray;
import net.imglib2.type.NativeType;
import net.imglib2.type.Type;
import net.imglib2.type.numeric.integer.ByteType;
import net.imglib2.util.ImgUtil;
import net.imglib2.util.Intervals;
import net.imglib2.util.Util;
import net.imglib2.view.Views;
import org.janelia.saalfeldlab.n5.DatasetAttributes;
import org.janelia.saalfeldlab.n5.GzipCompression;
import org.janelia.saalfeldlab.n5.hdf5.N5HDF5Writer;
import org.janelia.saalfeldlab.n5.imglib2.N5Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class AbstractStorageDescriptor {

    private String header;
    private String partitionPattern;
    private String datasetPattern; // naming!
    private long[] partitionSizes;
    private int[] chunkSizes;
    Object filters;

    AbstractStorageDescriptor(String header,
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

    public class Partition {
        Interval interval;

        Partition(Interval interval) {
            this.interval = interval;
        }

        int[] blockSizes() {
            return chunkSizes;
        }

        String getPath() {
            ArrayList<Long> intervals = new ArrayList<>();
            for (int d = 0; d < interval.numDimensions(); d++) {
                intervals.add(interval.min(d));
                intervals.add(interval.max(d));
            }

            return String.format(partitionPattern, intervals.toArray());
        }

        public long[] dimensions() {
            long[] dimensions = new long[interval.numDimensions()];
            interval.dimensions(dimensions);
            return dimensions;
        }

        @Override
        public String toString() {
            return getPath();
        }


    }

    <T extends Type<T>> List<Partition> getPartitions(RandomAccessibleInterval<T> rai) {
        int n = rai.numDimensions();
        long[] dimensions = new long[n];
        rai.dimensions(dimensions);

        long[] numberOfPartitions = new long[n];
        for(int d = 0; d < n; d++) {
            numberOfPartitions[d] = Double.valueOf(Math.ceil((double) dimensions[d] / partitionSizes[d])).longValue();
        }

        long[] partitionIndex = new long[n];
        List<Partition> partitions = new ArrayList<Partition>();
        for (int d = 0; d < n;) {

            long[] min = new long[n];
            long[] max = new long[n];
            for (int d2 = 0; d2<n; d2++) {
                min[d2] = partitionIndex[d2] * partitionSizes[d2];
                max[d2] = (partitionIndex[d2] + 1) * partitionSizes[d2] - 1;
            }
            Interval i = new FinalInterval(min, max);
            partitions.add(new Partition(i));

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

class MultiFileHDF5StorageDescriptor extends AbstractStorageDescriptor {
    MultiFileHDF5StorageDescriptor(String header, String partitionPattern, String datasetPattern,
                                   long[] partitionSize, int[] chunkSize, Object filters) {
        super(header, partitionPattern, datasetPattern, partitionSize, chunkSize, filters);
    }
}

class TestCode {

    public static void main(String[] args) throws Exception {
        AbstractStorageDescriptor sd = new MultiFileHDF5StorageDescriptor("", "/tmp/test-x[%d-%d]-y[%d-%d]-z[%d-%d].h5",
                "data", new long[]{512, 512, 5}, new int[]{256, 256, 5}, null);
        ArrayImg<ByteType, ByteArray> rai = ArrayImgs.bytes(new long[]{4096, 4096, 25});
        fromRAI(rai, sd);
    }

    static <T extends NativeType<T>> void fromRAI(RandomAccessibleInterval<T> rai, AbstractStorageDescriptor sd) throws Exception {
        long[] offset = new long[rai.numDimensions()];
        for (Object o : sd.getPartitions(rai)) {
            AbstractStorageDescriptor.Partition p = (AbstractStorageDescriptor.Partition) o;
            System.out.println(p);
            N5HDF5Writer writer = new N5HDF5Writer(p.getPath(), p.blockSizes());
            writer.createDataset("/test", p.dimensions(), p.blockSizes(),
                    N5Utils.dataType(Util.getTypeFromInterval(rai)),
                    new GzipCompression());
            N5Utils.saveBlock(Views.interval(rai, p.interval), writer, "/test", offset);
            H5
        }

    }

}