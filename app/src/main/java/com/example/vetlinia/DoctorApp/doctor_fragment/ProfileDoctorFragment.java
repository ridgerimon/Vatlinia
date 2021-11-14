package com.example.vetlinia.DoctorApp.doctor_fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.example.vetlinia.ActivitiesAndFragments.SplashScreen;
import com.example.vetlinia.Model.DoctorModel;
import com.example.vetlinia.Model.PatientModel;
import com.example.vetlinia.R;
import com.example.vetlinia.databinding.DoctorProfileFragmentBinding;
import com.example.vetlinia.patientApp.patient_fragments.PatientProfileFragment;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class ProfileDoctorFragment extends Fragment {
    DoctorProfileFragmentBinding binding;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    FirebaseStorage firebaseStorage;
    StorageReference storageReference;

    DoctorModel doctorModel;

    Uri photoPath;

    String full_name_txt, email_txt, species_txt, gender, selected_placeImageURL, mobile_txt;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DoctorProfileFragmentBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        databaseReference.keepSynced(true);

        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference().child("images");

        initViews();
    }

    private void initViews() {
        binding.fullnameField.setEnabled(false);
        binding.emailField.setEnabled(false);
        binding.speciesField.setEnabled(false);
        binding.genderField.setEnabled(false);
        binding.profileImage.setEnabled(false);
        binding.mobileField.setEnabled(false);

        binding.editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.fullnameField.setEnabled(true);
                binding.emailField.setEnabled(false);
                binding.speciesField.setEnabled(true);
                binding.genderField.setEnabled(true);
                binding.profileImage.setEnabled(true);
                binding.mobileField.setEnabled(true);
            }
        });


        binding.savechangesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                full_name_txt = binding.fullnameField.getText().toString();
                email_txt = binding.emailField.getText().toString();
                species_txt = binding.speciesField.getText().toString();
                gender = binding.genderField.getText().toString();
                mobile_txt = binding.mobileField.getText().toString();

                binding.fullnameField.setEnabled(false);
                binding.emailField.setEnabled(false);
                binding.speciesField.setEnabled(false);
                binding.genderField.setEnabled(false);
                binding.profileImage.setEnabled(false);
                binding.mobileField.setEnabled(false);

                if (photoPath == null) {
                    UpdatePatientProfile(full_name_txt, email_txt, mobile_txt, gender, species_txt, selected_placeImageURL);
                } else {
                    uploadImage(full_name_txt, email_txt, mobile_txt, gender, species_txt);
                }
            }
        });

        binding.profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON_TOUCH)
                        .setAspectRatio(1, 1)
                        .start(getContext(), ProfileDoctorFragment.this);
            }
        });

        binding.signoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent intent = new Intent(getContext(), NFCActivity.class);
                startActivity(intent);*/
                FirebaseAuth.getInstance().signOut();

                Intent intent = new Intent(getContext(), SplashScreen.class);
                startActivity(intent);
            }
        });

        returnData();
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
                            .placeholder(R.drawable.profile)
                            .error(R.drawable.profile)
                            .into(binding.profileImage);
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    private void uploadImage(String fullname, String email, String mobile,  String gender, String specialization) {
        binding.rotateloading.start();

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

                binding.rotateloading.stop();

                selected_placeImageURL = downloadUri.toString();

                UpdatePatientProfile(fullname, email, mobile,  gender, specialization, selected_placeImageURL);

                Toast.makeText(getContext(), "saved", Toast.LENGTH_SHORT).show();

                returnData();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Toast.makeText(getContext(), "Can't Upload Photo", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void UpdatePatientProfile(String fullname, String email, String mobile,  String gender, String specialization, String imageurl) {
        DoctorModel doctorModel = new DoctorModel(fullname,email,mobile,gender,"",specialization,imageurl);

        databaseReference.child("Doctors").child(doctorModel.getSpecialization()).child(getUID()).setValue(doctorModel);
        databaseReference.child("AllUsers").child("Doctors").child(getUID()).setValue(doctorModel);

        Toast.makeText(getContext(), "saved", Toast.LENGTH_SHORT).show();

        if (photoPath == null) {
            returnData();
        }
    }

    private String getUID() {
        String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        return id;
    }


    public void returnData() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.keepSynced(true);

        final String userId = user.getUid();

        mDatabase.child("AllUsers").child("Doctors").child(userId).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get user value
                        doctorModel = dataSnapshot.getValue(DoctorModel.class);

                        binding.emailField.setText(doctorModel.getEmail());
                        binding.speciesField.setText(doctorModel.getSpecialization());
                        binding.genderField.setText(doctorModel.getGender());
                        binding.fullnameField.setText(doctorModel.getFullname());
                        binding.mobileField.setText(doctorModel.getMobilenumber());

                        selected_placeImageURL = doctorModel.getImageurl();

                        Picasso.get()
                                .load(doctorModel.getImageurl())
                                .placeholder(R.drawable.logologo)
                                .error(R.drawable.logologo)
                                .into(binding.profileImage);

                        binding.rotateloading.stop();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(getContext(), "can't fetch data", Toast.LENGTH_SHORT).show();
                        binding.rotateloading.stop();
                    }
                });
    }
}
