package com.android.camera.fragment;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.RectF;
import android.hardware.camera2.params.MeteringRectangle;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.MiuiSettings.ScreenEffect;
import android.provider.MiuiSettings.System;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.text.SpannableStringBuilder;
import android.text.style.TextAppearanceSpan;
import android.util.Size;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.camera.R;
import com.android.camera.Util;
import com.android.camera.animation.type.AlphaInOnSubscribe;
import com.android.camera.animation.type.AlphaOutOnSubscribe;
import com.android.camera.animation.type.SlideInOnSubscribe;
import com.android.camera.animation.type.SlideOutOnSubscribe;
import com.android.camera.data.DataRepository;
import com.android.camera.fragment.mimoji.MimojiHelper;
import com.android.camera.log.Log;
import com.android.camera.protocol.ModeCoordinatorImpl;
import com.android.camera.protocol.ModeProtocol.AutoZoomModuleProtocol;
import com.android.camera.protocol.ModeProtocol.AutoZoomViewProtocol;
import com.android.camera.protocol.ModeProtocol.BottomPopupTips;
import com.android.camera.protocol.ModeProtocol.HandleBackTrace;
import com.android.camera.protocol.ModeProtocol.MainContentProtocol;
import com.android.camera.protocol.ModeProtocol.MimojiAvatarEngine;
import com.android.camera.protocol.ModeProtocol.ModeCoordinator;
import com.android.camera.protocol.ModeProtocol.SnapShotIndicator;
import com.android.camera.protocol.ModeProtocol.TopAlert;
import com.android.camera.protocol.ModeProtocol.VerticalProtocol;
import com.android.camera.ui.AfRegionsView;
import com.android.camera.ui.FaceView;
import com.android.camera.ui.FocusIndicator;
import com.android.camera.ui.FocusView;
import com.android.camera.ui.FocusView.ExposureViewListener;
import com.android.camera.ui.LightingView;
import com.android.camera.ui.ObjectView;
import com.android.camera.ui.ObjectView.ObjectViewListener;
import com.android.camera.ui.V6EffectCropView;
import com.android.camera.ui.V6PreviewFrame;
import com.android.camera.ui.V6PreviewPanel;
import com.android.camera.watermark.WaterMarkData;
import com.android.camera2.CameraHardwareFace;
import com.android.camera2.autozoom.AutoZoomCaptureResult;
import com.android.camera2.autozoom.AutoZoomView;
import com.bumptech.glide.c;
import com.mi.config.b;
import io.reactivex.Completable;
import java.util.List;
import miui.view.animation.QuadraticEaseInOutInterpolator;

public class FragmentMainContent extends BaseFragment implements AutoZoomViewProtocol, HandleBackTrace, MainContentProtocol, SnapShotIndicator {
    public static final int FRAGMENT_INFO = 243;
    public static final int FRONT_CAMERA_ID = 1;
    private static final String TAG = "FragmentMainContent";
    private long lastConfirmTime;
    /* access modifiers changed from: private */
    public int lastFaceResult;
    /* access modifiers changed from: private */
    public boolean lastFaceSuccess;
    private int mActiveIndicator = 2;
    private AfRegionsView mAfRegionsView;
    private AutoZoomView mAutoZoomOverlay;
    private View mBottomCover;
    private TextView mCaptureDelayNumber;
    private ImageView mCenterHintIcon;
    private TextView mCenterHintText;
    private ViewGroup mCoverParent;
    private int mDisplayRectTopMargin;
    private V6EffectCropView mEffectCropView;
    private FaceView mFaceView;
    private FocusView mFocusView;
    private Handler mHandler = new Handler();
    private boolean mIsIntentAction;
    private int mLastCameraId = -1;
    private LightingView mLightingView;
    /* access modifiers changed from: private */
    public LightingView mMimojiLightingView;
    private TextView mMultiSnapNum;
    private ObjectView mObjectView;
    /* access modifiers changed from: private */
    public ViewGroup mPreviewCenterHint;
    private V6PreviewFrame mPreviewFrame;
    private ViewGroup mPreviewPage;
    private V6PreviewPanel mPreviewPanel;
    private TextAppearanceSpan mSnapStyle;
    private SpannableStringBuilder mStringBuilder;
    private View mTopCover;
    private AnimatorSet mZoomInAnimator;
    private AnimatorSet mZoomOutAnimator;
    private RectF mergeRectF = new RectF();

