package com.example.android.motiondetect;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.CreatePlatformEndpointRequest;
import com.amazonaws.services.sns.model.CreatePlatformEndpointResult;
import com.amazonaws.services.sns.model.GetEndpointAttributesRequest;
import com.amazonaws.services.sns.model.GetEndpointAttributesResult;
import com.amazonaws.services.sns.model.InvalidParameterException;
import com.amazonaws.services.sns.model.NotFoundException;
import com.amazonaws.services.sns.model.SetEndpointAttributesRequest;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A placeholder fragment containing a simple view.
 */
public class CameraListActivityFragment extends Fragment implements CameraAdapter.ListItemClickListener, CameraAdapter.OnLongClickListener{

    public static CameraAdapter mAdapter;
    private RecyclerView mNumbersList;
    Toast mToast;
    String androidToken;
    ProgressDialog progressDialog;

    //may need to change to non-static variable
    public static ArrayList<String> cameraNameList = new ArrayList<>();
    public static ArrayList<String> urlList = new ArrayList<>();
    public static ArrayList<String> cameraIdList = new ArrayList<>();

    //constructor
    public CameraListActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //token for this android device
        //TODO save token to api with correct headers
        androidToken = FirebaseInstanceId.getInstance().getToken();

        new LoadAwsTask().execute();
        //inflate view
        View view = inflater.inflate(R.layout.fragment_camera_list, container, false);

        //find recycler view
        mNumbersList = (RecyclerView) view.findViewById(R.id.camera_numbers);

        //get layout managers
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());

        //set manager to recyclerview
        mNumbersList.setLayoutManager(layoutManager);

        //set cameraNameList and urlList
        getCamerasRequest();
//        cameraNameList = new ArrayList<>(Arrays.asList("Camera 1", "Camera 2"));

        //instantiate adapter with data and both click listeners below
        mAdapter = new CameraAdapter(cameraNameList,urlList , cameraIdList, this, this);
        mNumbersList.setAdapter(mAdapter);

        //Add camera with this FAB
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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

