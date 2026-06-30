package com.notes.backend.admin;

import com.notes.backend.auth.EmailAlreadyUsedException;
import com.notes.backend.auth.UsernameAlreadyUsedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/** Maps admin operation errors to HTTP responses for the admin controllers. */
@RestControllerAdvice(assignableTypes = {AdminUserController.class, AdminCatalogController.class})
public class AdminExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    ProblemDetail handleNotFound(UserNotFoundException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(LastAdminException.class)
    ProblemDetail handleLastAdmin(LastAdminException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler({EmailAlreadyUsedException.class, UsernameAlreadyUsedException.class})
    ProblemDetail handleConflict(RuntimeException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
    }
}
