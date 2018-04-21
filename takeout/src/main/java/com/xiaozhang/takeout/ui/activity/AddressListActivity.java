package com.xiaozhang.takeout.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xiaozhang.takeout.R;
import com.xiaozhang.takeout.global.MyApplication;
import com.xiaozhang.takeout.model.bean.ReceiptAddressBean;
import com.xiaozhang.takeout.model.dao.ReceiptAddressDao;
import com.xiaozhang.takeout.ui.adapter.AddressListAdapter;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by zhangxiao on 2018/3/23.
 *  收货地址
 */
public class AddressListActivity extends BaseActivity {
    @InjectView(R.id.ib_back)
    ImageButton ibBack;
    @InjectView(R.id.tv_title)
    TextView tvTitle;
    @InjectView(R.id.rv_receipt_address)
    RecyclerView rvReceiptAddress;
    @InjectView(R.id.ll_add_address)
    LinearLayout ll_add_address;
    private ReceiptAddressDao receiptAddressDao;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt_address);
        ButterKnife.inject(this);
    }

    @Override
    protected void onResume() {
        //1.查询地址的receiptAddressDao对象
        receiptAddressDao = new ReceiptAddressDao(this);
        List<ReceiptAddressBean> allUserAddressList = receiptAddressDao.getAllUserAddress(MyApplication.userId);
        //2.将上诉集合填充recyclerView列表
        AddressListAdapter addressListAdapter = new AddressListAdapter(allUserAddressList,this);
        rvReceiptAddress.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        rvReceiptAddress.setAdapter(addressListAdapter);
        super.onResume();
    }

    @OnClick({R.id.ll_add_address})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.ll_add_address:
                Intent intentAddress = new Intent(this, AddAddressActivity.class);
                startActivity(intentAddress);
                break;
        }
    }
}
