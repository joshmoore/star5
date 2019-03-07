package star5.tests;

import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.img.basictypeaccess.array.ByteArray;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.integer.ByteType;
import net.imglib2.util.Util;
import net.imglib2.view.Views;
import org.janelia.saalfeldlab.n5.GzipCompression;
import org.janelia.saalfeldlab.n5.hdf5.N5HDF5Writer;
import org.janelia.saalfeldlab.n5.imglib2.N5Utils;
import org.junit.Test;
import star5.AbstractStorageDescriptor;
import star5.MultiFileHDF5StorageDescriptor;

public class TestCode {

    @Test
    public void testFromRAI() throws Exception {
        AbstractStorageDescriptor sd = new MultiFileHDF5StorageDescriptor("", "/tmp/test-x[%d-%d]-y[%d-%d]-z[%d-%d].h5",
                "data", new long[]{512, 512, 5}, new int[]{256, 256, 5}, null);
        ArrayImg<ByteType, ByteArray> rai = ArrayImgs.bytes(new long[]{1024, 1024, 10});
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
            N5Utils.saveBlock(Views.interval(rai, p.interval()), writer, "/test", offset);
        }

    }

}
