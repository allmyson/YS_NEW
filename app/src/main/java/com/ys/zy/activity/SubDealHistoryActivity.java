package com.ys.zy.activity;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.yanzhenjie.nohttp.rest.Response;
import com.ys.zy.R;
import com.ys.zy.adapter.SubDealHistoryAdapter;
import com.ys.zy.base.BaseActivity;
import com.ys.zy.bean.MsgBean;
import com.ys.zy.bean.SubJYJL;
import com.ys.zy.http.HttpListener;
import com.ys.zy.sp.UserSP;
import com.ys.zy.ui.BlankView;
import com.ys.zy.ui.NoNetView;
import com.ys.zy.util.DateUtil;
import com.ys.zy.util.HttpUtil;
import com.ys.zy.util.NetWorkUtil;
import com.ys.zy.util.YS;

import java.util.ArrayList;
import java.util.List;

public class SubDealHistoryActivity extends BaseActivity implements NoNetView.ClickListener {

    private ListView lv;
    private List<SubJYJL.DataBeanX.DataBean> list;
    private SubDealHistoryAdapter adapter;
    private String userId;
    private TextView dayTV;
    private NoNetView noNetView;
    private BlankView blankView;
    private LinearLayout dataLL;

    @Override
    public int getLayoutId() {
        return R.layout.activity_sub_deal_history;
    }

    @Override
    public void initView() {
        setBarColor("#ededed");
        titleView.setText("下级交易记录");
        dataLL = getView(R.id.ll_data);
        noNetView = getView(R.id.nnv_);
        blankView = getView(R.id.bv_);
        blankView.setImage(R.mipmap.blank_inf_img).setText("暂无记录");
        noNetView.setClickListener(this);
        dayTV = getView(R.id.tv_day);
        dayTV.setText(DateUtil.longToYMD(System.currentTimeMillis()));
        lv = getView(R.id.lv_);
        list = new ArrayList<>();
        adapter = new SubDealHistoryAdapter(mContext, list, R.layout.item_sub_manage);
        lv.setAdapter(adapter);
        userId = UserSP.getUserId(mContext);
    }

    @Override
    public void getData() {
        if (NetWorkUtil.isNetworkAvailable(mContext)) {
            noNetView.setVisibility(View.GONE);
            dataLL.setVisibility(View.VISIBLE);
            HttpUtil.getSubJYJL(mContext, userId, DateUtil.getCurrentDayStartStr(), DateUtil.getCurrentDayEndStr(), new HttpListener<String>() {
                @Override
                public void onSucceed(int what, Response<String> response) {
                    list.clear();
                    SubJYJL subJYJL = new Gson().fromJson(response.get(), SubJYJL.class);
                    if (subJYJL != null) {
                        if (YS.SUCCESE.equals(subJYJL.code) && subJYJL.data != null && subJYJL.data.data != null && subJYJL.data.data.size() > 0) {
                            list.addAll(subJYJL.data.data);
                        }
                    } else {
                        show(YS.HTTP_TIP);
                    }
                    adapter.refresh(list);
                    if (adapter.getCount() > 0) {
                        blankView.setVisibility(View.GONE);
                    } else {
                        blankView.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onFailed(int what, Response<String> response) {
                    blankView.setVisibility(View.GONE);
                }
            });
        } else {
            noNetView.setVisibility(View.VISIBLE);
            blankView.setVisibility(View.GONE);
            dataLL.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void reload() {
        getData();
    }
}
