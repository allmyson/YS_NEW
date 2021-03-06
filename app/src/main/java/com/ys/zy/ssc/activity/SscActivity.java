package com.ys.zy.ssc.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.yanzhenjie.nohttp.rest.Response;
import com.ys.zy.R;
import com.ys.zy.api.FunctionApi;
import com.ys.zy.base.BaseActivity;
import com.ys.zy.common.TZJLFragment;
import com.ys.zy.dialog.DialogUtil;
import com.ys.zy.dialog.GameFragment;
import com.ys.zy.dialog.PlayFragment;
import com.ys.zy.fast3.activity.Fast3Activity;
import com.ys.zy.http.HttpListener;
import com.ys.zy.racing.activity.RacingActivity;
import com.ys.zy.racing.fragment.RacingTZFragment;
import com.ys.zy.roulette.activity.RouletteActivity;
import com.ys.zy.sp.User;
import com.ys.zy.sp.UserSP;
import com.ys.zy.ssc.SscUtil;
import com.ys.zy.ssc.fragment.SscTZFragment;
import com.ys.zy.ssc.fragment.SscTZJLFragment;
import com.ys.zy.ttz.activity.TtzActivity;
import com.ys.zy.util.HttpUtil;
import com.ys.zy.util.L;
import com.ys.zy.util.StringUtil;
import com.ys.zy.util.YS;
import com.ys.zy.winner.activity.WinnerActivity;

import java.util.ArrayList;
import java.util.List;

//时时彩-1分彩
public class SscActivity extends BaseActivity {
    public static final int TYPE_SSC = 1000;//时时彩
    public static final int TYPE_1FC = 1001;//1分彩
    private int type = TYPE_SSC;
    public static final int PLAY_DWD = 100;//定位胆
    public static final int PLAY_DXDS = 101;//大小单双
    public static final int PLAY_H2X = 102;//后二星
    public static final int PLAY_WX = 103;//五星直选
    private int play = PLAY_DWD;
    private RelativeLayout backRL;
    private LinearLayout gameLL;
    private TextView gameTV;

    private RelativeLayout tzRL, tzjlRL;
    private TextView tzTV, tzjlTV;
    private View tzView, tzjlView;
    public static final int TYPE_TZ = 100;
    public static final int TYPE_JL = 101;
    private int currentType = TYPE_TZ;
    private Fragment tzFragment, jlFragment;
    private TextView moneyTV;
    private ImageView showOrHideIV;
    private boolean isShow = false;
    private String money = "0.00";
    private ImageView gameMoreIV;
    private boolean isShowMoreGame = false;//是否显示其他游戏
    private LinearLayout playLL;
    private TextView playTV;
    private ImageView playIV;
    private String gameNo;
    private String userId;

    @Override
    public int getLayoutId() {
        return R.layout.activity_ssc;
    }

    @Override
    public void initView() {
        setBarColor2("#f7f7f7",true);
        regist();
        moneyTV = getView(R.id.tv_money);
        moneyTV.setText(money);
        showOrHideIV = getView(R.id.iv_showOrHide);
        showOrHideIV.setOnClickListener(this);
        manager = getSupportFragmentManager();
        type = getIntent().getIntExtra("type", TYPE_SSC);
        initFragment();
        backRL = getView(R.id.rl_back);
        backRL.setOnClickListener(this);
        gameLL = getView(R.id.ll_game);
        gameLL.setOnClickListener(this);
        gameTV = getView(R.id.tv_gameName);
        gameMoreIV = getView(R.id.iv_gameMore);
        switch (type) {
            case TYPE_SSC:
                gameTV.setText("时时彩");
                break;
            case TYPE_1FC:
                gameTV.setText("1分彩");
                break;
        }
        tzRL = getView(R.id.rl_wytz);
        tzjlRL = getView(R.id.rl_tzjl);
        tzTV = getView(R.id.tv_wytz);
        tzjlTV = getView(R.id.tv_tzjl);
        tzView = getView(R.id.view_wytz);
        tzjlView = getView(R.id.view_tzjl);
        tzRL.setOnClickListener(this);
        tzjlRL.setOnClickListener(this);
        showFragment(tzFragment);
        playLL = getView(R.id.ll_play);
        playTV = getView(R.id.tv_play);
        playLL.setOnClickListener(this);
        playIV = getView(R.id.iv_play);
        playIV.setColorFilter(Color.parseColor("#dd2230"));
        if (!isShow) {
            showOrHideIV.setImageResource(R.mipmap.btn_hide);
            moneyTV.setText(StringUtil.changeToX(money));
        } else {
            showOrHideIV.setImageResource(R.mipmap.btn_show);
            moneyTV.setText(money);
        }
        userId = UserSP.getUserId(mContext);
    }

