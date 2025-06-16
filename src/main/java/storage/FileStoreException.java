package storage;

/**
 * Custom File Store Exception
 */
public class FileStoreException  extends  Exception{

    public FileStoreException() {
    }

    public FileStoreException(String message) {
        super(message);
    }

    public FileStoreException(String message, Throwable cause) {
        super(message, cause);
    }

    public FileStoreException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public FileStoreException(Throwable cause) {
        super(cause);
    }
    
}
