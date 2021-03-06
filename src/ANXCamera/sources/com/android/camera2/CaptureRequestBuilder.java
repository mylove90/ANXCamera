package com.android.camera2;

import android.graphics.Rect;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.MeteringRectangle;
import android.util.Range;
import android.util.Rational;
import com.android.camera.HybridZoomingSystem;
import com.android.camera.Util;
import com.android.camera.data.DataRepository;
import com.android.camera.log.Log;
import com.android.camera.module.ModuleManager;
import com.android.camera2.compat.MiCameraCompat;
import com.android.camera2.vendortag.CaptureRequestVendorTags;
import com.mi.config.b;

public class CaptureRequestBuilder {
    private static final long MAX_REALTIME_EXPOSURE_TIME = 125000000;
    private static final String TAG = "CaptureRequestBuilder";

    static void applyAELock(CaptureRequest.Builder builder, boolean z) {
        if (builder != null) {
            String str = TAG;
            Log.d(str, "applyAELock: " + z);
            builder.set(CaptureRequest.CONTROL_AE_LOCK, Boolean.valueOf(z));
        }
    }

    static void applyAERegions(CaptureRequest.Builder builder, CameraConfigs cameraConfigs) {
        if (builder != null) {
            MeteringRectangle[] aERegions = cameraConfigs.getAERegions();
            if (aERegions != null) {
                builder.set(CaptureRequest.CONTROL_AE_REGIONS, aERegions);
            } else {
                builder.set(CaptureRequest.CONTROL_AE_REGIONS, MiCamera2.ZERO_WEIGHT_3A_REGION);
            }
        }
    }

    static void applyAFRegions(CaptureRequest.Builder builder, CameraConfigs cameraConfigs) {
        if (builder != null) {
            MeteringRectangle[] aFRegions = cameraConfigs.getAFRegions();
            if (aFRegions != null) {
                builder.set(CaptureRequest.CONTROL_AF_REGIONS, aFRegions);
            } else {
                builder.set(CaptureRequest.CONTROL_AF_REGIONS, MiCamera2.ZERO_WEIGHT_3A_REGION);
            }
        }
    }

    public static void applyASDScene(CameraCapabilities cameraCapabilities, CaptureRequest.Builder builder, CameraConfigs cameraConfigs) {
        if (builder != null && cameraConfigs != null) {
            if (!cameraCapabilities.isASDSceneSupported()) {
                Log.d(TAG, "applyASDScene(): unsupported");
                return;
            }
            int aSDScene = cameraConfigs.getASDScene();
            String str = TAG;
            Log.d(str, "applyASDScene: " + aSDScene);
            MiCameraCompat.applyASDScene(builder, aSDScene);
        }
    }

    static void applyAWBLock(CaptureRequest.Builder builder, boolean z) {
        if (builder != null) {
            String str = TAG;
            Log.d(str, "applyAWBLock: " + z);
            builder.set(CaptureRequest.CONTROL_AWB_LOCK, Boolean.valueOf(z));
        }
    }

    static void applyAWBMode(CaptureRequest.Builder builder, int i) {
        if (builder != null) {
            builder.set(CaptureRequest.CONTROL_AWB_MODE, Integer.valueOf(i));
        }
    }

    static void applyAiSceneDetectEnable(CaptureRequest.Builder builder, CameraConfigs cameraConfigs) {
        if (builder != null) {
            MiCameraCompat.applyASDEnable(builder, cameraConfigs.isAiSceneDetectEnabled());
        }
    }

    static void applyAiSceneDetectPeriod(CaptureRequest.Builder builder, CameraConfigs cameraConfigs) {
        if (builder != null) {
            MiCameraCompat.applyAiScenePeriod(builder, cameraConfigs.getAiSceneDetectPeriod());
        }
    }

    static void applyAntiBanding(CaptureRequest.Builder builder, CameraConfigs cameraConfigs) {
        if (builder != null) {
            builder.set(CaptureRequest.CONTROL_AE_ANTIBANDING_MODE, Integer.valueOf(cameraConfigs.getAntiBanding()));
        }
    }

