package com.latihan.food;

import android.content.Context;
import android.content.SharedPreferences;

public class LocalStorage {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Context context;
    String token;
    Integer cart_id;
    Integer user_id;

    private static final String TOKEN_KEY = "token";
    private static final String CART_ID = "cart_id";
    private static final String USER_ID =  "user_id";

    public LocalStorage(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(TOKEN_KEY, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void clearToken(){
        editor.remove(TOKEN_KEY);
        editor.apply();
    }

    public String getToken() {
        return sharedPreferences.getString(TOKEN_KEY, null);
    }

    public void setToken(String token) {
        editor.putString(TOKEN_KEY, token);
        editor.apply();
    }

    public Integer getCart_id() {
        return sharedPreferences.getInt(CART_ID, 0);
    }

    public void setCart_id(Integer cart_id) {
        editor.putInt(CART_ID, cart_id);
        editor.apply();
    }

    public Integer getUser_id() {
        return sharedPreferences.getInt(USER_ID, 0);
    }

    public void setUser_id(Integer user_id) {
        editor.putInt(USER_ID, user_id);
        editor.apply();
    }
}
