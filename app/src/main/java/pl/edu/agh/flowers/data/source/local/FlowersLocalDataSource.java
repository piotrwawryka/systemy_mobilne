/*
 * Copyright 2016, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package pl.edu.agh.flowers.data.source.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;

import pl.edu.agh.flowers.data.Flower;
import pl.edu.agh.flowers.data.source.FlowersDataSource;
import pl.edu.agh.flowers.data.source.local.FlowersPersistenceContract.TaskEntry;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;


/**
 * Concrete implementation of a data source as a db.
 */
public class FlowersLocalDataSource implements FlowersDataSource {

    private static FlowersLocalDataSource INSTANCE;

    private FlowersDbHelper mDbHelper;

    // Prevent direct instantiation.
    private FlowersLocalDataSource(@NonNull Context context) {
        checkNotNull(context);
        mDbHelper = new FlowersDbHelper(context);
    }

    public static FlowersLocalDataSource getInstance(@NonNull Context context) {
        if (INSTANCE == null) {
            INSTANCE = new FlowersLocalDataSource(context);
        }
        return INSTANCE;
    }

    private static Bitmap getImage(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }

    /**
     * Note: {@link LoadTasksCallback#onDataNotAvailable()} is fired if the database doesn't exist
     * or the table is empty.
     */
    @Override
    public void getFlowers(@NonNull LoadTasksCallback callback) {
        List<Flower> tasks = new ArrayList<Flower>();
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String[] projection = {
                TaskEntry.COLUMN_NAME_ENTRY_ID,
                TaskEntry.COLUMN_NAME_TITLE,
                TaskEntry.COLUMN_NAME_DESCRIPTION,
                TaskEntry.COLUMN_NAME_COMPLETED,
                TaskEntry.COLUMN_BEACON_BLUETOOTH_ADDRESS,
                TaskEntry.COLUMN_IMAGE
        };

        Cursor c = db.query(
                TaskEntry.TABLE_NAME, projection, null, null, null, null, null);

        if (c != null && c.getCount() > 0) {
            while (c.moveToNext()) {
                String itemId = c.getString(c.getColumnIndexOrThrow(TaskEntry.COLUMN_NAME_ENTRY_ID));
                String title = c.getString(c.getColumnIndexOrThrow(TaskEntry.COLUMN_NAME_TITLE));
                String description =
                        c.getString(c.getColumnIndexOrThrow(TaskEntry.COLUMN_NAME_DESCRIPTION));
                boolean completed =
                        c.getInt(c.getColumnIndexOrThrow(TaskEntry.COLUMN_NAME_COMPLETED)) == 1;
                final String beaconBluetoothAddress = c.getString(c.getColumnIndexOrThrow(TaskEntry.COLUMN_BEACON_BLUETOOTH_ADDRESS));
                byte[] image = c.getBlob(c.getColumnIndexOrThrow(TaskEntry.COLUMN_IMAGE));
                Flower task = new Flower(title, description, itemId, completed);
                if(image != null) {
                    task.setBitmap(getImage(image));
                }
                task.setBeaconBluetoothAddress(beaconBluetoothAddress);
                tasks.add(task);
            }
        }
        if (c != null) {
            c.close();
        }

        db.close();

        if (tasks.isEmpty()) {
            // This will be called if the table is new or just empty.
            callback.onDataNotAvailable();
        } else {
            callback.onFlowersLoaded(tasks);
        }

    }

    /**
     * Note: {@link GetTaskCallback#onDataNotAvailable()} is fired if the {@link Flower} isn't
     * found.
     */
    @Override
    public void getFlower(@NonNull String taskId, @NonNull GetTaskCallback callback) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String[] projection = {
                TaskEntry.COLUMN_NAME_ENTRY_ID,
                TaskEntry.COLUMN_NAME_TITLE,
                TaskEntry.COLUMN_NAME_DESCRIPTION,
                TaskEntry.COLUMN_NAME_COMPLETED,
                TaskEntry.COLUMN_BEACON_BLUETOOTH_ADDRESS,
                TaskEntry.COLUMN_IMAGE
        };

        String selection = TaskEntry.COLUMN_NAME_ENTRY_ID + " LIKE ?";
        String[] selectionArgs = { taskId };

        Cursor c = db.query(
                TaskEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null);

        Flower task = null;

