package com.example.vetlinia.DoctorApp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.vetlinia.R;
import com.example.vetlinia.patientApp.AnimalProfileActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.victor.loading.rotate.RotateLoading;

import de.hdodenhof.circleimageview.CircleImageView;

public class DoctorViewAllAnimalsProfile extends AppCompatActivity {

    EditText name, age, sub, species, notes, bloodType, gender, type;
    CircleImageView pic;
    RotateLoading rotateLoading;
    Button testLapBtn, cancelBtn, saveBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_view_all_animals_profile);

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
        pic = findViewById(R.id.profile_image);
        testLapBtn = findViewById(R.id.test_lap_btn);
        cancelBtn = findViewById(R.id.cancel_btn);
        saveBtn = findViewById(R.id.save_btn);
        rotateLoading = findViewById(R.id.rotateloading);


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

        testLapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DoctorViewAllAnimalsProfile.this, TestLapActivity.class);
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
                        .start(DoctorViewAllAnimalsProfile.this);
            }
        });
    }
}