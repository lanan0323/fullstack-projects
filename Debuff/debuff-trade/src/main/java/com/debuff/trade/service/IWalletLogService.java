// 包声明
package com.debuff.trade.service;

// 导入所需类
import java.util.List;
import com.debuff.trade.domain.WalletLog;

/**
 * 钱包流水Service接口
 * 
 * @author debuff
 * @date 2026-08-25
 */
public interface IWalletLogService // 钱包流水服务接口，定义流水管理业务方法
{
    /**
     * 查询钱包流水
     * 
     * @param id 钱包流水主键
     * @return 钱包流水
     */
    public WalletLog selectWalletLogById(Long id);

    /**
     * 查询钱包流水列表
     * 
     * @param walletLog 钱包流水
     * @return 钱包流水集合
     */
    public List<WalletLog> selectWalletLogList(WalletLog walletLog);

    /**
     * 新增钱包流水
     * 
     * @param walletLog 钱包流水
     * @return 结果
     */
    public int insertWalletLog(WalletLog walletLog);

    /**
     * 修改钱包流水
     * 
     * @param walletLog 钱包流水
     * @return 结果
     */
    public int updateWalletLog(WalletLog walletLog);

    /**
     * 批量删除钱包流水
     * 
     * @param ids 需要删除的钱包流水主键集合
     * @return 结果
     */
    public int deleteWalletLogByIds(Long[] ids);

    /**
     * 删除钱包流水信息
     * 
     * @param id 钱包流水主键
     * @return 结果
     */
    public int deleteWalletLogById(Long id);
}
