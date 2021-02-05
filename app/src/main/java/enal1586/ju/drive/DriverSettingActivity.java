package enal1586.ju.drive;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DriverSettingActivity extends AppCompatActivity {

    private EditText mNameField;
    private EditText mPhoneField;
    private EditText mCarField;
    private TextView mServiceField;

    private Button mBack, mConfirm, mHistory;

    private ImageView mProfileImage;

    private FirebaseAuth mAuth;
    private DatabaseReference mDriverDatabase;

    private String mUserID,mName,mPhone,mCar,mService,mProfileImageUrl;

    private Uri mResultUri;

    private RadioGroup mRadioGroup;

    private final static String Name = "name";
    private final static String Phone = "phone";
    private final static String Profile_Image= "profileImageUrl";
    private final static String Car = "car";
    private final static String Service = "service";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_setting);
        mNameField = (EditText) findViewById(R.id.name);
        mPhoneField = (EditText) findViewById(R.id.phone);
        mCarField = (EditText) findViewById(R.id.car);
        mServiceField = (TextView) findViewById(R.id.size);
        mHistory = (Button) findViewById(R.id.history);


        mProfileImage = (ImageView) findViewById(R.id.profileImage);

        mRadioGroup = (RadioGroup) findViewById(R.id.radioGroup);

        mBack = (Button) findViewById(R.id.back);
        mConfirm = (Button) findViewById(R.id.confirm);

        mAuth = FirebaseAuth.getInstance();
        mUserID = mAuth.getCurrentUser().getUid();
        mDriverDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(mUserID);

        getUserInfo();

        mProfileImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, 1);
        });

        mConfirm.setOnClickListener(v -> {

                saveUserInformation();

                finish();


        });

        mBack.setOnClickListener(v -> {
            finish();
            return;
        });


        //history driver
        mHistory.setOnClickListener(v -> {
            Intent intent = new Intent(DriverSettingActivity.this, HistoryActivity.class);
            intent.putExtra("customerOrDriver", "Drivers");
            startActivity(intent);
            return;
        });
    }
    // drivers information
    private void getUserInfo(){
        mDriverDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if(map.get(Name)!=null){
                        mName = map.get(Name).toString();
                        mNameField.setText(mName);
                    }
                    if(map.get(Phone)!=null){
                        mPhone = map.get(Phone).toString();
                        mPhoneField.setText(mPhone);
                    }
                    if(map.get(Car)!=null){
                        mCar = map.get(Car).toString();
                        mCarField.setText(mCar);
                    }


                        if(map.get(Service)!=null){
                            mService = map.get(Service).toString();
                            mServiceField.setText(mService);
                    }
                    if(map.get(Service)!=null){
                        mService = map.get(Service).toString();
                        switch (mService){
                            case"5Sits":
                                mRadioGroup.check(R.id.femSits);
                                break;
                            case"7Sits":
                                mRadioGroup.check(R.id.sjuSits);
                                break;
                            case"12Sits":
                                mRadioGroup.check(R.id.tolvSits);
                                break;
                        }

                    }
                    if(map.get(Profile_Image)!=null){
                        mProfileImageUrl = map.get(Profile_Image).toString();
                        Glide.with(getApplication()).load(mProfileImageUrl).into(mProfileImage);
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }


    //save on DB
    private void saveUserInformation() {
        mName = mNameField.getText().toString();
        mPhone = mPhoneField.getText().toString();
        mCar = mCarField.getText().toString();

        int selectId = mRadioGroup.getCheckedRadioButtonId();

       // final RadioButton radioButton = (RadioButton) findViewById(selectId);
        final RadioButton radioButton = (RadioButton)DriverSettingActivity.this.findViewById(selectId);

        if (radioButton.getText() == null){


        }

        mService = radioButton.getText().toString();

        Map userInfo = new HashMap();
        userInfo.put(Name, mName);
        userInfo.put(Phone, mPhone);
        userInfo.put(Car, mCar);
        userInfo.put(Service, mService);
        mDriverDatabase.updateChildren(userInfo);

        if(mResultUri != null) {

            StorageReference filePath = FirebaseStorage.getInstance().getReference().child("profile_images").child(mUserID);
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(), mResultUri);
            } catch (IOException e) {
                e.printStackTrace();
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
            byte[] data = baos.toByteArray();
            UploadTask uploadTask = filePath.putBytes(data);

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    finish();
                    return;
                }
            });
          /*  uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                   // Uri downloadUrl = taskSnapshot.getDownloadUr1();
                    Task<Uri> downloadUrl = taskSnapshot.getStorage().getDownloadUrl();

                    Map newImage = new HashMap();
                    newImage.put("profileImageUrl", downloadUrl.toString());
                    mDriverDatabase.updateChildren(newImage);

                    finish();
                    return;
                }
            });

           */

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> task = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                    task.addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String photoLink = uri.toString();
                            Map userInfo = new HashMap();
                            userInfo.put(Profile_Image, photoLink);
                            mDriverDatabase.updateChildren(userInfo);
                        }
                    });
                    finish();
                    return;
                }
            });

        }else{
            finish();
            return;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == Activity.RESULT_OK){
            final Uri imageUri = data.getData();
            mResultUri = imageUri;
            mProfileImage.setImageURI(mResultUri);
        }
    }





}