    private void adjustViewHeight() {
        if (getContext() != null && this.mPreviewPanel != null) {
            ViewGroup viewGroup = (ViewGroup) this.mPreviewPanel.getParent();
            MarginLayoutParams marginLayoutParams = (MarginLayoutParams) viewGroup.getLayoutParams();
            MarginLayoutParams marginLayoutParams2 = (MarginLayoutParams) this.mPreviewPanel.getLayoutParams();
            MarginLayoutParams marginLayoutParams3 = (MarginLayoutParams) this.mPreviewCenterHint.getLayoutParams();
            Rect previewRect = Util.getPreviewRect(getContext());
            if (!(marginLayoutParams2.height == previewRect.height() && previewRect.top == this.mDisplayRectTopMargin)) {
                this.mDisplayRectTopMargin = previewRect.top;
                marginLayoutParams2.height = previewRect.height();
                marginLayoutParams2.topMargin = previewRect.top;
                this.mPreviewPanel.setLayoutParams(marginLayoutParams2);
                marginLayoutParams3.height = (previewRect.width() * 4) / 3;
                this.mPreviewCenterHint.setLayoutParams(marginLayoutParams3);
                marginLayoutParams.height = previewRect.height() + this.mDisplayRectTopMargin;
                viewGroup.setLayoutParams(marginLayoutParams);
                setDisplaySize(previewRect.width(), previewRect.height());
            }
        }
    }

    private void consumeResult(final int i, final boolean z) {
        if (System.currentTimeMillis() - this.lastConfirmTime >= 1000) {
            this.lastConfirmTime = System.currentTimeMillis();
            StringBuilder sb = new StringBuilder();
            sb.append(i);
            sb.append("");
            Log.d("faceResult:", sb.toString());
            if (this.lastFaceResult != i) {
                this.lastFaceResult = i;
                final LightingView lightingView = z ? this.mMimojiLightingView : this.mLightingView;
                lightingView.post(new Runnable() {
                    public void run() {
                        if (z) {
                            int tipsResIdFace = MimojiHelper.getTipsResIdFace(i);
                            BottomPopupTips bottomPopupTips = (BottomPopupTips) ModeCoordinatorImpl.getInstance().getAttachProtocol(175);
                            if (bottomPopupTips != null && tipsResIdFace > 0) {
                                bottomPopupTips.showTips(19, tipsResIdFace, 1);
                            }
                            return;
                        }
                        TopAlert topAlert = (TopAlert) ModeCoordinatorImpl.getInstance().getAttachProtocol(172);
                        if (topAlert != null) {
                            topAlert.alertLightingHint(FragmentMainContent.this.lastFaceResult);
                        }
                        VerticalProtocol verticalProtocol = (VerticalProtocol) ModeCoordinatorImpl.getInstance().getAttachProtocol(198);
                        if (verticalProtocol != null) {
                            verticalProtocol.alertLightingHint(FragmentMainContent.this.lastFaceResult);
                        }
                    }
                });
                boolean z2 = i == 6;
                if (this.lastFaceSuccess != z2) {
                    this.lastFaceSuccess = z2;
                    lightingView.post(new Runnable() {
                        public void run() {
                            MimojiAvatarEngine mimojiAvatarEngine = (MimojiAvatarEngine) ModeCoordinatorImpl.getInstance().getAttachProtocol(217);
                            if (mimojiAvatarEngine != null) {
                                mimojiAvatarEngine.setDetectSuccess(FragmentMainContent.this.lastFaceSuccess);
                                if (FragmentMainContent.this.lastFaceSuccess) {
                                    BottomPopupTips bottomPopupTips = (BottomPopupTips) ModeCoordinatorImpl.getInstance().getAttachProtocol(175);
                                    if (bottomPopupTips != null) {
                                        bottomPopupTips.showTips(19, R.string.mimoji_check_normal, 2);
                                    }
                                }
                            }
                            if (FragmentMainContent.this.lastFaceSuccess) {
                                lightingView.triggerAnimateSuccess();
                            } else {
                                lightingView.triggerAnimateFocusing();
                            }
                        }
                    });
                }
            }
        }
    }

    private RectF getMergeRect(RectF rectF, RectF rectF2) {
        float max = Math.max(rectF.left, rectF2.left);
        float min = Math.min(rectF.right, rectF2.right);
        this.mergeRectF.set(max, Math.max(rectF.top, rectF2.top), min, Math.min(rectF.bottom, rectF2.bottom));
        return this.mergeRectF;
    }

