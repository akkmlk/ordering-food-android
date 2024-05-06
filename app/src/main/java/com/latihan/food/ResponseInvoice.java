package com.latihan.food;

import java.util.List;

public class ResponseInvoice {
    private boolean success;
    private String message;
    private ModelCart data;
    private List<ModelCartDetail> detail;
    private List<ModelMenu> menu;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public ModelCart getData() {
        return data;
    }

    public List<ModelCartDetail> getDetail() {
        return detail;
    }

    public List<ModelMenu> getMenu() {
        return menu;
    }
}
