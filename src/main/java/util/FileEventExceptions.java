package util;

import java.io.IOException;

public class FileEventExceptions extends  IOException{

    public FileEventExceptions() {
    }

    public FileEventExceptions(String message) {
        super(message);
    }

    public FileEventExceptions(String message, Throwable cause) {
        super(message, cause);
    }

    public FileEventExceptions(Throwable cause) {
        super(cause);
    }
}