//                        cameraNameList.add(cameraName);
//                        urlList.add(urlName);

                        //send to postNewCamera
                        postNewCamera(cameraName, urlName);

                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(getActivity(), "Cancel Clicked", Toast.LENGTH_SHORT).show();
                    }
                });


                AlertDialog dialog = builder.create();
                dialog.show();

            }
        });

        return view;
    }

    public void postNewCamera(final String cameraName, final String urlName){

        final String url = "http://ec2-54-242-89-175.compute-1.amazonaws.com:8000/api/camera/";
        StringRequest strReq = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject reader;
                try {

                    //TODO read cid,call adapter.add
                    reader = new JSONObject(response);
                    String cid = reader.getString("cid");
                    mAdapter.addCamera(cameraName, urlName, cid);
                    //notify adapter that the data changed
                    mAdapter.notifyDataSetChanged();
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
        MySingleton.getInstance(getActivity()).addToRequestQueue(strReq);
    }
    private void getCamerasRequest(){

//        progressDialog.setMessage("Logging in ...");
        //show dialog
//        showDialog();
        final String url = "http://ec2-54-242-89-175.compute-1.amazonaws.com:8000/api/camera/";
        StringRequest strReq = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONArray reader;
//                hideDialog();
                try {
                    reader = new JSONArray(response);
//                    {
//                        "cid": 4,
//                            "name": "hello",
//                            "address": "hello",
//                            "created_at": "2017-02-23T21:36:28Z",
//                            "is_active": true
//                    },

                    //TODO add time, day, address to camera name list
                    //put name of each camera name in an Arraylist
                    for(int i = 0; i < reader.length(); i++){
                        JSONObject jsonObject = reader.getJSONObject(i);
                        String cameraName = jsonObject.getString("name");
                        String urlName = jsonObject.getString("address");
                        String camerId = jsonObject.getString("cid");
                        cameraNameList.add(cameraName);
                        urlList.add(urlName);
                        cameraIdList.add(camerId);
                    }
                    mAdapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        },
                new Response.ErrorListener(){

                    @Override
                    public void onErrorResponse(VolleyError error) {
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
        };
        MySingleton.getInstance(getActivity().getApplicationContext()).addToRequestQueue(strReq);
    }
    @Override
    public void onListItemClicked(int indexClicked) {
        if(mToast != null){
            mToast.cancel();
        }

        //TODO send url
        Uri newVideoPath = Uri.parse(urlList.get(indexClicked));
        Intent intent = new Intent(Intent.ACTION_VIEW, newVideoPath);
        intent.setDataAndType(newVideoPath, "video/mjpeg");
        startActivity(intent);

//        Intent intent = new Intent(getActivity(), LiveActivityMainActivity.class);
//        intent.putExtra("Camera Name", urlList.get(indexClicked));
//        startActivity(intent);
    }

    private int requestCode = 1;
    @Override
    public void onItemLongClicked(final int pos) {
//        Intent intent = new Intent(getActivity(), NotificationActivity.class);
//        intent.putExtra("Camera Name", pos);
//        startActivityForResult(intent, requestCode);

        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View view = inflater.inflate(R.layout.camera_options_dialog,null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
        builder.setTitle(R.string.edit_options);
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        //set delete when textview is clicked
        TextView textView = (TextView) view.findViewById(R.id.delete_stream);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Verify that user wants to delete
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Are You sure?");
                builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteStream(pos);
                        Toast.makeText(getActivity(), "Delete Confirmed", Toast.LENGTH_SHORT).show();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
                Toast.makeText(getActivity(), "Delete Clicked", Toast.LENGTH_SHORT).show();
            }
        });

        //set edit when textview is clicked
        TextView textView2 = (TextView) view.findViewById(R.id.edit_stream);
        textView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "Edit Clicked", Toast.LENGTH_SHORT).show();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private void deleteStream(final int pos) {

        final String url = "http://ec2-54-242-89-175.compute-1.amazonaws.com:8000/api/camera/"+cameraIdList.get(pos)+"/";
        StringRequest strReq = new StringRequest(Request.Method.DELETE, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //update cameraAdapter
                mAdapter.deleteCamera(pos);
                mAdapter.notifyDataSetChanged();
            }
        },
                new Response.ErrorListener(){

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("Delete Error", "Could not delete camera");
                    }
                }
        ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("Authorization", LoginActivity.token);
                return map;
            }
        };
        MySingleton.getInstance(getActivity().getApplicationContext()).addToRequestQueue(strReq);
    }



    //will be called after NotificationActivity finishes
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //check wich activity for result called
        if(requestCode == this.requestCode){

            if(resultCode == Activity.RESULT_OK){
                //TODO Reset adapter so RecyclerView can have correct data
                //TODO empty adapter
                //TODO call getCamera request
                //TODO notify on changed adapter
                //get values from keys
                String time = data.getStringExtra("time_key");
                boolean days[] = data.getBooleanArrayExtra("days_key");
                Toast.makeText(getActivity(), Arrays.toString(days), Toast.LENGTH_LONG).show();
            }
            else if(resultCode == Activity.RESULT_CANCELED){
                Toast.makeText(getActivity(), "No notification change", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void showDialog() {
        if (!progressDialog.isShowing())
            progressDialog.show();
    }

    private void hideDialog() {
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }

    //innerClass Start
    private class LoadAwsTask extends AsyncTask<Void, Void, Void> {

        private AmazonSNSClient client; //provide credentials here
//        private String applicationArn = "arn:aws:sns:us-east-1:499555154246:app/GCM/testsns";
        private String applicationArn = "arn:aws:sns:us-east-1:397403242286:app/GCM/MotionDect";
        private String tokenInstance;
        private final String preferenceFile = "com.example.android.motiondetect.PreferenceFile";
        String endpointArn;

        @Override
        protected Void doInBackground(Void... voids) {
            // Initialize the Amazon Cognito credentials provider
            CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                    getActivity(),
                    "us-east-1:c49fe0ac-5660-4cda-b318-dbca755d05c1",
                    Regions.US_EAST_1 // Region
            );
//                    "us-east-1:844f80c7-2fbf-40b1-b7d4-472e608e3197",

            client = new AmazonSNSClient(credentialsProvider);
            registerWithSNS();

            return null;
        }

        public void registerWithSNS() {
//            storeEndpointArn(null);
            endpointArn = retrieveEndpointArn();
            tokenInstance = FirebaseInstanceId.getInstance().getToken();

            boolean updateNeeded = false;
            boolean createNeeded = (null == endpointArn);

            if (createNeeded) {
                // No platform endpoint ARN is stored; need to call createEndpoint.
                endpointArn = createEndpoint();
                createNeeded = false;
            }

            System.out.println("Retrieving platform endpoint data...");
            // Look up the platform endpoint and make sure the data in it is current, even if
            // it was just created.
            try {
                GetEndpointAttributesRequest geaReq =
                        new GetEndpointAttributesRequest()
                                .withEndpointArn(endpointArn);
                GetEndpointAttributesResult geaRes =
                        client.getEndpointAttributes(geaReq);

                updateNeeded = !geaRes.getAttributes().get("Token").equals(tokenInstance)
                        || !geaRes.getAttributes().get("Enabled").equalsIgnoreCase("true");

            } catch (NotFoundException nfe) {
                Log.i("Registrationnnn:", "Exception, cannot get token and enabled attributes");
                // We had a stored ARN, but the platform endpoint associated with it
                // disappeared. Recreate it.
                createNeeded = true;
            }

            if (createNeeded) {
                createEndpoint();
            }

            System.out.println("updateNeeded = " + updateNeeded);

            if (updateNeeded) {

                Log.i("Registrationnnn:", "UpdateNeeded");
                // The platform endpoint is out of sync with the current data;
                // update the token and enable it.
                System.out.println("Updating platform endpoint " + endpointArn);
                Map attribs = new HashMap();
                attribs.put("Token", tokenInstance);
                attribs.put("Enabled", "true");
                SetEndpointAttributesRequest saeReq =
                        new SetEndpointAttributesRequest()
                                .withEndpointArn(endpointArn)
                                .withAttributes(attribs);
                client.setEndpointAttributes(saeReq);
            }
        }

        /**
         * @return never null
         * */
        private String createEndpoint() {
            Log.i("Registrationnnn:", "CreateEndpointtt");
            endpointArn = null;
            try {
                System.out.println("Creating platform endpoint with token " + tokenInstance);
                CreatePlatformEndpointRequest cpeReq =
                        new CreatePlatformEndpointRequest()
                                .withPlatformApplicationArn(applicationArn)
                                .withToken(tokenInstance);
                CreatePlatformEndpointResult cpeRes = client
                        .createPlatformEndpoint(cpeReq);
                endpointArn = cpeRes.getEndpointArn();
            } catch (InvalidParameterException ipe) {
                String message = ipe.getErrorMessage();
                System.out.println("Exception message: " + message);
                Pattern p = Pattern
                        .compile(".*Endpoint (arn:aws:sns[^ ]+) already exists " +
                                "with the same token.*");
                Matcher m = p.matcher(message);
                if (m.matches()) {
                    // The platform endpoint already exists for this token, but with
                    // additional custom data that
                    // createEndpoint doesn't want to overwrite. Just use the
                    // existing platform endpoint.
                    endpointArn = m.group(1);
                } else {
                    // Rethrow the exception, the input is actually bad.
                    throw ipe;
                }
            }
            storeEndpointArn(endpointArn);
            return endpointArn;
        }

        /**
         * @return the ARN the app was registered under previously, or null if no
         *         platform endpoint ARN is stored.
         */
        private String retrieveEndpointArn() {
            // Retrieve the platform endpoint ARN from permanent storage,
            // or return null if null is stored.
            SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
            String defaultValue = null;
            String endpoint = sharedPref.getString(preferenceFile, defaultValue);

            Log.i("Registrationnnn:", "Retrieve Enpoint");
            return endpoint;
        }

        /**
         * Stores the platform endpoint ARN in permanent storage for lookup next time.
         * */
        private void storeEndpointArn(String endpointArn) {
            Log.i("Registrationnnn:", "Storeee Enpoint = ");

            // Write the platform endpoint ARN to permanent storage.
            SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(preferenceFile, endpointArn);
            editor.commit();

            //send to /api/endpoint/create/
            sendAndroidEndpoint();
        }

        private void sendAndroidEndpoint() {

            final String url = "http://ec2-54-242-89-175.compute-1.amazonaws.com:8000/api/endpoint/create/";
            StringRequest strReq = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.i("SendAndroidEndpoint Suc", response);
                }
            },
                    new Response.ErrorListener(){

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.i("SendAndroidEndpointttt", "Could not post endpoint to /endpoint/create/");
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
                    params.put("endpoint", endpointArn);
                    return params;
                }
            };
            MySingleton.getInstance(getActivity().getApplicationContext()).addToRequestQueue(strReq);
        }
    }
}
