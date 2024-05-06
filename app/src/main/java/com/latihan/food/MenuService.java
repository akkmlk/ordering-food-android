package com.latihan.food;

import retrofit2.Call;
import retrofit2.http.GET;

public interface MenuService {
    @GET("menu")
    Call<ResponseMenu> getMenu();
}
