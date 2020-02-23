package com.example.new_hopes;

import android.content.Context;
import android.content.SharedPreferences;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CollectSongs {
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
                                //call methods;
                                for(int i=0;i<allPlaylists.get(0).song.size();i++){
                                    new Downloader(mContext).YoutubeUrl(allPlaylists.get(0).song.get(i).name);
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
                                Log.d("hell", (object == null) + " song");
                                Song song=new Song();
                                JSONObject track = object.getJSONObject("track");
                                song.name = track.getString("name");
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
//                                Song song = gson.fromJson(track.toString(), Song.class);
                                song.img_url = img_url;
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
}
