package com.live.latency_nexus;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.SlidingDrawer;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.live.latency_nexus.Adapters.DrawerAdapter;

import java.util.Arrays;

public class HomeActivity extends AppCompatActivity implements DrawerAdapter.OnItemSelectedListener {
    ImageView view_menu;
    Dialog dialog;
    private static final int POS_CLOSE = 0;
    private static final int POS_HOME = 1;
    private static final int POS_ORDERS = 2;
    private static final int POS_DISCLAIMER = 3;
    private static final int POS_CONTACT_US = 4;
    private static final int POS_ABOUT = 5;
    private static final int POS_RATE_US = 6;
    private static final int POS_ONLINE_PAY = 7;
    private static final int POS_SHARE = 8;
    private static final int POS_LOGOUT = 10;
    Fragment selectedFragment = null;

    private String[] screenTitles;
    private Drawable[] screenIcons;


    String prdid,cstid;
    private final int UPDATE_REQUEST_CODE = 1612;
    private final BottomNavigationView.OnNavigationItemSelectedListener navlistener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId()) {
                case R.id.item1:
                    selectedFragment = new HomeFragment();
                    break;

                case R.id.item2:
                    selectedFragment = new SearchFragment();

                    break;
                case R.id.item3:
                Intent intent=new Intent(HomeActivity.this,AddItemActivity.class);
                    startActivity(intent);
                    break;

                case R.id.item4:
                    selectedFragment = new MyPostsOrders();
                    break;
                case R.id.item5:
                    selectedFragment = new ProfileFragment();
                    break;


            }
            if (!(selectedFragment == null)) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_layout, selectedFragment).commit();


            }
            return true;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);



        Intent intent;
        intent = getIntent();
        prdid = intent.getStringExtra("prdid");
        cstid = intent.getStringExtra("profileid");










        BottomNavigationView btnNav = findViewById(R.id.bottomNavView);
        btnNav.setOnNavigationItemSelectedListener(navlistener);
        dialog = new Dialog(this);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_layout, new HomeFragment()).commit();






    }

  /*  public void Showpopup() {

        TextView ok;
        TextView txt_vacancy, txt_image;

        dialog.setContentView(R.layout.custom_popuo_add);

        ok = dialog.findViewById(R.id.ok_btn);
        txt_vacancy = dialog.findViewById(R.id.txt_vacancy);
        txt_image = dialog.findViewById(R.id.txt_image);
        txt_vacancy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, JobUploadFirstActivity.class);
                startActivity(intent);

            }
        });
        txt_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, PostActivity.class);
                startActivity(intent);
            }
        });
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });


        dialog.show();


    }*/





    @Override
    public void onBackPressed() {

        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this,R.style.AlertDialogTheme);

        builder.setMessage("Are you sure want to Exit");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                finish();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();


    }





    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == UPDATE_REQUEST_CODE) {

            if (resultCode != RESULT_OK) {

                // If the update is cancelled or fails,
                // you can request to start the update again.
            }
        }
    }




    @ColorInt
    private int color(@ColorRes int res) {
        return ContextCompat.getColor(this, res);
    }

    private String[] loadScreenTitles() {
        return getResources().getStringArray(R.array.id_activityScreenTitles);


    }



    private Drawable[] loadScreeenIcons() {
        TypedArray ta = getResources().obtainTypedArray(R.array.id_activityScreenIcons);
        Drawable[] icons = new Drawable[ta.length()];
        for (int i = 0; i < ta.length(); i++) {
            int id = ta.getResourceId(i, 0);
            if (id != 0) {
                icons[i] = ContextCompat.getDrawable(this, id);
            }
        }
        ta.recycle();
        return icons;
    }

    @Override
    public void onItemSelected(int position) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if (position == POS_HOME) {

        }
        if (position == POS_ORDERS) {

           /* Intent intent = new Intent(HomeActivity.this, MainOrdersActivity.class);
            startActivity(intent);*/
        }
        if (position == POS_ABOUT) {

           /* Intent intent = new Intent(HomeActivity.this, TermsMainActivity.class);
            startActivity(intent);*/
        }
        if (position == POS_DISCLAIMER) {


           /* Intent intent = new Intent(HomeActivity.this, PrivacyPolicyActivity.class);
            startActivity(intent);*/
        }
        if (position == POS_ONLINE_PAY) {
            Toast.makeText(this, "Coming Soon...", Toast.LENGTH_SHORT).show();

        }
        if (position == POS_SHARE) {

            try {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Quickart");
                String shareMessage = "\nLet me recommend you this application for creating online stores instantly for free,get your orders in your fingertips.also buy products in wholesale price\n\n";
                shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID + "\n\n";
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                startActivity(Intent.createChooser(shareIntent, "choose one"));
            } catch (Exception e) {
                //e.toString();
            }

        }

        if (position == POS_CONTACT_US) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            String[] recipients = {"quickart567@gmail.com"};
            intent.putExtra(Intent.EXTRA_EMAIL, recipients);
            intent.putExtra(Intent.EXTRA_SUBJECT, "Quickart");
            intent.putExtra(Intent.EXTRA_TEXT, "");

            intent.setType("text/html");
            intent.setPackage("com.google.android.gm");
            startActivity(Intent.createChooser(intent, "Send mail"));
        }
        if (position == POS_RATE_US) {
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + HomeActivity.this.getPackageName())));
            } catch (ActivityNotFoundException e) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + HomeActivity.this.getPackageName())));
            }
        }

        if (position == POS_LOGOUT) {
            AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);

            builder.setMessage("Are you sure want to log out?");

            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    GoogleSignInOptions gso = new GoogleSignInOptions.
                            Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).
                            build();

                    GoogleSignInClient googleSignInClient= GoogleSignIn.getClient(HomeActivity.this,gso);
                    googleSignInClient.signOut();
                    Intent intent = new Intent(HomeActivity.this,MainActivity.class);
                    startActivity(intent);
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();


        }


    }


}