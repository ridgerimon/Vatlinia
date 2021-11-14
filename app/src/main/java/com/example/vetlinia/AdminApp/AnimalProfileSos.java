package com.example.vetlinia.AdminApp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.vetlinia.Model.AddAnimalModel;
import com.example.vetlinia.Model.sosModel;
import com.example.vetlinia.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.victor.loading.rotate.RotateLoading;

import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class AnimalProfileSos extends AppCompatActivity {

    EditText name, age, sub, species, notes, bloodType, gender, type;
    LinearLayout cancelSave;
    CircleImageView pic;
    RotateLoading rotateLoading;
    Button editBtn, cancelBtn, acceptBtn;
    Uri photoPath;

    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animal_profile_sos);


        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        databaseReference.keepSynced(true);


        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference().child("images");

        initViews();
        /*
        getIntent().getStringExtra("name")
         */
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
        pic = findViewById(R.id.profile_image);
        editBtn = findViewById(R.id.edit_btn);
        cancelBtn = findViewById(R.id.cancel_btn);
        acceptBtn = findViewById(R.id.accept_btn);
        rotateLoading = findViewById(R.id.rotateloading);

        databaseReference.child("AllAnimals").child(getIntent().getStringExtra("animalID")).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get user value
                        AddAnimalModel addAnimalModel = dataSnapshot.getValue(AddAnimalModel.class);
                        name.setText(addAnimalModel.getName());
                        age.setText(addAnimalModel.getAge());
                        sub.setText(addAnimalModel.getSub());
                        species.setText(addAnimalModel.getSpecies());
                        notes.setText(addAnimalModel.getNotes());
                        bloodType.setText(addAnimalModel.getBloodGroup());
                        gender.setText(addAnimalModel.getGender());
                        type.setText(addAnimalModel.getType());

                        if (addAnimalModel.getDogPic1() != "") {
                            Picasso
                                    .get()
                                    .load(addAnimalModel.getDogPic1())
                                    .placeholder(R.drawable.logologo)
                                    .error(R.drawable.logologo)
                                    .into(pic);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(AnimalProfileSos.this, "can't fetch data", Toast.LENGTH_SHORT).show();
                    }
                });

        name.setEnabled(false);
        age.setEnabled(false);
        sub.setEnabled(false);
        species.setEnabled(false);
        notes.setEnabled(false);
        bloodType.setEnabled(false);
        gender.setEnabled(false);
        type.setEnabled(false);
        pic.setEnabled(false);

        acceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(AnimalProfileSos.this)
                        .setTitle("Confirm This Request")
                        .setMessage("Are You Sure To confirm It ?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                databaseReference.child("SosAnimals").child(getIntent().getStringExtra("sosId")).addListenerForSingleValueEvent(
                                        new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                // Get user value
                                                sosModel sosModels = dataSnapshot.getValue(sosModel.class);
                                                String uri = String.format(Locale.ENGLISH, "geo:%f,%f", Double.valueOf(sosModels.getLat()), Double.valueOf(sosModels.getLong()));
                                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                                                AnimalProfileSos.this.startActivity(intent);

                                                sosModel sosModelUpdate = new sosModel(sosModels.getLat(), sosModels.getLong(), sosModels.getAddress(), sosModels.getAnimalKey(), sosModels.getPatientID(), sosModels.getKey(), "1", sosModels.getMessage());
                                                databaseReference.child("SosAnimalsPatient").child(sosModels.getPatientID()).child(sosModels.getKey()).setValue(sosModelUpdate);
                                                databaseReference.child("SosAnimals").child(sosModels.getKey()).setValue(sosModelUpdate);
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {
                                                Toast.makeText(AnimalProfileSos.this, "can't fetch data", Toast.LENGTH_SHORT).show();
                                            }
                                        });





                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });
    }
}