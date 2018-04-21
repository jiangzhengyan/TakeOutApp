package com.xiaozhang.takeout.ui.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amap.api.services.core.PoiItem;
import com.xiaozhang.takeout.R;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by zhangxiao on 2018/3/23.
 */
public class SelectAddressAdapter extends RecyclerView.Adapter {
    private Activity activity;
    private List<PoiItem> data;

    public SelectAddressAdapter(Activity activity, List<PoiItem> poiItems) {
        this.activity = activity;
        this.data = poiItems;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(activity, R.layout.item_select_receipt_address, null);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((ViewHolder) holder).tvTitle.setText(data.get(position).getTitle());
        ((ViewHolder) holder).tvSnippet.setText(data.get(position).getSnippet());

        ((ViewHolder) holder).setPosition(position);
    }

    @Override
    public int getItemCount() {
        if (data != null && data.size() > 0) {
            return data.size();
        }
        return 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        @InjectView(R.id.iv)
        ImageView iv;
        @InjectView(R.id.tv_title)
        TextView tvTitle;
        @InjectView(R.id.tv_snippet)
        TextView tvSnippet;
        private int position;

        ViewHolder(View view) {
            super(view);
            ButterKnife.inject(this, view);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PoiItem poiItem = data.get(position);
                    Intent intent = new Intent();
                    intent.putExtra("title",poiItem.getTitle());
                    intent.putExtra("snippet",poiItem.getSnippet());
                    activity.setResult(101,intent);
                    activity.finish();
                }
            });
        }

        public void setPosition(int position) {
            this.position = position;
        }
    }
}
