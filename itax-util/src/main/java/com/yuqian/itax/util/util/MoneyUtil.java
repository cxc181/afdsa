package com.yuqian.itax.util.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @ClassName: MoneyUtil
 * @Description: 金额工具类,主要是金额的格式化,金额的加、减
 * @Author: yejian
 * @Date: Created in 2019/12/16
 * @Version: 1.0
 * @Modified By:
 */
public class MoneyUtil {

    public static DecimalFormat fnum = new DecimalFormat("##0.00");

    /**
     * 格式化金额
     * @param value
     * @return
     */
    public static String formatMoney(String value){
        if(null == value || "" .equals(value)){
            value = "0.00";
        }
        return fnum.format(new BigDecimal(value));
    }



    /**
     * 金额相加
     * @param valueStr 基础值
     * @param addStr 被加数
     * @return
     */
    public static String moneyAdd(String valueStr,String addStr){
        BigDecimal value = new BigDecimal(valueStr);
        BigDecimal augend = new BigDecimal(addStr);
        return fnum.format(value.add(augend));
    }

    /**
     * 金额相加
     * @param value 基础值
     * @param augend 被加数
     * @return
     */
    public static BigDecimal moneyAdd(BigDecimal value,BigDecimal augend){
        return value.add(augend);
    }

    /**
     * 金额相减
     * @param valueStr 基础值
     * @param minusStr 减数
     * @return
     */
    public static String moneySub(String valueStr,String minusStr){
        BigDecimal value= new BigDecimal(valueStr);
        BigDecimal subtrahend = new BigDecimal(minusStr);
        return fnum.format(value.subtract(subtrahend));
    }

    /**
     * 金额相减
     * @param value 基础值
     * @param subtrahend 减数
     * @return
     */
    public static BigDecimal moneySub(BigDecimal value,BigDecimal subtrahend){
        return value.subtract(subtrahend);
    }


    /**
     * 金额相乘
     * @param valueStr 基础值
     * @param mulStr 被乘数
     * @return
     */
    public static String moneyMul(String valueStr,String mulStr){
        BigDecimal value = new BigDecimal(valueStr);
        BigDecimal mulValue = new BigDecimal(mulStr);
        return fnum.format(value.multiply(mulValue));
    }

    /**
     * 金额相乘
     * @param value 基础值
     * @param mulValue 被乘数
     * @return
     */
    public static BigDecimal moneyMul(BigDecimal value,BigDecimal mulValue){
        return value.multiply(mulValue);
    }

    /**
     * 金额相除 <br/>
     * 精确小位小数
     * @param valueStr 基础值
     * @param divideStr 被乘数
     * @return
     */
    public static String moneydiv(String valueStr,String divideStr){
        BigDecimal value = new BigDecimal(valueStr);
        BigDecimal divideValue = new BigDecimal(divideStr);
        return fnum.format(value.divide(divideValue, 6, BigDecimal.ROUND_HALF_UP));
    }

    /**
     * 金额相除 <br/>
     * 精确小位小数
     * @param value 基础值
     * @param divideValue 被乘数
     * @return
     */
    public static BigDecimal moneydiv(BigDecimal value,BigDecimal divideValue){
        return value.divide(divideValue, 6, BigDecimal.ROUND_HALF_UP);
    }


    /**
     * 值比较大小
     * <br/>如果valueStr大于compValueStr,则返回true,否则返回false
     *  true 代表可用余额不足
     * @param valueStr (需要消费金额)
     * @param compValueStr (可使用金额)
     * @return
     */
    public static boolean moneyComp(String valueStr,String compValueStr){
        BigDecimal value = new BigDecimal(valueStr);
        BigDecimal compValue = new BigDecimal(compValueStr);
        //0:等于    >0:大于    <0:小于
        int result = value.compareTo(compValue);
        if(result > 0){
            return true;
        }else{
            return false;
        }
    }

    /**
     * 值比较大小
     * <br/>如果valueStr大于compValueStr,则返回true,否则返回false
     *  true 代表可用余额不足
     * @param valueStr (需要消费金额)
     * @param compValueStr (可使用金额)
     * @return
     */
    public static boolean moneyComp(BigDecimal valueStr,BigDecimal compValueStr){
        //0:等于    >0:大于    <0:小于
        int result = valueStr.compareTo(compValueStr);
        if(result > 0){
            return true;
        }else{
            return false;
        }
    }

    /**
     * 值比较大小
     * <br/>如果valueStr大于等于compValueStr,则返回true,否则返回false
     *  true 代表可用余额不足
     * @param valueStr (需要消费金额)
     * @param compValueStr (可使用金额)
     * @return
     */
    public static boolean moneyCompGreaterEquals(BigDecimal valueStr,BigDecimal compValueStr){
        //0:等于    >0:大于    <0:小于
        int result = valueStr.compareTo(compValueStr);
        if(result >= 0){
            return true;
        }else{
            return false;
        }
    }

