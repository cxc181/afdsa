package com.yuqian.itax.util.util;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: IntervalUtil
 * @Description: 判断某个值是否在某个区间工具类，返回booblean值
 * @Author: yejian
 * @Date: Created in 2019/12/16
 * @Version: 1.0
 * @Modified By:
 */
public class IntervalUtil {

    /**
     * 判断data_value是否在interval区间范围内
     *
     * @param data_value 数值类型的
     * @param interval   正常的数学区间，包括无穷大等，如：(1,3)、>5%、(-∞,6]、(125%,135%)U(70%,80%)
     * @return true：表示data_value在区间interval范围内，false：表示data_value不在区间interval范围内
     */
    public static boolean isInTheInterval(String data_value, String interval) {
        //将区间和data_value转化为可计算的表达式
        String formula = getFormulaByAllInterval(data_value, interval, "||");
        ScriptEngine jse = new ScriptEngineManager().getEngineByName("JavaScript");
        try {
            //计算表达式
            return (Boolean) jse.eval(formula);
        } catch (Exception t) {
            return false;
        }
    }

    /**
     * 将所有阀值区间转化为公式：如
     * [75,80)   =》                   date_value < 80 && date_value >= 75
     * (125%,135%)U(70%,80%)   =》     (date_value < 1.35 && date_value > 1.25) || (date_value < 0.8 && date_value > 0.7)
     *
     * @param date_value
     * @param interval   形式如：(125%,135%)U(70%,80%)
     * @param connector  连接符 如：") || ("
     */
    private static String getFormulaByAllInterval(String date_value, String interval, String connector) {
        StringBuffer buff = new StringBuffer();
        for (String limit : interval.split("U")) {//如：（125%,135%）U (70%,80%)
            buff.append("(").append(getFormulaByInterval(date_value, limit, " && ")).append(")").append(connector);
        }
        String allLimitInvel = buff.toString();
        int index = allLimitInvel.lastIndexOf(connector);
        allLimitInvel = allLimitInvel.substring(0, index);
        return allLimitInvel;
    }

    /**
     * 将整个阀值区间转化为公式：如
     * 145)      =》         date_value < 145
     * [75,80)   =》        date_value < 80 && date_value >= 75
     *
     * @param date_value
     * @param interval   形式如：145)、[75,80)
     * @param connector  连接符 如：&&
     */
    private static String getFormulaByInterval(String date_value, String interval, String connector) {
        StringBuffer buff = new StringBuffer();
        for (String halfInterval : interval.split(",")) {//如：[75,80)、≥80
            buff.append(getFormulaByHalfInterval(halfInterval, date_value)).append(connector);
        }
        String limitInvel = buff.toString();
        int index = limitInvel.lastIndexOf(connector);
        limitInvel = limitInvel.substring(0, index);
        return limitInvel;
    }

    /**
     * 将半个阀值区间转化为公式：如
     * 145)      =》         date_value < 145
     * ≥80%      =》         date_value >= 0.8
     * [130      =》         date_value >= 130
     * <80%     =》         date_value < 0.8
     *
     * @param halfInterval 形式如：145)、≥80%、[130、<80%
     * @param date_value
     * @return date_value < 145
     */
    private static String getFormulaByHalfInterval(String halfInterval, String date_value) {
        halfInterval = halfInterval.trim();
        if (halfInterval.contains("∞")) {//包含无穷大则不需要公式
            return "1 == 1";
        }
        StringBuffer formula = new StringBuffer();
        String data = "";
        String opera = "";
        if (halfInterval.matches("^([<>≤≥\\[\\(]{1}(-?\\d+.?\\d*\\%?))$")) {//表示判断方向（如>）在前面 如：≥80%
            opera = halfInterval.substring(0, 1);
            data = halfInterval.substring(1);
        } else {//[130、145)
            opera = halfInterval.substring(halfInterval.length() - 1);
            data = halfInterval.substring(0, halfInterval.length() - 1);
        }
        double value = dealPercent(data);
        formula.append(date_value).append(" ").append(opera).append(" ").append(value);
        String a = formula.toString();
        //转化特定字符
        return a.replace("[", ">=").replace("(", ">").replace("]", "<=").replace(")", "<").replace("≤", "<=").replace("≥", ">=");
    }

