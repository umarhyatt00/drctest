package com.yeel.drc.api;

public interface CustomCallback {
    void success(CommonResponse commonResponse);
    void retry();
}
