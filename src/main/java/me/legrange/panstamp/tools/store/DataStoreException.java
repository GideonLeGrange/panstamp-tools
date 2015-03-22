package me.legrange.panstamp.tools.store;

import me.legrange.panstamp.NetworkException;

/**
 * Exception thrown if there is a problem loading from or saving to a DataStore
 *
 * @author gideon
 */
public class DataStoreException extends NetworkException {

    public DataStoreException(String message) {
        super(message);
    }

    public DataStoreException(String message, Throwable cause) {
        super(message, cause);
    }

}
