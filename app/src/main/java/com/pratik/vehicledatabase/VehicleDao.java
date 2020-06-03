package com.pratik.vehicledatabase;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface VehicleDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(Vehicle vehicle);

    @Query("DELETE FROM vehicle_table")
    void deleteAll();

    @Update
    int update(Vehicle vehicle);

    @Delete
    int delete(Vehicle vehicle);

    @Query("SELECT * from vehicle_table")
    LiveData<List<Vehicle>> getAllVehicles();

    @Query("DELETE FROM vehicle_table WHERE id = :id")
    int delete(int id);
}