    static void applyAntiShake(CaptureRequest.Builder builder, CameraCapabilities cameraCapabilities, CameraConfigs cameraConfigs) {
        if (builder != null) {
            boolean isEISEnabled = cameraConfigs.isEISEnabled();
            boolean isOISEnabled = cameraConfigs.isOISEnabled();
            if (!isEISEnabled || !isOISEnabled || !Util.isDebugOsBuild()) {
                String str = TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("EIS: ");
                String str2 = "on";
                sb.append(isEISEnabled ? str2 : "off");
                Log.v(str, sb.toString());
                builder.set(CaptureRequest.CONTROL_VIDEO_STABILIZATION_MODE, Integer.valueOf(isEISEnabled ? 1 : 0));
                if (cameraCapabilities.isSupportOIS()) {
                    String str3 = TAG;
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("OIS: ");
                    if (isEISEnabled || !isOISEnabled) {
                        str2 = "off";
                    }
                    sb2.append(str2);
                    Log.v(str3, sb2.toString());
                    builder.set(CaptureRequest.LENS_OPTICAL_STABILIZATION_MODE, Integer.valueOf((isEISEnabled || !isOISEnabled) ? 0 : 1));
                    return;
                }
                return;
            }
            throw new RuntimeException("EIS&OIS are both on");
        }
    }

    static void applyAutoZoomMode(CaptureRequest.Builder builder, CameraCapabilities cameraCapabilities, CameraConfigs cameraConfigs) {
        if (builder != null) {
            MiCameraCompat.applyAutoZoomMode(builder, cameraConfigs.getAutoZoomMode());
        }
    }

    static void applyAutoZoomScaleOffset(CaptureRequest.Builder builder, CameraCapabilities cameraCapabilities, CameraConfigs cameraConfigs) {
        if (builder != null) {
            MiCameraCompat.applyAutoZoomScaleOffset(builder, cameraConfigs.getAutoZoomScaleOffset());
        }
    }

    public static void applyBackSoftLight(CameraCapabilities cameraCapabilities, CaptureRequest.Builder builder, boolean z) {
        if (builder != null) {
            if (!cameraCapabilities.isBackSoftLightSupported()) {
                Log.d(TAG, "applyBackSoftLight(): unsupported");
                return;
            }
            String str = TAG;
            Log.d(str, "applyBackSoftLight(): " + z);
            MiCameraCompat.applyBackSoftLight(builder, z ? (byte) 1 : 0);
        }
    }

    public static void applyBackwardCaptureHint(CameraCapabilities cameraCapabilities, CaptureRequest.Builder builder, boolean z) {
        if (builder != null) {
            if (!cameraCapabilities.isBackwardCaptureSupported()) {
                Log.d(TAG, "applyBackwardCaptureHint(): unsupported");
                return;
            }
            String str = TAG;
            Log.d(str, "applyBackwardCaptureHint(): " + z);
            MiCameraCompat.applyBackwardCaptureHint(builder, z ? (byte) 1 : 0);
        }
    }

    static void applyBeautyValues(CaptureRequest.Builder builder, CameraCapabilities cameraCapabilities, CameraConfigs cameraConfigs) {
        if (builder != null && cameraCapabilities.isSupportBeauty() && cameraConfigs.getBeautyValues() != null) {
            MiCameraCompat.applyBeautyParameter(builder, cameraCapabilities.getCaptureRequestVendorKeys(), cameraConfigs.getBeautyValues());
        }
    }

    static void applyCameraAi30Enable(CaptureRequest.Builder builder, CameraCapabilities cameraCapabilities, CameraConfigs cameraConfigs) {
        if (builder != null && cameraCapabilities.isSupportCameraAi30()) {
            MiCameraCompat.applyCameraAi30Enable(builder, cameraConfigs.isCameraAi30Enabled());
        }
    }

