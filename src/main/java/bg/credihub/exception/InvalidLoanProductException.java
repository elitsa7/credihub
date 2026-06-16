package bg.credihub.exception;

public class InvalidLoanProductException extends RuntimeException {
    public InvalidLoanProductException(String message) {
        super(message);
    }
}
