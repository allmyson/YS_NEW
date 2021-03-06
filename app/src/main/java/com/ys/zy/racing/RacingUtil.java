package com.ys.zy.racing;

import com.ys.zy.racing.activity.RacingActivity;
import com.ys.zy.util.GameUtil;
import com.ys.zy.util.L;
import com.ys.zy.util.StringUtil;
import com.ys.zy.util.YS;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.ys.zy.util.GameUtil.toThreeDigit;

public class RacingUtil {
    private static List<String> nameList = new ArrayList<>();

    static {
        nameList.add("冠军");
        nameList.add("亚军");
        nameList.add("季军");
        nameList.add("第四");
        nameList.add("第五");
        nameList.add("第六");
        nameList.add("第七");
        nameList.add("第八");
        nameList.add("第九");
        nameList.add("第十");
    }

    public static String getType(String name) {
        String code = "";
        switch (name) {
            case "冠军":
                code = "1000";
                break;
            case "亚军":
                code = "1001";
                break;
            case "季军":
                code = "1002";
                break;
            case "第四":
                code = "1003";
                break;
            case "第五":
                code = "1004";
                break;
            case "第六":
                code = "1005";
                break;
            case "第七":
                code = "1006";
                break;
            case "第八":
                code = "1007";
                break;
            case "第九":
                code = "1008";
                break;
            case "第十":
                code = "1009";
                break;
        }
        return code;
    }

    public static String getNumber(int i) {
        if (i < 10) {
            return "0" + i;
        } else {
            return "" + i;
        }
    }

    public static String getNameByPosition(int position) {
        return nameList.get(position);
    }

    /**
     * 获取北京赛车当前期数  每 20 分钟 1 期，每天 9：10-23：50，共计 44 期，
     *
     * @return
     */
    public static String getCurrentBJSCPeriods() {
        if (isBJSCRunning()) {
            Calendar now = Calendar.getInstance();
            now.setTime(YS.getCurrentDate());
            int month = now.get(Calendar.MONTH) + 1;
            int day = now.get(Calendar.DAY_OF_MONTH);
            int hour = now.get(Calendar.HOUR_OF_DAY);
            int minute = now.get(Calendar.MINUTE);
            return getNumber(month) + getNumber(day) + getNumber((hour * 60 + minute - 9 * 60 - 10) / 20 + 1);
        }
        //预售
        return getNextBJSCPeriods();
    }

    /**
     * 获取北京赛车追qi后的期数
     *
     * @param qi
     * @return
     */
    public static String getBJSCPeriods(int qi) {
        if (isBJSCRunning()) {
            Calendar now = Calendar.getInstance();
            now.setTime(YS.getCurrentDate());
            int month = now.get(Calendar.MONTH) + 1;
            int day = now.get(Calendar.DAY_OF_MONTH);
            int hour = now.get(Calendar.HOUR_OF_DAY);
            int minute = now.get(Calendar.MINUTE);
            int qishu = (hour * 60 + minute - 9 * 60 - 10) / 20 + 1 + qi - 1;
            if (qishu > 44) {
                qishu = 44;
            }
            return getNumber(month) + getNumber(day) + getNumber(qishu);
        } else {
            String result = "";
            String format = "HH:mm:ss";
            SimpleDateFormat sf = new SimpleDateFormat("HH:mm:ss");
            String now = sf.format(YS.getCurrentDate());
            Date nowTime;
            try {
                nowTime = new SimpleDateFormat(format).parse(now);
                Date startTime = new SimpleDateFormat(format).parse("00:00:01");
                Date endTime = new SimpleDateFormat(format).parse("09:09:59");
                if (GameUtil.isEffectiveDate(nowTime, startTime, endTime)) {
                    L.e("系统时间在早上00:00:01到09:09:59之间.");
                    Calendar c = Calendar.getInstance();
                    c.setTime(new Date());
                    int month = c.get(Calendar.MONTH) + 1;
                    int day = c.get(Calendar.DAY_OF_MONTH);
                    if (qi > 44) {
                        qi = 44;
                    }
                    result = getNumber(month) + getNumber(day) + getNumber(qi);
                } else {
                    L.e("系统时间在早上23:50:01到23:59:59之间.");
                    Calendar c = Calendar.getInstance();
                    c.setTime(YS.getCurrentDate());
                    c.add(c.DATE, 1);//把日期往后增加一天.整数往后推,负数往前移动
                    int month = c.get(Calendar.MONTH) + 1;
                    int day = c.get(Calendar.DAY_OF_MONTH);
                    if (qi > 44) {
                        qi = 44;
                    }
                    result = getNumber(month) + getNumber(day) + getNumber(qi);
                }
            } catch (java.text.ParseException e) {
                e.printStackTrace();
            }
            return result;
        }
    }