        if (c != null && c.getCount() > 0) {
            c.moveToFirst();
            String itemId = c.getString(c.getColumnIndexOrThrow(TaskEntry.COLUMN_NAME_ENTRY_ID));
            String title = c.getString(c.getColumnIndexOrThrow(TaskEntry.COLUMN_NAME_TITLE));
            String description =
                    c.getString(c.getColumnIndexOrThrow(TaskEntry.COLUMN_NAME_DESCRIPTION));
            boolean completed =
                    c.getInt(c.getColumnIndexOrThrow(TaskEntry.COLUMN_NAME_COMPLETED)) == 1;
            final String beaconBluetoothAddress = c.getString(c.getColumnIndexOrThrow(TaskEntry.COLUMN_BEACON_BLUETOOTH_ADDRESS));
            byte[] image = c.getBlob(c.getColumnIndexOrThrow(TaskEntry.COLUMN_IMAGE));
            task = new Flower(title, description, itemId, completed);
            if(image != null) {
                task.setBitmap(getImage(image));
            }
            task.setBeaconBluetoothAddress(beaconBluetoothAddress);
        }
        if (c != null) {
            c.close();
        }

        db.close();

        if (task != null) {
            callback.onFlowerLoaded(task);
        } else {
            callback.onDataNotAvailable();
        }
    }

    // convert from bitmap to byte array
    private static byte[] getBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        return stream.toByteArray();
    }

    @Override
    public void saveFlower(@NonNull Flower task) {
        checkNotNull(task);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(TaskEntry.COLUMN_NAME_ENTRY_ID, task.getId());
        values.put(TaskEntry.COLUMN_NAME_TITLE, task.getTitle());
        values.put(TaskEntry.COLUMN_NAME_DESCRIPTION, task.getDescription());
        values.put(TaskEntry.COLUMN_NAME_COMPLETED, task.isCompleted());
        values.put(TaskEntry.COLUMN_BEACON_BLUETOOTH_ADDRESS, task.getBeaconBluetoothAddress());
        if(task.getBitmap() != null) {
            values.put(TaskEntry.COLUMN_IMAGE, getBytes(task.getBitmap()));
        }

        db.insert(TaskEntry.TABLE_NAME, null, values);

        db.close();
    }

    @Override
    public void completeFlower(@NonNull Flower task) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(TaskEntry.COLUMN_NAME_COMPLETED, true);

        String selection = TaskEntry.COLUMN_NAME_ENTRY_ID + " LIKE ?";
        String[] selectionArgs = { task.getId() };

        db.update(TaskEntry.TABLE_NAME, values, selection, selectionArgs);

        db.close();
    }

    @Override
    public void completeFlower(@NonNull String taskId) {
        // Not required for the local data source because the {@link TasksRepository} handles
        // converting from a {@code taskId} to a {@link task} using its cached data.
    }

    @Override
    public void activateFlower(@NonNull Flower task) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(TaskEntry.COLUMN_NAME_COMPLETED, false);

        String selection = TaskEntry.COLUMN_NAME_ENTRY_ID + " LIKE ?";
        String[] selectionArgs = { task.getId() };

        db.update(TaskEntry.TABLE_NAME, values, selection, selectionArgs);

        db.close();
    }

    @Override
    public void activateFlower(@NonNull String taskId) {
        // Not required for the local data source because the {@link TasksRepository} handles
        // converting from a {@code taskId} to a {@link task} using its cached data.
    }

    @Override
    public void clearCompletedFlower() {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        String selection = TaskEntry.COLUMN_NAME_COMPLETED + " LIKE ?";
        String[] selectionArgs = { "1" };

        db.delete(TaskEntry.TABLE_NAME, selection, selectionArgs);

        db.close();
    }

    @Override
    public void refreshTasks() {
        // Not required because the {@link TasksRepository} handles the logic of refreshing the
        // tasks from all the available data sources.
    }

    @Override
    public void deleteAllTasks() {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        db.delete(TaskEntry.TABLE_NAME, null, null);

        db.close();
    }

    @Override
    public void deleteTask(@NonNull String taskId) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        String selection = TaskEntry.COLUMN_NAME_ENTRY_ID + " LIKE ?";
        String[] selectionArgs = { taskId };

        db.delete(TaskEntry.TABLE_NAME, selection, selectionArgs);

        db.close();
    }
}
