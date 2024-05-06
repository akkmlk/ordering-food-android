package com.latihan.food;

public class ResponseLogin {
    private boolean success;
    private String message;
    private String token;
    private ModelUser data;
    private ModelCart cart;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public String getToken() {
        return token;
    }

    public ModelUser getData() {
        return data;
    }

    public ModelCart getCart() {
        return cart;
    }
}
