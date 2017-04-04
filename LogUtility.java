package com.bansalankit.learning;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.annotation.StringRes;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * This utility class is used for logging and toasting purpose.
 * File logging is also supported and can be toggled using preferences.
 * <p>
 * <br><i>Author : <b>Ankit Bansal</b></i>
 * <br><i>Created Date : <b>30 Mar 2017</b></i>
 * <br><i>Modified Date : <b>4 Apr 2017</b></i>
 */
public final class LogUtility {
    private static final String TAG = LogUtility.class.getSimpleName();
    private static final String FILE_NAME = "logs.txt";

    private static LogUtility sLogger;
    private boolean isFileLoggingEnabled;
    private boolean isDebuggable;
    private File mLogFile;

    static {
        sLogger = new LogUtility();
    }

    /**
     * Access private : To avoid instantiation
     */
    private LogUtility() {
        isDebuggable = BuildConfig.DEBUGGABLE;
    }

    // ======================== FILE LOG related methods ======================== //

    /**
     * Call this when application class is created and/or after changing the file logging setting in
     * preference.
     *
     * @param fileLogging {@code true} if logs (except DEBUG ones) must be printed in log file, {@code false} otherwise
     */
    public static void toggleFileLogging(Context context, boolean fileLogging) {
        if (context == null) return;

        // Nullify log file if not required, else initialize it.
        sLogger.isFileLoggingEnabled = fileLogging;
        if (!fileLogging) sLogger.mLogFile = null;
        else {
            File parent = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
            if (parent != null) sLogger.mLogFile = new File(parent, FILE_NAME);
        }
    }

    private synchronized void addFileEntry(final String tag, final String message, final Throwable error) {
        if (!isFileLoggingEnabled || mLogFile == null) {
            debug(TAG, "File logging disabled or not initialized");
            return;
        }

        // Start BG task to write in file
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    if (!mLogFile.exists() && !mLogFile.createNewFile()) return null;

                    // PrintWriter used for error tracing and BufferedWriter used for performance
                    PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(mLogFile, true)), true);
                    writer.write(System.currentTimeMillis() + "\t" + tag + "\t" + message);
                    if (error != null) error.printStackTrace(writer);
                    writer.close();

                    // Update last modified time of log file
                    // noinspection ResultOfMethodCallIgnored
                    mLogFile.setLastModified(System.currentTimeMillis());
                } catch (IOException ignored) {/*Ignored*/}
                return null;
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    // ======================== SYSTEM LOG related methods ======================== //

    public static void debug(String tag, String message) {
        if (TextUtils.isEmpty(tag) || TextUtils.isEmpty(message)) return;

        // Make a system log entry if app is debuggable
        if (sLogger.isDebuggable) Log.d(tag, message);
    }

    public static void info(String tag, String message) {
        if (TextUtils.isEmpty(tag) || TextUtils.isEmpty(message)) return;

        // Make a system log entry if app is debuggable and add in log file as well.
        if (sLogger.isDebuggable) Log.i(tag, message);
        sLogger.addFileEntry(tag, message, null);
    }

    public static void warn(String tag, String message) {
        if (TextUtils.isEmpty(tag) || TextUtils.isEmpty(message)) return;

        // Make a system log entry if app is debuggable and add in log file as well.
        if (sLogger.isDebuggable) Log.w(tag, message);
        sLogger.addFileEntry(tag, message, null);
    }

    public static void error(String tag, String message, Throwable error) {
        if (TextUtils.isEmpty(tag) || TextUtils.isEmpty(message)) return;

        // Make a system log entry if app is debuggable and add in log file as well.
        if (sLogger.isDebuggable) Log.e(tag, message, error);
        sLogger.addFileEntry(tag, message, error);
    }

    // ======================== TOAST related methods ======================== //

    public static void toastLong(Context context, @StringRes int msgId) {
        if (context != null && msgId != 0 && msgId != -1) // Show if msg is valid resource
            Toast.makeText(context, msgId, Toast.LENGTH_LONG).show();
    }

    public static void toastShort(Context context, @StringRes int msgId) {
        if (context != null && msgId != 0 && msgId != -1) // Show if msg is valid resource
            Toast.makeText(context, msgId, Toast.LENGTH_SHORT).show();
    }
}
