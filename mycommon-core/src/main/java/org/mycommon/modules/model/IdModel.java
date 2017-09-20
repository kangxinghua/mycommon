package org.mycommon.modules.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.ToStringSerializer;

/**
 * Created by KangXinghua on 2016/1/11.
 */
public class IdModel extends BaseModel {
    protected Long id;
    protected Boolean deleted; // 删除标记（0：正常；1：删除；）
    protected Integer version; //版本控制

    @JSONField(serializeUsing = ToStringSerializer.class)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }
}
