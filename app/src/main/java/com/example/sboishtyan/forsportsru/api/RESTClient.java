package com.example.sboishtyan.forsportsru.api;

import android.support.annotation.NonNull;
import android.util.Log;

import com.example.sboishtyan.forsportsru.data.json.model.ImageLinkResponse;
import com.example.sboishtyan.forsportsru.data.realm.PersistentHtmlPage;
import com.example.sboishtyan.forsportsru.data.realm.PersistentHtmlPageContract;
import com.example.sboishtyan.forsportsru.data.xml.Channel;
import com.example.sboishtyan.forsportsru.util.Constants;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.ResponseBody;

import java.io.IOException;

import io.realm.Realm;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;
import retrofit.SimpleXmlConverterFactory;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class RESTClient {

    private static final String SEARCH_TEMPLATE = "Футбольная команда %s логотип";

    private GoogleImageService googleImageService;
    private FileService        fileService;
    private RssService         rssService;

    public RESTClient(OkHttpClient okHttpClient) {
        init(okHttpClient);
    }

    private void init(OkHttpClient okHttpClient) {

        Retrofit retrofit = retrofitBuilder(okHttpClient, Constants.GOOGLE_ENDPOINT)
                .addConverterFactory(GsonConverterFactory.create()).build();
        googleImageService = retrofit.create(GoogleImageService.class);
        fileService = retrofit.create(FileService.class);

        retrofit = retrofitBuilder(okHttpClient, Constants.SPORTS_RU_ENDPOINT)
                .addConverterFactory(SimpleXmlConverterFactory.create()).build();
        rssService = retrofit.create(RssService.class);
    }

    @NonNull
    private Retrofit.Builder retrofitBuilder(OkHttpClient okHttpClient, String endPoint) {
        return new Retrofit.Builder().baseUrl(endPoint)
                                     .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                                     .validateEagerly().client(okHttpClient);
    }



    public Observable<ImageLinkResponse> searchTeamLogo(@NonNull String imageQuery, @NonNull String imageSize) {
        String query = String.format(SEARCH_TEMPLATE, imageQuery);
        Log.d(RESTClient.class.getSimpleName(), "searching Team Logo: query -> " + query);
        return defaultIOCustomize(googleImageService.searchTeamLogo(query, "1.0", "png", imageSize));
    }

    public Observable<Response<ResponseBody>> getFile(@NonNull String fileUrl) {
        return defaultIOCustomize(fileService.getFile(fileUrl));
    }

    /**
     * @param url of html page
     * @return content of htmlpage
     */
    public Observable<String> getAndSaveHtmlPage(@NonNull final String url) {
        try(Realm realm = Realm.getDefaultInstance()){
            final PersistentHtmlPage htmlPage = findHtmlPageByUrl(realm, url);

            if (htmlPage != null)
                return Observable.from(new String[]{htmlPage.getContent()});
        }

        return fileService.getFile(url).compose(new Observable.Transformer<Response<ResponseBody>, String>() {
            @Override
            public Observable<String> call(Observable<Response<ResponseBody>> responseObservable) {
                return responseObservable.map(new Func1<Response<ResponseBody>, String>() {
                    @Override
                    public String call(Response<ResponseBody> responseBodyResponse) {
                        try (Realm realm = Realm.getDefaultInstance()) {
                            PersistentHtmlPage htmlPage = realm.where(PersistentHtmlPage.class)
                                                               .equalTo(PersistentHtmlPageContract.URL, url)
                                                               .findFirst();
                            if (htmlPage == null) {
                                realm.beginTransaction();
                                htmlPage = realm.createObject(PersistentHtmlPage.class);
                                htmlPage.setUrl(url);
                                try {
                                    htmlPage.setContent(responseBodyResponse.body().string());
                                    realm.commitTransaction();
                                } catch (IOException e) {
                                    realm.cancelTransaction();
                                    return null;
                                }
                            }
                            return htmlPage.getContent();
                        }
                    }
                });
            }
        }).doOnError(new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                throwable.printStackTrace();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }


    public Observable<Channel> getRss(@NonNull String teamId) {
        return defaultIOCustomize(rssService.getRss(teamId));
    }


    private <T> Observable<T> defaultIOCustomize(Observable<T> observable) {
        return observable.retry(3)
                         .subscribeOn(Schedulers.io())
                         .observeOn(AndroidSchedulers.mainThread());
    }

    private PersistentHtmlPage findHtmlPageByUrl(Realm realm, String url){
        return realm.where(PersistentHtmlPage.class)
                                           .equalTo(PersistentHtmlPageContract.URL, url)
                                           .findFirst();
    }
}
