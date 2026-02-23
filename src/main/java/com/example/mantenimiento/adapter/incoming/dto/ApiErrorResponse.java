package com.example.mantenimiento.adapter.incoming.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "ApiErrorResponse", description = "Estructura estándar para respuestas de error")
public class ApiErrorResponse {

    @Schema(example = "2026-02-18T18:25:43.511Z")
    private String timestamp;

    @Schema(example = "401")
    private int status;

    @Schema(example = "Unauthorized")
    private String error;

    @Schema(example = "Credenciales inválidas")
    private String message;

    @Schema(example = "/login")
    private String path;

    public ApiErrorResponse() {
    }

    public ApiErrorResponse(String timestamp, int status, String error, String message, String path) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