    static void applyColorEffect(CaptureRequest.Builder builder, CameraConfigs cameraConfigs) {
        if (builder != null) {
            builder.set(CaptureRequest.CONTROL_EFFECT_MODE, Integer.valueOf(cameraConfigs.getColorEffect()));
        }
    }

    static void applyContrast(CaptureRequest.Builder builder, CameraCapabilities cameraCapabilities, CameraConfigs cameraConfigs) {
        if (builder != null && cameraCapabilities.isSupportContrast()) {
            MiCameraCompat.applyContrast(builder, cameraConfigs.getContrastLevel());
        }
    }

    static void applyCustomAWB(CaptureRequest.Builder builder, int i) {
        MiCameraCompat.applyCustomAWB(builder, i);
    }

    static void applyDepurpleEnable(CaptureRequest.Builder builder, CameraCapabilities cameraCapabilities, boolean z) {
        if (builder != null) {
            String str = TAG;
            Log.d(str, "applyDepurpleEnable: isSupport = " + cameraCapabilities.isSupportDepurple() + ", enadled = " + z);
            if (cameraCapabilities.isSupportDepurple()) {
                MiCameraCompat.applyDepurpleEnable(builder, z);
            }
        }
    }

    static void applyDeviceOrientation(CaptureRequest.Builder builder, CameraCapabilities cameraCapabilities, CameraConfigs cameraConfigs) {
        if (builder != null && cameraCapabilities.isSupportDeviceOrientation()) {
            String str = TAG;
            Log.d(str, "applyDeviceOrientation: " + cameraConfigs.getDeviceOrientation());
            MiCameraCompat.applyDeviceOrientation(builder, cameraConfigs.getDeviceOrientation());
        }
    }

    static void applyExposureCompensation(CaptureRequest.Builder builder, int i, CameraCapabilities cameraCapabilities, CameraConfigs cameraConfigs) {
        if (builder != null) {
            int exposureCompensationIndex = cameraConfigs.getExposureCompensationIndex();
            if (b.Ij() && ModuleManager.isManualModule() && i == 1 && cameraConfigs.getISO() == 0 && cameraConfigs.getExposureTime() > MAX_REALTIME_EXPOSURE_TIME) {
                double log = Math.log((double) ((float) (((double) cameraConfigs.getExposureTime()) / 1.25E8d))) / Math.log(2.0d);
                String str = TAG;
                Log.d(str, "applyExposureCompensation: EV = " + log);
                Rational exposureCompensationRational = cameraCapabilities.getExposureCompensationRational();
                exposureCompensationIndex = Math.min((int) ((log * ((double) exposureCompensationRational.getDenominator())) / ((double) exposureCompensationRational.getNumerator())), cameraCapabilities.getMaxExposureCompensation());
            }
            String str2 = TAG;
            Log.d(str2, "applyExposureCompensation: " + exposureCompensationIndex);
            builder.set(CaptureRequest.CONTROL_AE_EXPOSURE_COMPENSATION, Integer.valueOf(exposureCompensationIndex));
        }
    }

    static void applyExposureMeteringMode(CaptureRequest.Builder builder, CameraConfigs cameraConfigs) {
        if (builder != null) {
            MiCameraCompat.applyExposureMeteringMode(builder, cameraConfigs.getExposureMeteringMode());
        }
    }

    static void applyExposureTime(CaptureRequest.Builder builder, int i, CameraConfigs cameraConfigs) {
        if (builder != null) {
            long exposureTime = cameraConfigs.getExposureTime();
            if (b.Ij() && i == 1) {
                exposureTime = Math.min(exposureTime, MAX_REALTIME_EXPOSURE_TIME);
            }
            if (b.Ij() || i == 3) {
                String str = TAG;
                Log.d(str, "applyExposureTime: " + exposureTime);
                MiCameraCompat.applyExposureTime(builder, exposureTime);
            }
        }
    }

