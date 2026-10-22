package com.milktea.store.service;

import com.milktea.common.result.PageResult;
import com.milktea.model.dto.DeductItem;
import com.milktea.model.dto.StockRecordDTO;
import com.milktea.model.entity.Material;
import com.milktea.model.vo.MaterialVO;

import java.util.List;

/**
 * 原料服务接口
 * 负责人: 成员D
 */
public interface MaterialService {

    /** 原料分页: keyword 模糊原料名, VO 带库存状态(正常/偏低/预警) */
    PageResult<MaterialVO> page(Integer pageNum, Integer pageSize, String keyword);

    /** 全部原料(出入库弹窗下拉用) */
    List<Material> list();

    Material queryById(Long id);

    void add(Material material);

    void update(Material material);

    /** 删除: 校验无关联流水 */
    void delete(Long id);

    /** 出入库核心: 同事务更新库存 + 写流水 */
    void stockOperation(StockRecordDTO dto);

    /** 预警列表(工作台用) */
    List<MaterialVO> warningList();

    /**
     * 下单自动扣减原料(内部Feign接口): 按配方汇总扣减, 事务+防超扣
     * @return 扣减后仍低于阈值的原料名列表
     */
    java.util.List<String> deductForOrder(java.util.List<DeductItem> items, String orderNo);
}
