package com.bumptech.glide.load.a.a;

import android.net.Uri;
import com.ss.android.ugc.effectmanager.effect.model.ComposerHelper;
import com.ss.android.vesdk.VEEditor;

/* compiled from: MediaStoreUtil */
public final class b {
    private static final int he = 512;
    private static final int ie = 384;

    private b() {
    }

    public static boolean d(Uri uri) {
        return e(uri) && !j(uri);
    }

    public static boolean e(Uri uri) {
        return uri != null && ComposerHelper.COMPOSER_CONTENT.equals(uri.getScheme()) && "media".equals(uri.getAuthority());
    }

    public static boolean f(Uri uri) {
        return e(uri) && j(uri);
    }

    private static boolean j(Uri uri) {
        return uri.getPathSegments().contains(VEEditor.MVConsts.TYPE_VIDEO);
    }

    public static boolean k(int i, int i2) {
        return i != Integer.MIN_VALUE && i2 != Integer.MIN_VALUE && i <= 512 && i2 <= ie;
    }
}
