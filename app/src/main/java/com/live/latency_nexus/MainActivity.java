package com.live.latency_nexus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity  {

    private static final int RC_SIGN_IN = 1;
    private static final String TAG = "GOOGLEAUTH";
    GoogleSignInClient googleSignInClient;
    private FirebaseAuth mAuth;
    FirebaseUser firebaseUser;
    CheckBox checkBox;
    TextView tv_tc;
    lottiedialogfragment lottie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        mAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);


        TextView signInBtn = findViewById(R.id.google_signIn);
        tv_tc = findViewById(R.id.texttc);
        checkBox = findViewById(R.id.checkBox);

        tv_tc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              /*  Intent i = new Intent(MainActivity.this, TermsMainActivity.class);
                startActivity(i);*/
            }
        });
        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkBox.isChecked()) {
                    if (isOnline()) {

                        signIn();


                    } else {

                    }
                    ;
                } else {
                    Toast.makeText(MainActivity.this, "Please accept terms and conditions to continue", Toast.LENGTH_SHORT).show();
                }

            }
        });
        if (isOnline()) {
            if (GoogleSignIn.getLastSignedInAccount(MainActivity.this) != null) {


                // Enable verbose OneSignal logging to debug issues if needed.
                FirebaseDynamicLinks.getInstance()
                        .getDynamicLink(getIntent())
                        .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                            @Override
                            public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                                // Get deep link from result (may be null if no link is found)
                                Uri deepLink = null;
                                if (pendingDynamicLinkData != null) {
                                    deepLink = pendingDynamicLinkData.getLink();
                                    String referlink = deepLink.toString();
                                    try {

                                        referlink = referlink.substring(referlink.lastIndexOf("=") + 1);

                                        String custid = referlink.substring(0, referlink.indexOf("-"));
                                        String prodid = referlink.substring(referlink.indexOf("-") + 1);

                                        if (!custid.equals("null")) {


                                           /* Intent intentuser = new Intent(MainActivity.this, UserProfileActivity.class);
                                            intentuser.putExtra("profileid", custid);
                                            startActivity(intentuser);
                                            finish();*/

                                        } else {


                                            /*Intent intent = new Intent(MainActivity.this, ProductDetailsActivity.class);
                                            intent.putExtra("prdid", prodid);
                                            startActivity(intent);
                                            finish();*/
                                        }

                                    } catch (Exception e) {
                                        Toast.makeText(MainActivity.this, "Something gone wrong", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Intent i = new Intent(MainActivity.this, HomeActivity.class);
                                    startActivity(i);
                                    finish();
                                }

                                // Handle the deep link. For example, open the linked
                                // content, or apply promotional credit to the user's
                                // account.
                                // ...

                                // ...
                            }
                        })
                        .addOnFailureListener(this, new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MainActivity.this, "no link", Toast.LENGTH_SHORT).show();
                            }
                        });




            }
        } else {
           /* Intent mainIntent = new Intent(MainActivity.this, NoNetworkActivity.class);
            MainActivity.this.startActivity(mainIntent);
            MainActivity.this.finish();*/
        }


    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {

            lottie = new lottiedialogfragment(MainActivity.this);
            lottie.show();
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                // Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately

                Log.w(TAG, "Google sign in failed", e);
                lottie.dismiss();
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");

                            FirebaseUser user = mAuth.getCurrentUser();
                            adddata();

                            // updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            // Log.w(TAG, "signInWithCredential:failure", task.getException());
                            //nackbar.make(mBinding.mainLayout, "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            // updateUI(null);
                            lottie.dismiss();
                            Toast.makeText(MainActivity.this, "Login failed", Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }

    private void adddata() {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseUser.getUid());

        HashMap<String, Object> hashMap = new HashMap<>();


        hashMap.put("id", firebaseUser.getUid());
        hashMap.put("imageurl", firebaseUser.getPhotoUrl().toString());
        hashMap.put("username", firebaseUser.getDisplayName().toLowerCase());
        hashMap.put("name", firebaseUser.getDisplayName());
        hashMap.put("bio", "Nexus User");
        hashMap.put("usertype", false);
        hashMap.put("lati", "0");
        hashMap.put("longi", "0");
        hashMap.put("location", "");
        hashMap.put("userposts", "0");



        databaseReference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    lottie.dismiss();
                    Toast.makeText(MainActivity.this, "Data updated,you can edit it later", Toast.LENGTH_SHORT).show();
                  Intent i = new Intent(getApplicationContext(), HomeActivity.class);
                    startActivity(i);
                    finish();
                }
            }
        });
    }


    public boolean isOnline() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    private void signIn() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
}