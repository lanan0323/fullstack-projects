package com.milktea.model.dto;

import com.milktea.model.dto.DeductItem;
import lombok.Data;

import java.util.List;

/**
 * 下单扣减原料请求
 */
@Data
public class DeductRequest {
    private String orderNo;
    private List<DeductItem> items;
}
