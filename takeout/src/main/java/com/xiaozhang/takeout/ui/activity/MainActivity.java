package com.xiaozhang.takeout.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import com.xiaozhang.takeout.R;
import com.xiaozhang.takeout.ui.fragment.HomeFragment;
import com.xiaozhang.takeout.ui.fragment.MoreFragment;
import com.xiaozhang.takeout.ui.fragment.OrderFragment;
import com.xiaozhang.takeout.ui.fragment.UserFragment;
import java.util.ArrayList;
import butterknife.ButterKnife;
import butterknife.InjectView;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    @InjectView(R.id.main_fragment_container)
    FrameLayout mainFragmentContainer;

    @InjectView(R.id.main_bottome_switcher_container)
    LinearLayout mainBottomeSwitcherContainer;
    private ArrayList<Fragment> fragmentArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        //1.点击底部4个选项卡,目的切换顶部帧布局指向的Fragment,准备4个Fragment
        initFragment();
        //2.初始化底部选项卡的点击事件
        initClick();
        //3.默认选中索引位置为0的界面
        View view = mainBottomeSwitcherContainer.getChildAt(0);
        onClick(view);
    }

    private void initClick() {
        //找到底部选项卡的父容器,然后给每一个孩子节点设置点击事件
        int childCount = mainBottomeSwitcherContainer.getChildCount();
        for (int i = 0; i < childCount; i++) {
            FrameLayout frameLayout = (FrameLayout) mainBottomeSwitcherContainer.getChildAt(i);
            frameLayout.setOnClickListener(this);
        }
    }

    private void initFragment() {
        fragmentArrayList = new ArrayList<>();

        fragmentArrayList.add(new HomeFragment());
        fragmentArrayList.add(new OrderFragment());
        fragmentArrayList.add(new UserFragment());
        fragmentArrayList.add(new MoreFragment());
    }

    @Override
    public void onClick(View v) {
        //提供点中控所在线性布局容器中的索引位置
        int indexOfChild = mainBottomeSwitcherContainer.indexOfChild(v);
        //1.切换选中FrameLayout内部孩子节点的颜色
        changeUI(indexOfChild);
        //2.切换Fragment(首页,订单,用户,更多)
        changeFragment(indexOfChild);
    }

    private void changeFragment(int indexOfChild) {
        //根据索引位置,切换fragment
        getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment_container,
                fragmentArrayList.get(indexOfChild)).commit();
    }

    /**
     * @param indexOfChild  点中线性布局容器孩子节点的索引位置
     */
    private void changeUI(int indexOfChild) {
        for (int i = 0; i < mainBottomeSwitcherContainer.getChildCount(); i++) {
            View view = mainBottomeSwitcherContainer.getChildAt(i);
            if (i == indexOfChild){
                //找到了选中的view对象,view孩子节点都需要变蓝色,将view中的孩子节点都设置为不可用
                setEnable(view,false);
                //view          TextView  ImageView
                //viewGroup     容器  线性布局  相对布局  帧布局
            }else{
                //没找到选中的view对象,view孩子节点都不需要变色,将view中的孩子节点都设置为可用
                setEnable(view,true);
            }
        }
    }

    /**
     * @param view  修改内部孩子节点状态的空间
     * @param b     修改为状态  false 不可用   true 可用
     */
    private void setEnable(View view, boolean b) {
        view.setEnabled(b);
        //view是否能够转换成viewGroup判断
        if (view instanceof ViewGroup){
            //view转换成viewGroup后孩子节点的总数
            int childCount = ((ViewGroup) view).getChildCount();
            for (int i = 0; i < childCount; i++) {
                View viewChild = ((ViewGroup) view).getChildAt(i);
//                viewChild.setEnabled(b);
                setEnable(viewChild,b);
            }
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            exit();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
