package com.milktea.store.controller;

import com.milktea.common.result.PageResult;
import com.milktea.common.result.Result;
import com.milktea.model.dto.DeductRequest;
import com.milktea.model.dto.StockRecordDTO;
import com.milktea.model.entity.Material;
import com.milktea.model.vo.MaterialVO;
import com.milktea.store.service.MaterialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 原料管理 Controller
 * 负责人: 成员D
 */
@RestController
@RequestMapping("/material")
public class MaterialController {

    @Autowired
    private MaterialService materialService;

    /** 下单自动扣减原料(内部Feign接口, 订单服务调用) */
    @PostMapping("/deduct")
    public Result<java.util.List<String>> deduct(@RequestBody DeductRequest request) {
        return Result.success(materialService.deductForOrder(request.getItems(), request.getOrderNo()));
    }

    /** 原料分页: keyword 模糊原料名, VO 带库存状态 */
    @GetMapping("/page")
    public Result<PageResult<MaterialVO>> page(@RequestParam(defaultValue = "1") Integer pageNum,
                                               @RequestParam(defaultValue = "10") Integer pageSize,
                                               @RequestParam(required = false) String keyword) {
        return Result.success(materialService.page(pageNum, pageSize, keyword));
    }

    /** 全部原料(出入库弹窗下拉用) */
    @GetMapping("/list")
    public Result<List<Material>> list() {
        return Result.success(materialService.list());
    }

    @GetMapping("/{id}")
    public Result<Material> queryById(@PathVariable Long id) {
        return Result.success(materialService.queryById(id));
    }

    @PostMapping
    public Result<Void> add(@RequestBody Material material) {
        materialService.add(material);
        return Result.success();
    }

    @PutMapping
    public Result<Void> update(@RequestBody Material material) {
        materialService.update(material);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        materialService.delete(id);
        return Result.success();
    }

    /** 出入库核心接口: type 1入库 2出库 */
    @PostMapping("/stock")
    public Result<Void> stockOperation(@RequestBody StockRecordDTO dto) {
        materialService.stockOperation(dto);
        return Result.success();
    }

    /** 预警列表(工作台用) */
    @GetMapping("/warning")
    public Result<List<MaterialVO>> warningList() {
        return Result.success(materialService.warningList());
    }
}
