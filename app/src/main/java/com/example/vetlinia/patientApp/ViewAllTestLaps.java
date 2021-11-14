package com.example.vetlinia.patientApp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vetlinia.DoctorApp.TestLapActivity;
import com.example.vetlinia.Model.AddAnimalModel;
import com.example.vetlinia.Model.LapTestModel;
import com.example.vetlinia.R;
import com.example.vetlinia.databinding.AddAnimalDialogBinding;
import com.example.vetlinia.databinding.AddTestLapDialogBinding;
import com.example.vetlinia.patientApp.patient_fragments.addAnimalFragment;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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

import java.util.ArrayList;
import java.util.List;

public class ViewAllTestLaps extends AppCompatActivity {

    RecyclerView TestLapRecyclerView;
    FloatingActionButton addTestLap;

    AddTestLapDialogBinding binding;

    String description, TestLapImage;
    Uri photoPath;
    ProgressDialog progressDialog;

    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;

    ArrayList<LapTestModel> lapTestModels = new ArrayList<LapTestModel>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_test_laps);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        databaseReference.keepSynced(true);


        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference().child("images");

        initViews();
    }

    private void initViews() {
        TestLapRecyclerView = findViewById(R.id.test_lap_recyclerview);
        addTestLap = findViewById(R.id.fab);

        TestLapRecyclerView.setLayoutManager(new LinearLayoutManager(ViewAllTestLaps.this));

        addTestLap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAnimalDialog();
            }
        });

        databaseReference.child("AllTestLaps").child(getIntent().getStringExtra("animalID"))
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                lapTestModels.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    LapTestModel lapTestModel = postSnapshot.getValue(LapTestModel.class);
                    lapTestModels.add(lapTestModel);
                }
                TestLapRecyclerView.setAdapter(new TestLApAdapter(lapTestModels));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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
                            .into(binding.testLapImage);
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    private void showAnimalDialog() {

        final Dialog dialog = new Dialog(ViewAllTestLaps.this);


        binding = AddTestLapDialogBinding.inflate(getLayoutInflater());


        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(binding.getRoot());
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes();
        dialog.setCancelable(false);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        binding.testLapImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON_TOUCH)
                        .setAspectRatio(1, 1)
                        .start(ViewAllTestLaps.this);
            }
        });

       binding.addBtn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               description = binding.descriptionTestLapField.getText().toString();

               if (TextUtils.isEmpty(description)) {
                   Toast.makeText(ViewAllTestLaps.this, "please write description for doctor", Toast.LENGTH_SHORT).show();
                   return;
               }
               if (photoPath == null) {
                   Toast.makeText(ViewAllTestLaps.this, "please add animal picture", Toast.LENGTH_SHORT).show();
                   return;
               }

               progressDialog = new ProgressDialog(ViewAllTestLaps.this);
               progressDialog.setTitle("Adding your animal Lap Test");
               progressDialog.setMessage("Please Wait Until Finish ...");
               progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
               progressDialog.show();
               progressDialog.setCancelable(false);

               UploadLapTest(photoPath, description, dialog);
           }
       });

       binding.cancelBtn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               dialog.dismiss();
           }
       });

        dialog.show();
        dialog.getWindow().setAttributes(lp);

    }

    private void UploadLapTest(Uri photoPath, String description, Dialog dialog) {
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

                TestLapImage = downloadUri.toString();

                uploadDataTestLap(description, TestLapImage);
                progressDialog.dismiss();
                dialog.dismiss();

                Toast.makeText(ViewAllTestLaps.this, "successfully", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Toast.makeText(ViewAllTestLaps.this, "Can't Upload Photo", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }

    private void uploadDataTestLap(String description, String testLapImage)
    {
        lapTestModels.clear();
        String key = databaseReference.push().getKey();
        LapTestModel lapTestModel = new LapTestModel(testLapImage,description,"", key);
        databaseReference.child("AllTestLaps").child(getIntent().getStringExtra("animalID")).child(key).setValue(lapTestModel);
    }

    public class TestLApAdapter extends RecyclerView.Adapter<TestLApAdapter.ViewHolder> {
        List<LapTestModel> lapTestModels;

        public TestLApAdapter(List<LapTestModel> lapTestModels) {
            this.lapTestModels = lapTestModels;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(ViewAllTestLaps.this).inflate(R.layout.test_lap_item, null);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
            holder.doctorComment.setEnabled(false);
            holder.saveBtn.setVisibility(View.GONE);
            holder.doctorComment.setText(lapTestModels.get(position).getDoctorComment());
            holder.descriptionForDoctor.setText(lapTestModels.get(position).getDescription());

            if (lapTestModels.get(position).getImageUrl().toString() != "") {
                Picasso
                        .get()
                        .load(lapTestModels.get(position).getImageUrl())
                        .placeholder(R.drawable.logologo)
                        .error(R.drawable.logologo)
                        .into(holder.testLapImage);
            }

        }

        @Override
        public int getItemCount() {
            return lapTestModels.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            ImageView testLapImage;
            TextView descriptionForDoctor;
            EditText doctorComment;
            Button saveBtn;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                testLapImage = itemView.findViewById(R.id.test_lap_picture);
                descriptionForDoctor = itemView.findViewById(R.id.description_test_lap);
                doctorComment = itemView.findViewById(R.id.notes_test_lap);
                saveBtn = itemView.findViewById(R.id.save_lap_test_btn);
            }

            @Override
            public void onClick(View v) {

            }
        }
    }
}