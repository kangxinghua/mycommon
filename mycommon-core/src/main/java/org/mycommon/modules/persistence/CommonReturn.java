package org.mycommon.modules.persistence;

import java.util.Date;

/**
 * Created by KangXinghua on 2014/12/6.
 * 公共返回值,参照openApi
 */
public class CommonReturn implements java.io.Serializable {

    private Date respTime;
    private int ret;
    private String msg;

    private Object data;

    public CommonReturn() {
        this.respTime = new Date();
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
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
