package com.latihan.food;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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

public class RegisterActivity extends AppCompatActivity {

    EditText etName, etUsername, etAddress, etPassword, etPasswordConfirm;
    Button btnHaveAccont, btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etName = findViewById(R.id.etName);
        etUsername = findViewById(R.id.etUsername);
        etAddress = findViewById(R.id.etAddress);
        etPassword = findViewById(R.id.etPassword);
        etPasswordConfirm = findViewById(R.id.etPasswordConfirm);
        btnHaveAccont = findViewById(R.id.btnHaveAccount);
        btnRegister = findViewById(R.id.btnRegister);

        btnHaveAccont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, MainActivity.class));
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = etName.getText().toString().trim();
                String username = etUsername.getText().toString().trim();
                String address = etAddress.getText().toString().trim();
                String password = etPassword.getText().toString().trim();
                String passcordConfirm = etPasswordConfirm.getText().toString().trim();

                if (name.isEmpty() || username.isEmpty() || address.isEmpty() || password.isEmpty() || passcordConfirm.isEmpty()) {
                    failedMsg("Harap isi semua data");
                } else if (!password.equals(passcordConfirm)) {
                    failedMsg("Password dan Konfirmasi tidak cocok");
                } else {
                    RequestRegister requestRegister = new RequestRegister();
                    requestRegister.setName(name);
                    requestRegister.setUsername(username);
                    requestRegister.setAddress(address);
                    requestRegister.setPassword(password);
                    requestRegister.setPassword_confirmation(passcordConfirm);

                    HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
                    httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

                    OkHttpClient client = new OkHttpClient.Builder().addInterceptor(httpLoggingInterceptor).build();

                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(getString(R.string.url))
                            .addConverterFactory(GsonConverterFactory.create())
                            .client(client)
                            .build();

                    RegisterService registerService = retrofit.create(RegisterService.class);
                    Call<ResponseRegister> call = registerService.postRegister(requestRegister);
                    call.enqueue(new Callback<ResponseRegister>() {
                        @Override
                        public void onResponse(Call<ResponseRegister> call, Response<ResponseRegister> response) {
                            if (response.isSuccessful()) {
                                ModelUser modelUser = response.body().getData();

                                etName.setText("");
                                etUsername.setText("");
                                etAddress.setText("");
                                etPassword.setText("");
                                etPasswordConfirm.setText("");
                                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                intent.putExtra("registered", true);
                                intent.putExtra("username", modelUser.getUsername());
                                startActivity(intent);
                                Toast.makeText(RegisterActivity.this, modelUser.getName() + " Berhasil terdaftar", Toast.LENGTH_SHORT).show();
                            } else {
                                try {
                                    Toast.makeText(RegisterActivity.this, "Error response nya : " + response.errorBody().string(), Toast.LENGTH_SHORT).show();
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseRegister> call, Throwable throwable) {
                            Toast.makeText(RegisterActivity.this, "Throwable : " + throwable.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

    }

    private void failedMsg(String msg) {
        AlertDialog.Builder alertBuild = new AlertDialog.Builder(RegisterActivity.this)
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