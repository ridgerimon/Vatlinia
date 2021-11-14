package com.example.vetlinia.patientApp.patient_fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vetlinia.ActivitiesAndFragments.GPStracker;
import com.example.vetlinia.Model.AddAnimalModel;
import com.example.vetlinia.Model.sosModel;
import com.example.vetlinia.R;
import com.example.vetlinia.databinding.AddAnimalDialogBinding;
import com.example.vetlinia.databinding.AddAnimalFragmentBinding;
import com.example.vetlinia.databinding.SosDialogPatientBinding;
import com.example.vetlinia.patientApp.AnimalProfileActivity;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
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
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class addAnimalFragment extends Fragment {
    AddAnimalFragmentBinding binding;
    AddAnimalDialogBinding bindingDialog;
    SosDialogPatientBinding bindingSos;


    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;

    String name_txt, age, gender, notes, bloodGroup, type, sub, species, AnimalPhoto, message_text;
    Uri photoPath;
    ProgressDialog progressDialog;
    ArrayList<AddAnimalModel> addAnimalModels = new ArrayList<AddAnimalModel>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = AddAnimalFragmentBinding.inflate(getLayoutInflater());

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
        binding.myAnimalsRecyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAnimalDialog();
            }
        });

        databaseReference.child("AllAnimalsPatient").child(getUID()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                addAnimalModels.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    AddAnimalModel addAnimalModel = postSnapshot.getValue(AddAnimalModel.class);
                    addAnimalModels.add(addAnimalModel);
                }
                binding.myAnimalsRecyclerview.setAdapter(new MyAnimalsAdapter(addAnimalModels));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void showAnimalDialog() {

        final Dialog dialog = new Dialog(getActivity());


        bindingDialog = AddAnimalDialogBinding.inflate(getLayoutInflater());


        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(bindingDialog.getRoot());
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes();
        dialog.setCancelable(false);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(getContext(),
                R.array.bloodtypes, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner

        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(getContext(),
                R.array.type, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner

        ArrayAdapter<CharSequence> adapter3 = ArrayAdapter.createFromResource(getContext(),
                R.array.gender, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner

        bindingDialog.bloodTypeSpinner.setAdapter(adapter1);
        bindingDialog.typeSpinner.setAdapter(adapter2);
        bindingDialog.genderSpinner.setAdapter(adapter3);

        bindingDialog.bloodTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                bloodGroup = String.valueOf(parent.getItemAtPosition(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        bindingDialog.typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                type = String.valueOf(parent.getItemAtPosition(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        bindingDialog.genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                gender = String.valueOf(parent.getItemAtPosition(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        bindingDialog.profileImage.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON_TOUCH)
                        .setAspectRatio(1, 1)
                        .start(getContext(), addAnimalFragment.this);
            }
        });

        bindingDialog.addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name_txt = bindingDialog.firstNameField.getText().toString();
                age = bindingDialog.ageField.getText().toString();
                sub = bindingDialog.supField.getText().toString();
                species = bindingDialog.speciesField.getText().toString();
                notes = bindingDialog.notesField.getText().toString();

                if (TextUtils.isEmpty(name_txt)) {
                    Toast.makeText(getContext(), "please enter animal name", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(age)) {
                    Toast.makeText(getContext(), "please enter animal name", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(gender)) {
                    Toast.makeText(getContext(), "please choose gender", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(type)) {
                    Toast.makeText(getContext(), "please choose type", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(bloodGroup)) {
                    Toast.makeText(getContext(), "please choose blood group", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(sub)) {
                    Toast.makeText(getContext(), "please enter sub", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(species)) {
                    Toast.makeText(getContext(), "please enter species", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (photoPath == null) {
                    Toast.makeText(getContext(), "please add animal picture", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressDialog = new ProgressDialog(getContext());
                progressDialog.setTitle("Adding your animal");
                progressDialog.setMessage("Please Wait Until Finish ...");
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.show();
                progressDialog.setCancelable(false);

                uploadAnimalPhoto(name_txt, age, sub, species, notes, type, gender, bloodGroup, photoPath, dialog);

            }
        });

        bindingDialog.cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);

    }

    private void uploadAnimalPhoto(String name_txt, String age, String sub, String species, String notes, String type, String gender, String bloodGroup, Uri photoPath, Dialog dialog) {
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

                AnimalPhoto = downloadUri.toString();

                uploadDataAnimal(name_txt, age, sub, species, notes, type, gender, bloodGroup, AnimalPhoto);
                progressDialog.dismiss();
                dialog.dismiss();

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

    private void uploadDataAnimal(String name_txt, String age, String sub, String species, String notes, String type, String gender, String bloodGroup, String photoUrl) {

        addAnimalModels.clear();
        String key = databaseReference.push().getKey();
        AddAnimalModel addAnimalModel = new AddAnimalModel(name_txt, age, bloodGroup, photoUrl, " ", sub, species, type, notes, gender, key,getUID());
        databaseReference.child("AllAnimalsPatient").child(getUID()).child(key).setValue(addAnimalModel);
        databaseReference.child("AllAnimals").child(key).setValue(addAnimalModel);
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
                            .into(bindingDialog.profileImage);
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

    public class MyAnimalsAdapter extends RecyclerView.Adapter<MyAnimalsAdapter.ViewHolder> {

        List<AddAnimalModel> addAnimalModelList;

        public MyAnimalsAdapter(List<AddAnimalModel> addAnimalModelList) {
            this.addAnimalModelList = addAnimalModelList;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.my_animal_item, null);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyAnimalsAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
            holder.nameAnimal.setText(addAnimalModelList.get(position).getName());
            holder.sub.setText(addAnimalModelList.get(position).getSpecies());
            holder.sosBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GPStracker gps;
                    // Create class object
                    gps = new GPStracker(getContext());
                    if(gps.canGetLocation()) {
                        double latitude = gps.getLatitude();
                        double longitude = gps.getLongitude();

                        Geocoder geocoder;
                        geocoder = new Geocoder(getContext(), Locale.getDefault());
                        List<Address> addresses;
                        String address = "";
                        try {
                            addresses = geocoder.getFromLocation(latitude, longitude, 1);
                            address = addresses.get(0).getAddressLine(0);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        if(address != ""){
                            showSosDialog(String.valueOf(latitude), String.valueOf(longitude), address, addAnimalModelList.get(position));
                        }

                        Toast.makeText(getContext(), address, Toast.LENGTH_LONG).show();

                      //  Toast.makeText(getContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
                    } else {
                        // Can't get location.
                        // GPS or network is not enabled.
                        // Ask user to enable GPS/network in settings.
                        gps.showSettingsAlert();
                    }
                }
            });
            holder.viewProfileAnimalBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AddAnimalModel addAnimalModel = addAnimalModelList.get(position);
                    Intent intent = new Intent(getContext(), AnimalProfileActivity.class);
                    intent.putExtra("name", addAnimalModel.getName());
                    intent.putExtra("age", addAnimalModel.getAge());
                    intent.putExtra("pic", addAnimalModel.getDogPic1());
                    intent.putExtra("species", addAnimalModel.getSpecies());
                    intent.putExtra("blood", addAnimalModel.getBloodGroup());
                    intent.putExtra("sub", addAnimalModel.getSub());
                    intent.putExtra("gender", addAnimalModel.getGender());
                    intent.putExtra("notes", addAnimalModel.getNotes());
                    intent.putExtra("type", addAnimalModel.getType());
                    intent.putExtra("animalID", addAnimalModel.getMyAnimalID());
                    startActivity(intent);
                }
            });
            if (addAnimalModelList.get(position).getDogPic1().toString() != "") {
                Picasso
                        .get()
                        .load(addAnimalModels.get(position).getDogPic1())
                        .placeholder(R.drawable.logologo)
                        .error(R.drawable.logologo)
                        .into(holder.animalImage);
            }
        }

        @Override
        public int getItemCount() {
            return addAnimalModels.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            TextView nameAnimal;
            TextView sub;
            CircleImageView animalImage;
            Button viewProfileAnimalBtn, sosBtn;


            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                nameAnimal = itemView.findViewById(R.id.animal_fullname);
                sub = itemView.findViewById(R.id.animal_specialty);
                animalImage = itemView.findViewById(R.id.animal_profile_picture);
                viewProfileAnimalBtn = itemView.findViewById(R.id.view_profile_btn);
                sosBtn = itemView.findViewById(R.id.sos_btn);
            }

            @Override
            public void onClick(View v) {

            }
        }
    }

    private void showSosDialog(String latitude, String longitude, String address, AddAnimalModel addAnimalModel) {

        final Dialog dialog = new Dialog(getActivity());


        bindingSos = SosDialogPatientBinding.inflate(getLayoutInflater());


        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(bindingSos.getRoot());
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes();
        dialog.setCancelable(false);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        bindingSos.sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                message_text = bindingSos.messageField.getText().toString();

                String key = databaseReference.push().getKey();
                sosModel sosModel = new sosModel(String.valueOf(latitude),String.valueOf(longitude),address,addAnimalModel.getMyAnimalID(),getUID(),key, "0", message_text);
                databaseReference.child("SosAnimalsPatient").child(getUID()).child(key).setValue(sosModel);
                databaseReference.child("SosAnimals").child(key).setValue(sosModel);
            }
        });

        bindingSos.cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);

    }
}
