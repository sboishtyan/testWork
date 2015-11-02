package com.example.sboishtyan.forsportsru.data.json.model;

import android.support.annotation.VisibleForTesting;

import com.eccyan.optional.Optional;

import java.util.ArrayList;
import java.util.List;

import rx.functions.Func1;

public class ImageLinkResponse {
    public ResponseData responseData;

    public static class ResponseData {
        public List<Result> results;
    }

    public static class Result {
        public String url;
    }

    public ImageLinkResponse()
    {}

    @VisibleForTesting
    public ImageLinkResponse(String url) {
        responseData = new ResponseData();
        responseData.results = new ArrayList<>();
        responseData.results.add(new Result());
        responseData.results.get(0).url = url;
    }


    public Optional<String> getImageUrl() {
        return Optional.ofNullable(responseData).flatMap(new Func1<ResponseData, Optional<String>>() {
            @Override
            public Optional<String> call(ResponseData responseData) {
                return Optional.ofNullable(responseData.results).flatMap(new Func1<List<Result>, Optional<String>>() {
                    @Override
                    public Optional<String> call(List<Result> results) {
                        return !results.isEmpty() ? Optional.ofNullable(results.get(0).url) : Optional.<String>empty();
                    }
                });
            }
        });
    }

    @Override
    public String toString() {
        return String.format("responseData == null is %s", responseData == null);
    }
}
