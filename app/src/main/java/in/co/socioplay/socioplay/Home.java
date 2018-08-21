package in.co.socioplay.socioplay;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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


public class Home extends AppCompatActivity {
    private TextView mTextMessage;
    String  token1="null";
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setText(R.string.title_home);
                    return true;
                case R.id.navigation_dashboard:
                    mTextMessage.setText(R.string.Chats);
                    return true;
                case R.id.navigation_notifications:
                    mTextMessage.setText(R.string.profile);
                    return true;
            }
            return false;
        }
    };
    private List<Event> eventList = new ArrayList<>();
    private EventAdapter eAdapter;
    RecyclerView recyclerView;

    @Override
        protected void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mTextMessage = findViewById(R.id.message);
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        token();

        Event event = new Event("Cricket", "Pitampura", "20-August-2015");
        eventList.add(event);
        event = new Event("Chess", "Rohini", "21-August-2015");
        eventList.add(event);


        recyclerView = findViewById(R.id.r_view);
        eAdapter = new EventAdapter(eventList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(eAdapter);
        //prepareEventData();
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
    public boolean onCreateOptionsMenu(Menu menu) {
            getMenuInflater().inflate(R.menu.menu_main,menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
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
                        networkResp1 = response.body().string();
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
                        networkResp1 = response.body().string();
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

//    private void prepareEventData() {
//        Event event = new Event("Cricket", "Pitampura", "20-August-2015");
//        eventList.add(event);
//
//        event = new Event("Chess", "Rohini", "21-August-2015");
//        eventList.add(event);
//
//        eAdapter.notifyDataSetChanged();
//    }





}
