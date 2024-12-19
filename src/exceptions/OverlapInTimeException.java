package exceptions;

public class OverlapInTimeException extends RuntimeException {
    public OverlapInTimeException(String message) {
        super(message);
    }
}
