package com.example.android.motiondetect;

import android.util.Log;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.CreatePlatformEndpointRequest;
import com.amazonaws.services.sns.model.CreatePlatformEndpointResult;
import com.amazonaws.services.sns.model.GetEndpointAttributesRequest;
import com.amazonaws.services.sns.model.GetEndpointAttributesResult;
import com.amazonaws.services.sns.model.InvalidParameterException;
import com.amazonaws.services.sns.model.NotFoundException;
import com.amazonaws.services.sns.model.SetEndpointAttributesRequest;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by alex on 3/31/2017.
 */

public class Registration {

    private AmazonSNSClient client; //provide credentials here
    private String applicationArn = "arn:aws:sns:us-east-1:499555154246:app/GCM/testsns";
    private String arnStorage;
    private String tokenInstance;
    private final String preferenceFile = "com.example.android.motiondetect.PreferenceFile";

    //constructor that receives cognito credentials
    public Registration(CognitoCachingCredentialsProvider credentials){
            client = new AmazonSNSClient(credentials);
    }

    public void registerWithSNS() {

//        CreatePlatformApplicationRequest createPlatformApplicationRequest = new CreatePlatformApplicationRequest().withPlatform("GCM");
//        CreatePlatformApplicationResult createPlatformApplicationResult = createPlatformApplicationRequest.getPlatform();
        String endpointArn = retrieveEndpointArn();
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
        String endpointArn = null;
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
        Log.i("Registrationnnn:", "Retrieve Enpoint");
        return "arn:aws:sns:us-east-1:499555154246:endpoint/GCM/testsns/aff227a7-08d5-3438-b424-b996a54452f6";
    }

    /**
     * Stores the platform endpoint ARN in permanent storage for lookup next time.
     * */
    private void storeEndpointArn(String endpointArn) {
        Log.i("Registrationnnn:", "Storeee Enpoint = " + endpointArn);

        // Write the platform endpoint ARN to permanent storage.
        arnStorage = "arn:aws:sns:us-east-1:499555154246:endpoint/GCM/testsns/aff227a7-08d5-3438-b424-b996a54452f6";

//        SharedPreferences sharedPref = .getPreferences(Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPref.edit();
//        editor.putInt(getString(R.string.saved_high_score), newHighScore);
//        editor.commit();
    }
}