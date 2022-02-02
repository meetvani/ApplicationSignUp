package com.example.applicationsignup;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;
import hu.akarnokd.rxjava3.retrofit.RxJava3CallAdapterFactory;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    CircleImageView circleImageView;
    Button button;
    private static final int PICK_IMAGE = 123;
    Uri imageUri;
    File file;
    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        circleImageView = findViewById(R.id.register_image);
        button = findViewById(R.id.user_image_next);
        token = getIntent().getStringExtra("token");
        circleImageView.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select the Image From gallery"), PICK_IMAGE);
        });
        button.setOnClickListener(view -> {
            if (imageUri==null){
                Toast.makeText(MainActivity.this, "Please Select Image", Toast.LENGTH_SHORT).show();
            }
            OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request newRequest = chain.request().newBuilder()
                            .addHeader("x-auth-token", token)
                            .build();
                    return chain.proceed(newRequest);
                }
            }).build();
            RequestBody categoryBody = RequestBody.create(MediaType.parse("multipart/form-data"), "profile");
            MultipartBody.Part requestImage = null;
            if (file == null) {
                file = new File(String.valueOf(imageUri));
            }
            RequestBody imageBody = RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(imageUri));
            requestImage = MultipartBody.Part.createFormData("image", file.getName(), imageBody);
            ApiInterface apiInterface = new Retrofit.Builder()
                    .baseUrl("https://fileservice.celebrityschool.in/v1/file/").client(client)
                    .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                    .addConverterFactory(new NullOnEmptyConverterFactory())
                    .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().setLenient().create()))
                    .build().create(ApiInterface.class);
            Call<JsonObject> call = apiInterface.upload(requestImage, categoryBody);
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(MainActivity.this, "Image Uploaded Successfully", Toast.LENGTH_LONG).show();
                    } else {
                        System.out.println("responselogin = " + response.code());
                        Toast.makeText(MainActivity.this, "hi"+response.message(), Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    System.out.println("t login = " + t.getMessage());
                    Toast.makeText(MainActivity.this,"failure "+ t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            imageUri = Uri.parse(data.getDataString());
            System.out.println("imageUri = " + imageUri);
            circleImageView.setImageURI(imageUri);


        }


    }
}