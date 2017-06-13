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

package pl.edu.agh.flowers.data.source;

import android.support.annotation.NonNull;

import pl.edu.agh.flowers.data.Flower;

import java.util.List;

/**
 * Main entry point for accessing tasks data.
 * <p>
 * For simplicity, only getFlowers() and getFlower() have callbacks. Consider adding callbacks to other
 * methods to inform the user of network/database errors or successful operations.
 * For example, when a new task is created, it's synchronously stored in cache but usually every
 * operation on database or network should be executed in a different thread.
 */
public interface FlowersDataSource {

    interface LoadTasksCallback {

        void onFlowersLoaded(List<Flower> tasks);

        void onDataNotAvailable();
    }

    interface GetTaskCallback {

        void onFlowerLoaded(Flower task);

        void onDataNotAvailable();
    }

    void getFlowers(@NonNull LoadTasksCallback callback);

    void getFlower(@NonNull String taskId, @NonNull GetTaskCallback callback);

    void saveFlower(@NonNull Flower task);

    void completeFlower(@NonNull Flower task);

    void completeFlower(@NonNull String taskId);

    void activateFlower(@NonNull Flower task);

    void activateFlower(@NonNull String taskId);

    void clearCompletedFlower();

    void refreshTasks();

    void deleteAllTasks();

    void deleteTask(@NonNull String taskId);
}
