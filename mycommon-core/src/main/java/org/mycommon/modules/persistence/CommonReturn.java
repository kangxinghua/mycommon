package org.mycommon.modules.persistence;

import org.mycommon.modules.utils.Strings;

import java.util.Date;

/**
 * Created by KangXinghua on 2014/12/6.
 * 公共返回值,参照openApi
 */
public class CommonReturn<T> {

    private Date respTime;
    private int ret;
    private String msg;

    private T data;

    public CommonReturn() {
        this.respTime = new Date();
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }


    public int getRet() {
        return ret;
    }

    public void setRet(int code) {
        this.ret = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Date getRespTime() {
        return respTime;
    }

    public void setRespTime(Date respTime) {
        this.respTime = respTime;
    }

}
