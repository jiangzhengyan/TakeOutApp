package com.xiaozhang.takeout.model.bean;

import java.util.List;

/**
 * Created by zhangxiao on 2018/3/15.
 */

public class BusinessInfo {
    private List<GoodsTypeInfo> list;//商品分类
    public List<GoodsTypeInfo> getList() {
        return list;
    }
    public void setList(List<GoodsTypeInfo> list) {
        this.list = list;
    }
}
