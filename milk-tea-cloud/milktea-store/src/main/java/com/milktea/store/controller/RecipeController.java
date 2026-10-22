package com.milktea.store.controller;

import com.milktea.common.exception.BusinessException;
import com.milktea.common.result.Result;
import com.milktea.store.mapper.RecipeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 商品配方管理 Controller(管理端配置每杯用量)
 */
@RestController
@RequestMapping("/recipe")
public class RecipeController {

    @Autowired
    private RecipeMapper recipeMapper;

    /** 某商品配方列表 */
    @GetMapping("/{productId}")
    public Result<List<Map<String, Object>>> byProduct(@PathVariable Long productId) {
        return Result.success(recipeMapper.findByProductId(productId));
    }

    /** 保存配方(全量覆盖: 先删后插) */
    @PostMapping("/{productId}")
    @Transactional
    public Result<Void> save(@PathVariable Long productId, @RequestBody List<Map<String, Object>> rows) {
        if (productId == null) throw new BusinessException("商品ID不能为空");
        recipeMapper.deleteByProductId(productId);
        if (rows != null) {
            for (Map<String, Object> row : rows) {
                Object mid = row.get("materialId");
                Object qty = row.get("quantity");
                if (mid == null || qty == null) continue;
                BigDecimal q = new BigDecimal(qty.toString());
                if (q.compareTo(BigDecimal.ZERO) <= 0) throw new BusinessException("用量必须大于0");
                recipeMapper.insert(productId, Long.valueOf(mid.toString()), q);
            }
        }
        return Result.success();
    }

    /** 清空配方(该商品不再自动扣原料) */
    @DeleteMapping("/{productId}")
    public Result<Void> clear(@PathVariable Long productId) {
        recipeMapper.deleteByProductId(productId);
        return Result.success();
    }
}
