package org.mycommon.modules.persistence;

import java.util.List;

/**
 * Created by KangXinghua on 2014/12/4.
 * easyui datagrid 返回json对象
 */
public class PageModel<T> {
    public static final int PAGE_SIZE = Integer.MAX_VALUE;
    private int pageNum = 1;
    private int pageSize = PAGE_SIZE;
    private String order;
    private String sortName;
    private List<T> rows;
    private Object[] footer;
    private long total;

    public List<T> getRows() {
        return rows;
    }

    public void setRows(List<T> rows) {
        this.rows = rows;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public Object[] getFooter() {
        return footer;
    }

    public void setFooter(Object[] footer) {
        this.footer = footer;
    }

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public String getSortName() {
        return sortName;
    }

    public void setSortName(String sortName) {
        this.sortName = sortName;
    }
}
