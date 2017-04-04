package com.bansalankit.learning;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresPermission;

import java.util.ArrayList;
import java.util.List;

/**
 * This utility class is used to check connectivity with Internet.
 * This also supports registering as {@link BroadcastReceiver} to get events whenever connectivity
 * is changed, you just need to add your class as {@link Listener} and remove when not required.
 * <p>
 * <br><i>Author : <b>Ankit Bansal</b></i>
 * <br><i>Created Date : <b>04 Apr 2017</b></i>
 * <br><i>Modified Date : <b>04 Apr 2017</b></i>
 */
public final class InternetUtility extends BroadcastReceiver {
    private static InternetUtility sInternetChecker = new InternetUtility();
    private final List<Listener> mListeners;

    /**
     * Access private : To avoid instantiation
     */
    private InternetUtility() {
        mListeners = new ArrayList<>();
    }

    // ======================== CONNECTIVITY related methods ======================== //

    private static NetworkInfo getActiveNetworkInfo(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return manager.getActiveNetworkInfo();
    }

    /**
     * @return {@code true} if phone is connected to any internet source, {@code false} otherwise.
     */
    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    public static boolean isConnected(Context context) {
        NetworkInfo networkInfo = getActiveNetworkInfo(context);
        return networkInfo != null && networkInfo.isConnected();
    }

    /**
     * @return {@code true} if phone is connected to WiFi, {@code false} otherwise.
     */
    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    public static boolean isConnectedWifi(Context context) {
        NetworkInfo networkInfo = getActiveNetworkInfo(context);
        return networkInfo != null && networkInfo.isConnected() && networkInfo.getType() == ConnectivityManager.TYPE_WIFI;
    }

    /**
     * @return {@code true} if phone is connected to Mobile Network, {@code false} otherwise.
     */
    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    public static boolean isConnectedMobile(Context context) {
        NetworkInfo networkInfo = getActiveNetworkInfo(context);
        return networkInfo != null && networkInfo.isConnected() && networkInfo.getType() == ConnectivityManager.TYPE_MOBILE;
    }

    // ======================== LISTENER related methods ======================== //

    /**
     * @param listener Listener object which will observe Connectivity changes.
     */
    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    public static synchronized void addListener(Context context, Listener listener) {
        if (context == null || listener == null || sInternetChecker.mListeners.contains(listener)) return;

        // Register the receiver to get Connectivity related events and add listener to list.
        if (sInternetChecker.mListeners.isEmpty()) sInternetChecker.registerReceiver(context);
        sInternetChecker.mListeners.add(listener);
    }

    private void registerReceiver(@NonNull Context context) {
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        context.registerReceiver(this, intentFilter);
    }

    /**
     * @param listener Listener object which was observing Connectivity changes.
     */
    public static synchronized void removeListener(Context context, Listener listener) {
        if (context == null || listener == null) return;

        // Unregister receiver as no-one's listening for connectivity changes now
        if (sInternetChecker.mListeners.remove(listener) && sInternetChecker.mListeners.isEmpty())
            sInternetChecker.unregisterReceiver(context);
    }

    private void unregisterReceiver(@NonNull Context context) {
        context.unregisterReceiver(sInternetChecker);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null || intent.getAction() == null) return;

        // There is complete lack of connectivity from Wifi and Mobile
        if (intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false)) {
            notifyListeners(false);
        }
        // Notify as per information from active network that is or about to get connected
        else notifyListeners(isConnected(context));
    }

    private void notifyListeners(boolean connected) {
        for (Listener listener : mListeners) listener.onConnectivityChange(connected);
    }

    public interface Listener {
        /**
         * Callback when Internet Connectivity changes.
         *
         * @param connected {@code true} if Internet is now connected, {@code false} otherwise.
         */
        void onConnectivityChange(boolean connected);
    }
}