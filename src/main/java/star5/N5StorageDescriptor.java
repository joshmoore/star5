package star5;

import net.imglib2.FinalInterval;
import net.imglib2.Interval;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.NativeType;
import net.imglib2.type.Type;
import net.imglib2.util.Util;
import net.imglib2.view.Views;
import org.janelia.saalfeldlab.n5.GzipCompression;
import org.janelia.saalfeldlab.n5.N5FSWriter;
import org.janelia.saalfeldlab.n5.hdf5.N5HDF5Writer;
import org.janelia.saalfeldlab.n5.imglib2.N5Utils;
import star5.callbacks.Callback;
import star5.callbacks.Callbacks;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * {@link AbstractStorageDescriptor} implementation which uses {@link N5FSWriter} to write
 * {@link Views#interval(RandomAccessible, Interval)} wrapped {@link Partition} instances
 * to disk.
 */
public class N5StorageDescriptor extends AbstractStorageDescriptor {

    final File directory;

    public N5StorageDescriptor(String header, File directory, String datasetPattern,
                               long[] partitionSize, int[] chunkSize, Object filters) throws IOException {
        super(header, datasetPattern, partitionSize, chunkSize, filters);
        this.directory = directory;
    }

    public <T extends NativeType<T>> void handlePartition(RandomAccessibleInterval<T> rai, long[] offset, Partition p) throws IOException {
        N5FSWriter writer = new N5FSWriter(p.getPath());
        writer.createDataset("/test", p.getDimensions(), p.getChunkSizes(),
                N5Utils.dataType(Util.getTypeFromInterval(rai)),
                new GzipCompression());
        N5Utils.saveBlock(Views.interval(rai, p.getInterval()), writer, "/test", offset);
    }

    // TODO: returns a single partition. Move to abstract superclass?
    @Override
    public <T extends Type<T>> List<Partition> getPartitions(RandomAccessibleInterval<T> rai) {
        final int n = rai.numDimensions();
        final long[] dimensions = new long[n];
        rai.dimensions(dimensions);
        return Arrays.asList(
                new Partition() {

                    @Override
                    public long[] getDimensions() {
                        return dimensions;
                    }

                    @Override
                    public int[] getChunkSizes() {
                        return chunkSizes;
                    }

                    @Override
                    public String getPath() {
                        return directory.getAbsolutePath();
                    }

                    @Override
                    public Interval getInterval() {
                        long[] min = new long[n];
                        long[] max = new long[n];
                        for (int i = 0; i < n; i++) {
                            max[i] = rai.max(i);
                        }
                        return new FinalInterval(min, max);
                    }
                }
        );
    }
}