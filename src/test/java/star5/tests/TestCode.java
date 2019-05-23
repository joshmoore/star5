package star5.tests;

import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.img.basictypeaccess.array.ByteArray;
import net.imglib2.type.numeric.integer.ByteType;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import star5.MultiFileHDF5StorageDescriptor;
import star5.N5StorageDescriptor;
import star5.Partition;
import star5.StorageDescriptor;
import star5.callbacks.PartitionCallback;

import java.io.File;
import java.io.IOException;

public class TestCode {

    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    String prefix() throws IOException {
        return temp.newFolder().getAbsolutePath();
    }
    @Test()
    public void testHDF5() throws Exception {
        // TODO: store pattern metadata in files
        runSD(new MultiFileHDF5StorageDescriptor(
                "",
                prefix() + "/test-xs%04d-xe%04d-ys%04d-ye%04d-zs%04d-ze%04d-cs%d-ce%d-ts%02d-te%02d.h5",
                "data",
                new long[]{1024, 1024, 5, 1, 1},
                new int[]{256, 256, 5, 1, 1},
                null));
    }

    @Test()
    public void testN5() throws Exception {

        runSD(new N5StorageDescriptor(
                "",
                temp.newFolder("testN5.n5"),
                "data",
                new long[]{1024, 1024, 5, 1, 1},
                new int[]{256, 256, 5, 1, 1},
                null));
    }

    private void runSD(StorageDescriptor sd) throws Exception {
        ArrayImg<ByteType, ByteArray> rai = ArrayImgs.bytes(1024, 1024, 10, 3, 6);
        sd.saveRAI(rai, new PartitionCallback(){
            @Override
            public void afterPartition(Partition partition) {
                File tmp = new File(partition.getPath());
                if (!tmp.exists()) {
                    throw new RuntimeException("didn't find file:" + tmp);
                } else {
                    tmp.deleteOnExit(); // Also handled by TemporaryFolder
                }
            }
        });
    }

}
