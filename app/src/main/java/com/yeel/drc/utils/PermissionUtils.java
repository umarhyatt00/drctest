package com.yeel.drc.utils;
import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.yeel.drc.dialogboxes.AskStoragePermissionDialog;
import androidx.activity.result.ActivityResultLauncher;
public class PermissionUtils {
    Context context;
    AskStoragePermissionDialog askStoragePermissionDialog;

    public PermissionUtils(Context context) {
        this.context = context;
    }

    public boolean checkCameraPermission(
            ActivityResultLauncher<String> requestPermissionLauncher
    ) {
        if (ContextCompat.checkSelfPermission(
                context, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED) {
            return true;
        } else if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.CAMERA)) {
            askStoragePermissionDialog = new AskStoragePermissionDialog(context);
            askStoragePermissionDialog.show("camera");
        } else {
            requestPermissionLauncher.launch(
                    Manifest.permission.CAMERA);
        }
        return false;
    }

    public boolean checkCameraPermissionForScan(
            ActivityResultLauncher<String> requestPermissionLauncher
    ) {
        if (ContextCompat.checkSelfPermission(
                context, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED) {
            return true;
        } else if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.CAMERA)) {
            askStoragePermissionDialog = new AskStoragePermissionDialog(context);
            askStoragePermissionDialog.show("scan");
        } else {
            requestPermissionLauncher.launch(
                    Manifest.permission.CAMERA);
        }
        return false;
    }

    public boolean checkLocationPermission(ActivityResultLauncher<String> requestPermissionLauncher) {
        if (ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            return true;
        } else if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.ACCESS_FINE_LOCATION)) {
            askStoragePermissionDialog = new AskStoragePermissionDialog(context);
            askStoragePermissionDialog.show("location");
        } else {
            requestPermissionLauncher.launch(
                    Manifest.permission.ACCESS_FINE_LOCATION);
        }
        return false;
    }

    public boolean checkGalleryPermission(ActivityResultLauncher<String> requestPermissionLauncher) {
        String permissionGallery=null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionGallery = Manifest.permission.READ_MEDIA_IMAGES;
        }else{
            permissionGallery = Manifest.permission.READ_EXTERNAL_STORAGE;
        }
        if (ContextCompat.checkSelfPermission(
                context, permissionGallery) ==
                PackageManager.PERMISSION_GRANTED) {
            return true;
        } else if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, permissionGallery)) {
            askStoragePermissionDialog = new AskStoragePermissionDialog(context);
            askStoragePermissionDialog.show("gallery");
        } else {
            requestPermissionLauncher.launch(permissionGallery);
        }
        return false;
    }

    public boolean checkGalleryPermissionShare(ActivityResultLauncher<String> requestPermissionLauncher) {
        String permissionGallery=null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionGallery = Manifest.permission.READ_MEDIA_IMAGES;
        }else{
            permissionGallery = Manifest.permission.READ_EXTERNAL_STORAGE;
        }
        if (ContextCompat.checkSelfPermission(
                context, permissionGallery) ==
                PackageManager.PERMISSION_GRANTED) {
            return true;
        } else if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, permissionGallery)) {
            askStoragePermissionDialog = new AskStoragePermissionDialog(context);
            askStoragePermissionDialog.show("share");
        } else {
            requestPermissionLauncher.launch(permissionGallery);
        }
        return false;
    }


    public boolean checkContactPermission(ActivityResultLauncher<String> requestPermissionLauncher) {
        if (ContextCompat.checkSelfPermission(
                context, Manifest.permission.READ_CONTACTS) ==
                PackageManager.PERMISSION_GRANTED) {
            return true;
        } else if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.READ_CONTACTS)) {
            askStoragePermissionDialog = new AskStoragePermissionDialog(context);
            askStoragePermissionDialog.show("contact");
        } else {
            requestPermissionLauncher.launch(Manifest.permission.READ_CONTACTS);
        }
        return false;
    }


    public boolean checkPushNotificationPermission(ActivityResultLauncher<String> requestPermissionLauncher) {
        String permissionPushNotification = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionPushNotification = Manifest.permission.POST_NOTIFICATIONS;
        }
        assert permissionPushNotification != null;
        if (ContextCompat.checkSelfPermission(
                context, permissionPushNotification) ==
                PackageManager.PERMISSION_GRANTED) {
            return true;
        } else if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, permissionPushNotification)) {
            return false;
        } else {
            requestPermissionLauncher.launch(permissionPushNotification);
        }
        return false;
    }


}
