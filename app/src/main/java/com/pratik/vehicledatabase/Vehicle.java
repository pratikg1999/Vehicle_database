package com.pratik.vehicledatabase;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "vehicle_table")
public class Vehicle {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo()
    private String vehicleNumber;

    @ColumnInfo()
    private String make;

    @ColumnInfo()
    private String model;

    @ColumnInfo()
    private String variant;

    @ColumnInfo()
    private String photoPath;

    public Vehicle(String vehicleNumber, String make, String model, String variant, String photoPath) {
        this.vehicleNumber = vehicleNumber;
        this.make = make;
        this.model = model;
        this.variant = variant;
        this.photoPath = photoPath;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getVariant() {
        return variant;
    }

    public void setVariant(String variant) {
        this.variant = variant;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }
}
