package com.bumptech.glide.load.b.d;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.bumptech.glide.load.b.a.b;
import com.bumptech.glide.load.engine.A;
import com.bumptech.glide.load.g;
import java.io.ByteArrayOutputStream;

/* compiled from: BitmapBytesTranscoder */
public class a implements e<Bitmap, byte[]> {
    private final Bitmap.CompressFormat Yj;
    private final int quality;

    public a() {
        this(Bitmap.CompressFormat.JPEG, 100);
    }

    public a(@NonNull Bitmap.CompressFormat compressFormat, int i) {
        this.Yj = compressFormat;
        this.quality = i;
    }

    @Nullable
    public A<byte[]> a(@NonNull A<Bitmap> a2, @NonNull g gVar) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        a2.get().compress(this.Yj, this.quality, byteArrayOutputStream);
        a2.recycle();
        return new b(byteArrayOutputStream.toByteArray());
    }
}