    static void applyEyeLight(CaptureRequest.Builder builder, CameraCapabilities cameraCapabilities, CameraConfigs cameraConfigs) {
        if (builder != null && cameraCapabilities.isSupportEyeLight()) {
            int eyeLightType = cameraConfigs.getEyeLightType();
            if (eyeLightType < 0) {
                MiCameraCompat.applyEyeLight(builder, 0, 0);
            } else {
                MiCameraCompat.applyEyeLight(builder, eyeLightType, 100);
            }
        }
    }

    static void applyFNumber(CaptureRequest.Builder builder, CameraCapabilities cameraCapabilities, CameraConfigs cameraConfigs) {
        if (builder != null) {
            if (!cameraCapabilities.isSupportBokehAdjust()) {
                Log.d(TAG, "set f number on unsupported devices");
            } else if (cameraConfigs.getFNumber() != null) {
                MiCameraCompat.applyFNumber(builder, cameraConfigs.getFNumber());
            }
        }
    }

    static void applyFaceAgeAnalyze(CaptureRequest.Builder builder, CameraCapabilities cameraCapabilities, CameraConfigs cameraConfigs) {
        if (builder != null && cameraCapabilities.isSupportFaceAgeAnalyze()) {
            MiCameraCompat.applyFaceAgeAnalyzeEnable(builder, cameraConfigs.isFaceAgeAnalyzeEnabled());
        }
    }

    static void applyFaceDetection(CaptureRequest.Builder builder, CameraConfigs cameraConfigs) {
        if (builder != null) {
            MiCameraCompat.applyFaceDetection(builder, cameraConfigs.isFaceDetectionEnabled());
        }
    }

    static void applyFaceScore(CaptureRequest.Builder builder, CameraCapabilities cameraCapabilities, CameraConfigs cameraConfigs) {
        if (builder != null && cameraCapabilities.isSupportFaceScore()) {
            MiCameraCompat.applyFaceScoreEnable(builder, cameraConfigs.isFaceScoreEnabled());
        }
    }

    public static void applyFlawDetectEnable(CameraCapabilities cameraCapabilities, CaptureRequest.Builder builder, boolean z) {
        if (builder != null && cameraCapabilities != null && cameraCapabilities.isTagDefined(CaptureRequestVendorTags.FLAW_DETECT_ENABLE.getName())) {
            MiCameraCompat.applyFlawDetectEnable(builder, z);
        }
    }

    static void applyFocusDistance(CaptureRequest.Builder builder, CameraConfigs cameraConfigs) {
        if (builder != null && cameraConfigs.getFocusMode() == 0) {
            builder.set(CaptureRequest.LENS_FOCUS_DISTANCE, Float.valueOf(cameraConfigs.getFocusDistance()));
        }
    }

    static void applyFocusMode(CaptureRequest.Builder builder, CameraConfigs cameraConfigs) {
        if (builder != null) {
            builder.set(CaptureRequest.CONTROL_AF_MODE, Integer.valueOf(cameraConfigs.getFocusMode()));
            applyAFRegions(builder, cameraConfigs);
            applyAERegions(builder, cameraConfigs);
        }
    }

    static void applyFpsRange(CaptureRequest.Builder builder, CameraConfigs cameraConfigs) {
        if (builder != null) {
            Range<Integer> previewFpsRange = cameraConfigs.getPreviewFpsRange();
            String str = TAG;
            Log.d(str, "applyFpsRange: fpsRange = " + previewFpsRange);
            if (previewFpsRange != null) {
                builder.set(CaptureRequest.CONTROL_AE_TARGET_FPS_RANGE, previewFpsRange);
            }
        }
    }

    static void applyFrontMirror(CaptureRequest.Builder builder, int i, CameraCapabilities cameraCapabilities, CameraConfigs cameraConfigs) {
        if (builder != null && cameraCapabilities.isSupportFrontMirror() && i == 3) {
            String str = TAG;
            Log.d(str, "applyFrontMirror: " + cameraConfigs.isFrontMirror());
            MiCameraCompat.applyFrontMirror(builder, cameraConfigs.isFrontMirror());
        }
    }