    private void initSnapNumAnimator() {
        this.mZoomInAnimator = (AnimatorSet) AnimatorInflater.loadAnimator(getContext(), R.animator.zoom_button_zoom_in);
        this.mZoomInAnimator.setTarget(this.mMultiSnapNum);
        this.mZoomInAnimator.setInterpolator(new QuadraticEaseInOutInterpolator());
        this.mZoomOutAnimator = (AnimatorSet) AnimatorInflater.loadAnimator(getContext(), R.animator.zoom_button_zoom_out);
        this.mZoomOutAnimator.setTarget(this.mMultiSnapNum);
        this.mZoomOutAnimator.setInterpolator(new QuadraticEaseInOutInterpolator());
    }

    private boolean isRectIntersect(RectF rectF, RectF rectF2) {
        return rectF2.right >= rectF.left && rectF2.left <= rectF.right && rectF2.bottom >= rectF.top && rectF2.top <= rectF.bottom;
    }

    private void showIndicator(FocusIndicator focusIndicator, int i) {
        switch (i) {
            case 1:
                focusIndicator.showStart();
                return;
            case 2:
                focusIndicator.showSuccess();
                return;
            case 3:
                focusIndicator.showFail();
                return;
            default:
                return;
        }
    }

    public void clearFocusView(int i) {
        this.mFocusView.clear(i);
    }

    public void clearIndicator(int i) {
        switch (i) {
            case 1:
                this.mFaceView.clear();
                return;
            case 2:
                throw new RuntimeException("not allowed call in this method");
            case 3:
                this.mObjectView.clear();
                return;
            default:
                return;
        }
    }

    public void destroyEffectCropView() {
        this.mEffectCropView.onDestroy();
    }

    public void feedData(AutoZoomCaptureResult autoZoomCaptureResult) {
        this.mAutoZoomOverlay.feedData(autoZoomCaptureResult);
    }

    public int getActiveIndicator() {
        return this.mActiveIndicator;
    }

    public List<WaterMarkData> getFaceWaterMarkInfos() {
        return this.mFaceView.getFaceWaterMarkInfos();
    }

    public CameraHardwareFace[] getFaces() {
        return this.mFaceView.getFaces();
    }

    public RectF getFocusRect(int i) {
        if (i == 1) {
            return this.mFaceView.getFocusRect();
        }
        if (i == 3) {
            return this.mObjectView.getFocusRect();
        }
        String str = TAG;
        StringBuilder sb = new StringBuilder();
        sb.append(getFragmentTag());
        sb.append(": unexpected type ");
        sb.append(i);
        Log.w(str, sb.toString());
        return new RectF();
    }

    public RectF getFocusRectInPreviewFrame() {
        return this.mObjectView.getFocusRectInPreviewFrame();
    }

    public int getFragmentInto() {
        return 243;
    }

    /* access modifiers changed from: protected */
    public int getLayoutResourceId() {
        return R.layout.fragment_main_content;
    }

    public void hideDelayNumber() {
        if (this.mCaptureDelayNumber.getVisibility() != 8) {
            this.mCaptureDelayNumber.setVisibility(8);
        }
    }

    public void hideReviewViews() {
        if (this.mPreviewPanel.mVideoReviewImage.getVisibility() == 0) {
            Util.fadeOut(this.mPreviewPanel.mVideoReviewImage);
        }
        Util.fadeOut(this.mPreviewPanel.mVideoReviewPlay);
    }

    public void initEffectCropView() {
        this.mEffectCropView.onCreate();
    }

