package org.mycommon.modules.model;

import com.alibaba.fastjson.annotation.JSONField;
import org.mycommon.modules.utils.Dates;

import java.util.Date;

/**
 * Created by KangXinghua on 2016/1/10.
 */
public class DataModel extends IdModel {

    protected String createBy;    // 创建者
    protected String createByName;    // 创建者
    protected Date createTime;// 创建日期
    protected String updateBy;    // 更新者
    protected String updateByName;    // 更新者
    protected Date updateTime;// 更新日期


    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public String getCreateByName() {
        return createByName;
    }

    public void setCreateByName(String createByName) {
        this.createByName = createByName;
    }

    @JSONField(format = Dates.PATTERN_CLASSICAL)
    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }

    public String getUpdateByName() {
        return updateByName;
    }

    public void setUpdateByName(String updateByName) {
        this.updateByName = updateByName;
    }

    @JSONField(format = Dates.PATTERN_CLASSICAL)
    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
