package com.android.camera.network.net.base;

import com.android.volley.VolleyError;

public class RequestError extends VolleyError {
    private ErrorCode mErrorCode;
    private Object mResponseData;

    public RequestError(ErrorCode errorCode, String str, Object obj) {
        super(str);
        this.mErrorCode = errorCode;
        this.mResponseData = obj;
    }

    public Throwable getCause() {
        return this.mResponseData instanceof Throwable ? (Throwable) this.mResponseData : super.getCause();
    }

    public ErrorCode getErrorCode() {
        return this.mErrorCode;
    }

    public Object getResponseData() {
        return this.mResponseData;
    }

    public String toString() {
        return super.toString() + " code : " + this.mErrorCode;
    }
}