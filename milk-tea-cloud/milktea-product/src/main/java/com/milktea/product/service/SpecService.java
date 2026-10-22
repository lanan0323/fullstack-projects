package com.milktea.product.service;

import com.milktea.model.entity.SpecOption;
import com.milktea.model.entity.SpecType;

import java.util.List;

public interface SpecService {

    List<SpecType> listTypes();

    SpecType getTypeById(Long id);

    void addType(SpecType specType);

    void updateType(SpecType specType);

    void deleteType(Long id);

    List<SpecOption> listOptions(Long typeId);

    void addOption(SpecOption specOption);

    void updateOption(SpecOption specOption);

    void deleteOption(Long id);
}
