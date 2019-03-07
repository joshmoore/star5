package star5;

import net.imglib2.Interval;

/**
 * N-dimensional slice of a matrix which will be written independently to disk.
 *
 * This may be done by writing a separate file or as a uniquely marked region of
 * an existing file, e.g. a separate dataset in a larger HDF5 file.
 */
public interface Partition {

    /**
     * Total extent that this {@link Partition} covers.
     * @return non-null array matching the dimensionality of the overall matrix
     */
    long[] getDimensions();

    /**
     * Chunk sizes which should be applied to this {@link Partition} on writing.
     * @return non-null array matching the dimensionality of the overall matrix
     */
    int[] getChunkSizes();

    /**
     * Calculated name where this particular file would be written.
     * @return non-null {@link String} representation of a file path
     */
    String getPath();

    /**
     * Imglib2 representation of this
     * @return non-null {@link Interval} matching {@link #getDimensions()}
     */
    Interval getInterval();

}
