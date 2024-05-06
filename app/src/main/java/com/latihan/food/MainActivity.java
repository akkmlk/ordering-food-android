package com.latihan.food;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    EditText etUsername, etPassword;
    Button btnLogin, btnRegister;
    LocalStorage localStorage;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        localStorage = new LocalStorage(MainActivity.this);
        String token = localStorage.getToken();
        Toast.makeText(MainActivity.this, "token" +token, Toast.LENGTH_SHORT).show();

//        if (token != null) {
//            startActivity(new Intent(MainActivity.this, ContainerActivity.class));
//        }

        Intent messageIntent = getIntent();
        if (messageIntent != null && messageIntent.getBooleanExtra("registered", false)) {
            Toast.makeText(this, messageIntent.getStringExtra("username") + " Sukses terdaftar, silahkan login", Toast.LENGTH_SHORT).show();
        }

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, RegisterActivity.class));
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = etUsername.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                if (username.isEmpty() || password.isEmpty()) {
                    failedMsg("Lengkapi credentials");
                } else {
                    RequestLogin requestLogin = new RequestLogin();
                    requestLogin.setUsername(username);
                    requestLogin.setPassword(password);

                    HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
                    httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

                    OkHttpClient client = new OkHttpClient.Builder().addInterceptor(httpLoggingInterceptor).build();

                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(getString(R.string.url))
                            .addConverterFactory(GsonConverterFactory.create())
                            .client(client)
                            .build();

                    LoginService loginService = retrofit.create(LoginService.class);
                    Call<ResponseLogin> call = loginService.postLogin(requestLogin);
                    call.enqueue(new Callback<ResponseLogin>() {
                        @Override
                        public void onResponse(Call<ResponseLogin> call, Response<ResponseLogin> response) {
                            if (response.isSuccessful()) {

                                ModelCart modelCart = response.body().getCart();
                                Integer cartId = modelCart.getId();
                                Integer userId = modelCart.getUser_id();

                                String token = response.body().getToken();
                                localStorage = new LocalStorage(MainActivity.this);
                                localStorage.setToken(token);
                                localStorage.setCart_id(cartId);
                                localStorage.setUser_id(userId);
                                Toast.makeText(MainActivity.this, "ini cart id : " + localStorage.getCart_id(), Toast.LENGTH_SHORT).show();
                                Toast.makeText(MainActivity.this, "ini user id : " + localStorage.getUser_id(), Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(MainActivity.this, ContainerActivity.class));
                            } else {
                                failedMsg("Username atau password salah");
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseLogin> call, Throwable throwable) {
                            Toast.makeText(MainActivity.this, "Throwable : " + throwable.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

    }

    private void failedMsg(String msg) {
        AlertDialog.Builder alertBuild = new AlertDialog.Builder(MainActivity.this)
                .setTitle("Oops!")
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
}