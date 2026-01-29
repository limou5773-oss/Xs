package com.sxps.xsps.pojo;

import com.github.pagehelper.PageInfo;
import lombok.Data;

import java.util.List;

@Data
public class PageResult<T> {

    private Long total;       // 总记录数
    private List<T> records;  // 当前页数据

    public PageResult(List<T> list) {
        PageInfo<T> pageInfo = new PageInfo<>(list);
        this.total = pageInfo.getTotal();
        this.records = list;
    }
}
