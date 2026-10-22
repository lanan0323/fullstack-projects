package com.milktea.store.service.impl;

import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.PageHelper;
import com.milktea.common.context.UserContext;
import com.milktea.common.exception.BusinessException;
import com.milktea.common.result.PageResult;
import com.milktea.model.dto.DeductItem;
import com.milktea.model.dto.StockRecordDTO;
import com.milktea.model.entity.Material;
import com.milktea.model.entity.StockRecord;
import com.milktea.model.vo.MaterialVO;
import com.milktea.store.mapper.MaterialMapper;
import com.milktea.store.mapper.StockRecordMapper;
import com.milktea.store.service.MaterialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 原料服务实现
 * 负责人: 成员D
 */
@Service
public class MaterialServiceImpl implements MaterialService {

    @Autowired
    private MaterialMapper materialMapper;

    @Autowired
    private StockRecordMapper stockRecordMapper;

    @Override
    public PageResult<MaterialVO> page(Integer pageNum, Integer pageSize, String keyword) {
        PageHelper.startPage(pageNum, pageSize);
        List<Material> list = materialMapper.page(keyword);
        List<MaterialVO> vos = list.stream().map(this::toVO).collect(Collectors.toList());
        return PageResult.of(vos);
    }

    @Override
    public List<Material> list() {
        return materialMapper.list();
    }

    @Override
    public Material queryById(Long id) {
        return materialMapper.findById(id);
    }

    @Override
    public void add(Material material) {
        if (material == null || StrUtil.isBlank(material.getName())) {
            throw new BusinessException("原料名称不能为空");
        }
        if (StrUtil.isBlank(material.getUnit())) {
            throw new BusinessException("计量单位不能为空");
        }
        if (material.getMinThreshold() == null) {
            material.setMinThreshold(BigDecimal.ZERO);
        }
        if (material.getCurrentStock() == null) {
            material.setCurrentStock(BigDecimal.ZERO);
        }
        if (material.getStatus() == null) {
            material.setStatus(1);
        }
        material.setCreateTime(LocalDateTime.now());
        material.setUpdateTime(LocalDateTime.now());
        materialMapper.insert(material);
    }

    @Override
    public void update(Material material) {
        if (material == null || material.getId() == null) {
            throw new BusinessException("原料ID不能为空");
        }
        Material db = materialMapper.findById(material.getId());
        if (db == null) {
            throw new BusinessException("原料不存在");
        }
        if (StrUtil.isBlank(material.getName())) {
            throw new BusinessException("原料名称不能为空");
        }
        if (StrUtil.isBlank(material.getUnit())) {
            throw new BusinessException("计量单位不能为空");
        }
        if (material.getMinThreshold() == null) {
            throw new BusinessException("最低库存阈值不能为空");
        }
        // 当前库存只允许通过出入库变更, 编辑不覆盖 currentStock, 保证流水可追溯
        db.setName(material.getName());
        db.setUnit(material.getUnit());
        db.setMinThreshold(material.getMinThreshold());
        db.setStatus(material.getStatus() == null ? db.getStatus() : material.getStatus());
        db.setUpdateTime(LocalDateTime.now());
        materialMapper.update(db);
    }

    @Override
    public void delete(Long id) {
        if (id == null) {
            throw new BusinessException("原料ID不能为空");
        }
        Material db = materialMapper.findById(id);
        if (db == null) {
            throw new BusinessException("原料不存在");
        }
        long count = stockRecordMapper.countByMaterialId(id);
        if (count > 0) {
            throw new BusinessException("该原料存在出入库流水, 无法删除");
        }
        materialMapper.deleteById(id);
    }

    @Override
    @Transactional
    public void stockOperation(StockRecordDTO dto) {
        // 参数校验
        if (dto == null || dto.getMaterialId() == null) {
            throw new BusinessException("原料ID不能为空");
        }
        if (dto.getType() == null || (dto.getType() != 1 && dto.getType() != 2)) {
            throw new BusinessException("操作类型不正确: 1入库/2出库");
        }
        if (dto.getQuantity() == null || dto.getQuantity().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("变动数量必须大于0");
        }
        // 查询原料
        Material m = materialMapper.findById(dto.getMaterialId());
        if (m == null) {
            throw new BusinessException("原料不存在");
        }
        // 计算前后库存
        BigDecimal before = m.getCurrentStock() == null ? BigDecimal.ZERO : m.getCurrentStock();
        BigDecimal after = dto.getType() == 1
                ? before.add(dto.getQuantity())
                : before.subtract(dto.getQuantity());
        if (after.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("库存不足");
        }
        // 更新库存 + 写流水, 同事务保证两表一致
        materialMapper.updateStock(m.getId(), after, LocalDateTime.now());
        StockRecord r = new StockRecord();
        r.setMaterialId(m.getId());
        r.setMaterialName(m.getName());
        r.setType(dto.getType());
        r.setQuantity(dto.getQuantity());
        r.setBeforeStock(before);
        r.setAfterStock(after);
        Long uid = UserContext.getCurrentUserId();
        r.setOperator(uid == null ? "系统" : "员工" + uid);
        r.setRemark(dto.getRemark());
        r.setCreateTime(LocalDateTime.now());
        stockRecordMapper.insert(r);
    }

