package org.mycommon.modules.utils;

import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.LocaleResolver;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串工具类, 继承org.apache.commons.lang3.StringUtils类
 * Created by KangXinghua on 2015/1/23.
 */
public class Strings extends org.apache.commons.lang3.StringUtils {

    public static String lowerFirst(String str) {
        if (Strings.isBlank(str)) {
            return "";
        } else {
            return str.substring(0, 1).toLowerCase() + str.substring(1);
        }
    }

    public static String upperFirst(String str) {
        if (Strings.isBlank(str)) {
            return "";
        } else {
            return str.substring(0, 1).toUpperCase() + str.substring(1);
        }
    }

    /**
     * 替换掉HTML标签方法
     */
    public static String replaceHtml(String html) {
        if (isBlank(html)) {
            return "";
        }
        String regEx = "<.+?>";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(html);
        String s = m.replaceAll("");
        return s;
    }

    /**
     * 缩略字符串（不区分中英文字符）
     *
     * @param str    目标字符串
     * @param length 截取长度
     * @return
     */
    public static String abbr(String str, int length) {
        if (str == null) {
            return "";
        }
        try {
            StringBuilder sb = new StringBuilder();
            int currentLength = 0;
            for (char c : replaceHtml(StringEscapeUtils.unescapeHtml4(str)).toCharArray()) {
                currentLength += String.valueOf(c).getBytes("GBK").length;
                if (currentLength <= length - 3) {
                    sb.append(c);
                } else {
                    sb.append("...");
                    break;
                }
            }
            return sb.toString();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 缩略字符串（替换html）
     *
     * @param str    目标字符串
     * @param length 截取长度
     * @return
     */
    public static String rabbr(String str, int length) {
        return abbr(replaceHtml(str), length);
    }


    /**
     * 转换为Double类型
     */
    public static Double toDouble(Object val) {
        if (val == null) {
            return 0D;
        }
        try {
            return Double.valueOf(trim(val.toString()));
        } catch (Exception e) {
            return 0D;
        }
    }

    /**
     * 转换为Float类型
     */
    public static Float toFloat(Object val) {
        return toDouble(val).floatValue();
    }

    /**
     * 转换为Long类型
     */
    public static Long toLong(Object val) {
        return toDouble(val).longValue();
    }

    /**
     * 转换为Integer类型
     */
    public static Integer toInteger(Object val) {
        return toLong(val).intValue();
    }

    /**
     * 获得i18n字符串
     */
    public static String getMessage(String code, Object[] args) {
        LocaleResolver localLocaleResolver = SpringContextHolder.getBean(LocaleResolver.class);
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        Locale localLocale = localLocaleResolver.resolveLocale(request);
        return SpringContextHolder.getApplicationContext().getMessage(code, args, localLocale);
    }

    /**
     * 获得用户远程地址
     */
    public static String getRemoteAddr(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");

        String localIP = "127.0.0.1";

        if ((ip == null) || (ip.length() == 0) || (ip.equalsIgnoreCase(localIP)) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if ((ip == null) || (ip.length() == 0) || (ip.equalsIgnoreCase(localIP)) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if ((ip == null) || (ip.length() == 0) || (ip.equalsIgnoreCase(localIP)) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    public static String unicodeToString(String str) {

        Pattern pattern = Pattern.compile("(\\\\u(\\p{XDigit}{4}))");
        Matcher matcher = pattern.matcher(str);
        char ch;
        while (matcher.find()) {
            ch = (char) Integer.parseInt(matcher.group(2), 16);
            str = str.replace(matcher.group(1), ch + "");
        }
        return str;
    }

    public static String stringToUnicode(String s) {
        StringBuilder sb = new StringBuilder();
        sb.setLength(0);//清零
        StringBuilder tmp = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            sb.append("\\u"); //以 \ u开头
            tmp.setLength(0); //清零
            tmp.append(Integer.toHexString(s.charAt(i)).toLowerCase());
            while (tmp.length() < 4) {
                tmp.insert(0, 0);
            }
            sb.append(tmp);
        }
        return sb.toString();
    }

    public static String getUrl(Map map) {
        if (null == map || map.keySet().size() == 0) {
            return ("");
        }
        StringBuffer url = new StringBuffer();
        Set keys = map.keySet();
        for (Iterator i = keys.iterator(); i.hasNext(); ) {
            String key = String.valueOf(i.next());
            if (map.containsKey(key)) {
                Object val = map.get(key);
                String str = val != null ? val.toString() : "";
                try {
                    str = URLEncoder.encode(str, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                url.append(key).append("=").append(str).append("&");
            }
        }
        String strURL = "";
        strURL = url.toString();
        if ("&".equals("" + strURL.charAt(strURL.length() - 1))) {
            strURL = strURL.substring(0, strURL.length() - 1);
        }
        return (strURL);
    }

    private final static String[] STR_NUMBER = {"零", "壹", "贰", "叁", "肆", "伍",

            "陆", "柒", "捌", "玖"};

    private final static String[] STR_UNIT = {"", "拾", "佰", "仟", "万", "拾",

            "佰", "仟", "亿", "拾", "佰", "仟"};// 整数单位

    private final static String[] STR_UNIT2 = {"角", "分", "厘"};// 小数单位

    public static String convert(double d) {

        DecimalFormat df = new DecimalFormat("#0.###");

        String strNum = df.format(d);

        if (strNum.indexOf(".") != -1) {

            String num = strNum.substring(0, strNum.indexOf("."));

            if (num.length() > 12) {

                System.out.println("数字太大不能完成转换！ ");

                return "";

            }

        }

        String point = "";

        if (strNum.indexOf(".") != -1) {

            point = "元";

        } else {

            point = "元整";

        }

        String result = getInteger(strNum) + point + getDecimal(strNum);

        if (result.startsWith("元")) {

            result = result.substring(1, result.length());

        }

        return result;

    }

    public static String getInteger(String num) {

        if (num.indexOf(".") != -1) {

            num = num.substring(0, num.indexOf("."));

        }

        num = new StringBuffer(num).reverse().toString();

        StringBuffer temp = new StringBuffer();

        for (int i = 0; i < num.length(); i++) {

            temp.append(STR_UNIT[i]);

            temp.append(STR_NUMBER[num.charAt(i) - 48]);  //num.charAt(i)-48获取数值 或者使用Integer.pa……

        }

        num = temp.reverse().toString();// 反转字符串

        num = numReplace(num, "零拾", "零"); // 替换字符串的字符

        num = numReplace(num, "零佰", "零"); // 替换字符串的字符

        num = numReplace(num, "零仟", "零"); // 替换字符串的字符

        num = numReplace(num, "零万", "万"); // 替换字符串的字符

        num = numReplace(num, "零亿", "亿"); // 替换字符串的字符

        num = numReplace(num, "零零", "零"); // 替换字符串的字符

        num = numReplace(num, "亿万", "亿"); // 替换字符串的字符

        // 如果字符串以零结尾将其除去

        if (num.length() != 1 && num.lastIndexOf("零") == num.length() - 1) {

            num = num.substring(0, num.length() - 1);

        }

        return num;

    }

    /**
     * 替换字符串中内容
     *
     * @param num    字符串
     * @param oldStr 被替换内容
     * @param newStr 新内容
     * @return 替换后的字符串
     */

    public static String numReplace(String num, String oldStr, String newStr) {

        while (true) {

            // 判断字符串中是否包含指定字符

            if (num.indexOf(oldStr) == -1) {

                break;

            }

            // 替换字符串

            num = num.replaceAll(oldStr, newStr);

        }

        // 返回替换后的字符串

        return num;

    }

    public static String getDecimal(String num) {

        // 判断是否包含小数点

        if (num.indexOf(".") == -1) {

            return "";

        }

        num = num.substring(num.indexOf(".") + 1);

        // 反转字符串

        //num = new StringBuffer(num).reverse().toString();

        num = new StringBuffer(num).toString();

        // 创建一个StringBuffer对象

        StringBuffer temp = new StringBuffer();

        // 加入单位

        for (int i = 0; i < num.length(); i++) {

            temp.append(STR_NUMBER[num.charAt(i) - 48]);

            temp.append(STR_UNIT2[i]);

        }

        num = temp.toString(); // 替换字符串的字符

        num = numReplace(num, "零角", "零"); // 替换字符串的字符

        num = numReplace(num, "零分", "零"); // 替换字符串的字符

        num = numReplace(num, "零厘", "零"); // 替换字符串的字符

        num = numReplace(num, "零零", "零"); // 替换字符串的字符

        // 如果字符串以零结尾将其除去

        if (num.lastIndexOf("零") == num.length() - 1) {

            num = num.substring(0, num.length() - 1);

        }

        return num;

    }

    //判断输入字符串是否为科学计数法
    public static boolean isENum(String input) {
        String regx = "^((-?\\d+.?\\d*)[Ee]{1}(-?\\d+))$";//科学计数法正则表达式
        Pattern pattern = Pattern.compile(regx);
        return pattern.matcher(input).matches();
    }
}
