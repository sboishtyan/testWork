package com.example.sboishtyan.forsportsru.api;

import com.squareup.okhttp.ResponseBody;

import retrofit.Response;
import retrofit.http.GET;
import retrofit.http.Url;
import rx.Observable;

public interface FileService {
    @GET
    Observable<Response<ResponseBody>> getFile(@Url String fileUrl);
}
