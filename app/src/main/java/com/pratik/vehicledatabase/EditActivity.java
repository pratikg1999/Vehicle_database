package com.pratik.vehicledatabase;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EditActivity extends AppCompatActivity implements View.OnClickListener {


    private TextInputEditText etMake;
    private TextInputEditText etModel;
    private AutoCompleteTextView etVariant;
    private TextInputEditText etNumber;
    private ImageView ivPhotoEdit;
    private Button btSave;
    private Button btDelete;
    private ImageButton btImgEdit;
    Gson gson = new Gson();
    private boolean forNewVehicle;
    private Vehicle vehicle;
    private VehicleViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        etMake = findViewById(R.id.et_make);
        etModel = findViewById(R.id.et_model);
        etVariant = findViewById(R.id.et_variant);
        etNumber = findViewById(R.id.et_number);
        ivPhotoEdit = findViewById(R.id.iv_photo_edit);
        btSave = findViewById(R.id.bt_save);
        btDelete = findViewById(R.id.bt_delete);
        btImgEdit = findViewById(R.id.bt_img_edit);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.select_dialog_item, Constants.VARIANTS);


        viewModel = new ViewModelProvider(this).get(VehicleViewModel.class);
        Intent intent = getIntent();
        String vehicleJson = intent.getStringExtra(Constants.EDIT_VEHICLE_KEY);
        if (vehicleJson == null) {
            forNewVehicle = true;
            btDelete.setVisibility(View.GONE);
        } else {
            Type type = new TypeToken<Vehicle>() {}.getType();
            vehicle = gson.fromJson(vehicleJson, type);
            prePopulateWith(vehicle);
        }

