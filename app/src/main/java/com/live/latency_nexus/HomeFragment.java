package com.live.latency_nexus;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.appbar.AppBarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;
import com.live.latency_nexus.Adapters.NearbyStoresAdapter;
import com.live.latency_nexus.Adapters.PostAdapter;
import com.live.latency_nexus.Models.MyPosts;
import com.live.latency_nexus.Models.MyValues;
import com.live.latency_nexus.Models.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class HomeFragment extends Fragment {

    private PostAdapter postAdapter;
    private RecyclerView recyclerView;
    private List<MyPosts> postLists;
    private EditText ed_search;
    private ImageView btn_search, btn_chat, view_menu, view_search, view_chats;
    private final static int AUTOCOMPLETE_REQUEST_CODE = 1;
    FirebaseUser firebaseUser;
    String location;
    private NearbyStoresAdapter nearbyStoresAdapter;
    private RecyclerView recyclerView_nearby;
    private List<User> usertLists;
    int limit, posts_limit;
    lottiedialogfragment lottie;

    TextView tv_near, tv_recmd, tv_popular;
    double distance;
    Query mdatabaseReference;
    String intrests;
    TextView username;
    double lati1, lati2, longi1, longi2;
    AppBarLayout appBarLayout;
    CoordinatorLayout background;
    List<String> followingList;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);


        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView_nearby = view.findViewById(R.id.recycler_view_nearby);
        ed_search = view.findViewById(R.id.ed_search);
        btn_search = view.findViewById(R.id.img_search);
        btn_chat = view.findViewById(R.id.img_chat);
        view_search = view.findViewById(R.id.btn_search);
        username = view.findViewById(R.id.textView);
        view_chats = view.findViewById(R.id.btn_chat);
        view_menu = view.findViewById(R.id.menuicon);
        tv_near = view.findViewById(R.id.textView16);
        tv_recmd = view.findViewById(R.id.textView24);
        tv_popular = view.findViewById(R.id.textView25);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        appBarLayout = view.findViewById(R.id.appbar);
        background = view.findViewById(R.id.background);


        adddata();
        getNrPosts();
        recyclerView.setHasFixedSize(true);
        recyclerView_nearby.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        postLists = new ArrayList<>();
        postAdapter = new PostAdapter(getContext(), postLists);
        recyclerView.setAdapter(postAdapter);
        lottie = new lottiedialogfragment(getContext());
        lottie.show();
        checkFollowing();
        readPosts();
        LinearLayoutManager linearLayoutManagernearby = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, true);

        linearLayoutManagernearby.setStackFromEnd(true);
        recyclerView_nearby.setLayoutManager(linearLayoutManagernearby);
        usertLists = new ArrayList<>();

        nearbyStoresAdapter = new NearbyStoresAdapter(getContext(), usertLists);
        recyclerView_nearby.setAdapter(nearbyStoresAdapter);
        tv_near.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.tranceperent));
        tv_near.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
        tv_recmd.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.tranceperent));
        tv_recmd.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
        tv_popular.setBackgroundResource(R.drawable.btn_back);
        tv_popular.setTextColor(ContextCompat.getColor(getContext(), R.color.white));


        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Myvalues").child("followers");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                MyValues values = snapshot.getValue(MyValues.class);
                if (snapshot.hasChild("count")) {
                    limit = values.getCount();
                    addPopular();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        databaseReference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User values = snapshot.getValue(User.class);
                if (snapshot.hasChild("username") || snapshot.hasChild("id")) {
                    username.setText("Hi," + values.getUsername());

                } else {
                    Toast.makeText(getContext(), "Something went wrong please sign in again", Toast.LENGTH_SHORT).show();
                    GoogleSignInOptions gso = new GoogleSignInOptions.
                            Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).
                            build();

                    GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(getContext(), gso);
                    googleSignInClient.signOut();
                    Intent intent = new Intent(getContext(), MainActivity.class);
                    startActivity(intent);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        btn_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    /*11 startActivity(new Intent(getContext(), ChatListsActivity.class));*/
                } catch (Exception e) {
                    Toast.makeText(getContext(), "Something went wrong,try again", Toast.LENGTH_SHORT).show();
                }


            }
        });
        view_chats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


              // 22 startActivity(new Intent(getContext(), MainActivity2.class));


            }
        });
        view_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               /* 33Intent intent = new Intent(getContext(), SearchActivity.class);
                intent.putExtra("text", "");
                startActivity(intent);*/

            }
        });
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ed_search.getText().toString().equals("")) {
                    Toast.makeText(getContext(), "Empty keyword", Toast.LENGTH_SHORT).show();
                } else {
                    /**44Intent intent = new Intent(getContext(), SearchActivity.class);
                    intent.putExtra("text", ed_search.getText().toString());
                    startActivity(intent);*/
                }
            }
        });
        view_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //((HomeActivity) getActivity()).slidingRootNav.openMenu();
            }
        });

        tv_recmd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_near.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.tranceperent));
                tv_near.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
                tv_recmd.setBackgroundResource(R.drawable.btn_back);
                tv_recmd.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                tv_popular.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.tranceperent));
                tv_popular.setTextColor(ContextCompat.getColor(getContext(), R.color.black));

                addRecmnd();
            }
        });
        tv_near.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_recmd.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.tranceperent));
                tv_recmd.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
                tv_near.setBackgroundResource(R.drawable.btn_back);
                tv_near.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                tv_popular.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.tranceperent));
                tv_popular.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Myvalues").child("followers");
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        MyValues values = snapshot.getValue(MyValues.class);
                        if (snapshot.hasChild("posts")) {
                            posts_limit = values.getPosts();
                            checklocation();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


            }
        });
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (Math.abs(verticalOffset) == appBarLayout.getTotalScrollRange()) {
                    view_chats.setVisibility(View.VISIBLE);
                    view_search.setVisibility(View.VISIBLE);
                } else if (verticalOffset == 0) {
                    view_chats.setVisibility(View.GONE);
                    view_search.setVisibility(View.GONE);
                } else {
                    view_chats.setVisibility(View.GONE);
                    view_search.setVisibility(View.GONE);
                }
            }
        });

        tv_popular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_near.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.tranceperent));
                tv_near.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
                tv_recmd.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.tranceperent));
                tv_recmd.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
                tv_popular.setBackgroundResource(R.drawable.btn_back);
                tv_popular.setTextColor(ContextCompat.getColor(getContext(), R.color.white));


                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Myvalues").child("followers");
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        MyValues values = snapshot.getValue(MyValues.class);
                        if (snapshot.hasChild("count")) {
                            limit = values.getCount();
                            addPopular();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });
        return view;
    }


    public void adddata() {

        DatabaseReference referenceadd = FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(firebaseUser.getUid()).child("followers");
        referenceadd.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshotfl) {

                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
                HashMap<String, Object> hashMap = new HashMap<>();

                hashMap.put("popular", "" + snapshotfl.getChildrenCount());
                reference.updateChildren(hashMap);
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
                        if (posts.getPublisher().equals(firebaseUser.getUid())) {
                            i++;
                        }
                    }

                }
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
                HashMap<String, Object> hashMap = new HashMap<>();

                hashMap.put("userposts", "" + i);
                reference.updateChildren(hashMap);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void checklocation() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (getContext() == null) {
                    return;
                }
                User user = snapshot.getValue(User.class);
                //  Glide.with(getContext()).load(user.getImageurl()).into(image_profile);
                if (snapshot.hasChild("lati")) {
                    if (!user.getLati().equals("0")) {
                        lati1 = Double.parseDouble(user.getLati());
                        longi1 = Double.parseDouble(user.getLongi());
                        addNearby();

                    } else {
                        Places.initialize(getContext(), "AIzaSyDMjeI2Nwnnt6Nldp910f7BE1OdjqEtp2I" +
                                "" +
                                "");
                        addLocation();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void checkFollowing() {
        followingList = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Follow")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                followingList.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    followingList.add(snapshot1.getKey());
                    readPosts();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readPosts() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                postLists.clear();
                for (DataSnapshot snapshot : datasnapshot.getChildren()) {
                    MyPosts post = snapshot.getValue(MyPosts.class);
                    if (snapshot.hasChild("publisher")) {
                        for (String id : followingList) {
                            if (post.getPublisher().equals(id)) {
                                postLists.add(post);
                            }

                        }
                    }
                }
                if (postLists.size() == 0) {

                    for (DataSnapshot snapshot1 : datasnapshot.getChildren()) {
                        MyPosts post1 = snapshot1.getValue(MyPosts.class);

                        int mrp = Integer.parseInt(post1.getMrp());
                        int price = Integer.parseInt(post1.getOffer());
                        int subtract = mrp - price;
                        double result = ((double) subtract / mrp) * 100;

                        if (result > 20) {
                            postLists.add(post1);
                        }


                    }
                }
                postAdapter.notifyDataSetChanged();
                lottie.dismiss();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                Place place = Autocomplete.getPlaceFromIntent(data);
                String sSource = String.valueOf(place.getLatLng());
                sSource = sSource.replaceAll("lat/lng:", "");
                sSource = sSource.replace("(", "");
                sSource = sSource.replace(")", "");
                String[] split = sSource.split(",");
                lati1 = Double.parseDouble(split[0]);
                longi1 = Double.parseDouble(split[1]);
                location = place.getName();

                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("lati", String.valueOf(lati1));
                hashMap.put("longi", String.valueOf(longi1));
                hashMap.put("location", location);
                reference.updateChildren(hashMap);
                checklocation();
                Toast.makeText(getContext(), "Location Updated", Toast.LENGTH_SHORT).show();

            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);

                Toast.makeText(getContext(), "Try agin later", Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    void addNearby() {

        mdatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users").orderByChild("userposts");


        mdatabaseReference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                usertLists.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                    User upload = postSnapshot.getValue(User.class);

                    double lati2 = new Double(upload.getLati());
                    double longi2 = new Double(upload.getLongi());
                    double longDiff = longi1 - longi2;
                    distance = Math.sin(deg2rad(lati1))
                            * Math.sin(deg2rad(lati2))
                            + Math.cos(deg2rad(lati1))
                            * Math.cos(deg2rad(lati2))
                            * Math.cos(deg2rad(longDiff));
                    distance = Math.acos(distance);
                    distance = rad2deg(distance);
                    distance = distance * 60 * 1.1515;
                    distance = distance * 1.609344;


                    //   int userposts = new Integer(upload.getUserposts());

                    if (distance < 20) {
                        usertLists.add(upload);
                    }


                    lottie.dismiss();
                    nearbyStoresAdapter.notifyDataSetChanged();
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

    }

    private double rad2deg(double distance) {
        return (distance * 180.0 / Math.PI);
    }

    private double deg2rad(double lati1) {
        return (lati1 * Math.PI / 180.0);
    }

    void addRecmnd() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                usertLists.clear();
                for (DataSnapshot snapshot : datasnapshot.getChildren()) {
                    User post = snapshot.getValue(User.class);
                    if (snapshot.hasChild("recommended")) {
                        usertLists.add(post);
                    }


                }
                nearbyStoresAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    void addPopular() {


        Query reference = FirebaseDatabase.getInstance().getReference("Users").orderByChild("popular");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                usertLists.clear();
                for (DataSnapshot snapshot : datasnapshot.getChildren()) {
                    User post = snapshot.getValue(User.class);
                    if (snapshot.hasChild("popular") && !post.getId().equals(firebaseUser.getUid())) {

                        int followers = Integer.parseInt(post.getPopular());

                        if (followers >= limit) {
                            usertLists.add(post);
                            lottie.dismiss();
                        }

                    }


                }
                nearbyStoresAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



    private void addLocation() {
        Toast.makeText(getContext(), "Please select your place", Toast.LENGTH_SHORT).show();
        if (!Places.isInitialized()) {
            Places.initialize(getContext(), "AIzaSyDMjeI2Nwnnt6Nldp910f7BE1OdjqEtp2I" +
                    "" +
                    "");
        }


        // Set the fields to specify which types of place data to return.
        List<Place.Field> fields = Arrays.asList(Place.Field.NAME, Place.Field.LAT_LNG);

        // Start the autocomplete intent.
        Intent intent = new Autocomplete.IntentBuilder(
                AutocompleteActivityMode.OVERLAY, fields)
                .build(getContext());
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);


    }


}