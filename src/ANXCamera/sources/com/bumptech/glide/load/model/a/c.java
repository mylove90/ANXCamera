package com.bumptech.glide.load.model.a;

import android.net.Uri;
import android.support.annotation.NonNull;
import com.bumptech.glide.load.g;
import com.bumptech.glide.load.model.l;
import com.bumptech.glide.load.model.t;
import com.bumptech.glide.load.model.u;
import com.bumptech.glide.load.model.x;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/* compiled from: HttpUriLoader */
public class c implements t<Uri, InputStream> {
    private static final Set<String> SCHEMES = Collections.unmodifiableSet(new HashSet(Arrays.asList(new String[]{"http", "https"})));
    private final t<l, InputStream> li;

    /* compiled from: HttpUriLoader */
    public static class a implements u<Uri, InputStream> {
        public void D() {
        }

        @NonNull
        public t<Uri, InputStream> a(x xVar) {
            return new c(xVar.a(l.class, InputStream.class));
        }
    }

    public c(t<l, InputStream> tVar) {
        this.li = tVar;
    }

    public t.a<InputStream> a(@NonNull Uri uri, int i, int i2, @NonNull g gVar) {
        return this.li.a(new l(uri.toString()), i, i2, gVar);
    }

    /* renamed from: i */
    public boolean c(@NonNull Uri uri) {
        return SCHEMES.contains(uri.getScheme());
    }
}
