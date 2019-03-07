package star5;

public class MultiFileHDF5StorageDescriptor extends AbstractStorageDescriptor {
    public MultiFileHDF5StorageDescriptor(String header, String partitionPattern, String datasetPattern,
                                          long[] partitionSize, int[] chunkSize, Object filters) {
        super(header, partitionPattern, datasetPattern, partitionSize, chunkSize, filters);
    }
}