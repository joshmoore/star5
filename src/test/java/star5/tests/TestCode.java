package star5.tests;

import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.img.basictypeaccess.array.ByteArray;
import net.imglib2.type.numeric.integer.ByteType;
import org.junit.Test;
import star5.MultiFileHDF5StorageDescriptor;
import star5.StorageDescriptor;

public class TestCode {

    @Test
    public void testFromRAI() throws Exception {
        StorageDescriptor sd = new MultiFileHDF5StorageDescriptor("", "/tmp/test-x[%d-%d]-y[%d-%d]-z[%d-%d].h5",
                "data", new long[]{512, 512, 5}, new int[]{256, 256, 5}, null);
        ArrayImg<ByteType, ByteArray> rai = ArrayImgs.bytes(1024, 1024, 10);
        sd.saveRAI(rai);
    }

}
