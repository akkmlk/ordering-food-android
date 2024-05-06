package com.latihan.food;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface CartSubmitService {
    @POST("cart/submit/{cart}")
    Call<ResponseCartSubmit> submitCart(@Path("cart") int cartId, @Body RequestCartSubmit requestCartSubmit);
}
