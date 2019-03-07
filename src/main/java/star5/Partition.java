package star5;

import net.imglib2.Interval;

public interface Partition {

    int[] blockSizes();

    String getPath();

    long[] dimensions();

    Interval interval();
}
