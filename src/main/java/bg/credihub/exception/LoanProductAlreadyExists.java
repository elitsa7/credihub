package bg.credihub.exception;

public class LoanProductAlreadyExists extends RuntimeException {
    public LoanProductAlreadyExists(String message) {
        super(message);
    }
}
