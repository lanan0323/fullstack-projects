// 包声明
package com.debuff.system.mapper;

// 导入集合类和登录日志实体类
import java.util.List;
import com.debuff.system.domain.SysLogininfor;

/**
 * 系统访问日志情况信息 数据层
 * 
 * @author ruoyi
 */
// 登录日志Mapper接口，定义登录日志的增删查方法
public interface SysLogininforMapper
{
    /**
     * 新增系统登录日志
     * 
     * @param logininfor 访问日志对象
     */
    // 插入一条登录日志记录
    public void insertLogininfor(SysLogininfor logininfor);

    /**
     * 查询系统登录日志集合
     * 
     * @param logininfor 访问日志对象
     * @return 登录记录集合
     */
    // 根据条件分页查询登录日志列表
    public List<SysLogininfor> selectLogininforList(SysLogininfor logininfor);

    /**
     * 批量删除系统登录日志
     * 
     * @param infoIds 需要删除的登录日志ID
     * @return 结果
     */
    // 批量删除多条登录日志
    public int deleteLogininforByIds(Long[] infoIds);

    /**
     * 清空系统登录日志
     * 
     * @return 结果
     */
    // 清空所有登录日志记录
    public int cleanLogininfor();
}
