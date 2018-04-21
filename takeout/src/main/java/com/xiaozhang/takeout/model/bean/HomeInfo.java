package com.xiaozhang.takeout.model.bean;

import java.util.List;

/**
 * Created by zhangxiao on 2018/3/15.
 */
public class HomeInfo {
    private Head head;//head顶部的轮播图
    private List<HomeItem> body;//列表项

    public Head getHead() {
        return head;
    }

    public void setHead(Head head) {
        this.head = head;
    }

    public List<HomeItem> getBody() {
        return body;
    }

    public void setBody(List<HomeItem> body) {
        this.body = body;
    }
}
