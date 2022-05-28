package com.live.latency_nexus.Adapters;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;
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

import com.live.latency_nexus.ImageActivity;
import com.live.latency_nexus.Models.Comment;
import com.live.latency_nexus.Models.MyPosts;
import com.live.latency_nexus.Models.User;
import com.live.latency_nexus.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {
    public Context mContext;
    public List<MyPosts> mPost;
    private FirebaseUser firebaseUser;
    TextInputEditText prdct_name, desc, qnty, mrp, offer;
    TextView cancel, submit;
    ProgressBar progress;
    private final int limit = 30;
    ImageView product_image, product_image1;
    TextView prdct_name1, offer_price, mrp_price, pr_desc, off_percentage, tv_stock, tv_item_qnty;
    TextView btn_cart, btn_buy;

    public PostAdapter(Context mContext, List<MyPosts> mPost) {
        this.mContext = mContext;
        this.mPost = mPost;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.post_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {


        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        MyPosts post = mPost.get(position);

        Glide.with(this.mContext)
                .load(post.getImg1())
                .apply(RequestOptions.bitmapTransform(new RoundedCorners(14)))
                .into(holder.post_image);

        if (post.getPrddesc().equals("")) {
            holder.description.setVisibility(View.GONE);
        } else {
            holder.description.setVisibility(View.VISIBLE);
            holder.description.setText(post.getPrdname());
        }
        int mrp = Integer.parseInt(post.getMrp());
        int price = Integer.parseInt(post.getOffer());
        int subtract = mrp - price;
        double result = ((double) subtract / mrp) * 100;

        String pr = String.valueOf(result).substring(0, String.valueOf(result).indexOf("."));


        holder.off_percentage.setText(pr + "% off");
        holder.mrp_tag.setText("₹" + post.getMrp());
        holder.offer_tag.setText("₹" + post.getOffer());
        publisherInfo(holder.image_profile, holder.username, post.getPublisher());
        holder.iv_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showcomments(post.getPostid(), post.getPublisher());
            }
        });
        holder.iv_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareproduct(post, holder.post_image, holder.iv_share);
            }
        });
        /*holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ImageActivity.class);
                intent.putExtra("url", post.getPostimage());
                intent.putExtra("header", post.getDescription());
                Pair[] pairs = new Pair[2];
                pairs[0] = new Pair<View, String>(holder.post_image, "image");
                pairs[1] = new Pair<View, String>(holder.description, "text");
                if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)) {
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity) mContext, pairs);
                    mContext.startActivity(intent, options.toBundle());
                } else {
                    mContext.startActivity(intent);

                }

            }
        });*/
        holder.username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent intent = new Intent(mContext, UserProfileActivity.class);
                intent.putExtra("profileid",post.getPublisher());
                mContext.startActivity(intent);*/
            }
        });
        holder.image_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* Intent intent = new Intent(mContext, UserProfileActivity.class);
                intent.putExtra("profileid",post.getPublisher());
                mContext.startActivity(intent);*/
            }
        });
        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(
                        mContext, R.style.BottomSheetDialogTheme
                );
                View bottomSheetView = LayoutInflater.from(mContext)
                        .inflate(R.layout.bottomnav_prdct_details,
                                bottomSheetDialog.findViewById(R.id.bottomSheetContainer)
                        );
                bottomSheetDialog.setContentView(bottomSheetView);
                bottomSheetDialog.show();

                product_image = bottomSheetDialog.findViewById(R.id.image_item);
                product_image1 = bottomSheetDialog.findViewById(R.id.image_item1);
                prdct_name1 = bottomSheetDialog.findViewById(R.id.item_name);
                mrp_price = bottomSheetDialog.findViewById(R.id.mrp_txt);
                offer_price = bottomSheetDialog.findViewById(R.id.price_txt);
                off_percentage = bottomSheetDialog.findViewById(R.id.off_percentage);
                btn_cart = bottomSheetDialog.findViewById(R.id.btn_cart1);
                btn_buy = bottomSheetDialog.findViewById(R.id.btn_buy);
                pr_desc = bottomSheetDialog.findViewById(R.id.desc);
                tv_stock = bottomSheetDialog.findViewById(R.id.tv_stock);
                tv_item_qnty = bottomSheetDialog.findViewById(R.id.item_qnty);
                progress = bottomSheetDialog.findViewById(R.id.hoteldetails_progressBar);

                Picasso.get()
                        .load(post.getImg1())
                        .error(R.drawable.ic_launcher_foreground)
                        .into(product_image, new Callback() {
                            @Override
                            public void onSuccess() {
                                if (progress != null) {
                                    progress.setVisibility(View.GONE);
                                }
                            }

                            @Override
                            public void onError(Exception e) {

                            }


                        });

                Picasso.get()
                        .load(post.getImg2())
                        .error(R.drawable.ic_launcher_foreground)
                        .into(product_image1, new Callback() {
                            @Override
                            public void onSuccess() {
                                if (progress != null) {
                                    progress.setVisibility(View.GONE);
                                }
                            }

                            @Override
                            public void onError(Exception e) {

                            }

                        });


                int mrp = Integer.parseInt(post.getMrp());
                int price = Integer.parseInt(post.getOffer());
                int subtract = mrp - price;
                double result = ((double) subtract / mrp) * 100;

                String pr = String.valueOf(result).substring(0, String.valueOf(result).indexOf("."));
                if (post.getStock().equals("true")) {
                    tv_stock.setText("In Stock");
                    tv_stock.setTextColor(ContextCompat.getColor(mContext, R.color.green));
                } else {
                    tv_stock.setText("Out of stock");
                    tv_stock.setTextColor(ContextCompat.getColor(mContext, R.color.red));
                }

                off_percentage.setText(pr + "% off");
                tv_item_qnty.setText("Per" + post.getPrdqnt());
                prdct_name1.setText(post.getPrdname());
                mrp_price.setText("₹" + post.getMrp());
                mrp_price.setPaintFlags(mrp_price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                offer_price.setText("₹" + post.getOffer());
                pr_desc.setText(post.getPrddesc());
                isSaved(post.getPostid(), btn_cart);
product_image.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        Intent intent = new Intent(mContext, ImageActivity.class);
        intent.putExtra("url", post.getImg1());
        mContext.startActivity(intent);
    }
});
                product_image1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, ImageActivity.class);
                        intent.putExtra("url", post.getImg2());
                        mContext.startActivity(intent);
                    }
                });
                btn_buy.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!post.getStock().equals("true")) {
                            Toast.makeText(mContext, "This product is out of stock", Toast.LENGTH_SHORT).show();
                        } else {
                           /* Intent intent = new Intent(mContext, PlaceOrderActivity.class);
                            intent.putExtra("prdctname", post.getPrdname());
                            intent.putExtra("publisher", post.getPublisher());
                            intent.putExtra("prdctimg", post.getImg1());
                            intent.putExtra("prdctdesc", post.getPrddesc());
                            intent.putExtra("prdctid", post.getPostid());
                            intent.putExtra("prdctowner", post.getPublisher());
                            intent.putExtra("prdctmrp", post.getMrp());
                            intent.putExtra("prdctprice", post.getOffer());
                            intent.putExtra("prdcttype", post.getPayment());

                            mContext.startActivity(intent);*/
                        }
                    }
                });
                btn_cart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (btn_cart.getTag().equals("save")) {
                            FirebaseDatabase.getInstance().getReference("Saves").child(firebaseUser.getUid())
                                    .child(post.getPostid()).setValue(true);
                            Toast.makeText(mContext, "Product added to cart", Toast.LENGTH_SHORT).show();
                        } else {
                            FirebaseDatabase.getInstance().getReference("Saves").child(firebaseUser.getUid())
                                    .child(post.getPostid()).removeValue();
                            Toast.makeText(mContext, "Product removed from cart", Toast.LENGTH_SHORT).show();
                        }


                    }
                });

            }
        });
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

                                AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.AlertDialogTheme);

                                builder.setTitle("Confirm Delete");
                                builder.setMessage("Really want to delete this product?");

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
                            default:
                                return false;
                        }
                    }
                });
                popupMenu.inflate(R.menu.menu_post);
                if (!post.getPublisher().equals(firebaseUser.getUid())) {
                    popupMenu.getMenu().findItem(R.id.edit).setVisible(false);
                    popupMenu.getMenu().findItem(R.id.delete).setVisible(false);
                }
                popupMenu.show();

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
        public ImageView image_profile, more, iv_comment, iv_share;
        ImageView post_image;
        public TextView username, publisher, description, off_percentage, mrp_tag, offer_tag;
        public CardView container;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image_profile = itemView.findViewById(R.id.image_profile);
            post_image = itemView.findViewById(R.id.post_image);
            username = itemView.findViewById(R.id.username);
            container = itemView.findViewById(R.id.container);
            mrp_tag = itemView.findViewById(R.id.mrp_txt);
            offer_tag = itemView.findViewById(R.id.offer_txt);
            off_percentage = itemView.findViewById(R.id.off_percentage);
            //  publisher=itemView.findViewById(R.id.pub);
            description = itemView.findViewById(R.id.description);
            more = itemView.findViewById(R.id.more);

            iv_comment = itemView.findViewById(R.id.imageView3);
            iv_share = itemView.findViewById(R.id.imageView4);

            mrp_tag.setPaintFlags(mrp_tag.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
    }

    private void shareproduct(MyPosts post, ImageView prdimg, ImageView share) {
        Toast.makeText(mContext, "Please wait generating link....", Toast.LENGTH_SHORT).show();
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


        String sharelinktext = "https://Latency_Nexus.page.link/?" +
                "link=http://www.nexus.tk/myrefer.php?custid=" + "null" + "-" + post.getPostid() +
                "&apn=" + "com.live.latency_nexus" +
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


    public int getRandomColor() {
        Random rnd = new Random();
        return Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
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
        getImage(image_profile);
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

    private void isSaved(String postid, TextView imageView) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Saves")
                .child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(postid).exists()) {
                    btn_cart.setText("Remove from cart");
                    imageView.setTag("saved");
                } else {
                    btn_cart.setText("Add to cart");
                    imageView.setTag("save");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}
