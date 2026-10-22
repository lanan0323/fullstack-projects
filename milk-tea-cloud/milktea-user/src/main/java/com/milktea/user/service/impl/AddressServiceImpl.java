package com.milktea.user.service.impl;

import com.milktea.common.exception.BusinessException;
import com.milktea.model.entity.Address;
import com.milktea.user.mapper.AddressMapper;
import com.milktea.user.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AddressServiceImpl implements AddressService {

    @Autowired
    private AddressMapper addressMapper;

    @Override
    public List<Address> listByUser(Long userId) {
        return addressMapper.selectByUserId(userId);
    }

    @Override
    public void add(Long userId, Address address) {
        validate(address);
        // 首个地址自动设为默认
        address.setIsDefault(addressMapper.countByUserId(userId) == 0 ? 1 : 0);
        address.setUserId(userId);
        addressMapper.insert(address);
    }

    @Override
    public void update(Long userId, Address address) {
        if (address.getId() == null) {
            throw new BusinessException("缺少地址ID");
        }
        checkOwner(userId, address.getId());
        validate(address);
        address.setUserId(userId);
        addressMapper.update(address);
    }

    @Override
    @Transactional
    public void delete(Long userId, Long id) {
        checkOwner(userId, id);
        Address addr = addressMapper.selectById(id);
        addressMapper.deleteById(id);
        // 删除的是默认地址时, 顺延把最新一条设为默认
        if (addr != null && addr.getIsDefault() == 1) {
            List<Address> rest = addressMapper.selectByUserId(userId);
            if (!rest.isEmpty()) {
                addressMapper.setDefault(rest.get(0).getId());
            }
        }
    }

    @Override
    @Transactional
    public void setDefault(Long userId, Long id) {
        checkOwner(userId, id);
        addressMapper.clearDefault(userId);
        addressMapper.setDefault(id);
    }

    private void checkOwner(Long userId, Long id) {
        Address addr = addressMapper.selectById(id);
        if (addr == null || !addr.getUserId().equals(userId)) {
            throw new BusinessException("地址不存在");
        }
    }

    private void validate(Address address) {
        if (address.getReceiver() == null || address.getReceiver().isBlank()) {
            throw new BusinessException("请填写收货人");
        }
        if (address.getPhone() == null || !address.getPhone().matches("^1\\d{10}$")) {
            throw new BusinessException("请填写正确的手机号");
        }
        if (address.getAddress() == null || address.getAddress().isBlank()) {
            throw new BusinessException("请填写收货地址");
        }
    }
}
