.class public final Lmiui/R$anim;
.super Ljava/lang/Object;
.source "R.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Lmiui/R;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x19
    name = "anim"
.end annotation


# static fields
.field public static action_bar_slide_in_bottom:I

.field public static action_bar_slide_in_top:I

.field public static action_bar_slide_out_bottom:I

.field public static action_bar_slide_out_top:I

.field public static dialog_exit:I


# direct methods
.method static constructor <clinit>()V
    .registers 1

    const v0, 0x10040001

    sput v0, Lmiui/R$anim;->action_bar_slide_in_bottom:I

    const v0, 0x10040002

    sput v0, Lmiui/R$anim;->action_bar_slide_in_top:I

    const v0, 0x10040003

    sput v0, Lmiui/R$anim;->action_bar_slide_out_bottom:I

    const v0, 0x10040004

    sput v0, Lmiui/R$anim;->action_bar_slide_out_top:I

    const/high16 v0, 0x10040000

    sput v0, Lmiui/R$anim;->dialog_exit:I

    return-void
.end method

.method public constructor <init>()V
    .registers 1

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method
