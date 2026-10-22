// 包声明
package com.debuff.system.service;

// 导入集合类和操作日志实体类
import java.util.List;
import com.debuff.system.domain.SysOperLog;

/**
 * 操作日志 服务层
 * 
 * @author ruoyi
 */
// 操作日志服务接口，定义操作日志的增删查方法
public interface ISysOperLogService
{
    /**
     * 新增操作日志
     * 
     * @param operLog 操作日志对象
     */
    // 插入一条操作日志记录
    public void insertOperlog(SysOperLog operLog);

    /**
     * 查询系统操作日志集合
     * 
     * @param operLog 操作日志对象
     * @return 操作日志集合
     */
    // 根据条件分页查询操作日志列表
    public List<SysOperLog> selectOperLogList(SysOperLog operLog);

    /**
     * 批量删除系统操作日志
     * 
     * @param operIds 需要删除的操作日志ID
     * @return 结果
     */
    // 批量删除多条操作日志
    public int deleteOperLogByIds(Long[] operIds);

    /**
     * 查询操作日志详细
     * 
     * @param operId 操作ID
     * @return 操作日志对象
     */
    // 根据ID查询单条操作日志详情
    public SysOperLog selectOperLogById(Long operId);

    /**
     * 清空操作日志
     */
    // 清空所有操作日志记录
    public void cleanOperLog();
}