    static void applyHDR(CaptureRequest.Builder builder, int i, CameraCapabilities cameraCapabilities, CameraConfigs cameraConfigs) {
        if (builder == null || !cameraCapabilities.isSupportHdr()) {
            return;
        }
        if (i != 3) {
            MiCameraCompat.applyHDR(builder, false);
        } else {
            MiCameraCompat.applyHDR(builder, cameraConfigs.isHDREnabled());
        }
    }

    static void applyHDRCheckerEnable(CaptureRequest.Builder builder, int i, CameraCapabilities cameraCapabilities, CameraConfigs cameraConfigs) {
        if (builder != null && i == 1 && cameraCapabilities.isSupportAutoHdr()) {
            MiCameraCompat.applyHDRCheckerEnable(builder, cameraConfigs.isHDRCheckerEnabled());
        }
    }

    public static void applyHFRDeflicker(CaptureRequest.Builder builder, int i, CameraCapabilities cameraCapabilities, CameraConfigs cameraConfigs) {
        if (builder != null && cameraConfigs != null && cameraCapabilities.isSupportHFRDeflicker()) {
            boolean isHFRDeflicker = cameraConfigs.isHFRDeflicker();
            String str = TAG;
            Log.d(str, "applyHFRDeflicker: " + isHFRDeflicker);
            MiCameraCompat.applyHFRDeflicker(builder, isHFRDeflicker);
        }
    }

    static void applyHHT(CaptureRequest.Builder builder, int i, CameraCapabilities cameraCapabilities, CameraConfigs cameraConfigs) {
        if (builder != null && i == 3 && cameraCapabilities.isSupportHHT()) {
            MiCameraCompat.applyHHT(builder, cameraConfigs.isHHTEnabled());
        }
    }

    static void applyHwMfnr(CaptureRequest.Builder builder, int i, CameraCapabilities cameraCapabilities, CameraConfigs cameraConfigs) {
        if (builder == null || !cameraCapabilities.isSupportMfnr()) {
            return;
        }
        if (i != 3) {
            MiCameraCompat.applyMfnrEnable(builder, false);
            return;
        }
        String str = TAG;
        Log.d(str, "applyMfnrEnable: " + cameraConfigs.isMfnrEnabled());
        MiCameraCompat.applyMfnrEnable(builder, cameraConfigs.isMfnrEnabled());
    }

    static void applyIso(CaptureRequest.Builder builder, int i, CameraCapabilities cameraCapabilities, CameraConfigs cameraConfigs) {
        if (builder != null) {
            int iso = cameraConfigs.getISO();
            if (b.Ij() && i == 1 && iso > 0 && cameraConfigs.getExposureTime() > MAX_REALTIME_EXPOSURE_TIME) {
                iso = Math.min((int) (((float) iso) * ((float) (((double) cameraConfigs.getExposureTime()) / 1.25E8d))), cameraCapabilities.getMaxIso());
            }
            String str = TAG;
            Log.d(str, "applyIso: " + iso);
            MiCameraCompat.applyISO(builder, iso);
        }
    }

    static void applyLensDirtyDetect(CaptureRequest.Builder builder, CameraCapabilities cameraCapabilities, CameraConfigs cameraConfigs) {
        if (builder != null && cameraCapabilities.isSupportLensDirtyDetect()) {
            MiCameraCompat.applyLensDirtyDetect(builder, cameraConfigs.isLensDirtyDetectEnabled());
        }
    }

    public static void applyMacroMode(CaptureRequest.Builder builder, int i, CameraCapabilities cameraCapabilities, CameraConfigs cameraConfigs) {
        if (builder != null && cameraConfigs != null && cameraCapabilities.isSupportMacroMode()) {
            boolean isMacroMode = cameraConfigs.isMacroMode();
            String str = TAG;
            Log.d(str, "applyMacroMode: " + isMacroMode);
            MiCameraCompat.applyMacroMode(builder, isMacroMode);
        }
    }

