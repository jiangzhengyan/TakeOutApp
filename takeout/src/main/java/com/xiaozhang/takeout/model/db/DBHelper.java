package com.xiaozhang.takeout.model.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.xiaozhang.takeout.model.bean.ReceiptAddressBean;
import com.xiaozhang.takeout.model.bean.UserInfo;

import java.sql.SQLException;
import java.util.HashMap;

/**
 * Created by zhangxiao on 2018/3/16.
 */

public class DBHelper extends OrmLiteSqliteOpenHelper {
    private HashMap<String,Dao> daoMap = new HashMap<>();

    private DBHelper(Context context) {
        super(context, "xaiozhang.db", null, 1);
    }
    private static DBHelper dbHelper = null;
    public synchronized  static DBHelper getInstance(Context ctx){
        if (dbHelper == null){
            dbHelper = new DBHelper(ctx);
        }
        return dbHelper;
    };

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, UserInfo.class);//6个dao
            TableUtils.createTable(connectionSource, ReceiptAddressBean.class);//6个dao
            //dao对象,触发增删改查方法
            //dao对象可能会有多个,一张表,一个dao对象
            //javaee dao层 service层
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {

    }

    /**
     * @param clazz 表对应的javabean的字节码文件
     * @return  clazz对应的dao对象,用于操作表的增删改成
     */
    public Dao getDao(Class clazz){
        //根据不同的表,返回不同的dao对象,用于对此表进行增删改查操作
        try {
            Dao dao = daoMap.get(clazz.getSimpleName());
            if (dao == null){
                dao = super.getDao(clazz);
                //将创建的dao对象存储在map集合中
                daoMap.put(clazz.getSimpleName(),dao);
            }
            return dao;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void close() {
        //清空在集合中存储dao对象
        for (String key: daoMap.keySet()) {
            Dao dao = daoMap.get(key);
            dao = null;
        }
        super.close();
    }
}
