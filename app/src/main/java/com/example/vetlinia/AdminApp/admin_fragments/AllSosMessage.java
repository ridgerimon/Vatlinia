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

import com.example.vetlinia.AdminApp.AnimalProfileSos;
import com.example.vetlinia.Model.AddAnimalModel;
import com.example.vetlinia.Model.sosModel;
import com.example.vetlinia.R;
import com.example.vetlinia.databinding.AllAdminSosBinding;
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

public class AllSosMessage extends Fragment
{
    AllAdminSosBinding binding;

    ArrayList<sosModel> sosModelsMain = new ArrayList<sosModel>();
    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = AllAdminSosBinding.inflate(getLayoutInflater());
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
        databaseReference.child("SosAnimals").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                sosModelsMain.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    sosModel sosModels = postSnapshot.getValue(sosModel.class);
                    assert sosModels != null;
                    if(sosModels.getIsAccepted().equals("0"))
                    {
                        sosModelsMain.add(sosModels);
                    }
                }
                binding.allPatientRecyclerview.setAdapter(new AllAnimalsAdapter(sosModelsMain));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public class AllAnimalsAdapter extends RecyclerView.Adapter<AllAnimalsAdapter.ViewHolder> {

        List<sosModel> sosModels;

        public AllAnimalsAdapter(List<sosModel> sosModels) {
            this.sosModels = sosModels;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.sos_item, null);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {

            databaseReference.child("AllAnimals").child(sosModels.get(position).getAnimalKey()).addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            // Get user value
                            AddAnimalModel addAnimalModel = dataSnapshot.getValue(AddAnimalModel.class);
                            holder.nameAnimal.setText("Name: " + addAnimalModel.getName());
                            holder.sub.setText("Sub: " + addAnimalModel.getSub());
                            holder.animalOwner.setText("Message: " + sosModels.get(position).getMessage());
                            holder.address.setText(sosModels.get(position).getAddress());
                            if (addAnimalModel.getDogPic1() != "") {
                                Picasso
                                        .get()
                                        .load(addAnimalModel.getDogPic1())
                                        .placeholder(R.drawable.logologo)
                                        .error(R.drawable.logologo)
                                        .into(holder.animalImage);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Toast.makeText(getContext(), "can't fetch data", Toast.LENGTH_SHORT).show();

                        }
                    });


            holder.viewProfileAnimalBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(getContext(), AnimalProfileSos.class);
                    intent.putExtra("animalID", sosModels.get(position).getAnimalKey());
                    intent.putExtra("sosId", sosModels.get(position).getKey());
                    startActivity(intent);
                }
            });


        }

        @Override
        public int getItemCount() {
            return sosModels.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            TextView nameAnimal;
            TextView sub;
            TextView animalOwner;
            TextView address;
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
                address = itemView.findViewById(R.id.address);
            }

            @Override
            public void onClick(View v) {

            }
        }
    }
}
