package com.example.vetlinia.AdminApp.admin_fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vetlinia.DoctorApp.DoctorViewAllAnimalsProfile;
import com.example.vetlinia.DoctorApp.doctor_fragment.AllAnimalsDoctorFragment;
import com.example.vetlinia.Model.AddAnimalModel;
import com.example.vetlinia.Model.PatientModel;
import com.example.vetlinia.R;
import com.example.vetlinia.databinding.AllAdminAnimalsBinding;
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

public class AllAnimals extends Fragment
{
    AllAdminAnimalsBinding binding;

    ArrayList<AddAnimalModel> addAnimalModels = new ArrayList<AddAnimalModel>();
    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = AllAdminAnimalsBinding.inflate(getLayoutInflater());
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
        binding.allAnimalsRecyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
        databaseReference.child("AllAnimals").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                addAnimalModels.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    AddAnimalModel addAnimalModel = postSnapshot.getValue(AddAnimalModel.class);
                    addAnimalModels.add(addAnimalModel);
                }
                binding.allAnimalsRecyclerview.setAdapter(new AllAnimalsAdapter(addAnimalModels));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public class AllAnimalsAdapter extends RecyclerView.Adapter<AllAnimalsAdapter.ViewHolder> {

        List<AddAnimalModel> addAnimalModelList;

        public AllAnimalsAdapter(List<AddAnimalModel> addAnimalModelList) {
            this.addAnimalModelList = addAnimalModelList;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.all_animal_item, null);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
            databaseReference.child("Patients").child(addAnimalModelList.get(position).getUserUID()).addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            // Get user value
                            PatientModel patientModel = dataSnapshot.getValue(PatientModel.class);

                            holder.nameAnimal.setText(addAnimalModelList.get(position).getName());
                            holder.sub.setText(addAnimalModelList.get(position).getSpecies());
                            holder.animalOwner.setText(patientModel.getFullName());
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Toast.makeText(getContext(), "can't fetch data", Toast.LENGTH_SHORT).show();

                        }
                    });

            holder.viewProfileAnimalBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AddAnimalModel addAnimalModel = addAnimalModelList.get(position);
                    Intent intent = new Intent(getContext(), DoctorViewAllAnimalsProfile.class);
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

            if (addAnimalModelList.get(position).getDogPic1() != "") {
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
