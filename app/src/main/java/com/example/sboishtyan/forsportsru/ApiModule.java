package com.example.sboishtyan.forsportsru;

import com.example.sboishtyan.forsportsru.api.RESTClient;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.logging.HttpLoggingInterceptor;

import java.io.IOException;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class ApiModule {
    
    @Provides @Singleton
    RESTClient provideRestClient(OkHttpClient okHttpClient) {
        return new RESTClient(okHttpClient);
    }

    @Provides @Singleton
    OkHttpClient provideOkHttpClient(Interceptor interceptor){
        OkHttpClient okHttpClient = new OkHttpClient();
        HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor();
        logInterceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS);
        okHttpClient.interceptors().add(logInterceptor);
        okHttpClient.interceptors().add(interceptor);

        return okHttpClient;
    }

    @Provides @Singleton
    Interceptor provideResponseInterceptor(){
        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                return chain.proceed(request)
                            .newBuilder()
                            .header("Cache-Control", "max-age=60")
                            .build();
            }
        };
    }
}
