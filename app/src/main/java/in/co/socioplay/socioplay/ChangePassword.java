package in.co.socioplay.socioplay;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

public class ChangePassword extends AppCompatActivity {

    EditText etnewpassword;
    EditText etconfirmpassword;
    Button btnchangepassword;
    String token1="null";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);


        etnewpassword = findViewById(R.id.etnewpass);
        etconfirmpassword = findViewById(R.id.etconfirmpass);
        btnchangepassword = findViewById(R.id.btnreset);
        token();


        btnchangepassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pass=etnewpassword.getText().toString();
                String cpass=etconfirmpassword.getText().toString();
                if(TextUtils.isEmpty(pass)||TextUtils.isEmpty(cpass))
                {
                    Toast.makeText(ChangePassword.this, "cannot be empty", Toast.LENGTH_SHORT).show();
                }
                else if(pass.length()<4||cpass.length()<4)
                {
                    Toast.makeText(ChangePassword.this, "Too samll", Toast.LENGTH_SHORT).show();
                }
                else if (!etnewpassword.getText().toString().equals(etconfirmpassword.getText().toString())) {
                    Toast.makeText(ChangePassword.this, "passwords dont match", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    reset(pass);
                }


            }
        });


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


    private void reset(String s) {
        Reset rst = new Reset(etnewpassword.getText().toString());
        rst.doInBackground();

    }

    public class Reset extends AsyncTask<Void, Void, Boolean> {


        final String mPassword;
        final JSONObject log= new JSONObject();


        Reset(String password)  {
            mPassword = password;
            try {

                log.put("password",mPassword);

                if (log.length() > 0) {
                    doInBackground();
                }
            }catch (JSONException e){e.printStackTrace();}
        }


        @Override
        protected Boolean doInBackground(Void... voids) {



            try {
                // Simulate network access.
                Thread.sleep(2000);
                getServerResponse(log);
            } catch (InterruptedException e) {
                Log.d("Exception from :", "doInBackground");
                e.printStackTrace();
            }
            return null;
        }

        private void getServerResponse(final JSONObject log) {

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    JSONObject networkResp = null;
                    {
                        try {
                            try {
                                URL url = new URL("https://socioplay.in/reset/password");
                                MediaType JSON = MediaType.parse("application/json; charset=utf-8");

                                okhttp3.RequestBody body = RequestBody.create(JSON, log.toString());

                                OkHttpClient client = new OkHttpClient();
                                okhttp3.Request request = new okhttp3.Request.Builder()
                                        .url(url)
                                        .header("Authorization", "Bearer "+ token1)
                                        .header("Content-Type","application/json")
                                        .post(body)
                                        .build();


                                okhttp3.Response response = client.newCall(request).execute();
                                Log.d("Response", "achieved");
                                String networkResp1 = response.body().string();
                                networkResp = new JSONObject(networkResp1);
                            } catch (Exception ex) {
                                String err = String.format("{\"result\":\"false\",\"error\":\"%s\"}", ex.getMessage());
                                Log.d("Exception ex:", err);
                            }
                            String result = (networkResp.getString("status"));
                            Log.d("Status Code is :", result);
                            String S = "EVENT STATUS";
                            if (result != null) {
                                if (result.equals("200")) {
                                    Log.d("Reset success", S);
                                    onPostExecute();

                                } else {
                                    Log.d("Reset Failed", S);
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(ChangePassword.this, "Invalid Details!", Toast.LENGTH_LONG).show();
                                        }
                                    });

                                }
                            }
                        } catch (Exception e) {
                            Log.d("InputStream", e.getLocalizedMessage());
                        }
                    }

                }
            });
            thread.start();
        }

        private void onPostExecute() {

            String S = "STATUS";
            Log.d("Starting!", S);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(ChangePassword.this, "Password reset", Toast.LENGTH_LONG).show();

                }
            });

        }

        @Override
        protected void onCancelled() {

        }


    }
}