    /**
     * 去除百分号，转为小数
     *
     * @param str 可能含百分号的数字
     * @return
     */
    public static double dealPercent(String str) {
        double d = 0.0;
        if (str.contains("%")) {
            str = str.substring(0, str.length() - 1);
            d = Double.parseDouble(str) / 100;
        } else {
            d = Double.parseDouble(str);
        }
        return d;
    }

    /**
     * @param cycNum    起始周期累积量
     * @param useNum    使用量
     * @param charge    阶梯分界    100 200
     * @param price     价格信息    1,1.2,2
     * @return
     */
    public static BigDecimal getMoney(BigDecimal cycNum, BigDecimal useNum, List<BigDecimal> charge, List<BigDecimal> price){
        //System.out.println(cycNum.add(useNum));
        //System.out.println(getMoney(cycNum.add(useNum), price, charge));
        //System.out.println(getMoney(cycNum,price,charge));
        return getMoney(cycNum.add(useNum), charge, price).subtract(getMoney(cycNum, charge, price));
    }

    /**
     * 获得0到指定量之间的金额
     *
     * @param num
     * @param charge 区间
     * @param price  价格
     * @return
     */
    public static BigDecimal getMoney(BigDecimal num, List<BigDecimal> charge, List<BigDecimal> price) {
        // 区间初始化最低
        BigDecimal money = new BigDecimal("0");
        List<BigDecimal> chargeList = new LinkedList<BigDecimal>();
        chargeList.addAll(charge);
        chargeList.add(0, new BigDecimal("0"));
        chargeList.add(new BigDecimal(Integer.MAX_VALUE));

        //i用于确定当前在哪个区间
        for (int i = 0; i < chargeList.size(); i++) {
            if (num.compareTo(chargeList.get(i)) >= 0 && num.compareTo(chargeList.get(i + 1)) < 0) {
                //找到指定的区间了
                for (int j = 0; j <= i; j++) {
                    if (j != i) {
                        //防止字节溢出先提前处理一下价格
                        money = money.add((chargeList.get(j + 1).subtract(chargeList.get(j))).multiply(price.get(j)));
                    } else if(chargeList.get(j + 1).intValue() != Integer.MAX_VALUE) {
                        //代表使用量未超岀最大阶梯设置的区间，此时使用阶梯计费模板计费
                        money = money.add((num.subtract(chargeList.get(j))).multiply(price.get(j)));
                    }else {
                        //代表使用量已经超出最大阶梯设置的区间，此时使用最后一个模板计费
                        money = money.add((num.subtract(chargeList.get(j))).multiply(price.get(j - 1)));
                    }
                }
                break;
            }
        }
        return money;
    }

    /**
     * 获得0到指定量之间的金额
     *
     * @param num
     * @param charge 区间
     * @param price  价格
     * @return Map
     */
    public static Map<String,Object> getMoneyMap(BigDecimal num, List<BigDecimal> charge, List<BigDecimal> price) {
        Map<String,Object> result = new HashMap<String,Object>();
        // 区间初始化最低
        BigDecimal money = new BigDecimal("0");
        List<BigDecimal> chargeList = new LinkedList<BigDecimal>();
        chargeList.addAll(charge);
        chargeList.add(0, new BigDecimal("0"));
        chargeList.add(new BigDecimal(Integer.MAX_VALUE));

        //i用于确定当前在哪个区间
        for (int i = 0; i < chargeList.size(); i++) {
            if (num.compareTo(chargeList.get(i)) >= 0 && num.compareTo(chargeList.get(i + 1)) < 0) {
                //找到指定的区间了
                for (int j = 0; j <= i; j++) {
                    if (j != i) {
                        //防止字节溢出先提前处理一下价格
                        money = money.add((chargeList.get(j + 1).subtract(chargeList.get(j))).multiply(price.get(j)));
                    } else if(chargeList.get(j + 1).intValue() != Integer.MAX_VALUE) {
                        //代表使用量未超岀最大阶梯设置的区间，此时使用阶梯计费模板计费
                        money = money.add((num.subtract(chargeList.get(j))).multiply(price.get(j)));
                        if(chargeList.get(j).intValue() == 0){
                            result.put("rate",price.get(j));
                        }else if(IntervalUtil.isInTheInterval(num.toString(),"["+chargeList.get(j -1).toString()+","+chargeList.get(j).toString()+"]")){
                            result.put("rate",price.get(j -1));
                        }else if(IntervalUtil.isInTheInterval(num.toString(),"["+chargeList.get(j).toString()+","+chargeList.get(j + 1).toString()+"]")){
                            result.put("rate",price.get(j));
                        }
                    }else {
                        //代表使用量已经超出最大阶梯设置的区间，此时使用最后一个模板计费
                        money = money.add((num.subtract(chargeList.get(j))).multiply(price.get(j - 1)));
                        result.put("rate",price.get(j - 1));
                    }
                }
                break;
            }
        }
        result.put("taxAmount",money.setScale( 0, BigDecimal.ROUND_UP ));//向上取整
        return result;
    }

