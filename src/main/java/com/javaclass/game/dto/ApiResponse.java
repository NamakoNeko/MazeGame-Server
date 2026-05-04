package com.javaclass.game.dto;

import lombok.Data;

@Data
public class ApiResponse<T> {
    public int code;
    public String message;
    public T data;

    public ApiResponse(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(200, "success", data);
    }
}