    /**
     * 北京赛车是否正在进行
     *
     * @return
     */
    public static boolean isBJSCRunning() {
        boolean runFlag = false;
        String format = "HH:mm:ss";
        SimpleDateFormat sf = new SimpleDateFormat("HH:mm:ss");
        String now = sf.format(YS.getCurrentDate());
        Date nowTime;
        try {
            nowTime = new SimpleDateFormat(format).parse(now);
            Date startTime = new SimpleDateFormat(format).parse("09:10:00");
            Date endTime = new SimpleDateFormat(format).parse("23:50:00");
            if (GameUtil.isEffectiveDate(nowTime, startTime, endTime)) {
                runFlag = true;
                L.e("系统时间在早上9点10到下午23点50之间.");
            } else {
                runFlag = false;
                L.e("系统时间不在早上9点10到下午23点50之间.");
            }
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        return runFlag;
    }


    public static boolean isScRunning(int type) {
        if (type == RacingActivity.TYPE_BJSC) {
            return isBJSCRunning();
        } else {
            return true;
        }
    }

    /**
     * 获取1分赛车当前期数  1分钟1 期，每天1440 期，
     *
     * @return
     */
    public static String getCurrent1FSCPeriods() {
        Calendar now = Calendar.getInstance();
        now.setTime(YS.getCurrentDate());
        int month = now.get(Calendar.MONTH) + 1;
        int day = now.get(Calendar.DAY_OF_MONTH);
        int hour = now.get(Calendar.HOUR_OF_DAY);
        int minute = now.get(Calendar.MINUTE);
        return getNumber(month) + getNumber(day) + GameUtil.toFourDigit(hour * 60 + minute + 1);
    }

    /**
     * 获取一分赛车加qi后的期数
     *
     * @param qi
     * @return
     */
    public static String get1FSCPeriods(int qi) {
        Calendar now = Calendar.getInstance();
        now.setTime(YS.getCurrentDate());
        int month = now.get(Calendar.MONTH) + 1;
        int day = now.get(Calendar.DAY_OF_MONTH);
        int hour = now.get(Calendar.HOUR_OF_DAY);
        int minute = now.get(Calendar.MINUTE);
        int qishu = hour * 60 + minute + 1 + qi - 1;
        if (qishu > 1440) {
            qishu = 1440;
        }
        return getNumber(month) + getNumber(day) + GameUtil.toFourDigit(qishu);
    }

    /**
     * 获取5分赛车当前期数  5分钟1 期，每天288期，
     *
     * @return
     */
    public static String getCurrent5FSCPeriods() {
        Calendar now = Calendar.getInstance();
        now.setTime(YS.getCurrentDate());
        int month = now.get(Calendar.MONTH) + 1;
        int day = now.get(Calendar.DAY_OF_MONTH);
        int hour = now.get(Calendar.HOUR_OF_DAY);
        int minute = now.get(Calendar.MINUTE);
        return getNumber(month) + getNumber(day) + toThreeDigit((hour * 60 + minute) / 5 + 1);
    }

    /**
     * 获取5分赛车加qi期后的期数
     *
     * @return
     */
    public static String get5FSCPeriods(int qi) {
        Calendar now = Calendar.getInstance();
        now.setTime(YS.getCurrentDate());
        int month = now.get(Calendar.MONTH) + 1;
        int day = now.get(Calendar.DAY_OF_MONTH);
        int hour = now.get(Calendar.HOUR_OF_DAY);
        int minute = now.get(Calendar.MINUTE);
        int qishu = (hour * 60 + minute) / 5 + 1 + qi - 1;
        if (qishu > 288) {
            qishu = 288;
        }
        return getNumber(month) + getNumber(day) + toThreeDigit(qishu);
    }

    public static String getSCPeriods(int type, int qi) {
        String result = "";
        switch (type) {
            case RacingActivity.TYPE_BJSC:
                result = getBJSCPeriods(qi);
                break;
            case RacingActivity.TYPE_1FSC:
                result = get1FSCPeriods(qi);
                break;
            case RacingActivity.TYPE_5FSC:
                result = get5FSCPeriods(qi);
                break;
        }
        return result;
    }

    public static int getJGTime(int type) {
        int jgTime = 1;
        switch (type) {
            case RacingActivity.TYPE_BJSC:
                jgTime = 20;
                break;
            case RacingActivity.TYPE_1FSC:
                jgTime = 1;
                break;
            case RacingActivity.TYPE_5FSC:
                jgTime = 5;
                break;
        }
        return jgTime;
    }

    public static List<String> getRandomResultList(int num) {
        List<String> result = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            result.add(getNumber(num));
        }
        return result;
    }


