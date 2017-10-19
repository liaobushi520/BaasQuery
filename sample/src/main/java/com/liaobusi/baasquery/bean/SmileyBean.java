package com.liaobusi.baasquery.bean;

import com.droi.sdk.core.DroiExpose;
import com.droi.sdk.core.DroiObject;
import com.droi.sdk.core.DroiObjectName;

/**
 * Created by huangxiaotian on 2017/6/1.
 */
@DroiObjectName("dt_smiley")
public class SmileyBean extends DroiObject {
    /**
     *是否是删除的
     */
    @DroiExpose
    private boolean status;
    /**
     *上传图片的路径
     */
    @DroiExpose
    private String fileUrl;

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }
}
