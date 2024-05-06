package com.latihan.food;

import retrofit2.Call;
import retrofit2.http.GET;

public interface CartService {
    @GET("cart/")
    Call<ResponseCart> getCartTotalPrice();
}
