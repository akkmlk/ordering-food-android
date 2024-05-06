package com.latihan.food;

import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ReCreateCartService {
    @POST("recreate/{user}")
    Call<ResponseReCreateCart> postReCreate(@Path("user") int userId);
}
