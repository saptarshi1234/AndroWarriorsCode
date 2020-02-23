package com.example.new_hopes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements SongsFrag.OnFragmentInteractionListener, PlayListsFrag.OnFragmentInteractionListener, ArtistsFrag.OnFragmentInteractionListener, BottomSongPlay.OnFragmentInteractionListener {
    private static final String CLIENT_ID = "dae0c85164de446a84f2d867b2908818";
    private static final String REDIRECT_URI = "http://localhost:8888/callback";
    private static final int REQUEST_CODE = 1337;
    private static final String SCOPES = "user-read-recently-played,user-library-modify,user-read-email,user-read-private";
    private SharedPreferences.Editor editor;
    private SharedPreferences msharedPreferences;
    private RequestQueue queue;
    final String TAG = "MainActivity";
    ArrayList<PlayListNames> playListnames = new ArrayList<>();
    MaterialSearchView searchView;
    ViewPager viewPager;
    TabLayout tabLayout;
    TabAdapter adapter;
    String list[];


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


                CollectSongs collectSongs = new CollectSongs(msharedPreferences,queue);
                collectSongs.startGettingSongs();





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
        Toolbar toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);


        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);

        AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI);
        builder.setScopes(new String[]{SCOPES});
        AuthenticationRequest request = builder.build();

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);

        Log.d("hell","auth");
        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);
        Log.d("hell","auth2");
        msharedPreferences = this.getSharedPreferences("SPOTIFY", 0);
        queue = Volley.newRequestQueue(this);



        viewPager =  findViewById(R.id.view_pager);
        tabLayout =  findViewById(R.id.tab_layout);
        adapter = new TabAdapter(getSupportFragmentManager());
        adapter.addFragment(new SongsFrag(), "Songs");
        adapter.addFragment(new PlayListsFrag(), "Playlist");
        adapter.addFragment(new ArtistsFrag(), "Artists");

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);


        list = new String[]{"Clipcodes", "Android Tutorials", "Youtube Clipcodes Tutorials", "SearchView Clicodes", "Android Clipcodes", "Tutorials Clipcodes"};
        searchViewCode();



    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    private void searchViewCode() {
        searchView = (MaterialSearchView) findViewById(R.id.search_view);

        searchView.setSuggestions(list);
        searchView.setEllipsize(true);
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Toast.makeText(getApplicationContext(), query, Toast.LENGTH_SHORT).show();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
            }

            @Override
            public void onSearchViewClosed() {
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu, menu);

        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onBackPressed() {
        if (searchView.isSearchOpen()) {
            searchView.closeSearch();
        } else {
            super.onBackPressed();
        }
    }
}
