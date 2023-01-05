package com.stringsandtones.ProductService.exception;

import lombok.Data;

@Data
public class ProductServiceException extends RuntimeException {

  private String errorCode;

  // While this is the custom exception, the handler is what utilizes this exception
  // via spring boot's @ControllerAdvice and @ExceptionHandler
  public ProductServiceException(String message, String errorCode) {
    super(message);
    this.errorCode = errorCode;
  }
}
