package com.example.vetlinia.patientApp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.vetlinia.DoctorApp.DoctorViewAllAnimalsProfile;
import com.example.vetlinia.DoctorApp.TestLapActivity;
import com.example.vetlinia.Model.AddAnimalModel;
import com.example.vetlinia.R;
import com.example.vetlinia.patientApp.patient_fragments.addAnimalFragment;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.victor.loading.rotate.RotateLoading;

import de.hdodenhof.circleimageview.CircleImageView;

public class AnimalProfileActivity extends AppCompatActivity {

    EditText name, age, sub, species, notes, bloodType, gender, type;
    LinearLayout cancelSave;
    CircleImageView pic;
    RotateLoading rotateLoading;
    Button editBtn, cancelBtn, saveBtn, addTestLap;
    Uri photoPath;
    String name_txt, age_text, species_txt, sub_txt, bloodType_txt, gender_txt, type_txt, note_txt, selected_placeImageURL;

    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animal_profile);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        databaseReference.keepSynced(true);


        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference().child("images");

        initViews();
    }

    private void initViews() {
        name = findViewById(R.id.full_name_field_profile);
        age = findViewById(R.id.age_field);
        sub = findViewById(R.id.sup_field);
        species = findViewById(R.id.species_field);
        notes = findViewById(R.id.notes_field);
        bloodType = findViewById(R.id.blood_type_spinner);
        gender = findViewById(R.id.gender_spinner);
        type = findViewById(R.id.type_spinner);
        cancelSave = findViewById(R.id.save_cancel);
        addTestLap = findViewById(R.id.add_test_lap_btn);
        pic = findViewById(R.id.profile_image);
        editBtn = findViewById(R.id.edit_btn);
        cancelBtn = findViewById(R.id.cancel_btn);
        saveBtn = findViewById(R.id.save_btn);
        rotateLoading = findViewById(R.id.rotateloading);

        cancelSave.setVisibility(View.GONE);

        name.setText(getIntent().getStringExtra("name"));
        age.setText(getIntent().getStringExtra("age"));
        sub.setText(getIntent().getStringExtra("sub"));
        species.setText(getIntent().getStringExtra("species"));
        notes.setText(getIntent().getStringExtra("notes"));
        bloodType.setText(getIntent().getStringExtra("blood"));
        type.setText(getIntent().getStringExtra("type"));
        gender.setText(getIntent().getStringExtra("gender"));

        Picasso.get()
                .load(getIntent().getStringExtra("pic"))
                .placeholder(R.drawable.logologo)
                .error(R.drawable.logologo)
                .into(pic);

        name.setEnabled(false);
        age.setEnabled(false);
        sub.setEnabled(false);
        species.setEnabled(false);
        notes.setEnabled(false);
        bloodType.setEnabled(false);
        gender.setEnabled(false);
        type.setEnabled(false);
        pic.setEnabled(false);

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name.setEnabled(true);
                age.setEnabled(true);
                sub.setEnabled(true);
                species.setEnabled(true);
                notes.setEnabled(true);
                pic.setEnabled(true);

                cancelSave.setVisibility(View.VISIBLE);
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name.setEnabled(false);
                age.setEnabled(false);
                sub.setEnabled(false);
                species.setEnabled(false);
                notes.setEnabled(false);
                pic.setEnabled(false);

                cancelSave.setVisibility(View.GONE);
            }
        });

        addTestLap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AnimalProfileActivity.this, ViewAllTestLaps.class);
                intent.putExtra("animalID", getIntent().getStringExtra("animalID"));
                startActivity(intent);
            }
        });

        pic.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON_TOUCH)
                        .setAspectRatio(1, 1)
                        .start(AnimalProfileActivity.this);
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name_txt = name.getText().toString();
                age_text = age.getText().toString();
                sub_txt = sub.getText().toString();
                species_txt = species.getText().toString();
                note_txt = notes.getText().toString();
                gender_txt = gender.getText().toString();
                bloodType_txt = bloodType.getText().toString();
                type_txt = type.getText().toString();

                name.setEnabled(false);
                age.setEnabled(false);
                sub.setEnabled(false);
                species.setEnabled(false);
                notes.setEnabled(false);
                pic.setEnabled(false);

                cancelSave.setVisibility(View.GONE);

                if (photoPath == null) {
                    uploadDataAnimal(name_txt, age_text, sub_txt, species_txt, note_txt, gender_txt, bloodType_txt, type_txt, getIntent().getStringExtra("pic"));
                } else {
                    uploadImage(name_txt, age_text, sub_txt, species_txt, note_txt, gender_txt, bloodType_txt, type_txt);
                }
            }
        });
    }

    private void uploadDataAnimal(String name_txt, String age, String sub, String species, String notes, String type, String gender, String bloodGroup, String photoUrl) {
        AddAnimalModel addAnimalModel = new AddAnimalModel(name_txt, age, bloodGroup, photoUrl, " ", sub, species, type, notes, gender, getIntent().getStringExtra("animalID"),getUID());

        databaseReference.child("AllAnimalsPatient").child(getUID()).child(getIntent().getStringExtra("animalID")).setValue(addAnimalModel);
        databaseReference.child("AllAnimals").child(getIntent().getStringExtra("animalID")).setValue(addAnimalModel);
    }

    private void uploadImage(String name_txt, String age_text, String sub_txt, String species_txt, String note_txt, String gender_txt, String bloodType_txt, String type_txt) {
        rotateLoading.start();

        UploadTask uploadTask;

        final StorageReference ref = storageReference.child("images/" + photoPath.getLastPathSegment());

        uploadTask = ref.putFile(photoPath);

        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return ref.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                Uri downloadUri = task.getResult();

                rotateLoading.stop();

                selected_placeImageURL = downloadUri.toString();

                uploadDataAnimal(name_txt, age_text, sub_txt, species_txt, note_txt, gender_txt, bloodType_txt, type_txt, selected_placeImageURL);

                Toast.makeText(AnimalProfileActivity.this, "saved", Toast.LENGTH_SHORT).show();

                /*returndata();*/
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Toast.makeText(AnimalProfileActivity.this, "Can't Upload Photo", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == Activity.RESULT_OK) {
                if (result != null) {
                    photoPath = result.getUri();

                    Picasso.get()
                            .load(photoPath)
                            .placeholder(R.drawable.addphoto)
                            .error(R.drawable.addphoto)
                            .into(pic);
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    private String getUID() {
        String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        return id;
    }
}