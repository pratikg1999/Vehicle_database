package com.pratik.vehicledatabase;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rView;
    private FloatingActionButton fab;
    private TextView tvNoVehicles;
    private List<Vehicle> vehicles;
    VehicleViewModel viewModel;
    private Gson gson = new Gson();
    private String TAG = "in mainactivity";
    VehicleAdapter vehicleAdapter;
    Queue<Vehicle> deleteQueue = new LinkedList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rView = findViewById(R.id.rview);
        fab = findViewById(R.id.fab);
        tvNoVehicles = findViewById(R.id.tv_no_vehicles);

        rView.setLayoutManager(new LinearLayoutManager(this));
        vehicleAdapter = new VehicleAdapter(this);
        rView.setAdapter(vehicleAdapter);

        viewModel = new ViewModelProvider(this).get(VehicleViewModel.class);
        viewModel.getAllVehicles().

                observe(this, new Observer<List<Vehicle>>() {

                    @Override
                    public void onChanged(List<Vehicle> vehicles) {
                        MainActivity.this.vehicles = vehicles;
                        if (vehicles != null && vehicles.size() > 0) {
                            rView.setVisibility(View.VISIBLE);
                            tvNoVehicles.setVisibility(View.GONE);
                            vehicleAdapter.setVehicles(vehicles);
                        } else {
                            rView.setVisibility(View.GONE);
                            tvNoVehicles.setVisibility(View.VISIBLE);
                        }
                    }
                });

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback =
                new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
                    @Override
                    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder
                            target) {
                        return false;
                    }

                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                        onDeleteViewHolder(viewHolder);
                    }
                };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(rView);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(MainActivity.this, EditActivity.class), Constants.NEW_REQUEST_CODE);
            }
        });
    }


    private void onDeleteViewHolder(RecyclerView.ViewHolder viewHolder){
        int adapterPosition = viewHolder.getAdapterPosition();
        Vehicle toDelete = vehicles.get(viewHolder.getAdapterPosition());
        deleteQueue.add(toDelete);
        vehicles.remove(adapterPosition);
        vehicleAdapter.notifyItemRemoved(adapterPosition);
        Snackbar.make(rView, "Vehicle record deleted", Snackbar.LENGTH_LONG)
                .setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        vehicles.add(adapterPosition, toDelete);
                        vehicleAdapter.notifyItemInserted(adapterPosition);
                        deleteQueue.remove(toDelete);
                    }
                })
                .addCallback(new Snackbar.Callback() {

                    @Override
                    public void onDismissed(Snackbar snackbar, int event) {
//                        if(event!=DISMISS_EVENT_ACTION){
//                            viewModel.delete(toDelete);
//                        }
                    }

                    @Override
                    public void onShown(Snackbar snackbar) {
                    }
                })
                .show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        while(deleteQueue.size()>0){
            viewModel.delete(deleteQueue.poll());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.NEW_REQUEST_CODE && resultCode == RESULT_OK) {
            String vehicleJson = data.getStringExtra(Constants.RESULT_KEY);
            Log.d(TAG, "onActivityResult: got result " + vehicleJson);
            if (vehicleJson != null) {
                Type type = new TypeToken<Vehicle>() {
                }.getType();
                Vehicle vehicle = gson.fromJson(vehicleJson, type);
                viewModel.insert(vehicle);
            } else {
                Toast.makeText(this, "No vehicle created", Toast.LENGTH_SHORT).show();
            }
        }
    }

    class VehicleAdapter extends RecyclerView.Adapter<VehicleAdapter.VehicleViewHolder> {

        private Context ctx;
        private List<Vehicle> vehicles;

        @NonNull
        @Override
        public VehicleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(ctx).inflate(R.layout.card_vehicle, parent, false);
            return new VehicleViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull VehicleViewHolder holder, int position) {
            Vehicle curVehicle = vehicles.get(position);
            holder.tvNumber.setText(curVehicle.getVehicleNumber());
            holder.tvVariant.setText(curVehicle.getVariant());
            holder.tvMakeModel.setText(curVehicle.getMake() + " " + curVehicle.getModel());
            Bitmap vehiclePhoto = BitmapFactory.decodeFile(curVehicle.getPhotoPath());
            holder.ivPhoto.setTransitionName("vehicle_trans_name");
            if (vehiclePhoto != null) {
                holder.ivPhoto.setImageBitmap(vehiclePhoto);
            }
            holder.itemView.setOnClickListener((v) -> {
                if(curVehicle.getPhotoPath()!=null) {
                    ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(MainActivity.this, holder.ivPhoto, "vehicle_trans_name");
                    ctx.startActivity(new Intent(ctx, DetailsActivity.class).putExtra(Constants.VIEW_VEHICLE_KEY, gson.toJson(curVehicle)), activityOptionsCompat.toBundle());
                }
                else{
                    ctx.startActivity(new Intent(ctx, DetailsActivity.class).putExtra(Constants.VIEW_VEHICLE_KEY, gson.toJson(curVehicle)));
                }

            });

            holder.editButton.setOnClickListener((v) -> {
                ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(MainActivity.this, holder.ivPhoto, "vehicle_trans_name");
                ctx.startActivity(new Intent(ctx, EditActivity.class).putExtra(Constants.EDIT_VEHICLE_KEY, gson.toJson(curVehicle)), activityOptionsCompat.toBundle());
            });

            holder.deleteButton.setOnClickListener((v) -> {
                onDeleteViewHolder(holder);
            });
        }

        @Override
        public int getItemCount() {
            return vehicles != null ? vehicles.size() : 0;
        }

        VehicleAdapter(Context ctx) {
            this.ctx = ctx;
        }

        void setVehicles(List<Vehicle> vehicles) {
            this.vehicles = vehicles;
            this.notifyDataSetChanged();
        }

        class VehicleViewHolder extends RecyclerView.ViewHolder {

            ImageView ivPhoto;
            TextView tvMakeModel;
            TextView tvVariant;
            TextView tvNumber;
            ImageButton editButton;
            ImageButton deleteButton;

            public VehicleViewHolder(@NonNull View itemView) {
                super(itemView);
                ivPhoto = itemView.findViewById(R.id.iv_photo);
                tvMakeModel = itemView.findViewById(R.id.tv_make_model);
                tvVariant = itemView.findViewById(R.id.tv_variant);
                tvNumber = itemView.findViewById(R.id.tv_number);
                editButton = itemView.findViewById(R.id.bt_img_edit);
                deleteButton = itemView.findViewById(R.id.bt_img_delete);
            }
        }
    }
}
