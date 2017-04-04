package com.example.android.motiondetect;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

import static com.example.android.motiondetect.CameraListActivityFragment.cameraNameList;
import static com.example.android.motiondetect.CameraListActivityFragment.urlList;

public class CameraListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //set AlertDialog on FAB
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(CameraListActivity.this);
                builder.setView(R.layout.add_camera_dialog);
                builder.setTitle(R.string.dialog_title);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        //find Camera EditText view
                        //add text to list of cameras
                        //Notify adapter of change
                        EditText editText = (EditText) ((AlertDialog)dialogInterface).findViewById(R.id.cameraNameId);
                        EditText editUrlText = (EditText) ((AlertDialog)dialogInterface).findViewById(R.id.urlId);

                        //Add name and url to ArrayList
                        String cameraName = editText.getText().toString();
                        String urlName = editUrlText.getText().toString();

                        cameraNameList.add(cameraName);
                        urlList.add(urlName);

                        //send to postNewCamera
                        postNewCamera(cameraName, urlName);

                        //notify adapter that the data changed
                        CameraListActivityFragment.mAdapter.notifyDataSetChanged();
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(CameraListActivity.this, "Cancel Clicked", Toast.LENGTH_SHORT).show();
                    }
                });


                AlertDialog dialog = builder.create();
                dialog.show();

            }
        });
    }

    public void postNewCamera(final String cameraName, final String urlName){

        final String url = "http://ec2-54-242-89-175.compute-1.amazonaws.com:8000/api/camera/";
        StringRequest strReq = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONArray reader;
                try {
                    reader = new JSONArray(response);
                    Log.i("CameraListActivityyyy", "Post request works");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        },
                new Response.ErrorListener(){

                    @Override
                    public void onErrorResponse(VolleyError error) {
//                        hideDialog();
                        Log.i("Error.Response", "Ahhhh");
                    }
                }
        ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("Authorization", LoginActivity.token);
                return map;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("name", cameraName);
                params.put("address", urlName);
                return params;
            }
        };
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(strReq);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_camera_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id){
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_logout:
                Intent intent2 = new Intent(this, LoginActivity.class);
                startActivity(intent2);
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
