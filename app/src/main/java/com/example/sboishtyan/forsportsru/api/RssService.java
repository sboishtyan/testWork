package com.example.sboishtyan.forsportsru.api;

import com.example.sboishtyan.forsportsru.data.xml.Channel;
import com.example.sboishtyan.forsportsru.util.Constants;

import retrofit.http.GET;
import retrofit.http.Query;
import rx.Observable;

public interface RssService {

    @GET("/stat/export/rss/taglenta.xml")
    Observable<Channel> getRss(
            @Query(value = Constants.API.ID) String teamId);
}
