package com.ys.zy.racing.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.google.gson.Gson;
import com.ys.zy.R;
import com.ys.zy.adapter.CommonAdapter;
import com.ys.zy.adapter.ViewHolder;
import com.ys.zy.racing.RacingUtil;
import com.ys.zy.util.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DwdAdapter extends CommonAdapter<String> {
    private List<List<Boolean>> list;
    private int numberSize = 10;
    private ChangeListener changeListener;

    private void initData() {
        list = new ArrayList<>();
        if (mDatas != null && mDatas.size() > 0) {
            for (int i = 0; i < mDatas.size(); i++) {
                List<Boolean> ll = new ArrayList<>();
                for (int j = 0; j < numberSize; j++) {
                    ll.add(false);
                }
                list.add(ll);
            }
        }
    }


    public DwdAdapter(Context context, List<String> mDatas, int itemLayoutId) {
        super(context, mDatas, itemLayoutId);
        initData();
    }

    public void clear() {
        initData();
        notifyDataSetChanged();
    }

    @Override
    public void convert(ViewHolder helper, String item, final int position) {
        helper.setText(R.id.tv_, StringUtil.valueOf(item));
        GridView gv = helper.getView(R.id.gv_);
        gv.setSelector(new ColorDrawable(Color.TRANSPARENT));// 去掉默认点击背景
        final NumberAdapter numberAdapter = new NumberAdapter(mContext, getNumberList(), R.layout
                .item_number);
        numberAdapter.setList(list.get(position));
        gv.setAdapter(numberAdapter);
        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int p, long id) {
                if (list.get(position).get(p)) {
                    list.get(position).set(p, false);
                    numberAdapter.cancelChooseOne(p);
                } else {
                    numberAdapter.chooseOne(p);
                    list.get(position).set(p, true);
                }
                callback();
            }
        });
    }

    private List<Integer> getNumberList() {
        List<Integer> numberList = new ArrayList<>();
        for (int i = 1; i < 11; i++) {
            numberList.add(i);
        }
        return numberList;
    }

    public interface ChangeListener {
        void getData(int tzNum, String showResult);
    }

    public ChangeListener getChangeListener() {
        return changeListener;
    }

    public void setChangeListener(ChangeListener changeListener) {
        this.changeListener = changeListener;
    }

    public void callback() {
        if (changeListener != null) {
            changeListener.getData(getTZNum(), getShowResult());
        }
    }


    //是否可以投注
    public boolean canTZ() {
        return getTZNum() > 0;
    }

    public int getTZNum() {
        int num = 0;
        for (int i = 0; i < list.size(); i++) {
            for (int j = 0; j < list.get(i).size(); j++) {
                if (list.get(i).get(j)) {
                    num++;
                }
            }
        }
        return num;
    }

    /**
     * 投注内容显示结果
     * 02 03,03,03,03,-,-,-,-,-,-
     */
    public String getShowResult() {
        String result = "";
        for (int i = 0; i < list.size(); i++) {
            String x = "";
            List<String> data = new ArrayList<>();
            for (int j = 0; j < list.get(i).size(); j++) {
                if (list.get(i).get(j)) {
                    data.add(RacingUtil.getNumber(j + 1));
                }
            }
            if (data.size() == 0) {
                x = "-";
            } else if (data.size() == 1) {
                x = data.get(0);
            } else {
                for (int j = 0; j < data.size(); j++) {
                    if (j == data.size() - 1) {
                        x += data.get(j);
                    } else {
                        x += (data.get(j) + " ");
                    }
                }
            }
            if (i == list.size() - 1) {
                result += x;
            } else {
                result += x + ",";
            }
        }
        return result;
    }

    /**
     * 得到投注内容
     *
     * @return
     */
    public String getJsonResult() {
        String json = "";
        List<Map<String,String>> tzList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            for (int j = 0; j < list.get(i).size(); j++) {
                if (list.get(i).get(j)) {
                    Map<String, String> map = new HashMap<>();
                    map.put("betsNum", "" + (j + 1));
                    map.put("bit", RacingUtil.getType(mDatas.get(i)));
                    tzList.add(map);
                }
            }
        }
        json = new Gson().toJson(tzList);
        return json;
    }


}
