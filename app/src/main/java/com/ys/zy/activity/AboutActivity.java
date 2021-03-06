package com.ys.zy.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ys.zy.R;
import com.ys.zy.base.BaseActivity;
import com.ys.zy.dialog.DialogUtil;
import com.ys.zy.dialog.ExitFragment;
import com.ys.zy.dialog.ShareFragment;
import com.ys.zy.util.ActivityUtil;
import com.ys.zy.util.AppUtil;
import com.ys.zy.util.SPUtil;

public class AboutActivity extends BaseActivity {
    private LinearLayout shareLL, updateLL, exitLL;
    private TextView versionNameTV;

    @Override
    public int getLayoutId() {
        return R.layout.activity_about;
    }

    @Override
    public void initView() {
        setBarColor("#ededed");
        titleView.setText("关于");
        shareLL = getView(R.id.ll_shareApp);
        updateLL = getView(R.id.ll_update);
        exitLL = getView(R.id.ll_exit);
        shareLL.setOnClickListener(this);
        updateLL.setOnClickListener(this);
        exitLL.setOnClickListener(this);
        versionNameTV = getView(R.id.tv_versionName);
        versionNameTV.setText("Version " + AppUtil.getVersionName(mContext, AppUtil.getPackageName(mContext)));
    }

    @Override
    public void getData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_shareApp:
                DialogUtil.showShare(mContext, new ShareFragment.ClickListener() {
                    @Override
                    public void shareToWX() {

                    }

                    @Override
                    public void saveToLocal() {

                    }
                });
                break;
            case R.id.ll_update:
                startActivity(new Intent(mContext, UpdateInfoActivity.class));
                break;
            case R.id.ll_exit:
                DialogUtil.showExit(mContext, new ExitFragment.ClickListener() {
                    @Override
                    public void sure() {
                        SPUtil.clear(mContext);
                        ActivityUtil.finish();
                        startActivity(new Intent(mContext, LoginActivity.class));
                    }
                });
                break;
        }
    }
}
