package star5.tests;

import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.img.basictypeaccess.array.ByteArray;
import net.imglib2.type.numeric.integer.ByteType;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import star5.MultiFileHDF5StorageDescriptor;
import star5.N5StorageDescriptor;
import star5.Partition;
import star5.StorageDescriptor;
import star5.callbacks.PartitionCallback;

import java.io.File;
import java.io.IOException;

public class N5Test {

    @Rule
    public final TemporaryFolder temp = new TemporaryFolder();

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Test()
    public void testThrowOnMultiFile() throws Exception {
        exception.expect(Exception.class); // TODO: new type
        runSD(new N5StorageDescriptor(
                "",
                temp.newFolder("N5Test.n5"),
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
