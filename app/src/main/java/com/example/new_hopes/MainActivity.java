package com.example.new_hopes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static final String CLIENT_ID = "dae0c85164de446a84f2d867b2908818";
    private static final String REDIRECT_URI = "http://localhost:8888/callback";
    private static final int REQUEST_CODE = 1337;
    private static final String SCOPES = "user-read-recently-played,user-library-modify,user-read-email,user-read-private";
    private SharedPreferences.Editor editor;
    private SharedPreferences msharedPreferences;
    private RequestQueue queue;
    final String TAG = "hell";
    ArrayList<PlayListNames> playListnames = new ArrayList<>();


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        Log.d("hell","callback");

        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);

            AuthenticationResponse.Type type = response.getType();// Response was successful and contains auth token
            if (type == AuthenticationResponse.Type.TOKEN) {
                editor = getSharedPreferences("SPOTIFY", 0).edit();
                editor.putString("token", response.getAccessToken());
                Log.d(TAG, "GOT AUTH TOKEN");
                editor.apply();

                final ArrayList<PlayList> allPlaylists = new ArrayList<>();
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
                                        //call methods;;

                                    }
                                }
                            });
                        }

                    }
                });






            } else if (type == AuthenticationResponse.Type.ERROR) {
                Log.d("hell", "err");
            }
            else{
                Log.d("hell", "at a loss");
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
        getSupportActionBar().hide();

        AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI);
        builder.setScopes(new String[]{SCOPES});
        AuthenticationRequest request = builder.build();

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);

        Log.d("hell","auth");
        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);
        Log.d("hell","auth2");
        msharedPreferences = this.getSharedPreferences("SPOTIFY", 0);
        queue = Volley.newRequestQueue(this);




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
                String token = msharedPreferences.getString("token", "");
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
                        Log.d("hell", (jsonArray == null) + "array");
                        for (int n = 0; n < jsonArray.length(); n++) {
                            try {
                                JSONObject object = jsonArray.getJSONObject(n);
                                if (object == null)
                                    continue;
                                Log.d("hell", (object == null) + " song");
                                JSONObject track = object.getJSONObject("track");
                                JSONObject album = object.getJSONObject("album");
                                JSONArray images = album.optJSONArray("images");
                                String img_url="";
                                for (int i = 0; i < images.length(); i++) {
                                    JSONObject image = images.getJSONObject(i);
                                    img_url = image.optString("url").replace(null,"");
                                    String width = image.optString("width").replace(null,"");
                                    String height = image.optString("height").replace(null,"");
                                    if (Integer.parseInt(width) >= 640 | Integer.parseInt(height) >= 640)
                                        break;
                                }
                                Log.d("hell",img_url);
                                Song song = gson.fromJson(track.toString(), Song.class);
                                song.img_url = img_url;

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
                String token = msharedPreferences.getString("token", "");
                String auth = "Bearer " + token;
                headers.put("Authorization", auth);
                return headers;
            }
        };
        queue.add(jsonObjectRequest);

    }
}
