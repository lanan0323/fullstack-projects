// 包声明
//package com.debuff.quartz.config;
//
// 导入所需类
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.scheduling.quartz.SchedulerFactoryBean;
//import javax.sql.DataSource;
//import java.util.Properties;
//
/**
 * 定时任务配置（单机部署建议删除此类和qrtz数据库表，默认走内存会最高效）
 * 
 * @author ruoyi
 */
// 注意：此类已被完全注释，单机模式下使用默认的内存Quartz调度
// 如需集群部署，取消注释并配置数据库持久化
//@Configuration
//public class ScheduleConfig
//{
//    @Bean
//    public SchedulerFactoryBean schedulerFactoryBean(DataSource dataSource) // 创建调度工厂Bean
//    {
//        SchedulerFactoryBean factory = new SchedulerFactoryBean();
//        factory.setDataSource(dataSource); // 设置数据源
//
//        // quartz参数
//        Properties prop = new Properties();
//        prop.put("org.quartz.scheduler.instanceName", "RuoyiScheduler"); // 调度器实例名称
//        prop.put("org.quartz.scheduler.instanceId", "AUTO"); // 实例ID自动生成
//        // 线程池配置
//        prop.put("org.quartz.threadPool.class", "org.quartz.simpl.SimpleThreadPool"); // 线程池实现类
//        prop.put("org.quartz.threadPool.threadCount", "20"); // 线程数量
//        prop.put("org.quartz.threadPool.threadPriority", "5"); // 线程优先级
//        // JobStore配置
//        prop.put("org.quartz.jobStore.class", "org.springframework.scheduling.quartz.LocalDataSourceJobStore"); // 使用数据库存储
//        // 集群配置
//        prop.put("org.quartz.jobStore.isClustered", "true"); // 启用集群模式
//        prop.put("org.quartz.jobStore.clusterCheckinInterval", "15000"); // 集群检查间隔15秒
//        prop.put("org.quartz.jobStore.maxMisfiresToHandleAtATime", "10"); // 最大misfire处理数
//        prop.put("org.quartz.jobStore.txIsolationLevelSerializable", "true"); // 序列化隔离级别
//
//        // sqlserver 启用
//        // prop.put("org.quartz.jobStore.selectWithLockSQL", "SELECT * FROM {0}LOCKS UPDLOCK WHERE LOCK_NAME = ?");
//        prop.put("org.quartz.jobStore.misfireThreshold", "12000"); // misfire阈值12秒
//        prop.put("org.quartz.jobStore.tablePrefix", "QRTZ_"); // 数据库表前缀
//        factory.setQuartzProperties(prop); // 设置Quartz属性
//
//        factory.setSchedulerName("RuoyiScheduler"); // 调度器名称
//        // 延时启动
//        factory.setStartupDelay(1); // 延迟1秒启动
//        factory.setApplicationContextSchedulerContextKey("applicationContextKey"); // Spring上下文Key
//        // 可选，QuartzScheduler
//        // 启动时更新己存在的Job，这样就不用每次修改targetObject后删除qrtz_job_details表对应记录了
//        factory.setOverwriteExistingJobs(true); // 覆盖已有任务
//        // 设置自动启动，默认为true
//        factory.setAutoStartup(true); // 自动启动
//
//        return factory;
//    }
//}
