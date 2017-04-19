package com.example.android.motiondetect;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    public static String token;
    public static String id;
    public static String username;
    private String typeParam = "";


    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    if(mEmailView.getText().toString().contains("@")){
                        typeParam = "email";
                        loginUser();
                    }
                    else {
                        typeParam = "username";
                        loginUser();
                    }
                    return true;
                }
                return false;
            }
        });

        //instantiate ProgressDialog and setCancelable to false
        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setCancelable(false);

        //Sign-In when clicked
        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!validCredentials()){
                    Toast.makeText(LoginActivity.this, "Not valid credentials", Toast.LENGTH_SHORT).show();
                    return;
                }
                //TODO Remove auto login
                if(mEmailView.getText().toString().contains("@")){
                    typeParam = "email";
                    loginUser();
                }
                else {
                    typeParam = "username";
                    loginUser();
                }
            }
        });

        //Click register button to go to register activity
        Button registerButton = (Button) findViewById(R.id.email_register_button);
        registerButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

    }

    //check email and password
    private boolean validCredentials(){
        String email = mEmailView.getText().toString().trim();
        String password = mPasswordView.getText().toString().trim();

        if(email.isEmpty() | password.isEmpty())
            return false;

        return true;
    }
    private void loginUser(){
        progressDialog.setMessage("Logging in ...");
        //show dialog
        showDialog();

//        String url = "http://api.openweathermap.org/data/2.5/forecast/daily?q=94043&mode=json&units=metric&cnt=7&APPID=2416aeb32b3e3d8360593abf67e88ddc";
        String url = "http://ec2-54-242-89-175.compute-1.amazonaws.com:8000/api/login/";
        StringRequest strReq = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                hideDialog();
                JSONObject reader;

                try {
                    reader = new JSONObject(response);
                    token = "jwt " + reader.getString("token");
                    id = reader.getString("id");
                    username = reader.getString("username");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Intent intent = new Intent(LoginActivity.this, CameraListActivity.class);
                startActivity(intent);
            }
        },
            new Response.ErrorListener(){

                @Override
                public void onErrorResponse(VolleyError error) {
                    hideDialog();

                    if(error.networkResponse.data != null){
                        try {
                            String body = new String(error.networkResponse.data, "UTF-8");
                            Log.i("Error.Responseee", body);

                            //TODO process response
                            try {
                                JSONObject jsonObject = new JSONObject(body);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
//                            reader = new JSONObject(response);
//                            token = "jwt " + reader.getString("token");
//                            id = reader.getString("id");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put(typeParam, mEmailView.getText().toString());
                params.put("password", mPasswordView.getText().toString());
                return params;
            }
        };
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(strReq);
    }

    private void showDialog() {
        if (!progressDialog.isShowing())
            progressDialog.show();
    }

    private void hideDialog() {
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }

}

