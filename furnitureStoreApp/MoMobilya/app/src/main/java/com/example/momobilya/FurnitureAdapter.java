package com.example.momobilya;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.momobilya.databinding.RecyclerRowBinding;

import java.util.ArrayList;

public class FurnitureAdapter extends RecyclerView.Adapter<FurnitureAdapter.FurnitureHolder> {

    ArrayList<Furniture> furnitureArrayList;

    public FurnitureAdapter(ArrayList<Furniture> furnitureArrayList){
        this.furnitureArrayList=furnitureArrayList;
    }
    //connection with holder
    @NonNull
    @Override
    public FurnitureHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerRowBinding recyclerRowBinding=RecyclerRowBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new FurnitureHolder(recyclerRowBinding);
    }

    @SuppressLint("RecyclerView")
    @Override
    public void onBindViewHolder(@NonNull FurnitureHolder holder, int position) {
        holder.recyclerRowBinding.recyclerViewTextView.setText("Type: "+furnitureArrayList.get(position).name +" - "+"Brand: "+furnitureArrayList.get(position).brand);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(holder.itemView.getContext(),FurnitureActivity.class);
                intent.putExtra("info","old");
                intent.putExtra("furnitureId",furnitureArrayList.get(position).id);
                holder.itemView.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return furnitureArrayList.size();
    }

    public class FurnitureHolder extends RecyclerView.ViewHolder{
        private RecyclerRowBinding recyclerRowBinding;
        public FurnitureHolder(RecyclerRowBinding recyclerRowBinding) {
            super(recyclerRowBinding.getRoot());
            this.recyclerRowBinding=recyclerRowBinding;
        }
    }
}
