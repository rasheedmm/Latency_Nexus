package com.live.latency_nexus.Adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
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

import com.live.latency_nexus.Models.Comment;
import com.live.latency_nexus.Models.MyPosts;
import com.live.latency_nexus.Models.User;
import com.live.latency_nexus.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MyPostsAdapter extends RecyclerView.Adapter<MyPostsAdapter.ViewHolder> {
    public Context mContext;
    public List<MyPosts> mPost;
    private FirebaseUser firebaseUser;
    private Dialog dialog, dialog2;
    EditText prdct_name, desc, qnty, mrp, offer;
    TextView cancel, submit;
    public static final int PERMISSION_WRITE = 0;
    String fileUri;

    public MyPostsAdapter(Context mContext, List<MyPosts> mPost) {
        this.mContext = mContext;
        this.mPost = mPost;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.my_posts_item, parent, false);
        return new MyPostsAdapter.ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        MyPosts post = mPost.get(position);
        Glide.with(this.mContext)
                .load(post.getImg1())
                .apply(RequestOptions.bitmapTransform(new RoundedCorners(14)))
                .into(holder.btn_rew);

        holder.item_name.setText(post.getPrdname());
        holder.mrp_txt.setText("₹" + post.getMrp());
        holder.offer_txt.setText("₹" + post.getOffer());
        if (post.getStock().equals("true")) {
            holder.status.setText("In Stock");
            holder.status.setTextColor(ContextCompat.getColor(mContext, R.color.green));
        } else {
            holder.status.setText("Out of stock");
            holder.status.setTextColor(ContextCompat.getColor(mContext, R.color.red));
        }
        holder.prd_name = post.getPrdname();
        holder.prd_desc = post.getPrddesc();
        holder.prd_img = post.getImg1();
        holder.prd_id = post.getPostid();
        int mrp = Integer.parseInt(post.getMrp());
        int price = Integer.parseInt(post.getOffer());
        int subtract = mrp - price;
        double result = ((double) subtract / mrp) * 100;

        String pr = String.valueOf(result).substring(0, String.valueOf(result).indexOf("."));


        holder.percentage.setText(pr + "% off");
        if (post.getPublisher().equals(firebaseUser.getUid())) {
            holder.mySwitch.setVisibility(View.VISIBLE);
        }
        if (post.getStock().equals("true")) {
            holder.mySwitch.setChecked(true);
        } else holder.mySwitch.setChecked(false);
        holder.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareproduct(post, holder.btn_rew, holder.share);
            }
        });
        holder.comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showcomments(post.getPostid(), post.getPublisher());
            }
        });
        holder.mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Toast.makeText(mContext, "Product set as stock available", Toast.LENGTH_SHORT).show();
                    FirebaseDatabase.getInstance().getReference().child("Posts").child(post.getPostid())
                            .child("stock").setValue("true");
                    holder.status.setText("In Stock");
                    holder.status.setTextColor(ContextCompat.getColor(mContext, R.color.green));
                } else {
                    Toast.makeText(mContext, "Product set as out of stock ", Toast.LENGTH_SHORT).show();
                    FirebaseDatabase.getInstance().getReference().child("Posts").child(post.getPostid())
                            .child("stock").setValue("f");
                    holder.status.setText("Out Of Stock");
                    holder.status.setTextColor(ContextCompat.getColor(mContext, R.color.red));
                }
            }

        });

        // holder.container.setBackgroundResource(backgr[r.nextInt(8)]);

       /* if (post.getDescription().equals("")) {
            holder.description.setVisibility(View.GONE);
        } else {
            holder.description.setVisibility(View.VISIBLE);
            holder.description.setText(post.getDescription());
        }*/
        publisherInfo(holder.image_profile, holder.username, post.getPublisher());


        holder.more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(mContext, v);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.edit:
                                editPost(post.getPostid());
                                return true;
                            case R.id.delete:


                                AlertDialog.Builder builder = new AlertDialog.Builder(mContext,R.style.AlertDialogTheme);

                                builder.setTitle("Confirm Delete");
                                builder.setMessage("Really want to delete this product");

                                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int which) {
                                        // Do nothing but close the dialog


                                        FirebaseDatabase.getInstance().getReference("Posts")
                                                .child(post.getPostid()).removeValue()
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Toast.makeText(mContext, "Deleted", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                    }
                                });

                                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        // Do nothing
                                        dialog.dismiss();
                                    }
                                });

                                AlertDialog alert = builder.create();
                                alert.show();

                                return true;
                            case R.id.report:
                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Reports");
                                HashMap<String, Object> hashMap = new HashMap<>();

                                hashMap.put("postid", post.getPostid());
                                hashMap.put("user", firebaseUser.getUid());
                                reference.push().setValue(hashMap);
                                Toast.makeText(mContext, "Reported", Toast.LENGTH_SHORT).show();
                                return true;
                            case R.id.comment:
                                showcomments(post.getPostid(), post.getPublisher());
                                return true;
                            case R.id.share:
                                shareproduct(post, holder.btn_rew, holder.share);
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                popupMenu.inflate(R.menu.menu_post);
                popupMenu.getMenu().findItem(R.id.share).setVisible(false);
                popupMenu.getMenu().findItem(R.id.comment).setVisible(false);
                if (!post.getPublisher().equals(firebaseUser.getUid())) {
                    popupMenu.getMenu().findItem(R.id.edit).setVisible(false);
                    popupMenu.getMenu().findItem(R.id.delete).setVisible(false);
                }
                popupMenu.show();

            }
        });
    }

    private void shareproduct(MyPosts post, ImageView prdimg, ImageView share) {
        Toast.makeText(mContext, "Plese waite generating link....", Toast.LENGTH_SHORT).show();
        share.setVisibility(View.GONE);

        DynamicLink dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse("https://www.malluhub.in/"))
                .setDomainUriPrefix("quickart.page.link")
                // Open links with this app on Android
                .setAndroidParameters(new DynamicLink.AndroidParameters.Builder().build())
                // Open links with com.example.ios on iOS
                .setIosParameters(new DynamicLink.IosParameters.Builder("com.example.ios").build())
                .buildDynamicLink();

        Uri dynamicLinkUri = dynamicLink.getUri();


        String sharelinktext = "https://quickart.page.link/?" +
                "link=http://www.malluhub.tk/myrefer.php?custid=" + "null" + "-" + post.getPostid() +
                "&apn=" + "com.hono.onlinestore" +
                "&st=" + post.getPrdname() +
                "&sd=" + post.getPrddesc() +
                "&si=" + post.getImg1();


        Task<ShortDynamicLink> shortLinkTask = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLongLink(Uri.parse(sharelinktext))
                .buildShortDynamicLink()
                .addOnCompleteListener((Activity) mContext, new OnCompleteListener<ShortDynamicLink>() {
                    @Override
                    public void onComplete(@NonNull Task<ShortDynamicLink> task) {
                        if (task.isSuccessful()) {

                            Uri shortLink = task.getResult().getShortLink();
                            Uri flowchartLink = task.getResult().getPreviewLink();

                            BitmapDrawable drawable = (BitmapDrawable) prdimg.getDrawable();
                            Bitmap bitmap = drawable.getBitmap();
                            String bitmappath = MediaStore.Images.Media.insertImage(mContext.getApplicationContext().getContentResolver(), bitmap
                                    , "IMG_" + System.currentTimeMillis(), "desc");

                            Uri uri = Uri.parse(bitmappath);

                            Intent intent = new Intent(Intent.ACTION_SEND);
                            intent.setType("image/png");
                            intent.putExtra(Intent.EXTRA_STREAM, uri);

                            intent.putExtra(Intent.EXTRA_TEXT, post.getPrdname() + "\n" + "₹" + post.getOffer() + "\nCheck out this awesome product\nClick here to buy\n" + shortLink.toString());

                            mContext.startActivity(Intent.createChooser(intent, "share"));
                            share.setVisibility(View.VISIBLE);

                        } else {
                            Toast.makeText(mContext, "Error", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public int getItemCount() {
        return mPost.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView image_profile, more, comments, share;
        public TextView username, publisher, description, mrp_txt, offer_txt, item_name, status, percentage;
        public ConstraintLayout container;
        String prd_name, prd_desc, prd_img, prd_id;
        ImageView btn_rew;
        Switch mySwitch = null;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            //  publisher=itemView.findViewById(R.id.pub);
            description = itemView.findViewById(R.id.slide_desc);

            more = itemView.findViewById(R.id.imageView2);
            btn_rew = itemView.findViewById(R.id.imageView);
            btn_rew.setClipToOutline(true);
            mrp_txt = itemView.findViewById(R.id.mrp_txt);
            offer_txt = itemView.findViewById(R.id.offer_txt);
            item_name = itemView.findViewById(R.id.item_name);

            status = itemView.findViewById(R.id.stock);
            percentage = itemView.findViewById(R.id.off_percentage);
            comments = itemView.findViewById(R.id.btn_comment);
            share = itemView.findViewById(R.id.btn_share);

            mrp_txt.setPaintFlags(mrp_txt.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            mySwitch = (Switch) itemView.findViewById(R.id.switch1);

        }
    }

    private void publisherInfo(ImageView image_profile, TextView username, String userid) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);


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


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void editPost(String postid) {


        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(
                mContext, R.style.BottomSheetDialogTheme
        );
        View bottomSheetView = LayoutInflater.from(mContext)
                .inflate(R.layout.bottomnav_editposts,
                        bottomSheetDialog.findViewById(R.id.bottomSheetContainer)
                );
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();

        prdct_name = bottomSheetDialog.findViewById(R.id.prdctname);
        desc = bottomSheetDialog.findViewById(R.id.details);
        qnty = bottomSheetDialog.findViewById(R.id.prdctsize);
        mrp = bottomSheetDialog.findViewById(R.id.mrp);
        offer = bottomSheetDialog.findViewById(R.id.offer);
        cancel = bottomSheetDialog.findViewById(R.id.btn_cancel);
        submit = bottomSheetDialog.findViewById(R.id.btn_submit);

        getText(postid, prdct_name, desc, qnty, mrp, offer);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.cancel();
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("prdname", prdct_name.getText().toString());
                hashMap.put("prddesc", desc.getText().toString());
                hashMap.put("prdqnt", qnty.getText().toString());
                hashMap.put("mrp", mrp.getText().toString());
                hashMap.put("offer", offer.getText().toString());

                FirebaseDatabase.getInstance().getReference("Posts")
                        .child(postid).updateChildren(hashMap);
                Toast.makeText(mContext, "Data Upadated", Toast.LENGTH_SHORT).show();
                bottomSheetDialog.cancel();
            }
        });

    }


    private void getText(String postid, EditText prdct_name, EditText desc, EditText qnty, EditText mrp, EditText offer) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts")
                .child(postid);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                prdct_name.setText(snapshot.getValue(MyPosts.class).getPrdname());
                desc.setText(snapshot.getValue(MyPosts.class).getPrddesc());
                qnty.setText(snapshot.getValue(MyPosts.class).getPrdqnt());
                mrp.setText(snapshot.getValue(MyPosts.class).getMrp());
                offer.setText(snapshot.getValue(MyPosts.class).getOffer());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void showcomments(String postid, String publisher) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(
                mContext, R.style.BottomSheetDialogTheme
        );
        View bottomSheetView = LayoutInflater.from(mContext)
                .inflate(R.layout.bottom_sheet_container_comments,
                        bottomSheetDialog.findViewById(R.id.bottomSheetContainer)
                );
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();


        RecyclerView recyclerView;
        CommentAdapter commentAdapter;
        List<Comment> commentList;
        EditText addcomment;
        ImageView image_profile;
        TextView post, love, angry, sadcry, superone, dislike, like, fire, awsome;
        String username;
        String publisherid;
        FirebaseUser firebaseUser;


        angry = bottomSheetDialog.findViewById(R.id.angry);
        sadcry = bottomSheetDialog.findViewById(R.id.sadcry);
        superone = bottomSheetDialog.findViewById(R.id.superone);
        dislike = bottomSheetDialog.findViewById(R.id.dislike);
        like = bottomSheetDialog.findViewById(R.id.like);
        fire = bottomSheetDialog.findViewById(R.id.fire);
        awsome = bottomSheetDialog.findViewById(R.id.awesome);

        recyclerView = bottomSheetDialog.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        recyclerView.setLayoutManager(linearLayoutManager);
        commentList = new ArrayList<>();
        commentAdapter = new CommentAdapter(mContext, commentList);
        recyclerView.setAdapter(commentAdapter);

        addcomment = bottomSheetDialog.findViewById(R.id.add_comment);
        image_profile = bottomSheetDialog.findViewById(R.id.image_profile);
        post = bottomSheetDialog.findViewById(R.id.post);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        postid = postid;
        publisherid = publisher;
        love = bottomSheetDialog.findViewById(R.id.love);


        love.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addcomment.setText("❤❤❤❤");
            }
        });
        angry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addcomment.setText("😡😡😡");
            }
        });
        sadcry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addcomment.setText("😭😭😭");
            }
        });
        superone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addcomment.setText("👌👌👌");
            }
        });
        dislike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addcomment.setText("👎👎👎");
            }
        });
        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addcomment.setText("👍👍👍");
            }
        });
        fire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addcomment.setText("🔥🔥🔥");
            }
        });
        awsome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addcomment.setText("❤❤❤");
            }
        });


        String finalPostid = postid;
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (addcomment.getText().toString().equals("")) {
                    Toast.makeText(mContext, "Write Something", Toast.LENGTH_SHORT).show();
                } else {
                    addComment(addcomment, finalPostid);
                }
            }
        });

        readComment(commentList, commentAdapter, postid);


    }

    private void addComment(EditText addcomment, String postid) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Comments").child(postid);
        HashMap<String, Object> hashMap = new HashMap<>();

        hashMap.put("comment", addcomment.getText().toString());

        hashMap.put("publisher", firebaseUser.getUid());
        reference.push().setValue(hashMap);


        addcomment.setText("");
    }

    private void readComment(List<Comment> commentList, CommentAdapter commentAdapter, String postid) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Comments").child(postid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                commentList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Comment comment = snapshot.getValue(Comment.class);
                    commentList.add(comment);
                }

                commentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });

    }

    public Uri getLocalBitmapUri(ImageView imageView) {
        // Extract Bitmap from ImageView drawable
        Drawable drawable = imageView.getDrawable();
        Bitmap bmp = null;
        if (drawable instanceof BitmapDrawable) {
            bmp = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        } else {
            return null;
        }
        // Store image to default external storage directory
        Uri bmpUri = null;
        try {
            File file = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS), "share_image_" + System.currentTimeMillis() + ".png");
            file.getParentFile().mkdirs();
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
            bmpUri = Uri.fromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }
}
