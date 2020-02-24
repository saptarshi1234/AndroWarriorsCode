package com.example.new_hopes;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.os.FileUtils;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CollectSongs {
    private static final String TAG = "hell";
    private SharedPreferences sharedPreferences;
    private RequestQueue queue;
    public Context mContext;
    public ArrayList<PlayListNames> playListnames = new ArrayList<>();
    public ArrayList<PlayList> allPlaylists = new ArrayList<>();

    public CollectSongs(SharedPreferences sharedPreferences, RequestQueue queue,Context mContext) {
        this.sharedPreferences = sharedPreferences;
        this.queue = queue;
        this.mContext = mContext;
    }


    public void startGettingSongs(){
        setPlayListnames(new CallBack() {
            @Override
            public void OnCalledBack() {
                Log.d("hell",""+ playListnames.size());
                for (int i = 0; i < playListnames.size(); i++) {
                    final PlayListNames pListName = playListnames.get(i);

                    final ArrayList<Song> songs = new ArrayList<>();
                    Log.d("hell", pListName.name + " getting");
                    final int finalI = i;
                    getSongs(songs, pListName.id, new CallBack() {
                        @Override
                        public void OnCalledBack() {
                            Log.d("hell", songs.toString());
                            allPlaylists.add(new PlayList(pListName, songs));

                            Log.d("hell", "jnkdjj" + allPlaylists.size() + "");
                            if(finalI ==playListnames.size()-1){
                                Log.d("hell","all set");

//                                File rootFolder = new File(Environment.getExternalStorageDirectory(),"new_hopes");
//                                if(!rootFolder.exists())
//                                    rootFolder.mkdirs();
//                                File savedPlaylistFile = new File(rootFolder,"allPlaylists.ser");
//                                try {
//                                    if (!savedPlaylistFile.exists())
//                                        savedPlaylistFile.createNewFile();
//                                }catch (Exception e){
//                                    Log.d(TAG, "OnCalledBack: could not create file"+ e.getMessage());
//                                }
                                final File savedPlaylistFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() +
                                        File.separator + "new_hopes21" + File.separator);

                                Log.d(TAG, "OnCalledBack: playlist saved at"+ savedPlaylistFile.getAbsolutePath());
                                List<PlayList> restoredPlist = null;
                                if(savedPlaylistFile.exists())
                                    restoredPlist = (List<PlayList>) restore_state(savedPlaylistFile);

                                saveState(savedPlaylistFile.getAbsoluteFile(),allPlaylists);


                                //allplaylist can be restored in any class by this static method

                                for (int i1 = 0; i1 < restoredPlist.size(); i1++) {
                                    PlayList plist = restoredPlist.get(i1);
                                    ArrayList<Song> songArrayList = plist.songs;
                                    for (int i2 = 0; i2 < songArrayList.size(); i2++) {
                                        Song song = songArrayList.get(i2);
                                        try{
                                            allPlaylists.get(i1).songs.get(i2).isDownloaded = song.isDownloaded;
                                            allPlaylists.get(i1).songs.get(i2).songLocation = song.songLocation;

                                        }catch (Exception e){
                                            Log.d(TAG, "OnCalledBack: err merging "+e.getMessage());
                                        }
                                    }
                                }

                                File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/storage/emulated/0/new_hopes21/songs1");
                                Log.d(TAG, "OnCalledBack: here"+ f.getAbsolutePath());
//                                ArrayList<String> b = new ArrayList<>();
//                                for(File child : f.listFiles()){
//                                    b.add(child.delete()?"true":"false");
//                                }


                                //call methods;
                                for(PlayList playList: allPlaylists) {
                                    Log.d(TAG, "OnCalledBack: local playList"+playList);
                                    for (int i = 0; i < playList.songs.size(); i++) {
                                       // if(!playList.songs.get(i).isDownloaded) {
                                            playList.songs.get(i).isDownloaded = true;
                                            Toast.makeText(mContext, "downloading", Toast.LENGTH_SHORT).show();
                                            new Downloader(mContext,allPlaylists,savedPlaylistFile).YoutubeUrl(playList.songs.get(i));
                                        //}
                                    }
                                }
                                //Toast.makeText(mContext, "all downloaded", Toast.LENGTH_SHORT).show();
                                CollectSongs.saveState(savedPlaylistFile.getAbsoluteFile(),allPlaylists);



                            }
                        }
                    });
                }

            }
        });
    }
    private void setPlayListnames(final CallBack callBack){
        String endpoint = "https://api.spotify.com/v1/me/playlists";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, endpoint, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Gson gson = new Gson();
                        Log.d("hell", "response "+response.toString());

                        JSONArray jsonArray = response.optJSONArray("items");
                        Log.d("hell", (jsonArray == null) + "arrray");
                        for (int n = 0; n < jsonArray.length(); n++) {
                            try {
                                JSONObject object = jsonArray.getJSONObject(n);
                                if (object == null)
                                    continue;
                                PlayListNames pList = gson.fromJson(object.toString(), PlayListNames.class);
                                Log.d("hell", (pList == null) + " playlist");
                                Log.d("hell",pList.name);
                                //Log.d("hell",object.toString());
                                playListnames.add(pList);

                            } catch (JSONException e) {
                                e.printStackTrace();

                            }
                        }
                        callBack.OnCalledBack();


                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error

                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                String token = sharedPreferences.getString("token", "");
                String auth = "Bearer " + token;
                headers.put("Authorization", auth);
                return headers;
            }
        };
        queue.add(jsonObjectRequest);

    }
    private void getSongs(final ArrayList<Song> songs, String id, final CallBack callBack) {
        Log.d("hell","getSong");
        String endpoint = "https://api.spotify.com/v1/playlists/" + id;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, endpoint, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Gson gson = new Gson();
                        Log.d("hell", "response " + response.toString());
                        JSONObject tracks = null;
                        try {
                            tracks = response.getJSONObject("tracks");
                        }catch(Exception e){
                            Log.d("hell","error"+e.getMessage());
                        }
                        JSONArray jsonArray = tracks.optJSONArray("items");
                        Log.d("hell", (jsonArray == null) + "arrray");
                        for (int n = 0; n < jsonArray.length(); n++) {
                            try {
                                JSONObject object = jsonArray.getJSONObject(n);
                                if (object == null)
                                    continue;
                                Log.d("hell", (object == null) + " songs");
                                JSONObject track = object.getJSONObject("track");
                                JSONObject album = track.getJSONObject("album");
                                JSONArray images = album.optJSONArray("images");
                                String img_url="";
                                for (int i = 0; i < images.length(); i++) {
                                    JSONObject image = images.getJSONObject(i);
                                    img_url = image.getString("url");
                                    String width = image.getString("width");
                                    String height = image.getString("height");
                                    if (Integer.parseInt(width) >= 640 | Integer.parseInt(height) >= 640)
                                        break;
                                }
                                Log.d("hell",img_url);
                                Song song = gson.fromJson(track.toString(), Song.class);
                                Log.d("hell",song.name);
                                songs.add(song);

                            } catch (JSONException e) {
                                e.printStackTrace();

                            }
                        }
                        callBack.OnCalledBack();

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error

                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                String token = sharedPreferences.getString("token", "");
                String auth = "Bearer " + token;
                headers.put("Authorization", auth);
                return headers;
            }
        };
        queue.add(jsonObjectRequest);

    }
    public static void saveState(File dest1,Object obj){
//        File sd = Environment.getExternalStorageDirectory();
//        File dest1 = new File(sd, "chat.ser");
        try {
            FileOutputStream out = new FileOutputStream(dest1);
            ObjectOutputStream objOut = new ObjectOutputStream(out);
            objOut.writeObject(obj);
            out.close();

        } catch (Exception e) {
            Log.d(TAG, "saveState: could not save object : "+e.getMessage());
        }
    }

    public static Object restore_state(File file){
        Object obj = null;
        try {
            FileInputStream out = new FileInputStream(file);
            ObjectInputStream objOut = new ObjectInputStream(out);
            obj = objOut.readObject();
            Log.d(TAG,"found object"+obj.getClass());
            out.close();

        } catch (Exception e) {
            Log.d(TAG, "restore_state: could not read object : "+e.getMessage());
        }
        return obj;

    }
}
