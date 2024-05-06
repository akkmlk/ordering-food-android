package com.latihan.food;

public class ResponseCartAdd {
    private boolean success;
    private String message;
    private ModelCart data;
    private ModelCartDetail detail;
    private ModelMenu menu;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public ModelCart getData() {
        return data;
    }

    public ModelCartDetail getDetail() {
        return detail;
    }

    public ModelMenu getMenu() {
        return menu;
    }
}
