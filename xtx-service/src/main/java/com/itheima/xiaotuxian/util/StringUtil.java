package com.itheima.xiaotuxian.util;

import com.itheima.xiaotuxian.constant.enums.ErrorMessageEnum;
import com.itheima.xiaotuxian.exception.BusinessException;

import java.io.UnsupportedEncodingException;

public class StringUtil {
    private StringUtil() {
    }

    private static final int[] li_SecPosValue = {1601, 1637, 1833, 2078, 2274,
            2302, 2433, 2594, 2787, 3106, 3212, 3472, 3635, 3722, 3730, 3858,
            4027, 4086, 4390, 4558, 4684, 4925, 5249, 5590};
    private static final String[] lc_FirstLetter = {"a", "b", "c", "d", "e",
            "f", "g", "h", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s",
            "t", "w", "x", "y", "z"};

    /**
     * 取得给定汉字串的首字母串,即声母串
     *
     * @param str 给定汉字串
     * @return 声母串
     */
    public static String getAllFirstLetter(String str) {
        if (str == null || str.trim().length() == 0) {
            return "";
        }

        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            stringBuilder.append(getFirstLetter(str.substring(i, i + 1)));
        }

        return stringBuilder.toString();
    }

    /**
     * 取得给定汉字的首字母,即声母
     *
     * @param chinese 给定的汉字
     * @return 给定汉字的声母
     */
    public static String getFirstLetter(String chinese) {
        if (chinese == null || chinese.trim().length() == 0) {
            return "";
        }
        chinese = conversionStr(chinese, "GB2312", "ISO8859-1");
        if (chinese.length() > 1) // 判断是不是汉字
        {
            int liSectorCode = (int) chinese.charAt(0); // 汉字区码
            int liPositionCode = (int) chinese.charAt(1); // 汉字位码
            liSectorCode = liSectorCode - 160;
            liPositionCode = liPositionCode - 160;
            int liSecPosCode = liSectorCode * 100 + liPositionCode; // 汉字区位码
            if (liSecPosCode > 1600 && liSecPosCode < 5590) {
                for (int i = 0; i < 23; i++) {
                    if (liSecPosCode >= li_SecPosValue[i]
                            && liSecPosCode < li_SecPosValue[i + 1]) {
                        chinese = lc_FirstLetter[i];
                        break;
                    }
                }
            } else // 非汉字字符,如图形符号或ASCII码
            {
                chinese = conversionStr(chinese, "ISO8859-1", "GB2312");
                chinese = chinese.substring(0, 1);
            }
        }
        if (!chinese.matches("[a-zA-Z]+")) {
            chinese = "#";
        }
        return chinese;
    }

    /**
     * 字符串编码转换
     *
     * @param str           要转换编码的字符串
     * @param charsetName   原来的编码
     * @param toCharsetName 转换后的编码
     * @return 经过编码转换后的字符串
     */
    private static String conversionStr(String str, String charsetName, String toCharsetName) {
        try {
            str = new String(str.getBytes(charsetName), toCharsetName);
        } catch (UnsupportedEncodingException ex) {
            throw new BusinessException(ErrorMessageEnum.CHAR_CONVERT_ERROR);
        }
        return str;
    }
}
