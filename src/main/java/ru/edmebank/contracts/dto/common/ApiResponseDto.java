package ru.edmebank.contracts.dto.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.edmebank.clients.fw.exception.ErrorResponse;
import ru.edmebank.contracts.enums.ApiStatus;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponseDto<T> {
    private String status;
    private T data;
    private ErrorResponse error;

    public static <T> ApiResponseDto<T> success(T data) {
        ApiResponseDto<T> response = new ApiResponseDto<>();
        response.setStatus(ApiStatus.SUCCESS.name());
        response.setData(data);
        return response;
    }

    public static <T> ApiResponseDto<T> error(ErrorResponse error) {
        ApiResponseDto<T> response = new ApiResponseDto<>();
        response.setStatus(ApiStatus.ERROR.name());
        response.setError(error);
        return response;
    }
}