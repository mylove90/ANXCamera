.class public Lcom/android/camera/fragment/beauty/MakeupBeautyFragment;
.super Lcom/android/camera/fragment/beauty/BaseBeautyMakeupFragment;
.source "MakeupBeautyFragment.java"


# static fields
.field private static final TAG:Ljava/lang/String; = "MakeupBeautyFragment"


# direct methods
.method public constructor <init>()V
    .locals 0

    invoke-direct {p0}, Lcom/android/camera/fragment/beauty/BaseBeautyMakeupFragment;-><init>()V

    return-void
.end method


# virtual methods
.method protected getClassString()Ljava/lang/String;
    .locals 0

    const-class p0, Lcom/android/camera/fragment/beauty/MakeupBeautyFragment;

    invoke-virtual {p0}, Ljava/lang/Class;->getSimpleName()Ljava/lang/String;

    move-result-object p0

    return-object p0
.end method

.method protected getShineType()Ljava/lang/String;
    .locals 0

    const-string p0, "5"

    return-object p0
.end method

.method protected initExtraType()V
    .locals 1

    const/4 v0, 0x1

    iput v0, p0, Lcom/android/camera/fragment/beauty/BaseBeautyMakeupFragment;->mHeaderElement:I

    const/4 v0, -0x1

    iput v0, p0, Lcom/android/camera/fragment/beauty/BaseBeautyMakeupFragment;->mFooterElement:I

    return-void
.end method

.method protected onAdapterItemClick(Lcom/android/camera/data/data/TypeItem;)V
    .locals 1

    invoke-static {}, Lcom/android/camera/protocol/ModeCoordinatorImpl;->getInstance()Lcom/android/camera/protocol/ModeCoordinatorImpl;

    move-result-object p0

    const/16 v0, 0xb4

    invoke-virtual {p0, v0}, Lcom/android/camera/protocol/ModeCoordinatorImpl;->getAttachProtocol(I)Lcom/android/camera/protocol/ModeProtocol$BaseProtocol;

    move-result-object p0

    check-cast p0, Lcom/android/camera/protocol/ModeProtocol$MakeupProtocol;

    if-eqz p0, :cond_0

    iget-object p1, p1, Lcom/android/camera/data/data/TypeItem;->mKeyOrType:Ljava/lang/String;

    const/4 v0, 0x1

    invoke-interface {p0, p1, v0}, Lcom/android/camera/protocol/ModeProtocol$MakeupProtocol;->onMakeupItemSelected(Ljava/lang/String;Z)V

    :cond_0
    return-void
.end method

.method protected onClearClick()V
    .locals 0

    invoke-static {}, Lcom/android/camera/CameraSettings;->resetEyeLight()V

    invoke-static {}, Lcom/android/camera/fragment/beauty/BeautyHelper;->clearBeauty()V

    invoke-virtual {p0}, Lcom/android/camera/fragment/beauty/BaseBeautyMakeupFragment;->selectFirstItem()V

    return-void
.end method

.method protected onResetClick()V
    .locals 2

    invoke-static {}, Lcom/android/camera/CameraSettings;->resetEyeLight()V

    invoke-static {}, Lcom/android/camera/fragment/beauty/BeautyHelper;->resetBeauty()V

    invoke-virtual {p0}, Lcom/android/camera/fragment/beauty/BaseBeautyMakeupFragment;->selectFirstItem()V

    invoke-virtual {p0}, Landroid/support/v4/app/Fragment;->getResources()Landroid/content/res/Resources;

    move-result-object v0

    const v1, 0x7f0f0149

    invoke-virtual {v0, v1}, Landroid/content/res/Resources;->getString(I)Ljava/lang/String;

    move-result-object v0

    invoke-virtual {p0, v0}, Lcom/android/camera/fragment/beauty/BaseBeautyMakeupFragment;->toast(Ljava/lang/String;)V

    return-void
.end method
