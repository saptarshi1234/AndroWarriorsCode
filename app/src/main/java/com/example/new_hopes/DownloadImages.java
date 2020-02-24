package com.example.new_hopes;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;


import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

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

    static Bitmap getImage(String song_name){
        Bitmap bitmap = null;
        try {

            File directory = new File (Environment.getExternalStorageDirectory().getAbsolutePath() + "/img_data",song_name.toLowerCase());

            File file = new File(directory, "image_name.jpg");


            FileInputStream fis = new FileInputStream(file);
            /*DataInputStream in = new DataInputStream(fis);
            BufferedReader br =
                    new BufferedReader(new InputStreamReader(in));
            String strLine;
            while ((strLine = br.readLine()) != null) {
                myData = myData + strLine;


            }*/

            bitmap = BitmapFactory.decodeStream(fis); //This gets the image



            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }


}
