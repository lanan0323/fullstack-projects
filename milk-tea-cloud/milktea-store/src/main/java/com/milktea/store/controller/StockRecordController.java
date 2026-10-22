package com.milktea.store.controller;

import com.github.pagehelper.PageHelper;
import com.milktea.common.result.PageResult;
import com.milktea.common.result.Result;
import com.milktea.model.entity.StockRecord;
import com.milktea.store.mapper.StockRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 出入库流水 Controller
 * 负责人: 成员D
 */
@RestController
@RequestMapping("/stock/record")
public class StockRecordController {

    @Autowired
    private StockRecordMapper stockRecordMapper;

    /** 流水分页: keyword 按原料名过滤, 按时间倒序 */
    @GetMapping("/page")
    public Result<PageResult<StockRecord>> page(@RequestParam(defaultValue = "1") Integer pageNum,
                                                @RequestParam(defaultValue = "10") Integer pageSize,
                                                @RequestParam(required = false) String keyword) {
        PageHelper.startPage(pageNum, pageSize);
        List<StockRecord> list = stockRecordMapper.page(keyword);
        return Result.success(PageResult.of(list));
    }

    /** 某原料全部流水 */
    @GetMapping("/material/{materialId}")
    public Result<List<StockRecord>> findByMaterialId(@PathVariable Long materialId) {
        return Result.success(stockRecordMapper.findByMaterialId(materialId));
    }
}
