package com.bumptech.glide.load.engine;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.v4.util.Pools;
import android.util.Log;
import com.bumptech.glide.Priority;
import com.bumptech.glide.e;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.c;
import com.bumptech.glide.load.engine.DecodeJob;
import com.bumptech.glide.load.engine.a.a;
import com.bumptech.glide.load.engine.a.o;
import com.bumptech.glide.load.engine.u;
import com.bumptech.glide.load.g;
import com.bumptech.glide.load.j;
import com.bumptech.glide.util.a.d;
import com.bumptech.glide.util.i;
import com.bumptech.glide.util.l;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public class Engine implements r, o.a, u.a {
    private static final String TAG = "Engine";
    private static final int uf = 150;
    private static final boolean vf = Log.isLoggable(TAG, 2);
    private final a Ce;
    private final o cache;
    private final w of;
    private final t pf;
    private final EngineJobFactory qf;
    private final D rf;
    private final DecodeJobFactory sf;
    private final ActiveResources tf;

    @VisibleForTesting
    static class DecodeJobFactory {
        final DecodeJob.d Ce;
        private int kf;
        final Pools.Pool<DecodeJob<?>> pool = d.a(150, new p(this));

        DecodeJobFactory(DecodeJob.d dVar) {
            this.Ce = dVar;
        }

        /* access modifiers changed from: package-private */
        public <R> DecodeJob<R> a(e eVar, Object obj, s sVar, c cVar, int i, int i2, Class<?> cls, Class<R> cls2, Priority priority, o oVar, Map<Class<?>, j<?>> map, boolean z, boolean z2, boolean z3, g gVar, DecodeJob.a<R> aVar) {
            DecodeJob acquire = this.pool.acquire();
            i.checkNotNull(acquire);
            DecodeJob decodeJob = acquire;
            int i3 = this.kf;
            int i4 = i3;
            this.kf = i3 + 1;
            return decodeJob.a(eVar, obj, sVar, cVar, i, i2, cls, cls2, priority, oVar, map, z, z2, z3, gVar, aVar, i4);
        }
    }

    @VisibleForTesting
    static class EngineJobFactory {
        final com.bumptech.glide.load.engine.b.b Mb;
        final com.bumptech.glide.load.engine.b.b Nb;
        final com.bumptech.glide.load.engine.b.b Rb;
        final com.bumptech.glide.load.engine.b.b lf;
        final r listener;
        final Pools.Pool<EngineJob<?>> pool = d.a(150, new q(this));

        EngineJobFactory(com.bumptech.glide.load.engine.b.b bVar, com.bumptech.glide.load.engine.b.b bVar2, com.bumptech.glide.load.engine.b.b bVar3, com.bumptech.glide.load.engine.b.b bVar4, r rVar) {
            this.Nb = bVar;
            this.Mb = bVar2;
            this.lf = bVar3;
            this.Rb = bVar4;
            this.listener = rVar;
        }

        private static void a(ExecutorService executorService) {
            executorService.shutdown();
            try {
                if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                    executorService.shutdownNow();
                    if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                        throw new RuntimeException("Failed to shutdown");
                    }
                }
            } catch (InterruptedException e2) {
                throw new RuntimeException(e2);
            }
        }

        /* access modifiers changed from: package-private */
        public <R> EngineJob<R> a(c cVar, boolean z, boolean z2, boolean z3, boolean z4) {
            EngineJob acquire = this.pool.acquire();
            i.checkNotNull(acquire);
            return acquire.init(cVar, z, z2, z3, z4);
        }

        /* access modifiers changed from: package-private */
        @VisibleForTesting
        public void shutdown() {
            a(this.Nb);
            a(this.Mb);
            a(this.lf);
            a(this.Rb);
        }
    }

    private static class a implements DecodeJob.d {
        private final a.C0007a factory;
        private volatile com.bumptech.glide.load.engine.a.a mf;

        a(a.C0007a aVar) {
            this.factory = aVar;
        }

        /* access modifiers changed from: package-private */
        @VisibleForTesting
        public synchronized void clearDiskCacheIfCreated() {
            if (this.mf != null) {
                this.mf.clear();
            }
        }

        public com.bumptech.glide.load.engine.a.a n() {
            if (this.mf == null) {
                synchronized (this) {
                    if (this.mf == null) {
                        this.mf = this.factory.build();
                    }
                    if (this.mf == null) {
                        this.mf = new com.bumptech.glide.load.engine.a.b();
                    }
                }
            }
            return this.mf;
        }
    }

    public static class b {
        private final com.bumptech.glide.request.g cb;
        private final EngineJob<?> nf;

        b(com.bumptech.glide.request.g gVar, EngineJob<?> engineJob) {
            this.cb = gVar;
            this.nf = engineJob;
        }

        public void cancel() {
            this.nf.b(this.cb);
        }
    }

    @VisibleForTesting
    Engine(o oVar, a.C0007a aVar, com.bumptech.glide.load.engine.b.b bVar, com.bumptech.glide.load.engine.b.b bVar2, com.bumptech.glide.load.engine.b.b bVar3, com.bumptech.glide.load.engine.b.b bVar4, w wVar, t tVar, ActiveResources activeResources, EngineJobFactory engineJobFactory, DecodeJobFactory decodeJobFactory, D d2, boolean z) {
        this.cache = oVar;
        a.C0007a aVar2 = aVar;
        this.Ce = new a(aVar);
        ActiveResources activeResources2 = activeResources == null ? new ActiveResources(z) : activeResources;
        this.tf = activeResources2;
        activeResources2.a((u.a) this);
        this.pf = tVar == null ? new t() : tVar;
        this.of = wVar == null ? new w() : wVar;
        this.qf = engineJobFactory == null ? new EngineJobFactory(bVar, bVar2, bVar3, bVar4, this) : engineJobFactory;
        this.sf = decodeJobFactory == null ? new DecodeJobFactory(this.Ce) : decodeJobFactory;
        this.rf = d2 == null ? new D() : d2;
        oVar.a((o.a) this);
    }

    public Engine(o oVar, a.C0007a aVar, com.bumptech.glide.load.engine.b.b bVar, com.bumptech.glide.load.engine.b.b bVar2, com.bumptech.glide.load.engine.b.b bVar3, com.bumptech.glide.load.engine.b.b bVar4, boolean z) {
        this(oVar, aVar, bVar, bVar2, bVar3, bVar4, (w) null, (t) null, (ActiveResources) null, (EngineJobFactory) null, (DecodeJobFactory) null, (D) null, z);
    }

    private static void a(String str, long j, c cVar) {
        Log.v(TAG, str + " in " + com.bumptech.glide.util.e.g(j) + "ms, key: " + cVar);
    }

    @Nullable
    private u<?> b(c cVar, boolean z) {
        if (!z) {
            return null;
        }
        u<?> b2 = this.tf.b(cVar);
        if (b2 != null) {
            b2.acquire();
        }
        return b2;
    }

    private u<?> c(c cVar, boolean z) {
        if (!z) {
            return null;
        }
        u<?> i = i(cVar);
        if (i != null) {
            i.acquire();
            this.tf.b(cVar, i);
        }
        return i;
    }

    private u<?> i(c cVar) {
        A<?> a2 = this.cache.a(cVar);
        if (a2 == null) {
            return null;
        }
        return a2 instanceof u ? (u) a2 : new u<>(a2, true, true);
    }

    public void Bf() {
        this.Ce.n().clear();
    }

    public <R> b a(e eVar, Object obj, c cVar, int i, int i2, Class<?> cls, Class<R> cls2, Priority priority, o oVar, Map<Class<?>, j<?>> map, boolean z, boolean z2, g gVar, boolean z3, boolean z4, boolean z5, boolean z6, com.bumptech.glide.request.g gVar2) {
        boolean z7 = z3;
        com.bumptech.glide.request.g gVar3 = gVar2;
        l.Ih();
        long Gh = vf ? com.bumptech.glide.util.e.Gh() : 0;
        s a2 = this.pf.a(obj, cVar, i, i2, map, cls, cls2, gVar);
        u<?> b2 = b(a2, z7);
        if (b2 != null) {
            gVar3.a(b2, DataSource.MEMORY_CACHE);
            if (vf) {
                a("Loaded resource from active resources", Gh, (c) a2);
            }
            return null;
        }
        u<?> c2 = c(a2, z7);
        if (c2 != null) {
            gVar3.a(c2, DataSource.MEMORY_CACHE);
            if (vf) {
                a("Loaded resource from cache", Gh, (c) a2);
            }
            return null;
        }
        EngineJob<?> a3 = this.of.a((c) a2, z6);
        if (a3 != null) {
            a3.a(gVar3);
            if (vf) {
                a("Added to existing load", Gh, (c) a2);
            }
            return new b(gVar3, a3);
        }
        EngineJob a4 = this.qf.a(a2, z3, z4, z5, z6);
        DecodeJob<R> a5 = this.sf.a(eVar, obj, a2, cVar, i, i2, cls, cls2, priority, oVar, map, z, z2, z6, gVar, a4);
        this.of.a((c) a2, (EngineJob<?>) a4);
        a4.a(gVar3);
        a4.c(a5);
        if (vf) {
            a("Started new load", Gh, (c) a2);
        }
        return new b(gVar3, a4);
    }

    public void a(c cVar, u<?> uVar) {
        l.Ih();
        this.tf.d(cVar);
        if (uVar.ug()) {
            this.cache.a(cVar, uVar);
        } else {
            this.rf.g(uVar);
        }
    }

    public void a(EngineJob<?> engineJob, c cVar) {
        l.Ih();
        this.of.b(cVar, engineJob);
    }

    public void a(EngineJob<?> engineJob, c cVar, u<?> uVar) {
        l.Ih();
        if (uVar != null) {
            uVar.a(cVar, this);
            if (uVar.ug()) {
                this.tf.b(cVar, uVar);
            }
        }
        this.of.b(cVar, engineJob);
    }

    public void b(@NonNull A<?> a2) {
        l.Ih();
        this.rf.g(a2);
    }

    public void e(A<?> a2) {
        l.Ih();
        if (a2 instanceof u) {
            ((u) a2).release();
            return;
        }
        throw new IllegalArgumentException("Cannot release anything but an EngineResource");
    }

    @VisibleForTesting
    public void shutdown() {
        this.qf.shutdown();
        this.Ce.clearDiskCacheIfCreated();
        this.tf.shutdown();
    }
}
