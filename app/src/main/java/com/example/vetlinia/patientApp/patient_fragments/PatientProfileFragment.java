package com.example.vetlinia.patientApp.patient_fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.example.vetlinia.ActivitiesAndFragments.SignupFragment;
import com.example.vetlinia.ActivitiesAndFragments.SplashScreen;
import com.example.vetlinia.Model.PatientModel;
import com.example.vetlinia.R;
import com.example.vetlinia.databinding.PatientProfileFragmentBinding;
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

import java.util.Calendar;


public class PatientProfileFragment extends Fragment
{
    static PatientProfileFragmentBinding binding;

    String full_name_txt, email_txt, mobile_txt, address_txt, profile_image_url, birthDate, closeMobile, selected_placeImageURL;
    Uri photoPath;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    FirebaseStorage firebaseStorage;
    StorageReference storageReference;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       binding = PatientProfileFragmentBinding.inflate(getLayoutInflater());

        return binding.getRoot();

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initViews();
    }

    private void initViews() {

        binding.saveCard.setVisibility(View.GONE);
        binding.emailField.setEnabled(false);
        binding.fullnameField.setEnabled(false);
        binding.mobileField.setEnabled(false);
        binding.addressField.setEnabled(false);
        binding.patientProfilePicture.setEnabled(false);
        binding.savechangesBtn.setEnabled(false);
        binding.closestMobileField.setEnabled(false);
        binding.datebirthField.setEnabled(false);
        binding.datePicker.setEnabled(false);

        binding.savechangesBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                full_name_txt = binding.fullnameField.getText().toString();
                email_txt = binding.emailField.getText().toString();
                mobile_txt = binding.mobileField.getText().toString();
                address_txt = binding.addressField.getText().toString();
                birthDate = binding.datebirthField.getText().toString();
                closeMobile = binding.closestMobileField.getText().toString();

                if (TextUtils.isEmpty(full_name_txt)) {
                    Toast.makeText(getContext(), "please enter your full name", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(email_txt)) {
                    Toast.makeText(getContext(), "please enter your email", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(mobile_txt)) {
                    Toast.makeText(getContext(), "please enter your mobile number", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(address_txt)) {
                    Toast.makeText(getContext(), "please enter your address", Toast.LENGTH_SHORT).show();
                    return;
                }

                binding.saveCard.setVisibility(View.GONE);
                binding.fullnameField.setEnabled(false);
                binding.mobileField.setEnabled(false);
                binding.addressField.setEnabled(false);
                binding.patientProfilePicture.setEnabled(false);
                binding.savechangesBtn.setEnabled(false);
                binding.closestMobileField.setEnabled(false);
                binding.editProfileBtn.setEnabled(true);
                binding.datebirthField.setEnabled(false);
                binding.datePicker.setEnabled(false);

                if (photoPath == null) {
                    UpdatePatientProfile(full_name_txt, email_txt, mobile_txt, birthDate, closeMobile, address_txt, profile_image_url);
                } else {
                    uploadImage(full_name_txt, email_txt, mobile_txt, birthDate, closeMobile, address_txt);
                }
            }
        });

        binding.patientProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON_TOUCH)
                        .setAspectRatio(1, 1)
                        .start(getContext(), PatientProfileFragment.this);
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

        binding.editProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.saveCard.setVisibility(View.VISIBLE);
                binding.fullnameField.setEnabled(true);
                binding.mobileField.setEnabled(true);
                binding.addressField.setEnabled(true);
                binding.patientProfilePicture.setEnabled(true);
                binding.savechangesBtn.setEnabled(true);
                binding.editProfileBtn.setEnabled(false);
                binding.datebirthField.setEnabled(true);
                binding.datePicker.setEnabled(true);
            }
        });

        binding.datePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getFragmentManager(), "datePicker");
            }
        });

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        databaseReference.keepSynced(true);

        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference().child("images");

        binding.rotateloading.start();

        returndata();
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            return new DatePickerDialog(getActivity(), R.style.dialoge, this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            int month2 = month + 1;
            binding.datebirthField.setText(day + "/" + month2 + "/" + year);
        }
    }

    private void uploadImage(String full_name_txt, String email_txt, String mobile_txt, String birthDate, String closeMobile, String address_txt) {
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

                UpdatePatientProfile(full_name_txt, email_txt, mobile_txt, birthDate, address_txt, closeMobile,selected_placeImageURL);

                Toast.makeText(getContext(), "saved", Toast.LENGTH_SHORT).show();

                returndata();
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
    public void UpdatePatientProfile(String fullname, String email, String mobile, String birthDate, String closeMobile, String address, String imageurl) {
        PatientModel patientModel = new PatientModel(fullname, getUID(), email, birthDate, closeMobile, mobile, address, imageurl);

        databaseReference.child("Patients").child(getUID()).setValue(patientModel);
        databaseReference.child("AllUsers").child("Patients").child(getUID()).setValue(patientModel);

        Toast.makeText(getContext(), "saved", Toast.LENGTH_SHORT).show();

        if (photoPath == null) {
            returndata();
        }
    }

    public void returndata() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.keepSynced(true);

        final String userId = user.getUid();

        mDatabase.child("Patients").child(userId).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get user value
                        PatientModel patientModel = dataSnapshot.getValue(PatientModel.class);

                        mobile_txt = patientModel.getMobileNumber();
                        binding.mobileField.setText(mobile_txt);

                        binding.emailField.setText(patientModel.getEmail());

                        binding.fullnameField.setText(patientModel.getFullName());

                        binding.addressField.setText(patientModel.getAddress());

                        profile_image_url = patientModel.getImageUrl();

                        binding.datebirthField.setText(patientModel.getBirthdate());

                        binding.closestMobileField.setText(patientModel.getCloseMobileNumber());

                        Picasso.get()
                                .load(profile_image_url)
                                .placeholder(R.drawable.logologo)
                                .error(R.drawable.logologo)
                                .into(binding.patientProfilePicture);

                        binding.rotateloading.stop();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(getContext(), "can't fetch data", Toast.LENGTH_SHORT).show();
                        binding.rotateloading.stop();
                    }
                });
    }

    private String getUID() {
        String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        return id;
    }
}
