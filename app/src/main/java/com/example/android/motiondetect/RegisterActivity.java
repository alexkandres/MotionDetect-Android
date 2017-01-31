package com.example.android.motiondetect;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class RegisterActivity extends AppCompatActivity {

    final String TAG = RegisterActivity.class.getSimpleName();
    private AutoCompleteTextView emailTextView;
    private EditText passwordTextView;
    private EditText repasswordTextView;
    private Button registerButton;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //get EMAIL, PASSWORD, and ReTYPED PASWORD views
        emailTextView = (AutoCompleteTextView) findViewById(R.id.email_register_view);
        passwordTextView = (EditText) findViewById(R.id.password_register_view);
        repasswordTextView = (EditText) findViewById(R.id.password_register_view2);

        registerButton = (Button) findViewById(R.id.acty_reg_register_button);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //obtain strings from views
                String email = emailTextView.getText().toString();
                String password = passwordTextView.getText().toString();
                String reTypePassword = repasswordTextView.getText().toString();

                //check if password is typed correctly
                if(password.equals(reTypePassword)){
                    registerUser(email, password);
                }else {
                    Toast.makeText(RegisterActivity.this, "Please make sure passwords are the same", Toast.LENGTH_SHORT).show();
                }
            }
        });

        progressDialog = new ProgressDialog(RegisterActivity.this);
        progressDialog.setCancelable(false);
    }

    public void registerUser(String email, String password){

        //TODO 2, Create a queue for StringRequest
        RequestQueue queue = Volley.newRequestQueue(this);

        progressDialog.setMessage("Logging in...");
        showDialog();

        String url = "http://api.openweathermap.org/data/2.5/forecast/daily?q=94043&mode=json&units=metric&cnt=7&APPID=2416aeb32b3e3d8360593abf67e88ddc";
        StringRequest strReq = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                hideDialog();

                //Display Success
                Toast.makeText(RegisterActivity.this, "Successfully Registered", Toast.LENGTH_SHORT).show();

                //Start LoginActivity
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        },
                new Response.ErrorListener(){

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("Error.Response", error.getMessage());
                        hideDialog();
                    }
                }
        );

        queue.add(strReq);
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
