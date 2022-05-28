package com.live.latency_nexus.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.live.latency_nexus.Models.User;
import com.live.latency_nexus.R;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

import io.alterac.blurkit.BlurLayout;


public class NearbyStoresAdapter extends RecyclerView.Adapter<NearbyStoresAdapter.ViewHolder> {
    public Context mContext;
    public List<User> mPost;
    private FirebaseUser firebaseUser;
    TextInputEditText prdct_name, desc, qnty, mrp, offer;
    Button cancel, submit;
    private final int limit = 20;

    public NearbyStoresAdapter(Context mContext, List<User> mPost) {
        this.mContext = mContext;
        this.mPost = mPost;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.nearby_items, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {


        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        User user = mPost.get(position);
        Glide.with(mContext).load(user.getImageurl()).into(holder.post_image);
        holder.blurLayout.startBlur();
     holder.username.setText(user.getName());
        holder.description.setText(user.getLocation());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* Intent intent = new Intent(mContext, UserProfileActivity.class);
                intent.putExtra("profileid",user.getId());
                mContext.startActivity(intent);*/
            }
        });

    }

    @Override
    public int getItemCount() {
        if(mPost.size() > limit){
            return limit;
        }
        else
        {
            return mPost.size();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        BlurLayout blurLayout;
        RoundedImageView post_image;
        public TextView username, publisher, description;
        public CardView container;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            blurLayout=itemView.findViewById(R.id.blurLayout);

            post_image = itemView.findViewById(R.id.post_image);
            username = itemView.findViewById(R.id.username);
            container = itemView.findViewById(R.id.container);
            //  publisher=itemView.findViewById(R.id.pub);
            description = itemView.findViewById(R.id.description);

        }
    }

    private void publisherInfo(ImageView image_profile, TextView username, String userid) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                username.setText(user.getUsername());
                Glide.with(mContext).load(user.getImageurl()).into(image_profile);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }




    private void getImage(ImageView image_profile) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                Glide.with(mContext.getApplicationContext()).load(user.getImageurl()).into(image_profile);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