    static void applyMiBokeh(CaptureRequest.Builder builder, CameraCapabilities cameraCapabilities, CameraConfigs cameraConfigs) {
        if (builder != null && cameraCapabilities.isSupportMiBokeh()) {
            String str = TAG;
            Log.d(str, "applyMiBokeh: " + cameraConfigs.isMiBokehEnabled());
            MiCameraCompat.applyMiBokehEnable(builder, cameraConfigs.isMiBokehEnabled());
        }
    }

    private void applyNoiseReduction(CaptureRequest.Builder builder) {
        if (builder != null) {
            builder.set(CaptureRequest.NOISE_REDUCTION_MODE, 2);
        }
    }

    static void applyNormalWideLDC(CaptureRequest.Builder builder, int i, CameraCapabilities cameraCapabilities, CameraConfigs cameraConfigs) {
        if (builder != null && cameraCapabilities.isSupportNormalWideLDC()) {
            boolean normalWideLDCEnabled = cameraConfigs.getNormalWideLDCEnabled();
            if (i == 4) {
                normalWideLDCEnabled = false;
            }
            MiCameraCompat.applyNormalWideLDC(builder, normalWideLDCEnabled);
        }
    }

    public static void applyOnTripodModeStatus(CaptureRequest.Builder builder, CameraConfigs cameraConfigs) {
        if (builder != null && cameraConfigs != null && cameraConfigs.getOnTripodScenes() != null) {
            MiCameraCompat.applyOnTripodModeStatus(builder, cameraConfigs.getOnTripodScenes());
        }
    }

    static void applyPortraitLighting(CaptureRequest.Builder builder, int i, CameraCapabilities cameraCapabilities, CameraConfigs cameraConfigs) {
        if (builder != null) {
            if ((i == 3 || DataRepository.dataItemFeature().wa()) && cameraCapabilities.isSupportPortraitLighting()) {
                MiCameraCompat.applyPortraitLighting(builder, cameraConfigs.getPortraitLightingPattern());
            }
        }
    }

    static void applyRearBokeh(CaptureRequest.Builder builder, CameraCapabilities cameraCapabilities, CameraConfigs cameraConfigs) {
        if (builder != null && cameraCapabilities.isSupportRearBokeh()) {
            String str = TAG;
            Log.d(str, "applyRearBokeh: " + cameraConfigs.isRearBokehEnabled());
            MiCameraCompat.applyRearBokehEnable(builder, cameraConfigs.isRearBokehEnabled());
        }
    }

    public static void applySatIsZooming(CaptureRequest.Builder builder, CameraConfigs cameraConfigs) {
        if (builder != null && cameraConfigs != null) {
            MiCameraCompat.applySatIsZooming(builder, cameraConfigs.isSatIsZooming());
        }
    }

    static void applySaturation(CaptureRequest.Builder builder, CameraConfigs cameraConfigs) {
        if (builder != null) {
            MiCameraCompat.applySaturation(builder, cameraConfigs.getSaturationLevel());
        }
    }

    static void applySceneMode(CaptureRequest.Builder builder, CameraConfigs cameraConfigs) {
        if (builder != null) {
            int sceneMode = cameraConfigs.getSceneMode();
            if (!"-1".equals(String.valueOf(sceneMode))) {
                builder.set(CaptureRequest.CONTROL_SCENE_MODE, Integer.valueOf(sceneMode));
                builder.set(CaptureRequest.CONTROL_MODE, 2);
                return;
            }
            builder.set(CaptureRequest.CONTROL_MODE, 1);
        }
    }

