package com.xiaozhang.takeout.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by zhangxiao on 2018/3/5.
 */
public class BaseFragment extends Fragment {
    private Toast toast;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        TextView textView = new TextView(getActivity());
        textView.setText(this.getClass().getSimpleName());
        return textView;
    }

    /**
     *  显示吐司
     * @param msg
     */
    public void makeText(String msg) {
        if (toast == null) {
            toast = Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT);
        }
        toast.show();
    }
}
