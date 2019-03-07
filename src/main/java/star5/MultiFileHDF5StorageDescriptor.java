package star5;

import net.imglib2.Interval;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.NativeType;
import net.imglib2.util.Util;
import net.imglib2.view.Views;
import org.janelia.saalfeldlab.n5.GzipCompression;
import org.janelia.saalfeldlab.n5.hdf5.N5HDF5Writer;
import org.janelia.saalfeldlab.n5.imglib2.N5Utils;

/**
 * {@link AbstractStorageDescriptor} implementation which uses {@link N5HDF5Writer} to write
 * {@link Views#interval(RandomAccessible, Interval)} wrapped {@link Partition} instances
 * to disk.
 */
public class MultiFileHDF5StorageDescriptor extends AbstractStorageDescriptor {
    public MultiFileHDF5StorageDescriptor(String header, String partitionPattern, String datasetPattern,
                                          long[] partitionSize, int[] chunkSize, Object filters) {
        super(header, partitionPattern, datasetPattern, partitionSize, chunkSize, filters);
    }


    @Override
    public <T extends NativeType<T>> void saveRAI(RandomAccessibleInterval<T> rai) throws Exception {
        long[] offset = new long[rai.numDimensions()];
        for (Partition p : getPartitions(rai)) {
            N5HDF5Writer writer = new N5HDF5Writer(p.getPath(), p.getChunkSizes());
            writer.createDataset("/test", p.getDimensions(), p.getChunkSizes(),
                    N5Utils.dataType(Util.getTypeFromInterval(rai)),
                    new GzipCompression());
            N5Utils.saveBlock(Views.interval(rai, p.getInterval()), writer, "/test", offset);
        }
    }

}