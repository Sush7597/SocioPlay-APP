package in.co.socioplay.socioplay;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mUserView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    TextView signup;
    TextView reset;
    EditText user;
    EditText mail;
    Dialog d;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        mUserView = findViewById(R.id.username);
        populateAutoComplete();

        mPasswordView = findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    assert inputManager != null;
                    inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = findViewById(R.id.email_sign_in_button);
        signup= findViewById(R.id.SignUp);
        reset=findViewById(R.id.resetP);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                assert inputManager != null;
                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
               attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        signup.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v) {

                Intent intent =new Intent(LoginActivity.this,SignUp.class);
                startActivity(intent);
            }
        });

       reset.setOnClickListener(new OnClickListener() {
            @SuppressLint("InflateParams")
            @Override
            public void onClick(View v) {
               View view =  LayoutInflater.from(LoginActivity.this).inflate(R.layout.forget_u_or_p,null);
               AlertDialog.Builder forget = new AlertDialog.Builder(LoginActivity.this);
                mail = view.findViewById(R.id.mail);
                forget.setView(view);
                forget.setPositiveButton("Request", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String mail1 = mail.getText().toString();
                        forget obj = new forget(mail1);
                        obj.doInBackground();
                    }
                });
                d=forget.create();
                d.show();


            }
        });


    }

    public void refresh() {

        finish();
        startActivity(getIntent());

    }

    private boolean isEmailValid(){

        String getText=mail.getText().toString().trim();

        return Pattern.compile("^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$").matcher(getText).matches();
    }

    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

        getLoaderManager().initLoader(0, null, this);
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mUserView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mUserView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String unam = mUserView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
        mPasswordView.setError(getString(R.string.error_invalid_password));
        focusView = mPasswordView;
        cancel = true;
    }

    // Check for a valid email address.
        if (TextUtils.isEmpty(unam)) {
        mUserView.setError(getString(R.string.error_field_required));
        focusView = mUserView;
        cancel = true;
    }

        if (cancel) {
        // There was an error; don't attempt login and focus the first
        // form field with an error.
        focusView.requestFocus();
    } else {
        // Show a progress spinner, and kick off a background task to
        // perform the user login attempt.
            showProgress(true);
        mAuthTask = new UserLoginTask(unam, password);
        mAuthTask.execute((Void) null);
    }
}

   /* private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }*/

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean shw) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mLoginFormView.setVisibility(shw ? View.GONE : View.VISIBLE);
        mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                shw ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mLoginFormView.setVisibility(shw ? View.GONE : View.VISIBLE);
            }
        });

        mProgressView.setVisibility(shw ? View.VISIBLE : View.GONE);
        mProgressView.animate().setDuration(shortAnimTime).alpha(
                shw ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgressView.setVisibility(shw ? View.VISIBLE : View.GONE);
            }
        });
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mUserView.setAdapter(adapter);
    }

    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
    }
    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     * */


    @SuppressLint("StaticFieldLeak")
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

         final String mUsername;
         final String mPassword;
         final JSONObject log= new JSONObject();


        UserLoginTask(String username, String password)  {
            mUsername = username;
            mPassword = password;
            try {
            log.put("email",mUsername);
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
                Log.d("Exception from :","doInBackground");
                e.printStackTrace(); }
            return null;
        }
        private void getServerResponse(final JSONObject log) {
            JSONObject networkResp = new JSONObject();

            try {
                try {
                    URL url = new URL("https://socioplay.in/users/login");
                    MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                    OkHttpClient client = new OkHttpClient();
                    okhttp3.RequestBody body = RequestBody.create(JSON, log.toString());
                    okhttp3.Request request = new okhttp3.Request.Builder()
                                                                    .url(url)
                                                                    .post(body)
                                                                    .build();

                    okhttp3.Response response = client.newCall(request).execute();
                    String networkResp1=response.body().string();
                    networkResp= new JSONObject(networkResp1);

                } catch (Exception ex)
                {
                    String err = String.format("{\"result\":\"false\",\"error\":\"%s\"}", ex.getMessage());
                    Log.d("Exception ex:",err);
                }
                String result = (networkResp.getString("status"));
                String token = networkResp.getString("result");
                Log.d("Status Code is :",result);
                Log.d("Token is :",token);
                SharedPreferences sp = getSharedPreferences("socioplay", MODE_PRIVATE);
                SharedPreferences.Editor editor= sp.edit();
                editor.putString("token", token);
                editor.apply();

                String S="EVENT STATUS";
                if (result != null)
                {
                    if(result.equals("200"))
                    {
                        Log.d("LogIn success",S);
                       onPostExecute(token);

                    }
                    else
                    {
                        Log.d("LogIn Failed",S);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showProgress(false);
                                Toast.makeText(LoginActivity.this, "Invalid E-mail ID Or Password!", Toast.LENGTH_LONG).show();
                                refresh();
                            }
                        });

                    }
                }
            } catch (Exception e) {
                Log.d("InputStream", e.getLocalizedMessage());
            }
        }
        private void onPostExecute(String token) {

                Intent intent=new Intent(LoginActivity.this,Home.class);
                String S="STATUS";
                Log.d("Starting!",S);
                startActivity(intent);
        }
        @Override
        protected void onCancelled()
        {
            mAuthTask = null;
            showProgress(false);
        }
    }


    @SuppressLint("StaticFieldLeak")
    public class forget extends AsyncTask<Void, Void, Boolean> {


        final String mmail;
        int i;


        forget(String Email)  {

            mmail = Email;
            Log.d("Status","Constructor Called");
        }


        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                // Simulate network access.
                Thread.sleep(2000);
                getServerResponse(mmail);
            } catch (InterruptedException e) {
                Log.d("Exception from :","doInBackground");
                e.printStackTrace(); }
            return null;
        }
        private void getServerResponse(final String log1) {

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    JSONObject networkResp = null;
                   {
                        try {
                            try {
                                String query = URLEncoder.encode(log1, "utf-8");
                                URL url = new URL("https://socioplay.in/reset/password/" + query);
                                OkHttpClient client = new OkHttpClient();
                                okhttp3.Request request = new okhttp3.Request.Builder()
                                        .url(url)
                                        .header("Content-Type", "application/json")
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
                                            Toast.makeText(LoginActivity.this, "Invalid Details!", Toast.LENGTH_LONG).show();
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

            String S="STATUS";
            Log.d("Starting!",S);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(LoginActivity.this, "Check Your Email Id for Reset Link!", Toast.LENGTH_LONG).show();
                    refresh();
                }
            });

        }
        @Override
        protected void onCancelled()
        {

        }
    }

}