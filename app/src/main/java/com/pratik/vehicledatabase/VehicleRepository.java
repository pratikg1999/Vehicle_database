package com.pratik.vehicledatabase;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

public class VehicleRepository {

    VehicleDao vehicleDao;
    LiveData<List<Vehicle>> vehicles;

    public VehicleRepository(Application application){
        VehicleDatabase database = VehicleDatabase.getDatabase(application.getBaseContext());
        vehicleDao = database.getVehicleDao();
        vehicles = vehicleDao.getAllVehicles();
    }

    public void insert(Vehicle vehicle){
        VehicleDatabase.databaseWriteExecutor.execute(() -> {
             vehicleDao.insert(vehicle);
        });
    }

    public void delete(Vehicle vehicle){
        VehicleDatabase.databaseWriteExecutor.execute(() -> {
            vehicleDao.delete(vehicle);
        });
    }

    public void delete(int id){
        VehicleDatabase.databaseWriteExecutor.execute(() -> {
            vehicleDao.delete(id);
        });
    }

    public LiveData<List<Vehicle>> getVehicles() {
        return vehicles;
    }

    public void update(Vehicle vehicle){
        VehicleDatabase.databaseWriteExecutor.execute(() -> {
            vehicleDao.update(vehicle);
        });
    }
}
