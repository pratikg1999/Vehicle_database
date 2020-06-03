package com.pratik.vehicledatabase;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class VehicleViewModel extends AndroidViewModel {

    VehicleRepository repo;
    LiveData<List<Vehicle>> vehicles;

    public VehicleViewModel(@NonNull Application application) {
        super(application);
        repo = new VehicleRepository(application);
        vehicles = repo.getVehicles();
    }

    void insert(Vehicle vehicle){
        repo.insert(vehicle);
    }

    void delete(Vehicle vehicle){
        repo.delete(vehicle);
    }

    void delete(int vehicleId){
        repo.delete(vehicleId);
    }

    void edit(Vehicle vehicle){
        repo.update(vehicle);
    }

    LiveData<List<Vehicle>> getAllVehicles(){
        return vehicles;
    }

    List<Vehicle> getVehiclesWithMakeAs(Vehicle vehicle){
        return repo.getVehiclesWithMakeAs(vehicle);
    }
}
