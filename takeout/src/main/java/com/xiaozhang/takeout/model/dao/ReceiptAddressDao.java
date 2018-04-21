package com.xiaozhang.takeout.model.dao;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.Where;
import com.xiaozhang.takeout.model.bean.ReceiptAddressBean;
import com.xiaozhang.takeout.model.db.DBHelper;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by zhangxiao on 2018/3/11.
 */

public class ReceiptAddressDao {

    private Dao<ReceiptAddressBean, Integer> dao;

    public ReceiptAddressDao(Context ctx) {
        //1.DBHelper对象
        DBHelper dbHelper = DBHelper.getInstance(ctx);
        dao = dbHelper.getDao(ReceiptAddressBean.class);
    }

    /**
     * @param userId    查询那个用户的地址数据
     * @return  查询当前登录用户的所有地址数据
     */
    public List<ReceiptAddressBean> getAllUserAddress(int userId){
        //select * from t_receiptaddress where 'uid' = userid;
        try {
            Where<ReceiptAddressBean, Integer> where = dao.queryBuilder().where();
            List<ReceiptAddressBean> receiptAddressList = where.eq("uid", userId).query();
            return receiptAddressList;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param receiptAddressBean    需要插入的数据的javabean对象
     */
    public void insert(ReceiptAddressBean receiptAddressBean){
        try {
            dao.create(receiptAddressBean);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param receiptAddressBean    将receiptAddressBean中的数据更新到数据库中
     */
    public void update(ReceiptAddressBean receiptAddressBean){
        try {
            dao.update(receiptAddressBean);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param receiptAddressBean    需要删除的数据指向的bean对象
     */
    public void delete(ReceiptAddressBean receiptAddressBean){
        try {
            dao.delete(receiptAddressBean);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param userId    查询登录用户id值
     * @param isSelect  查询默认选中地址或者未选中地址
     * @return          返回查询选中地址数据
     */
    //查询登录用户的选中默认送货地址的数据
    //select * from t_receiptaddress where 'uid' = userid and 'isSelect' = 1;
    public ReceiptAddressBean getUserSelectAddress(int userId,int isSelect){
        try {
            //提供拼接sql的对象
            Where<ReceiptAddressBean, Integer> where = dao.queryBuilder().where();
            //指定查询条件
            List<ReceiptAddressBean> receiptAddressList = where.eq("uid", userId).and()
                    .eq("isSelect", isSelect).query();
            if (receiptAddressList!=null && receiptAddressList.size()>0){
                ReceiptAddressBean receiptAddressBean = receiptAddressList.get(0);
                return receiptAddressBean;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
