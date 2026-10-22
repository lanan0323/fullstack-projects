// 包声明
package com.debuff.trade.service.impl;

// 导入所需类
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.debuff.common.exception.ServiceException;
import com.debuff.common.utils.StringUtils;
import com.debuff.trade.domain.Listing;
import com.debuff.trade.domain.entity.Inventory;
import com.debuff.trade.domain.entity.Skin;
import com.debuff.trade.domain.entity.SkinQuality;
import com.debuff.trade.domain.entity.WeaponType;
import com.debuff.trade.domain.query.SkinMarketQuery;
import com.debuff.trade.domain.vo.CategoryVO;
import com.debuff.trade.domain.vo.SkinDetailVO;
import com.debuff.trade.domain.vo.SkinMarketVO;
import com.debuff.trade.enums.WearLevel;
import com.debuff.trade.mapper.InventoryMapper;
import com.debuff.trade.mapper.ListingMapper;
import com.debuff.trade.mapper.SkinMapper;
import com.debuff.trade.mapper.SkinQualityMapper;
import com.debuff.trade.mapper.WeaponTypeMapper;
import com.debuff.trade.service.ISkinService;

/**
 * 饰品 服务层实现
 * 
 * @author B同学
 */
@Service // 注册为Spring服务Bean
public class SkinServiceImpl implements ISkinService // 饰品服务实现类
{
    /** 武器分类中文名映射 */
    private static final Map<String, String> CATEGORY_NAMES = new HashMap<String, String>()
    {
        private static final long serialVersionUID = 1L;
        {
            put("rifle", "步枪"); // 步枪分类
            put("pistol", "手枪"); // 手枪分类
            put("smg", "冲锋枪"); // 冲锋枪分类
            put("sniper", "狙击枪"); // 狙击枪分类
            put("shotgun", "霰弹枪"); // 霰弹枪分类
            put("knife", "刀"); // 刀分类
            put("glove", "手套"); // 手套分类
        }
    };

    /** 平台管理员用户ID（管理端上架时自动补货挂单） */
    private static final Long ADMIN_USER_ID = 1L;

    @Autowired // 自动注入饰品Mapper
    private SkinMapper skinMapper;

    @Autowired // 自动注入武器类型Mapper
    private WeaponTypeMapper weaponTypeMapper;

    @Autowired // 自动注入品质Mapper
    private SkinQualityMapper skinQualityMapper;

    @Autowired // 自动注入库存Mapper
    private InventoryMapper inventoryMapper;

    @Autowired // 自动注入在售Mapper
    private ListingMapper listingMapper;

    @Override
    public List<Skin> selectSkinList(Skin skin) // 查询饰品列表
    {
        return skinMapper.selectSkinList(skin); // 委托Mapper查询
    }

    @Override
    public Skin selectSkinById(Long skinId) // 根据ID查询饰品
    {
        return skinMapper.selectSkinById(skinId); // 委托Mapper查询
    }

    @Override
    public List<SkinMarketVO> selectMarketList(SkinMarketQuery query) // 查询市场列表
    {
        return skinMapper.selectMarketList(query); // 委托Mapper查询市场
    }

    @Override
    public List<SkinMarketVO> selectHotSkins(int limit) // 查询热门饰品
    {
        return skinMapper.selectHotSkins(limit); // 委托Mapper查询热门
    }

    @Override
    public Map<String, Object> selectCategories() // 查询分类数据（武器分类+品质）
    {
        List<WeaponType> weaponTypes = weaponTypeMapper.selectAllWeaponTypes(); // 查询所有武器类型
        List<SkinQuality> qualities = skinQualityMapper.selectAllSkinQualities(); // 查询所有品质

        // 按武器分类分组，保持 sort_order 顺序
        Map<String, CategoryVO> categoryMap = new LinkedHashMap<String, CategoryVO>();
        for (WeaponType weaponType : weaponTypes) // 遍历武器类型
        {
            String category = weaponType.getWeaponCategory(); // 获取分类标识
            CategoryVO categoryVO = categoryMap.get(category); // 尝试获取已有分类
            if (categoryVO == null) // 分类不存在则新建
            {
                categoryVO = new CategoryVO();
                categoryVO.setWeaponCategory(category); // 设置分类标识
                categoryVO.setCategoryName(CATEGORY_NAMES.getOrDefault(category, category)); // 设置中文名称
                categoryVO.setWeaponTypes(new ArrayList<WeaponType>()); // 初始化武器类型列表
                categoryMap.put(category, categoryVO); // 放入Map
            }
            categoryVO.getWeaponTypes().add(weaponType); // 添加武器类型到分类
        }

        Map<String, Object> result = new HashMap<String, Object>(); // 构建返回结果
        result.put("weaponCategories", new ArrayList<CategoryVO>(categoryMap.values())); // 武器分类列表
        result.put("qualities", qualities); // 品质列表
        return result; // 返回分类数据
    }

