package com.milktea.common.result;

import com.github.pagehelper.PageInfo;
import lombok.Data;

import java.util.List;

@Data
public class PageResult<T> {
    private List<T> list;
    private Long total;
    private Integer pageNum;
    private Integer pageSize;
    private Integer pages;

    public static <T> PageResult<T> of(List<T> list) {
        PageInfo<T> pageInfo = new PageInfo<>(list);
        PageResult<T> pr = new PageResult<>();
        pr.setList(pageInfo.getList());
        pr.setTotal(pageInfo.getTotal());
        pr.setPageNum(pageInfo.getPageNum());
        pr.setPageSize(pageInfo.getPageSize());
        pr.setPages(pageInfo.getPages());
        return pr;
    }

    public static <T> PageResult<T> of(List<T> list, Long total, Integer pageNum, Integer pageSize, Integer pages) {
        PageResult<T> pr = new PageResult<>();
        pr.setList(list);
        pr.setTotal(total);
        pr.setPageNum(pageNum);
        pr.setPageSize(pageSize);
        pr.setPages(pages);
        return pr;
    }
}
