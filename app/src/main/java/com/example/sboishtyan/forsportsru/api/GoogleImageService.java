package com.example.sboishtyan.forsportsru.api;

import com.example.sboishtyan.forsportsru.data.json.model.ImageLinkResponse;
import com.example.sboishtyan.forsportsru.util.Constants;

import retrofit.http.GET;
import retrofit.http.Query;
import rx.Observable;

public interface GoogleImageService {

    @GET("/ajax/services/search/images")
    Observable<ImageLinkResponse> searchTeamLogo(
            @Query(value = Constants.API.QUERY) String query,
            @Query(value = Constants.API.PROTOCOL_VERSION) String version,
            @Query(value = Constants.API.IMAGE_TYPE) String type,
            @Query(value = Constants.API.IMAGE_SIZE) String size);
}
