package com.latihan.food;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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

public class InvoiceActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    Button btnSave, btnShare, btnFinish;
    TextView btnBack, tvPriceTotal;
    LocalStorage localStorage;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice);

        btnBack = findViewById(R.id.btnBack);
        btnSave = findViewById(R.id.btnSave);
        btnShare = findViewById(R.id.btnShare);
        btnFinish = findViewById(R.id.btnFinish);
        tvPriceTotal = findViewById(R.id.tvPriceTotal);

        localStorage = new LocalStorage(InvoiceActivity.this);
        String token = localStorage.getToken();
        int cartId = localStorage.getCart_id();
        int userId = localStorage.getUser_id();

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(InvoiceActivity.this, ContainerActivity.class));
            }
        });

        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reCreateCart(userId, token);
                View rootView = getWindow().getDecorView().getRootView();
                Bitmap bitmap = getScreenshotLayout(rootView);
                saveBitmapToStorage(bitmap);
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("application/jpg");
                Uri uri = getImageUri(InvoiceActivity.this, bitmap);
                intent.putExtra(Intent.EXTRA_STREAM, uri);
                startActivity(Intent.createChooser(intent, "Bagikan Via"));
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View rootView =  getWindow().getDecorView().getRootView();
                Bitmap bitmap = getScreenshotLayout(rootView);
                saveBitmapToStorage(bitmap);
                reCreateCart(userId, token);
                startActivity(new Intent(InvoiceActivity.this, ContainerActivity.class));
            }
        });

        btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reCreateCart(userId, token);
                startActivity(new Intent(InvoiceActivity.this, ContainerActivity.class));
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

        InvoiceService invoiceService = retrofit.create(InvoiceService.class);
        Call<ResponseInvoice> call = invoiceService.getInvoice(cartId);
        call.enqueue(new Callback<ResponseInvoice>() {
            @Override
            public void onResponse(Call<ResponseInvoice> call, retrofit2.Response<ResponseInvoice> response) {
                if (response.isSuccessful()) {
                    ModelCart modelCart = response.body().getData();
                    tvPriceTotal.setText(String.valueOf(modelCart.getPrice_total()));

                    List<ModelCartDetail> cartDetailList = response.body().getDetail();
                    List<ModelMenu> menuList = response.body().getMenu();
                    AdapterMenuDibeli adapterMenuDibeli = new AdapterMenuDibeli(InvoiceActivity.this, cartDetailList, menuList);
                    recyclerView = findViewById(R.id.rvBuyMenu);
                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(InvoiceActivity.this);
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.setAdapter(adapterMenuDibeli);
                } else {
                    try {
                        Toast.makeText(InvoiceActivity.this, "Error response nya : " + response.errorBody().string(), Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseInvoice> call, Throwable throwable) {
                Toast.makeText(InvoiceActivity.this, "Throwable : " + throwable.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private Uri getImageUri(Context context, Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "Invoice", null);
        return Uri.parse(path);
    }

    private void saveBitmapToStorage(Bitmap bitmap) {
        File directory = InvoiceActivity.this.getFilesDir();
        File file = new File(directory, "screenshot.jpg");

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            fileOutputStream.close();
            Toast.makeText(this, "diaimpan di : " + file.getAbsoluteFile(), Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Gagal disimpan", Toast.LENGTH_SHORT).show();
        }
    }

    private Bitmap getScreenshotLayout(View rootView) {
        rootView.setDrawingCacheEnabled(true);
        Bitmap screenshot = Bitmap.createBitmap(rootView.getDrawingCache());
        rootView.setDrawingCacheEnabled(false);
        return screenshot;
    }

    private void reCreateCart(int userId, String token) {
        localStorage = new LocalStorage(InvoiceActivity.this);
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

        ReCreateCartService reCreateCartService = retrofit.create(ReCreateCartService.class);
        Call<ResponseReCreateCart> call = reCreateCartService.postReCreate(userId);
        call.enqueue(new Callback<ResponseReCreateCart>() {
            @Override
            public void onResponse(Call<ResponseReCreateCart> call, retrofit2.Response<ResponseReCreateCart> response) {
                if (response.isSuccessful()) {
                    ModelCart modelCart = response.body().getData();
                    localStorage.setCart_id(modelCart.getId());
                    Toast.makeText(InvoiceActivity.this, "ini id cart baru nya : " + localStorage.getCart_id(), Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        Toast.makeText(InvoiceActivity.this, "Error response nya : " + response.errorBody().string(), Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseReCreateCart> call, Throwable throwable) {
                Toast.makeText(InvoiceActivity.this, "Throwable : " + throwable.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}