package bg.credihub.exception;

public class LoanProductNotFoundException extends RuntimeException {
    public LoanProductNotFoundException(String message) {
        super(message);
    }
}
