package com.android.camera.data.data.config;

import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.provider.MiuiSettings;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import com.android.camera.CameraSettings;
import com.android.camera.CameraSize;
import com.android.camera.R;
import com.android.camera.Util;
import com.android.camera.data.data.ComponentData;
import com.android.camera.data.data.ComponentDataItem;
import com.android.camera.fragment.beauty.BeautyValues;
import com.android.camera.log.Log;
import com.android.camera2.CameraCapabilities;
import com.android.camera2.MiCustomFpsRange;
import com.mi.config.b;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ComponentConfigVideoQuality extends ComponentData {
    public static final String QUALITY_1080P = "6";
    public static final String QUALITY_1080P_60FPS = "6,60";
    public static final String QUALITY_4K = "8";
    public static final String QUALITY_4K_60FPS = "8,60";
    public static final String QUALITY_720P = "5";
    private static final String TAG = "ComponentConfigVideoQuality";
    private String mDefaultValue = "6";
    private String mForceValue;

    public ComponentConfigVideoQuality(DataItemConfig dataItemConfig) {
        super(dataItemConfig);
    }

    private boolean isContain(String str, List<ComponentDataItem> list) {
        if (list == null || list.size() == 0) {
            return false;
        }
        for (ComponentDataItem componentDataItem : list) {
            if (TextUtils.equals(str, componentDataItem.mValue)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isSupportFpsRange(int i, int i2, int i3, CameraCapabilities cameraCapabilities) {
        if (i3 != 0) {
            return false;
        }
        List<MiCustomFpsRange> supportedCustomFpsRange = cameraCapabilities.getSupportedCustomFpsRange();
        if (supportedCustomFpsRange != null && !supportedCustomFpsRange.isEmpty()) {
            for (MiCustomFpsRange next : supportedCustomFpsRange) {
                if (next.getWidth() == i && next.getHeight() == i2) {
                    return true;
                }
            }
        }
        return false;
    }

    /* access modifiers changed from: protected */
    public boolean checkValueValid(String str) {
        if (isContain(str, this.mItems)) {
            return true;
        }
        String str2 = TAG;
        Log.d(str2, "checkValueValid: invalid value: " + str);
        return false;
    }

    public boolean disableUpdate() {
        if (this.mForceValue != null) {
            return true;
        }
        List<ComponentDataItem> list = this.mItems;
        return list == null || list.size() == 1;
    }

    public String getComponentValue(int i) {
        return getComponentValue(i, "");
    }

    public String getComponentValue(int i, String str) {
        String str2 = this.mForceValue;
        if (str2 != null) {
            return str2;
        }
        if (!TextUtils.isEmpty(str) && checkValueValid(str)) {
            return str;
        }
        String defaultValue = getDefaultValue(i);
        String string = this.mParentDataItem.getString(getKey(i), defaultValue);
        if (string == null || string.equals(defaultValue) || checkValueValid(string)) {
            return string;
        }
        String simpleName = ComponentConfigVideoQuality.class.getSimpleName();
        Log.e(simpleName, "reset invalid value " + string);
        int indexOf = string.indexOf(",");
        if (indexOf <= 0) {
            return getDefaultValue(i);
        }
        String substring = string.substring(0, indexOf);
        return isContain(substring, this.mItems) ? substring : getDefaultValue(i);
    }

    @NonNull
    public String getDefaultValue(int i) {
        String str = this.mForceValue;
        return str != null ? str : this.mDefaultValue;
    }

    public int getDisplayTitleString() {
        return R.string.pref_video_quality_title;
    }

    public List<ComponentDataItem> getItems() {
        List<ComponentDataItem> list = this.mItems;
        return list == null ? Collections.emptyList() : list;
    }

    public String getKey(int i) {
        return "pref_video_quality_key";
    }

    public String getNextValue(int i) {
        String persistValue = getPersistValue(i);
        List<ComponentDataItem> list = this.mItems;
        if (list != null) {
            int size = list.size();
            for (int i2 = 0; i2 < size; i2++) {
                if (TextUtils.equals(this.mItems.get(i2).mValue, persistValue)) {
                    return this.mItems.get((i2 + 1) % size).mValue;
                }
            }
        }
        return getDefaultValue(i);
    }

    public void reInit(int i, int i2, CameraCapabilities cameraCapabilities, int i3) {
        int i4 = i;
        int i5 = i2;
        CameraCapabilities cameraCapabilities2 = cameraCapabilities;
        int i6 = i3;
        ArrayList arrayList = new ArrayList();
        this.mForceValue = null;
        List<CameraSize> supportedOutputSize = cameraCapabilities2.getSupportedOutputSize(MediaRecorder.class);
        int i7 = CameraSettings.get4kProfile();
        if (i4 == 162 || i4 == 169) {
            if (CameraSettings.isStereoModeOn() || CameraSettings.isAutoZoomEnabled(i) || CameraSettings.isSuperEISEnabled(i)) {
                this.mForceValue = "6";
            }
            if (CameraSettings.isFaceBeautyOn(i4, (BeautyValues) null) || CameraSettings.isVideoBokehOn()) {
                this.mForceValue = "5";
            }
            if (supportedOutputSize.contains(new CameraSize(1280, Util.LIMIT_SURFACE_WIDTH)) && CamcorderProfile.hasProfile(i5, 5)) {
                String str = this.mForceValue;
                if (str == null || !str.equals("5")) {
                    arrayList.add(new ComponentDataItem((int) R.drawable.ic_config_720p_30, (int) R.drawable.ic_config_720p_30, (int) R.string.pref_video_quality_entry_720p, "5"));
                } else {
                    arrayList.add(new ComponentDataItem((int) R.drawable.ic_config_720p_30_disable, (int) R.drawable.ic_config_720p_30_disable, (int) R.string.pref_video_quality_entry_720p, "5"));
                }
            }
            if (supportedOutputSize.contains(new CameraSize(1920, MiuiSettings.ScreenEffect.SCREEN_PAPER_MODE_TWILIGHT_END_DEAULT)) && CamcorderProfile.hasProfile(i5, 6)) {
                String str2 = this.mForceValue;
                if (str2 == null || !str2.equals("6")) {
                    arrayList.add(new ComponentDataItem((int) R.drawable.ic_config_1080p_30, (int) R.drawable.ic_config_1080p_30, (int) R.string.pref_video_quality_entry_1080p, "6"));
                } else {
                    arrayList.add(new ComponentDataItem((int) R.drawable.ic_config_1080p_30_disable, (int) R.drawable.ic_config_1080p_30_disable, (int) R.string.pref_video_quality_entry_1080p, "6"));
                }
                if (i4 != 169 && isSupportFpsRange(1920, MiuiSettings.ScreenEffect.SCREEN_PAPER_MODE_TWILIGHT_END_DEAULT, i6, cameraCapabilities2)) {
                    arrayList.add(new ComponentDataItem((int) R.drawable.ic_config_1080p_60, (int) R.drawable.ic_config_1080p_60, (int) R.string.pref_video_quality_entry_1080p_60fps, QUALITY_1080P_60FPS));
                }
            }
            if (b.Cj() && supportedOutputSize.contains(new CameraSize(3840, 2160)) && CamcorderProfile.hasProfile(i5, i7)) {
                arrayList.add(new ComponentDataItem((int) R.drawable.ic_config_4k_30, (int) R.drawable.ic_config_4k_30, (int) R.string.pref_video_quality_entry_4kuhd, "8"));
                if (i4 != 169 && isSupportFpsRange(3840, 2160, i6, cameraCapabilities2)) {
                    arrayList.add(new ComponentDataItem((int) R.drawable.ic_config_4k_60, (int) R.drawable.ic_config_4k_60, (int) R.string.pref_video_quality_entry_4kuhd_60fps, QUALITY_4K_60FPS));
                }
            }
        }
        if (arrayList.size() == 1) {
            if (((ComponentDataItem) arrayList.get(0)).mValue.equals("5")) {
                this.mForceValue = "5";
                arrayList.clear();
                arrayList.add(new ComponentDataItem((int) R.drawable.ic_config_720p_30_disable, (int) R.drawable.ic_config_720p_30_disable, (int) R.string.pref_video_quality_entry_720p, "5"));
            } else if (((ComponentDataItem) arrayList.get(0)).mValue.equals("6")) {
                this.mForceValue = "6";
                arrayList.clear();
                arrayList.add(new ComponentDataItem((int) R.drawable.ic_config_1080p_30_disable, (int) R.drawable.ic_config_1080p_30_disable, (int) R.string.pref_video_quality_entry_1080p, "6"));
            }
        } else if (i5 == 1) {
            this.mDefaultValue = "6";
        } else if (i5 == 0) {
            String string = CameraSettings.getString(R.string.pref_video_quality_default);
            if (!isContain(string, arrayList)) {
                int size = arrayList.size();
                if (size > 0) {
                    this.mDefaultValue = ((ComponentDataItem) arrayList.get(size - 1)).mValue;
                }
            } else {
                this.mDefaultValue = string;
            }
        }
        this.mItems = Collections.unmodifiableList(arrayList);
    }
}
