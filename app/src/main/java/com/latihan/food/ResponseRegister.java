package com.latihan.food;

public class ResponseRegister {
    private boolean  success;
    private String message;
    private ModelUser data;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public ModelUser getData() {
        return data;
    }
}
