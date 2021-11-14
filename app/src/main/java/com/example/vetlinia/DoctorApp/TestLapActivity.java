package com.example.vetlinia.DoctorApp;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vetlinia.Model.LapTestModel;
import com.example.vetlinia.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class TestLapActivity extends AppCompatActivity {
    RecyclerView TestLapRecyclerView;

    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;

    ArrayList<LapTestModel> lapTestModels = new ArrayList<LapTestModel>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_lap);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        databaseReference.keepSynced(true);


        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference().child("images");

        initViews();
    }

    private void initViews() {
        TestLapRecyclerView = findViewById(R.id.test_lap_recyclerview);
        TestLapRecyclerView.setLayoutManager(new LinearLayoutManager(TestLapActivity.this));

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

    private void uploadDataTestLap(String description, String testLapImage, String doctorComment, String key) {
        lapTestModels.clear();
        LapTestModel lapTestModel = new LapTestModel(testLapImage, description, doctorComment, key);
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
            View view = LayoutInflater.from(TestLapActivity.this).inflate(R.layout.test_lap_item, null);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
            holder.doctorComment.setText(lapTestModels.get(position).getDoctorComment());
            holder.descriptionForDoctor.setText(lapTestModels.get(position).getDescription());

            holder.saveBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String descriptionDoctor;
                    descriptionDoctor = holder.doctorComment.getText().toString();
                    if (TextUtils.isEmpty(descriptionDoctor)) {
                        Toast.makeText(TestLapActivity.this, "please write your comment", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    uploadDataTestLap(lapTestModels.get(position).getDescription(), lapTestModels.get(position).getImageUrl(), descriptionDoctor, lapTestModels.get(position).getKey());
                }
            });

            if (lapTestModels.get(position).getImageUrl() != "") {
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