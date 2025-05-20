package com.bugsquashers.backend.util.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;

@Getter
@RequiredArgsConstructor
public class ApiResponse<T> {
    private final Boolean isSuccess;
    private final String code;
    private final String message;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final T payload;

    public static <T> ResponseEntity<ApiResponse<T>> onSuccess(SuccessStatus code, T payload) {
        ApiResponse<T> response = new ApiResponse<>(true, code.getCode(), code.getMessage(), payload);
        return ResponseEntity.status(code.getHttpStatus()).body(response);
    }

    public static <T> ResponseEntity<ApiResponse<T>> onSuccess(SuccessStatus code) {
        ApiResponse<T> response = new ApiResponse<>(true, code.getCode(), code.getMessage(), null);
        return ResponseEntity.status(code.getHttpStatus()).body(response);
    }

    public static <T> ResponseEntity<ApiResponse<T>> onError(ErrorStatus code, T payload) {
        ApiResponse<T> response = new ApiResponse<>(false, code.getCode(), code.getMessage(), payload);
        return ResponseEntity.status(code.getHttpStatus()).body(response);
    }

    public static <T> ResponseEntity<ApiResponse<T>> onError(ErrorStatus code) {
        ApiResponse<T> response = new ApiResponse<>(false, code.getCode(), code.getMessage(), null);
        return ResponseEntity.status(code.getHttpStatus()).body(response);
    }

    public static <T> ResponseEntity<Object> onErrorForOverride(ErrorStatus code, T payload) {
        ApiResponse<T> response = new ApiResponse<>(false, code.getCode(), code.getMessage(), payload);
        return ResponseEntity.status(code.getHttpStatus()).body(response);
    }
}
