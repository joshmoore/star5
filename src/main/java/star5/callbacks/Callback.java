package star5.callbacks;

import star5.Partition;

/**
 * Base interface for all callback which will be passed to writing methods.
 */
public interface Callback {

    void registerWith(CallbackHelper helper);

}