    @Override
    public void getData() {
        HttpUtil.getUserInfoById(mContext, userId, new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) {
                User user = new Gson().fromJson(response.get(), User.class);
                if (user != null && YS.SUCCESE.equals(user.code) && user.data != null) {
                    money = StringUtil.StringToDoubleStr(user.data.balance);
//                    moneyTV.setText(money);
                    if (!isShow) {
                        showOrHideIV.setImageResource(R.mipmap.btn_hide);
                        moneyTV.setText(StringUtil.changeToX(money));
                    } else {
                        showOrHideIV.setImageResource(R.mipmap.btn_show);
                        moneyTV.setText(money);
                    }
                }
            }

            @Override
            public void onFailed(int what, Response<String> response) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_back:
                finish();
                break;
            case R.id.rl_wytz:
                if (currentType == TYPE_JL) {
                    currentType = TYPE_TZ;
                    tzTV.setTextColor(Color.parseColor("#dd2230"));
                    tzView.setVisibility(View.VISIBLE);
                    tzjlTV.setTextColor(Color.parseColor("#a5a5a5"));
                    tzjlView.setVisibility(View.INVISIBLE);
                    showFragment(tzFragment);
                }
                break;
            case R.id.rl_tzjl:
                if (currentType == TYPE_TZ) {
                    currentType = TYPE_JL;
                    tzjlTV.setTextColor(Color.parseColor("#dd2230"));
                    tzjlView.setVisibility(View.VISIBLE);
                    tzTV.setTextColor(Color.parseColor("#a5a5a5"));
                    tzView.setVisibility(View.INVISIBLE);
                    showFragment(jlFragment);
                }
                break;

            case R.id.iv_showOrHide:
                if (isShow) {
                    isShow = false;
                    showOrHideIV.setImageResource(R.mipmap.btn_hide);
                    moneyTV.setText(StringUtil.changeToX(money));
                } else {
                    isShow = true;
                    showOrHideIV.setImageResource(R.mipmap.btn_show);
                    moneyTV.setText(money);
                }
                break;
            case R.id.ll_game:
                gameMoreIV.setImageResource(R.mipmap.bottom_btn_more);
                DialogUtil.showGameFragment(mContext, new GameFragment.ClickListener() {
                    @Override
                    public void click(int position) {
                        gameMoreIV.setImageResource(R.mipmap.top_btn_more);
                        DialogUtil.removeDialog(mContext);
                        switch (position) {
                            case 0:
                                //轮盘
                                startActivity(new Intent(mContext, RouletteActivity.class));
                                finish();
                                break;
                            case 1:
                                //1分快3
                                Fast3Activity.intentToFast3(mContext, Fast3Activity.TYPE_1FK3);
                                finish();
                                break;
                            case 2:
                                //推筒子
                                startActivity(new Intent(mContext, TtzActivity.class));
                                finish();
                                break;
                            case 3:
                                //5分快3
                                Fast3Activity.intentToFast3(mContext, Fast3Activity.TYPE_5FK3);
                                finish();
                                break;
                            case 4:
                                //最后胜利者
                                WinnerActivity.intentToWinner(mContext);
                                finish();
                                break;
                            case 5:
                                //江苏快3
                                Fast3Activity.intentToFast3(mContext, Fast3Activity.TYPE_JSK3);
                                finish();
                                break;
                            case 6:
                                //分分彩
                                if (type != TYPE_1FC) {
                                    SscActivity.intentToSSC(mContext, TYPE_1FC);
                                    finish();
                                }
                                break;
                            case 7:
                                //北京赛车
                                RacingActivity.intentToRacing(mContext, RacingActivity.TYPE_BJSC);
                                finish();
                                break;
                            case 8:
                                //重庆时时彩
                                if (type != TYPE_SSC) {
                                    SscActivity.intentToSSC(mContext, TYPE_SSC);
                                    finish();
                                }
                                break;
                            case 9:
                                //1分赛车
                                RacingActivity.intentToRacing(mContext, RacingActivity.TYPE_1FSC);
                                finish();
                                break;
                            case 10:
                                //更多
                                break;
                            case 11:
                                //5分赛车
                                RacingActivity.intentToRacing(mContext, RacingActivity.TYPE_5FSC);
                                finish();
                                break;
                        }
                    }
                }, new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        gameMoreIV.setImageResource(R.mipmap.top_btn_more);
                    }
                });
                break;
            case R.id.ll_play:
                playIV.setImageResource(R.mipmap.bottom_btn_more);
                DialogUtil.showPlayDialog(mContext, getPlayList(), new PlayFragment.ClickListener() {
                    @Override
                    public void click(String name) {
                        playTV.setText(name);
                        if ("定位胆".equals(name)) {
                            play = PLAY_DWD;
                        } else if ("大小单双".equals(name)) {
                            play = PLAY_DXDS;
                        } else if ("后二星组选".equals(name)) {
                            play = PLAY_H2X;
                        } else if ("五星直选".equals(name)) {
                            play = PLAY_WX;
                        }
                        playIV.setImageResource(R.mipmap.top_btn_red_more_);
                        DialogUtil.removeDialog(mContext);
                        ((SscTZFragment) tzFragment).showFragment(name);
                        ((SscTZFragment) tzFragment).clearData();
                        ((SscTZFragment) tzFragment).setPlay(play);
                        ((TZJLFragment) jlFragment).setPlay(play);
                    }
                }, new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        playIV.setImageResource(R.mipmap.top_btn_red_more_);
                    }
                });
                break;
        }
    }

    public static void intentToSSC(Context context, int type) {
        Intent intent = new Intent(context, SscActivity.class);
        intent.putExtra("type", type);
        context.startActivity(intent);
    }

    private Fragment currentFragment;
    private FragmentManager manager;

    private void showFragment(Fragment fragment) {
        try {
            if (currentFragment != fragment) {
                FragmentTransaction transaction = manager.beginTransaction();
                if (currentFragment != null) {
                    transaction.hide(currentFragment);
                }
                currentFragment = fragment;
                if (!fragment.isAdded()) {
                    transaction.add(R.id.fl_, fragment).show(fragment).commitAllowingStateLoss();
                } else {
                    transaction.show(fragment).commitAllowingStateLoss();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initFragment() {
        tzFragment = SscTZFragment.newInstance(type, play);
        if (type == TYPE_SSC) {
            jlFragment = TZJLFragment.newInstance(YS.CODE_SSC, play);
        } else {
            jlFragment = TZJLFragment.newInstance(YS.CODE_1FC, play);
        }
//        jlFragment = SscTZJLFragment.newInstance(type, play);
    }

    public double getMoney() {
        return StringUtil.StringToDoubleTwo(money);
    }

    private List<String> getPlayList() {
        List<String> list = new ArrayList<>();
        list.add("定位胆");
        list.add("大小单双");
        list.add("后二星组选");
        list.add("五星直选");
        return list;
    }

    public String getGameName() {
        String gameName = "时时彩";
        switch (type) {
            case TYPE_SSC:
                gameName = "时时彩";
                break;
            case TYPE_1FC:
                gameName = "1分彩";
                break;
        }
        switch (play) {
            case PLAY_DWD:
                gameName += "(定位胆)";
                break;
            case PLAY_DXDS:
                gameName += "(大小单双)";
                break;
            case PLAY_H2X:
                gameName += "(后二星组选)";
                break;
            case PLAY_WX:
                gameName += "(五星直选)";
                break;
        }
        return gameName;
    }

    public String getGameNo() {
        String gameNo = "";
        switch (type) {
            case TYPE_SSC:
                gameNo = SscUtil.getCurrentSscPeriods();
                break;
            case TYPE_1FC:
                gameNo = SscUtil.getCurrent1FCPeriods();
                break;
            default:
                gameNo = "";
                break;
        }
        return gameNo;
    }

    private TZSuccReceiver tzSuccReceiver;

    private class TZSuccReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            L.e("收到TZFragment的投注成功广播！");
            if(jlFragment.isAdded()) {
                ((TZJLFragment) jlFragment).reload();
            }
        }
    }
    private void regist() {
        IntentFilter intentFilter = new IntentFilter(YS.ACTION_TZ_SUCCESS);
        tzSuccReceiver = new TZSuccReceiver();
        registerReceiver(tzSuccReceiver, intentFilter);
    }

    private void unRegist() {
        if (tzSuccReceiver != null) {
            unregisterReceiver(tzSuccReceiver);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unRegist();
    }
}
