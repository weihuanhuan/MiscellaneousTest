package spring.json.validator.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import spring.json.validator.response.ValidationErrorResponse;
import spring.json.validator.response.Violation;

//Spring 3.2 brings support for a global @ExceptionHandler with the @ControllerAdvice annotation.
@ControllerAdvice
public class MethodArgumentNotValidResponseEntityExHandler extends ResponseEntityExceptionHandler {

    //处理 spring.json.validator.RequestBodyValidateBeanTest.requestBodyValidateBeanAutoTest 中 spring 自动抛出的异常
    //This enables a mechanism that breaks away from the older MVC model and makes use of ResponseEntity along with the type safety and flexibility of @ExceptionHandler:
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        ValidationErrorResponse error = new ValidationErrorResponse();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            Violation violation = new Violation();
            violation.setFieldName(fieldError.getField());
            violation.setMessage(fieldError.getDefaultMessage());
            error.getViolations().add(violation);
        }
        return handleExceptionInternal(ex, error, headers, status, request);
    }

}