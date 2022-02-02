package com.example.applicationsignup;

import com.google.gson.JsonObject;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiInterface {

    @POST("mobile_login_signup")
    Call<JsonObject> login(@Body Signup signup);
    @Multipart
    @POST("upload")
    Call<JsonObject> upload(@Part MultipartBody.Part image,
                            @Part("category") RequestBody category);
}
