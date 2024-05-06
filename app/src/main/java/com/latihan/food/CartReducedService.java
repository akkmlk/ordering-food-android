package com.latihan.food;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface CartReducedService {
    @POST("cart/cart-reduced/{cart}")
    Call<ResponseCartReduced> postCartReduced(@Path("cart") int cartId, @Body RequestCartReduced requestCartReduced);
}