    /* access modifiers changed from: protected */
    public void initView(View view) {
        this.mCoverParent = (ViewGroup) view.findViewById(R.id.cover_parent);
        this.mMultiSnapNum = (TextView) this.mCoverParent.findViewById(R.id.v6_multi_snap_number);
        this.mCaptureDelayNumber = (TextView) this.mCoverParent.findViewById(R.id.v6_capture_delay_number);
        this.mTopCover = this.mCoverParent.findViewById(R.id.top_cover_layout);
        this.mBottomCover = this.mCoverParent.findViewById(R.id.bottom_cover_layout);
        this.mPreviewPage = (ViewGroup) view.findViewById(R.id.v6_preview_page);
        this.mPreviewPanel = (V6PreviewPanel) this.mPreviewPage.findViewById(R.id.v6_preview_panel);
        this.mPreviewFrame = (V6PreviewFrame) this.mPreviewPanel.findViewById(R.id.v6_frame_layout);
        this.mPreviewCenterHint = (ViewGroup) this.mPreviewPanel.findViewById(R.id.center_hint_placeholder);
        this.mCenterHintIcon = (ImageView) this.mPreviewCenterHint.findViewById(R.id.center_hint_icon);
        this.mCenterHintText = (TextView) this.mPreviewCenterHint.findViewById(R.id.center_hint_text);
        this.mEffectCropView = (V6EffectCropView) this.mPreviewPanel.findViewById(R.id.v6_effect_crop_view);
        this.mFaceView = (FaceView) this.mPreviewPanel.findViewById(R.id.v6_face_view);
        this.mFocusView = (FocusView) this.mPreviewPanel.findViewById(R.id.v6_focus_view);
        this.mAutoZoomOverlay = (AutoZoomView) this.mPreviewPanel.findViewById(R.id.autozoom_overlay);
        this.mLightingView = (LightingView) this.mPreviewPanel.findChildrenById(R.id.lighting_view);
        this.mObjectView = (ObjectView) this.mPreviewPanel.findViewById(R.id.object_view);
        this.mAfRegionsView = (AfRegionsView) this.mPreviewPanel.findViewById(R.id.afregions_view);
        this.mMimojiLightingView = (LightingView) this.mPreviewPanel.findChildrenById(R.id.mimoji_lighting_view);
        this.mMimojiLightingView.setCircleRatio(1.2f);
        this.mLightingView.setRotation(this.mDegree);
        adjustViewHeight();
        this.mCoverParent.getLayoutParams().height = Util.sWindowHeight - Util.getBottomHeight(getResources());
        this.mBottomCover.getLayoutParams().height = ((((int) (((float) Util.sWindowWidth) / 0.75f)) - Util.sWindowWidth) / 2) + getResources().getDimensionPixelSize(R.dimen.square_mode_bottom_cover_extra_margin);
        this.mTopCover.getLayoutParams().height = (this.mCoverParent.getLayoutParams().height - Util.sWindowWidth) - this.mBottomCover.getLayoutParams().height;
        this.mIsIntentAction = DataRepository.dataItemGlobal().isIntentAction();
        provideAnimateElement(this.mCurrentMode, null, 2);
    }

    public void initializeFocusView(ExposureViewListener exposureViewListener) {
        this.mFocusView.initialize(exposureViewListener);
    }

    public boolean initializeObjectTrack(RectF rectF, boolean z) {
        this.mFocusView.clear();
        this.mObjectView.clear();
        this.mObjectView.setVisibility(0);
        return this.mObjectView.initializeTrackView(rectF, z);
    }

    public boolean initializeObjectView(RectF rectF, boolean z) {
        return this.mObjectView.initializeTrackView(rectF, z);
    }

    public boolean isAdjustingObjectView() {
        return this.mObjectView.isAdjusting();
    }

    public boolean isAutoZoomActive() {
        return this.mAutoZoomOverlay.isViewActive();
    }

    public boolean isAutoZoomEnabled() {
        return this.mAutoZoomOverlay.isViewEnabled();
    }

    public boolean isAutoZoomViewEnabled() {
        return this.mAutoZoomOverlay.isViewEnabled();
    }

    public boolean isEffectViewMoved() {
        return this.mEffectCropView.isMoved();
    }

    public boolean isEffectViewVisible() {
        return this.mEffectCropView.isVisible();
    }

    public boolean isEvAdjusted(boolean z) {
        return z ? this.mFocusView.isEvAdjustedTime() : this.mFocusView.isEvAdjusted();
    }

    public boolean isFaceExists(int i) {
        if (i == 1) {
            return this.mFaceView.faceExists();
        }
        if (i != 3) {
            return false;
        }
        return this.mObjectView.faceExists();
    }

    public boolean isFaceStable(int i) {
        if (i == 1) {
            return this.mFaceView.isFaceStable();
        }
        if (i != 3) {
            return false;
        }
        return this.mObjectView.isFaceStable();
    }

    public boolean isFocusViewVisible() {
        return this.mFocusView.isVisible();
    }

    public boolean isIndicatorVisible(int i) {
        boolean z;
        boolean z2 = true;
        switch (i) {
            case 1:
                if (this.mFaceView.getVisibility() != 0) {
                    z = false;
                }
                return z;
            case 2:
                if (this.mFocusView.getVisibility() != 0) {
                    z2 = false;
                }
                return z2;
            case 3:
                if (this.mObjectView.getVisibility() != 0) {
                    z2 = false;
                }
                return z2;
            default:
                return false;
        }
    }

    public boolean isNeedExposure(int i) {
        if (i == 1) {
            return this.mFaceView.isNeedExposure();
        }
        if (i != 3) {
            return false;
        }
        return this.mObjectView.isNeedExposure();
    }

    public boolean isObjectTrackFailed() {
        return this.mObjectView.isTrackFailed();
    }

    public void lightingCancel() {
        this.mLightingView.triggerAnimateExit();
        this.lastConfirmTime = -1;
        this.mFaceView.setLightingOn(false);
        this.mAfRegionsView.setLightingOn(false);
    }

