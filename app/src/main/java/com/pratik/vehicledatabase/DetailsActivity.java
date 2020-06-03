package com.pratik.vehicledatabase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.w3c.dom.Text;

import java.lang.reflect.Type;
import java.util.List;

import static android.view.View.GONE;

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
    private RecyclerView rViewDetails;
    private VehicleAdapterSmall adapter;
    List<Vehicle> withSameMake;
    TextView tvMoreLabel;

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
        rViewDetails = findViewById(R.id.rv_details);
        tvMoreLabel = findViewById(R.id.tv_more_similar_label);
        adapter = new VehicleAdapterSmall(this);

        viewModel = new ViewModelProvider(this).get(VehicleViewModel.class);


        Intent intent = getIntent();
        String vehicleJson = intent.getStringExtra(Constants.VIEW_VEHICLE_KEY);
        Type type = new TypeToken<Vehicle>(){}.getType();
        vehicle = gson.fromJson(vehicleJson, type);
        prePopulate(vehicle);
        new AsyncTask<Vehicle, Void, List<Vehicle>>(){

            @Override
            protected List<Vehicle> doInBackground(Vehicle... params) {
                List<Vehicle> list = viewModel.getVehiclesWithMakeAs(params[0]);
                updateWithSameMake(list);
                return list;
            }
        }.execute(vehicle);

        rViewDetails.setAdapter(adapter);

        btEditButton.setOnClickListener(this);
        btDeleteButton.setOnClickListener(this);
        btExpandButton.setOnClickListener((v)->{
            if(detailsContainer.getVisibility() == GONE) {
                detailsContainer.setVisibility(View.VISIBLE);
                btExpandButton.setImageResource(R.drawable.ic_arrow_up_black_24dp);
            }
            else{
                detailsContainer.setVisibility(GONE);
                btExpandButton.setImageResource(R.drawable.ic_arrow_down_black_24dp);

            }
        });

    }

    private void updateWithSameMake(List<Vehicle> newList){
        if(newList==null || newList.size()==0){
            tvMoreLabel.setVisibility(GONE);
        }
        else {
            tvMoreLabel.setVisibility(View.VISIBLE);
        }
        withSameMake = newList;
        adapter.setVehicles(withSameMake);
    }

    private void prePopulate(Vehicle vehicle){
        this.vehicle = vehicle;
        if(vehicle.getPhotoPath()!=null){
            ivPhoto.setImageBitmap(BitmapFactory.decodeFile(vehicle.getPhotoPath()));
        }
        else{
            ivPhoto.setImageResource(R.drawable.car_bg);
        }
        tvMakeModel.setText(vehicle.getMake() + " "+ vehicle.getModel());
        tvNumber.setText(vehicle.getVehicleNumber());
        tvVariant.setText(vehicle.getVariant());

        tvMakeInfo.setText(vehicle.getMake());
        tvModelInfo.setText(vehicle.getModel());
        tvNumberInfo.setText(vehicle.getVehicleNumber());
        tvVariantInfo.setText(vehicle.getVariant());

        tvMoreLabel.setText("More from " + vehicle.getMake());
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

    class VehicleAdapterSmall extends RecyclerView.Adapter<VehicleAdapterSmall.VehicleViewHolderSmall> {

        private Context ctx;
        private List<Vehicle> vehicles;

        @NonNull
        @Override
        public VehicleViewHolderSmall onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(ctx).inflate(R.layout.card_vehicle_small, parent, false);
            return new VehicleViewHolderSmall(v);
        }

        @Override
        public void onBindViewHolder(@NonNull VehicleViewHolderSmall holder, int position) {
            Vehicle curVehicle = vehicles.get(position);
            holder.tvModel.setText(curVehicle.getModel());
//            holder.ivPhoto.setTransitionName("vehicle_trans_name");
            if(curVehicle.getPhotoPath()!=null) {
                Bitmap vehiclePhoto = BitmapFactory.decodeFile(curVehicle.getPhotoPath());
                if (vehiclePhoto != null) {
                    holder.ivPhoto.setImageBitmap(vehiclePhoto);
                }
            }
            else{
//                holder.tvModel.setLayoutParams(new ViewGroup.MarginLayoutParams(ViewGroup.MarginLayoutParams.MATCH_PARENT, ViewGroup.MarginLayoutParams.MATCH_PARENT));
                holder.ivPhoto.setVisibility(GONE);
                holder.tvAltText.setText(vehicle.getMake());
            }
            holder.itemView.setOnClickListener((v) -> {
//                if(curVehicle.getPhotoPath()!=null) {
//                    ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(DetailsActivity.this, holder.ivPhoto, "vehicle_trans_name");
//                    ctx.startActivity(new Intent(ctx, DetailsActivity.class).putExtra(Constants.VIEW_VEHICLE_KEY, gson.toJson(curVehicle)), activityOptionsCompat.toBundle());
//                }
//                else{
//                    ctx.startActivity(new Intent(ctx, DetailsActivity.class).putExtra(Constants.VIEW_VEHICLE_KEY, gson.toJson(curVehicle)));
//                }
//                finish();
                DetailsActivity.this.withSameMake.add(DetailsActivity.this.vehicle);
                prePopulate(curVehicle);
                DetailsActivity.this.withSameMake.remove(curVehicle);
                ivPhoto.setTransitionName("");
                this.notifyItemInserted(vehicles.size()-1);
                this.notifyItemRemoved(holder.getAdapterPosition());
            });

        }

        @Override
        public int getItemCount() {
            return vehicles != null ? vehicles.size() : 0;
        }

        VehicleAdapterSmall(Context ctx) {
            this.ctx = ctx;
        }

        void setVehicles(List<Vehicle> vehicles) {
            this.vehicles = vehicles;
            this.notifyDataSetChanged();
        }

        class VehicleViewHolderSmall extends RecyclerView.ViewHolder {

            ImageView ivPhoto;
            TextView tvModel;
            TextView tvAltText;

            public VehicleViewHolderSmall(@NonNull View itemView) {
                super(itemView);
                ivPhoto = itemView.findViewById(R.id.iv_photo_card_short);
                tvModel = itemView.findViewById(R.id.tv_model_card_short);
                tvAltText = itemView.findViewById(R.id.tv_alt_text);

            }
        }
    }
}
