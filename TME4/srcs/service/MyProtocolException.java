package srcs.service;

import java.io.Serializable;

// Extends RuntimeException in order to not force to catch them in try/catch blocks
public class MyProtocolException extends RuntimeException implements Serializable {

    public MyProtocolException() {
        super();
    }

    public MyProtocolException(String message) {
        super(message);
    }

    public MyProtocolException(String message, Throwable cause) {
        super(message, cause);
    }

    public MyProtocolException(Throwable cause) {
        super(cause);
    }

}
