package com.pratik.vehicledatabase;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Vehicle.class}, version = 1, exportSchema = false)
public abstract class VehicleDatabase extends RoomDatabase {

    public abstract VehicleDao getVehicleDao();

    private static volatile VehicleDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    static VehicleDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (VehicleDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            VehicleDatabase.class, "vehicle_database")
//                            .addCallback(vehicleDBCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static RoomDatabase.Callback vehicleDBCallback = new RoomDatabase.Callback() {
//        @Override
//        public void onCreate(@NonNull SupportSQLiteDatabase db) {
//            super.onCreate(db);
////            databaseWriteExecutor.execute(() -> {
////                // Populate the database in the background.
////                // If you want to start with more words, just add them.
////                VehicleDao dao = INSTANCE.getVehicleDao();
////                dao.deleteAll();
////
////                Vehicle vehicle = new Vehicle("Test numver", "test make", "test model", "test var", null);
////                dao.insert(vehicle);
////            });
//        }

        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);

            // If you want to keep data through app restarts,
            // comment out the following block
            databaseWriteExecutor.execute(() -> {
                // Populate the database in the background.
                // If you want to start with more words, just add them.
                VehicleDao dao = INSTANCE.getVehicleDao();
//                dao.deleteAll();

                for(int i=0; i<5; i++) {
                    Vehicle vehicle = new Vehicle("Test numver"+i, "test make"+i, "test model"+i, "test var"+i, null);
                    dao.insert(vehicle);
                }
            });
        }
    };
}