    @Override
    public SkinDetailVO selectSkinDetail(Long skinId) // 查询饰品详情
    {
        Skin skin = skinMapper.selectSkinById(skinId); // 查询饰品基本信息
        if (skin == null) // 饰品不存在
        {
            return null; // 返回null
        }
        SkinDetailVO detailVO = new SkinDetailVO(); // 构建详情VO
        detailVO.setSkinId(skin.getId()); // 设置饰品ID
        detailVO.setSkinName(skin.getSkinName()); // 设置饰品名称
        detailVO.setImageUrl(skin.getImageUrl()); // 设置图片URL
        detailVO.setDescription(skin.getDescription()); // 设置描述
        detailVO.setMinPrice(skin.getMinPrice()); // 设置最低价
        detailVO.setWeaponTypeId(skin.getWeaponTypeId()); // 设置武器类型ID
        detailVO.setWeaponName(skin.getWeaponName()); // 设置武器名称
        detailVO.setWeaponCategory(skin.getWeaponCategory()); // 设置武器分类
        detailVO.setQualityId(skin.getQualityId()); // 设置品质ID
        detailVO.setQualityName(skin.getQualityName()); // 设置品质名称
        detailVO.setStatus(skin.getStatus()); // 设置状态
        detailVO.setListings(skinMapper.selectListingsBySkinId(skinId)); // 查询在售列表
        detailVO.setPriceHistory(skinMapper.selectPriceHistoryBySkinId(skinId)); // 查询价格历史
        return detailVO; // 返回详情
    }

    @Override
    public List<SkinDetailVO.PricePoint> selectPriceHistory(Long skinId) // 查询价格历史
    {
        return skinMapper.selectPriceHistoryBySkinId(skinId); // 委托Mapper查询
    }

    @Override
    @Transactional // 事务管理
    public int insertSkin(Skin skin) // 新增饰品
    {
        if (StringUtils.isEmpty(skin.getStatus())) // 状态为空时设默认值
        {
            skin.setStatus("0"); // 默认上架状态
        }
        int rows = skinMapper.insertSkin(skin); // 插入数据库
        // 上架状态新增：立即补货挂单，保证用户端可购买
        if ("0".equals(skin.getStatus())) // 如果是上架状态
        {
            ensureBuyable(skin, skin.getMinPrice()); // 确保可购买（自动补货挂单）
        }
        return rows; // 返回结果
    }

    @Override
    @Transactional // 事务管理
    public int updateSkin(Skin skin) // 修改饰品
    {
        if (skin == null || skin.getId() == null) // 参数校验
        {
            throw new ServiceException("参数错误"); // 抛出异常
        }
        int rows = skinMapper.updateSkin(skin); // 更新数据库
        
        // 如果传入了floatValue，同步更新该饰品所有库存的磨损度
        if (skin.getFloatValue() != null) // 磨损度不为空
        {
            inventoryMapper.batchUpdateWearBySkinId(skin.getId(), skin.getFloatValue()); // 批量更新库存磨损度
        }
        
        // 编辑弹窗里改了状态：上架立即补货，下架撤回挂单
        if (!StringUtils.isEmpty(skin.getStatus())) // 状态有变更
        {
            Skin dbSkin = skinMapper.selectSkinById(skin.getId()); // 查询最新数据
            if (dbSkin != null) // 饰品存在
            {
                applyStatusCascade(dbSkin, skin.getStatus(), skin.getMinPrice()); // 执行状态联动
            }
        }
        return rows; // 返回结果
    }

    /**
     * 上架/下架（联动库存与挂单）：
     * 上架：若该饰品无在售挂单，自动补一条管理员库存并按最低价挂单，保证用户端可购买；
     * 下架：撤回该饰品全部在售挂单（0->2），在售库存退回在库（1->0）。
     */
    @Override
    @Transactional // 事务管理
    public int updateSkinStatus(Skin skin) // 修改饰品状态（联动库存和挂单）
    {
        if (skin == null || skin.getId() == null || StringUtils.isEmpty(skin.getStatus())) // 参数校验
        {
            throw new ServiceException("参数错误"); // 抛出异常
        }
        Skin dbSkin = skinMapper.selectSkinById(skin.getId()); // 查询数据库中的饰品
        if (dbSkin == null) // 饰品不存在
        {
            throw new ServiceException("饰品不存在"); // 抛出异常
        }
        applyStatusCascade(dbSkin, skin.getStatus(), skin.getMinPrice()); // 执行状态联动
        return skinMapper.updateSkinStatus(skin); // 更新状态
    }

