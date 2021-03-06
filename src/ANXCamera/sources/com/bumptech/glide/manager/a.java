package com.bumptech.glide.manager;

import android.support.annotation.NonNull;
import com.bumptech.glide.util.l;
import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

/* compiled from: ActivityFragmentLifecycle */
class a implements i {
    private final Set<j> ek = Collections.newSetFromMap(new WeakHashMap());
    private boolean fk;
    private boolean ya;

    a() {
    }

    public void a(@NonNull j jVar) {
        this.ek.remove(jVar);
    }

    public void b(@NonNull j jVar) {
        this.ek.add(jVar);
        if (this.fk) {
            jVar.onDestroy();
        } else if (this.ya) {
            jVar.onStart();
        } else {
            jVar.onStop();
        }
    }

    /* access modifiers changed from: package-private */
    public void onDestroy() {
        this.fk = true;
        for (T onDestroy : l.b(this.ek)) {
            onDestroy.onDestroy();
        }
    }

    /* access modifiers changed from: package-private */
    public void onStart() {
        this.ya = true;
        for (T onStart : l.b(this.ek)) {
            onStart.onStart();
        }
    }

    /* access modifiers changed from: package-private */
    public void onStop() {
        this.ya = false;
        for (T onStop : l.b(this.ek)) {
            onStop.onStop();
        }
    }
}
