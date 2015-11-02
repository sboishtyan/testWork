package com.example.sboishtyan.forsportsru.util;

import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;

import com.eccyan.optional.Optional;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public final class FileUtils {

    private static final String BACKGROUND_IMAGE = "backgroundImage";

    private FileUtils() {
    }

    public static Optional<String> getBackgroundFilePathIfPresent(){
        File mediaStorageDir = mediaStorageDir();
        if (!mediaStorageDir.exists())
            return Optional.empty();

        File backGroundFile = getBackgroundFile(mediaStorageDir);
        return backGroundFile.exists() ? Optional.of(backGroundFile.getPath()) : Optional.<String>empty();
    }

    @NonNull
    private static File getBackgroundFile(File mediaStorageDir) {
        return new File(mediaStorageDir.getPath() + File.separator + BACKGROUND_IMAGE);
    }
    public static Observable<Optional<String>> saveFileAndGetPathForBackgroundImage(final byte[] bytes) {
        return Observable.create(new Observable.OnSubscribe<Optional<String>>() {
            @Override
            public void call(Subscriber<? super Optional<String>> subscriber) {
                File mediaStorageDir = mediaStorageDir();

                if (!mediaStorageDir.exists()) {
                    if(!mediaStorageDir.mkdirs()) {
                        Log.d(FileUtils.class.getSimpleName(), "Failed to create media directory");
                        subscriber.onNext(Optional.<String>empty());
                        return;
                    }
                }
                File file = getBackgroundFile(mediaStorageDir);

                try(FileOutputStream outputStream = new FileOutputStream(file)) {
                    outputStream.write(bytes);
                } catch (IOException e) {
                    subscriber.onNext(Optional.<String>empty());
                    return;
                }
                subscriber.onNext(Optional.of(file.getPath()));
                return;
            }
        }).subscribeOn(Schedulers.computation())
          .observeOn(AndroidSchedulers.mainThread());
    }

    @NonNull
    private static File mediaStorageDir() {
        return new File(Environment
                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), Constants.APP_NAME);
    }

}
