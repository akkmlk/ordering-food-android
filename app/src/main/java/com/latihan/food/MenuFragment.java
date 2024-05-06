package com.latihan.food;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MenuFragment extends Fragment implements AdapterMenu.OnPriceTotalListener {

    LocalStorage localStorage;
    RecyclerView recyclerView;
    Button btnBuy;
    SearchView svSearch;
    List<ModelMenu> menuList;
    TextView tvPriceTotal;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_menu, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvPriceTotal = view.findViewById(R.id.priceTotal);
        btnBuy = view.findViewById(R.id.btnBuy);

        localStorage = new LocalStorage(getActivity());
        String token = localStorage.getToken();
        int cartId =  localStorage.getCart_id();
//        Integer userId = localStorage.getCart_id();
//        Toast.makeText(getActivity(), "user id : " + userId, Toast.LENGTH_SHORT).show();

        btnBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Double priceTotal = Double.parseDouble(tvPriceTotal.getText().toString().trim());
                RequestCartSubmit requestCartSubmit = new RequestCartSubmit();
                requestCartSubmit.setPrice_total(priceTotal);

                OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
                    @NonNull
                    @Override
                    public Response intercept(@NonNull Chain chain) throws IOException {
                        Request newRequest = chain.request().newBuilder()
                                .addHeader("Authorization", "Bearer " + token)
                                .build();
                        return chain.proceed(newRequest);
                    }
                }).build();

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(getString(R.string.url))
                        .addConverterFactory(GsonConverterFactory.create())
                        .client(client)
                        .build();

                CartSubmitService cartSubmitService = retrofit.create(CartSubmitService.class);
                Call<ResponseCartSubmit> call = cartSubmitService.submitCart(cartId, requestCartSubmit);
                call.enqueue(new Callback<ResponseCartSubmit>() {
                    @Override
                    public void onResponse(Call<ResponseCartSubmit> call, retrofit2.Response<ResponseCartSubmit> response) {
                        if (response.isSuccessful()) {
                            tvPriceTotal.setText("0");
                            Toast.makeText(getActivity(), "Oke finish dengan harga Rp. " + priceTotal , Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getActivity(), InvoiceActivity.class));
                        } else {
                            try {
                                Toast.makeText(getActivity(), "Error response nya : " + response.errorBody().string(), Toast.LENGTH_SHORT).show();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseCartSubmit> call, Throwable throwable) {

                    }
                });
            }
        });

        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
            @NonNull
            @Override
            public Response intercept(@NonNull Chain chain) throws IOException {
                Request newRequest = chain.request().newBuilder()
                        .addHeader("Authorization", "Bearer " + token)
                        .build();
                return chain.proceed(newRequest);
            }
        }).build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.url))
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        MenuFragment menuFragment = this;

        MenuService menuService = retrofit.create(MenuService.class);
        Call<ResponseMenu> call = menuService.getMenu();
        call.enqueue(new Callback<ResponseMenu>() {
            @Override
            public void onResponse(Call<ResponseMenu> call, retrofit2.Response<ResponseMenu> response) {
                if (response.isSuccessful()) {
                    menuList = response.body().getData();

                    AdapterMenu adapterMenu = new AdapterMenu(getActivity(), menuList);
                    adapterMenu.setOnPriceTotalListener(menuFragment);
                    recyclerView = view.findViewById(R.id.rvMenu);
                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.setAdapter(adapterMenu);
                } else {
                    try {
                        Toast.makeText(getActivity(), "Error response nya : " + response.errorBody().string(), Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseMenu> call, Throwable throwable) {
                Toast.makeText(getActivity(), "Throwable : " + throwable.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onUpdate(double priceTotal) {
        tvPriceTotal.setText(String.valueOf(priceTotal));
    }
}