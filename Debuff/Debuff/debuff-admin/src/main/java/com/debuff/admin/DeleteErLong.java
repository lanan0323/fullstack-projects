// 包声明
package com.debuff.admin;

// 导入SQL相关类
import java.sql.*;

// 删除二龙传说相关数据的工具类（一次性脚本）
public class DeleteErLong {
    // 主方法：连接数据库并执行删除操作
    public static void main(String[] args) {
        // 数据库连接地址
        String url = "jdbc:mysql://192.168.120.39:3306/Debuff?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=false&serverTimezone=GMT%2B8";
        // 数据库用户名
        String username = "root";
        // 数据库密码
        String password = "1234";
        
        // 使用try-with-resources自动关闭数据库连接
        try (Connection conn = DriverManager.getConnection(url, username, password)) {
            System.out.println("========== 开始删除二龙传说相关数据 ==========");
            
            // 1. 查询二龙传说的 ID
            // 查询包含"二龙"的饰品记录
            String selectSql = "SELECT id, skin_name FROM debuff_skin WHERE skin_name LIKE '%二龙%'";
            // 创建Statement对象执行SQL
            Statement stmt = conn.createStatement();
            // 执行查询并获取结果集
            ResultSet rs = stmt.executeQuery(selectSql);
            
            // 标记是否找到匹配记录
            boolean found = false;
            // 遍历查询结果
            while (rs.next()) {
                found = true;
                // 打印找到的饰品信息
                System.out.println("找到饰品 - ID: " + rs.getInt("id") + ", 名称: " + rs.getString("skin_name"));
            }
            // 关闭结果集
            rs.close();
            
            // 如果未找到相关饰品则退出
            if (!found) {
                System.out.println("未找到二龙传说相关饰品");
                return;
            }
            
            // 2. 删除挂单记录
            // 删除与二龙传说相关的挂单数据
            String deleteListingSql = "DELETE FROM debuff_skin_listing WHERE skin_id IN (SELECT id FROM debuff_skin WHERE skin_name LIKE '%二龙%')";
            // 执行删除并获取影响行数
            int listingCount = stmt.executeUpdate(deleteListingSql);
            System.out.println("删除挂单记录数：" + listingCount);
            
            // 3. 删除库存记录
            // 删除与二龙传说相关的库存数据
            String deleteInventorySql = "DELETE FROM debuff_inventory WHERE skin_id IN (SELECT id FROM debuff_skin WHERE skin_name LIKE '%二龙%')";
            // 执行删除并获取影响行数
            int inventoryCount = stmt.executeUpdate(deleteInventorySql);
            System.out.println("删除库存记录数：" + inventoryCount);
            
            // 4. 删除饰品定义
            // 删除二龙传说的饰品定义记录
            String deleteSkinSql = "DELETE FROM debuff_skin WHERE skin_name LIKE '%二龙%'";
            // 执行删除并获取影响行数
            int skinCount = stmt.executeUpdate(deleteSkinSql);
            System.out.println("删除饰品定义数：" + skinCount);
            
            // 5. 验证删除结果
            // 查询是否还有残留的二龙传说饰品
            rs = stmt.executeQuery("SELECT COUNT(*) as cnt FROM debuff_skin WHERE skin_name LIKE '%二龙%'");
            if (rs.next()) {
                // 获取剩余数量
                long remaining = rs.getLong("cnt");
                System.out.println("剩余二龙传说饰品数：" + remaining);
            }
            // 关闭结果集和Statement
            rs.close();
            stmt.close();
            
            System.out.println("========== 删除完成 ==========");
            
        } catch (SQLException e) {
            // 捕获并打印数据库操作异常
            System.err.println("数据库操作失败：" + e.getMessage());
            e.printStackTrace();
        }
    }
}
