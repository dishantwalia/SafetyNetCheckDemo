package com.dishant.safetynetdemo;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dishant.safetynetdemo.model.JWS;
import com.dishant.safetynetdemo.model.JWSRequest;
import com.dishant.safetynetdemo.model.Response;
import com.dishant.safetynetdemo.network.RetrofitInterface;
import com.dishant.safetynetdemo.utilities.Util;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.safetynet.SafetyNet;
import com.google.android.gms.safetynet.SafetyNetApi;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by punchh_dishant on 19,June,2018
 */
public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks {


    private GoogleApiClient mGoogleApiClient;
    private ProgressBar mProgress;
    private TextView txtStatus;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mProgress = findViewById(R.id.progress);
        txtStatus = findViewById(R.id.txt_status);
        initClient();

    }


    private void initClient() {
        mProgress.setVisibility(View.VISIBLE);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(SafetyNet.API)
                .addConnectionCallbacks(this)
                .build();

        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        startVerification();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    private void startVerification() {

        final byte[] nonce = getRequestNonce();

        SafetyNet.getClient(this).attest(nonce, getString(R.string.api_key))
                .addOnSuccessListener(this,
                        new OnSuccessListener<SafetyNetApi.AttestationResponse>() {
                            @Override
                            public void onSuccess(SafetyNetApi.AttestationResponse response) {
                                // Indicates communication with the service was successful.
                                // Use response.getJwsResult() to get the result data.
                                String jwsResult = response.getJwsResult();
                                verifyOnline(jwsResult);
                            }
                        })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        mProgress.setVisibility(View.GONE);
                        // An error occurred while communicating with the service.
                        String error;
                        if (e instanceof ApiException) {
                            // An error with the Google Play services API contains some
                            // additional details.
                            ApiException apiException = (ApiException) e;
                            // You can retrieve the status code using the
                            // apiException.getStatusCode() method.
                            error = apiException.getLocalizedMessage();
                        } else {
                            // A different, unknown type of error occurred.
                            error = e.getLocalizedMessage();
                        }
                        Util.showAlert(MainActivity.this, "Verification", "Unable to perform operation due to :" + error, "Okay",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        finish();
                                    }
                                }, new Dialog.OnKeyListener() {
                                    @Override
                                    public boolean onKey(DialogInterface arg0, int keyCode, KeyEvent event) {
                                        if (keyCode == KeyEvent.KEYCODE_BACK) {
                                        }
                                        return true;
                                    }
                                });
                    }
                });


    }

    private void verifyOnline(final String jws) {

        Retrofit retrofit = null;
        try {
            retrofit = new Retrofit.Builder()
                    .baseUrl(getString(R.string.base_api_url))
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            RetrofitInterface retrofitInterface = retrofit.create(RetrofitInterface.class);

            JWSRequest jwsRequest = new JWSRequest();
            jwsRequest.setSignedAttestation(jws);
            Call<Response> responseCall = retrofitInterface.getResult(jwsRequest, getString(R.string.api_key));

            responseCall.enqueue(new Callback<Response>() {
                @Override
                public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {

                    boolean result = response.body().isValidSignature();

                    if (result) {

                        decodeJWS(jws);

                    } else {

                        Util.showAlert(MainActivity.this, "Verification Error!", "Unable to perform operation due to invalid signature", "Okay",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        finish();
                                    }
                                }, new Dialog.OnKeyListener() {
                                    @Override
                                    public boolean onKey(DialogInterface arg0, int keyCode, KeyEvent event) {
                                        if (keyCode == KeyEvent.KEYCODE_BACK) {
                                        }
                                        return true;
                                    }
                                });
                    }

                }

                @Override
                public void onFailure(Call<Response> call, Throwable t) {
                    mProgress.setVisibility(View.GONE);
                    Util.showAlert(MainActivity.this, "Verification", "Something went wrong : " + t.getLocalizedMessage(), "Okay",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    finish();
                                }
                            }, new Dialog.OnKeyListener() {
                                @Override
                                public boolean onKey(DialogInterface arg0, int keyCode, KeyEvent event) {
                                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                                    }
                                    return true;
                                }
                            });
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void decodeJWS(String jwsString) {
        byte[] json = Base64.decode(jwsString.split("[.]")[1], Base64.DEFAULT);
        String text = new String(json, StandardCharsets.UTF_8);
        Gson gson = new Gson();
        JWS jws = gson.fromJson(text, JWS.class);
        displayResults(jws.isBasicIntegrity(), jws.isCtsProfileMatch());
    }

    private byte[] getRequestNonce() {

        String data = String.valueOf(System.currentTimeMillis());

        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        byte[] bytes = new byte[24];
        Random random = new Random();
        random.nextBytes(bytes);
        try {
            byteStream.write(bytes);
            byteStream.write(data.getBytes());
        } catch (IOException e) {
            return null;
        }

        return byteStream.toByteArray();
    }

    private void displayResults(boolean integrity, boolean cts) {
        mProgress.setVisibility(View.GONE);
        if (integrity && cts) {
            txtStatus.setVisibility(View.VISIBLE);
            Util.showAlert(MainActivity.this, "Verification", "Your Device is verified", "Okay",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }, new Dialog.OnKeyListener() {
                        @Override
                        public boolean onKey(DialogInterface arg0, int keyCode, KeyEvent event) {
                            if (keyCode == KeyEvent.KEYCODE_BACK) {
                            }
                            return true;
                        }
                    });
        } else {
            Util.showAlert(MainActivity.this, "Verification", "Your device compatibility test check failed, Probably your device is tampered", "Okay",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            finish();
                        }
                    }, new Dialog.OnKeyListener() {
                        @Override
                        public boolean onKey(DialogInterface arg0, int keyCode, KeyEvent event) {
                            if (keyCode == KeyEvent.KEYCODE_BACK) {
                            }
                            return true;
                        }
                    });
        }
    }

}
