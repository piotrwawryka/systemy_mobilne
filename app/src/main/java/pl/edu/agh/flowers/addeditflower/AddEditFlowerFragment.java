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

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import pl.edu.agh.flowers.R;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Main UI for the add task screen. Users can enter a task title and description.
 */
public class AddEditFlowerFragment extends Fragment implements AddEditFlowerContract.View {

    private static final String TAG = "AddEditTaskFragment";
    public static final String ARGUMENT_EDIT_TASK_ID = "EDIT_TASK_ID";
    private static final int GALLERY_CODE = 50;

    private AddEditFlowerContract.Presenter mPresenter;

    private TextView mTitle;

    private TextView mDescription;

    private Button addImageButton;

    private ImageView imageView;

    private Spinner beaconSpinner;

    private Uri imageUri;

    public static AddEditFlowerFragment newInstance() {
        return new AddEditFlowerFragment();
    }

    public AddEditFlowerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
    }

    @Override
    public void setPresenter(@NonNull AddEditFlowerContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        FloatingActionButton fab =
                (FloatingActionButton) getActivity().findViewById(R.id.fab_edit_task_done);
        fab.setImageResource(R.drawable.ic_done);
        fab.setOnClickListener(v -> {
            Bitmap bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
            final String beaconId = beaconSpinner.getSelectedItem() != null ? beaconSpinner.getSelectedItem().toString() : null;
            mPresenter.saveTask(mTitle.getText().toString(), mDescription.getText().toString(), beaconId, bitmap);
        });

        setupSpinner(AddEditFlowerActivity.dataAdapter);
    }

    public void setupSpinner(ArrayAdapter<String> arrayAdapter) {
        beaconSpinner = (Spinner) getActivity().findViewById(R.id.beacon_scanner);

        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        beaconSpinner.setAdapter(arrayAdapter);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.addtask_frag, container, false);
        mTitle = (TextView) root.findViewById(R.id.add_task_title);
        mDescription = (TextView) root.findViewById(R.id.add_task_description);
        setHasOptionsMenu(true);
        addImageButton = (Button) root.findViewById(R.id.add_image_button);
        imageView = (ImageView) root.findViewById(R.id.flower_image);
        addImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });
        return root;
    }

    private void openGallery() {

        if (ActivityCompat.checkSelfPermission(getContext(),
                android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                    1);
        }
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(intent, GALLERY_CODE);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK && requestCode == GALLERY_CODE) {
            imageUri = data.getData();
            imageView.setVisibility(View.VISIBLE);
            imageView.setImageURI(imageUri);
        }
    }

    @Override
    public void showEmptyTaskError() {
        Snackbar.make(mTitle, getString(R.string.empty_task_message), Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void showTasksList() {
        getActivity().setResult(Activity.RESULT_OK);
        getActivity().finish();
    }

    @Override
    public void setTitle(String title) {
        mTitle.setText(title);
    }

    @Override
    public void setDescription(String description) {
        mDescription.setText(description);
    }

    @Override
    public boolean isActive() {
        return isAdded();
    }

}
