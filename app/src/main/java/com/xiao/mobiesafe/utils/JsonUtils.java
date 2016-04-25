package com.xiao.mobiesafe.utils;

public class JsonUtils {

    public static String string2Json(String s) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case ':':
                    sb.append("：");
                    break;
                case '[':
                    sb.append("【");
                    break;
                case ']':
                    sb.append("】");
                    break;
                case '"':
                    sb.append("“");
                    break;
                case '{':
                    sb.append("（");
                    break;
                case '}':
                    sb.append("）");
                    break;
                default:
                    sb.append(c);
            }
        }
        return sb.toString();
    }

    public static String json2String(String s) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '：':
                    sb.append(":");
                    break;
                case '【':
                    sb.append("[");
                    break;
                case '】':
                    sb.append("]");
                    break;
                case '“':
                    sb.append("\"");
                    break;
                case '（':
                    sb.append("{");
                    break;
                case '）':
                    sb.append("}");
                    break;
                default:
                    sb.append(c);
            }
        }
        return sb.toString();
    }
}
