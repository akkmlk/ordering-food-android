package com.latihan.food;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface LoginService {
    @POST("login")
    Call<ResponseLogin> postLogin(@Body RequestLogin requestLogin);
}