//        etVariant.setThreshold(0);
        etVariant.setAdapter(adapter);
        btSave.setOnClickListener(this);
        btDelete.setOnClickListener(this);
        btImgEdit.setOnClickListener(this);
    }

    private void prePopulateWith(Vehicle vehicle){
        if(vehicle.getPhotoPath()!=null){
            ivPhotoEdit.setImageBitmap(BitmapFactory.decodeFile(vehicle.getPhotoPath()));
        }
        etMake.setText(vehicle.getMake());
        etModel.setText(vehicle.getModel());
        etVariant.setText(vehicle.getVariant());
        etNumber.setText(vehicle.getVehicleNumber());
    }

    private Vehicle createVehicleFromData() {
        return new Vehicle(etNumber.getText().toString(), etMake.getText().toString(), etModel.getText().toString(), etVariant.getText().toString(), saveBitmapFromImageView(ivPhotoEdit));
    }

    private boolean validate() {
        String curMake = etMake.getText().toString();
        String curModel = etModel.getText().toString();
        String curVariant = etVariant.getText().toString();
        String curNumber  = etNumber.getText().toString();
        boolean ans = true;
        if(curMake== null || curMake.trim().equals("")){
            etMake.setError("Can't be empty");
            etMake.requestFocus();
            ans = false;
        }
        if(curModel== null || curModel.trim().equals("")){
            etModel.setError("Can't be empty");
            etModel.requestFocus();
            ans = false;
        }
        if(curVariant== null || curVariant.trim().equals("")){
            etVariant.setError("Can't be empty");
            etVariant.requestFocus();
            ans = false;
        }
        if(curNumber== null || curNumber.trim().equals("")){
            etNumber.setError("Can't be empty");
            etNumber.requestFocus();
            ans = false;
        }
        return ans;

    }

    private String saveBitmapFromImageView(ImageView view){
        try {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) view.getDrawable();
            if (bitmapDrawable == null) {
                return null;
            }
            Bitmap bitmap = bitmapDrawable.getBitmap();
            return saveBitmap(bitmap);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
    private String saveBitmap(Bitmap bitmap){
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String filename = timeStamp + "_"+".jpeg";
        try {
            saveImage(bitmap, filename);
            return new File(this.getExternalFilesDir(null), filename).getAbsolutePath();
        } catch (IOException e) {
            Toast.makeText(this, "Saving image failed", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            return null;
        }
    }

    private void saveImage(Bitmap bitmap, String filename) throws IOException {
//        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//        String filename = timeStamp + "_"+"/jpeg";
        File imageFile = new File(this.getExternalFilesDir(null), filename);
        if (imageFile.exists ()) {
            imageFile.delete ();
        }
        imageFile.createNewFile();
        FileOutputStream out = new FileOutputStream(imageFile);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
        out.flush();
        out.close();
    }

    private Vehicle editVehicle(){
        String curMake = etMake.getText().toString();
        String curModel = etModel.getText().toString();
        String curVariant = etVariant.getText().toString();
        String curNumber  = etNumber.getText().toString();
        vehicle.setMake(curMake);
        vehicle.setModel(curModel);
        vehicle.setVariant(curVariant);
        vehicle.setVehicleNumber(curNumber);
        if(vehicle.getPhotoPath()!=null) {
            File prevImageFile = new File(vehicle.getPhotoPath());
            prevImageFile.delete();
        }
        vehicle.setPhotoPath(saveBitmapFromImageView(ivPhotoEdit));
        return vehicle;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_save:
                if (validate()) {
                    if(forNewVehicle) {
                    Vehicle newVehicle = createVehicleFromData();
                        Intent replyIntent = new Intent();
                        replyIntent.putExtra(Constants.RESULT_KEY, gson.toJson(newVehicle));
                        setResult(RESULT_OK, replyIntent);
                        finish();
                    }
                    else{
                        vehicle = editVehicle();
                        viewModel.edit(vehicle);
                        Toast.makeText(this, "Vehicle updated successfully", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.bt_delete:
                onDeleteClick();
                break;
            case R.id.bt_img_edit:
                onImgEditClick();
                break;
        }
    }

    void onImgEditClick(){
        final View selectImageDialog = LayoutInflater.from(this).inflate(R.layout.select_image_dialog, null);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(selectImageDialog)
                .create();
        selectImageDialog.findViewById(R.id.bt_select_from_camera).setOnClickListener((v)-> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, Constants.CAMERA_PERMISSION_REQUEST_CODE);
            }
            else {
                Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(takePicture, Constants.CAMERA_REQUEST_CODE);
            }
            dialog.dismiss();
        });
        selectImageDialog.findViewById(R.id.bt_select_from_gallery).setOnClickListener((v)-> {
            Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(pickPhoto, Constants.GALLERY_REQUEST_CODE);
            dialog.dismiss();
        });
        dialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Constants.CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(takePicture, Constants.CAMERA_REQUEST_CODE);//zero can be replaced with any action code (called requestCode)
            } else {
                Snackbar.make(findViewById(android.R.id.content), "Camera permission needed!", Snackbar.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch(requestCode) {
            case Constants.CAMERA_REQUEST_CODE:
                if(resultCode == RESULT_OK){
                    Log.d("got image result", "onActivityResult: got the image data");
                    Bitmap bitmap = (Bitmap) imageReturnedIntent.getExtras().get("data");
                    Bitmap resizedBitmpap = Bitmap.createScaledBitmap(bitmap, Constants.IM_WIDTH, Constants.IM_HEIGHT, false);
                    ivPhotoEdit.setImageBitmap(resizedBitmpap);
                }

                break;

            case Constants.GALLERY_REQUEST_CODE:
                if(resultCode == RESULT_OK){
                    Log.d("got image result", "onActivityResult: got the image data");
                    Uri selectedImage = imageReturnedIntent.getData();
                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
//                    Bitmap bitmap = (Bitmap) imageReturnedIntent.getExtras().get("data");
                    Bitmap resizedBitmpap = Bitmap.createScaledBitmap(bitmap, Constants.IM_WIDTH, Constants.IM_HEIGHT, false);
                    ivPhotoEdit.setImageBitmap(resizedBitmpap);
                }

                break;
        }
    }

    void onDeleteClick(){
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Confirm")
                .setMessage("Are you sure you want to delete")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        viewModel.delete(vehicle);
                        dialogInterface.dismiss();
                        finish();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .create();
        dialog.show();
    }
}
