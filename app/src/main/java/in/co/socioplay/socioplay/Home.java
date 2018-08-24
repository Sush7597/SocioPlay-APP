package in.co.socioplay.socioplay;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import okhttp3.OkHttpClient;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static in.co.socioplay.socioplay.R.id.drawer_layout;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private TextView mTextMessage;
    String  token1="null";
    private List<Event> eventList = new ArrayList<>();
    RecyclerView rView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mTextMessage = findViewById(R.id.message);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = findViewById(drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        token();

        Event event = new Event("Cricket", "Pitampura", "20-August-2018");
        eventList.add(event);
        event = new Event("Chess", "Rohini", "21-August-2018");
        eventList.add(event);

        EventAdapter eAdapter = new EventAdapter(eventList);
        rView = findViewById(R.id.rview);


        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        rView.setLayoutManager(mLayoutManager);
        rView.setItemAnimator(new DefaultItemAnimator());
        rView.setAdapter(eAdapter);


        eAdapter.notifyDataSetChanged();

    }

    public void token()
    {

        SharedPreferences sp = getSharedPreferences("socioplay",MODE_PRIVATE);
        token1 = sp.getString("token", "null");
        Log.d("Stored Token:", token1);

        if(token1.equals("null"))
        {
            Intent act=new Intent(this,LoginActivity.class);
            startActivity(act);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        switch (id)
        {
            case R.id.del :
                DELETE obj= new DELETE();
                obj.doInBackground();
                @SuppressLint("SdCardPath") File sharedPreferenceFile = new File("/data/data/"+ getPackageName()+ "/shared_prefs/");
                File[] listFiles = sharedPreferenceFile.listFiles();
                for (File file : listFiles) {
                    file.delete();
                }
                Intent in3 = new Intent(Home.this,LoginActivity.class);
                startActivity(in3);
                break;
            case R.id.logout:
                Logout ob = new Logout();
                ob.doInBackground();
                @SuppressLint("SdCardPath") File sharedPreferenceFile1 = new File("/data/data/"+ getPackageName()+ "/shared_prefs/");
                File[] listFiles1 = sharedPreferenceFile1.listFiles();
                for (File file : listFiles1) {
                    file.delete();
                }
                Intent in4 = new Intent(Home.this,LoginActivity.class);
                startActivity(in4);

        }

        return super.onOptionsItemSelected(item);


    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = findViewById(drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    @SuppressLint("StaticFieldLeak")
    public class DELETE extends AsyncTask<Void,Void,Boolean>
    {

        void DACC () {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {

                    URL url = null;
                    try {
                        url = new URL("https://socioplay.in/users/delete");

                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }

                    OkHttpClient client = new OkHttpClient();
                    assert url != null;
                    okhttp3.Request request = new okhttp3.Request.Builder()
                            .url(url)
                            .header("Authorization", "Bearer "+ token1)
                            .header("Content-Type","application/json")
                            .build();

                    okhttp3.Response response = null;
                    try {
                        response = client.newCall(request).execute();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    String networkResp1 = null;
                    try {
                        assert response != null;
                        networkResp1 = Objects.requireNonNull(response.body()).string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    JSONObject res= null;
                    try {
                        res = new JSONObject(networkResp1);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        assert res != null;
                        String status=res.getString("status");
                        Log.d("Status Code:", status);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            );
            thread.start();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                // Simulate network access.
                Thread.sleep(2000);
                DACC();
            } catch (InterruptedException e) {
                Log.d("Exception from :","doInBackground");
                e.printStackTrace(); }
            return null;
        }
    }

    @SuppressLint("StaticFieldLeak")
    public class Logout extends AsyncTask<Void,Void,Boolean>
    {

        void LogOut () {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {

                    URL url = null;
                    try {
                        url = new URL("https://socioplay.in/users/logout");

                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }

                    OkHttpClient client = new OkHttpClient();
                    assert url != null;
                    okhttp3.Request request = new okhttp3.Request.Builder()
                            .url(url)
                            .header("Authorization", "Bearer "+ token1)
                            .header("Content-Type","application/json")
                            .build();

                    okhttp3.Response response = null;
                    try {
                        response = client.newCall(request).execute();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    String networkResp1 = null;
                    try {
                        assert response != null;
                        networkResp1 = Objects.requireNonNull(response.body()).string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    JSONObject res= null;
                    try {
                        res = new JSONObject(networkResp1);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        assert res != null;
                        String status=res.getString("status");
                        Log.d("Status Code:", status);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }


            }

            );
            thread.start();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                // Simulate network access.
                Thread.sleep(2000);
                LogOut();

            } catch (InterruptedException e) {
                Log.d("Exception from :","doInBackground");
                e.printStackTrace(); }
            return null;
        }

    }

}
