package com.milktea.product.service.impl;

import com.github.pagehelper.PageHelper;
import com.milktea.common.exception.BusinessException;
import com.milktea.common.result.PageResult;
import com.milktea.model.dto.ProductDTO;
import com.milktea.model.entity.Product;
import com.milktea.model.vo.ProductVO;
import com.milktea.product.mapper.ProductMapper;
import com.milktea.product.service.ProductService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductMapper productMapper;

    public ProductServiceImpl(ProductMapper productMapper) {
        this.productMapper = productMapper;
    }

    @Override
    public PageResult<ProductVO> page(Integer pageNum, Integer pageSize,
                                      Long categoryId, String keyword, Integer status) {
        if (pageNum == null || pageNum < 1) {
            pageNum = 1;
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = 10;
        }
        PageHelper.startPage(pageNum, pageSize);
        List<ProductVO> list = productMapper.page(categoryId, keyword, status);
        return PageResult.of(list);
    }

    @Override
    public ProductVO getById(Long id) {
        ProductVO vo = productMapper.findById(id);
        if (vo == null) {
            throw new BusinessException("商品不存在");
        }
        vo.setSpecs(productMapper.findSpecsByProductId(id));
        return vo;
    }

    @Override
    public List<ProductVO> listOnSale() {
        return productMapper.findOnSale();
    }

    @Override
    @Transactional
    public void add(ProductDTO dto) {
        checkProduct(dto);
        Product product = new Product();
        copyProperties(dto, product);
        productMapper.insert(product);
        saveSpecs(product.getId(), dto.getSpecOptionIds());
    }

    @Override
    @Transactional
    public void update(ProductDTO dto) {
        if (dto.getId() == null) {
            throw new BusinessException("商品ID不能为空");
        }
        if (productMapper.findById(dto.getId()) == null) {
            throw new BusinessException("商品不存在");
        }
        checkProduct(dto);
        Product product = new Product();
        copyProperties(dto, product);
        productMapper.update(product);
        productMapper.deleteProductSpecByProductId(dto.getId());
        saveSpecs(dto.getId(), dto.getSpecOptionIds());
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (productMapper.findById(id) == null) {
            throw new BusinessException("商品不存在");
        }
        productMapper.deleteById(id);
        productMapper.deleteProductSpecByProductId(id);
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        if (status == null || (status != 0 && status != 1)) {
            throw new BusinessException("状态值不合法");
        }
        if (productMapper.findById(id) == null) {
            throw new BusinessException("商品不存在");
        }
        productMapper.updateStatus(id, status);
    }

    private void checkProduct(ProductDTO dto) {
        if (dto.getProductName() == null || dto.getProductName().trim().isEmpty()) {
            throw new BusinessException("商品名称不能为空");
        }
        if (dto.getCategoryId() == null) {
            throw new BusinessException("请选择商品分类");
        }
        if (dto.getPrice() == null || dto.getPrice().doubleValue() <= 0) {
            throw new BusinessException("商品价格必须大于0");
        }
        if (dto.getStatus() == null) {
            dto.setStatus(1);
        }
        if (dto.getSort() == null) {
            dto.setSort(0);
        }
    }

    private void copyProperties(ProductDTO dto, Product product) {
        product.setId(dto.getId());
        product.setCategoryId(dto.getCategoryId());
        product.setProductName(dto.getProductName());
        product.setPrice(dto.getPrice());
        product.setDescription(dto.getDescription());
        product.setImageUrl(dto.getImageUrl());
        product.setStatus(dto.getStatus());
        product.setSort(dto.getSort());
    }

    private void saveSpecs(Long productId, List<Long> specOptionIds) {
        if (specOptionIds == null || specOptionIds.isEmpty()) {
            return;
        }
        for (Long optionId : specOptionIds) {
            productMapper.insertProductSpec(productId, optionId);
        }
    }
}
