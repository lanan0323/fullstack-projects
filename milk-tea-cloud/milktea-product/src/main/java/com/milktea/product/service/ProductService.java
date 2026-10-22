package com.milktea.product.service;

import com.milktea.common.result.PageResult;
import com.milktea.model.dto.ProductDTO;
import com.milktea.model.vo.ProductVO;

import java.util.List;

public interface ProductService {

    PageResult<ProductVO> page(Integer pageNum, Integer pageSize,
                               Long categoryId, String keyword, Integer status);

    ProductVO getById(Long id);

    List<ProductVO> listOnSale();

    void add(ProductDTO dto);

    void update(ProductDTO dto);

    void delete(Long id);

    void updateStatus(Long id, Integer status);
}
