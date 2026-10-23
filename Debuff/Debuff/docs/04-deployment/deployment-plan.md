# Debuff - CS2饰品交易平台部署运行方案

> 文档版本：v1.0  
> 项目版本：Debuff 1.0  
> 编写日期：2024年  
> 适用环境：Windows 开发环境 / Linux 生产环境

---

## 目录

1. [部署环境要求](#1-部署环境要求)
2. [环境搭建步骤](#2-环境搭建步骤)
3. [后端部署](#3-后端部署)
4. [前端部署](#4-前端部署)
5. [生产环境部署方案](#5-生产环境部署方案)
6. [系统运行与维护](#6-系统运行与维护)
7. [开发环境快速启动指南](#7-开发环境快速启动指南)

---

## 1. 部署环境要求

### 1.1 硬件要求

| 配置项 | 最低配置 | 推荐配置 |
|--------|----------|----------|
| CPU | 2核 | 4核及以上 |
| 内存 | 4GB | 8GB及以上 |
| 硬盘 | 20GB可用空间 | 50GB SSD |
| 网络 | 1Mbps | 10Mbps及以上 |

**说明：**
- 开发环境：最低配置即可满足日常开发调试需求
- 生产环境：建议使用推荐配置，以保证系统稳定运行
- 硬盘空间需考虑MySQL数据库、Redis缓存、日志文件等占用

### 1.2 软件要求

#### 操作系统
- **开发环境：** Windows 10/11 64位
- **生产环境：** CentOS 7.x/8.x 或 Ubuntu 20.04/22.04 LTS

#### 核心软件依赖

| 软件 | 版本要求 | 用途 | 必要性 |
|------|----------|------|--------|
| JDK | 17.0+ | Java运行环境 | 必需 |
| Maven | 3.8.6+ | 后端构建工具 | 必需 |
| Node.js | 16.0+ (推荐18.x LTS) | 前端运行环境 | 必需 |
| MySQL | 8.0.31+ | 关系型数据库 | 必需 |
| Redis | 6.0+ | 缓存服务 | 必需 |
| Nginx | 1.20+ | 反向代理/静态资源服务 | 生产环境必需 |
| Git | 2.30+ | 版本控制 | 必需 |

#### 软件版本兼容性说明
- JDK 17 为长期支持版本(LTS)，Spring Boot 2.5.15 完全兼容
- Maven 3.8.6 与 JDK 17 兼容，支持多模块项目构建
- Node.js 16+ 支持 Vue3 + Vite 5.0.4 构建需求
- MySQL 8.0.31 支持窗口函数、JSON数据类型等现代特性

### 1.3 网络要求

#### 端口开放
| 端口 | 服务 | 说明 |
|------|------|------|
| 8080 | 后端API服务 | Spring Boot应用端口 |
| 8081 | 前端开发服务器 | Vite开发模式端口 |
| 3306 | MySQL数据库 | 数据库连接端口 |
| 6379 | Redis服务 | 缓存服务端口 |
| 80/443 | Nginx | 生产环境Web服务端口 |

#### 网络配置要求
- 开发环境：localhost本地回环即可，无需外网访问
- 生产环境：需开放80/443端口供用户访问
- 防火墙配置：确保各服务端口可正常通信
- 域名配置（可选）：生产环境建议配置域名并申请SSL证书

---

## 2. 环境搭建步骤

### 2.1 JDK 17 安装与配置

#### Windows环境安装

1. **下载JDK 17**
   - 访问Oracle官网或采用OpenJDK发行版（如Adoptium）
   - 下载Windows x64 Installer (.msi)

2. **安装JDK**
   - 运行安装程序，建议安装路径：`D:\study\jdk17`
   - 记住安装路径，后续需要配置环境变量

3. **配置环境变量**
   - 右键"此电脑" → "属性" → "高级系统设置" → "环境变量"
   - 新建系统变量：
     ```
     变量名：JAVA_HOME
     变量值：D:\study\jdk17
     ```
   - 编辑Path变量，添加：
     ```
     %JAVA_HOME%\bin
     ```

4. **验证安装**
   ```bash
   java -version
   javac -version
   ```
   预期输出：
   ```
   java 17.0.x
   javac 17.0.x
   ```

#### Linux环境安装（生产环境）

```bash
# Ubuntu/Debian
sudo apt update
sudo apt install openjdk-17-jdk

# CentOS/RHEL
sudo yum install java-17-openjdk-devel

# 验证安装
java -version
```

### 2.2 Maven 安装与配置

#### Windows环境安装

1. **下载Maven**
   - 访问Maven官网：https://maven.apache.org/download.cgi
   - 下载Binary zip archive（如apache-maven-3.8.6-bin.zip）

2. **解压并配置**
   - 解压到指定目录，如：`D:\study\maven\apache-maven-3.8.6`
   - 配置环境变量：
     ```
     变量名：MAVEN_HOME
     变量值：D:\study\maven\apache-maven-3.8.6
     ```
   - 编辑Path变量，添加：
     ```
     %MAVEN_HOME%\bin
     ```

3. **配置本地仓库**
   - 编辑 `D:\study\maven\apache-maven-3.8.6\conf\settings.xml`
   - 配置本地仓库路径：
     ```xml
     <localRepository>D:\study\maven\maven_reposity</localRepository>
     ```

4. **配置镜像源（加速下载）**
   - 在settings.xml的`<mirrors>`标签内添加阿里云镜像：
     ```xml
     <mirrors>
       <mirror>
         <id>aliyunmaven</id>
         <mirrorOf>*</mirrorOf>
         <name>阿里云公共仓库</name>
         <url>https://maven.aliyun.com/repository/public</url>
       </mirror>
     </mirrors>
     ```

5. **验证安装**
   ```bash
   mvn -version
   ```
   预期输出：
   ```
   Apache Maven 3.8.6
   Maven home: D:\study\maven\apache-maven-3.8.6
   Java version: 17.0.x
   ```

### 2.3 Node.js 安装（使用nvm管理）

#### Windows环境安装

1. **下载nvm-windows**
   - 访问：https://github.com/coreybutler/nvm-windows/releases
   - 下载nvm-setup.exe并安装

2. **配置nvm**
   - 安装完成后，配置nvm镜像源（加速下载）：
     ```bash
     nvm node_mirror https://npmmirror.com/mirrors/node/
     nvm npm_mirror https://npmmirror.com/mirrors/npm/
     ```

3. **安装Node.js**
   ```bash
   # 安装Node.js 18.x LTS版本
   nvm install 18.18.0
   
   # 切换到该版本
   nvm use 18.18.0
   
   # 验证安装
   node -v
   npm -v
   ```

4. **配置npm镜像（可选）**
   ```bash
   npm config set registry https://registry.npmmirror.com
   ```

#### Linux环境安装

```bash
# 安装nvm
curl -o- https://raw.githubusercontent.com/nvm-sh/nvm/v0.39.0/install.sh | bash

# 重新加载配置
source ~/.bashrc

# 安装Node.js
nvm install 18.18.0
nvm use 18.18.0
nvm alias default 18.18.0

# 验证
node -v
npm -v
```

### 2.4 MySQL 8.0 安装与初始化

#### Windows环境安装

1. **下载MySQL**
   - 访问MySQL官网：https://dev.mysql.com/downloads/mysql/
   - 下载MySQL 8.0.31 MSI安装包

2. **安装MySQL**
   - 运行安装程序，选择"Developer Default"
   - 设置root密码：`1234`（开发环境）
   - 其他选项保持默认

3. **配置环境变量**
   - 添加MySQL的bin目录到Path：
     ```
     C:\Program Files\MySQL\MySQL Server 8.0\bin
     ```

4. **验证安装**
   ```bash
   mysql --version
   ```

#### Linux环境安装

```bash
# Ubuntu/Debian
sudo apt update
sudo apt install mysql-server-8.0

# CentOS/RHEL
sudo yum install mysql-community-server

# 启动MySQL
sudo systemctl start mysqld
sudo systemctl enable mysqld

# 获取临时密码
sudo grep 'temporary password' /var/log/mysqld.log

# 登录并修改密码
mysql -u root -p
```

#### 数据库初始化配置

1. **登录MySQL**
   ```bash
   mysql -u root -p
   ```

2. **创建数据库**
   ```sql
   -- 创建Debuff数据库
   CREATE DATABASE IF NOT EXISTS `Debuff` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
   
   -- 验证数据库创建
   SHOW DATABASES;
   ```

3. **字符集配置**
   - 编辑MySQL配置文件（my.ini或my.cnf）：
     ```ini
     [mysqld]
     character-set-server=utf8mb4
     collation-server=utf8mb4_general_ci
     init_connect='SET NAMES utf8mb4'
     
     [client]
     default-character-set=utf8mb4
     ```

4. **时区配置**
   ```sql
   -- 查看当前时区
   SELECT @@global.time_zone;
   
   -- 设置为东八区（中国标准时间）
   SET GLOBAL time_zone = '+8:00';
   ```

### 2.5 Redis 安装与启动

#### Windows环境安装

1. **下载Redis**
   - Windows版Redis：https://github.com/tporadowski/redis/releases
   - 下载Redis-x64-5.0.14.1.zip（或更新版本）

2. **解压并启动**
   ```bash
   # 解压到指定目录，如：D:\study\redis
   
   # 启动Redis服务
   redis-server redis.windows.conf
   
   # 或注册为Windows服务（推荐）
   redis-server --service-install redis.windows.conf
   redis-server --service-start
   ```

3. **验证安装**
   ```bash
   redis-cli ping
   ```
   预期输出：`PONG`

#### Linux环境安装

```bash
# Ubuntu/Debian
sudo apt update
sudo apt install redis-server

# CentOS/RHEL
sudo yum install redis

# 启动Redis
sudo systemctl start redis
sudo systemctl enable redis

# 验证
redis-cli ping
```

#### Redis配置优化（开发环境）

编辑redis.conf或redis.windows.conf：
```conf
# 绑定地址（开发环境可绑定localhost）
bind 127.0.0.1

# 端口
port 6379

# 设置密码（生产环境必须设置）
# requirepass your_password

# 最大内存
maxmemory 256mb

# 内存淘汰策略
maxmemory-policy allkeys-lru
```

### 2.6 数据库创建与初始化

#### SQL脚本执行顺序

项目SQL脚本位于：`Debuff/sql/` 目录

1. **执行基础表结构脚本**
   ```bash
   # 登录MySQL
   mysql -u root -p
   
   # 选择数据库
   use Debuff;
   
   # 执行若依基础表结构
   source D:/study/Debuff/Debuff/sql/ry_20231019.sql
   ```

2. **执行业务表结构脚本**
   ```bash
   # 执行Debuff业务表结构
   source D:/study/Debuff/Debuff/sql/debuff_trade.sql
   ```

3. **验证数据导入**
   ```sql
   -- 查看所有表
   SHOW TABLES;
   
   -- 预期看到以下表（部分示例）：
   -- sys_user, sys_role, sys_menu, sys_dept
   -- trade_item, trade_order, trade_category
   ```

#### 数据初始化说明

- **系统表数据：** 包含默认管理员账号、角色、菜单等
- **业务表数据：** 包含CS2饰品分类、初始配置数据等
- **默认管理员账号：** admin / admin123

---

## 3. 后端部署

### 3.1 Maven构建

#### 构建命令

```bash
# 进入后端项目根目录
cd D:\study\Debuff\Debuff

# 清理并打包（跳过测试）
mvn clean package -DskipTests

# 或指定JDK路径构建
mvn clean package -DskipTests -Djava.home=D:\study\jdk17
```

#### 构建产物

构建成功后，生成jar包位于：
```
Debuff\debuff-admin\target\debuff-admin.jar
```

#### 构建常见问题

**问题1：依赖下载失败**
```bash
# 解决：检查Maven镜像配置，使用阿里云镜像
mvn clean package -DskipTests -U
```

**问题2：JDK版本不匹配**
```bash
# 解决：确认JAVA_HOME指向JDK 17
echo %JAVA_HOME%
java -version
```

### 3.2 配置文件修改

#### application.yml 主配置

文件位置：`Debuff\debuff-admin\src\main\resources\application.yml`

```yaml
# 应用配置
server:
  port: 8080  # 后端服务端口

# Spring配置
spring:
  application:
    name: debuff-admin
  
  # 数据源配置
  datasource:
    type: com.alibaba.druid.pool.DruidDataSource
    driver-class-name: com.mysql.cj.jdbc.Driver
    
  # Redis配置
  redis:
    host: localhost
    port: 6379
    password:  # 开发环境无密码
    database: 0
    timeout: 10s

# 日志配置
logging:
  level:
    root: info
    com.debuff: debug
  file:
    path: ./logs
```

#### application-druid.yml 数据源配置

文件位置：`Debuff\debuff-admin\src\main\resources\application-druid.yml`

```yaml
# 数据源配置
spring:
  datasource:
    druid:
      # 主库配置
      master:
        url: jdbc:mysql://localhost:3306/Debuff?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8
        username: root
        password: 1234
      
      # 连接池配置
      initial-size: 5
      min-idle: 10
      max-active: 20
      max-wait: 60000
      
      # 连接检测
      validation-query: SELECT 1 FROM DUAL
      test-while-idle: true
      test-on-borrow: false
      test-on-return: false
```

### 3.3 启动后端服务

#### 开发环境启动

```bash
# 方式1：直接运行jar包
java -jar debuff-admin\target\debuff-admin.jar

# 方式2：指定JDK路径
D:\study\jdk17\bin\java -jar debuff-admin\target\debuff-admin.jar

# 方式3：后台运行（Linux）
nohup java -jar debuff-admin.jar > backend.log 2>&1 &
```

#### 启动参数说明

```bash
java -jar debuff-admin.jar \
  --server.port=8080 \
  --spring.datasource.druid.master.url=jdbc:mysql://localhost:3306/Debuff \
  --spring.redis.host=localhost
```

#### 启动验证

1. **查看启动日志**
   ```
   启动成功后会看到类似输出：
   Started DebuffApplication in xx.xxx seconds
   ```

2. **访问API接口**
   - 浏览器访问：http://localhost:8080
   - 预期返回：若依框架默认JSON响应

3. **查看端口占用**
   ```bash
   # Windows
   netstat -ano | findstr 8080
   
   # Linux
   netstat -tlnp | grep 8080
   ```

### 3.4 后端常见问题与解决

**问题1：数据库连接失败**
```
错误信息：Communications link failure
解决：
1. 检查MySQL服务是否启动
2. 确认数据库名、用户名、密码正确
3. 检查MySQL端口3306是否可访问
```

**问题2：Redis连接失败**
```
错误信息：Cannot get Jedis connection
解决：
1. 检查Redis服务是否启动
2. 确认Redis host和port配置正确
3. 如有密码，配置spring.redis.password
```

**问题3：端口被占用**
```
错误信息：Port 8080 was already in use
解决：
1. 修改application.yml中的server.port
2. 或关闭占用8080端口的程序
```

**问题4：内存溢出**
```bash
# 解决：增加JVM堆内存
java -Xms512m -Xmx1024m -jar debuff-admin.jar
```

---

## 4. 前端部署

### 4.1 开发模式运行

#### 安装依赖

```bash
# 进入前端项目目录
cd D:\study\Debuff\Debuff-ui

# 安装npm依赖
npm install

# 或使用淘宝镜像加速
npm install --registry=https://registry.npmmirror.com
```

#### 启动开发服务器

```bash
# 启动开发服务器
npm run dev
```

启动成功后输出：
```
VITE v5.0.4  ready in xxx ms

➜  Local:   http://localhost:8081/
➜  Network: use --host to expose
```

#### 访问系统

- 浏览器打开：http://localhost:8081
- 默认管理员账号：admin / admin123

#### 开发环境代理配置

文件位置：`Debuff-ui\vite.config.js`

```javascript
export default defineConfig({
  // ...
  server: {
    port: 8081,  // 前端开发端口
    host: '0.0.0.0',
    proxy: {
      // 代理配置：将/dev-api请求转发到后端
      '/dev-api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/dev-api/, '')
      }
    }
  }
})
```

### 4.2 生产环境构建

#### 构建命令

```bash
# 构建生产版本
npm run build:prod
```

#### 构建产物

构建成功后，生成静态资源位于：
```
Debuff-ui\dist\
├── index.html
├── assets\
│   ├── css\
│   ├── js\
│   └── img\
└── favicon.ico
```

#### 构建常见问题

**问题1：依赖安装失败**
```bash
# 解决：清除npm缓存
npm cache clean --force
rm -rf node_modules package-lock.json
npm install
```

**问题2：构建内存不足**
```bash
# 解决：增加Node.js内存
export NODE_OPTIONS="--max-old-space-size=4096"
npm run build:prod
```

### 4.3 环境变量说明

#### .env.development 开发环境

文件位置：`Debuff-ui\.env.development`

```bash
# 开发环境配置
ENV = 'development'

# 接口前缀（会被vite代理处理）
VITE_APP_BASE_API = '/dev-api'

# 是否启用Mock数据
VITE_APP_USE_MOCK = 'false'
```

#### .env.production 生产环境

文件位置：`Debuff-ui\.env.production`

```bash
# 生产环境配置
ENV = 'production'

# 接口前缀（生产环境直接访问后端）
VITE_APP_BASE_API = '/api'

# 是否启用Mock数据
VITE_APP_USE_MOCK = 'false'
```

#### 环境变量使用

在代码中通过 `import.meta.env.VITE_APP_BASE_API` 访问环境变量。

### 4.4 Nginx配置（生产环境）

详见第5章"生产环境部署方案"。

---

## 5. 生产环境部署方案

### 5.1 Nginx完整配置

#### 安装Nginx

```bash
# Ubuntu/Debian
sudo apt update
sudo apt install nginx

# CentOS/RHEL
sudo yum install epel-release
sudo yum install nginx

# 启动Nginx
sudo systemctl start nginx
sudo systemctl enable nginx
```

#### Nginx配置文件

文件位置：`/etc/nginx/conf.d/debuff.conf`

```nginx
# Debuff CS2饰品交易平台 - Nginx配置

# 上游后端服务
upstream debuff_backend {
    server 127.0.0.1:8080;
    keepalive 32;
}

# HTTP服务器配置
server {
    listen 80;
    server_name your-domain.com;  # 替换为实际域名或IP
    
    # 前端静态资源
    root /var/www/debuff/dist;
    index index.html;
    
    # 前端路由 - Vue Router History模式
    location / {
        try_files $uri $uri/ /index.html;
        
        # 静态资源缓存
        location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg|woff|woff2|ttf|eot)$ {
            expires 30d;
            add_header Cache-Control "public, immutable";
        }
    }
    
    # API反向代理
    location /api/ {
        proxy_pass http://debuff_backend/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        
        # 连接超时
        proxy_connect_timeout 60s;
        proxy_send_timeout 60s;
        proxy_read_timeout 60s;
        
        # 缓冲配置
        proxy_buffering on;
        proxy_buffer_size 4k;
        proxy_buffers 8 4k;
    }
    
    # WebSocket支持（若依实时通知）
    location /ws/ {
        proxy_pass http://debuff_backend/ws/;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "upgrade";
        proxy_set_header Host $host;
    }
    
    # 禁止访问隐藏文件
    location ~ /\. {
        deny all;
    }
    
    # 访问日志
    access_log /var/log/nginx/debuff_access.log;
    error_log /var/log/nginx/debuff_error.log;
}

# HTTPS配置（可选，需申请SSL证书）
# server {
#     listen 443 ssl http2;
#     server_name your-domain.com;
#     
#     ssl_certificate /etc/nginx/ssl/your-domain.crt;
#     ssl_certificate_key /etc/nginx/ssl/your-domain.key;
#     
#     # 其他配置同上...
# }
# 
# # HTTP重定向到HTTPS
# server {
#     listen 80;
#     server_name your-domain.com;
#     return 301 https://$server_name$request_uri;
# }
```

#### 部署前端静态资源

```bash
# 创建部署目录
sudo mkdir -p /var/www/debuff

# 复制构建产物
sudo cp -r Debuff-ui/dist/* /var/www/debuff/

# 设置权限
sudo chown -R www-data:www-data /var/www/debuff
sudo chmod -R 755 /var/www/debuff
```

#### 验证Nginx配置

```bash
# 测试配置文件
sudo nginx -t

# 重载配置
sudo nginx -s reload
```

### 5.2 后端jar包部署

#### 部署目录结构

```bash
/opt/debuff/
├── debuff-admin.jar          # 后端jar包
├── config/                   # 配置文件
│   ├── application.yml
│   └── application-druid.yml
├── logs/                     # 日志目录
└── scripts/                  # 启动脚本
    ├── start.sh
    └── stop.sh
```

#### 启动脚本 start.sh

```bash
#!/bin/bash

APP_NAME=debuff-admin.jar
APP_PATH=/opt/debuff
LOG_PATH=$APP_PATH/logs

# 检查是否已运行
PID=$(ps -ef | grep $APP_NAME | grep -v grep | awk '{ print $2 }')
if [ -n "$PID" ]; then
    echo "应用已在运行，PID: $PID"
    exit 1
fi

# 启动应用
echo "启动Debuff后端服务..."
nohup java \
    -Xms512m \
    -Xmx1024m \
    -XX:+UseG1GC \
    -jar $APP_PATH/$APP_NAME \
    --spring.config.location=$APP_PATH/config/ \
    > $LOG_PATH/startup.log 2>&1 &

echo "启动完成，PID: $!"
```

#### 停止脚本 stop.sh

```bash
#!/bin/bash

APP_NAME=debuff-admin.jar

PID=$(ps -ef | grep $APP_NAME | grep -v grep | awk '{ print $2 }')

if [ -z "$PID" ]; then
    echo "应用未运行"
    exit 0
fi

echo "停止应用，PID: $PID"
kill $PID

# 等待进程结束
for i in {1..30}; do
    if ! ps -p $PID > /dev/null 2>&1; then
        echo "应用已停止"
        exit 0
    fi
    sleep 1
done

# 强制停止
echo "强制停止应用"
kill -9 $PID
```

#### 部署步骤

```bash
# 1. 创建部署目录
sudo mkdir -p /opt/debuff/{config,logs,scripts}

# 2. 复制jar包
cp debuff-admin/target/debuff-admin.jar /opt/debuff/

# 3. 复制配置文件
cp debuff-admin/src/main/resources/application*.yml /opt/debuff/config/

# 4. 复制启动脚本
cp scripts/*.sh /opt/debuff/scripts/
chmod +x /opt/debuff/scripts/*.sh

# 5. 启动应用
cd /opt/debuff
./scripts/start.sh
```

### 5.3 Redis配置优化

#### 生产环境配置

编辑 `/etc/redis/redis.conf`：

```conf
# 绑定地址（仅允许本地访问）
bind 127.0.0.1

# 设置密码（必须）
requirepass YourStrongPassword

# 最大内存限制
maxmemory 1gb

# 内存淘汰策略
maxmemory-policy allkeys-lru

# 持久化配置
save 900 1
save 300 10
save 60 10000

# AOF持久化
appendonly yes
appendfsync everysec

# 慢查询日志
slowlog-log-slower-than 10000
slowlog-max-len 128
```

#### 重启Redis

```bash
sudo systemctl restart redis
```

#### 更新后端配置

修改 `application.yml`：

```yaml
spring:
  redis:
    host: localhost
    port: 6379
    password: YourStrongPassword
    database: 0
```

### 5.4 MySQL配置优化

#### 生产环境配置

编辑 `/etc/mysql/mysql.conf.d/mysqld.cnf`：

```ini
[mysqld]
# 基础配置
port = 3306
bind-address = 127.0.0.1
datadir = /var/lib/mysql
socket = /var/run/mysqld/mysqld.sock
pid-file = /var/run/mysqld/mysqld.pid

# 字符集
character-set-server = utf8mb4
collation-server = utf8mb4_general_ci

# 连接数
max_connections = 200
max_connect_errors = 1000

# InnoDB配置
innodb_buffer_pool_size = 1G
innodb_log_file_size = 256M
innodb_flush_log_at_trx_commit = 2

# 查询缓存
query_cache_type = 0
query_cache_size = 0

# 日志
log_error = /var/log/mysql/error.log
slow_query_log = 1
slow_query_log_file = /var/log/mysql/slow.log
long_query_time = 2

# 时区
default-time-zone = '+08:00'
```

#### 重启MySQL

```bash
sudo systemctl restart mysql
```

### 5.5 日志管理

#### 后端日志配置

修改 `application.yml`：

```yaml
logging:
  level:
    root: info
    com.debuff: info
  file:
    path: /opt/debuff/logs
  logback:
    rollingpolicy:
      max-file-size: 100MB
      max-history: 30
      total-size-cap: 3GB
```

#### 日志轮转配置

创建 `/etc/logrotate.d/debuff`：

```
/opt/debuff/logs/*.log {
    daily
    rotate 30
    missingok
    notifempty
    compress
    delaycompress
    copytruncate
}
```

#### Nginx日志轮转

```bash
# 编辑 /etc/logrotate.d/nginx
/var/log/nginx/*.log {
    daily
    rotate 14
    missingok
    notifempty
    compress
    delaycompress
    sharedscripts
    postrotate
        [ -f /var/run/nginx.pid ] && kill -USR1 `cat /var/run/nginx.pid`
    endscript
}
```

---

## 6. 系统运行与维护

### 6.1 服务启动顺序

#### 正确的启动顺序

```bash
# 1. 启动MySQL
sudo systemctl start mysql

# 2. 启动Redis
sudo systemctl start redis

# 3. 启动后端服务
cd /opt/debuff
./scripts/start.sh

# 4. 启动Nginx
sudo systemctl start nginx
```

#### 启动检查脚本

创建 `check-services.sh`：

```bash
#!/bin/bash

echo "=== Debuff服务状态检查 ==="

# 检查MySQL
if systemctl is-active --quiet mysql; then
    echo "✓ MySQL: 运行中"
else
    echo "✗ MySQL: 未运行"
fi

# 检查Redis
if systemctl is-active --quiet redis; then
    echo "✓ Redis: 运行中"
else
    echo "✗ Redis: 未运行"
fi

# 检查后端服务
if curl -s http://localhost:8080 > /dev/null; then
    echo "✓ 后端服务: 运行中"
else
    echo "✗ 后端服务: 未运行"
fi

# 检查Nginx
if systemctl is-active --quiet nginx; then
    echo "✓ Nginx: 运行中"
else
    echo "✗ Nginx: 未运行"
fi
```

### 6.2 健康检查

#### 后端健康检查接口

若依框架提供健康检查端点：

```bash
# 检查后端服务
curl http://localhost:8080/actuator/health

# 预期返回
{"status":"UP"}
```

#### 数据库连接检查

```bash
mysql -u root -p -e "SHOW DATABASES LIKE 'Debuff';"
```

#### Redis连接检查

```bash
redis-cli ping
```

#### 自动化健康检查脚本

创建 `health-check.sh`：

```bash
#!/bin/bash

LOG_FILE=/opt/debuff/logs/health-check.log

echo "$(date '+%Y-%m-%d %H:%M:%S') - 开始健康检查" >> $LOG_FILE

# 检查后端API
HTTP_CODE=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8080)
if [ $HTTP_CODE -eq 200 ]; then
    echo "✓ 后端API正常" >> $LOG_FILE
else
    echo "✗ 后端API异常，HTTP状态码: $HTTP_CODE" >> $LOG_FILE
    # 可添加告警通知逻辑
fi

# 检查数据库
if mysql -u root -p1234 -e "use Debuff" 2>/dev/null; then
    echo "✓ 数据库连接正常" >> $LOG_FILE
else
    echo "✗ 数据库连接失败" >> $LOG_FILE
fi

# 检查Redis
if redis-cli ping | grep -q PONG; then
    echo "✓ Redis连接正常" >> $LOG_FILE
else
    echo "✗ Redis连接失败" >> $LOG_FILE
fi

echo "========================" >> $LOG_FILE
```

#### 定时健康检查

```bash
# 添加crontab定时任务，每5分钟检查一次
crontab -e

# 添加以下行
*/5 * * * * /opt/debuff/scripts/health-check.sh
```

### 6.3 数据备份策略

#### MySQL数据库备份

**全量备份脚本** `backup-mysql.sh`：

```bash
#!/bin/bash

BACKUP_DIR=/opt/debuff/backup/mysql
DATE=$(date +%Y%m%d_%H%M%S)
BACKUP_FILE=$BACKUP_DIR/debuff_$DATE.sql

# 创建备份目录
mkdir -p $BACKUP_DIR

# 执行备份
mysqldump -u root -p1234 \
    --single-transaction \
    --routines \
    --triggers \
    --databases Debuff \
    > $BACKUP_FILE

# 压缩备份
gzip $BACKUP_FILE

# 删除30天前的备份
find $BACKUP_DIR -name "debuff_*.sql.gz" -mtime +30 -delete

echo "备份完成: ${BACKUP_FILE}.gz"
```

**定时备份配置**

```bash
# 编辑crontab
crontab -e

# 每天凌晨2点执行全量备份
0 2 * * * /opt/debuff/scripts/backup-mysql.sh
```

#### Redis数据备份

Redis默认开启AOF持久化，数据文件位于：
```
/var/lib/redis/dump.rdb      # RDB快照
/var/lib/redis/appendonly.aof # AOF日志
```

**手动备份Redis数据**

```bash
# 触发RDB快照
redis-cli BGSAVE

# 备份RDB文件
cp /var/lib/redis/dump.rdb /opt/debuff/backup/redis/dump_$(date +%Y%m%d).rdb
```

#### 备份恢复

**恢复MySQL数据库**

```bash
# 解压备份文件
gunzip debuff_20240101_020000.sql.gz

# 恢复数据
mysql -u root -p < debuff_20240101_020000.sql
```

**恢复Redis数据**

```bash
# 停止Redis
sudo systemctl stop redis

# 恢复RDB文件
cp dump_20240101.rdb /var/lib/redis/dump.rdb
chown redis:redis /var/lib/redis/dump.rdb

# 启动Redis
sudo systemctl start redis
```

### 6.4 日志监控

#### 实时查看日志

```bash
# 查看后端日志
tail -f /opt/debuff/logs/debuff.log

# 查看Nginx访问日志
tail -f /var/log/nginx/debuff_access.log

# 查看Nginx错误日志
tail -f /var/log/nginx/debuff_error.log

# 查看MySQL错误日志
tail -f /var/log/mysql/error.log
```

#### 日志分析命令

```bash
# 统计API访问次数
awk '{print $7}' /var/log/nginx/debuff_access.log | sort | uniq -c | sort -rn | head -20

# 查看404错误
grep " 404 " /var/log/nginx/debuff_access.log

# 查看500错误
grep " 500 " /var/log/nginx/debuff_access.log

# 统计IP访问频率
awk '{print $1}' /var/log/nginx/debuff_access.log | sort | uniq -c | sort -rn
```

#### 日志告警

创建 `log-alert.sh`：

```bash
#!/bin/bash

LOG_FILE=/var/log/nginx/debuff_error.log
ALERT_EMAIL=admin@example.com

# 统计最近5分钟的500错误数
ERROR_COUNT=$(tail -n 1000 $LOG_FILE | \
    awk -v d="$(date -d '5 minutes ago' '+%d/%b/%Y:%H:%M')" \
    '$4 > d && $9 ~ /500/ {count++} END {print count+0}')

if [ $ERROR_COUNT -gt 10 ]; then
    echo "警告：最近5分钟内有 $ERROR_COUNT 个500错误" | \
        mail -s "Debuff系统告警" $ALERT_EMAIL
fi
```

### 6.5 常见问题排查

#### 问题1：系统无法访问

**排查步骤：**

```bash
# 1. 检查服务是否运行
./check-services.sh

# 2. 检查端口监听
netstat -tlnp | grep -E '80|8080|3306|6379'

# 3. 检查防火墙
sudo ufw status
sudo iptables -L

# 4. 查看Nginx错误日志
tail -f /var/log/nginx/debuff_error.log

# 5. 测试后端API
curl http://localhost:8080
```

**常见原因：**
- 服务未启动
- 防火墙阻止端口
- Nginx配置错误
- 后端服务崩溃

#### 问题2：数据库连接失败

**排查步骤：**

```bash
# 1. 检查MySQL服务
sudo systemctl status mysql

# 2. 测试数据库连接
mysql -u root -p -e "SHOW DATABASES;"

# 3. 检查连接数
mysql -u root -p -e "SHOW STATUS LIKE 'Threads_connected';"

# 4. 查看MySQL错误日志
tail -f /var/log/mysql/error.log

# 5. 检查后端配置
cat /opt/debuff/config/application-druid.yml | grep -A5 "master:"
```

**常见原因：**
- MySQL服务未启动
- 用户名或密码错误
- 数据库不存在
- 连接数达到上限

#### 问题3：Redis连接失败

**排查步骤：**

```bash
# 1. 检查Redis服务
sudo systemctl status redis

# 2. 测试Redis连接
redis-cli ping

# 3. 检查Redis配置
cat /etc/redis/redis.conf | grep -E "bind|port|requirepass"

# 4. 查看Redis日志
tail -f /var/log/redis/redis.log
```

**常见原因：**
- Redis服务未启动
- 密码配置错误
- 内存达到上限

#### 问题4：前端页面白屏

**排查步骤：**

```bash
# 1. 检查静态资源是否存在
ls -la /var/www/debuff/

# 2. 检查Nginx配置
sudo nginx -t

# 3. 查看浏览器控制台错误
# F12打开开发者工具，查看Console和Network标签

# 4. 检查API接口
curl http://localhost:8080/captchaImage
```

**常见原因：**
- 静态资源路径错误
- API接口无法访问
- 前端构建失败
- 浏览器缓存问题

#### 问题5：系统响应缓慢

**排查步骤：**

```bash
# 1. 检查系统资源
top
free -h
df -h

# 2. 检查MySQL慢查询
mysql -u root -p -e "SHOW VARIABLES LIKE 'slow_query_log';"
tail -f /var/log/mysql/slow.log

# 3. 检查Redis性能
redis-cli --latency

# 4. 检查JVM内存
jstat -gc $(pgrep -f debuff-admin.jar) 1000

# 5. 查看后端日志
tail -f /opt/debuff/logs/debuff.log | grep -E "ERROR|WARN"
```

**优化建议：**
- 增加MySQL索引
- 优化Redis缓存策略
- 增加JVM堆内存
- 启用Nginx gzip压缩
- 配置静态资源CDN

---

## 7. 开发环境快速启动指南

### 7.1 前置条件检查

#### 环境检查脚本

创建 `check-env.sh`：

```bash
#!/bin/bash

echo "=== Debuff开发环境检查 ==="

# 检查JDK
if command -v java &> /dev/null; then
    JAVA_VERSION=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}')
    echo "✓ JDK: $JAVA_VERSION"
else
    echo "✗ JDK: 未安装"
fi

# 检查Maven
if command -v mvn &> /dev/null; then
    MVN_VERSION=$(mvn -version | head -1 | awk '{print $3}')
    echo "✓ Maven: $MVN_VERSION"
else
    echo "✗ Maven: 未安装"
fi

# 检查Node.js
if command -v node &> /dev/null; then
    NODE_VERSION=$(node -v)
    echo "✓ Node.js: $NODE_VERSION"
else
    echo "✗ Node.js: 未安装"
fi

# 检查MySQL
if command -v mysql &> /dev/null; then
    MYSQL_VERSION=$(mysql --version | awk '{print $3}')
    echo "✓ MySQL: $MYSQL_VERSION"
else
    echo "✗ MySQL: 未安装"
fi

# 检查Redis
if command -v redis-cli &> /dev/null; then
    if redis-cli ping | grep -q PONG; then
        echo "✓ Redis: 运行中"
    else
        echo "✗ Redis: 未运行"
    fi
else
    echo "✗ Redis: 未安装"
fi

echo "========================"
```

### 7.2 一键启动脚本

#### 完整启动脚本 `startup.sh`

```bash
#!/bin/bash

echo "=========================================="
echo "  Debuff CS2饰品交易平台 - 开发环境启动"
echo "=========================================="

# 项目根目录
PROJECT_ROOT=$(pwd)
BACKEND_DIR="$PROJECT_ROOT/Debuff"
FRONTEND_DIR="$PROJECT_ROOT/Debuff-ui"

# 1. 检查环境
echo ""
echo "[1/6] 检查开发环境..."
bash check-env.sh

# 2. 启动MySQL
echo ""
echo "[2/6] 启动MySQL..."
if systemctl is-active --quiet mysql; then
    echo "✓ MySQL已在运行"
else
    sudo systemctl start mysql
    echo "✓ MySQL已启动"
fi

# 3. 启动Redis
echo ""
echo "[3/6] 启动Redis..."
if systemctl is-active --quiet redis; then
    echo "✓ Redis已在运行"
else
    sudo systemctl start redis
    echo "✓ Redis已启动"
fi

# 4. 初始化数据库（首次运行）
echo ""
echo "[4/6] 检查数据库..."
if mysql -u root -p1234 -e "use Debuff" 2>/dev/null; then
    echo "✓ 数据库已存在"
else
    echo "→ 创建数据库..."
    mysql -u root -p1234 -e "CREATE DATABASE Debuff DEFAULT CHARACTER SET utf8mb4;"
    echo "→ 导入数据表..."
    mysql -u root -p1234 Debuff < "$BACKEND_DIR/sql/ry_20231019.sql"
    mysql -u root -p1234 Debuff < "$BACKEND_DIR/sql/debuff_trade.sql"
    echo "✓ 数据库初始化完成"
fi

# 5. 构建并启动后端
echo ""
echo "[5/6] 启动后端服务..."
cd "$BACKEND_DIR"

# 检查jar包是否存在
if [ ! -f "debuff-admin/target/debuff-admin.jar" ]; then
    echo "→ 构建后端项目..."
    mvn clean package -DskipTests
fi

# 启动后端
if curl -s http://localhost:8080 > /dev/null; then
    echo "✓ 后端服务已在运行"
else
    echo "→ 启动后端..."
    java -jar debuff-admin/target/debuff-admin.jar > ../backend.log 2>&1 &
    BACKEND_PID=$!
    echo "✓ 后端服务已启动，PID: $BACKEND_PID"
    
    # 等待后端启动
    echo "→ 等待后端启动..."
    for i in {1..30}; do
        if curl -s http://localhost:8080 > /dev/null; then
            echo "✓ 后端服务就绪"
            break
        fi
        sleep 2
    done
fi

# 6. 启动前端
echo ""
echo "[6/6] 启动前端服务..."
cd "$FRONTEND_DIR"

# 检查依赖
if [ ! -d "node_modules" ]; then
    echo "→ 安装前端依赖..."
    npm install
fi

# 启动前端
echo "→ 启动前端开发服务器..."
npm run dev &
FRONTEND_PID=$!

echo ""
echo "=========================================="
echo "  启动完成！"
echo "=========================================="
echo ""
echo "前端地址: http://localhost:8081"
echo "后端地址: http://localhost:8080"
echo "数据库: localhost:3306/Debuff"
echo "Redis: localhost:6379"
echo ""
echo "默认账号: admin"
echo "默认密码: admin123"
echo ""
echo "前端PID: $FRONTEND_PID"
echo "后端PID: $BACKEND_PID"
echo ""
echo "按 Ctrl+C 停止所有服务"
echo "=========================================="

# 等待用户中断
wait
```

#### 停止脚本 `shutdown.sh`

```bash
#!/bin/bash

echo "停止Debuff服务..."

# 停止前端
pkill -f "vite"
echo "✓ 前端服务已停止"

# 停止后端
pkill -f "debuff-admin.jar"
echo "✓ 后端服务已停止"

# 停止MySQL（可选）
# sudo systemctl stop mysql
# echo "✓ MySQL已停止"

# 停止Redis（可选）
# sudo systemctl stop redis
# echo "✓ Redis已停止"

echo "服务已全部停止"
```

### 7.3 从Git Clone到系统运行

#### 完整流程

```bash
# 1. 克隆项目
git clone <repository-url>
cd Debuff

# 2. 检查环境
bash check-env.sh

# 3. 初始化数据库
mysql -u root -p1234 -e "CREATE DATABASE IF NOT EXISTS Debuff DEFAULT CHARACTER SET utf8mb4;"
mysql -u root -p1234 Debuff < Debuff/sql/ry_20231019.sql
mysql -u root -p1234 Debuff < Debuff/sql/debuff_trade.sql

# 4. 构建后端
cd Debuff
mvn clean package -DskipTests

# 5. 启动后端
java -jar debuff-admin/target/debuff-admin.jar &

# 6. 安装并启动前端
cd ../Debuff-ui
npm install
npm run dev

# 7. 访问系统
# 浏览器打开: http://localhost:8081
# 账号: admin / admin123
```

#### 一键启动

```bash
# 赋予执行权限
chmod +x startup.sh shutdown.sh

# 启动所有服务
./startup.sh

# 停止所有服务
./shutdown.sh
```

### 7.4 答辩演示准备

#### 演示前检查清单

- [ ] 所有服务正常运行（MySQL、Redis、后端、前端）
- [ ] 数据库已初始化，包含演示数据
- [ ] 浏览器已登录系统（admin/admin123）
- [ ] 准备演示脚本（用户注册、饰品发布、交易流程等）
- [ ] 关闭无关通知和弹窗
- [ ] 准备系统架构图和技术栈说明

#### 演示账号准备

```sql
-- 创建演示账号
INSERT INTO sys_user (user_id, dept_id, user_name, nick_name, password, status) 
VALUES (100, 100, 'demo', '演示用户', '$2a$10$...', '0');

-- 分配角色
INSERT INTO sys_user_role (user_id, role_id) VALUES (100, 2);
```

#### 演示数据准备

确保数据库包含：
- 多个CS2饰品分类（刀具、手套、步枪、手枪等）
- 若干饰品商品数据
- 示例交易订单
- 用户评价数据

### 7.5 开发环境目录说明

```
Debuff/
├── Debuff/                    # 后端项目
│   ├── debuff-admin/         # 启动模块
│   │   ├── src/main/java/    # 启动类
│   │   ├── src/main/resources/ # 配置文件
│   │   └── target/           # 构建产物
│   ├── debuff-framework/     # 框架核心
│   ├── debuff-system/        # 系统模块
│   ├── debuff-common/        # 通用工具
│   ├── debuff-quartz/        # 定时任务
│   ├── debuff-generator/     # 代码生成
│   ├── debuff-trade/         # 交易模块（业务）
│   ├── sql/                  # SQL脚本
│   └── pom.xml               # Maven配置
│
├── Debuff-ui/                # 前端项目
│   ├── src/
│   │   ├── api/             # API接口
│   │   ├── assets/          # 静态资源
│   │   ├── components/      # 组件
│   │   ├── layout/          # 布局
│   │   ├── router/          # 路由
│   │   ├── store/           # 状态管理
│   │   ├── utils/           # 工具函数
│   │   └── views/           # 页面
│   ├── public/              # 公共资源
│   ├── dist/                # 构建产物
│   ├── .env.development     # 开发环境配置
│   ├── .env.production      # 生产环境配置
│   ├── vite.config.js       # Vite配置
│   └── package.json         # npm配置
│
└── docs/                    # 项目文档
    ├── 01-requirements/     # 需求分析
    ├── 02-design/          # 系统设计
    ├── 03-deployment/      # 部署运维
    └── 04-defense/         # 答辩材料
```

### 7.6 常用开发命令

#### 后端开发

```bash
# 编译项目
mvn clean compile

# 运行测试
mvn test

# 打包（跳过测试）
mvn clean package -DskipTests

# 运行单个模块
mvn -pl debuff-admin -am clean package -DskipTests

# 查看依赖树
mvn dependency:tree
```

#### 前端开发

```bash
# 安装依赖
npm install

# 开发模式
npm run dev

# 代码检查
npm run lint

# 构建生产版本
npm run build:prod

# 预览生产版本
npm run preview
```

#### 数据库操作

```bash
# 登录MySQL
mysql -u root -p1234

# 备份数据库
mysqldump -u root -p1234 Debuff > backup.sql

# 恢复数据库
mysql -u root -p1234 Debuff < backup.sql

# 查看数据库大小
mysql -u root -p1234 -e "SELECT table_schema AS 'Database', ROUND(SUM(data_length + index_length) / 1024 / 1024, 2) AS 'Size (MB)' FROM information_schema.tables WHERE table_schema = 'Debuff' GROUP BY table_schema;"
```

---

## 附录

### A. 端口占用处理

```bash
# Windows - 查找占用端口的进程
netstat -ano | findstr :8080

# 杀死进程
taskkill /PID <PID> /F

# Linux - 查找占用端口的进程
lsof -i :8080

# 杀死进程
kill -9 <PID>
```

### B. Maven常用配置

#### settings.xml 完整示例

```xml
<?xml version="1.0" encoding="UTF-8"?>
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0
                              http://maven.apache.org/xsd/settings-1.0.0.xsd">
  
  <!-- 本地仓库 -->
  <localRepository>D:\study\maven\maven_reposity</localRepository>
  
  <!-- 镜像配置 -->
  <mirrors>
    <mirror>
      <id>aliyunmaven</id>
      <mirrorOf>*</mirrorOf>
      <name>阿里云公共仓库</name>
      <url>https://maven.aliyun.com/repository/public</url>
    </mirror>
  </mirrors>
  
  <!--  profiles -->
  <profiles>
    <profile>
      <id>jdk-17</id>
      <activation>
        <activeByDefault>true</activeByDefault>
        <jdk>17</jdk>
      </activation>
      <properties>
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
        <maven.compiler.compilerVersion>17</maven.compiler.compilerVersion>
      </properties>
    </profile>
  </profiles>
  
</settings>
```

### C. 系统架构图

```
┌─────────────────────────────────────────────────────────┐
│                      用户浏览器                          │
│                  http://localhost:8081                   │
└────────────────────┬────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────┐
│              Vue3 + Vite 前端开发服务器                    │
│                    端口: 8081                            │
│              (开发环境代理 /dev-api → 8080)               │
└────────────────────┬────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────┐
│              Spring Boot 后端服务                         │
│                    端口: 8080                            │
│  ┌──────────┬──────────┬──────────┬──────────┐         │
│  │  admin   │framework │  system  │  trade   │         │
│  │  启动模块 │ 框架核心  │ 系统模块  │ 交易模块  │         │
│  └──────────┴──────────┴──────────┴──────────┘         │
└─────────┬──────────────────┬────────────────────────────┘
          │                  │
          ▼                  ▼
┌─────────────────┐  ┌─────────────────┐
│   MySQL 8.0     │  │     Redis       │
│   端口: 3306    │  │   端口: 6379    │
│   数据库: Debuff │  │   缓存服务       │
└─────────────────┘  └─────────────────┘
```

### D. 技术栈版本清单

| 技术 | 版本 | 说明 |
|------|------|------|
| Spring Boot | 2.5.15 | 后端框架 |
| JDK | 17 | Java运行环境 |
| Maven | 3.8.6 | 构建工具 |
| Node.js | 18.18.0 | 前端运行环境 |
| Vue | 3.x | 前端框架 |
| Vite | 5.0.4 | 前端构建工具 |
| MySQL | 8.0.31 | 数据库 |
| Redis | 6.0+ | 缓存 |
| 若依(RuoYi-Vue) | 3.8.7 | 基础框架 |
| Druid | 1.2.x | 数据库连接池 |
| MyBatis | 3.5.x | ORM框架 |
| JWT | 0.11.x | Token认证 |

### E. 联系与支持

- 项目文档：`D:\study\Debuff\docs\`
- 后端日志：`D:\study\Debuff\backend.log`
- 前端日志：浏览器控制台（F12）
- 数据库日志：MySQL安装目录/logs/

---

**文档维护说明：**
- 本文档随项目迭代持续更新
- 配置变更时请同步更新文档
- 遇到问题及解决方案请补充到相应章节
- 版本升级时注意更新版本号信息

**最后更新：** 2024年