    /**
     * 值比较大小
     * <br/>如果valueStr等于compValueStr,则返回true,否则返回false
     *  true 代表可用余额不足
     * @param valueStr (需要消费金额)
     * @param compValueStr (可使用金额)
     * @return
     */
    public static boolean moneyCompEquals(BigDecimal valueStr,BigDecimal compValueStr){
        //0:等于    >0:大于    <0:小于
        int result = valueStr.compareTo(compValueStr);
        if(result == 0){
            return true;
        }else{
            return false;
        }
    }

    /**
     * 金额乘以，省去小数点
     * @param valueStr 基础值
     * @return
     */
    public static String moneyMulOfNotPoint (String valueStr, String divideStr){
        BigDecimal value = new BigDecimal(valueStr);
        BigDecimal mulValue = new BigDecimal(divideStr);
        valueStr = fnum.format(value.multiply(mulValue));
        return fnum.format(value.multiply(mulValue)).substring(0, valueStr.length()-3);
    }

    /**
     * 给金额加逗号切割
     * @param str
     * @return
     */
    public static String addComma(String str) {
        try {
            String banNum = "";
            if (str.contains(".")) {
                String[] arr = str.split("\\.");
                if (arr.length == 2) {
                    str = arr[0];
                    banNum = "." + arr[1];
                }
            }
            // 将传进数字反转
            String reverseStr = new StringBuilder(str).reverse().toString();
            String strTemp = "";
            for (int i = 0; i < reverseStr.length(); i++) {
                if (i * 3 + 3 > reverseStr.length()) {
                    strTemp += reverseStr.substring(i * 3, reverseStr.length());
                    break;
                }
                strTemp += reverseStr.substring(i * 3, i * 3 + 3) + ",";
            }
            // 将[789,456,] 中最后一个[,]去除
            if (strTemp.endsWith(",")) {
                strTemp = strTemp.substring(0, strTemp.length() - 1);
            }
            // 将数字重新反转
            String resultStr = new StringBuilder(strTemp).reverse().toString();
            resultStr += banNum;
            return resultStr;
        }catch(Exception e){
            return str;
        }

    }
    private static final String CN_ZERO = "零";
    private static final String CN_SYMBOL = "";
    private static final String CN_DOLLAR = "圆";
    private static final String CN_INTEGER = "整";

