package com.pratik.vehicledatabase;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.w3c.dom.Text;

import java.lang.reflect.Type;

public class DetailsActivity extends AppCompatActivity implements View.OnClickListener{

    TextView tvMakeModel;
    TextView tvNumber;
    TextView tvVariant;
    ImageView ivPhoto;
    private Gson gson = new Gson();
    Vehicle vehicle;
    private Button btEditButton;
    private ImageButton btExpandButton;
    private LinearLayout detailsContainer;
    private TextView tvMakeInfo;
    private TextView tvModelInfo;
    private TextView tvVariantInfo;
    private TextView tvNumberInfo;
    private Button btDeleteButton;
    VehicleViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        tvMakeModel = findViewById(R.id.tv_make_model_details);
        tvNumber = findViewById(R.id.tv_num_details);
        tvVariant = findViewById(R.id.tv_variant_details);
        btEditButton = findViewById(R.id.bt_edit_details);
        ivPhoto = findViewById(R.id.iv_photo_details);
        btExpandButton = findViewById(R.id.bt_arrow_expand);
        detailsContainer = findViewById(R.id.ll_details_container);
        tvMakeInfo = findViewById(R.id.tv_make_info);
        tvModelInfo = findViewById(R.id.tv_model_info);
        tvVariantInfo = findViewById(R.id.tv_variant_info);
        tvNumberInfo = findViewById(R.id.tv_number_info);
        btDeleteButton = findViewById(R.id.bt_delete_details);

        viewModel = new ViewModelProvider(this).get(VehicleViewModel.class);
        Intent intent = getIntent();
        String vehicleJson = intent.getStringExtra(Constants.VIEW_VEHICLE_KEY);
        Type type = new TypeToken<Vehicle>(){}.getType();
        vehicle = gson.fromJson(vehicleJson, type);
        prePopulate(vehicle);
        btEditButton.setOnClickListener(this);
        btDeleteButton.setOnClickListener(this);

        btExpandButton.setOnClickListener((v)->{
            if(detailsContainer.getVisibility() == View.GONE) {
                detailsContainer.setVisibility(View.VISIBLE);
                btExpandButton.setImageResource(R.drawable.ic_arrow_up_black_24dp);
            }
            else{
                detailsContainer.setVisibility(View.GONE);
                btExpandButton.setImageResource(R.drawable.ic_arrow_down_black_24dp);

            }
        });

    }

    private void prePopulate(Vehicle vehicle){
        if(vehicle.getPhotoPath()!=null){
            ivPhoto.setImageBitmap(BitmapFactory.decodeFile(vehicle.getPhotoPath()));
        }
        tvMakeModel.setText(vehicle.getMake() + " "+ vehicle.getModel());
        tvNumber.setText(vehicle.getVehicleNumber());
        tvVariant.setText(vehicle.getVariant());

        tvMakeInfo.setText(vehicle.getMake());
        tvModelInfo.setText(vehicle.getModel());
        tvNumberInfo.setText(vehicle.getVehicleNumber());
        tvVariantInfo.setText(vehicle.getVariant());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.bt_edit_details:
                startActivity(new Intent(DetailsActivity.this, EditActivity.class).putExtra(Constants.EDIT_VEHICLE_KEY, gson.toJson(vehicle)));
                finish();
                break;
            case R.id.bt_delete_details:
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
                break;
        }
    }
}
