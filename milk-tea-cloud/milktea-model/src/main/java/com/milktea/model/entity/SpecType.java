package com.milktea.model.entity;

import lombok.Data;

@Data
public class SpecType {
    private Long id;
    private String specName;
    private String specCode;
    private Integer sort;
    private Integer status;
}
