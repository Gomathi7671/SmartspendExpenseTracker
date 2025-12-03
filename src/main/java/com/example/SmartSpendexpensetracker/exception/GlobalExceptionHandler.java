package com.example.SmartSpendexpensetracker.exception;

import org.springframework.dao.DataAccessException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    // Handle custom exceptions
    @ExceptionHandler(UserNotFoundException.class)
    public String handleUserNotFound(UserNotFoundException ex, Model model) {
        model.addAttribute("error", ex.getMessage());
        return "message"; // your message.html page
    }

    @ExceptionHandler(InvalidTokenException.class)
    public String handleInvalidToken(InvalidTokenException ex, Model model) {
        model.addAttribute("error", ex.getMessage());
        return "message";
    }

    @ExceptionHandler(DatabaseException.class)
    public String handleDatabaseError(DatabaseException ex, Model model) {
        model.addAttribute("error", "Database error: " + ex.getMessage());
        return "message";
    }

    // Handle Spring Data exceptions
    @ExceptionHandler(DataAccessException.class)
    public String handleDataAccess(DataAccessException ex, Model model) {
        model.addAttribute("error", "Database access error. Try again later.");
        return "message";
    }

    // Handle all other exceptions
    // @ExceptionHandler(Exception.class)
    // public String handleGenericException(Exception ex, Model model) {
    //     model.addAttribute("error", "Unexpected error occurred. Please contact support.");
    //     return "message";
    // }


    @ExceptionHandler(ResourceNotFoundException.class)
public String handleResourceNotFound(ResourceNotFoundException ex, Model model) {
    model.addAttribute("error", ex.getMessage());
    return "message";
}

@ExceptionHandler(UnauthorizedActionException.class)
public String handleUnauthorized(UnauthorizedActionException ex, Model model) {
    model.addAttribute("error", ex.getMessage());
    return "message";
}
@ExceptionHandler(Exception.class)
    public String handleAllExceptions(Exception ex, Model model) {
        // Log the exception (optional)
        ex.printStackTrace();

        // Send error info to a generic error page
        model.addAttribute("errorMsg", "Something went wrong! Please try again later.");
        model.addAttribute("errorDetails", ex.getMessage());

        return "error"; // Thymeleaf template: error.html
    }

}
