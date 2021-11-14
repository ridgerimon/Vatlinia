package com.example.vetlinia.ActivitiesAndFragments;

import static android.content.Context.MODE_PRIVATE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.balysv.materialripple.MaterialRippleLayout;
import com.example.vetlinia.DoctorApp.DoctorMainActivity;
import com.example.vetlinia.Model.DoctorModel;
import com.example.vetlinia.Model.PatientModel;
import com.example.vetlinia.R;
import com.example.vetlinia.databinding.SignInFragmentBinding;
import com.example.vetlinia.databinding.SignUpFragmentBinding;
import com.example.vetlinia.patientApp.AnimalProfileActivity;
import com.example.vetlinia.patientApp.PatientMainActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class SignupFragment extends Fragment {
    public static String EXTRA_PROFILE_TAG = "profile_tag";
    static EditText first_name, last_name, email_address, password, phone_number, closest_number, address, date_edittext;
    //MaterialRippleLayout doctor_sign_up,patient_sign_up;
    //Button paramedic_btn;//guest_btn;

    //CircleImageView profile_image;
    View view;
    SignUpFragmentBinding binding;
    Uri photoPath;
    ProgressDialog progressDialog;
    FirebaseAuth auth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    SharedPreferences loginPreferences;
    SharedPreferences.Editor loginPrefsEditor;
    SharedPreferences preferences;
    // doctor dialog
    CircleImageView profile_image;
    String first_name_txt, last_name_txt, getAddress_txt, full_name_txt, date_txt, email_txt, password_txt, mobile_txt, address_txt, closest_txt;
    Spinner specialization_spinner, gander_spinner;
    Button sign_up_btn, cancel_btn, hospitalLocation;
    String specialization_txt, gender_text;
    String selectedimageurl, selectedimageurl2;
    ImageView date_picker;

    Boolean saveLogin;

    FusedLocationProviderClient fusedLocationProviderClient;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = SignUpFragmentBinding.inflate(getLayoutInflater());

        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        preferences = getContext().getSharedPreferences("Address", Context.MODE_PRIVATE);

        loginPreferences = getActivity().getSharedPreferences("loginPrefs", MODE_PRIVATE);
        loginPrefsEditor = loginPreferences.edit();

        auth = FirebaseAuth.getInstance();

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        databaseReference.keepSynced(true);

        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference().child("images");

        binding.doctorSignUpCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDoctorDialog();
            }
        });

        binding.patientSignUpCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAnimalDialog();
            }
        });

    }

    private void showDoctorDialog() {
        final Dialog dialog = new Dialog(getActivity());

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.doctor_register_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes();
        dialog.setCancelable(false);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        profile_image = dialog.findViewById(R.id.profile_image);
        first_name = dialog.findViewById(R.id.first_name_field);
        last_name = dialog.findViewById(R.id.last_name_field);
        email_address = dialog.findViewById(R.id.email_field);
        password = dialog.findViewById(R.id.password_field);
        phone_number = dialog.findViewById(R.id.mobile_field);
        specialization_spinner = dialog.findViewById(R.id.specialization_spinner);
        gander_spinner = dialog.findViewById(R.id.Gender_spinner);
        hospitalLocation = dialog.findViewById(R.id.hospital_location_up_btn);

        sign_up_btn = dialog.findViewById(R.id.sign_up_btn);
        cancel_btn = dialog.findViewById(R.id.cancel_btn);

        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(getContext(),
                R.array.department, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(getContext(),
                R.array.gender, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        specialization_spinner.setAdapter(adapter1);
        gander_spinner.setAdapter(adapter2);

        specialization_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                specialization_txt = String.valueOf(parent.getItemAtPosition(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        gander_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                gender_text = String.valueOf(parent.getItemAtPosition(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        sign_up_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                first_name_txt = first_name.getText().toString();
                last_name_txt = last_name.getText().toString();
                full_name_txt = first_name_txt + " " + last_name_txt;
                email_txt = email_address.getText().toString();
                password_txt = password.getText().toString();
                mobile_txt = phone_number.getText().toString();
                getAddress_txt = preferences.getString("Address", "empty");

                if (TextUtils.isEmpty(first_name_txt)) {
                    Toast.makeText(getContext(), "please enter your first name", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(last_name_txt)) {
                    Toast.makeText(getContext(), "please enter your last name", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(email_txt)) {
                    Toast.makeText(getContext(), "please enter your email", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password_txt)) {
                    Toast.makeText(getContext(), "please enter your password", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(mobile_txt)) {
                    Toast.makeText(getContext(), "please enter your mobile number", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (photoPath == null) {
                    Toast.makeText(getContext(), "please add your picture", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (specialization_txt.equals("Select your specialty")) {
                    Toast.makeText(getContext(), "please select your specialization", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (gender_text.equals("Select your Gender")) {
                    Toast.makeText(getContext(), "please select your Gender", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (getAddress_txt.isEmpty()) {
                    Toast.makeText(getContext(), "please set your hospital location", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressDialog = new ProgressDialog(getContext());
                progressDialog.setTitle("Doctor Sign Up");
                progressDialog.setMessage("Please Wait Until Creating Account ...");
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.show();
                progressDialog.setCancelable(false);

                //Toast.makeText(getContext(), full_name_txt, Toast.LENGTH_SHORT).show();

                CreateDoctorAccount(email_txt, password_txt, full_name_txt, mobile_txt, specialization_txt, getAddress_txt);

                loginPrefsEditor.putBoolean("savepassword", true);
                loginPrefsEditor.putString("pass", password_txt);
                loginPrefsEditor.apply();

                //CustomerRegister(full_name_txt,email_txt,password_txt,mobile_txt,"Customer");
            }
        });

        hospitalLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), MapsActivity.class);
                startActivity(intent);
            }
        });

        profile_image.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON_TOUCH)
                        .setAspectRatio(1, 1)
                        .start(getContext(), SignupFragment.this);
            }
        });

        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    private void showAnimalDialog() {
        final Dialog dialog = new Dialog(getActivity());

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.patient_register_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes();
        dialog.setCancelable(false);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        profile_image = dialog.findViewById(R.id.profile_image);
        first_name = dialog.findViewById(R.id.first_name_field);
        last_name = dialog.findViewById(R.id.last_name_field);
        email_address = dialog.findViewById(R.id.email_field);
        password = dialog.findViewById(R.id.password_field);
        phone_number = dialog.findViewById(R.id.mobile_field);
        closest_number = dialog.findViewById(R.id.closest_mobile_field);
        address = dialog.findViewById(R.id.address_field);


        date_picker = dialog.findViewById(R.id.date_picker);
        date_edittext = dialog.findViewById(R.id.date_field);

        sign_up_btn = dialog.findViewById(R.id.sign_up_btn);
        cancel_btn = dialog.findViewById(R.id.cancel_btn);

        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(getContext(),
                R.array.bloodtypes, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner

        sign_up_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                first_name_txt = first_name.getText().toString();
                last_name_txt = last_name.getText().toString();
                full_name_txt = first_name_txt + " " + last_name_txt;
                email_txt = email_address.getText().toString();
                password_txt = password.getText().toString();
                mobile_txt = phone_number.getText().toString();
                closest_txt = closest_number.getText().toString();
                address_txt = address.getText().toString();

                date_txt = date_edittext.getText().toString();

                if (TextUtils.isEmpty(first_name_txt)) {
                    Toast.makeText(getContext(), "please enter your first name", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(last_name_txt)) {
                    Toast.makeText(getContext(), "please enter your last name", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(email_txt)) {
                    Toast.makeText(getContext(), "please enter your email", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password_txt)) {
                    Toast.makeText(getContext(), "please enter your password", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(mobile_txt)) {
                    Toast.makeText(getContext(), "please enter your mobile number", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(closest_txt)) {
                    Toast.makeText(getContext(), "please enter your closest mobile number", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(address_txt)) {
                    Toast.makeText(getContext(), "please enter your address", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(date_txt)) {
                    Toast.makeText(getContext(), "please pick or enter your birth date", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (photoPath == null) {
                    Toast.makeText(getContext(), "please add your picture", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressDialog = new ProgressDialog(getContext());
                progressDialog.setTitle("Patient Sign Up");
                progressDialog.setMessage("Please Wait Until Creating Account ...");
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.show();
                progressDialog.setCancelable(false);

                //Toast.makeText(getContext(), full_name_txt, Toast.LENGTH_SHORT).show();

                CreatePatientAccount(full_name_txt, email_txt, password_txt, date_txt, closest_txt, mobile_txt, address_txt, selectedimageurl, selectedimageurl2);

                loginPrefsEditor.putBoolean("savepassword", true);
                loginPrefsEditor.putString("pass", password_txt);
                loginPrefsEditor.apply();

                //CustomerRegister(full_name_txt,email_txt,password_txt,mobile_txt,"Customer");
            }
        });

        date_picker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getFragmentManager(), "datePicker");
            }
        });

        profile_image.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON_TOUCH)
                        .setAspectRatio(1, 1)
                        .start(getContext(), SignupFragment.this);
            }
        });

        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    /*public static void replaceFragment(Fragment from, Fragment to, boolean save) {
        if (save) {
            from
                    .requireActivity()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, to)
                    .addToBackStack(null)
                    .commit();
        } else {
            from
                    .requireActivity()
                    .getSupportFragmentManager()
                    .popBackStack();

            from
                    .requireActivity()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, to)
                    .disallowAddToBackStack()
                    .commit();
        }
    }*/

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
                            .into(profile_image);
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    private void CreatePatientAccount(final String fullname, final String email, String password, final String birthdate, final String closemobile, final String mobile, final String address, final String imageurl, final String imageurl2) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        auth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    String id = FirebaseAuth.getInstance().getCurrentUser().getUid();

                                    uploadPatientImage(id, fullname, email, birthdate, closemobile, mobile, address);
                                } else {
                                    String error_message = task.getException().getMessage();
                                    Toast.makeText(getContext(), error_message, Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                }
                            }
                        });

                    }
                });
    }

    private void uploadPatientImage(final String user_id, final String fullname, final String email, final String birthdate, final String closemobile, final String mobile, final String address) {
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

                selectedimageurl = downloadUri.toString();

                AddPatienttoDB(user_id, fullname, email, birthdate, closemobile, mobile, address, selectedimageurl);
                progressDialog.dismiss();

               /* Intent intent = new Intent(getContext(), PatientMainActivity.class);
                intent.putExtra(EXTRA_PROFILE_TAG, 123);
                startActivity(intent);*/
                Toast.makeText(getContext(), "Successfully", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Toast.makeText(getContext(), "Can't Upload Photo", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }

    private void AddPatienttoDB(String user_id, String fullname, String email, String birthdate, String closemobile, String mobile, String address, String imageurl) {
        PatientModel patientModel = new PatientModel(fullname, user_id, email, birthdate, closemobile, mobile, address, imageurl);

        databaseReference.child("Patients").child(getUID()).setValue(patientModel);
        databaseReference.child("AllUsers").child("Patients").child(getUID()).setValue(patientModel);
    }

    private void uploadImage(final String fullname, final String email, final String mobilenumber, final String specialty,  String get_address) {
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

                selectedimageurl = downloadUri.toString();

                AddDoctorToDB(fullname, email, mobilenumber, specialty, selectedimageurl, get_address);
                progressDialog.dismiss();

                //Intent intent = new Intent(getContext(), DoctorMainActivity.class);
                //startActivity(intent);
                Toast.makeText(getContext(), "successfully", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Toast.makeText(getContext(), "Can't Upload Photo", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }

    private void CreateDoctorAccount(final String email, String password, final String fullname, final String mobilenumber, final String specialty, String get_address) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        auth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    uploadImage(fullname, email, mobilenumber, specialty, get_address);
                                } else {
                                    String error_message = task.getException().getMessage();
                                    Toast.makeText(getContext(), error_message, Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                }
                            }
                        });
                    }
                });
    }

    private void AddDoctorToDB(String fullname, String email, String mobilenumber, String specialty, String imageurl,  String get_address) {
        DoctorModel doctorModel = new DoctorModel(fullname, email, mobilenumber, gender_text, get_address, specialty, imageurl);

        databaseReference.child("Doctors").child(specialty).child(getUID()).setValue(doctorModel);
        databaseReference.child("AllUsers").child("Doctors").child(getUID()).setValue(doctorModel);
    }

    private String getUID() {
        String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        return id;
    }

    /*private void maps() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext());
        GPStracker gps;
        gps = new GPStracker(getContext());
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //gps.getLocation();
            Toast.makeText(getContext(), String.valueOf(gps.latitude), Toast.LENGTH_SHORT).show();
        } else {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }

    }*/

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
            date_edittext.setText(day + "/" + month2 + "/" + year);
        }
    }

}