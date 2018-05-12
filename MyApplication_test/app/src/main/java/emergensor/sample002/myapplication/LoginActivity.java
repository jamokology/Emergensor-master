//友達サーチ
//通知機能

//name the specific LoginActivity
package emergensor.sample002.myapplication;

//import: doesnt have to write the long name because of this

//180322addforFacebooklogin/Shohei
//import com.facebook.FacebookSdk;
//import com.facebook.appevents.AppEventsLogger;

//17/12/05追記＆コメントアウト

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.app.AlertDialog;
import android.os.Bundle;

//180324addforRadioButton/Shohei
import android.util.Log;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.RadioGroup.OnCheckedChangeListener;


import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

import emergensor.sample002.myapplication.block.BlockBuilder;
import emergensor.sample002.myapplication.block.Message;
import emergensor.sample002.myapplication.block.drain.SimpleVectorConcatenateDrain;
import emergensor.sample002.myapplication.block.filter.BufferFilter;
import emergensor.sample002.myapplication.block.filter.FunctionFilter;
import emergensor.sample002.myapplication.block.filter.VectorPeriodicSampleFilter;
import emergensor.sample002.myapplication.block.sink.URLSenderSink;
import emergensor.sample002.myapplication.block.source.AccelerationSensorSource;
import emergensor.sample002.myapplication.block.source.IntervalSource;
import emergensor.sample002.myapplication.block.source.LocationSensorSource;
import emergensor.sample002.myapplication.functions.ComplexAbsoluteFunction;
import emergensor.sample002.myapplication.functions.FFTFunction;
import emergensor.sample002.myapplication.functions.HanningWindowFunction;
import emergensor.sample002.myapplication.functions.MapFunctionWrapper;
import emergensor.sample002.myapplication.functions.MeanFunction;
import emergensor.sample002.myapplication.functions.MessageFunctionWrapper;
import emergensor.sample002.myapplication.functions.NormFunction;
import emergensor.sample002.myapplication.functions.PassFrequencyFunction;
import emergensor.sample002.myapplication.functions.VarianceFunction;
import emergensor.sample002.myapplication.lib.Consumer;
import emergensor.sample002.myapplication.lib.Function;
import emergensor.sample002.myapplication.lib.Utils;
import emergensor.sample002.myapplication.lib.Vector;

//import static: field and method without the name of the class
import static emergensor.sample002.myapplication.block.BlockBuilder.build;

//17/12/05追記＆コメントアウト //180324EditforButton/Shohei
public class LoginActivity extends AppCompatActivity {

    public static String name;

    //180331AddforFBlogin/Shohei
    private CallbackManager callbackManager;


    //180331AddforFBlogin/Shohei
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //log
        Log.i("test", "test");

        try {

            FacebookSdk.sdkInitialize(getApplicationContext());
            AppEventsLogger.activateApp(this);

            //180331AddforFBlogin/Shohei
            callbackManager = CallbackManager.Factory.create();

            LoginActivity.name = "None";

            LoginManager.getInstance().logOut();
            LoginManager.getInstance().registerCallback(callbackManager,
                    new FacebookCallback<LoginResult>() {
                        @Override
                        public void onSuccess(LoginResult loginResult) {
                            // App code
                            Log.i("test", "Success" + loginResult);

                            {
                                GraphRequest request = GraphRequest.newGraphPathRequest(
                                        AccessToken.getCurrentAccessToken(),
                                        "/me",
                                        new GraphRequest.Callback() {
                                            @Override
                                            public void onCompleted(GraphResponse response) {
                                                // Insert your code here

                                                //180409forJASONtoHandleMemberlist/Shohei
                                                ObjectMapper mapper = new ObjectMapper();
                                                try {
                                                    JsonNode node = mapper.readTree(response.getRawResponse());

                                                    String name = node.get("name").asText();
                                                    LoginActivity.name = name;

                                                    Intent intent = new Intent(LoginActivity.this, MapActivity.class);
                                                    startActivity(intent);
                                                    finish();

                                                } catch (JsonParseException e) {
                                                    Log.i("test", "", e);
                                                } catch (NullPointerException e) {
                                                    Log.i("test", "", e);
                                                } catch (IOException e) {
                                                    Log.i("test", "", e);
                                                }

                                            }

                                        });

                                Bundle parameters = new Bundle();
                                parameters.putString("fields", "name");
                                request.setParameters(parameters);

                                request.executeAsync();
                            }
                        }

                        @Override
                        public void onCancel() {
                            // App code
                            Log.i("test", "Cancel");

                            Intent intent = new Intent(LoginActivity.this, MapActivity.class);
                            startActivity(intent);
                            finish();
                        }

                        @Override
                        public void onError(FacebookException exception) {
                            // App code
                            Log.i("test", "Error");
                        }
                    });

/*
            ProfileTracker profileTracker = new ProfileTracker() {
                @Override
                protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                    //updateUI();
                    // It's possible that we were waiting for Profile to be populated in order to
                    // post a status update.
                    //handlePendingAction();
                    Log.i("test", currentProfile.getFirstName());
                }
            };
*/
             LoginManager.getInstance().logInWithPublishPermissions(this, Arrays.<String>asList());
  //          LoginManager.getInstance().logInWithReadPermissions(this, Arrays.<String>asList("user_friends"));
        } catch (Exception e) {
            Log.i("test", "", e);
        }
    }

}
