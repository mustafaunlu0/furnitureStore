package com.example.momobilya;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import com.example.momobilya.databinding.ActivityFurnitureBinding;
import com.google.android.material.snackbar.Snackbar;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class FurnitureActivity extends AppCompatActivity {
    ActivityResultLauncher<Intent> activityResultLauncher;
    ActivityResultLauncher<String> permissionLauncher;
    private ActivityFurnitureBinding binding;
    Bitmap selectedImage;
    SQLiteDatabase database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityFurnitureBinding.inflate(getLayoutInflater());
        View view=binding.getRoot();
        setContentView(view);

        registerLauncher();

        database=this.openOrCreateDatabase("FURNITURES",MODE_PRIVATE,null);

        Intent intent=getIntent();
        String info=intent.getStringExtra("info");

        if(info.matches("new")){
            binding.saveButton.setVisibility(View.VISIBLE);
            binding.furnitureNameEditText.setText("");
            binding.furnitureBrandNameEditText.setText("");
            binding.furniturePriceEditText.setText("");
            binding.imageView4.setImageResource(R.drawable.select);

        }
        else{
            int furnitureId=intent.getIntExtra("furnitureId",0);
            binding.saveButton.setVisibility(View.INVISIBLE);

            try{
                Cursor cursor=database.rawQuery("SELECT * FROM Furnitures where id=?",new String[]{String.valueOf(furnitureId)});
                int fnameIx=cursor.getColumnIndex("name");
                int fbrandIx=cursor.getColumnIndex("brand");
                int fpriceIx=cursor.getColumnIndex("price");
                int fimageIx=cursor.getColumnIndex("image");

                while(cursor.moveToNext()){
                    binding.furnitureNameEditText.setText(cursor.getString(fnameIx));
                    binding.furnitureBrandNameEditText.setText(cursor.getString(fbrandIx));
                    binding.furniturePriceEditText.setText(cursor.getString(fpriceIx));

                    byte[] bytes=cursor.getBlob(fimageIx);
                    Bitmap bitmap= BitmapFactory.decodeByteArray(bytes,0,bytes.length);

                    binding.imageView4.setImageBitmap(bitmap);


                }
                cursor.close();
            }catch (Exception e){
                e.printStackTrace();
            }

        }

    }
    public void save(View view){
        String name=binding.furnitureNameEditText.getText().toString();
        String brand=binding.furnitureBrandNameEditText.getText().toString();
        String price=binding.furniturePriceEditText.getText().toString();

        Bitmap smallImage=makeSmallerImage(selectedImage,300);

        ByteArrayOutputStream outputStream=new ByteArrayOutputStream();
        smallImage.compress(Bitmap.CompressFormat.PNG,50,outputStream);
        byte[] byteArray=outputStream.toByteArray();

        try{
            database=this.openOrCreateDatabase("FURNITURES",MODE_PRIVATE,null);
            database.execSQL("CREATE TABLE IF NOT EXISTS Furnitures(id INTEGER PRIMARY KEY,name VARCHAR,brand VARCHAR,price VARCHAR,image BLOB)");

            String SQLString="INSERT INTO Furnitures(name,brand,price,image) VALUES(?,?,?,?)";
            SQLiteStatement sqLiteStatement=database.compileStatement(SQLString);
            sqLiteStatement.bindString(1,name);
            sqLiteStatement.bindString(2,brand);
            sqLiteStatement.bindString(3,price);
            sqLiteStatement.bindBlob(4,byteArray);
            sqLiteStatement.execute();

        }catch (Exception e){
            e.printStackTrace();
        }

        Intent intent=new Intent(FurnitureActivity.this,DetailsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
    public Bitmap makeSmallerImage(Bitmap image,int maximumSize){
        int width= image.getWidth();
        int height=image.getHeight();
        float bitmapRatio= (float) width / (float) height;
        if(bitmapRatio>1){
            //landscape image
            width=maximumSize;
            height= (int)(maximumSize/bitmapRatio);
        }
        else{
            //portrait image
            height=maximumSize;
            width=(int)(maximumSize*bitmapRatio);
        }
        return image.createScaledBitmap(image,width,height,true);
    }
    public void select(View view){
        Toast.makeText(this, "dsadas", Toast.LENGTH_SHORT).show();
        try{
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                //request permission
                if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_EXTERNAL_STORAGE)){
                    Snackbar.make(view,"Permission needed for gallery",Snackbar.LENGTH_INDEFINITE).setAction("Give permisson", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //request permission - launcher yazılacak
                            permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);

                        }
                    }).show();
                }
                else{
                    //request permission - launcher yazılacak
                    permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                }
            }
            else{
                //to gallery - launcher yazılacak
                Intent intent=new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                activityResultLauncher.launch(intent);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }




    }
    private void registerLauncher(){
        activityResultLauncher=registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if(result.getResultCode()==RESULT_OK){
                    Intent intentFromResult=result.getData();
                    if(intentFromResult!=null){
                        Uri imageData=intentFromResult.getData();
                        try{
                            if(Build.VERSION.SDK_INT>28){
                                ImageDecoder.Source source=ImageDecoder.createSource(getContentResolver(),imageData);
                                selectedImage=ImageDecoder.decodeBitmap(source);
                                binding.imageView4.setImageBitmap(selectedImage);
                            }
                            else{
                                selectedImage= MediaStore.Images.Media.getBitmap(getContentResolver(),imageData);
                                binding.imageView4.setImageBitmap(selectedImage);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

        permissionLauncher=registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {
                if(result){
                    //permission granted
                    Intent intentToGallery=new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    activityResultLauncher.launch(intentToGallery);
                }
                else{
                    //permission denied
                    Toast.makeText(FurnitureActivity.this, "Permisson needed", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}