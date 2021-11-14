package com.example.vetlinia.AdminApp.admin_fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vetlinia.Model.DoctorModel;
import com.example.vetlinia.Model.PatientModel;
import com.example.vetlinia.R;
import com.example.vetlinia.databinding.AllAdminPatientBinding;
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

import de.hdodenhof.circleimageview.CircleImageView;

public class AllPatient extends Fragment
{
    AllAdminPatientBinding binding;

    ArrayList<PatientModel> patientModels = new ArrayList<PatientModel>();
    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = AllAdminPatientBinding.inflate(getLayoutInflater());
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
        binding.allPatientRecyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
        databaseReference.child("AllUsers").child("Patients").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                patientModels.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    PatientModel patientModel = postSnapshot.getValue(PatientModel.class);
                    patientModels.add(patientModel);
                }
                binding.allPatientRecyclerview.setAdapter(new AllAnimalsAdapter(patientModels));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public class AllAnimalsAdapter extends RecyclerView.Adapter<AllAnimalsAdapter.ViewHolder> {

        List<PatientModel> patientModels;

        public AllAnimalsAdapter(List<PatientModel> patientModels) {
            this.patientModels = patientModels;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.all_animal_item, null);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
            /*databaseReference.child("Patients").child(doctorModels.get(position).getUserUID()).addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            // Get user value
                            PatientModel patientModel = dataSnapshot.getValue(PatientModel.class);


                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Toast.makeText(getContext(), "can't fetch data", Toast.LENGTH_SHORT).show();

                        }
                    });*/
            holder.nameAnimal.setText(patientModels.get(position).getFullName());
            holder.sub.setText(patientModels.get(position).getMobileNumber());
            holder.animalOwner.setText(patientModels.get(position).getEmail());

            holder.viewProfileAnimalBtn.setVisibility(View.GONE);

            if (patientModels.get(position).getImageUrl() != "") {
                Picasso
                        .get()
                        .load(patientModels.get(position).getImageUrl())
                        .placeholder(R.drawable.logologo)
                        .error(R.drawable.logologo)
                        .into(holder.animalImage);
            }
        }

        @Override
        public int getItemCount() {
            return patientModels.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            TextView nameAnimal;
            TextView sub;
            TextView animalOwner;
            CircleImageView animalImage;
            Button viewProfileAnimalBtn, sosBtn;


            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                nameAnimal = itemView.findViewById(R.id.animal_fullname);
                sub = itemView.findViewById(R.id.animal_specialty);
                animalImage = itemView.findViewById(R.id.animal_profile_picture);
                viewProfileAnimalBtn = itemView.findViewById(R.id.view_profile_btn);
                sosBtn = itemView.findViewById(R.id.sos_btn);
                animalOwner = itemView.findViewById(R.id.animal_owner);
            }

            @Override
            public void onClick(View v) {

            }
        }
    }
}
