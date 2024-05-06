package com.latihan.food;

public class ResponseReCreateCart {
    private boolean success;
    private String message;
    private ModelCart data;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public ModelCart getData() {
        return data;
    }
}
