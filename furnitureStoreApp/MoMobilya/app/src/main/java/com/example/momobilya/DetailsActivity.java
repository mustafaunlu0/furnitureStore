package com.example.momobilya;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.momobilya.databinding.ActivityDetailsBinding;
import com.google.firebase.firestore.core.View;

import java.util.ArrayList;

public class DetailsActivity extends AppCompatActivity {
    private ActivityDetailsBinding binding;
    ArrayList<Furniture> furnitureArrayList;
    FurnitureAdapter furnitureAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        androidx.appcompat.widget.Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        furnitureArrayList=new ArrayList<>();
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        furnitureAdapter=new FurnitureAdapter(furnitureArrayList);
        binding.recyclerView.setAdapter(furnitureAdapter);

        getData();

    }

    private void getData() {
        try{
            SQLiteDatabase sqLiteDatabase=this.openOrCreateDatabase("FURNITURES",MODE_PRIVATE,null);

            Cursor cursor=sqLiteDatabase.rawQuery("SELECT * FROM Furnitures",null);
            int nameIx=cursor.getColumnIndex("name");
            int brandIx=cursor.getColumnIndex("brand");
            int idIx=cursor.getColumnIndex("id");

            while(cursor.moveToNext()){
                String name=cursor.getString(nameIx);
                String brand=cursor.getString(brandIx);
                int id=cursor.getInt(idIx);
                Furniture furniture=new Furniture(name,brand,id);
                furnitureArrayList.add(furniture);

            }
            furnitureAdapter.notifyDataSetChanged();
            cursor.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.menu,menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.add_item){
            Intent intent=new Intent(DetailsActivity.this,FurnitureActivity.class);
            intent.putExtra("info","new");
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}