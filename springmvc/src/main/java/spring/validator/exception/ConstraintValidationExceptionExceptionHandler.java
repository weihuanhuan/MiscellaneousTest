package spring.validator.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import spring.validator.response.ValidationErrorResponse;
import spring.validator.response.Violation;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

//Note the @ControllerAdvice annotation which makes the exception handler methods available globally to all controllers within the application context.
@ControllerAdvice
public class ConstraintValidationExceptionExceptionHandler {

    //处理 spring.json.validator.RequestBodyValidateBeanTest.requestBodyValidateBeanManualTest 中我们手动抛出的异常
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    ValidationErrorResponse onConstraintValidationException(ConstraintViolationException e) {
        ValidationErrorResponse error = new ValidationErrorResponse();
        for (ConstraintViolation constraintViolation : e.getConstraintViolations()) {
            Violation violation = new Violation();
            violation.setFieldName(constraintViolation.getPropertyPath().toString());
            violation.setMessage(constraintViolation.getMessage());
            error.getViolations().add(violation);
        }
        return error;
    }

}