package com.xiaozhang.takeout.ui.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaozhang.takeout.R;
import com.xiaozhang.takeout.model.bean.ReceiptAddressBean;
import com.xiaozhang.takeout.model.dao.ReceiptAddressDao;
import com.xiaozhang.takeout.ui.activity.AddAddressActivity;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by zhangxiao on 2018/3/23.
 */
public class AddressListAdapter extends RecyclerView.Adapter {
    private int[] bgLabels;
    private String[] addressLabels;
    private Activity activity;
    private List<ReceiptAddressBean> data;
    private final ReceiptAddressDao receiptAddressDao;

    public AddressListAdapter(List<ReceiptAddressBean> allUserAddressList, Activity activity) {
        this.data = allUserAddressList;
        this.activity = activity;

        receiptAddressDao = new ReceiptAddressDao(activity);

        addressLabels = new String[]{"家", "公司", "学校"};
        //家  橙色
        //公司 蓝色
        //学校   绿色
        bgLabels = new int[]{
                Color.parseColor("#fc7251"),//家  橙色
                Color.parseColor("#468ade"),//公司 蓝色
                Color.parseColor("#02c14b"),//学校   绿色
        };
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(activity, R.layout.item_receipt_address, null);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ReceiptAddressBean receiptAddressBean = data.get(position);
        ((ViewHolder) holder).tvName.setText(receiptAddressBean.getName());
        ((ViewHolder) holder).tvSex.setText(receiptAddressBean.getSex());

        if (!TextUtils.isEmpty(receiptAddressBean.getPhone()) && !TextUtils.isEmpty(receiptAddressBean.getPhoneOther())) {
            ((ViewHolder) holder).tvPhone.setText(receiptAddressBean.getPhone() + "," + receiptAddressBean.getPhoneOther());
        }
        if (!TextUtils.isEmpty(receiptAddressBean.getPhone()) && TextUtils.isEmpty(receiptAddressBean.getPhoneOther())) {
            ((ViewHolder) holder).tvPhone.setText(receiptAddressBean.getPhone());
        }

        ((ViewHolder) holder).tvAddress.setText(receiptAddressBean.getReceiptAddress() + receiptAddressBean.getDetailAddress());
        ((ViewHolder) holder).tvLabel.setText(receiptAddressBean.getLabel());
        int index = getIndex(receiptAddressBean.getLabel());
        ((ViewHolder) holder).tvLabel.setBackgroundColor(bgLabels[index]);
        ((ViewHolder) holder).tvLabel.setVisibility(View.VISIBLE);

        ((ViewHolder) holder).setPosition(position);

        //被选中,让checkbox显示,并且选中状态checked是true
        //只需要根据查询到的数据isSelect中的值是否为1,判断是否选中
        if(receiptAddressBean.isSelect()==1){
            //选中条目
            ((ViewHolder) holder).cb.setVisibility(View.VISIBLE);
            ((ViewHolder) holder).cb.setChecked(true);
        }else{
            //未选中的条目
            ((ViewHolder) holder).cb.setVisibility(View.GONE);
        }
    }

    private int getIndex(String label) {
        int index = -1;
        for (int i = 0; i < addressLabels.length; i++) {
            if (label.equals(addressLabels[i])){
                index = i;
                break;
            }
        }
        return index;
    }

    @Override
    public int getItemCount() {
        if (data != null && data.size() > 0) {
            return data.size();
        }
        return 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        @InjectView(R.id.cb)
        CheckBox cb;
        @InjectView(R.id.tv_name)
        TextView tvName;
        @InjectView(R.id.tv_sex)
        TextView tvSex;
        @InjectView(R.id.tv_phone)
        TextView tvPhone;
        @InjectView(R.id.tv_label)
        TextView tvLabel;
        @InjectView(R.id.tv_address)
        TextView tvAddress;
        @InjectView(R.id.iv_edit)
        ImageView ivEdit;
        private int position;

        ViewHolder(View view) {
            super(view);
            ButterKnife.inject(this, view);
            //点中一个条目后,需要将此条目的数据传递给前一个界面,展示
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (int i = 0; i < data.size(); i++) {
                        ReceiptAddressBean receiptAddressBean = data.get(i);
                        if (i == position){
                            receiptAddressBean.setSelect(1);//选中条目在数据库中的isSelect更改为1
                        }else{
                            receiptAddressBean.setSelect(0);//其余条目在数据库中的isSelect更改为0
                        }
                        //将上诉的更改,告知数据库,让数据库更新
                        receiptAddressDao.update(receiptAddressBean);
                    }
                    notifyDataSetChanged();
                    //将选中条目的对象进行数据的回传
                    Intent intent = new Intent();
                    intent.putExtra("receiptAddress",data.get(position));
                    activity.setResult(101,intent);
                    activity.finish();
                }
            });
        }
        @OnClick({R.id.iv_edit})
        public void onClick(View view){
            switch (view.getId()){
                case R.id.iv_edit:
                    Intent intent = new Intent(activity, AddAddressActivity.class);
                    intent.putExtra("receiptAddress",data.get(position));
                    activity.startActivity(intent);
                    break;
            }
        }

        public void setPosition(int position) {
            this.position = position;
        }
    }
}
