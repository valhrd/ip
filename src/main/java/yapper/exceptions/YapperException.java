package yapper.exceptions;

public class YapperException extends RuntimeException {
    public YapperException(String errorMessage) {
        super(errorMessage);
    }
}