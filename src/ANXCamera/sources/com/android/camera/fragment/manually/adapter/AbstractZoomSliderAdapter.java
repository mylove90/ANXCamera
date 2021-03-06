package com.android.camera.fragment.manually.adapter;

import com.android.camera.ui.HorizontalSlideView;
import com.mi.config.b;

public abstract class AbstractZoomSliderAdapter extends HorizontalSlideView.HorizontalDrawAdapter implements HorizontalSlideView.OnPositionSelectListener {
    protected static int getRealZoomRatioTele() {
        return b.An ? 17 : 20;
    }

    public abstract boolean isEnable();

    public abstract float mapPositionToZoomRatio(float f2);

    public abstract float mapZoomRatioToPosition(float f2);

    public abstract void setEnable(boolean z);
}
