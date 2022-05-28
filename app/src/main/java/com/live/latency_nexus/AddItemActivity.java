package com.live.latency_nexus;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.drjacky.imagepicker.ImagePicker;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.live.latency_nexus.Models.User;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import id.zelory.compressor.Compressor;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;

public class AddItemActivity extends AppCompatActivity {
    EditText product_name, product_desc, product_quant, mrp, offer;
    TextView btn_save;
    ImageView img1, img2;
    lottiedialogfragment lottie;
    private FirebaseAuth mAuth;
    FirebaseUser firebaseUser;
    public Uri imageUri1;
    public Uri imageUri2;
    Boolean usertype;
    String myUri1 = "null";
    String myUri2 = "null";
    Uri compressUri;
    private int imageNo;
    StorageReference storageReference, storageReference1;
    StorageTask uploadTask, uploadTask1;
    int offernum, mrpnum;
    private RadioGroup radioGroup;
    private RadioButton radioButton;
    private RadioButton notSelectedRadio;
    RadioButton codRadio;
    String payment;
    byte[] finalimage, finalimage2;
    private static String filepath;
    File path = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/compressor");

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);
        product_name = findViewById(R.id.product_name);
        product_desc = findViewById(R.id.product_desc);
        product_quant = findViewById(R.id.product_quant);
        mrp = findViewById(R.id.product_mrp);
        offer = findViewById(R.id.product_off);
        btn_save = findViewById(R.id.btn_save);
        img1 = findViewById(R.id.img1);
        img2 = findViewById(R.id.img2);
        imageUri1 = null;
        imageUri2 = null;
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        radioGroup = (RadioGroup) findViewById(R.id.radio);
        filepath = path.getAbsolutePath();
        if (!path.exists()) {
            path.mkdir();
        }
        mAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference("uploads");
        storageReference1 = FirebaseStorage.getInstance().getReference("uploads");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                usertype = user.getUsertype();
                if (usertype.equals(false)) {
                    notSelectedRadio = (RadioButton) findViewById(R.id.radioOnline);
                    notSelectedRadio.setEnabled(false);
                    codRadio = (RadioButton) findViewById(R.id.radioCod);
                    codRadio.setSelected(true);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        img1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageNo = 1;
                ImagePicker.Companion.with(AddItemActivity.this)
                        .crop()
                        .cropSquare()
                        .maxResultSize(720, 720, true)
                        .createIntentFromDialog((Function1) (new Function1() {
                            public Object invoke(Object var1) {
                                this.invoke((Intent) var1);
                                return Unit.INSTANCE;
                            }

                            public final void invoke(@NotNull Intent it) {
                                Intrinsics.checkNotNullParameter(it, "it");
                                launcher.launch(it);
                            }
                        }));
            }
        });
        img2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageNo = 2;
                ImagePicker.Companion.with(AddItemActivity.this)
                        .crop()
                        .cropSquare()
                        .maxResultSize(720, 720, true)
                        .createIntentFromDialog((Function1) (new Function1() {
                            public Object invoke(Object var1) {
                                this.invoke((Intent) var1);
                                return Unit.INSTANCE;
                            }

                            public final void invoke(@NotNull Intent it) {
                                Intrinsics.checkNotNullParameter(it, "it");
                                launcher.launch(it);
                            }
                        }));
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (TextUtils.isEmpty(product_name.getText())) {
                    product_name.setError("Please fill this field");
                    //Map to add user to array

                }
                if (TextUtils.isEmpty(product_desc.getText())) {
                    product_desc.setError("Please fill this field");
                }
                if (TextUtils.isEmpty(product_quant.getText())) {
                    product_quant.setError("Please fill this field");
                }
                if (TextUtils.isEmpty(mrp.getText())) {
                    mrp.setError("Please fill this field");
                }
                if (TextUtils.isEmpty(offer.getText())) {
                    offer.setError("Please fill this field");
                }

                try {
                    offernum = Integer.parseInt(String.valueOf(offer.getText()));
                } catch (NumberFormatException ex) { // handle your exception

                }
                try {
                    mrpnum = Integer.parseInt(String.valueOf(mrp.getText()));
                } catch (NumberFormatException ex) { // handle your exception

                }

                if (offernum > mrpnum) {
                    Toast.makeText(AddItemActivity.this, "Selling price must be lesser than MRP", Toast.LENGTH_SHORT).show();
                }

                if (!notSelectedRadio.isChecked() && !codRadio.isChecked()) {
                    Toast.makeText(AddItemActivity.this, "Please select one payment option", Toast.LENGTH_SHORT).show();
                }
                {

                }
                if (imageUri1 == null || imageUri2 == null) {
                    Toast.makeText(AddItemActivity.this, "Please select 2 photos of product", Toast.LENGTH_SHORT).show();
                }
                String prdname, prd_desc, prd_size, sellingprice, mrpprice;
                prdname = product_name.getText().toString();
                prd_desc = product_desc.getText().toString();
                prd_size = product_quant.getText().toString();
                sellingprice = offer.getText().toString();
                mrpprice = mrp.getText().toString();

                if (imageUri1 != null && imageUri2 != null && offernum <= mrpnum && !prdname.equals("")
                        && !prd_desc.equals("") && !prd_size.equals("") && !sellingprice.equals("") && !mrpprice.equals("") && codRadio.isChecked()) {

                    upload();
                    lottie = new lottiedialogfragment(AddItemActivity.this);
                    lottie.show();

                    // get selected radio button from radioGroup
                    int selectedId = radioGroup.getCheckedRadioButtonId();

                    // find the radiobutton by returned id
                    radioButton = (RadioButton) findViewById(selectedId);

                    payment = String.valueOf(radioButton.getText());

                }
            }
        });
    }

    private void upload() {


        if (imageUri1 != null) {


            try {
                File actualImage1 = new File(imageUri1.getPath());
                Bitmap compressedImage1 = new Compressor(AddItemActivity.this)
                        .setMaxWidth(800)
                        .setMaxHeight(800)
                        .setQuality(60)
                        .compressToBitmap(actualImage1);
                ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
                compressedImage1.compress(Bitmap.CompressFormat.JPEG, 70, baos1);
                finalimage = baos1.toByteArray();

            } catch (IOException e) {
                e.printStackTrace();
            }


            final StorageReference fileRefference1 = storageReference.child(System.currentTimeMillis()
                    + "." + getFileExtension(imageUri1));
            uploadTask = fileRefference1.putBytes(finalimage);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return fileRefference1.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                        Uri downloaduri1 = (Uri) task.getResult();
                        myUri1 = downloaduri1.toString();
                        if (imageUri2 != null) {
                            try {
                                File actualImage2 = new File(imageUri2.getPath());
                                Bitmap compressedImage2 = new Compressor(AddItemActivity.this)
                                        .setMaxWidth(800)
                                        .setMaxHeight(800)
                                        .setQuality(60)
                                        .compressToBitmap(actualImage2);
                                ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
                                compressedImage2.compress(Bitmap.CompressFormat.JPEG, 70, baos2);
                                finalimage2 = baos2.toByteArray();

                            } catch (IOException e) {
                                e.printStackTrace();
                            }


                            final StorageReference fileRefference2 = storageReference1.child(System.currentTimeMillis()
                                    + "." + getFileExtension(imageUri2));
                            uploadTask1 = fileRefference2.putBytes(finalimage2);
                            uploadTask1.continueWithTask(new Continuation() {
                                @Override
                                public Object then(@NonNull Task task) throws Exception {
                                    if (!task.isSuccessful()) {
                                        throw task.getException();
                                    }
                                    return fileRefference2.getDownloadUrl();
                                }
                            }).addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {
                                    if (task.isSuccessful()) {
                                        Uri downloaduri2 = (Uri) task.getResult();
                                        myUri2 = downloaduri2.toString();

                                        adddata();

                                    } else {
                                        Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(getApplicationContext(), "No image selected", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Toast.makeText(getApplicationContext(), "No image selected", Toast.LENGTH_SHORT).show();
        }


    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void adddata() {


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        String postid = reference.push().getKey();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("postid", postid);
        hashMap.put("prdname", product_name.getText().toString());
        hashMap.put("prddesc", product_desc.getText().toString());
        hashMap.put("prdqnt", product_quant.getText().toString());
        hashMap.put("publisher", FirebaseAuth.getInstance().getCurrentUser().getUid());
        hashMap.put("img1", myUri1);
        hashMap.put("img2", myUri2);
        hashMap.put("mrp", mrp.getText().toString());
        hashMap.put("offer", offer.getText().toString());
        hashMap.put("stock", "true");
        hashMap.put("payment", payment);
        lottie.dismiss();
        Toast.makeText(AddItemActivity.this, "Product Added.", Toast.LENGTH_SHORT).show();
        reference.child(postid).setValue(hashMap);


        finish();

    }

    ActivityResultLauncher<Intent> launcher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), (ActivityResult result) -> {
                if (result.getResultCode() == RESULT_OK) {


                    switch (imageNo) {
                        case (1):
                            imageUri1 = result.getData().getData();

                            Glide.with(getApplicationContext()).load(imageUri1).into(img1);
                            break;
                        case (2):
                            imageUri2 = result.getData().getData();
                            Glide.with(getApplicationContext()).load(imageUri2).into(img2);
                            break;

                    }

                    // Use the uri to load the image
                } else if (result.getResultCode() == ImagePicker.RESULT_ERROR) {
                    // Use ImagePicker.Companion.getError(result.getData()) to show an error
                }
            });


}
