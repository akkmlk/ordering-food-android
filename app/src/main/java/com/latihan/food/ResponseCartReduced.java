package com.latihan.food;

public class ResponseCartReduced {
    private boolean success;
    private ModelCart data;
    private ModelCartDetail detail;
    private ModelMenu menu;

    public boolean isSuccess() {
        return success;
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