    /**
     * 获得0到指定量之间的金额
     *
     * @param num
     * @param charge 区间
     * @param price  价格
     * @return Map
     */
    public static BigDecimal getMoneyRate(BigDecimal num, List<BigDecimal> charge, List<BigDecimal> price) {
        BigDecimal rate = new BigDecimal(0);
        // 区间初始化最低
        BigDecimal money = new BigDecimal("0");
        List<BigDecimal> chargeList = new LinkedList<BigDecimal>();
        chargeList.addAll(charge);
        chargeList.add(0, new BigDecimal("0"));
        chargeList.add(new BigDecimal(Integer.MAX_VALUE));

        //i用于确定当前在哪个区间
        for (int i = 0; i < chargeList.size(); i++) {
            if (num.compareTo(chargeList.get(i)) >= 0 && num.compareTo(chargeList.get(i + 1)) < 0) {
                //找到指定的区间了
                for (int j = 0; j <= i; j++) {
                    if (j != i) {
                        //防止字节溢出先提前处理一下价格
                        money = money.add((chargeList.get(j + 1).subtract(chargeList.get(j))).multiply(price.get(j)));
                    } else if(chargeList.get(j + 1).intValue() != Integer.MAX_VALUE) {
                        //代表使用量未超岀最大阶梯设置的区间，此时使用阶梯计费模板计费
                        money = money.add((num.subtract(chargeList.get(j))).multiply(price.get(j)));
                        if(chargeList.get(j).intValue() == 0){
                            rate = price.get(j);
                        }else if(IntervalUtil.isInTheInterval(num.toString(),"["+chargeList.get(j -1).toString()+","+chargeList.get(j).toString()+"]")){
                            rate = price.get(j -1);
                        }else if(IntervalUtil.isInTheInterval(num.toString(),"["+chargeList.get(j).toString()+","+chargeList.get(j + 1).toString()+"]")){
                            rate = price.get(j);
                        }
                    }else {
                        //代表使用量已经超出最大阶梯设置的区间，此时使用最后一个模板计费
                        money = money.add((num.subtract(chargeList.get(j))).multiply(price.get(j - 1)));
                        rate = price.get(j - 1);
                    }
                }
                break;
            }
        }
        return rate;
    }

    public static void main(String[] args) {
        List<BigDecimal> vol = new LinkedList<BigDecimal>();
        vol.add(new BigDecimal("50000"));
        vol.add(new BigDecimal("100000"));
        vol.add(new BigDecimal("150000"));
        List<BigDecimal> price = new LinkedList<BigDecimal>();
        price.add(new BigDecimal("0.01"));
        price.add(new BigDecimal("0.02"));
        price.add(new BigDecimal("0.03"));
        //BigDecimal cycNum = new BigDecimal("10000");
        //BigDecimal useNum = new BigDecimal("10000");
        //System.out.println(getMoney(cycNum, useNum, vol, price));
        BigDecimal useNum = new BigDecimal("10");
        Map<String,Object> result = getMoneyMap(useNum, vol, price);
        System.out.println(result);
    }


}
