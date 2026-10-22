package com.milktea.user.service;

import com.milktea.model.entity.Address;

import java.util.List;

public interface AddressService {

    /** 我的地址列表(默认地址排最前) */
    List<Address> listByUser(Long userId);

    /** 新增地址(首个地址自动设为默认) */
    void add(Long userId, Address address);

    /** 修改地址(校验归属) */
    void update(Long userId, Address address);

    /** 删除地址(校验归属) */
    void delete(Long userId, Long id);

    /** 设为默认地址 */
    void setDefault(Long userId, Long id);
}
