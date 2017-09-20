package org.mycommon.modules.persistence;

/**
 * Created by KangXinghua on 2014/12/5.
 */
public interface Constants {
    Integer ERROR = Integer.MAX_VALUE;//未知错误
    Integer NORMAL = 0;//正常返回
    Integer NOT_LOGGED_IN = 1;//未登入
    Integer BAD_REQUEST = 400;//无效请求,一般是必填项目未填写
    Integer UNAUTHORIZED = 401;//未授权

    String BASE_ERROR_MESSAGE = "系统错误!";
    String NOT_CHANGE_MESSAGE = "未更新成功数据!";
}
