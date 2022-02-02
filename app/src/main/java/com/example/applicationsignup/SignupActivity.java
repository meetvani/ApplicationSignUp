package com.example.applicationsignup;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import hu.akarnokd.rxjava3.retrofit.RxJava3CallAdapterFactory;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SignupActivity extends AppCompatActivity {
    TextInputEditText name_ed, email_ed, mobile_ed, password_ed;
    String name, email, mobile, password;
    Button signup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        name_ed = findViewById(R.id.login_name);
        email_ed = findViewById(R.id.login_email);
        mobile_ed = findViewById(R.id.login_phome);
        password_ed = findViewById(R.id.login_password);
        signup = findViewById(R.id.login_loginbt);
        signup.setOnClickListener(view -> {
            name = name_ed.getText().toString();
            email = email_ed.getText().toString();
            mobile = mobile_ed.getText().toString();
            password = password_ed.getText().toString();
            if (name.isEmpty()) {
                name_ed.setError("Please Enter Name");
                name_ed.requestFocus();
            } else if (email.isEmpty()) {
                email_ed.setError("Please Enter Email Address");
                email_ed.requestFocus();
            } else if (!email.contains("@")) {
                email_ed.setError("Please Enter Valid Email Address");
                email_ed.requestFocus();
            } else if (mobile.length() != 10) {
                mobile_ed.setError("Please Enter Valid Mobile Number");
                mobile_ed.requestFocus();
            } else if (password.isEmpty()) {
                password_ed.setError("Please Enter Password");
                password_ed.requestFocus();
            } else {
                Signup signup = new Signup();
                signup.setName(name);
                signup.setEmail(email);
                signup.setMobile(mobile);
                signup.setPassword(password);
                signup.setProfilepic("");
                signup.setSocial_id("");
                signup.setSocial_platform("manual");
                signup.setLogin_type("signup");
                signup.setDeviceType("android");
                signUp(signup);
            }

        });
    }

    private void signUp(Signup signup) {
        ApiInterface apiInterface = new Retrofit.Builder()
                .baseUrl("https://www.origin.celebrityschool.in:1337/api/v1/user/")
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .addConverterFactory(new NullOnEmptyConverterFactory())
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().setLenient().create()))
                .build().create(ApiInterface.class);
        Call<JsonObject> call= apiInterface.login(signup);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject apiResponse = response.body();
                    JsonObject meta=apiResponse.getAsJsonObject("meta");
                    if (meta.get("flag").getAsString().matches("SUCCESS")){
                        JsonObject data=apiResponse.getAsJsonObject("data");
                        String token=data.get("token").getAsString();
                        Intent intent = new Intent(SignupActivity.this,MainActivity.class);
                        intent.putExtra("token",token);
                        startActivity(intent);
                    }else {
                        Toast.makeText(SignupActivity.this, meta.get("message").getAsString(), Toast.LENGTH_LONG).show();
                    }


                } else {
                    System.out.println("responselogin = " + response.code());
                    Toast.makeText(SignupActivity.this, "hi"+response.message(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                System.out.println("t login = " + t.getMessage());
                Toast.makeText(SignupActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}