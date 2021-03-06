package com.bumptech.glide.manager;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.util.Log;
import com.bumptech.glide.manager.c;
import com.bumptech.glide.util.i;

/* compiled from: DefaultConnectivityMonitor */
final class f implements c {
    private static final String TAG = "ConnectivityMonitor";
    private final Context context;
    private final BroadcastReceiver gk = new e(this);
    boolean isConnected;
    private boolean isRegistered;
    final c.a listener;

    f(@NonNull Context context2, @NonNull c.a aVar) {
        this.context = context2.getApplicationContext();
        this.listener = aVar;
    }

    private void register() {
        if (!this.isRegistered) {
            this.isConnected = isConnected(this.context);
            try {
                this.context.registerReceiver(this.gk, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
                this.isRegistered = true;
            } catch (SecurityException e2) {
                if (Log.isLoggable(TAG, 5)) {
                    Log.w(TAG, "Failed to register", e2);
                }
            }
        }
    }

    private void unregister() {
        if (this.isRegistered) {
            this.context.unregisterReceiver(this.gk);
            this.isRegistered = false;
        }
    }

    /* access modifiers changed from: package-private */
    @SuppressLint({"MissingPermission"})
    public boolean isConnected(@NonNull Context context2) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context2.getSystemService("connectivity");
        i.checkNotNull(connectivityManager);
        try {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        } catch (RuntimeException e2) {
            if (Log.isLoggable(TAG, 5)) {
                Log.w(TAG, "Failed to determine connectivity status when connectivity changed", e2);
            }
            return true;
        }
    }

    public void onDestroy() {
    }

    public void onStart() {
        register();
    }

    public void onStop() {
        unregister();
    }
}
