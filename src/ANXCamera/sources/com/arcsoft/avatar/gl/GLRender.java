package com.arcsoft.avatar.gl;

import android.opengl.GLES20;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class GLRender {

    /* renamed from: a  reason: collision with root package name */
    private static final String f20a = "Arc_GLRender";
    private static final int l = 4;
    private static final String m = "attribute vec2 vPos;\nattribute vec2 vTex;\nvarying   vec2 vTextureCoord;\nvoid main(){\ngl_Position=vec4(vPos,0.0,1.0);\nvTextureCoord = vTex;\n}\n";
    private static final String n = "precision mediump float;\nvarying vec2 vTextureCoord;\nuniform sampler2D sTexture;\nvoid main() {\n    gl_FragColor = texture2D(sTexture, vTextureCoord);\n}\n";

    /* renamed from: b  reason: collision with root package name */
    private int f21b;
    private int c;
    private int d;
    private int e;
    private int[] f = new int[2];
    private int g;
    private int h;
    private boolean i;
    private int j;
    private boolean k;

    public GLRender(int i2, int i3, int i4, boolean z) {
        if (i3 * i3 <= 0 || i2 < 0 || i3 < 0) {
            throw new RuntimeException("GLRender() frameWidth=" + i2 + " ,frameHeight=" + i3 + " invalid.");
        }
        this.g = i2;
        this.h = i3;
        this.k = false;
    }

    public void initRender(boolean z) {
        GLES20.glGenBuffers(2, this.f, 0);
        float[] fArr = {-1.0f, 1.0f, -1.0f, -1.0f, 1.0f, -1.0f, 1.0f, 1.0f};
        ByteBuffer allocateDirect = ByteBuffer.allocateDirect(fArr.length * 4);
        allocateDirect.order(ByteOrder.nativeOrder());
        FloatBuffer asFloatBuffer = allocateDirect.asFloatBuffer();
        asFloatBuffer.put(fArr);
        asFloatBuffer.position(0);
        GLES20.glBindBuffer(34962, this.f[0]);
        GLES20.glBufferData(34962, fArr.length * 4, asFloatBuffer, 35044);
        GLES20.glBindBuffer(34962, 0);
        float[] fArr2 = {0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f};
        float[] fArr3 = {0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f};
        if (!z) {
            fArr3 = fArr2;
        }
        ByteBuffer allocateDirect2 = ByteBuffer.allocateDirect(fArr3.length * 4);
        allocateDirect2.order(ByteOrder.nativeOrder());
        FloatBuffer asFloatBuffer2 = allocateDirect2.asFloatBuffer();
        asFloatBuffer2.put(fArr3);
        asFloatBuffer2.position(0);
        GLES20.glBindBuffer(34962, this.f[1]);
        GLES20.glBufferData(34962, 4 * fArr3.length, asFloatBuffer2, 35044);
        GLES20.glBindBuffer(34962, 0);
        this.f21b = ShaderManager.createProgram(m, n);
        if (this.f21b != 0) {
            this.c = GLES20.glGetAttribLocation(this.f21b, "vPos");
            this.d = GLES20.glGetAttribLocation(this.f21b, "vTex");
            this.e = GLES20.glGetUniformLocation(this.f21b, "sTexture");
            this.k = true;
        }
    }

    public void renderWithTextureId(int i2) {
        if (this.k) {
            GLES20.glUseProgram(this.f21b);
            GLES20.glClear(16640);
            GLES20.glBindBuffer(34962, this.f[0]);
            GLES20.glVertexAttribPointer(this.c, 2, 5126, false, 8, 0);
            GLES20.glEnableVertexAttribArray(this.c);
            GLES20.glBindBuffer(34962, this.f[1]);
            GLES20.glVertexAttribPointer(this.d, 2, 5126, false, 8, 0);
            GLES20.glEnableVertexAttribArray(this.d);
            GLES20.glActiveTexture(33984);
            GLES20.glBindTexture(3553, i2);
            GLES20.glUniform1i(this.e, 0);
            GLES20.glDrawArrays(6, 0, 4);
            GLES20.glDisableVertexAttribArray(this.c);
            GLES20.glDisableVertexAttribArray(this.d);
            GLES20.glBindBuffer(34962, 0);
            GLES20.glBindTexture(3553, 0);
            GLES20.glUseProgram(0);
        }
    }

    public void unInitRender() {
        if (this.k) {
            GLES20.glDeleteProgram(this.f21b);
            GLES20.glDeleteBuffers(2, this.f, 0);
        }
        this.k = false;
    }

    public void updateMirrorAndOrientation() {
    }
}