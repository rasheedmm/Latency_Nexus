package com.live.latency_nexus;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;

import com.live.latency_nexus.Adapters.MyPostsAdapter;
import com.live.latency_nexus.Models.MyPosts;
import com.live.latency_nexus.Models.User;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;


public class ProfileFragment extends Fragment {
    ImageView image_profile;
    ImageView iv_logout;
    TextView posts, followers, tv_followes, tv_following, following, fullname, bio;
    TextView editprofile, share;
    FirebaseUser firebaseUser;
    String profileid;
    ImageView my_posts, my_orders;
    RecyclerView recyclerView;
   MyPostsAdapter userPostsAdapter;
    List<MyPosts> postsList;
    LottieAnimationView lottieAnimationView;
    private List<String> mySaves;

    List<MyPosts> postsList_Saves;
    String image_url, store_name, store_desc;
    lottiedialogfragment lottie;
    AppBarLayout appBarLayout;
    CoordinatorLayout background;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile_new_layout, container, false);


        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        lottieAnimationView = view.findViewById(R.id.lottie);
        profileid = firebaseUser.getUid();
        image_profile = view.findViewById(R.id.image_profile);
        lottie = new lottiedialogfragment(getContext());
        lottie.show();
        posts = view.findViewById(R.id.posts);
        tv_followes = view.findViewById(R.id.textView27);
        tv_following = view.findViewById(R.id.textView30);
        share = view.findViewById(R.id.img_share);
        followers = view.findViewById(R.id.followers);
        background = view.findViewById(R.id.background);
        following = view.findViewById(R.id.following);
        iv_logout = view.findViewById(R.id.btn_logout);
        fullname = view.findViewById(R.id.fullname);
        appBarLayout = view.findViewById(R.id.appbar);
        bio = view.findViewById(R.id.bio);
        editprofile = view.findViewById(R.id.edit_profile);
        my_posts = view.findViewById(R.id.my_posts);
        my_orders = view.findViewById(R.id.my_orders);
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new GridLayoutManager(getContext(), 1);
        recyclerView.setLayoutManager(linearLayoutManager);
        postsList = new ArrayList<>();
        userPostsAdapter = new MyPostsAdapter(getContext(), postsList);
        recyclerView.setAdapter(userPostsAdapter);
        userInfo();
        getFollowers();
        getNrPosts();
        userPosts();
        //  mySaves();

        image_profile.setClipToOutline(true);


        if (profileid.equals(firebaseUser.getUid())) {
            editprofile.setText("Edit Profile");
        } else {
            checkFollow();
            my_orders.setVisibility(View.GONE);
        }
        iv_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.AlertDialogTheme);

                builder.setMessage("Are you sure want to log out?");

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        GoogleSignInOptions gso = new GoogleSignInOptions.
                                Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).
                                build();

                        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(getContext(), gso);
                        googleSignInClient.signOut();
                        Intent intent = new Intent(getContext(), MainActivity.class);
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
        });
        my_posts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.setVisibility(View.VISIBLE);


            }
        });
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createlink(image_profile, image_url);
            }
        });
        my_orders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // Intent intent = new Intent(getContext(), CartActivity.class);
              //  startActivity(intent);
            }
        });
        editprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String btn = editprofile.getText().toString();
                if (editprofile.getText().toString().equals("Edit Profile")) {
                   // Intent intent = new Intent(getContext(), EditProfileActivity.class);
                    Pair[] pairs = new Pair[1];
                    pairs[0] = new Pair<View, String>(image_profile, "profile");
                    if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)) {
                        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity) getContext(), pairs);
                       // startActivity(intent, options.toBundle());
                    } else {
                      //  startActivity(intent);
                    }
                }
                //Editprofile
                else if (editprofile.getText().toString().equals("follow")) {
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                            .child("following").child(profileid).setValue(true);
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                            .child("followers").child(firebaseUser.getUid()).setValue(true);

                } else if (editprofile.getText().toString().equals("following")) {
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                            .child("following").child(profileid).removeValue();
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(profileid)
                            .child("followers").child(firebaseUser.getUid()).removeValue();

                }

            }
        });
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (Math.abs(verticalOffset) == appBarLayout.getTotalScrollRange()) {
                    background.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.white));
                } else if (verticalOffset == 0) {
                    background.setBackgroundResource(R.drawable.cstm_hmbg);
                } else {
                    background.setBackgroundResource(R.drawable.cstm_hmbg);
                }
            }
        });
        tv_following.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  Intent intent = new Intent(getContext(), FollowersActivity.class);
             //   intent.putExtra("id", profileid);
             //   intent.putExtra("title", "following");
               // startActivity(intent);
            }
        });

        tv_followes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // Intent intent = new Intent(getContext(), FollowersActivity.class);
               // intent.putExtra("id", profileid);
                //intent.putExtra("title", "followers");
                //startActivity(intent);
            }
        });
        following.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // Intent intent = new Intent(getContext(), FollowersActivity.class);
               //intent.putExtra("id", profileid);
                //intent.putExtra("title", "following");
               // startActivity(intent);
            }
        });

        followers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* Intent intent = new Intent(getContext(), FollowersActivity.class);
                intent.putExtra("id", profileid);
                intent.putExtra("title", "followers");
                startActivity(intent);*/
            }
        });
        return view;
    }

    private void userInfo() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(profileid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (getContext() == null) {
                    return;
                }
                User user = snapshot.getValue(User.class);
                Glide.with(getContext()).load(user.getImageurl()).into(image_profile);
                fullname.setText(user.getName());
                bio.setText(user.getBio());
                image_url = user.getImageurl();

                store_name = user.getName();
                store_desc = user.getBio();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void checkFollow() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(firebaseUser.getUid()).child("following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(profileid).exists()) {
                    editprofile.setText("Following");
                } else {
                    editprofile.setText("Follow");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getFollowers() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(profileid).child("followers");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                followers.setText("" + snapshot.getChildrenCount());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(profileid).child("following");
        reference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                following.setText("" + snapshot.getChildrenCount());


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    private void getNrPosts() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int i = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    MyPosts posts = snapshot.getValue(MyPosts.class);
                    if (snapshot.hasChild("publisher")) {


                        if (posts.getPublisher().equals(profileid)) {
                            i++;
                        }
                    }
                }
                posts.setText("" + i);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void userPosts() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postsList.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    MyPosts posts = snapshot1.getValue(MyPosts.class);
                    if (snapshot1.hasChild("publisher")) {
                        if (posts.getPublisher().equals(profileid)) {
                            postsList.add(posts);
                        }
                    }
                }
                Collections.reverse(postsList);
                userPostsAdapter.notifyDataSetChanged();
                lottie.dismiss();
                if (postsList.size() == 0) {
                    lottieAnimationView.setVisibility(View.VISIBLE);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void createlink(ImageView image_profile, String image_url) {
        Toast.makeText(getContext(), "Please wait generating link....", Toast.LENGTH_SHORT).show();
        share.setVisibility(View.GONE);
        DynamicLink dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse("https://www.malluhub.in/"))
                .setDynamicLinkDomain("quickart.page.link")
                // Open links with this app on Androidv
                .setAndroidParameters(new DynamicLink.AndroidParameters.Builder().build())
                // Open links with com.example.ios on iOS
                .setIosParameters(new DynamicLink.IosParameters.Builder("com.example.ios").build())
                .buildDynamicLink();

        Uri dynamicLinkUri = dynamicLink.getUri();


        createLink(firebaseUser.getUid(), "null", image_profile, image_url);

    }

    public void createLink(String custid, String prodid, ImageView image_profile1, String image_url1) {
        String sharelinktext = "https://quickart.page.link/?" +
                "link=http://www.malluhub.tk/myrefer.php?custid=" + custid + "-" + prodid +
                "&apn=" + getActivity().getPackageName() +
                "&st=" + store_name +
                "&sd=" + store_desc +
                "&si=" + image_url;


        Task<ShortDynamicLink> shortLinkTask = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLongLink(Uri.parse(sharelinktext))
                .buildShortDynamicLink()
                .addOnCompleteListener((Activity) getContext(), new OnCompleteListener<ShortDynamicLink>() {
                    @Override
                    public void onComplete(@NonNull Task<ShortDynamicLink> task) {
                        if (task.isSuccessful()) {
                            // Short link created
                            Uri shortLink = task.getResult().getShortLink();
                            Uri flowchartLink = task.getResult().getPreviewLink();
                            BitmapDrawable drawable = (BitmapDrawable) image_profile1.getDrawable();
                            Bitmap bitmap = drawable.getBitmap();
                            String bitmappath = MediaStore.Images.Media.insertImage(getContext().getApplicationContext().getContentResolver(), bitmap
                                    ,  "IMG_" + System.currentTimeMillis(), "desc");

                            Uri uri = Uri.parse(bitmappath);

                            Intent intent = new Intent(Intent.ACTION_SEND);
                            intent.setType("image/png");
                            intent.putExtra(Intent.EXTRA_STREAM, uri);

                            intent.putExtra(Intent.EXTRA_TEXT, "*"+store_name+"*"+"\nYour orders will arrive your doorsteps.Visit our online store now.\n" + shortLink.toString());

                            startActivity(Intent.createChooser(intent, "share"));
                            share.setVisibility(View.VISIBLE);
                        } else {
                            Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}