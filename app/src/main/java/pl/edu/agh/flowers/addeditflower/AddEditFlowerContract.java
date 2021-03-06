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

import pl.edu.agh.flowers.BasePresenter;
import pl.edu.agh.flowers.BaseView;

/**
 * This specifies the contract between the view and the presenter.
 */
public interface AddEditFlowerContract {

    interface View extends BaseView<Presenter> {

        void showEmptyTaskError();

        void showTasksList();

        void setTitle(String title);

        void setDescription(String description);

        boolean isActive();

        void setBitmap(Bitmap bitmap);
    }

    interface Presenter extends BasePresenter {

        void saveTask(String title, String description, String beaconBluetoothAddress, Bitmap image);

        void populateTask();

        boolean isDataMissing();
    }
}