    public static void applyScreenLightHint(CameraCapabilities cameraCapabilities, CaptureRequest.Builder builder, boolean z) {
        if (builder != null) {
            if (!cameraCapabilities.isScreenLightHintSupported()) {
                Log.d(TAG, "applyScreenLightHint(): unsupported");
                return;
            }
            String str = TAG;
            Log.d(str, "applyScreenLightHint(): " + z);
            MiCameraCompat.applyScreenLightHint(builder, z ? (byte) 1 : 0);
        }
    }

    public static void applySessionParameters(CaptureRequest.Builder builder, CaptureSessionConfigurations captureSessionConfigurations) {
        if (builder != null && captureSessionConfigurations != null) {
            captureSessionConfigurations.apply(builder);
        }
    }

    static void applySharpness(CaptureRequest.Builder builder, CameraConfigs cameraConfigs) {
        if (builder != null) {
            MiCameraCompat.applySharpness(builder, cameraConfigs.getSharpnessLevel());
        }
    }

    public static void applySmoothTransition(CameraCapabilities cameraCapabilities, CaptureRequest.Builder builder, boolean z) {
        if (builder != null && cameraCapabilities.isTagDefined(CaptureRequestVendorTags.ST_ENABLED.getName())) {
            MiCameraCompat.applySmoothTransition(builder, z);
        }
    }

    static void applySuperNightScene(CaptureRequest.Builder builder, int i, CameraCapabilities cameraCapabilities, CameraConfigs cameraConfigs) {
        if (builder != null && i == 3 && cameraCapabilities.isSupportSuperNight()) {
            String str = TAG;
            Log.d(str, "applySuperNightScene: " + cameraConfigs.isSuperNightEnabled());
            MiCameraCompat.applySuperNightScene(builder, cameraConfigs.isSuperNightEnabled());
        }
    }

    static void applySuperResolution(CaptureRequest.Builder builder, int i, CameraCapabilities cameraCapabilities, CameraConfigs cameraConfigs, boolean z) {
        if (builder != null && cameraCapabilities.isSupportSuperResolution()) {
            boolean isSuperResolutionEnabled = cameraConfigs.isSuperResolutionEnabled();
            if (!b.isMTKPlatform()) {
                if (!z || ModuleManager.isManualModule()) {
                    isSuperResolutionEnabled &= i == 3;
                }
                String str = TAG;
                Log.d(str, "applySuperResolution: " + isSuperResolutionEnabled + ", applyType = " + i);
                MiCameraCompat.applySuperResolution(builder, isSuperResolutionEnabled);
            } else if (i != 3) {
                String str2 = TAG;
                Log.d(str2, "applySuperResolution: ignored for applyType(" + i + ")");
            } else {
                String str3 = TAG;
                Log.d(str3, "applySuperResolution: " + isSuperResolutionEnabled + ", applyType = " + i);
                MiCameraCompat.applySuperResolution(builder, isSuperResolutionEnabled);
            }
        }
    }

    static void applySwMfnr(CaptureRequest.Builder builder, int i, CameraCapabilities cameraCapabilities, CameraConfigs cameraConfigs) {
        if (builder == null || !cameraCapabilities.isSupportSwMfnr()) {
            return;
        }
        if (i != 3) {
            MiCameraCompat.applySwMfnrEnable(builder, false);
            return;
        }
        String str = TAG;
        Log.d(str, "applySwMfnrEnable: " + cameraConfigs.isSwMfnrEnabled());
        MiCameraCompat.applySwMfnrEnable(builder, cameraConfigs.isSwMfnrEnabled());
    }

    public static void applyUltraPixelPortrait(CaptureRequest.Builder builder, int i, CameraCapabilities cameraCapabilities, CameraConfigs cameraConfigs) {
        if (builder != null && cameraConfigs != null && cameraCapabilities.isSupportUltraPixelPortrait()) {
            boolean isUltraPixelPortraitEnabled = cameraConfigs.isUltraPixelPortraitEnabled();
            String str = TAG;
            Log.d(str, "applyUltraPixelPortrait: " + isUltraPixelPortraitEnabled);
            MiCameraCompat.applyUltraPixelPortrait(builder, isUltraPixelPortraitEnabled);
        }
    }

