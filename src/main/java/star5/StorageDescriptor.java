package star5;

import net.imglib2.FinalInterval;
import net.imglib2.Interval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.NativeType;
import net.imglib2.type.Type;

import java.util.ArrayList;
import java.util.List;

public interface StorageDescriptor {

    <T extends Type<T>> List<Partition> getPartitions(RandomAccessibleInterval<T> rai);

    <T extends NativeType<T>> void saveRAI(RandomAccessibleInterval<T> rai) throws Exception;

}