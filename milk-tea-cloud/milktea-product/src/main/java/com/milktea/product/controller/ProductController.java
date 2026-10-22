package com.milktea.product.controller;

import com.milktea.common.result.PageResult;
import com.milktea.common.result.Result;
import com.milktea.model.dto.ProductDTO;
import com.milktea.model.vo.ProductVO;
import com.milktea.product.service.ProductService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/product")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/page")
    public Result<PageResult<ProductVO>> page(@RequestParam(required = false) Integer pageNum,
                                              @RequestParam(required = false) Integer pageSize,
                                              @RequestParam(required = false) Long categoryId,
                                              @RequestParam(required = false) String keyword,
                                              @RequestParam(required = false) Integer status) {
        return Result.success(productService.page(pageNum, pageSize, categoryId, keyword, status));
    }

    @GetMapping("/{id}")
    public Result<ProductVO> getById(@PathVariable Long id) {
        return Result.success(productService.getById(id));
    }

    @GetMapping("/list")
    public Result<List<ProductVO>> listOnSale() {
        return Result.success(productService.listOnSale());
    }

    @PostMapping
    public Result<Void> add(@RequestBody ProductDTO dto) {
        productService.add(dto);
        return Result.success();
    }

    @PutMapping
    public Result<Void> update(@RequestBody ProductDTO dto) {
        productService.update(dto);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        productService.delete(id);
        return Result.success();
    }

    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        productService.updateStatus(id, status);
        return Result.success();
    }
}
