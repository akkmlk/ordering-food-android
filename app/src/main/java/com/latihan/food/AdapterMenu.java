package com.latihan.food;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AdapterMenu extends RecyclerView.Adapter<AdapterMenu.HolderMenu> {

    Context context;

    LocalStorage localStorage;
    List<ModelMenu> menuList;
    List<ModelMenu> selectedMenus;

    public AdapterMenu(Context context, List<ModelMenu> menuList) {
        this.context = context;
        this.menuList = menuList;
    }

    public interface OnPriceTotalListener {
        void onUpdate(double priceTotal);
    }

    private OnPriceTotalListener onPriceTotalListener;

    public void setOnPriceTotalListener(OnPriceTotalListener listener) {
        this.onPriceTotalListener = listener;
    }

    @NonNull
    @Override
    public AdapterMenu.HolderMenu onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item, parent, false);
        return new HolderMenu(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterMenu.HolderMenu holder, @SuppressLint("RecyclerView") int position) {
        holder.tvName.setText(menuList.get(position).getName());
        holder.tvPrice.setText(String.valueOf(menuList.get(position).getPrice()));

        holder.btnPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                localStorage = new LocalStorage(context);
                String token = localStorage.getToken();
                int cartId = localStorage.getCart_id();
                int menuId = menuList.get(position).getId();
                int qty = 1;

                RequestCartAdd requestCartAdd = new RequestCartAdd();
                requestCartAdd.setMenu_id(menuId);
                requestCartAdd.setQty(qty);

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
                        .baseUrl(context.getString(R.string.url))
                        .addConverterFactory(GsonConverterFactory.create())
                        .client(client)
                        .build();

                CartAddService cartAddService = retrofit.create(CartAddService.class);
                Call<ResponseCartAdd> call = cartAddService.postCartAdd(cartId, requestCartAdd);
                call.enqueue(new Callback<ResponseCartAdd>() {
                    @Override
                    public void onResponse(Call<ResponseCartAdd> call, retrofit2.Response<ResponseCartAdd> response) {
                        if (response.isSuccessful()) {
                            ModelCartDetail modelCartDetail = response.body().getDetail();
                            ModelMenu modelMenu = response.body().getMenu();

                            holder.tvQty.setText(String.valueOf(modelCartDetail.getQty()));

                            if (selectedMenus == null) {
                                selectedMenus = new ArrayList<>();
                            }
//                            selectedMenus.add(modelCartDetail);
                            selectedMenus.add(modelMenu);

                            double priceTotal = calculatePriceTotal(selectedMenus);
                            onPriceTotalListener.onUpdate(priceTotal);
                        } else {
                            try {
                                errorMsg("error response nya : " + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseCartAdd> call, Throwable throwable) {
                        Toast.makeText(context, "Throwable : " + throwable.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        errorMsg(String.valueOf(selectedMenus));

        holder.btnMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                localStorage = new LocalStorage(context);
                String token = localStorage.getToken();
                int cartId = localStorage.getCart_id();
                int menuId = menuList.get(position).getId();
                int qty = 1;

                RequestCartReduced requestCartReduced = new RequestCartReduced();
                requestCartReduced.setMenu_id(menuId);
                requestCartReduced.setQty(qty);

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
                        .baseUrl(context.getString(R.string.url))
                        .addConverterFactory(GsonConverterFactory.create())
                        .client(client)
                        .build();

                CartReducedService cartReducedService = retrofit.create(CartReducedService.class);
                Call<ResponseCartReduced> call = cartReducedService.postCartReduced(cartId, requestCartReduced);
                call.enqueue(new Callback<ResponseCartReduced>() {
                    @Override
                    public void onResponse(Call<ResponseCartReduced> call, retrofit2.Response<ResponseCartReduced> response) {
                        if (response.isSuccessful()) {
                            ModelCartDetail modelCartDetail = response.body().getDetail();
                            ModelMenu modelMenu = response.body().getMenu();

                            holder.tvQty.setText(String.valueOf(modelCartDetail.getQty()));

                            ModelMenu menuToRemove = null;
                            for (ModelMenu menu : selectedMenus) {
                                if (menu.getId() == menuId) {
                                    menuToRemove = menu;
                                    break;
                                }
                            }
                            if (menuToRemove != null) {
                                selectedMenus.remove(menuToRemove);
                            }
                            errorMsg(String.valueOf(selectedMenus));

                            double priceTotal = calculatePriceTotal(selectedMenus);
                            onPriceTotalListener.onUpdate(priceTotal);
                        } else {
                            try {
                                errorMsg("Error response nya : " + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseCartReduced> call, Throwable throwable) {
                        errorMsg("Throwable : " + throwable.getLocalizedMessage());
                    }
                });
            }
        });
    }

    private double calculatePriceTotal(List<ModelMenu> selectedMenus) {
        double priceTotal = 0;
        for (ModelMenu menu: selectedMenus) {
            priceTotal += menu.getPrice();
        }
        return priceTotal;
    }

    private void spSubtotalList(Context context, List<String> subtotalList) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("subtotal", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String subtotalListJson = gson.toJson(subtotalList);
        editor.putString("subtotal", subtotalListJson);
        editor.apply();
    }

    private void errorMsg(String msg) {
        AlertDialog.Builder  alertBuild = new AlertDialog.Builder(context)
                .setTitle("Oops")
                .setMessage(msg)
                .setPositiveButton("Oke", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

        AlertDialog alert = alertBuild.create();
        alert.show();
    }

    @Override
    public int getItemCount() {
        return menuList != null ? menuList.size() : 0;
    }

    public class HolderMenu extends RecyclerView.ViewHolder {
        TextView tvName, tvPrice, tvQty;
        ImageView btnMinus, btnPlus;
        public HolderMenu(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tvName);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvQty = itemView.findViewById(R.id.tvQty);
            btnMinus = itemView.findViewById(R.id.btnMinus);
            btnPlus = itemView.findViewById(R.id.btnPlus);
        }
    }
}
