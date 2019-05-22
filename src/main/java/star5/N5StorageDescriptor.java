package star5;

import net.imglib2.Interval;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.NativeType;
import net.imglib2.util.Util;
import net.imglib2.view.Views;
import org.janelia.saalfeldlab.n5.GzipCompression;
import org.janelia.saalfeldlab.n5.N5FSWriter;
import org.janelia.saalfeldlab.n5.hdf5.N5HDF5Writer;
import org.janelia.saalfeldlab.n5.imglib2.N5Utils;
import star5.callbacks.Callback;
import star5.callbacks.Callbacks;

import java.io.IOException;

/**
 * {@link AbstractStorageDescriptor} implementation which uses {@link N5HDF5Writer} to write
 * {@link Views#interval(RandomAccessible, Interval)} wrapped {@link Partition} instances
 * to disk.
 */
public class N5StorageDescriptor extends AbstractStorageDescriptor {
    public N5StorageDescriptor(String header, String partitionPattern, String datasetPattern,
                               long[] partitionSize, int[] chunkSize, Object filters) {
        super(header, partitionPattern, datasetPattern, partitionSize, chunkSize, filters);
    }

    public <T extends NativeType<T>> void handlePartition(RandomAccessibleInterval<T> rai, long[] offset, Partition p) throws IOException {
        N5FSWriter writer = new N5FSWriter(p.getPath());
        writer.createDataset("/test", p.getDimensions(), p.getChunkSizes(),
                N5Utils.dataType(Util.getTypeFromInterval(rai)),
                new GzipCompression());
        N5Utils.saveBlock(Views.interval(rai, p.getInterval()), writer, "/test", offset);
    }

}