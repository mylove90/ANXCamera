package com.android.camera.module;

/* compiled from: lambda */
public final /* synthetic */ class p implements Runnable {
    private final /* synthetic */ Camera2Module ub;
    private final /* synthetic */ boolean vb;

    public /* synthetic */ p(Camera2Module camera2Module, boolean z) {
        this.ub = camera2Module;
        this.vb = z;
    }

    public final void run() {
        this.ub.j(this.vb);
    }
}
