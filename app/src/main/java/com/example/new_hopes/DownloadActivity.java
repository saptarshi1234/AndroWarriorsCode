package com.example.new_hopes;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.util.SparseArray;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import at.huber.youtubeExtractor.VideoMeta;
import at.huber.youtubeExtractor.YouTubeExtractor;
import at.huber.youtubeExtractor.YtFile;

import static android.content.ContentValues.TAG;

public class DownloadActivity  {

    Context context;

    private static final int ITAG_FOR_AUDIO = 140;

    private static String youtubeLink;

    private List<YtFragmentedVideo> formatsToShowList;


    DownloadActivity(Context context){
        this.context=context;
    }

    void startDownloading(String url,String songName)
    {
        if (url.equals(""))
            return;
        // We have a valid link
        Log.d(TAG, "startDownloading: NOW songname"+songName);
        getYoutubeDownloadUrl(url,songName);

    }

    @SuppressLint("StaticFieldLeak")
    private void getYoutubeDownloadUrl(String youtubeLink, final String songName) {
        new YouTubeExtractor(context) {

            @Override
            public void onExtractionComplete(SparseArray<YtFile> ytFiles, VideoMeta vMeta) {
                if (ytFiles == null) {
                    return;
                }
                formatsToShowList = new ArrayList<>();
                for (int i = 0, itag; i < ytFiles.size(); i++) {
                    itag = ytFiles.keyAt(i);
                    YtFile ytFile = ytFiles.get(itag);

                    if (ytFile.getFormat().getHeight() == -1 || ytFile.getFormat().getHeight() >= 360) {
                        addFormatToList(ytFile, ytFiles);
                    }
                }
                Collections.sort(formatsToShowList, new Comparator<YtFragmentedVideo>() {
                    @Override
                    public int compare(YtFragmentedVideo lhs, YtFragmentedVideo rhs) {
                        return lhs.height - rhs.height;
                    }
                });
                for (YtFragmentedVideo files : formatsToShowList) {

                    addButtonToMainLayout(vMeta.getTitle(), files,songName);
                }
            }
        }.extract(youtubeLink, true, false);
    }

    private void addFormatToList(YtFile ytFile, SparseArray<YtFile> ytFiles) {
        int height = ytFile.getFormat().getHeight();
        if (height != -1) {
            for (YtFragmentedVideo frVideo : formatsToShowList) {
                if (frVideo.height == height && (frVideo.videoFile == null ||
                        frVideo.videoFile.getFormat().getFps() == ytFile.getFormat().getFps())) {
                    return;
                }
            }
        }
        YtFragmentedVideo frVideo = new YtFragmentedVideo();
        frVideo.height = height;
        if (ytFile.getFormat().isDashContainer()) {
            if (height > 0) {
                frVideo.videoFile = ytFile;
                frVideo.audioFile = ytFiles.get(ITAG_FOR_AUDIO);
            } else {
                frVideo.audioFile = ytFile;
            }
        } else {
            frVideo.videoFile = ytFile;
        }
        formatsToShowList.add(frVideo);
    }


    private void addButtonToMainLayout(final String videoTitle, final YtFragmentedVideo ytFrVideo,String songName) {
        // Display some buttons and let the user choose the format
        if (ytFrVideo.height != -1)
            return;

        String filename;
        if (videoTitle.length() > 55) {
            filename = videoTitle.substring(0, 55);
        } else {
            filename = videoTitle;
        }
        filename = filename.replaceAll("[\\\\><\"|*?%:#/]", "");
        filename += (ytFrVideo.height == -1) ? "" : "-" + ytFrVideo.height + "p";
        String downloadIds = "";
        boolean hideAudioDownloadNotification = false;
        if (ytFrVideo.videoFile != null) {
            downloadIds += downloadFromUrl(ytFrVideo.videoFile.getUrl(), videoTitle,
                    filename + "." + ytFrVideo.videoFile.getFormat().getExt(), false,songName);
            downloadIds += "-";
            hideAudioDownloadNotification = true;
        }
        if (ytFrVideo.audioFile != null) {
            downloadIds += downloadFromUrl(ytFrVideo.audioFile.getUrl(), videoTitle,
                    filename + "." + ytFrVideo.audioFile.getFormat().getExt(), hideAudioDownloadNotification,songName);
        }
        if (ytFrVideo.audioFile != null)
            cacheDownloadIds(downloadIds);
    }

    private long downloadFromUrl(String youtubeDlUrl, String downloadTitle, String fileName, boolean hide,String songName) {
        Log.d(TAG, "downloadFromUrl: "+youtubeDlUrl);
        Log.d(TAG, "downloadFromUrl: title"+downloadTitle);
        Uri uri = Uri.parse(youtubeDlUrl);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setTitle(downloadTitle);
        if (hide) {
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
            request.setVisibleInDownloadsUi(false);
        } else
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);


        File rootFolder = new File(Environment.DIRECTORY_DCIM,"new_hopes2");
        File songStorageFolder  = new File(rootFolder,"songs");
        if(!songStorageFolder.exists())
            songStorageFolder.mkdirs();
        Log.d(TAG, "downloadFromUrl: TO save as "+ songName);
        fileName=songName;
        request.setDestinationInExternalPublicDir(songStorageFolder.getAbsolutePath(), songName);


        DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        return manager.enqueue(request);
    }

    private void cacheDownloadIds(String downloadIds) {
        File dlCacheFile = new File(context.getCacheDir().getAbsolutePath() + "/" + downloadIds);
        try {
            dlCacheFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class YtFragmentedVideo {
        int height;
        YtFile audioFile;
        YtFile videoFile;
    }




}
