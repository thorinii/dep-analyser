package me.lachlanap.lct;

/**
 *
 * @author lachlan
 */
public class ConstantException extends RuntimeException {

    public ConstantException(String msg) {
        super(msg);
    }

    public ConstantException(String message, Throwable cause) {
        super(message, cause);
    }
}