    //获取北京赛车不在运营时间内的下一期期号
    public static String getNextBJSCPeriods() {
        String result = "";
        String format = "HH:mm:ss";
        SimpleDateFormat sf = new SimpleDateFormat("HH:mm:ss");
        String now = sf.format(YS.getCurrentDate());
        Date nowTime;
        try {
            nowTime = new SimpleDateFormat(format).parse(now);
            Date startTime = new SimpleDateFormat(format).parse("00:00:01");
            Date endTime = new SimpleDateFormat(format).parse("09:09:59");
            if (GameUtil.isEffectiveDate(nowTime, startTime, endTime)) {
                L.e("系统时间在早上00:00:01到09:09:59之间.");
                Calendar c = Calendar.getInstance();
                c.setTime(YS.getCurrentDate());
                int month = c.get(Calendar.MONTH) + 1;
                int day = c.get(Calendar.DAY_OF_MONTH);
                result = getNumber(month) + getNumber(day) + "01";
            } else {
                L.e("系统时间在早上23:50:01到23:59:59之间.");
                Calendar c = Calendar.getInstance();
                c.setTime(YS.getCurrentDate());
                c.add(c.DATE, 1);//把日期往后增加一天.整数往后推,负数往前移动
                int month = c.get(Calendar.MONTH) + 1;
                int day = c.get(Calendar.DAY_OF_MONTH);
                result = getNumber(month) + getNumber(day) + "01";
            }
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        return result;
    }


    //获取北京赛车不在运营时间内的开奖最后一期期号
    public static String getLastBJSCPeriods() {
        String result = "";
        String format = "HH:mm:ss";
        SimpleDateFormat sf = new SimpleDateFormat("HH:mm:ss");
        String now = sf.format(YS.getCurrentDate());
        Date nowTime;
        try {
            nowTime = new SimpleDateFormat(format).parse(now);
            Date startTime = new SimpleDateFormat(format).parse("00:00:01");
            Date endTime = new SimpleDateFormat(format).parse("09:09:59");
            if (GameUtil.isEffectiveDate(nowTime, startTime, endTime)) {
                L.e("系统时间在早上00:00:01到09:09:59之间.");
                Calendar c = Calendar.getInstance();
                c.setTime(YS.getCurrentDate());
                c.add(c.DATE, -1);//把日期往前减一天.整数往后推,负数往前移动
                int month = c.get(Calendar.MONTH) + 1;
                int day = c.get(Calendar.DAY_OF_MONTH);
                result = getNumber(month) + getNumber(day) + "44";
            } else {
                L.e("系统时间在早上23:10:01到23:59:59之间.");
                Calendar c = Calendar.getInstance();
                c.setTime(YS.getCurrentDate());
                int month = c.get(Calendar.MONTH) + 1;
                int day = c.get(Calendar.DAY_OF_MONTH);
                result = getNumber(month) + getNumber(day) + "44";
            }
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        return result;
    }
}
