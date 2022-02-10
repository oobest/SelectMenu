package com.example.myapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.example.myapplication.menu.SelectMenu;
import com.example.myapplication.menu.SelectMenuCallback;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    static final int REQUEST_TAKE_PHOTO = 1;

    private static final int PERMISSION_REQUEST_CAMERA = 0;

    private ImageView imageView;

    private TextView selectBridge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = findViewById(R.id.imageView);

        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestCameraPermission();
            }
        });

        selectBridge = findViewById(R.id.selectBridge);
        selectBridge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMenu(view);
            }
        });
    }

    private void showMenu(View view) {
        List<String> dataList = Arrays.asList("桥梁一", "桥梁二", "桥梁三", "桥梁四", "桥梁五","桥梁六","桥梁七","桥梁8","桥梁9","桥梁10");
//           List<String> dataList = Arrays.asList("桥梁一", "桥梁二", "桥梁三");

        new SelectMenu<String>()
                .setDataList(dataList)
                .setFragmentManager(getSupportFragmentManager())
                .setBindView(view)
                .show(o -> {
                    selectBridge.setText(o);
                });
    }

    private File photoFile = null;

    @SuppressLint("QueryPermissionsNeeded")
    private void dispatchTakePictureIntent() {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Log.e(TAG, ex.getLocalizedMessage());
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.myapplication.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        return File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
    }

    private void requestCameraPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA) || ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // Display a SnackBar with cda button to request the missing permission.
            Snackbar.make(imageView, R.string.camera_access_required,
                    Snackbar.LENGTH_INDEFINITE).setAction(R.string.ok, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Request the permission
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            PERMISSION_REQUEST_CAMERA);
                }
            }).show();

        } else {
            Snackbar.make(imageView, R.string.camera_unavailable, Snackbar.LENGTH_SHORT).show();
            // Request the permission. The result will be received in onRequestPermissionResult().
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST_CAMERA);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CAMERA) {
            // Request for camera permission.
            if (grantResults.length == 2
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                // Permission has been granted. Start camera preview Activity.
                Snackbar.make(imageView, R.string.camera_permission_granted,
                        Snackbar.LENGTH_SHORT)
                        .show();
                dispatchTakePictureIntent();
            } else {
                // Permission request was denied.
                Snackbar.make(imageView, R.string.camera_permission_denied,
                        Snackbar.LENGTH_SHORT)
                        .show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            if (data != null) {
                File target = null;
                try {
                    target = createImageFile();
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
                CompressImageUtils.compress(this, photoFile, target, new CompressImageUtils.ResultListener() {
                    @Override
                    public void onSuccess(File file) {
                        Log.d(TAG, "compressed" + file.getAbsolutePath());
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Log.e(TAG, throwable.getMessage(), throwable);
                    }
                });

            }
        }
    }
}