package com.milktea.product.service.impl;

import com.milktea.common.exception.BusinessException;
import com.milktea.model.entity.Category;
import com.milktea.product.mapper.CategoryMapper;
import com.milktea.product.mapper.ProductMapper;
import com.milktea.product.service.CategoryService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryMapper categoryMapper;
    private final ProductMapper productMapper;

    public CategoryServiceImpl(CategoryMapper categoryMapper, ProductMapper productMapper) {
        this.categoryMapper = categoryMapper;
        this.productMapper = productMapper;
    }

    @Override
    public List<Category> list() {
        return categoryMapper.findAll();
    }

    @Override
    public Category getById(Long id) {
        Category category = categoryMapper.findById(id);
        if (category == null) {
            throw new BusinessException("分类不存在");
        }
        return category;
    }

    @Override
    public void add(Category category) {
        if (category.getCategoryName() == null || category.getCategoryName().trim().isEmpty()) {
            throw new BusinessException("分类名称不能为空");
        }
        if (category.getParentId() == null) {
            category.setParentId(0L);
        }
        if (category.getSort() == null) {
            category.setSort(0);
        }
        if (category.getStatus() == null) {
            category.setStatus(1);
        }
        categoryMapper.insert(category);
    }

    @Override
    public void update(Category category) {
        if (category.getId() == null) {
            throw new BusinessException("分类ID不能为空");
        }
        getById(category.getId());
        if (category.getCategoryName() == null || category.getCategoryName().trim().isEmpty()) {
            throw new BusinessException("分类名称不能为空");
        }
        categoryMapper.update(category);
    }

    @Override
    public void delete(Long id) {
        getById(id);
        if (categoryMapper.countByParentId(id) > 0) {
            throw new BusinessException("该分类下存在子分类，无法删除");
        }
        if (productMapper.countByCategoryId(id) > 0) {
            throw new BusinessException("该分类下存在商品，无法删除");
        }
        categoryMapper.deleteById(id);
    }
}