    private static final String[] digits = new String[] { "零", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖" };
    private static final String[] radices = new String[] { "", "拾", "佰", "仟" };
    private static final String[] bigRadices = new String[] { "", "万", "亿", "万" };
    private static final String[] decimals = new String[] { "角", "分" };

    /**
     * 转换大写金额
     * @param amt
     * @return
     */
    public static String convertToChineseAmt(BigDecimal amt) {
        String currencyDigits = StringUtil.formatNumber("0.00", amt);

        String integral = null; // 整数部分
        String decimal = null; // 小数部分
        String outputCharacters = null; // 最终转换输出结果

        String d = null;
        int zeroCount = 0, p = 0, quotient = 0, modulus = 0;

        // 删除数字中的逗号,
        currencyDigits = currencyDigits.replace("/,/g", "");
        // 删除数字左边的0
        currencyDigits = currencyDigits.replace("/^0+/", "");

        // 拆分数字中的整数与小数部分
        String[] parts = currencyDigits.split("\\.");
        if (parts.length > 1) {
            integral = parts[0];
            decimal = parts[1];

            // 如果小数部分长度大于2，四舍五入，保留两位小数
            if (decimal.length() > 2) {
                long dd = Math.round(Double.parseDouble("0." + decimal) * 100);
                decimal = Long.toString(dd);
            }

        } else {
            integral = parts[0];
            decimal = "0";
        }

        // Start processing:
        outputCharacters = "";
        // Process integral part if it is larger than 0:
        if (Double.parseDouble(integral) > 0) {

            zeroCount = 0;

            for (int i = 0; i < integral.length(); i++) {

                p = integral.length() - i - 1;
                d = integral.substring(i, i + 1);

                quotient = p / 4;
                modulus = p % 4;
                if (d.equals("0")) {
                    zeroCount++;
                } else {
                    if (zeroCount > 0) {
                        outputCharacters += digits[0];
                    }
                    zeroCount = 0;
                    outputCharacters += digits[Integer.parseInt(d)] + radices[modulus];
                }
                if (modulus == 0 && zeroCount < 4) {
                    outputCharacters += bigRadices[quotient];
                }
            }
            outputCharacters += CN_DOLLAR;
        }

        // Process decimal part if it is larger than 0:
        if (Double.parseDouble(decimal) > 0) {
            for (int i = 0; i < decimal.length(); i++) {
                d = decimal.substring(i, i + 1);
                if (!d.equals("0")) {
                    outputCharacters += digits[Integer.parseInt(d)] + decimals[i];
                } else {
                    if (i == 0) {
                        outputCharacters += CN_ZERO;
                    }
                }
            }
        }

        // Confirm and return the final output string:
        if (outputCharacters.equals("")) {
            outputCharacters = CN_ZERO + CN_DOLLAR;
        }

        if (decimal == null || decimal.equals("00")) {
            outputCharacters += CN_INTEGER;
        }

        outputCharacters = CN_SYMBOL + outputCharacters;
        return outputCharacters;
    }

    /**
     * 元转分。乘以100，保留整数，四舍五入。
     *
     * @param amount 元
     * @return 分
     */
    public static BigDecimal yuan2fen(BigDecimal amount) {
        // int moveScale = 2;
        int resultScale = 0;
        if (amount == null) {
            return BigDecimal.ZERO.setScale(resultScale, BigDecimal.ROUND_HALF_UP);
        } else {
            return amount.movePointRight(2).setScale(resultScale, BigDecimal.ROUND_HALF_UP);
        }
    }

    /**
     * 分转元。除以100，保留2位小数，四舍五入。
     *
     * @param amount 分
     * @return 元
     */
    public static BigDecimal fen2yuan(BigDecimal amount) {
        // int moveScale = -2;
        int resultScale = 2;
        if (amount != null) {
            return amount.movePointLeft(2).setScale(resultScale, BigDecimal.ROUND_HALF_UP);
        } else {
            return BigDecimal.ZERO.setScale(resultScale, BigDecimal.ROUND_HALF_UP);
        }
    }

    /**
     * 分转元。除以100，保留2位小数，四舍五入。
     *
     * @param amount 分
     * @return 元
     */
    public static String fen2yuanStr(BigDecimal amount) {
        // int moveScale = -2;
        int resultScale = 2;
        if (amount != null) {
            amount = amount.movePointLeft(2).setScale(resultScale, BigDecimal.ROUND_HALF_UP);
        } else {
            amount = BigDecimal.ZERO.setScale(resultScale, BigDecimal.ROUND_HALF_UP);
        }
        // 不足两位小数补0
        DecimalFormat decimalFormat = new DecimalFormat("0.00#");
        String strVal = decimalFormat.format(amount);
        return strVal;
    }

    /**
     * 判断小数点后2位的数字的正则表达式
     * @param str
     * @return
     */
    public static boolean isAmountNumber(String str){
        if(str == null)
            return false;
        Pattern pattern=Pattern.compile("^(([1-9]{1}\\d*)|([0]{1}))(\\.(\\d){0,2})?$");
        Matcher match=pattern.matcher(str);
        if(match.matches()==false){
            return false;
        }else{
            return true;
        }
    }

    /**
     * 判断小数点后2位的数字的正则表达式
     * @param str
     * @return
     */
    public static boolean isNumber(String str){
        Pattern pattern=Pattern.compile("^[1-9]{1}\\d*$");
        Matcher match=pattern.matcher(str);
        if(match.matches()==false){
            return false;
        }else{
            return true;
        }
    }

    public static void main(String[] args) {
        //Long me = 100L;
        //BigDecimal di = new BigDecimal("100");
        //System.out.println(me);
        //System.out.println(MoneyUtil.formatMoney(me.toString()));
        //System.out.println(di);
        //System.out.println(MoneyUtil.formatMoney(di.toString()));
        //System.out.println(MoneyUtil.moneyMul("100","0.02"));
        BigDecimal di1 = new BigDecimal("9999");
        BigDecimal di2 = new BigDecimal("10000");
        BigDecimal rate = new BigDecimal("11");
        BigDecimal res = MoneyUtil.moneyMul(new BigDecimal("51"), moneydiv(rate,new BigDecimal("100")));
        System.out.println(res);
        long up = res.setScale( 0, BigDecimal.ROUND_UP ).longValue(); // 向上取整
        long down = res.setScale( 0, BigDecimal.ROUND_DOWN ).longValue(); // 向下取整
        System.out.println(up);
        System.out.println(down);

        System.out.println(MoneyUtil.moneyComp(di2, di1));
    }

    /**
     * 简单的Integer相加，和别超过Integer.MAX_VALUE
     * @param nums
     * @return
     */
    public static Integer numAdd(Integer... nums) {
        if (nums == null || nums.length <= 0) {
           return null;
        }
        Integer sum = 0;
        for (Integer num : nums) {
            if (num == null) {
                continue;
            }
            sum += num;
        }
        return sum;
    }
}
