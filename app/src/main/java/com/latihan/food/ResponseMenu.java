package com.latihan.food;

import java.util.List;

public class ResponseMenu {
    private boolean succsess;
    private List<ModelMenu> data;

    public boolean isSuccsess() {
        return succsess;
    }

    public List<ModelMenu> getData() {
        return data;
    }
}
