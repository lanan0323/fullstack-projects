package com.milktea.model.entity;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class Menu {
    private Long id;
    private Long parentId;
    private String name;
    private String path;
    private String component;
    private String icon;
    private Integer type;
    private String permission;
    private Integer sort;
    private Integer visible;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private List<Menu> children;
}