    @Override
    public List<MaterialVO> warningList() {
        return materialMapper.list().stream()
                .filter(m -> m.getCurrentStock() != null && m.getMinThreshold() != null
                        && m.getCurrentStock().compareTo(m.getMinThreshold()) < 0)
                .map(this::toVO)
                .collect(Collectors.toList());
    }

    @Autowired
    private com.milktea.store.mapper.RecipeMapper recipeMapper;

    @Override
    @Transactional
    public List<String> deductForOrder(List<DeductItem> items, String orderNo) {
        if (items == null || items.isEmpty()) {
            throw new BusinessException("扣减明细不能为空");
        }
        // 1. 按配方汇总每种原料的需求量(多商品共用原料合并)
        java.util.Map<Long, BigDecimal> need = new java.util.LinkedHashMap<>();
        java.util.Map<Long, String> names = new java.util.HashMap<>();
        for (DeductItem item : items) {
            if (item == null || item.getProductId() == null) continue;
            BigDecimal qty = item.getQuantity() == null ? BigDecimal.ONE : item.getQuantity();
            List<java.util.Map<String, Object>> recipe = recipeMapper.findByProductId(item.getProductId());
            if (recipe == null || recipe.isEmpty()) continue; // 无配方的商品不扣原料
            for (java.util.Map<String, Object> row : recipe) {
                Long mid = ((Number) row.get("materialId")).longValue();
                BigDecimal perCup = new BigDecimal(row.get("quantity").toString());
                need.merge(mid, perCup.multiply(qty), BigDecimal::add);
                names.put(mid, (String) row.get("name"));
            }
        }
        if (need.isEmpty()) return java.util.Collections.emptyList();
        // 2. 原子扣减(防并发超扣), 不足即抛异常整体回滚
        for (java.util.Map.Entry<Long, BigDecimal> e : need.entrySet()) {
            Material m = materialMapper.findById(e.getKey());
            int rows = materialMapper.deductStock(e.getKey(), e.getValue(), LocalDateTime.now());
            if (rows == 0) {
                throw new BusinessException("原料「" + names.get(e.getKey()) + "」库存不足, 无法下单");
            }
            // 3. 写出库流水
            BigDecimal after = m.getCurrentStock().subtract(e.getValue());
            StockRecord r = new StockRecord();
            r.setMaterialId(e.getKey());
            r.setMaterialName(m.getName());
            r.setType(2);
            r.setQuantity(e.getValue());
            r.setBeforeStock(m.getCurrentStock());
            r.setAfterStock(after);
            r.setOperator("系统");
            r.setRemark("订单自动扣减" + (orderNo == null || orderNo.isEmpty() ? "" : " " + orderNo));
            r.setCreateTime(LocalDateTime.now());
            stockRecordMapper.insert(r);
        }
        // 4. 返回扣后预警原料
        return warningList().stream().map(MaterialVO::getName).collect(Collectors.toList());
    }

    private MaterialVO toVO(Material m) {
        MaterialVO vo = new MaterialVO();
        vo.setId(m.getId());
        vo.setName(m.getName());
        vo.setUnit(m.getUnit());
        vo.setCurrentStock(m.getCurrentStock());
        vo.setMinThreshold(m.getMinThreshold());
        vo.setStatus(m.getStatus());
        vo.setStockStatus(judgeStockStatus(m));
        return vo;
    }

    /**
     * 库存状态判定:
     * currentStock < minThreshold         -> 预警(红)
     * currentStock < minThreshold × 1.5   -> 偏低(黄)
     * 其余                                 -> 正常(绿)
     */
    private String judgeStockStatus(Material m) {
        BigDecimal cur = m.getCurrentStock() == null ? BigDecimal.ZERO : m.getCurrentStock();
        BigDecimal threshold = m.getMinThreshold() == null ? BigDecimal.ZERO : m.getMinThreshold();
        if (cur.compareTo(threshold) < 0) {
            return "预警";
        }
        if (cur.compareTo(threshold.multiply(new BigDecimal("1.5"))) < 0) {
            return "偏低";
        }
        return "正常";
    }
}
