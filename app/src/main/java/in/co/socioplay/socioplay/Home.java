package in.co.socioplay.socioplay;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import okhttp3.OkHttpClient;



public class Home extends AppCompatActivity {
    private TextView mTextMessage;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setText(R.string.title_home);
                    return true;
                case R.id.navigation_dashboard:
                    mTextMessage.setText(R.string.title_dashboard);
                    return true;
                case R.id.navigation_notifications:
                    mTextMessage.setText(R.string.title_notifications);
                    return true;
            }
            return false;
        }
    };
        @Override
        protected void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
            Button delete = findViewById(R.id.delete);
        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        delete.setOnClickListener(new OnClickListener() {
                                      @Override
                                      public void onClick(View v) {
                                            DELETE obj= new DELETE();
                                            obj.doInBackground();
                                      }
                                  }
        );
    }
    public class DELETE extends AsyncTask<Void,Void,Boolean>
    {

        void DACC () {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    String token = null;
                    Bundle bundle = getIntent().getExtras();
                    if (bundle != null) {
                        token = (String) bundle.getCharSequence("token");
                    }
                    Log.d("Token", token);
                    URL url = null;
                    try {
                        url = new URL("http://59f9d951.ngrok.io/users/delete");

                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }

                    OkHttpClient client = new OkHttpClient();
                    okhttp3.Request request = new okhttp3.Request.Builder()
                               .url(url)
                                 .header("Authorization", "Bearer "+ token)
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
                        String status=res.getString("statusCode");
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


}
