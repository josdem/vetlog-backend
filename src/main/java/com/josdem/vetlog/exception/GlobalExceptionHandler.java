/*
  Copyright 2025 Jose Morales contact@josdem.io

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
*/

package com.josdem.vetlog.exception;

import com.josdem.vetlog.dto.ErrorDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(InvalidTokenException.class)
  public ResponseEntity<ErrorDto> handleInvalidTokenException(
      InvalidTokenException ex, WebRequest request) {
    log.error("Invalid token exception: {}", ex.getMessage());

    ErrorDto errorDto =
        ErrorDto.builder().status(HttpStatus.FORBIDDEN.value()).message(ex.getMessage()).build();

    return new ResponseEntity<>(errorDto, HttpStatus.FORBIDDEN);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorDto> handleGenericException(Exception ex, WebRequest request) {
    log.error("Unexpected exception: {}", ex.getMessage(), ex);

    ErrorDto errorDto =
        ErrorDto.builder()
            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .message("Internal server error")
            .build();

    return new ResponseEntity<>(errorDto, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorDto> handleValidationExceptions(MethodArgumentNotValidException ex) {
    String message = ex.getBindingResult().getAllErrors().getFirst().getDefaultMessage();
    ErrorDto errorDto =
        ErrorDto.builder().status(HttpStatus.BAD_REQUEST.value()).message(message).build();
    return new ResponseEntity<>(errorDto, HttpStatus.BAD_REQUEST);
  }
}
