// 包声明
package com.debuff.trade.service.impl;

// 导入所需类
import java.util.List;
import com.debuff.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.debuff.trade.mapper.WalletLogMapper;
import com.debuff.trade.domain.WalletLog;
import com.debuff.trade.service.IWalletLogService;

/**
 * 钱包流水Service业务层处理
 * 
 * @author debuff
 * @date 2026-08-25
 */
@Service // 标记为Spring服务组件
public class WalletLogServiceImpl implements IWalletLogService // 实现钱包流水服务接口
{
    @Autowired // 自动注入钱包流水Mapper
    private WalletLogMapper walletLogMapper;

    /**
     * 查询钱包流水
     * 
     * @param id 钱包流水主键
     * @return 钱包流水
     */
    @Override
    public WalletLog selectWalletLogById(Long id)
    {
        return walletLogMapper.selectWalletLogById(id); // 委托给Mapper根据ID查询流水
    }

    /**
     * 查询钱包流水列表
     * 
     * @param walletLog 钱包流水
     * @return 钱包流水
     */
    @Override
    public List<WalletLog> selectWalletLogList(WalletLog walletLog)
    {
        return walletLogMapper.selectWalletLogList(walletLog); // 委托给Mapper查询流水列表
    }

    /**
     * 新增钱包流水
     * 
     * @param walletLog 钱包流水
     * @return 结果
     */
    @Override
    public int insertWalletLog(WalletLog walletLog)
    {
        walletLog.setCreateTime(DateUtils.getNowDate()); // 设置创建时间为当前时间
        return walletLogMapper.insertWalletLog(walletLog); // 委托给Mapper执行新增
    }

    /**
     * 修改钱包流水
     * 
     * @param walletLog 钱包流水
     * @return 结果
     */
    @Override
    public int updateWalletLog(WalletLog walletLog)
    {
        walletLog.setUpdateTime(DateUtils.getNowDate()); // 设置更新时间为当前时间
        return walletLogMapper.updateWalletLog(walletLog); // 委托给Mapper执行更新
    }

    /**
     * 批量删除钱包流水
     * 
     * @param ids 需要删除的钱包流水主键
     * @return 结果
     */
    @Override
    public int deleteWalletLogByIds(Long[] ids)
    {
        return walletLogMapper.deleteWalletLogByIds(ids); // 委托给Mapper执行批量删除
    }

    /**
     * 删除钱包流水信息
     * 
     * @param id 钱包流水主键
     * @return 结果
     */
    @Override
    public int deleteWalletLogById(Long id)
    {
        return walletLogMapper.deleteWalletLogById(id); // 委托给Mapper执行单条删除
    }
}
