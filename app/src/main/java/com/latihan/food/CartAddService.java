package com.latihan.food;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface CartAddService {
    @POST("cart/cart-add/{cart}")
    Call<ResponseCartAdd> postCartAdd(@Path("cart") int  cartId, @Body RequestCartAdd requestCartAdd);
}
