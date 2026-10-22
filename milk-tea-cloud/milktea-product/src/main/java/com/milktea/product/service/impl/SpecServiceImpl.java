package com.milktea.product.service.impl;

import com.milktea.common.exception.BusinessException;
import com.milktea.model.entity.SpecOption;
import com.milktea.model.entity.SpecType;
import com.milktea.product.mapper.SpecOptionMapper;
import com.milktea.product.mapper.SpecTypeMapper;
import com.milktea.product.service.SpecService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SpecServiceImpl implements SpecService {

    private final SpecTypeMapper specTypeMapper;
    private final SpecOptionMapper specOptionMapper;

    public SpecServiceImpl(SpecTypeMapper specTypeMapper, SpecOptionMapper specOptionMapper) {
        this.specTypeMapper = specTypeMapper;
        this.specOptionMapper = specOptionMapper;
    }

    @Override
    public List<SpecType> listTypes() {
        return specTypeMapper.findAll();
    }

    @Override
    public SpecType getTypeById(Long id) {
        SpecType type = specTypeMapper.findById(id);
        if (type == null) {
            throw new BusinessException("规格类型不存在");
        }
        return type;
    }

    @Override
    public void addType(SpecType specType) {
        if (specType.getSpecName() == null || specType.getSpecName().trim().isEmpty()) {
            throw new BusinessException("规格名称不能为空");
        }
        if (specType.getSpecCode() == null || specType.getSpecCode().trim().isEmpty()) {
            specType.setSpecCode(specType.getSpecName());
        }
        if (specType.getSort() == null) {
            specType.setSort(0);
        }
        if (specType.getStatus() == null) {
            specType.setStatus(1);
        }
        specTypeMapper.insert(specType);
    }

    @Override
    public void updateType(SpecType specType) {
        if (specType.getId() == null) {
            throw new BusinessException("规格类型ID不能为空");
        }
        getTypeById(specType.getId());
        if (specType.getSpecName() == null || specType.getSpecName().trim().isEmpty()) {
            throw new BusinessException("规格名称不能为空");
        }
        specTypeMapper.update(specType);
    }

    @Override
    public void deleteType(Long id) {
        getTypeById(id);
        if (specTypeMapper.countOptionsByTypeId(id) > 0) {
            throw new BusinessException("该规格类型下存在选项，请先删除选项");
        }
        specTypeMapper.deleteById(id);
    }

    @Override
    public List<SpecOption> listOptions(Long typeId) {
        return specOptionMapper.findByTypeId(typeId);
    }

    @Override
    public void addOption(SpecOption specOption) {
        if (specOption.getSpecTypeId() == null) {
            throw new BusinessException("规格类型ID不能为空");
        }
        getTypeById(specOption.getSpecTypeId());
        if (specOption.getOptionName() == null || specOption.getOptionName().trim().isEmpty()) {
            throw new BusinessException("选项名称不能为空");
        }
        if (specOption.getExtraPrice() == null) {
            specOption.setExtraPrice(java.math.BigDecimal.ZERO);
        }
        if (specOption.getSort() == null) {
            specOption.setSort(0);
        }
        if (specOption.getStatus() == null) {
            specOption.setStatus(1);
        }
        specOptionMapper.insert(specOption);
    }

    @Override
    public void updateOption(SpecOption specOption) {
        if (specOption.getId() == null) {
            throw new BusinessException("选项ID不能为空");
        }
        if (specOptionMapper.findById(specOption.getId()) == null) {
            throw new BusinessException("规格选项不存在");
        }
        if (specOption.getOptionName() == null || specOption.getOptionName().trim().isEmpty()) {
            throw new BusinessException("选项名称不能为空");
        }
        specOptionMapper.update(specOption);
    }

    @Override
    public void deleteOption(Long id) {
        if (specOptionMapper.findById(id) == null) {
            throw new BusinessException("规格选项不存在");
        }
        specOptionMapper.deleteById(id);
    }
}
