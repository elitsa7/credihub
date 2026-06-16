package bg.credihub.exception;

public class IdentificationNumberAlreadyExistsException extends RuntimeException {
    public IdentificationNumberAlreadyExistsException(String message) {
        super(message);
    }
}
