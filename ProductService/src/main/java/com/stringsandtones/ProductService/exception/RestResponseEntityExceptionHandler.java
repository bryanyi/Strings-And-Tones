package com.stringsandtones.ProductService.exception;

import com.stringsandtones.ProductService.model.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

  // ProductServiceException.class is how this handler will know to invoke this method
  @ExceptionHandler(ProductServiceException.class)
  public ResponseEntity<ErrorResponse> handleProductServiceException(
      ProductServiceException exception) {
    return new ResponseEntity<>(
        new ErrorResponse()
            .builder()
            .errorMessage(exception.getMessage())
            .errorCode(exception.getErrorCode())
            .build(),
        HttpStatus.NOT_FOUND);
  }
}
