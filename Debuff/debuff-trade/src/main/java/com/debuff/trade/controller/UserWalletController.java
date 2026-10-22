// 包声明
package com.debuff.trade.controller;

// 导入所需类
import java.math.BigDecimal;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.debuff.common.core.controller.BaseController;
import com.debuff.common.core.domain.AjaxResult;
import com.debuff.common.core.page.TableDataInfo;
import com.debuff.common.utils.SecurityUtils;
import com.debuff.trade.domain.WalletLog;
import com.debuff.trade.service.IWalletService;

/**
 * 我的钱包 前台接口（余额 / 流水 / 充值 / 提现）
 * 
 * @author debuff
 */
@RestController // 标记为REST控制器
@RequestMapping("/api/wallet") // 钱包API路径前缀
public class UserWalletController extends BaseController // 用户钱包控制器
{
    /** 单次充值上限 */
    private static final BigDecimal MAX_RECHARGE = new BigDecimal("100000");

    /** 单次提现上限 */
    private static final BigDecimal MAX_WITHDRAW = new BigDecimal("50000");

    @Autowired // 自动注入钱包服务
    private IWalletService walletService;

    /**
     * 查询我的钱包（无钱包自动创建）
     */
    @GetMapping("/my") // GET /api/wallet/my
    public AjaxResult my()
    {
        return success(walletService.getOrCreateWallet(SecurityUtils.getUserId())); // 获取当前用户钱包
    }

    /**
     * 查询我的流水（分页，按时间倒序）
     */
    @GetMapping("/logs") // GET /api/wallet/logs
    public TableDataInfo logs()
    {
        startPage(); // 启动PageHelper分页
        List<WalletLog> list = walletService.selectMyLogs(SecurityUtils.getUserId()); // 查询当前用户流水
        return getDataTable(list); // 返回分页数据
    }

    /**
     * 充值（模拟支付）
     */
    @PostMapping("/recharge") // POST /api/wallet/recharge
    public AjaxResult recharge(@RequestBody WalletAmountBody body) // 接收充值请求体
    {
        if (body.getAmount() == null || body.getAmount().compareTo(BigDecimal.ZERO) <= 0) // 金额校验：必须大于0
        {
            return error("充值金额必须大于0"); // 返回错误提示
        }
        if (body.getAmount().compareTo(MAX_RECHARGE) > 0) // 金额校验：不超过上限
        {
            return error("单次充值不能超过" + MAX_RECHARGE + "元"); // 返回超限提示
        }
        walletService.recharge(SecurityUtils.getUserId(), body.getAmount()); // 执行充值
        return success(); // 返回成功
    }

    /**
     * 提现
     */
    @PostMapping("/withdraw") // POST /api/wallet/withdraw
    public AjaxResult withdraw(@RequestBody WalletAmountBody body) // 接收提现请求体
    {
        if (body.getAmount() == null || body.getAmount().compareTo(BigDecimal.ZERO) <= 0) // 金额校验：必须大于0
        {
            return error("提现金额必须大于0"); // 返回错误提示
        }
        if (body.getAmount().compareTo(MAX_WITHDRAW) > 0) // 金额校验：不超过上限
        {
            return error("单次提现不能超过" + MAX_WITHDRAW + "元"); // 返回超限提示
        }
        walletService.withdraw(SecurityUtils.getUserId(), body.getAmount()); // 执行提现
        return success(); // 返回成功
    }

    /**
     * 金额请求体
     */
    public static class WalletAmountBody // 内部类：金额请求体
    {
        private BigDecimal amount; // 金额字段

        /** 获取金额 */
        public BigDecimal getAmount()
        {
            return amount; // 返回金额
        }

        /** 设置金额 */
        public void setAmount(BigDecimal amount)
        {
            this.amount = amount; // 设置金额
        }
    }
}
