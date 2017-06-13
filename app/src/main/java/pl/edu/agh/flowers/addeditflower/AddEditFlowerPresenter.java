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

package pl.edu.agh.flowers.addeditflower;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import pl.edu.agh.flowers.data.Flower;
import pl.edu.agh.flowers.data.source.FlowersDataSource;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Listens to user actions from the UI ({@link AddEditFlowerFragment}), retrieves the data and updates
 * the UI as required.
 */
public class AddEditFlowerPresenter implements AddEditFlowerContract.Presenter,
        FlowersDataSource.GetTaskCallback {

    @NonNull
    private final FlowersDataSource mTasksRepository;

    @NonNull
    private final AddEditFlowerContract.View mAddTaskView;

    @Nullable
    private String mTaskId;

    private boolean mIsDataMissing;

    /**
     * Creates a presenter for the add/edit view.
     *
     * @param taskId ID of the task to edit or null for a new task
     * @param tasksRepository a repository of data for tasks
     * @param addTaskView the add/edit view
     * @param shouldLoadDataFromRepo whether data needs to be loaded or not (for config changes)
     */
    public AddEditFlowerPresenter(@Nullable String taskId, @NonNull FlowersDataSource tasksRepository,
                                  @NonNull AddEditFlowerContract.View addTaskView, boolean shouldLoadDataFromRepo) {
        mTaskId = taskId;
        mTasksRepository = checkNotNull(tasksRepository);
        mAddTaskView = checkNotNull(addTaskView);
        mIsDataMissing = shouldLoadDataFromRepo;

        mAddTaskView.setPresenter(this);
    }

    @Override
    public void start() {
        if (!isNewTask() && mIsDataMissing) {
            populateTask();
        }
    }

    @Override
    public void saveTask(String title, String description, String beaconBluetoothAddress, Bitmap image) {
        if (isNewTask()) {
            createTask(title, description, beaconBluetoothAddress, image);
        } else {
            updateTask(title, description, beaconBluetoothAddress, image);
        }
    }

    @Override
    public void populateTask() {
        if (isNewTask()) {
            throw new RuntimeException("populateTask() was called but task is new.");
        }
        mTasksRepository.getFlower(mTaskId, this);
    }

    @Override
    public void onFlowerLoaded(Flower task) {
        // The view may not be able to handle UI updates anymore
        if (mAddTaskView.isActive()) {
            mAddTaskView.setTitle(task.getTitle());
            mAddTaskView.setDescription(task.getDescription());
            mAddTaskView.setBitmap(task.getBitmap());
        }
        mIsDataMissing = false;
    }

    @Override
    public void onDataNotAvailable() {
        // The view may not be able to handle UI updates anymore
        if (mAddTaskView.isActive()) {
            mAddTaskView.showEmptyTaskError();
        }
    }

    @Override
    public boolean isDataMissing() {
        return mIsDataMissing;
    }

    private boolean isNewTask() {
        return mTaskId == null;
    }

    private void createTask(String title, String description, String beaconBluetoothAddress, Bitmap image) {
        Flower newTask = new Flower(title, description);
        newTask.setBitmap(image);
        newTask.setBeaconBluetoothAddress(beaconBluetoothAddress);
        if (newTask.isEmpty()) {
            mAddTaskView.showEmptyTaskError();
        } else {
            mTasksRepository.saveFlower(newTask);
            mAddTaskView.showTasksList();
        }
    }

    private void updateTask(String title, String description, String beaconBluetoothAddress, Bitmap image) {
        if (isNewTask()) {
            throw new RuntimeException("updateTask() was called but task is new.");
        }
        Flower task = new Flower(title, description, mTaskId);
        task.setBitmap(image);
        task.setBeaconBluetoothAddress(beaconBluetoothAddress);
        mTasksRepository.saveFlower(task);
        mAddTaskView.showTasksList(); // After an edit, go back to the list.
    }
}