    public void lightingDetectFace(CameraHardwareFace[] cameraHardwareFaceArr, boolean z) {
        LightingView lightingView = z ? this.mMimojiLightingView : this.mLightingView;
        int i = 5;
        if (cameraHardwareFaceArr == null || cameraHardwareFaceArr.length == 0 || cameraHardwareFaceArr.length > 1) {
            consumeResult(5, z);
        } else if (this.lastConfirmTime != -1) {
            this.mFaceView.transToViewRect(cameraHardwareFaceArr[0].rect, lightingView.getFaceViewRectF());
            RectF faceViewRectF = lightingView.getFaceViewRectF();
            RectF focusRectF = lightingView.getFocusRectF();
            if (isRectIntersect(faceViewRectF, focusRectF)) {
                getMergeRect(faceViewRectF, focusRectF);
                float width = faceViewRectF.width() * faceViewRectF.height();
                float width2 = this.mergeRectF.width() * this.mergeRectF.height();
                float width3 = focusRectF.width() * focusRectF.height();
                float f = 1.0f;
                float f2 = z ? 0.5f : 1.0f;
                if (z) {
                    f = 1.5f;
                }
                float f3 = 0.2f * width3 * f2;
                float f4 = width3 * 0.5f * f;
                if (width2 >= 0.5f * width) {
                    i = width2 < f3 ? 4 : (width2 >= f4 || width >= f4) ? 3 : 6;
                }
            }
            consumeResult(i, z);
        }
    }

    public void lightingFocused() {
        this.mLightingView.triggerAnimateSuccess();
    }

    public void lightingStart() {
        this.mLightingView.triggerAnimateStart();
        this.lastFaceResult = -1;
        this.lastFaceSuccess = false;
        this.lastConfirmTime = System.currentTimeMillis();
        this.mFaceView.setLightingOn(true);
        this.mAfRegionsView.setLightingOn(true);
    }

