package com.example.new_hopes;


import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;


import java.io.File;

import static com.example.new_hopes.BasicImageDownloader.*;

public class DownloadImages {
    public static void download_image( String name_p,String url){
        final String name = name_p;
        final BasicImageDownloader downloader = new BasicImageDownloader(new OnImageLoaderListener() {
            @Override
            public void onError(ImageError error) {
                error.printStackTrace();
                Log.d("hell", "onError: COuld not download image");
            }

            @Override
            public void onProgressChange(int percent) {

            }

            @Override
            public void onComplete(Bitmap result) {
                /* save the image - I'm gonna use JPEG */
                Log.d("hell", "onComplete: Bitmap is being downloaded");
                final Bitmap.CompressFormat mFormat = Bitmap.CompressFormat.JPEG;
                /* don't forget to include the extension into the file name */
                final File myImageFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() +
                        File.separator + "Download" + File.separator + "img_data" + File.separator + name + "." + mFormat.name().toLowerCase());
                BasicImageDownloader.writeToDisk(myImageFile, result, new BasicImageDownloader.OnBitmapSaveListener() {
                    @Override
                    public void onBitmapSaved() {
                        Log.d("hell", "onBitmapSaved: Saved successfully");
                    }

                    @Override
                    public void onBitmapSaveError(ImageError error) {
                        error.printStackTrace();
                    }


                }, mFormat, false);

            }
        });
        downloader.download(url,false);
    }


}
