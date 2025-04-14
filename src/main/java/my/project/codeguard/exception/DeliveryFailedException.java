package my.project.codeguard.exception;

public class DeliveryFailedException extends RuntimeException {
    public DeliveryFailedException(String message) {
        super(message);
    }

    public DeliveryFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
