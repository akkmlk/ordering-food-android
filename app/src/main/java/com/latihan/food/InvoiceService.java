package com.latihan.food;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface InvoiceService {
    @GET("cart/invoice/{cart}")
    Call<ResponseInvoice> getInvoice(@Path("cart") int cartId);
}
