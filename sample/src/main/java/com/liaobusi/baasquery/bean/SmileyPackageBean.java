package com.liaobusi.baasquery.bean;

import com.droi.sdk.core.DroiExpose;
import com.droi.sdk.core.DroiObject;
import com.droi.sdk.core.DroiObjectName;
import com.droi.sdk.core.DroiReference;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by huangxiaotian on 2017/6/1.
 * 表情包实体类，根据其中的type来判断是帖子还是表情包
 */
@DroiObjectName("dt_smiley_package")
public class SmileyPackageBean extends DroiObject   {
    /**
     * 关于发布者的一些信息
     */
    @DroiReference
    UserBean author;
    /**
     * 发布消息的标题
     */
    @DroiExpose
    String title;
    /**
     * 保存表情图片的列表
     */
    List<SmileyBean> list = new ArrayList<>();
    /**
     * 分享的数量
     */
    @DroiExpose
    int shareNum;
    /**
     * 收藏的数量
     */
    @DroiExpose
    int favoritesNum;
    /**
     * 下载的数量
     */
    @DroiExpose
    int downloadNum;
    /**
     * 存储的类型，0表示帖子，1表示表情包
     */
    @DroiExpose
    int type;
    /**
     * 本地存储的路径
     */
    @DroiExpose
    String typePic;
    /**
     * 是否删除的状态
     */
    @DroiExpose
    boolean status;
    /**
     * 帖子图片的路径
     */
    @DroiExpose
    String fileUrl;
    /**
     * 是否需要发布到发现社区
     */
    @DroiExpose
    boolean isPublish;
    /**
     * 表示显示的样式
     * 1：表情包
     * 2：帖子
     * 3：大图
     */
    @DroiExpose
    int showType;
    /**
     * 评论的数量
     */
    @DroiExpose
    int commentNum;
    /**
     * 阅读的数量
     */
    @DroiExpose
    int watchNum;
    /**
     * 表情包和帖子的关键词
     */
    @DroiExpose
    String keyWords;
    /**
     * 点赞的数量
     */
    @DroiExpose
    int praisenum;
    private List<String> imageList;

    public List<String> getImageList() {
        return imageList;
    }

    public void setImageList(List<String> imageList) {
        this.imageList = imageList;
    }
    public int getWatchNum() {
        return watchNum;
    }

    public void setWatchNum(int watchNum) {
        this.watchNum = watchNum;
    }

    public int getCommentNum() {
        return commentNum;
    }

    public void setCommentNum(int commentNum) {
        this.commentNum = commentNum;
    }

    public int getShowType() {
        return showType;
    }

    public void setShowType(int showType) {
        this.showType = showType;
    }

    public boolean isPublish() {
        return isPublish;
    }

    public void setPublish(boolean publish) {
        isPublish = publish;
    }

    public UserBean getAuthor() {
        return author;
    }

    public void setAuthor(UserBean author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<SmileyBean> getList() {
        return list;
    }

    public void setList(List<SmileyBean> list) {
        this.list = list;
    }

    public int getShareNum() {
        return shareNum;
    }

    public void setShareNum(int shareNum) {
        this.shareNum = shareNum;
    }

    public int getFavoritesNum() {
        return favoritesNum;
    }

    public void setFavoritesNum(int favoritesNum) {
        this.favoritesNum = favoritesNum;
    }

    public int getDownloadNum() {
        return downloadNum;
    }

    public void setDownloadNum(int downloadNum) {
        this.downloadNum = downloadNum;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTypePic() {
        return typePic;
    }

    public void setTypePic(String typePic) {
        this.typePic = typePic;
    }

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

    public String getKeyWords() {
        return keyWords;
    }

    public void setKeyWords(String keyWords) {
        this.keyWords = keyWords;
    }


    public int getPraisenum() {
        return praisenum;
    }

    public void setPraisenum(int praisenum) {
        this.praisenum = praisenum;
    }
}