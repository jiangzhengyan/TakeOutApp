package com.xiaozhang.takeout.ui.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xiaozhang.takeout.R;
import com.xiaozhang.takeout.global.MyApplication;
import com.xiaozhang.takeout.model.bean.ReceiptAddressBean;
import com.xiaozhang.takeout.model.dao.ReceiptAddressDao;
import com.xiaozhang.takeout.util.SMSUtil;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by zhangxiao on 2018/3/23.
 */
public class AddAddressActivity extends BaseActivity implements View.OnFocusChangeListener {
    @InjectView(R.id.ib_back)
    ImageButton ibBack;
    @InjectView(R.id.tv_title)
    TextView tvTitle;
    @InjectView(R.id.ib_delete)
    ImageButton ibDelete;
    @InjectView(R.id.tv_name)
    TextView tvName;
    @InjectView(R.id.et_name)
    EditText etName;
    @InjectView(R.id.rb_man)
    RadioButton rbMan;
    @InjectView(R.id.rb_women)
    RadioButton rbWomen;
    @InjectView(R.id.rg_sex)
    RadioGroup rgSex;
    @InjectView(R.id.et_phone)
    EditText etPhone;
    @InjectView(R.id.ib_delete_phone)
    ImageButton ibDeletePhone;
    @InjectView(R.id.ib_add_phone_other)
    ImageButton ibAddPhoneOther;
    @InjectView(R.id.et_phone_other)
    EditText etPhoneOther;
    @InjectView(R.id.ib_delete_phone_other)
    ImageButton ibDeletePhoneOther;
    @InjectView(R.id.rl_phone_other)
    RelativeLayout rlPhoneOther;
    @InjectView(R.id.tv_receipt_address)
    TextView tvReceiptAddress;
    @InjectView(R.id.et_detail_address)
    EditText etDetailAddress;
    @InjectView(R.id.tv_label)
    TextView tvLabel;
    @InjectView(R.id.ib_select_label)
    ImageView ibSelectLabel;
    @InjectView(R.id.tv_ok)
    TextView tv_ok;
    private String[] addressLabels;
    private int[] bgLabels;
    private ReceiptAddressDao receiptAddressDao;
    private ReceiptAddressBean receiptAddressBean;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_receipt_address);
        ButterKnife.inject(this);
        receiptAddressDao = new ReceiptAddressDao(this);

        addressLabels = new String[]{ "家", "公司", "学校"};
        //家  橙色
        //公司 蓝色
        //学校   绿色
        bgLabels = new int[]{
                Color.parseColor("#fc7251"),//家  橙色
                Color.parseColor("#468ade"),//公司 蓝色
                Color.parseColor("#02c14b"),//学校   绿色
        };

        etPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                String phone = etPhone.getText().toString();
                if (!TextUtils.isEmpty(phone)){
                    ibDeletePhone.setVisibility(View.VISIBLE);
                }else{
                    ibDeletePhone.setVisibility(View.GONE);
                }
            }
        });
        etPhoneOther.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                String phone = etPhoneOther.getText().toString();
                if (!TextUtils.isEmpty(phone)){
                    ibDeletePhoneOther.setVisibility(View.VISIBLE);
                }else{
                    ibDeletePhoneOther.setVisibility(View.GONE);
                }
            }
        });
        //监听editText焦点的变化
        etPhone.setOnFocusChangeListener(this);
        etPhoneOther.setOnFocusChangeListener(this);

        receiptAddressBean = (ReceiptAddressBean) getIntent().getSerializableExtra("receiptAddress");
        showReceiptAddress();
    }
    private void showReceiptAddress() {
        if (receiptAddressBean != null) {
            etName.setText(receiptAddressBean.getName());
            String sex = receiptAddressBean.getSex();
            if (sex.equals("男性")) {
                rgSex.check(R.id.rb_man);
            } else {
                rgSex.check(R.id.rb_women);
            }
            etPhone.setText(receiptAddressBean.getPhone());
            etPhoneOther.setText(receiptAddressBean.getPhoneOther());
            tvReceiptAddress.setText(receiptAddressBean.getReceiptAddress());
            etDetailAddress.setText(receiptAddressBean.getDetailAddress());

            tvLabel.setText(receiptAddressBean.getLabel());
            int index = getIndex(receiptAddressBean.getLabel());
            tvLabel.setBackgroundColor(bgLabels[index]);
        }
    }
    private int getIndex(String label) {
        int index = 0;
        for (int i = 0; i < addressLabels.length; i++) {
            if (label.equals(addressLabels[i]) ){
                //一致,则设置背景,展示即可
                index = i;
                break;
            }
        }
        return index;
    }

    @OnClick({R.id.ib_add_phone_other,R.id.ib_delete_phone,
            R.id.ib_delete_phone_other,R.id.ib_select_label,R.id.tv_ok
            ,R.id.ib_delete,R.id.tv_receipt_address})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.ib_add_phone_other:
                //显示或者隐藏备用电话输入框
                if (rlPhoneOther.getVisibility() == View.VISIBLE){
                    rlPhoneOther.setVisibility(View.GONE);
                }else{
                    rlPhoneOther.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.ib_delete_phone:
                etPhone.setText("");
                break;
            case R.id.ib_delete_phone_other:
                etPhoneOther.setText("");
                break;
            case R.id.ib_select_label:
                showLabelDialog();
                break;
            case R.id.tv_ok:
                if (checkData()){
                    if (receiptAddressBean!=null){
                        updateReceiptAddress();
                    }else{
                        //要求用户输入的内容,都有输入,而且合法,数据库的插入
                        insertReceiptAddress();
                    }
                    finish();
                }
                break;
            case R.id.ib_delete:
                showDeleteDialog();
                break;
            case R.id.tv_receipt_address:
                Intent intent = new Intent(this, SelectAddressActivity.class);
                startActivityForResult(intent,100);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data!=null && requestCode == 100 && resultCode == 101){

            String title = data.getStringExtra("title");
            String snippet = data.getStringExtra("snippet");

            tvReceiptAddress.setText(title);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void showDeleteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("是否删除此此条数据?");
        builder.setNegativeButton("否", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (receiptAddressBean!=null){
                    receiptAddressDao.delete(receiptAddressBean);
                }
                dialog.dismiss();
                finish();
            }
        });
        builder.show();
    }

    private void updateReceiptAddress() {
        String name = etName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String otherPhone = etPhoneOther.getText().toString().trim();
        String receiptAddress = tvReceiptAddress.getText().toString().trim();
        String detailAddress = etDetailAddress.getText().toString().trim();
        String tvLableString = tvLabel.getText().toString();
        int checkId = rgSex.getCheckedRadioButtonId();
        String sex = "";
        if (checkId == R.id.rb_man) {
            sex = "男性";
        } else {
            sex = "女性";
        }
        receiptAddressBean.setName(name);
        receiptAddressBean.setPhone(phone);
        receiptAddressBean.setPhoneOther(otherPhone);
        receiptAddressBean.setReceiptAddress(receiptAddress);
        receiptAddressBean.setDetailAddress(detailAddress);
        receiptAddressBean.setLabel(tvLableString);
        receiptAddressBean.setSex(sex);
        //更新数据库
        receiptAddressDao.update(receiptAddressBean);
    }

    private void insertReceiptAddress() {
        String name = etName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String otherPhone = etPhoneOther.getText().toString().trim();
        String receiptAddress = tvReceiptAddress.getText().toString().trim();
        String detailAddress = etDetailAddress.getText().toString().trim();
        String tvLableString = tvLabel.getText().toString();
        int checkId = rgSex.getCheckedRadioButtonId();
        String sex = "";
        if (checkId == R.id.rb_man) {
            sex = "男性";
        } else {
            sex = "女性";
        }

        ReceiptAddressBean receiptAddressBean = new ReceiptAddressBean(MyApplication.userId, name, sex, phone,
                otherPhone, receiptAddress, detailAddress, tvLableString, 0);
        receiptAddressDao.insert(receiptAddressBean);
    }

    private boolean checkData() {
        String name = etName.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "请填写联系人", Toast.LENGTH_SHORT).show();
            return false;
        }
        String phone = etPhone.getText().toString().trim();
        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "请填写手机号码", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!SMSUtil.isMobileNO(phone)) {
            Toast.makeText(this, "请填写合法的手机号", Toast.LENGTH_SHORT).show();
            return false;
        }
        String receiptAddress = tvReceiptAddress.getText().toString().trim();
        if (TextUtils.isEmpty(receiptAddress)) {
            Toast.makeText(this, "请选择收货地址", Toast.LENGTH_SHORT).show();
            return false;
        }
        String address = etDetailAddress.getText().toString().trim();
        if (TextUtils.isEmpty(address)) {
            Toast.makeText(this, "请填写详细地址", Toast.LENGTH_SHORT).show();
            return false;
        }
        int checkedRadioButtonId = rgSex.getCheckedRadioButtonId();
        if (checkedRadioButtonId != R.id.rb_man && checkedRadioButtonId != R.id.rb_women) {
            //2个不相等，则说明没有选中任意一个
            Toast.makeText(this, "请选择性别", Toast.LENGTH_SHORT).show();
            return false;
        }

        String tvLableString = tvLabel.getText().toString();
        if (TextUtils.isEmpty(tvLableString)) {
            Toast.makeText(this, "请输入标签信息", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void showLabelDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("请选择标签内容");
        builder.setItems(addressLabels, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                tvLabel.setText(addressLabels[which]);
                tvLabel.setBackgroundColor(bgLabels[which]);
            }
        });
        builder.show();
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        //hasFocus在调用此方法的时候,控件是否有焦点  true 有焦点    false 没有焦点
        switch (v.getId()){
            case R.id.et_phone:
                String phone = etPhone.getText().toString().trim();
                if (!TextUtils.isEmpty(phone) && hasFocus){
                    ibDeletePhone.setVisibility(View.VISIBLE);
                }else{
                    ibDeletePhone.setVisibility(View.GONE);
                }
                break;
            case R.id.et_phone_other:
                String phoneOther = etPhoneOther.getText().toString().trim();
                if (!TextUtils.isEmpty(phoneOther) && hasFocus){
                    ibDeletePhoneOther.setVisibility(View.VISIBLE);
                }else{
                    ibDeletePhoneOther.setVisibility(View.GONE);
                }
                break;
        }
    }
}