    static void applyUltraWideLDC(CaptureRequest.Builder builder, CameraCapabilities cameraCapabilities, CameraConfigs cameraConfigs) {
        if (builder != null && cameraCapabilities.isSupportUltraWideLDC()) {
            MiCameraCompat.applyUltraWideLDC(builder, cameraConfigs.isUltraWideLDCEnabled());
        }
    }

    static void applyVideoFlash(CaptureRequest.Builder builder, CameraConfigs cameraConfigs) {
        if (builder != null) {
            if (2 == cameraConfigs.getFlashMode()) {
                builder.set(CaptureRequest.FLASH_MODE, 2);
            } else {
                builder.set(CaptureRequest.FLASH_MODE, 0);
            }
        }
    }

    static void applyVideoFpsRange(CaptureRequest.Builder builder, CameraConfigs cameraConfigs) {
        if (builder != null) {
            Range<Integer> videoFpsRange = cameraConfigs.getVideoFpsRange();
            String str = TAG;
            Log.d(str, "applyVideoFpsRange: fpsRange = " + videoFpsRange);
            if (videoFpsRange != null) {
                builder.set(CaptureRequest.CONTROL_AE_TARGET_FPS_RANGE, videoFpsRange);
            }
        }
    }

    static void applyWaterMark(CaptureRequest.Builder builder, int i, CameraCapabilities cameraCapabilities, CameraConfigs cameraConfigs) {
        if (builder == null || !cameraCapabilities.isSupportWatermark()) {
            return;
        }
        if (i != 3) {
            if (i == 4) {
                MiCameraCompat.applyWaterMarkAppliedList(builder, "");
            }
        } else if (!DataRepository.dataItemFeature().Uc()) {
            String join = Util.join(",", cameraConfigs.getWaterMarkAppliedList());
            String str = TAG;
            Log.d(str, "applyWaterMark appliedList:" + join);
            MiCameraCompat.applyWaterMarkAppliedList(builder, join);
            if (DataRepository.dataItemFeature().hd() && cameraCapabilities.isSupportCustomWatermark() && join.contains("device")) {
                String watermarkFileName = Util.getWatermarkFileName();
                MiCameraCompat.applyCustomWaterMark(builder, Util.WATERMARK_STORAGE_DIRECTORY + watermarkFileName);
            }
            if (join.contains("watermark")) {
                MiCameraCompat.applyTimeWaterMark(builder, cameraConfigs.getTimeWaterMarkValue());
            }
            if (join.contains("beautify")) {
                MiCameraCompat.applyFaceWaterMark(builder, cameraConfigs.getFaceWaterMarkFormat());
            }
        }
    }

    static void applyZoomRatio(CaptureRequest.Builder builder, CameraCapabilities cameraCapabilities, CameraConfigs cameraConfigs) {
        if (builder != null) {
            float zoomRatio = cameraConfigs != null ? cameraConfigs.getZoomRatio() : 1.0f;
            Rect activeArraySize = cameraCapabilities.getActiveArraySize();
            Rect cropRegion = HybridZoomingSystem.toCropRegion(zoomRatio, activeArraySize);
            builder.set(CaptureRequest.SCALER_CROP_REGION, cropRegion);
            String str = TAG;
            Log.v(str, "applyZoomRatio(): cameraId = " + cameraCapabilities.getCameraId() + ", zoomRatio = " + zoomRatio + ", activeArraySize = " + activeArraySize + ", cropRegion = " + cropRegion);
        }
    }

    static void applyZsl(CaptureRequest.Builder builder, CameraConfigs cameraConfigs) {
        if (builder != null) {
            boolean isZslEnabled = cameraConfigs.isZslEnabled();
            String str = TAG;
            Log.v(str, "applyZsl(): " + isZslEnabled);
            MiCameraCompat.applyZsl(builder, isZslEnabled);
        }
    }
}
