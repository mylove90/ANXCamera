package com.bumptech.glide.util.a;

import android.support.annotation.NonNull;
import android.support.v4.util.Pools;
import android.util.Log;
import java.util.List;

/* compiled from: FactoryPools */
public final class d {
    private static final int DEFAULT_POOL_SIZE = 20;
    private static final String TAG = "FactoryPools";
    private static final C0014d<Object> gm = new a();

    /* compiled from: FactoryPools */
    public interface a<T> {
        T create();
    }

    /* compiled from: FactoryPools */
    private static final class b<T> implements Pools.Pool<T> {
        private final a<T> factory;
        private final C0014d<T> fm;
        private final Pools.Pool<T> pool;

        b(@NonNull Pools.Pool<T> pool2, @NonNull a<T> aVar, @NonNull C0014d<T> dVar) {
            this.pool = pool2;
            this.factory = aVar;
            this.fm = dVar;
        }

        public T acquire() {
            T acquire = this.pool.acquire();
            if (acquire == null) {
                acquire = this.factory.create();
                if (Log.isLoggable(d.TAG, 2)) {
                    Log.v(d.TAG, "Created new " + acquire.getClass());
                }
            }
            if (acquire instanceof c) {
                ((c) acquire).getVerifier().u(false);
            }
            return acquire;
        }

        public boolean release(@NonNull T t) {
            if (t instanceof c) {
                ((c) t).getVerifier().u(true);
            }
            this.fm.reset(t);
            return this.pool.release(t);
        }
    }

    /* compiled from: FactoryPools */
    public interface c {
        @NonNull
        g getVerifier();
    }

    /* renamed from: com.bumptech.glide.util.a.d$d  reason: collision with other inner class name */
    /* compiled from: FactoryPools */
    public interface C0014d<T> {
        void reset(@NonNull T t);
    }

    private d() {
    }

    @NonNull
    public static <T> Pools.Pool<List<T>> I(int i) {
        return a(new Pools.SynchronizedPool(i), new b(), new c());
    }

    @NonNull
    public static <T> Pools.Pool<List<T>> Lh() {
        return I(20);
    }

    @NonNull
    public static <T extends c> Pools.Pool<T> a(int i, @NonNull a<T> aVar) {
        return a(new Pools.SimplePool(i), aVar);
    }

    @NonNull
    private static <T extends c> Pools.Pool<T> a(@NonNull Pools.Pool<T> pool, @NonNull a<T> aVar) {
        return a(pool, aVar, el());
    }

    @NonNull
    private static <T> Pools.Pool<T> a(@NonNull Pools.Pool<T> pool, @NonNull a<T> aVar, @NonNull C0014d<T> dVar) {
        return new b(pool, aVar, dVar);
    }

    @NonNull
    public static <T extends c> Pools.Pool<T> b(int i, @NonNull a<T> aVar) {
        return a(new Pools.SynchronizedPool(i), aVar);
    }

    @NonNull
    private static <T> C0014d<T> el() {
        return gm;
    }
}
