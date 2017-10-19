package com.liaobusi.baasquery.bean;

import com.droi.sdk.core.DroiExpose;
import com.droi.sdk.core.DroiObjectName;
import com.droi.sdk.core.DroiUser;

import java.io.Serializable;

/**
 * 用户表
 * Created by liumeilin on 2017/3/25.
 */
@DroiObjectName("_User")
public class UserBean extends DroiUser implements Serializable{
    @DroiExpose
    private String _Id;
    @DroiExpose
    private String userName;
    @DroiExpose
    private String headImgUrl;
    /**性别0为男，1为女*/
    @DroiExpose
    private int gender = 0;
    @DroiExpose
    private String birthday;
    /**
     * 个人设置的背景图
     */
    @DroiExpose
    private String background;

    public UserBean() {
    }


    public String get_Id() {
        return _Id;
    }

    public void set_Id(String _Id) {
        this._Id = _Id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getHeadImgUrl() {
        return headImgUrl;
    }

    public void setHeadImgUrl(String headImgUrl) {
        this.headImgUrl = headImgUrl;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }
}
