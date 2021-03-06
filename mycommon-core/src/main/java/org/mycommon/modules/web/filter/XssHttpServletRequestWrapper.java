package org.mycommon.modules.web.filter;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringEscapeUtils;
import org.mycommon.modules.utils.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by KangXinghua on 2016/3/3.
 */
public class XssHttpServletRequestWrapper extends HttpServletRequestWrapper {

    private static Logger logger = LoggerFactory.getLogger(XssHttpServletRequestWrapper.class);

    public XssHttpServletRequestWrapper(HttpServletRequest servletRequest) {
        super(servletRequest);
    }

    public String[] getParameterValues(String parameter) {
        String[] values = super.getParameterValues(parameter);
        if (values == null) {
            return null;
        }
        int count = values.length;
        String[] encodedValues = new String[count];
        for (int i = 0; i < count; i++) {
            encodedValues[i] = cleanXSS(values[i]);
        }
        return encodedValues;

    }

    public String getParameter(String parameter) {
        String value = super.getParameter(parameter);
        if (value == null) {
            return null;
        }
        return cleanXSS(value);

    }

    public String getHeader(String name) {
        String value = super.getHeader(name);
        if (value == null) {
            return null;
        }
        return cleanXSS(value);
    }

    private String cleanXSS(String value) {
        if (value != null) {
            Boolean isJson = false;
            try {//判断是否是JSON字符串
                if ((Strings.contains(value, "{") && Strings.contains(value, "}")) || (Strings.contains(value, "[") && Strings.contains(value, "]"))) {
                    Object josn = JSONObject.parse(value);
                    if (josn instanceof JSONArray) {
                        JSONArray jsonArray = (JSONArray) josn;
                        escapeHtml(jsonArray);
                        value = jsonArray.toJSONString();
                    } else if (josn instanceof JSONObject) {
                        JSONObject jsonObject = (JSONObject) josn;
                        escapeHtml(jsonObject);
                        value = jsonObject.toJSONString();
                    } else {
                        logger.error(value);
                    }
                    isJson = true;
                }
            } catch (JSONException e) {
                logger.error(value, e);
            }

            if (!isJson) {
                value = StringEscapeUtils.escapeHtml4(value);
            }
        }
        return value;
    }

    private void escapeHtml(JSONArray jsonArray) {
        for (int i = 0; i < jsonArray.size(); i++) {
            Object o = jsonArray.get(i);
            if (o instanceof JSONArray) {
                escapeHtml((JSONArray) o);
            } else if (o instanceof JSONObject) {
                escapeHtml((JSONObject) o);
            } else {
                jsonArray.set(i, StringEscapeUtils.escapeHtml4(jsonArray.get(i).toString()));
            }
        }
    }

    private void escapeHtml(JSONObject jsonObject) {
        Iterator<Map.Entry<String, Object>> itr = jsonObject.entrySet().iterator();
        while (itr.hasNext()) {
            Map.Entry<String, Object> entry = itr.next();
            Object keyObject = jsonObject.get(entry.getKey());
            if (keyObject instanceof JSONArray) {
                escapeHtml((JSONArray) keyObject);
            } else if (keyObject instanceof JSONObject) {
                escapeHtml((JSONObject) keyObject);
            } else {
                if (entry.getValue() != null) {
                    jsonObject.put(entry.getKey(), StringEscapeUtils.escapeHtml4(entry.getValue().toString()));
                }
            }
        }
    }
}