// 包声明
package com.debuff.trade.service.impl;

// 导入所需类
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.debuff.common.utils.StringUtils;
import com.debuff.trade.domain.entity.Inventory;
import com.debuff.trade.domain.vo.InventoryVO;
import com.debuff.trade.enums.WearLevel;
import com.debuff.trade.mapper.InventoryMapper;
import com.debuff.trade.service.IInventoryService;

/**
 * 用户库存 服务层实现
 * 
 * @author B同学
 */
@Service // 注册为Spring服务Bean
public class InventoryServiceImpl implements IInventoryService // 库存服务实现类
{
    @Autowired // 自动注入库存Mapper
    private InventoryMapper inventoryMapper;

    @Override
    public List<InventoryVO> selectInventoryList(Long userId, Long weaponTypeId, Long qualityId, // 查询库存列表
            Integer status, String keyword)
    {
        return inventoryMapper.selectInventoryList(userId, weaponTypeId, qualityId, status, keyword); // 委托Mapper查询
    }

    @Override
    public InventoryVO selectInventoryDetail(Long id) // 查询库存详情
    {
        return inventoryMapper.selectInventoryById(id); // 委托Mapper查询详情
    }

    @Override
    public Inventory selectInventoryById(Long id) // 查询库存实体
    {
        return inventoryMapper.selectInventoryEntityById(id); // 查询实体对象（非VO）
    }

    @Override
    public int updateInventory(Inventory inventory) // 修改库存
    {
        // 磨损度变化时重新生成磨损名称
        if (inventory.getFloatValue() != null) // 磨损度不为空
        {
            inventory.setWearName(WearLevel.getWearName(inventory.getFloatValue())); // 根据磨损度计算磨损名称
        }
        return inventoryMapper.updateInventory(inventory); // 委托Mapper更新
    }

    @Override
    public int updateStatus(Long inventoryId, Integer fromStatus, Integer toStatus) // 条件更新库存状态
    {
        return inventoryMapper.updateStatus(inventoryId, fromStatus, toStatus); // 委托Mapper更新状态
    }

    @Override
    public int addInventory(Inventory inventory) // 新增库存
    {
        if (StringUtils.isEmpty(inventory.getWearName())) // 磨损名称为空时自动生成
        {
            inventory.setWearName(WearLevel.getWearName(inventory.getFloatValue())); // 根据磨损度计算
        }
        if (StringUtils.isEmpty(inventory.getSource())) // 来源为空时设默认值
        {
            inventory.setSource("purchase"); // 默认来源为购买
        }
        if (inventory.getStatus() == null) // 状态为空时设默认值
        {
            inventory.setStatus(Integer.valueOf(0)); // 默认状态为在库
        }
        return inventoryMapper.insertInventory(inventory); // 插入数据库
    }

    @Override
    public int deleteInventoryById(Long inventoryId) // 删除库存
    {
        return inventoryMapper.deleteInventoryById(inventoryId); // 委托Mapper删除
    }

    @Override
    public List<InventoryVO> selectAdminInventoryList(Long userId, Long skinId, Integer status, String keyword) // 管理员查询库存
    {
        return inventoryMapper.selectAdminInventoryList(userId, skinId, status, keyword); // 委托Mapper查询
    }
}
