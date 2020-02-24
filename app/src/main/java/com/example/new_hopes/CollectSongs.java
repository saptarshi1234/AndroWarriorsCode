package com.example.new_hopes;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.util.Log;

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

                                File rootFolder = new File(Environment.DIRECTORY_DCIM,"new_hopes2");
                                if(!rootFolder.exists())
                                    rootFolder.mkdirs();
                                File savedPlaylistFile = new File(rootFolder,"allPlaylists1.ser");
                                try {
                                    if (!savedPlaylistFile.exists())
                                        savedPlaylistFile.createNewFile();
                                }catch (Exception e){
                                    Log.d(TAG, "OnCalledBack: could not create file"+ e.getMessage());
                                }
                                Log.d(TAG, "OnCalledBack: playlist saved at"+ savedPlaylistFile.getAbsolutePath());
                                ArrayList<PlayList> restoredPlist = (ArrayList<PlayList>) restore_state(savedPlaylistFile);
                                //allPlaylists = restoredPlist;

//                                for (int i1 = 0; i1 < restoredPlist.size(); i1++) {
//                                    PlayList plist = restoredPlist.get(i1);
//                                    ArrayList<Song> songArrayList = plist.songs;
//                                    for (int i2 = 0; i2 < songArrayList.size(); i2++) {
//                                        Song song = songArrayList.get(i2);
//                                        allPlaylists.get(i1).songs.get(i2).isDownloaded=song.isDownloaded;
//                                    }
//                                }
                                saveState(savedPlaylistFile,allPlaylists);


                                //allplaylist can be restored in any class by this static method
                                //List<PlayList> restoredPlist = (List<PlayList>) restore_state(new File(Environment.DIRECTORY_DOWNLOADS+"/new_hopes2/allPlalists.ser"));


                                //call methods;
                                for(PlayList playList: allPlaylists) {
                                    Log.d(TAG, "OnCalledBack: local playList"+playList);
                                    for (int i = 0; i < playList.songs.size(); i++) {
                                        File songFolder = new File(rootFolder+"/songs");
                                        boolean toDownLoad = true;
                                        String[] files = null;
                                        try {
                                            files = mContext.getAssets().list(songFolder.getAbsolutePath());
                                        }catch (Exception e){
                                            Log.d(TAG, "OnCalledBack: could not read folder : "+e.getMessage());
                                        }
//                                        for(String fileName: files){
//                                            if(fileName.startsWith(playList.songs.get(i).name))
//                                                toDownLoad = false;
//
//                                        }
                                        if(!playList.songs.get(i).isDownloaded)
                                            playList.songs.get(i).isDownloaded = true;
                                            new Downloader(mContext).YoutubeUrl(playList.songs.get(i).name);
                                    }
                                }

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
                                Song song = gson.fromJson(track.toString(), Song.class);
                                song.isDownloaded = false;
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
    private void saveState(File file,Object obj){
        try {
            FileOutputStream out = new FileOutputStream(file);
            ObjectOutputStream objOut = new ObjectOutputStream(out);
            objOut.writeObject(obj);
            out.close();

        } catch (Exception e) {
            Log.d(TAG, "saveState: could not save object : "+e.getMessage());
        }
    }

    private Object restore_state(File file){
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
