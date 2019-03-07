package star5;

import net.imglib2.FinalInterval;
import net.imglib2.Interval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.NativeType;
import net.imglib2.type.Type;

import java.util.ArrayList;
import java.util.List;

/**
 * Generic description of how multidimensional data can be partitioned into multiple files for writing to disk.
 *
 * Each {@link StorageDescriptor} implementation is responsible for mapping from a single N-dimensional matrix
 * to multiple {@link Partition}s where each {@link Partition} is responsible for storing particular intervals
 * of each dimension. Implementations for common storage formats like HDF5, N5, and TIFF exist. Enough files
 * will be created to represent the entire matrix and additionally one or more "header" files will be created
 * to tie back together the multiple individual representations into a single whole.
 */
public interface StorageDescriptor {

    /**
     * Given a {@link RandomAccessibleInterval} calculate the list of {@link Partition} instances which will
     * appropriately represent the matrix.
     *
     * @param rai non-null {@link RandomAccessibleInterval} which is the input matrix
     * @param <T> type of each matrix cell
     * @return non-null {@link List} of {@link Partition} instances, usually in an optimal layout order.
     */
    <T extends Type<T>> List<Partition> getPartitions(RandomAccessibleInterval<T> rai);

    /**
     *
     * Internally calls {@link #getPartitions(RandomAccessibleInterval)}.
     * @param rai non-null {@link RandomAccessibleInterval} representing the matrix to be saved
     * @param <T> type of each matrix cell
     * @throws Exception thrown if any errors occur during IO
     */
    <T extends NativeType<T>> void saveRAI(RandomAccessibleInterval<T> rai) throws Exception;

}