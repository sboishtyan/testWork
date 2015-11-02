package com.example.sboishtyan.forsportsru.ui;

import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.eccyan.optional.Optional;
import com.example.sboishtyan.forsportsru.Injector;
import com.example.sboishtyan.forsportsru.R;
import com.example.sboishtyan.forsportsru.SportsRuAppComponent;
import com.example.sboishtyan.forsportsru.api.BaseSubscriber;
import com.example.sboishtyan.forsportsru.api.RESTClient;
import com.example.sboishtyan.forsportsru.data.json.model.ImageLinkResponse;
import com.example.sboishtyan.forsportsru.data.preferences.StringPreference;
import com.example.sboishtyan.forsportsru.data.qualifiers.TeamId;
import com.example.sboishtyan.forsportsru.data.qualifiers.TeamTitle;
import com.example.sboishtyan.forsportsru.data.xml.Channel;
import com.example.sboishtyan.forsportsru.databinding.RssItemBinding;
import com.example.sboishtyan.forsportsru.util.AndroidUtils;
import com.example.sboishtyan.forsportsru.util.Constants;
import com.example.sboishtyan.forsportsru.util.FileUtils;
import com.squareup.okhttp.ResponseBody;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Response;
import rx.Subscription;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

public class NewsActivity extends AppCompatActivity {

    private static final int ALPHA = 127;
    private SportsRuAppComponent appGraph;

    @Inject            RESTClient       restClient;
    @Inject @TeamId    StringPreference teamIdpPreference;
    @Inject @TeamTitle StringPreference teamTitlePreference;

    @Bind(R.id.list)         RecyclerView   newsList;
    @Bind(R.id.contentPanel) RelativeLayout layout;
    RecyclerView.Adapter adapter;
    WebView              webView;
    List<Channel.Item>   rssItems = new ArrayList<>();

    CompositeSubscription compositeSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        appGraph = (SportsRuAppComponent) Injector.obtain(getApplication());
        appGraph.inject(this);
        setContentView(R.layout.list);
        ButterKnife.bind(this);

        adapter = new NewsListAdapter();
        newsList.setLayoutManager(new LinearLayoutManager(this));
        newsList.setAdapter(adapter);

        createWebView();
        compositeSubscription = new CompositeSubscription();
        ifAbsentDownloadBackground();
        downloadRssNews();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (compositeSubscription.isUnsubscribed())
            compositeSubscription.unsubscribe();
    }

    /**
     * Стандартное кеширование не получилось настроить.
     * Даже подменил header Cache-Control, который приходил =0
     * {@link com.example.sboishtyan.forsportsru.ApiModule}
     */
    private void createWebView() {
        webView = new WebView(this);
        webView.getSettings()
               .setAppCachePath(getApplicationContext().getCacheDir().getAbsolutePath());
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webView.getSettings().setAllowUniversalAccessFromFileURLs(true);
    }

    private void downloadRssNews() {
        final Subscription subscription = restClient
                .getRss(teamIdpPreference.get())
                .subscribe(new BaseSubscriber<Channel>() {
                    @Override
                    public void onNext(Channel rss) {
                        rssItems.addAll(rss.getItems());
                        adapter.notifyDataSetChanged();
                    }
                });
        compositeSubscription.add(subscription);
    }

    private void ifAbsentDownloadBackground() {
        Optional<String> backgroundFilePathIfPresent = FileUtils.getBackgroundFilePathIfPresent();
        backgroundFilePathIfPresent.ifPresent(setImageBackGround());
        if (!backgroundFilePathIfPresent.isPresent()) searchTeamLogo();
    }

    private void searchTeamLogo() {
        String screenSizeCategory = AndroidUtils.getScreenSizeCategory(getResources()
                .getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK);
        final Subscription subscription = restClient
                .searchTeamLogo(teamTitlePreference.get(), screenSizeCategory)
                .subscribe(new BaseSubscriber<ImageLinkResponse>() {
                    @Override
                    public void onNext(ImageLinkResponse imageLinkResponse) {
                        imageLinkResponse.getImageUrl().ifPresent(getLogo());
                    }
                });
        compositeSubscription.add(subscription);
    }

    private Action1<? super String> getLogo() {
        return new Action1<String>() {
            @Override
            public void call(String url) {
                restClient.getFile(url).subscribe(new BaseSubscriber<Response<ResponseBody>>() {
                    @Override
                    public void onNext(Response<ResponseBody> response) {
                        try {
                            FileUtils.saveFileAndGetPathForBackgroundImage(response.body().bytes())
                                     .subscribe(getBackGroundSubscriber());
                        } catch (IOException e) {
                            Toast.makeText(NewsActivity.this, R.string.error_save_background_image, Toast.LENGTH_SHORT)
                                 .show();
                        }
                    }
                });

            }
        };
    }

    @NonNull
    private BaseSubscriber<Optional<String>> getBackGroundSubscriber() {
        return new BaseSubscriber<Optional<String>>() {
            @Override
            public void onNext(Optional<String> stringOptional) {
                stringOptional.ifPresent(setImageBackGround());
            }
        };
    }

    private Action1<? super String> setImageBackGround() {
        return new Action1<String>() {
            @Override
            public void call(String filePath) {
                Drawable backGround = Drawable.createFromPath(filePath);
                backGround.setAlpha(ALPHA);
                layout.setBackground(backGround);
                layout.invalidate();
            }
        };
    }

    class NewsListAdapter extends RecyclerView.Adapter<ViewHolder> {

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            RssItemBinding binding = RssItemBinding
                    .inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new ViewHolder(binding.getRoot());
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            RssItemBinding binding = DataBindingUtil.getBinding(holder.itemView);
            binding.setRssItem(rssItems.get(position));
        }

        @Override
        public int getItemCount() {
            return rssItems.size();
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @Bind(R.id.title) TextView title;
        @Bind(R.id.time) TextView time;


        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
//            webView.loadUrl(rssItems.get(getAdapterPosition()).getFullLink()); Так не кешируется

            final Channel.Item item = rssItems.get(getAdapterPosition());
            final String url = item.getFullLink();
            restClient.getAndSaveHtmlPage(url).subscribe(new BaseSubscriber<String>(){
                @Override
                public void onNext(String content) {
                    if (content != null){
                        CookieManager.getInstance().setAcceptCookie(true);
                        final String data = ""+ content + "";
                        // Проблемы, что на эмуляторе не работает localhost - Origin 'http://localhost' is therefore not allowed access.
                        webView.loadDataWithBaseURL("http://localhost/", data, Constants.MIME_TYPE_HTML, Constants.UTF8, null);
                        return;
                    }
                    // Если при скачивании и сохранении что то пошло не так, откроем без сохранения
                    webView.loadUrl(url);
                }
            });

        }
    }
}
