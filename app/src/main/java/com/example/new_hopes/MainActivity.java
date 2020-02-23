package com.example.new_hopes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Window;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.new_hopes.R;
import com.google.gson.Gson;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
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
    ArrayList<String> playLists = new ArrayList<>();


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
        Log.d("hell","auth");
        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);
        Log.d("hell","auth2");
        msharedPreferences = this.getSharedPreferences("SPOTIFY", 0);
        queue = Volley.newRequestQueue(this);

    }
    private ArrayList<String> getPlayLists(){
        String endpoint = "https://api.spotify.com/v1/me/playlists";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, endpoint, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Gson gson = new Gson();
                        Log.d("hell", response.toString());

                        File sd = Environment.getExternalStorageDirectory();
                        File dest1 = new File(sd, "song.json");
                        FileOutputStream out;

                        try {
                            if (!dest1.exists()) {
                                dest1.createNewFile();
                            }
                            out = new FileOutputStream(dest1);
                            out.write(response.toString().getBytes());
                            out.close();
                            Log.d("hell", "done");
                        } catch (Exception e) {
                            Log.d("hell", e.getMessage().toString());
                        }


                        JSONArray jsonArray = response.optJSONArray("items");
                        Log.d("hell", (jsonArray == null) + "arrray");
                        for (int n = 0; n < jsonArray.length(); n++) {
                            try {
                                JSONObject object = jsonArray.getJSONObject(n);
                                object = object.optJSONObject("track");
                                if (object == null)
                                    continue;
                                Log.d("hell", (object == null) + "song");
                                //Log.d("hell",object.toString());
//                                Song song = gson.fromJson(object.toString(), Song.class);
//                                songs.add(song);

                            } catch (JSONException e) {
                                e.printStackTrace();

                            }
                        }

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
        return playLists;
    }

}
