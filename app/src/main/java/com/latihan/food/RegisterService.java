package com.latihan.food;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RegisterService {
    @POST("register")
    Call<ResponseRegister> postRegister(@Body RequestRegister requestRegister);
}