    /**
     * 状态联动：上架自动补货挂单，下架撤回全部在售挂单
     */
    private void applyStatusCascade(Skin dbSkin, String status, BigDecimal requestPrice) // 状态联动处理
    {
        if ("0".equals(status)) // 上架
        {
            ensureBuyable(dbSkin, requestPrice); // 确保可购买
        }
        else // 下架
        {
            listingMapper.updateStatusBySkinId(dbSkin.getId(), 2); // 撤回全部在售挂单（状态改为2已下架）
            inventoryMapper.updateStatusBySkinId(dbSkin.getId(), 1, 0); // 在售库存退回在库
        }
    }

    /**
     * 保证饰品可购买：无在售挂单时，补一条管理员库存并按参考价挂单
     */
    private void ensureBuyable(Skin dbSkin, BigDecimal requestPrice) // 确保饰品可购买
    {
        if (!skinMapper.selectListingsBySkinId(dbSkin.getId()).isEmpty()) // 已有在售挂单
        {
            return; // 无需补货
        }
        BigDecimal price = requestPrice != null ? requestPrice : dbSkin.getMinPrice(); // 确定挂单价格
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) // 价格无效
        {
            throw new ServiceException("饰品「" + dbSkin.getSkinName() + "」需设置参考价（大于0）才能上架"); // 抛出异常
        }
        // 取管理员在库库存，没有则新建一条（直接置为在售）
        Inventory inventory = inventoryMapper.selectInStockBySkinIdAndUser(dbSkin.getId(), ADMIN_USER_ID); // 查询管理员库存
        if (inventory == null) // 库存不存在
        {
            inventory = new Inventory(); // 创建新库存
            inventory.setUserId(ADMIN_USER_ID); // 设为管理员
            inventory.setSkinId(dbSkin.getId()); // 关联饰品
            inventory.setFloatValue(new BigDecimal("0.00")); // 默认磨损度
            inventory.setWearName(WearLevel.getWearName(new BigDecimal("0.00"))); // 计算磨损名称
            inventory.setStatus(1); // 直接设为在售
            inventory.setSource("upload"); // 来源为上传
            inventoryMapper.insertInventory(inventory); // 插入库存
        }
        else // 库存存在
        {
            inventoryMapper.updateStatus(inventory.getId(), 0, 1); // 在库改为在售
        }
        // 按参考价挂单
        Listing listing = new Listing(); // 创建在售记录
        listing.setInventoryId(inventory.getId()); // 关联库存
        listing.setSellerId(ADMIN_USER_ID); // 卖家为管理员
        listing.setSkinId(dbSkin.getId()); // 关联饰品
        listing.setPrice(price); // 设置价格
        listingMapper.insertListing(listing); // 插入挂单
    }

    @Override
    @Transactional // 事务管理
    public int deleteSkinByIds(Long[] skinIds) // 批量删除饰品
    {
        if (skinIds == null || skinIds.length == 0) // 参数为空
        {
            return 0; // 返回0
        }
        for (Long skinId : skinIds) // 遍历每个饰品ID
        {
            Map<String, Object> refs = skinMapper.selectSkinRefCounts(skinId); // 查询关联数据数量
            long buyOrderCount = toLong(refs.get("buy_order_count")); // 求购数量
            long listingCount = toLong(refs.get("listing_count")); // 挂单数量
            Skin skin = skinMapper.selectSkinById(skinId); // 查询饰品信息
            String name = skin != null ? skin.getSkinName() : String.valueOf(skinId); // 获取饰品名称
            // 仅存在交易/求购记录时禁止删除（涉及资金与订单历史）
            if (buyOrderCount > 0 || listingCount > 0) // 有求购或挂单记录
            {
                throw new ServiceException("饰品「" + name + "」存在求购/挂单记录，无法删除"); // 禁止删除
            }
            // 无交易记录：级联清理该饰品下的挂单与库存后删除定义
            listingMapper.deleteBySkinId(skinId); // 删除挂单
            inventoryMapper.deleteBySkinId(skinId); // 删除库存
        }
        return skinMapper.deleteSkinByIds(skinIds); // 删除饰品定义
    }

    /**
     * 将数据库统计结果转为 long（兼容 Integer/Long/BigInteger）
     */
    private long toLong(Object value) // 对象转long工具方法
    {
        return value == null ? 0L : Long.parseLong(String.valueOf(value)); // 空值返回0，否则转换
    }
}
