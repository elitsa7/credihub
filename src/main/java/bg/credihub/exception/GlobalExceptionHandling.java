package bg.credihub.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class GlobalExceptionHandling {

    @ExceptionHandler({UserNotFoundException.class,
            LoanProductNotFoundException.class,
            LoanApplicationNotFoundException.class})
    public ModelAndView handleNotFoundExceptions(RuntimeException e) {
        ModelAndView mav = new ModelAndView("error/404");
        mav.addObject("status", 404);
        mav.addObject("message", e.getMessage());
        return mav;
    }

    @ExceptionHandler({EmailAlreadyExistsException.class,
            IdentificationNumberAlreadyExistsException.class,
            PhoneNumberAlreadyExistsException.class})
    public ModelAndView handleConflictExceptions(RuntimeException e) {
        ModelAndView mav = new ModelAndView("error/409");
        mav.addObject("status", 409);
        mav.addObject("message", e.getMessage());
        return mav;
    }

    @ExceptionHandler({UnauthorizedActionException.class,
            RoleModificationException.class})
    public ModelAndView handleForbiddenExceptions(RuntimeException e) {
        ModelAndView mav = new ModelAndView("error/403");
        mav.addObject("status", 403);
        mav.addObject("message", e.getMessage());
        return mav;
    }

    @ExceptionHandler({InvalidLoanApplicationException.class,
            InvalidLoanProductException.class,
            PasswordsDoNotMatchException.class})
    public ModelAndView handleBadRequestExceptions(RuntimeException e) {
        ModelAndView mav = new ModelAndView("error/400");
        mav.addObject("status", 400);
        mav.addObject("message", e.getMessage());
        return mav;
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView handleUnexpectedException(Exception e) {
        ModelAndView mav = new ModelAndView("error/500");
        mav.addObject("status", 500);
        mav.addObject("message", "An unexpected error occurred.");
        return mav;
    }


}
