package org.mycommon.modules.persistence;

/**
 * Created by KangXinghua on 2014/12/5.
 */
public interface Constants {

    /**
     * emis 系统数据库名
     */
    String EMIS_DATABASE_NAME = "emis";

    /**
     * 删除字段
     */
    String FIELD_DELETED = "deleted";
    /**
     * 正常
     */
    String DEL_FLAG_NORMAL = "0";
    /**
     * 删除
     */
    String DEL_FLAG_DELETE = "1";

    String SESSION_FORCE_LOGOUT_KEY = "session.force.logout";

    String LOGIN_ERROR_KEY = "login_error_key";

    String SUPER_ADMINISTRATOR_ROLE_ID = "286e0559-1b97-11e4-9933-448a5b8d838e";
    String BASE_ERROR_MESSAGE = "286e0559-1b97-11e4-9933-448a5b8d838e";
    Integer ERROR = Integer.MAX_VALUE;
    Integer NORMAL = 0;
}