    public void mimojiEnd() {
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                FragmentMainContent.this.mMimojiLightingView.triggerAnimateExit();
            }
        });
    }

    public void mimojiFaceDetect(final int i) {
        this.mMimojiLightingView.post(new Runnable() {
            public void run() {
                int tipsResId = MimojiHelper.getTipsResId(i);
                BottomPopupTips bottomPopupTips = (BottomPopupTips) ModeCoordinatorImpl.getInstance().getAttachProtocol(175);
                if (bottomPopupTips != null && tipsResId > 0) {
                    bottomPopupTips.showTips(19, tipsResId, 1);
                }
            }
        });
    }

    public void mimojiStart() {
        this.lastFaceResult = -1;
        this.lastFaceSuccess = false;
        this.lastConfirmTime = System.currentTimeMillis();
        this.mFaceView.setLightingOn(true);
        this.mAfRegionsView.setLightingOn(true);
        this.mMimojiLightingView.triggerAnimateStart();
    }

    public boolean needViewClear() {
        return true;
    }

    public void notifyAfterFrameAvailable(int i) {
        super.notifyAfterFrameAvailable(i);
        this.mPreviewFrame.updateReferenceLineAccordSquare();
        this.mPreviewFrame.updatePreviewGrid();
        this.mFocusView.reInit();
        this.mEffectCropView.updateVisible();
    }

    public void notifyDataChanged(int i, int i2) {
        super.notifyDataChanged(i, i2);
        boolean isIntentAction = DataRepository.dataItemGlobal().isIntentAction();
        if (isIntentAction != this.mIsIntentAction) {
            this.mIsIntentAction = isIntentAction;
            hideReviewViews();
        }
        if (DataRepository.dataItemGlobal().getCurrentCameraId() != this.mLastCameraId) {
            this.mLastCameraId = DataRepository.dataItemGlobal().getCurrentCameraId();
            if (Util.isAccessible()) {
                if (this.mLastCameraId != 1) {
                    this.mPreviewFrame.setContentDescription(getString(R.string.accessibility_back_preview_status));
                    this.mPreviewFrame.announceForAccessibility(getString(R.string.accessibility_back_preview_status));
                } else if (Util.isScreenSlideOff(getActivity())) {
                    this.mPreviewFrame.setContentDescription(getString(R.string.accessibility_pull_down_to_open_camera));
                    this.mPreviewFrame.announceForAccessibility(getString(R.string.accessibility_pull_down_to_open_camera));
                } else {
                    this.mPreviewFrame.setContentDescription(getString(R.string.accessibility_front_preview_status));
                    this.mPreviewFrame.announceForAccessibility(getString(R.string.accessibility_front_preview_status));
                }
            }
        }
        switch (i) {
            case 2:
                adjustViewHeight();
                return;
            case 3:
                adjustViewHeight();
                return;
            default:
                return;
        }
    }

    public void onAutoZoomStarted() {
        if (!this.mAutoZoomOverlay.isViewEnabled()) {
            this.mAutoZoomOverlay.setViewEnable(true);
            this.mAutoZoomOverlay.setViewActive(false);
            this.mAutoZoomOverlay.clear(0);
        }
    }

    public void onAutoZoomStopped() {
        if (this.mAutoZoomOverlay.isViewEnabled()) {
            this.mAutoZoomOverlay.setViewEnable(false);
            this.mAutoZoomOverlay.setViewActive(false);
            this.mAutoZoomOverlay.clear(4);
        }
    }

    public boolean onBackEvent(int i) {
        return false;
    }

    public void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
    }

    public void onDestroy() {
        super.onDestroy();
        destroyEffectCropView();
    }

    public boolean onEffectViewTouchEvent(MotionEvent motionEvent) {
        return this.mEffectCropView.onTouchEvent(motionEvent);
    }

    public void onPause() {
        super.onPause();
        this.mHandler.removeCallbacksAndMessages(null);
    }

    public void onStop() {
        super.onStop();
        this.mLightingView.clear();
    }

    public void onStopObjectTrack() {
        this.mObjectView.clear();
        this.mObjectView.setVisibility(8);
    }

    public void onTrackingStarted(RectF rectF) {
        AutoZoomModuleProtocol autoZoomModuleProtocol = (AutoZoomModuleProtocol) ModeCoordinatorImpl.getInstance().getAttachProtocol(215);
        if (autoZoomModuleProtocol != null) {
            autoZoomModuleProtocol.startTracking(rectF);
        }
    }

    public void onTrackingStopped(int i) {
        if (this.mAutoZoomOverlay.isViewActive()) {
            this.mAutoZoomOverlay.setViewActive(false);
            this.mAutoZoomOverlay.clear(0);
        }
    }

    public boolean onViewTouchEvent(int i, MotionEvent motionEvent) {
        if (i == this.mFocusView.getId()) {
            return this.mFocusView.onViewTouchEvent(motionEvent);
        }
        if (i == this.mEffectCropView.getId()) {
            return this.mEffectCropView.onViewTouchEvent(motionEvent);
        }
        if (i == this.mAutoZoomOverlay.getId()) {
            return this.mAutoZoomOverlay.onViewTouchEvent(motionEvent);
        }
        return false;
    }

    public void performHapticFeedback(int i) {
        this.mPreviewFrame.performHapticFeedback(i);
    }

    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void provideAnimateElement(int i, List<Completable> list, int i2) {
        int i3 = this.mCurrentMode;
        super.provideAnimateElement(i, list, i2);
        int i4 = i != 165 ? -1 : 1;
        boolean z = false;
        setSnapNumVisible(false, true);
        hideDelayNumber();
        this.mPreviewFrame.hidePreviewGrid();
        this.mFaceView.clear();
        this.mFaceView.clearFaceFlags();
        this.mFocusView.clear();
        this.mLightingView.clear();
        this.mAfRegionsView.clear();
        if (i2 == 3) {
            this.mMimojiLightingView.clear();
        }
        if (i3 != 162) {
            switch (i3) {
                case 168:
                case 169:
                    break;
            }
        }
        if (i != 162) {
            switch (i) {
                case 168:
                case 169:
                    break;
            }
            z = true;
        }
        if (z) {
            this.mFocusView.releaseListener();
        }
        if (this.mTopCover.getTag() == null || ((Integer) this.mTopCover.getTag()).intValue() != i4) {
            this.mTopCover.setTag(Integer.valueOf(i4));
            if (i4 == 1) {
                if (list == null) {
                    SlideInOnSubscribe.directSetResult(this.mTopCover, 48);
                    SlideInOnSubscribe.directSetResult(this.mBottomCover, 80);
                } else {
                    list.add(Completable.create(new SlideInOnSubscribe(this.mTopCover, 48)));
                    list.add(Completable.create(new SlideInOnSubscribe(this.mBottomCover, 80)));
                }
            } else if (list == null) {
                SlideOutOnSubscribe.directSetResult(this.mTopCover, 48);
                SlideOutOnSubscribe.directSetResult(this.mBottomCover, 80);
            } else {
                list.add(Completable.create(new SlideOutOnSubscribe(this.mTopCover, 48).setDurationTime(200)));
                list.add(Completable.create(new SlideOutOnSubscribe(this.mBottomCover, 80).setDurationTime(200)));
            }
        }
    }

    public void provideRotateItem(List<View> list, int i) {
        super.provideRotateItem(list, i);
        this.mFaceView.setOrientation((360 - i) % ScreenEffect.SCREEN_PAPER_MODE_TWILIGHT_START_DEAULT, false);
        this.mAfRegionsView.setOrientation(i, false);
        this.mLightingView.setOrientation(i, false);
        this.mFocusView.setOrientation(i, false);
        list.add(this.mFocusView);
        list.add(this.mMultiSnapNum);
        list.add(this.mCaptureDelayNumber);
    }

    public void reShowFaceRect() {
        this.mFaceView.reShowFaceRect();
    }

    /* access modifiers changed from: protected */
    public void register(ModeCoordinator modeCoordinator) {
        super.register(modeCoordinator);
        modeCoordinator.attachProtocol(166, this);
        modeCoordinator.attachProtocol(214, this);
        registerBackStack(modeCoordinator, this);
        if (!b.isSupportedOpticalZoom()) {
            modeCoordinator.attachProtocol(184, this);
        }
    }

    public void removeTiltShiftMask() {
        this.mEffectCropView.removeTiltShiftMask();
    }

    public void setActiveIndicator(int i) {
        this.mActiveIndicator = i;
    }

    public void setAfRegionView(MeteringRectangle[] meteringRectangleArr, Rect rect, float f) {
        this.mAfRegionsView.setAfRegionRect(meteringRectangleArr, rect, f);
    }

    public void setCameraDisplayOrientation(int i) {
        if (this.mFaceView != null && this.mAfRegionsView != null) {
            this.mFaceView.setCameraDisplayOrientation(i);
            this.mAfRegionsView.setCameraDisplayOrientation(i);
        }
    }

    public void setCenterHint(int i, String str, String str2, int i2) {
        this.mHandler.removeCallbacksAndMessages(this.mPreviewCenterHint);
        if (i == 0) {
            this.mCenterHintText.setText(str);
            if (str == null || str.equals("")) {
                this.mCenterHintText.setVisibility(8);
            } else {
                this.mCenterHintText.setVisibility(0);
            }
            if (str2 == null || str2.equals("")) {
                this.mCenterHintIcon.setVisibility(8);
            } else {
                c.a((Fragment) this).l(str2).a(this.mCenterHintIcon);
                this.mCenterHintIcon.setVisibility(0);
            }
            if (i2 > 0) {
                this.mHandler.postAtTime(new Runnable() {
                    public void run() {
                        FragmentMainContent.this.mPreviewCenterHint.setVisibility(8);
                    }
                }, this.mPreviewCenterHint, SystemClock.uptimeMillis() + ((long) i2));
            }
        }
        this.mPreviewCenterHint.setVisibility(i);
    }

    public void setDisplaySize(int i, int i2) {
        this.mObjectView.setDisplaySize(i, i2);
    }

    public void setEffectViewVisible(boolean z) {
        if (z) {
            this.mEffectCropView.show();
        } else {
            this.mEffectCropView.hide();
        }
    }

    public void setEvAdjustable(boolean z) {
        this.mFocusView.setEvAdjustable(z);
    }

    public boolean setFaces(int i, CameraHardwareFace[] cameraHardwareFaceArr, Rect rect, float f) {
        if (i == 1) {
            return this.mFaceView.setFaces(cameraHardwareFaceArr, rect, f);
        }
        if (i != 3) {
            return false;
        }
        if (cameraHardwareFaceArr != null && cameraHardwareFaceArr.length > 0) {
            this.mObjectView.setObject(cameraHardwareFaceArr[0]);
        }
        return true;
    }

    public void setFocusViewPosition(int i, int i2, int i3) {
        this.mFocusView.setPosition(i, i2, i3);
        this.mFaceView.forceHideRect();
    }

    public void setFocusViewType(boolean z) {
        this.mFocusView.setFocusType(z);
    }

    public void setObjectViewListener(ObjectViewListener objectViewListener) {
        this.mObjectView.setObjectViewListener(objectViewListener);
    }

    public void setPreviewAspectRatio(float f) {
        adjustViewHeight();
    }

    public void setPreviewSize(int i, int i2) {
        if (this.mAutoZoomOverlay != null) {
            this.mAutoZoomOverlay.setPreviewSize(new Size(i, i2));
        }
    }

    public void setShowGenderAndAge(boolean z) {
        this.mFaceView.setShowGenderAndAge(z);
    }

    public void setShowMagicMirror(boolean z) {
        this.mFaceView.setShowMagicMirror(z);
    }

    public void setSkipDrawFace(boolean z) {
        this.mFaceView.setSkipDraw(z);
    }

    @TargetApi(21)
    public void setSnapNumValue(int i) {
        if (this.mSnapStyle == null) {
            this.mSnapStyle = new TextAppearanceSpan(getContext(), R.style.SnapTipTextStyle);
        }
        if (this.mStringBuilder == null) {
            this.mStringBuilder = new SpannableStringBuilder();
        } else {
            this.mStringBuilder.clear();
        }
        this.mStringBuilder.append(String.format("%02d", new Object[]{Integer.valueOf(i)}), this.mSnapStyle, 33);
        this.mMultiSnapNum.setText(this.mStringBuilder);
    }

    public void setSnapNumVisible(boolean z, boolean z2) {
        if (z != (this.mMultiSnapNum.getVisibility() == 0)) {
            if (this.mZoomInAnimator == null) {
                initSnapNumAnimator();
            }
            if (z) {
                AlphaInOnSubscribe.directSetResult(this.mMultiSnapNum);
                setSnapNumValue(0);
                this.mZoomInAnimator.start();
            } else {
                this.mZoomOutAnimator.start();
                Completable.create(new AlphaOutOnSubscribe(this.mMultiSnapNum).setStartDelayTime(System.SCREEN_KEY_LONG_PRESS_TIMEOUT_DEFAULT)).subscribe();
            }
        }
    }

    public void showDelayNumber(int i) {
        if (this.mCaptureDelayNumber.getVisibility() != 0) {
            int i2 = 0;
            if (this.mCurrentMode != 165) {
                i2 = getResources().getDimensionPixelSize(R.dimen.capture_delay_number_margin) + this.mDisplayRectTopMargin;
            }
            if (this.mDisplayRectTopMargin == 0 || this.mCurrentMode == 165) {
                i2 += getResources().getDimensionPixelSize(R.dimen.top_control_panel_height);
                int dimensionPixelSize = getResources().getDimensionPixelSize(R.dimen.square_delay_number_offset_extra);
                if (!Util.sIsnotchScreenHidden && dimensionPixelSize > 0) {
                    i2 += dimensionPixelSize;
                }
            }
            ((MarginLayoutParams) this.mCaptureDelayNumber.getLayoutParams()).topMargin = i2;
            if (this.mDegree > 0) {
                ViewCompat.setRotation(this.mCaptureDelayNumber, (float) this.mDegree);
            }
            Completable.create(new AlphaInOnSubscribe(this.mCaptureDelayNumber)).subscribe();
        }
        this.mCaptureDelayNumber.setText(String.valueOf(i));
    }

    public void showIndicator(int i, int i2) {
        switch (i) {
            case 1:
                showIndicator((FocusIndicator) this.mFaceView, i2);
                return;
            case 2:
                showIndicator((FocusIndicator) this.mFocusView, i2);
                return;
            case 3:
                showIndicator((FocusIndicator) this.mObjectView, i2);
                return;
            default:
                return;
        }
    }

    public void showReviewViews(Bitmap bitmap) {
        if (bitmap != null) {
            this.mPreviewPanel.mVideoReviewImage.setImageBitmap(bitmap);
            this.mPreviewPanel.mVideoReviewImage.setVisibility(0);
        }
        Util.fadeIn(this.mPreviewPanel.mVideoReviewPlay);
    }

    /* access modifiers changed from: protected */
    public void unRegister(ModeCoordinator modeCoordinator) {
        super.unRegister(modeCoordinator);
        modeCoordinator.detachProtocol(166, this);
        unRegisterBackStack(modeCoordinator, this);
        modeCoordinator.detachProtocol(214, this);
        if (!b.isSupportedOpticalZoom()) {
            modeCoordinator.detachProtocol(184, this);
        }
    }

    public void updateContentDescription() {
        this.mPreviewFrame.setContentDescription(getString(R.string.accessibility_front_preview_status));
        this.mPreviewFrame.announceForAccessibility(getString(R.string.accessibility_front_preview_status));
    }

    public void updateEffectViewVisible() {
        this.mEffectCropView.updateVisible();
    }

    public void updateEffectViewVisible(int i) {
        this.mEffectCropView.updateVisible(i);
    }

    public void updateFaceView(boolean z, boolean z2, boolean z3, boolean z4, int i) {
        if (z2) {
            this.mFaceView.clear();
        }
        this.mFaceView.setVisibility(z ? 0 : 8);
        if (i > 0) {
            this.mFaceView.setCameraDisplayOrientation(i);
        }
        this.mFaceView.setMirror(z3);
        if (z4) {
            this.mFaceView.resume();
        }
    }
}