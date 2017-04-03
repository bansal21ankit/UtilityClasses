package com.bansalankit.learning;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.annotation.IntDef;
import android.support.annotation.StringDef;
import android.support.annotation.StringRes;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.SOURCE;

/**
 * This utility class is used to ease out the runtime permission requesting process.
 * You still needs to override {@code onRequestPermissionsResult()} method in their activity class
 * to check the results of permission requests.
 * <p>
 * <b>NOTE: </b>This class requires {@code <string name="permission_reason_title">Permission Info.</string>}
 * to be defined in {@code strings.xml}.
 * <p>
 * <br><i>Author : <b>Ankit Bansal</b></i>
 * <br><i>Created Date : <b>31 Mar 2017</b></i>
 * <br><i>Modified Date : <b>3 Apr 2017</b></i>
 */
public final class PermissionUtility {
    @Retention(SOURCE)
    @StringDef({/*TODO Add required permissions here android.Manifest.permission.XXX*/})
    public @interface PermissionNames {
    }

    @Retention(SOURCE)
    @IntDef({/*TODO Add request code for above permissions here*/})
    public @interface PermissionCodes {
        // TODO Add request code values here
    }

    /**
     * Access private : To avoid instantiation.
     */
    private PermissionUtility() {
    }

    /**
     * @param permission Permission application is seeking, add required permission in allowed ones as well.
     * @return {@code true} if permission is granted to application, {@code false} otherwise.
     */
    public static boolean isAlreadyGranted(Context context, @PermissionNames String permission) {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * @param permission Permission application is seeking, add required permission in allowed ones as well.
     * @return {@code true} if reason for permission must be shown whether if this is first time or
     * user has denied permission before, {@code false} otherwise.
     */
    private static boolean requireReason(Activity activity, @PermissionNames String permission) {
        return ActivityCompat.shouldShowRequestPermissionRationale(activity, permission);
    }

    /**
     * @param reasonId    Reason for the permission, shown only if required.
     * @param permission  Permission application is seeking, add required permission in allowed ones as well.
     * @param requestCode Request Code for the permission.
     */
    public static void askPermission(final Activity activity, @StringRes int reasonId, @PermissionNames final String permission, @PermissionCodes final int requestCode) {
        if (isAlreadyGranted(activity, permission)) return; // Do nothing as permission is already granted.

        if (!requireReason(activity, permission)) askPermission(activity, permission, requestCode);
        else {
            // Show alert stating the reason for permission and ask for permission if user respond positively.
            new AlertDialog.Builder(activity).setTitle(R.string.permission_reason_title).setMessage(reasonId)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            askPermission(activity, permission, requestCode);
                        }
                    }).setNegativeButton(android.R.string.cancel, null);
        }
    }

    private static void askPermission(Activity activity, @PermissionNames String permission, @PermissionCodes int requestCode) {
        ActivityCompat.requestPermissions(activity, new String[]{permission}, requestCode);
    }
}