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

package pl.edu.agh.flowers.data;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Strings;

import java.util.UUID;

public final class Flower {

    @NonNull
    private final String mId;

    @Nullable
    private final String mTitle;

    @Nullable
    private final String mDescription;

    private final boolean mCompleted;

    private Bitmap bitmap;

    private String mBeaconBluetoothAddress;

    public Flower(@Nullable String title, @Nullable String description) {
        this(title, description, UUID.randomUUID().toString(), false);
    }

    public Flower(@Nullable String title, @Nullable String description, Bitmap bitmap) {
        this(title, description, UUID.randomUUID().toString(), false);
        this.bitmap = bitmap;
    }

    public Flower(@Nullable String title, @Nullable String description, @NonNull String id) {
        this(title, description, id, false);
    }

    public Flower(@Nullable String title, @Nullable String description, boolean completed) {
        this(title, description, UUID.randomUUID().toString(), completed);
    }

    public Flower(@Nullable String title, @Nullable String description,
                  @NonNull String id, boolean completed) {
        mId = id;
        mTitle = title;
        mDescription = description;
        mCompleted = completed;
    }

    @NonNull
    public String getId() {
        return mId;
    }

    @Nullable
    public String getTitle() {
        return mTitle;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public String getBeaconBluetoothAddress() {
        return mBeaconBluetoothAddress;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public void setBeaconBluetoothAddress(String beaconBluetoothAddress) {
        this.mBeaconBluetoothAddress = beaconBluetoothAddress;
    }

    @Nullable
    public String getTitleForList() {
        if (!Strings.isNullOrEmpty(mTitle)) {
            return mTitle;
        } else {
            return mDescription;
        }
    }

    @Nullable
    public String getDescription() {
        return mDescription;
    }

    public boolean isCompleted() {
        return mCompleted;
    }

    public boolean isActive() {
        return !mCompleted;
    }

    public boolean isEmpty() {
        return Strings.isNullOrEmpty(mTitle) &&
               Strings.isNullOrEmpty(mDescription);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Flower task = (Flower) o;
        return Objects.equal(mId, task.mId) &&
               Objects.equal(mTitle, task.mTitle) &&
               Objects.equal(mDescription, task.mDescription) &&
               Objects.equal(mBeaconBluetoothAddress, task.mBeaconBluetoothAddress);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(mId, mTitle, mDescription, mBeaconBluetoothAddress);
    }

    @Override
    public String toString() {
        return "Task with title " + mTitle;
    }
}
