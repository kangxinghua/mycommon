package org.mycommon.modules.web.filter;

import org.apache.commons.lang3.StringEscapeUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * Created by KangXinghua on 2016/3/3.
 */
public class XssHttpServletRequestWrapper extends HttpServletRequestWrapper {

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

        if (value == null)

            return null;

        return cleanXSS(value);

    }

    private String cleanXSS(String value) {

        if (value != null) {
//            value = StringEscapeUtils.escapeHtml4(value);
        }

        return value;

    }

    public static void main(String[] args) {
        String s = "(fdsafasdf)";
        System.out.println(StringEscapeUtils.escapeHtml4(s));
    }
}