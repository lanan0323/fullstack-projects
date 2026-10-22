# Debuff - CS2饰品交易平台 接口设计文档

> 版本：1.0.0  
> 最后更新：2025年  
> 项目后端：Spring Boot + MyBatis + Spring Security + JWT  
> 前端代理：`/dev-api` -> `http://localhost:8080`

---

## 目录

- [1. 概述](#1-概述)
  - [1.1 基础URL](#11-基础url)
  - [1.2 认证方式](#12-认证方式)
  - [1.3 统一响应格式](#13-统一响应格式)
  - [1.4 分页响应格式](#14-分页响应格式)
  - [1.5 通用请求头](#15-通用请求头)
  - [1.6 状态码说明](#16-状态码说明)
- [2. 前台接口 - 用户端](#2-前台接口---用户端)
  - [2.1 饰品市场模块](#21-饰品市场模块)
  - [2.2 用户库存模块](#22-用户库存模块)
  - [2.3 上架下架模块](#23-上架下架模块)
  - [2.4 交易订单模块](#24-交易订单模块)
  - [2.5 求购单模块](#25-求购单模块)
  - [2.6 钱包模块](#26-钱包模块)
  - [2.7 公告模块](#27-公告模块)
  - [2.8 收藏模块](#28-收藏模块)
- [3. 后台管理接口](#3-后台管理接口)
  - [3.1 饰品管理](#31-饰品管理)
  - [3.2 武器类型管理](#32-武器类型管理)
  - [3.3 品质管理](#33-品质管理)
  - [3.4 数据看板](#34-数据看板)
  - [3.5 订单管理](#35-订单管理)
  - [3.6 钱包管理](#36-钱包管理)
  - [3.7 流水管理](#37-流水管理)
  - [3.8 收藏管理](#38-收藏管理)
  - [3.9 公告管理](#39-公告管理)

---

## 1. 概述

### 1.1 基础URL

| 环境 | 基础URL |
|------|---------|
| 开发环境 | `http://localhost:8080` |
| 前端代理 | `/dev-api` -> `http://localhost:8080` |

所有接口路径均基于上述基础URL进行拼接。例如市场列表接口的完整请求地址为：

```
GET http://localhost:8080/api/market/list
```

前端通过 Vue 开发服务器代理转发：

```
GET /dev-api/api/market/list  ->  http://localhost:8080/api/market/list
```

### 1.2 认证方式

系统采用 **JWT (JSON Web Token)** 进行身份认证，基于 Spring Security 实现。

**认证流程：**

1. 用户通过登录接口（若依内置 `/login`）获取 Token
2. 后续请求在 HTTP Header 中携带 Token
3. 后端校验 Token 有效性，解析用户身份及权限

**Token 传递方式：**

```http
Authorization: Bearer <token>
```

**接口权限分级：**

| 级别 | 说明 | 示例 |
|------|------|------|
| 匿名访问 | 无需登录 | 市场列表、饰品详情、公开公告 |
| 登录用户 | 需携带有效Token | 库存管理、下单、钱包操作 |
| 管理员 | 需具备admin角色或特定权限 | 后台管理接口 |

**权限标识格式：** `模块:资源:操作`，如 `admin:skin:list`、`trade:order:add`。

### 1.3 统一响应格式

系统采用若依框架标准的 `AjaxResult` 作为统一响应封装。

**成功响应（无返回数据）：**

```json
{
  "code": 200,
  "msg": "操作成功"
}
```

**成功响应（携带数据）：**

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": { }
}
```

**失败响应：**

```json
{
  "code": 500,
  "msg": "操作失败：具体错误信息"
}
```

**AjaxResult 字段说明：**

| 字段 | 类型 | 说明 |
|------|------|------|
| code | Integer | 状态码，200表示成功，500表示失败，401表示未授权 |
| msg | String | 提示信息 |
| data | Object | 返回的业务数据（可选） |

### 1.4 分页响应格式

分页查询采用若依的 `TableDataInfo` 封装。

```json
{
  "code": 200,
  "msg": "查询成功",
  "total": 150,
  "rows": [ ]
}
```

**TableDataInfo 字段说明：**

| 字段 | 类型 | 说明 |
|------|------|------|
| code | Integer | 状态码 |
| msg | String | 提示信息 |
| total | Long | 总记录数 |
| rows | Array | 当前页数据列表 |

**通用分页请求参数：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| pageNum | Integer | 否 | 页码，默认1 |
| pageSize | Integer | 否 | 每页条数，默认10 |

### 1.5 通用请求头

| Header | 值 | 说明 |
|--------|-----|------|
| Content-Type | application/json | 请求体格式（POST/PUT请求） |
| Authorization | Bearer {token} | 认证令牌（需登录接口） |

### 1.6 状态码说明

| 状态码 | 说明 |
|--------|------|
| 200 | 操作成功 |
| 401 | 未认证或Token已过期 |
| 403 | 权限不足 |
| 500 | 服务器内部错误 / 操作失败 |

---

## 2. 前台接口 - 用户端

### 2.1 饰品市场模块

> 基础路径：`/api/market`  
> 权限要求：匿名访问（无需登录）

---

#### 2.1.1 市场列表（分页+筛选）

获取在售饰品的分页列表，支持多维度筛选和排序。

- **URL**：`GET /api/market/list`
- **权限**：匿名访问

**请求参数（Query）：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| pageNum | Integer | 否 | 页码，默认1 |
| pageSize | Integer | 否 | 每页条数，默认10 |
| weaponType | String | 否 | 武器类型ID，如 "1" |
| quality | String | 否 | 品质ID，如 "3" 代表久经沙场 |
| keyword | String | 否 | 关键词搜索（饰品名称） |
| sortBy | String | 否 | 排序字段：price / createTime |
| sortOrder | String | 否 | 排序方向：asc / desc，默认desc |

**请求示例：**

```
GET /api/market/list?pageNum=1&pageSize=10&weaponType=1&quality=3&sortBy=price&sortOrder=asc
```

**响应示例：**

```json
{
  "code": 200,
  "msg": "查询成功",
  "total": 256,
  "rows": [
    {
      "listingId": 1001,
      "skinId": 501,
      "skinName": "AK-47 | 红线 (久经沙场)",
      "weaponTypeName": "AK-47",
      "qualityName": "久经沙场",
      "price": 128.50,
      "wearValue": 0.2534,
      "imageUrl": "https://example.com/images/ak_redline.jpg",
      "stickers": ["iBUYPOWER", "s1mple"],
      "sellerId": 10,
      "sellerName": "player001",
      "createTime": "2025-01-15 10:30:00"
    }
  ]
}
```

**响应字段说明：**

| 字段 | 类型 | 说明 |
|------|------|------|
| listingId | Long | 上架记录ID |
| skinId | Long | 饰品ID |
| skinName | String | 饰品完整名称 |
| weaponTypeName | String | 武器类型名称 |
| qualityName | String | 品质名称 |
| price | BigDecimal | 售价（元） |
| wearValue | Double | 磨损值（0~1） |
| imageUrl | String | 饰品图片URL |
| stickers | Array\<String\> | 贴纸名称列表 |
| sellerId | Long | 卖家用户ID |
| sellerName | String | 卖家昵称 |
| createTime | String | 上架时间 |

---

#### 2.1.2 关键词搜索

根据关键词搜索在售饰品。

- **URL**：`GET /api/market/search`
- **权限**：匿名访问

**请求参数（Query）：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| keyword | String | 是 | 搜索关键词（饰品名称模糊匹配） |
| pageNum | Integer | 否 | 页码，默认1 |
| pageSize | Integer | 否 | 每页条数，默认10 |

**请求示例：**

```
GET /api/market/search?keyword=AK-47&pageNum=1&pageSize=10
```

**响应格式：** 与市场列表相同（TableDataInfo分页格式）。

---

#### 2.1.3 热门推荐

获取在售数量前10的热门饰品推荐。

- **URL**：`GET /api/market/hot`
- **权限**：匿名访问

**请求参数：** 无

**响应示例：**

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": [
    {
      "skinId": 501,
      "skinName": "AK-47 | 红线 (久经沙场)",
      "weaponTypeName": "AK-47",
      "qualityName": "久经沙场",
      "imageUrl": "https://example.com/images/ak_redline.jpg",
      "listingCount": 45,
      "avgPrice": 135.20,
      "minPrice": 89.00
    }
  ]
}
```

**响应字段说明：**

| 字段 | 类型 | 说明 |
|------|------|------|
| skinId | Long | 饰品ID |
| skinName | String | 饰品名称 |
| weaponTypeName | String | 武器类型名称 |
| qualityName | String | 品质名称 |
| imageUrl | String | 图片URL |
| listingCount | Integer | 当前在售数量 |
| avgPrice | BigDecimal | 平均价格 |
| minPrice | BigDecimal | 最低在售价格 |

---

#### 2.1.4 分类数据

获取武器分类列表和品质列表，用于前端筛选条件渲染。

- **URL**：`GET /api/market/categories`
- **权限**：匿名访问

**请求参数：** 无

**响应示例：**

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "weaponTypes": [
      {
        "id": 1,
        "name": "AK-47",
        "sortOrder": 1
      },
      {
        "id": 2,
        "name": "M4A4",
        "sortOrder": 2
      }
    ],
    "qualities": [
      {
        "id": 1,
        "name": "崭新出厂",
        "color": "#4CAF50"
      },
      {
        "id": 2,
        "name": "略有磨损",
        "color": "#8BC34A"
      },
      {
        "id": 3,
        "name": "久经沙场",
        "color": "#FFC107"
      },
      {
        "id": 4,
        "name": "破损不堪",
        "color": "#FF9800"
      },
      {
        "id": 5,
        "name": "战痕累累",
        "color": "#F44336"
      }
    ]
  }
}
```

---

#### 2.1.5 饰品详情

获取指定饰品的详细信息，包括基本信息、当前在售列表、价格历史摘要。

- **URL**：`GET /api/market/detail/{skinId}`
- **权限**：匿名访问

**路径参数：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| skinId | Long | 是 | 饰品ID |

**请求示例：**

```
GET /api/market/detail/501
```

**响应示例：**

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "skin": {
      "skinId": 501,
      "skinName": "AK-47 | 红线 (久经沙场)",
      "weaponTypeName": "AK-47",
      "qualityName": "久经沙场",
      "imageUrl": "https://example.com/images/ak_redline.jpg",
      "description": "AK-47经典红线皮肤",
      "minWear": 0.10,
      "maxWear": 0.70
    },
    "listings": [
      {
        "listingId": 1001,
        "price": 128.50,
        "wearValue": 0.2534,
        "stickers": ["iBUYPOWER"],
        "sellerName": "player001",
        "createTime": "2025-01-15 10:30:00"
      }
    ],
    "priceSummary": {
      "avgPrice": 135.20,
      "minPrice": 89.00,
      "maxPrice": 210.00,
      "listingCount": 45,
      "lastPrice": 128.50
    }
  }
}
```

**响应字段说明：**

| 字段 | 类型 | 说明 |
|------|------|------|
| skin | Object | 饰品基本信息 |
| skin.skinId | Long | 饰品ID |
| skin.skinName | String | 饰品名称 |
| skin.weaponTypeName | String | 武器类型名称 |
| skin.qualityName | String | 品质名称 |
| skin.imageUrl | String | 图片URL |
| skin.description | String | 饰品描述 |
| skin.minWear | Double | 最小磨损值 |
| skin.maxWear | Double | 最大磨损值 |
| listings | Array | 当前在售列表（按价格升序） |
| listings[].listingId | Long | 上架记录ID |
| listings[].price | BigDecimal | 售价 |
| listings[].wearValue | Double | 磨损值 |
| listings[].stickers | Array\<String\> | 贴纸列表 |
| listings[].sellerName | String | 卖家昵称 |
| listings[].createTime | String | 上架时间 |
| priceSummary | Object | 价格摘要 |
| priceSummary.avgPrice | BigDecimal | 平均价格 |
| priceSummary.minPrice | BigDecimal | 最低价格 |
| priceSummary.maxPrice | BigDecimal | 最高价格 |
| priceSummary.listingCount | Integer | 在售数量 |
| priceSummary.lastPrice | BigDecimal | 最近成交价 |

---

#### 2.1.6 价格历史

获取指定饰品的历史价格数据，供前端 ECharts 图表渲染。

- **URL**：`GET /api/market/priceHistory/{skinId}`
- **权限**：匿名访问

**路径参数：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| skinId | Long | 是 | 饰品ID |

**请求示例：**

```
GET /api/market/priceHistory/501
```

**响应示例：**

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "skinId": 501,
    "skinName": "AK-47 | 红线 (久经沙场)",
    "dates": ["2025-01-01", "2025-01-02", "2025-01-03", "2025-01-04", "2025-01-05"],
    "prices": [125.30, 128.50, 130.00, 127.80, 132.40],
    "volumes": [12, 8, 15, 10, 6]
  }
}
```

**响应字段说明：**

| 字段 | 类型 | 说明 |
|------|------|------|
| skinId | Long | 饰品ID |
| skinName | String | 饰品名称 |
| dates | Array\<String\> | 日期数组（X轴数据） |
| prices | Array\<BigDecimal\> | 日均价格数组（Y轴数据） |
| volumes | Array\<Integer\> | 每日成交量数组 |

---

### 2.2 用户库存模块

> 基础路径：`/api/inventory`  
> 权限要求：需登录

---

#### 2.2.1 我的库存列表

获取当前登录用户的库存列表，支持筛选和搜索。

- **URL**：`GET /api/inventory/list`
- **权限**：登录用户

**请求参数（Query）：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| pageNum | Integer | 否 | 页码，默认1 |
| pageSize | Integer | 否 | 每页条数，默认10 |
| weaponType | String | 否 | 武器类型ID |
| quality | String | 否 | 品质ID |
| status | String | 否 | 库存状态：0-在库，1-已上架，2-已售出 |
| keyword | String | 否 | 关键词搜索 |

**请求示例：**

```
GET /api/inventory/list?pageNum=1&pageSize=10&status=0&weaponType=1
```

**响应示例：**

```json
{
  "code": 200,
  "msg": "查询成功",
  "total": 25,
  "rows": [
    {
      "id": 2001,
      "skinId": 501,
      "skinName": "AK-47 | 红线 (久经沙场)",
      "weaponTypeName": "AK-47",
      "qualityName": "久经沙场",
      "wearValue": 0.2534,
      "imageUrl": "https://example.com/images/ak_redline.jpg",
      "stickers": ["iBUYPOWER", "s1mple"],
      "inspectLink": "steam://rungame/730/...",
      "status": "0",
      "statusText": "在库",
      "acquireTime": "2025-01-10 14:20:00",
      "createTime": "2025-01-10 14:20:00"
    }
  ]
}
```

**响应字段说明：**

| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 库存记录ID |
| skinId | Long | 饰品ID |
| skinName | String | 饰品名称 |
| weaponTypeName | String | 武器类型名称 |
| qualityName | String | 品质名称 |
| wearValue | Double | 磨损值 |
| imageUrl | String | 图片URL |
| stickers | Array\<String\> | 贴纸列表 |
| inspectLink | String | 检视链接 |
| status | String | 状态：0-在库，1-已上架，2-已售出 |
| statusText | String | 状态文本 |
| acquireTime | String | 获取时间 |
| createTime | String | 创建时间 |

---

#### 2.2.2 库存详情

获取指定库存记录的详细信息。

- **URL**：`GET /api/inventory/detail/{id}`
- **权限**：登录用户（仅能查看自己的库存）

**路径参数：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | Long | 是 | 库存记录ID |

**响应示例：**

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "id": 2001,
    "skinId": 501,
    "skinName": "AK-47 | 红线 (久经沙场)",
    "weaponTypeId": 1,
    "weaponTypeName": "AK-47",
    "qualityId": 3,
    "qualityName": "久经沙场",
    "wearValue": 0.2534,
    "imageUrl": "https://example.com/images/ak_redline.jpg",
    "stickers": ["iBUYPOWER", "s1mple"],
    "inspectLink": "steam://rungame/730/...",
    "status": "0",
    "statusText": "在库",
    "acquireTime": "2025-01-10 14:20:00",
    "createTime": "2025-01-10 14:20:00",
    "updateTime": "2025-01-10 14:20:00"
  }
}
```

---

#### 2.2.3 更新库存信息

更新库存记录的贴纸信息和检视链接。

- **URL**：`PUT /api/inventory/update/{id}`
- **权限**：登录用户（仅能修改自己的库存）

**路径参数：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | Long | 是 | 库存记录ID |

**请求体（Body）：**

```json
{
  "stickers": "iBUYPOWER,s1mple",
  "inspectLink": "steam://rungame/730/76561198xxxxx/+IDixxxxxx/inspect"
}
```

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| stickers | String | 否 | 贴纸名称，多个用逗号分隔 |
| inspectLink | String | 否 | CS2检视链接 |

**响应示例：**

```json
{
  "code": 200,
  "msg": "修改成功"
}
```

---

### 2.3 上架下架模块

> 基础路径：`/api/listing`  
> 权限要求：需登录

---

#### 2.3.1 上架饰品

将库存中的饰品上架到市场，状态从"在库"变为"在售"。

- **URL**：`POST /api/listing`
- **权限**：登录用户

**请求体（Body）：**

```json
{
  "inventoryId": 2001,
  "price": 128.50
}
```

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| inventoryId | Long | 是 | 库存记录ID |
| price | BigDecimal | 是 | 售价（元），需大于0 |

**响应示例（成功）：**

```json
{
  "code": 200,
  "msg": "上架成功"
}
```

**响应示例（失败）：**

```json
{
  "code": 500,
  "msg": "该饰品已上架或已售出，无法重复上架"
}
```

---

#### 2.3.2 下架饰品

将已上架的饰品从市场下架，恢复为"在库"状态。

- **URL**：`DELETE /api/listing/inventory/{inventoryId}`
- **权限**：登录用户（仅能下架自己的饰品）

**路径参数：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| inventoryId | Long | 是 | 库存记录ID |

**响应示例：**

```json
{
  "code": 200,
  "msg": "下架成功"
}
```

---

### 2.4 交易订单模块

> 基础路径：`/api/order`  
> 权限要求：需登录

---

#### 2.4.1 下单购买

买家对指定上架记录发起购买，系统冻结买家钱包对应金额。

- **URL**：`POST /api/order/buy/{listingId}`
- **权限**：登录用户

**路径参数：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| listingId | Long | 是 | 上架记录ID |

**响应示例（成功）：**

```json
{
  "code": 200,
  "msg": "下单成功",
  "data": {
    "orderId": 3001,
    "listingId": 1001,
    "skinName": "AK-47 | 红线 (久经沙场)",
    "price": 128.50,
    "status": "0",
    "statusText": "待发货",
    "createTime": "2025-01-20 15:00:00"
  }
}
```

**响应示例（余额不足）：**

```json
{
  "code": 500,
  "msg": "钱包余额不足，请先充值"
}
```

**订单状态说明：**

| 状态值 | 含义 | 说明 |
|--------|------|------|
| 0 | 待发货 | 买家已下单，等待卖家确认 |
| 1 | 待收货 | 卖家已确认，等待买家收货 |
| 2 | 已完成 | 交易完成 |
| 3 | 已取消 | 买家取消或超时取消 |

---

#### 2.4.2 确认收货

买家确认收到饰品，交易完成，资金从冻结转入卖家钱包。

- **URL**：`POST /api/order/confirm/{orderId}`
- **权限**：登录用户（仅买方可操作）

**路径参数：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| orderId | Long | 是 | 订单ID |

**响应示例：**

```json
{
  "code": 200,
  "msg": "确认收货成功，交易已完成"
}
```

---

#### 2.4.3 取消订单

买家取消订单，冻结金额退回钱包。

- **URL**：`POST /api/order/cancel/{orderId}`
- **权限**：登录用户（仅买方可操作，仅限"待发货"状态）

**路径参数：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| orderId | Long | 是 | 订单ID |

**响应示例：**

```json
{
  "code": 200,
  "msg": "订单已取消，资金已退回"
}
```

---

#### 2.4.4 我的交易

获取当前登录用户的交易订单列表。

- **URL**：`GET /api/order/my`
- **权限**：登录用户

**请求参数（Query）：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| pageNum | Integer | 否 | 页码，默认1 |
| pageSize | Integer | 否 | 每页条数，默认10 |
| status | String | 否 | 订单状态：0-待发货，1-待收货，2-已完成，3-已取消 |
| direction | String | 否 | 交易方向：buy-我买入了，sell-我卖出了 |

**请求示例：**

```
GET /api/order/my?pageNum=1&pageSize=10&status=2&direction=buy
```

**响应示例：**

```json
{
  "code": 200,
  "msg": "查询成功",
  "total": 8,
  "rows": [
    {
      "orderId": 3001,
      "listingId": 1001,
      "skinName": "AK-47 | 红线 (久经沙场)",
      "imageUrl": "https://example.com/images/ak_redline.jpg",
      "price": 128.50,
      "status": "2",
      "statusText": "已完成",
      "direction": "buy",
      "counterpartyName": "player002",
      "createTime": "2025-01-20 15:00:00",
      "completeTime": "2025-01-21 09:30:00"
    }
  ]
}
```

**响应字段说明：**

| 字段 | 类型 | 说明 |
|------|------|------|
| orderId | Long | 订单ID |
| listingId | Long | 上架记录ID |
| skinName | String | 饰品名称 |
| imageUrl | String | 图片URL |
| price | BigDecimal | 成交价格 |
| status | String | 订单状态 |
| statusText | String | 状态文本 |
| direction | String | 交易方向：buy/sell |
| counterpartyName | String | 对方昵称 |
| createTime | String | 下单时间 |
| completeTime | String | 完成时间（可能为空） |

---

### 2.5 求购单模块

> 基础路径：`/api/buyOrder`  
> 权限要求：需登录

---

#### 2.5.1 发布求购

为指定饰品发布求购单，冻结求购金额。

- **URL**：`POST /api/buyOrder`
- **权限**：登录用户

**请求体（Body）：**

```json
{
  "skinId": 501,
  "price": 120.00
}
```

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| skinId | Long | 是 | 饰品ID |
| price | BigDecimal | 是 | 求购价格（元），需大于0 |

**响应示例：**

```json
{
  "code": 200,
  "msg": "发布求购成功"
}
```

---

#### 2.5.2 某饰品有效求购列表

获取指定饰品的所有有效求购单（按价格降序排列）。

- **URL**：`GET /api/buyOrder/list`
- **权限**：登录用户

**请求参数（Query）：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| skinId | Long | 是 | 饰品ID |
| pageNum | Integer | 否 | 页码，默认1 |
| pageSize | Integer | 否 | 每页条数，默认10 |

**请求示例：**

```
GET /api/buyOrder/list?skinId=501&pageNum=1&pageSize=10
```

**响应示例：**

```json
{
  "code": 200,
  "msg": "查询成功",
  "total": 5,
  "rows": [
    {
      "id": 4001,
      "skinId": 501,
      "skinName": "AK-47 | 红线 (久经沙场)",
      "buyerId": 10,
      "buyerName": "player001",
      "price": 120.00,
      "status": "0",
      "statusText": "求购中",
      "createTime": "2025-01-18 11:00:00"
    }
  ]
}
```

---

#### 2.5.3 我的求购

获取当前登录用户发布的所有求购单。

- **URL**：`GET /api/buyOrder/my`
- **权限**：登录用户

**请求参数（Query）：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| pageNum | Integer | 否 | 页码，默认1 |
| pageSize | Integer | 否 | 每页条数，默认10 |

**响应示例：**

```json
{
  "code": 200,
  "msg": "查询成功",
  "total": 3,
  "rows": [
    {
      "id": 4001,
      "skinId": 501,
      "skinName": "AK-47 | 红线 (久经沙场)",
      "imageUrl": "https://example.com/images/ak_redline.jpg",
      "price": 120.00,
      "status": "0",
      "statusText": "求购中",
      "createTime": "2025-01-18 11:00:00"
    }
  ]
}
```

---

#### 2.5.4 修改求购价格

修改已有求购单的价格，系统会重新冻结/解冻差额。

- **URL**：`PUT /api/buyOrder/{id}`
- **权限**：登录用户（仅能修改自己的求购单）

**路径参数：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | Long | 是 | 求购单ID |

**请求体（Body）：**

```json
{
  "price": 125.00
}
```

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| price | BigDecimal | 是 | 新求购价格 |

**响应示例：**

```json
{
  "code": 200,
  "msg": "修改成功"
}
```

---

#### 2.5.5 取消求购

取消求购单，冻结金额退回钱包余额。

- **URL**：`DELETE /api/buyOrder/{id}`
- **权限**：登录用户（仅能取消自己的求购单）

**路径参数：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | Long | 是 | 求购单ID |

**响应示例：**

```json
{
  "code": 200,
  "msg": "取消成功，冻结资金已退回"
}
```

---

### 2.6 钱包模块

> 基础路径：`/api/wallet`  
> 权限要求：需登录  
> 说明：钱包在首次访问时自动创建

---

#### 2.6.1 我的钱包

获取当前登录用户的钱包信息。若钱包不存在则自动创建。

- **URL**：`GET /api/wallet/my`
- **权限**：登录用户

**请求参数：** 无

**响应示例：**

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "walletId": 5001,
    "userId": 10,
    "balance": 1520.50,
    "frozenAmount": 128.50,
    "totalRecharge": 3000.00,
    "totalWithdraw": 1351.00,
    "createTime": "2025-01-01 00:00:00"
  }
}
```

**响应字段说明：**

| 字段 | 类型 | 说明 |
|------|------|------|
| walletId | Long | 钱包ID |
| userId | Long | 用户ID |
| balance | BigDecimal | 可用余额（元） |
| frozenAmount | BigDecimal | 冻结金额（元），下单/求购时冻结 |
| totalRecharge | BigDecimal | 累计充值金额 |
| totalWithdraw | BigDecimal | 累计提现金额 |
| createTime | String | 创建时间 |

---

#### 2.6.2 流水记录

获取钱包的资金流水记录（分页）。

- **URL**：`GET /api/wallet/logs`
- **权限**：登录用户

**请求参数（Query）：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| pageNum | Integer | 否 | 页码，默认1 |
| pageSize | Integer | 否 | 每页条数，默认10 |

**响应示例：**

```json
{
  "code": 200,
  "msg": "查询成功",
  "total": 45,
  "rows": [
    {
      "logId": 6001,
      "type": "1",
      "typeText": "充值",
      "amount": 500.00,
      "balanceBefore": 1020.50,
      "balanceAfter": 1520.50,
      "remark": "用户充值",
      "createTime": "2025-01-19 16:00:00"
    }
  ]
}
```

**流水类型说明：**

| 类型值 | 含义 | 说明 |
|--------|------|------|
| 1 | 充值 | 用户充值到钱包 |
| 2 | 提现 | 用户从钱包提现 |
| 3 | 购买冻结 | 下单时冻结资金 |
| 4 | 购买扣款 | 交易完成，扣除冻结资金 |
| 5 | 出售收入 | 卖出饰品获得收入 |
| 6 | 退款 | 取消订单退回资金 |
| 7 | 求购冻结 | 发布求购冻结资金 |
| 8 | 求购退款 | 取消求购退回资金 |

**响应字段说明：**

| 字段 | 类型 | 说明 |
|------|------|------|
| logId | Long | 流水ID |
| type | String | 流水类型编码 |
| typeText | String | 流水类型文本 |
| amount | BigDecimal | 变动金额（正数为收入，负数为支出） |
| balanceBefore | BigDecimal | 变动前余额 |
| balanceAfter | BigDecimal | 变动后余额 |
| remark | String | 备注说明 |
| createTime | String | 变动时间 |

---

#### 2.6.3 充值

向钱包充值。

- **URL**：`POST /api/wallet/recharge`
- **权限**：登录用户

**请求体（Body）：**

```json
{
  "amount": 500.00
}
```

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| amount | BigDecimal | 是 | 充值金额（元），上限100000（10万） |

**响应示例（成功）：**

```json
{
  "code": 200,
  "msg": "充值成功，当前余额：1520.50元"
}
```

**响应示例（超额）：**

```json
{
  "code": 500,
  "msg": "单次充值金额不能超过100000元"
}
```

---

#### 2.6.4 提现

从钱包提现。

- **URL**：`POST /api/wallet/withdraw`
- **权限**：登录用户

**请求体（Body）：**

```json
{
  "amount": 200.00
}
```

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| amount | BigDecimal | 是 | 提现金额（元），上限50000（5万），不得超过可用余额 |

**响应示例（成功）：**

```json
{
  "code": 200,
  "msg": "提现成功，当前余额：1320.50元"
}
```

**响应示例（余额不足）：**

```json
{
  "code": 500,
  "msg": "可用余额不足"
}
```

---

### 2.7 公告模块

> 基础路径：`/trade/announcement`  
> 权限要求：公开接口匿名访问

---

#### 2.7.1 公开公告列表

获取已发布的公开公告列表。

- **URL**：`GET /trade/announcement/publicList`
- **权限**：匿名访问

**请求参数（Query）：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| pageNum | Integer | 否 | 页码，默认1 |
| pageSize | Integer | 否 | 每页条数，默认10 |

**响应示例：**

```json
{
  "code": 200,
  "msg": "查询成功",
  "total": 5,
  "rows": [
    {
      "id": 1,
      "title": "平台上线公告",
      "content": "Debuff平台正式上线，欢迎体验！",
      "announcementType": "1",
      "typeText": "系统公告",
      "status": "0",
      "createTime": "2025-01-01 00:00:00"
    }
  ]
}
```

**响应字段说明：**

| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 公告ID |
| title | String | 公告标题 |
| content | String | 公告内容 |
| announcementType | String | 公告类型编码 |
| typeText | String | 公告类型文本 |
| status | String | 状态：0-正常，1-关闭 |
| createTime | String | 发布时间 |

---

### 2.8 收藏模块

> 基础路径：`/api/favorite`  
> 权限要求：需登录

---

#### 2.8.1 添加收藏

收藏指定的上架饰品。

- **URL**：`POST /api/favorite`
- **权限**：登录用户

**请求体（Body）：**

```json
{
  "listingId": 1001
}
```

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| listingId | Long | 是 | 上架记录ID |

**响应示例：**

```json
{
  "code": 200,
  "msg": "收藏成功"
}
```

---

#### 2.8.2 取消收藏

取消对指定上架饰品的收藏。

- **URL**：`DELETE /api/favorite/{listingId}`
- **权限**：登录用户

**路径参数：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| listingId | Long | 是 | 上架记录ID |

**响应示例：**

```json
{
  "code": 200,
  "msg": "取消收藏成功"
}
```

---

## 3. 后台管理接口

> 以下接口均需管理员权限，请求时须携带有效的管理员Token。

### 3.1 饰品管理

> 基础路径：`/api/admin/skin`  
> 权限标识：`admin:skin:*`

---

#### 3.1.1 饰品列表

获取饰品列表（分页+多维度筛选）。

- **URL**：`GET /api/admin/skin/list`
- **权限**：`admin:skin:list`

**请求参数（Query）：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| pageNum | Integer | 否 | 页码，默认1 |
| pageSize | Integer | 否 | 每页条数，默认10 |
| inMarket | String | 否 | 是否在售：0-否，1-是 |
| weaponType | String | 否 | 武器类型ID |
| quality | String | 否 | 品质ID |
| keyword | String | 否 | 关键词搜索（饰品名称） |
| status | String | 否 | 状态：0-正常，1-停用 |

**请求示例：**

```
GET /api/admin/skin/list?pageNum=1&pageSize=10&weaponType=1&status=0
```

**响应示例：**

```json
{
  "code": 200,
  "msg": "查询成功",
  "total": 500,
  "rows": [
    {
      "skinId": 501,
      "skinName": "AK-47 | 红线 (久经沙场)",
      "weaponTypeId": 1,
      "weaponTypeName": "AK-47",
      "qualityId": 3,
      "qualityName": "久经沙场",
      "imageUrl": "https://example.com/images/ak_redline.jpg",
      "description": "AK-47经典红线皮肤",
      "minWear": 0.10,
      "maxWear": 0.70,
      "inMarket": "1",
      "status": "0",
      "createTime": "2025-01-01 00:00:00"
    }
  ]
}
```

---

#### 3.1.2 饰品详情

获取指定饰品的详细信息。

- **URL**：`GET /api/admin/skin/{skinId}`
- **权限**：`admin:skin:query`

**路径参数：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| skinId | Long | 是 | 饰品ID |

**响应示例：**

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "skinId": 501,
    "skinName": "AK-47 | 红线 (久经沙场)",
    "weaponTypeId": 1,
    "weaponTypeName": "AK-47",
    "qualityId": 3,
    "qualityName": "久经沙场",
    "imageUrl": "https://example.com/images/ak_redline.jpg",
    "description": "AK-47经典红线皮肤",
    "minWear": 0.10,
    "maxWear": 0.70,
    "inMarket": "1",
    "status": "0",
    "createTime": "2025-01-01 00:00:00",
    "updateTime": "2025-01-01 00:00:00"
  }
}
```

---

#### 3.1.3 新增饰品

新增一个饰品记录。

- **URL**：`POST /api/admin/skin`
- **权限**：`admin:skin:add`

**请求体（Body）：**

```json
{
  "skinName": "AK-47 | 红线 (久经沙场)",
  "weaponTypeId": 1,
  "qualityId": 3,
  "imageUrl": "https://example.com/images/ak_redline.jpg",
  "description": "AK-47经典红线皮肤",
  "minWear": 0.10,
  "maxWear": 0.70,
  "status": "0"
}
```

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| skinName | String | 是 | 饰品名称 |
| weaponTypeId | Long | 是 | 武器类型ID |
| qualityId | Long | 是 | 品质ID |
| imageUrl | String | 否 | 图片URL |
| description | String | 否 | 描述 |
| minWear | Double | 否 | 最小磨损值 |
| maxWear | Double | 否 | 最大磨损值 |
| status | String | 否 | 状态：0-正常，1-停用，默认0 |

**响应示例：**

```json
{
  "code": 200,
  "msg": "新增成功"
}
```

---

#### 3.1.4 修改饰品

修改已有饰品信息。

- **URL**：`PUT /api/admin/skin`
- **权限**：`admin:skin:edit`

**请求体（Body）：**

```json
{
  "skinId": 501,
  "skinName": "AK-47 | 红线 (久经沙场)",
  "weaponTypeId": 1,
  "qualityId": 3,
  "imageUrl": "https://example.com/images/ak_redline_new.jpg",
  "description": "AK-47经典红线皮肤（更新描述）",
  "minWear": 0.10,
  "maxWear": 0.70,
  "status": "0"
}
```

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| skinId | Long | 是 | 饰品ID |
| skinName | String | 否 | 饰品名称 |
| weaponTypeId | Long | 否 | 武器类型ID |
| qualityId | Long | 否 | 品质ID |
| imageUrl | String | 否 | 图片URL |
| description | String | 否 | 描述 |
| minWear | Double | 否 | 最小磨损值 |
| maxWear | Double | 否 | 最大磨损值 |
| status | String | 否 | 状态 |

**响应示例：**

```json
{
  "code": 200,
  "msg": "修改成功"
}
```

---

#### 3.1.5 批量删除饰品

批量删除指定饰品。

- **URL**：`DELETE /api/admin/skin/{skinIds}`
- **权限**：`admin:skin:remove`

**路径参数：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| skinIds | String | 是 | 饰品ID，多个用逗号分隔，如 "501,502,503" |

**请求示例：**

```
DELETE /api/admin/skin/501,502,503
```

**响应示例：**

```json
{
  "code": 200,
  "msg": "删除成功"
}
```

---

#### 3.1.6 上下架切换

切换饰品的上架/下架状态。

- **URL**：`PUT /api/admin/skin/changeStatus`
- **权限**：`admin:skin:edit`

**请求体（Body）：**

```json
{
  "skinId": 501,
  "inMarket": "0"
}
```

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| skinId | Long | 是 | 饰品ID |
| inMarket | String | 是 | 是否在售：0-下架，1-上架 |

**响应示例：**

```json
{
  "code": 200,
  "msg": "修改成功"
}
```

---

### 3.2 武器类型管理

> 基础路径：`/api/admin/weaponType`  
> 权限要求：admin角色

---

#### 3.2.1 武器类型列表

- **URL**：`GET /api/admin/weaponType/list`
- **权限**：admin角色

**请求参数（Query）：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| pageNum | Integer | 否 | 页码，默认1 |
| pageSize | Integer | 否 | 每页条数，默认10 |
| name | String | 否 | 武器类型名称（模糊搜索） |

**响应示例：**

```json
{
  "code": 200,
  "msg": "查询成功",
  "total": 30,
  "rows": [
    {
      "id": 1,
      "name": "AK-47",
      "sortOrder": 1,
      "status": "0",
      "createTime": "2025-01-01 00:00:00"
    }
  ]
}
```

**响应字段说明：**

| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 武器类型ID |
| name | String | 武器类型名称 |
| sortOrder | Integer | 排序号 |
| status | String | 状态：0-正常，1-停用 |
| createTime | String | 创建时间 |

---

#### 3.2.2 武器类型详情

- **URL**：`GET /api/admin/weaponType/{id}`
- **权限**：admin角色

**路径参数：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | Long | 是 | 武器类型ID |

**响应示例：**

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "id": 1,
    "name": "AK-47",
    "sortOrder": 1,
    "status": "0",
    "createTime": "2025-01-01 00:00:00",
    "updateTime": "2025-01-01 00:00:00"
  }
}
```

---

#### 3.2.3 新增武器类型

- **URL**：`POST /api/admin/weaponType`
- **权限**：admin角色

**请求体（Body）：**

```json
{
  "name": "AWP",
  "sortOrder": 5,
  "status": "0"
}
```

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| name | String | 是 | 武器类型名称 |
| sortOrder | Integer | 否 | 排序号，默认0 |
| status | String | 否 | 状态：0-正常，1-停用，默认0 |

**响应示例：**

```json
{
  "code": 200,
  "msg": "新增成功"
}
```

---

#### 3.2.4 修改武器类型

- **URL**：`PUT /api/admin/weaponType`
- **权限**：admin角色

**请求体（Body）：**

```json
{
  "id": 1,
  "name": "AK-47",
  "sortOrder": 2,
  "status": "0"
}
```

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | Long | 是 | 武器类型ID |
| name | String | 否 | 武器类型名称 |
| sortOrder | Integer | 否 | 排序号 |
| status | String | 否 | 状态 |

**响应示例：**

```json
{
  "code": 200,
  "msg": "修改成功"
}
```

---

#### 3.2.5 删除武器类型

- **URL**：`DELETE /api/admin/weaponType/{ids}`
- **权限**：admin角色

**路径参数：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| ids | String | 是 | 武器类型ID，多个用逗号分隔 |

**响应示例：**

```json
{
  "code": 200,
  "msg": "删除成功"
}
```

---

### 3.3 品质管理

> 基础路径：`/api/admin/quality`  
> 权限要求：admin角色

---

#### 3.3.1 品质列表

- **URL**：`GET /api/admin/quality/list`
- **权限**：admin角色

**请求参数（Query）：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| pageNum | Integer | 否 | 页码，默认1 |
| pageSize | Integer | 否 | 每页条数，默认10 |
| name | String | 否 | 品质名称（模糊搜索） |

**响应示例：**

```json
{
  "code": 200,
  "msg": "查询成功",
  "total": 5,
  "rows": [
    {
      "id": 1,
      "name": "崭新出厂",
      "color": "#4CAF50",
      "sortOrder": 1,
      "status": "0",
      "createTime": "2025-01-01 00:00:00"
    }
  ]
}
```

**响应字段说明：**

| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 品质ID |
| name | String | 品质名称 |
| color | String | 品质对应颜色（十六进制） |
| sortOrder | Integer | 排序号 |
| status | String | 状态：0-正常，1-停用 |
| createTime | String | 创建时间 |

---

#### 3.3.2 品质详情

- **URL**：`GET /api/admin/quality/{id}`
- **权限**：admin角色

**路径参数：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | Long | 是 | 品质ID |

**响应示例：**

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "id": 3,
    "name": "久经沙场",
    "color": "#FFC107",
    "sortOrder": 3,
    "status": "0",
    "createTime": "2025-01-01 00:00:00",
    "updateTime": "2025-01-01 00:00:00"
  }
}
```

---

#### 3.3.3 新增品质

- **URL**：`POST /api/admin/quality`
- **权限**：admin角色

**请求体（Body）：**

```json
{
  "name": "崭新出厂",
  "color": "#4CAF50",
  "sortOrder": 1,
  "status": "0"
}
```

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| name | String | 是 | 品质名称 |
| color | String | 否 | 品质颜色（十六进制色值） |
| sortOrder | Integer | 否 | 排序号 |
| status | String | 否 | 状态：0-正常，1-停用 |

**响应示例：**

```json
{
  "code": 200,
  "msg": "新增成功"
}
```

---

#### 3.3.4 修改品质

- **URL**：`PUT /api/admin/quality`
- **权限**：admin角色

**请求体（Body）：**

```json
{
  "id": 3,
  "name": "久经沙场",
  "color": "#FFC107",
  "sortOrder": 3,
  "status": "0"
}
```

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | Long | 是 | 品质ID |
| name | String | 否 | 品质名称 |
| color | String | 否 | 品质颜色 |
| sortOrder | Integer | 否 | 排序号 |
| status | String | 否 | 状态 |

**响应示例：**

```json
{
  "code": 200,
  "msg": "修改成功"
}
```

---

#### 3.3.5 删除品质

- **URL**：`DELETE /api/admin/quality/{ids}`
- **权限**：admin角色

**路径参数：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| ids | String | 是 | 品质ID，多个用逗号分隔 |

**响应示例：**

```json
{
  "code": 200,
  "msg": "删除成功"
}
```

---

### 3.4 数据看板

> 基础路径：`/api/admin/stats`  
> 权限要求：`admin:skin:list`

---

#### 3.4.1 总览数据

获取平台运营总览数据。

- **URL**：`GET /api/admin/stats/overview`
- **权限**：`admin:skin:list`

**请求参数：** 无

**响应示例：**

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "totalSkins": 500,
    "totalListings": 256,
    "totalOrders": 1024,
    "totalUsers": 320,
    "totalVolume": 158600.50,
    "todayOrders": 15,
    "todayVolume": 2350.00,
    "todayNewUsers": 8
  }
}
```

**响应字段说明：**

| 字段 | 类型 | 说明 |
|------|------|------|
| totalSkins | Integer | 饰品总数 |
| totalListings | Integer | 当前在售数量 |
| totalOrders | Integer | 订单总数 |
| totalUsers | Integer | 注册用户总数 |
| totalVolume | BigDecimal | 累计交易金额（元） |
| todayOrders | Integer | 今日订单数 |
| todayVolume | BigDecimal | 今日交易金额（元） |
| todayNewUsers | Integer | 今日新增用户数 |

---

#### 3.4.2 订单趋势

获取指定时间范围内的订单趋势数据。

- **URL**：`GET /api/admin/stats/orderTrend`
- **权限**：`admin:skin:list`

**请求参数（Query）：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| days | Integer | 否 | 统计天数，默认7天，可选值：7/30/90 |

**请求示例：**

```
GET /api/admin/stats/orderTrend?days=7
```

**响应示例：**

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "dates": ["2025-01-14", "2025-01-15", "2025-01-16", "2025-01-17", "2025-01-18", "2025-01-19", "2025-01-20"],
    "orderCounts": [12, 18, 15, 22, 10, 8, 15],
    "orderVolumes": [1580.00, 2350.00, 1920.00, 3100.00, 1200.00, 980.00, 2350.00]
  }
}
```

**响应字段说明：**

| 字段 | 类型 | 说明 |
|------|------|------|
| dates | Array\<String\> | 日期数组 |
| orderCounts | Array\<Integer\> | 每日订单数 |
| orderVolumes | Array\<BigDecimal\> | 每日交易金额 |

---

#### 3.4.3 TOP饰品排行

获取交易量/交易额排名前N的饰品。

- **URL**：`GET /api/admin/stats/topSkins`
- **权限**：`admin:skin:list`

**请求参数（Query）：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| top | Integer | 否 | 返回数量，默认10 |
| sortBy | String | 否 | 排序依据：volume-交易额，count-交易量，默认volume |

**请求示例：**

```
GET /api/admin/stats/topSkins?top=10&sortBy=volume
```

**响应示例：**

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": [
    {
      "skinId": 501,
      "skinName": "AK-47 | 红线 (久经沙场)",
      "imageUrl": "https://example.com/images/ak_redline.jpg",
      "tradeCount": 120,
      "tradeVolume": 15600.00,
      "avgPrice": 130.00
    }
  ]
}
```

**响应字段说明：**

| 字段 | 类型 | 说明 |
|------|------|------|
| skinId | Long | 饰品ID |
| skinName | String | 饰品名称 |
| imageUrl | String | 图片URL |
| tradeCount | Integer | 交易次数 |
| tradeVolume | BigDecimal | 交易总额 |
| avgPrice | BigDecimal | 平均成交价 |

---

#### 3.4.4 状态分布

获取库存/订单的状态分布统计。

- **URL**：`GET /api/admin/stats/statusDist`
- **权限**：`admin:skin:list`

**请求参数（Query）：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| type | String | 否 | 统计类型：inventory-库存分布，order-订单分布，默认inventory |

**请求示例：**

```
GET /api/admin/stats/statusDist?type=inventory
```

**响应示例（库存分布）：**

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": [
    { "name": "在库", "value": 1200 },
    { "name": "已上架", "value": 256 },
    { "name": "已售出", "value": 800 }
  ]
}
```

**响应示例（订单分布）：**

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": [
    { "name": "待发货", "value": 15 },
    { "name": "待收货", "value": 8 },
    { "name": "已完成", "value": 980 },
    { "name": "已取消", "value": 21 }
  ]
}
```

**响应字段说明：**

| 字段 | 类型 | 说明 |
|------|------|------|
| name | String | 状态名称 |
| value | Integer | 对应数量 |

---

### 3.5 订单管理

> 基础路径：`/trade/order`  
> 权限标识：`trade:order:*`

---

#### 3.5.1 订单列表

- **URL**：`GET /trade/order/list`
- **权限**：`trade:order:list`

**请求参数（Query）：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| pageNum | Integer | 否 | 页码，默认1 |
| pageSize | Integer | 否 | 每页条数，默认10 |
| status | String | 否 | 订单状态 |
| buyerName | String | 否 | 买家昵称 |
| sellerName | String | 否 | 卖家昵称 |

**响应格式：** TableDataInfo 分页格式。

---

#### 3.5.2 导出订单

- **URL**：`POST /trade/order/export`
- **权限**：`trade:order:export`

**请求体（Body）：** 与列表查询参数一致，支持按条件筛选后导出。

**响应：** 返回 Excel 文件流（`application/vnd.openxmlformats-officedocument.spreadsheetml.sheet`）。

---

#### 3.5.3 订单详情

- **URL**：`GET /trade/order/{id}`
- **权限**：`trade:order:query`

**路径参数：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | Long | 是 | 订单ID |

**响应格式：** AjaxResult，data 为完整订单对象。

---

#### 3.5.4 新增订单

- **URL**：`POST /trade/order`
- **权限**：`trade:order:add`

**请求体（Body）：** 订单对象 JSON。

**响应示例：**

```json
{
  "code": 200,
  "msg": "新增成功"
}
```

---

#### 3.5.5 修改订单

- **URL**：`PUT /trade/order`
- **权限**：`trade:order:edit`

**请求体（Body）：** 订单对象 JSON（需包含id字段）。

**响应示例：**

```json
{
  "code": 200,
  "msg": "修改成功"
}
```

---

#### 3.5.6 删除订单

- **URL**：`DELETE /trade/order/{ids}`
- **权限**：`trade:order:remove`

**路径参数：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| ids | String | 是 | 订单ID，多个用逗号分隔 |

**响应示例：**

```json
{
  "code": 200,
  "msg": "删除成功"
}
```

---

### 3.6 钱包管理

> 基础路径：`/trade/wallet`  
> 权限标识：`trade:wallet:*`

---

#### 3.6.1 钱包列表

- **URL**：`GET /trade/wallet/list`
- **权限**：`trade:wallet:list`

**请求参数（Query）：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| pageNum | Integer | 否 | 页码，默认1 |
| pageSize | Integer | 否 | 每页条数，默认10 |
| userId | Long | 否 | 用户ID |

**响应格式：** TableDataInfo 分页格式。

---

#### 3.6.2 导出钱包

- **URL**：`POST /trade/wallet/export`
- **权限**：`trade:wallet:export`

**响应：** 返回 Excel 文件流。

---

#### 3.6.3 钱包详情

- **URL**：`GET /trade/wallet/{id}`
- **权限**：`trade:wallet:query`

**路径参数：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | Long | 是 | 钱包ID |

**响应格式：** AjaxResult。

---

#### 3.6.4 新增钱包

- **URL**：`POST /trade/wallet`
- **权限**：`trade:wallet:add`

**请求体（Body）：** 钱包对象 JSON。

---

#### 3.6.5 修改钱包

- **URL**：`PUT /trade/wallet`
- **权限**：`trade:wallet:edit`

**请求体（Body）：** 钱包对象 JSON（需包含id字段）。

---

#### 3.6.6 删除钱包

- **URL**：`DELETE /trade/wallet/{ids}`
- **权限**：`trade:wallet:remove`

**路径参数：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| ids | String | 是 | 钱包ID，多个用逗号分隔 |

---

### 3.7 流水管理

> 基础路径：`/trade/walletLog`  
> 权限标识：`trade:walletLog:*`

---

#### 3.7.1 流水列表

- **URL**：`GET /trade/walletLog/list`
- **权限**：`trade:walletLog:list`

**请求参数（Query）：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| pageNum | Integer | 否 | 页码，默认1 |
| pageSize | Integer | 否 | 每页条数，默认10 |
| walletId | Long | 否 | 钱包ID |
| type | String | 否 | 流水类型 |

**响应格式：** TableDataInfo 分页格式。

---

#### 3.7.2 导出流水

- **URL**：`POST /trade/walletLog/export`
- **权限**：`trade:walletLog:export`

**响应：** 返回 Excel 文件流。

---

#### 3.7.3 流水详情

- **URL**：`GET /trade/walletLog/{id}`
- **权限**：`trade:walletLog:query`

**路径参数：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | Long | 是 | 流水ID |

**响应格式：** AjaxResult。

---

#### 3.7.4 新增流水

- **URL**：`POST /trade/walletLog`
- **权限**：`trade:walletLog:add`

**请求体（Body）：** 流水对象 JSON。

---

#### 3.7.5 修改流水

- **URL**：`PUT /trade/walletLog`
- **权限**：`trade:walletLog:edit`

**请求体（Body）：** 流水对象 JSON（需包含id字段）。

---

#### 3.7.6 删除流水

- **URL**：`DELETE /trade/walletLog/{ids}`
- **权限**：`trade:walletLog:remove`

**路径参数：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| ids | String | 是 | 流水ID，多个用逗号分隔 |

---

### 3.8 收藏管理

> 基础路径：`/trade/favorite`  
> 权限标识：`trade:favorite:*`

---

#### 3.8.1 收藏列表

- **URL**：`GET /trade/favorite/list`
- **权限**：`trade:favorite:list`

**请求参数（Query）：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| pageNum | Integer | 否 | 页码，默认1 |
| pageSize | Integer | 否 | 每页条数，默认10 |
| userId | Long | 否 | 用户ID |
| listingId | Long | 否 | 上架记录ID |

**响应格式：** TableDataInfo 分页格式。

---

#### 3.8.2 导出收藏

- **URL**：`POST /trade/favorite/export`
- **权限**：`trade:favorite:export`

**响应：** 返回 Excel 文件流。

---

#### 3.8.3 收藏详情

- **URL**：`GET /trade/favorite/{id}`
- **权限**：`trade:favorite:query`

**路径参数：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | Long | 是 | 收藏ID |

**响应格式：** AjaxResult。

---

#### 3.8.4 新增收藏

- **URL**：`POST /trade/favorite`
- **权限**：`trade:favorite:add`

**请求体（Body）：** 收藏对象 JSON。

---

#### 3.8.5 修改收藏

- **URL**：`PUT /trade/favorite`
- **权限**：`trade:favorite:edit`

**请求体（Body）：** 收藏对象 JSON（需包含id字段）。

---

#### 3.8.6 删除收藏

- **URL**：`DELETE /trade/favorite/{ids}`
- **权限**：`trade:favorite:remove`

**路径参数：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| ids | String | 是 | 收藏ID，多个用逗号分隔 |

---

### 3.9 公告管理

> 基础路径：`/trade/announcement`  
> 权限标识：`trade:announcement:*`

---

#### 3.9.1 公告列表

- **URL**：`GET /trade/announcement/list`
- **权限**：`trade:announcement:list`

**请求参数（Query）：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| pageNum | Integer | 否 | 页码，默认1 |
| pageSize | Integer | 否 | 每页条数，默认10 |
| title | String | 否 | 公告标题（模糊搜索） |
| announcementType | String | 否 | 公告类型 |
| status | String | 否 | 状态 |

**响应格式：** TableDataInfo 分页格式。

---

#### 3.9.2 导出公告

- **URL**：`POST /trade/announcement/export`
- **权限**：`trade:announcement:export`

**响应：** 返回 Excel 文件流。

---

#### 3.9.3 公告详情

- **URL**：`GET /trade/announcement/{id}`
- **权限**：`trade:announcement:query`

**路径参数：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | Long | 是 | 公告ID |

**响应格式：** AjaxResult。

---

#### 3.9.4 新增公告

- **URL**：`POST /trade/announcement`
- **权限**：`trade:announcement:add`

**请求体（Body）：**

```json
{
  "title": "系统维护通知",
  "content": "系统将于2025年2月1日凌晨2:00-6:00进行维护升级，届时将暂停所有交易服务。",
  "announcementType": "1",
  "status": "0"
}
```

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| title | String | 是 | 公告标题 |
| content | String | 是 | 公告内容 |
| announcementType | String | 否 | 公告类型：1-系统公告，2-活动公告，3-维护公告 |
| status | String | 否 | 状态：0-正常（显示），1-关闭（隐藏） |

**响应示例：**

```json
{
  "code": 200,
  "msg": "新增成功"
}
```

---

#### 3.9.5 修改公告

- **URL**：`PUT /trade/announcement`
- **权限**：`trade:announcement:edit`

**请求体（Body）：** 公告对象 JSON（需包含id字段）。

**响应示例：**

```json
{
  "code": 200,
  "msg": "修改成功"
}
```

---

#### 3.9.6 删除公告

- **URL**：`DELETE /trade/announcement/{ids}`
- **权限**：`trade:announcement:remove`

**路径参数：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| ids | String | 是 | 公告ID，多个用逗号分隔 |

**响应示例：**

```json
{
  "code": 200,
  "msg": "删除成功"
}
```

---

## 附录

### A. 接口汇总清单

| 序号 | 模块 | 方法 | URL | 权限 |
|------|------|------|-----|------|
| 1 | 市场 | GET | /api/market/list | 匿名 |
| 2 | 市场 | GET | /api/market/search | 匿名 |
| 3 | 市场 | GET | /api/market/hot | 匿名 |
| 4 | 市场 | GET | /api/market/categories | 匿名 |
| 5 | 市场 | GET | /api/market/detail/{skinId} | 匿名 |
| 6 | 市场 | GET | /api/market/priceHistory/{skinId} | 匿名 |
| 7 | 库存 | GET | /api/inventory/list | 登录 |
| 8 | 库存 | GET | /api/inventory/detail/{id} | 登录 |
| 9 | 库存 | PUT | /api/inventory/update/{id} | 登录 |
| 10 | 上架 | POST | /api/listing | 登录 |
| 11 | 上架 | DELETE | /api/listing/inventory/{inventoryId} | 登录 |
| 12 | 订单 | POST | /api/order/buy/{listingId} | 登录 |
| 13 | 订单 | POST | /api/order/confirm/{orderId} | 登录 |
| 14 | 订单 | POST | /api/order/cancel/{orderId} | 登录 |
| 15 | 订单 | GET | /api/order/my | 登录 |
| 16 | 求购 | POST | /api/buyOrder | 登录 |
| 17 | 求购 | GET | /api/buyOrder/list | 登录 |
| 18 | 求购 | GET | /api/buyOrder/my | 登录 |
| 19 | 求购 | PUT | /api/buyOrder/{id} | 登录 |
| 20 | 求购 | DELETE | /api/buyOrder/{id} | 登录 |
| 21 | 钱包 | GET | /api/wallet/my | 登录 |
| 22 | 钱包 | GET | /api/wallet/logs | 登录 |
| 23 | 钱包 | POST | /api/wallet/recharge | 登录 |
| 24 | 钱包 | POST | /api/wallet/withdraw | 登录 |
| 25 | 公告 | GET | /trade/announcement/publicList | 匿名 |
| 26 | 收藏 | POST | /api/favorite | 登录 |
| 27 | 收藏 | DELETE | /api/favorite/{listingId} | 登录 |
| 28 | 饰品管理 | GET | /api/admin/skin/list | admin:skin:list |
| 29 | 饰品管理 | GET | /api/admin/skin/{skinId} | admin:skin:query |
| 30 | 饰品管理 | POST | /api/admin/skin | admin:skin:add |
| 31 | 饰品管理 | PUT | /api/admin/skin | admin:skin:edit |
| 32 | 饰品管理 | DELETE | /api/admin/skin/{skinIds} | admin:skin:remove |
| 33 | 饰品管理 | PUT | /api/admin/skin/changeStatus | admin:skin:edit |
| 34 | 武器类型 | GET | /api/admin/weaponType/list | admin |
| 35 | 武器类型 | GET | /api/admin/weaponType/{id} | admin |
| 36 | 武器类型 | POST | /api/admin/weaponType | admin |
| 37 | 武器类型 | PUT | /api/admin/weaponType | admin |
| 38 | 武器类型 | DELETE | /api/admin/weaponType/{ids} | admin |
| 39 | 品质管理 | GET | /api/admin/quality/list | admin |
| 40 | 品质管理 | GET | /api/admin/quality/{id} | admin |
| 41 | 品质管理 | POST | /api/admin/quality | admin |
| 42 | 品质管理 | PUT | /api/admin/quality | admin |
| 43 | 品质管理 | DELETE | /api/admin/quality/{ids} | admin |
| 44 | 数据看板 | GET | /api/admin/stats/overview | admin:skin:list |
| 45 | 数据看板 | GET | /api/admin/stats/orderTrend | admin:skin:list |
| 46 | 数据看板 | GET | /api/admin/stats/topSkins | admin:skin:list |
| 47 | 数据看板 | GET | /api/admin/stats/statusDist | admin:skin:list |
| 48 | 订单管理 | GET | /trade/order/list | trade:order:list |
| 49 | 订单管理 | POST | /trade/order/export | trade:order:export |
| 50 | 订单管理 | GET | /trade/order/{id} | trade:order:query |
| 51 | 订单管理 | POST | /trade/order | trade:order:add |
| 52 | 订单管理 | PUT | /trade/order | trade:order:edit |
| 53 | 订单管理 | DELETE | /trade/order/{ids} | trade:order:remove |
| 54 | 钱包管理 | GET | /trade/wallet/list | trade:wallet:list |
| 55 | 钱包管理 | POST | /trade/wallet/export | trade:wallet:export |
| 56 | 钱包管理 | GET | /trade/wallet/{id} | trade:wallet:query |
| 57 | 钱包管理 | POST | /trade/wallet | trade:wallet:add |
| 58 | 钱包管理 | PUT | /trade/wallet | trade:wallet:edit |
| 59 | 钱包管理 | DELETE | /trade/wallet/{ids} | trade:wallet:remove |
| 60 | 流水管理 | GET | /trade/walletLog/list | trade:walletLog:list |
| 61 | 流水管理 | POST | /trade/walletLog/export | trade:walletLog:export |
| 62 | 流水管理 | GET | /trade/walletLog/{id} | trade:walletLog:query |
| 63 | 流水管理 | POST | /trade/walletLog | trade:walletLog:add |
| 64 | 流水管理 | PUT | /trade/walletLog | trade:walletLog:edit |
| 65 | 流水管理 | DELETE | /trade/walletLog/{ids} | trade:walletLog:remove |
| 66 | 收藏管理 | GET | /trade/favorite/list | trade:favorite:list |
| 67 | 收藏管理 | POST | /trade/favorite/export | trade:favorite:export |
| 68 | 收藏管理 | GET | /trade/favorite/{id} | trade:favorite:query |
| 69 | 收藏管理 | POST | /trade/favorite | trade:favorite:add |
| 70 | 收藏管理 | PUT | /trade/favorite | trade:favorite:edit |
| 71 | 收藏管理 | DELETE | /trade/favorite/{ids} | trade:favorite:remove |
| 72 | 公告管理 | GET | /trade/announcement/list | trade:announcement:list |
| 73 | 公告管理 | POST | /trade/announcement/export | trade:announcement:export |
| 74 | 公告管理 | GET | /trade/announcement/{id} | trade:announcement:query |
| 75 | 公告管理 | POST | /trade/announcement | trade:announcement:add |
| 76 | 公告管理 | PUT | /trade/announcement | trade:announcement:edit |
| 77 | 公告管理 | DELETE | /trade/announcement/{ids} | trade:announcement:remove |

### B. 业务状态码参考

| 业务场景 | 状态值 | 含义 |
|----------|--------|------|
| 库存状态 | 0 | 在库 |
| 库存状态 | 1 | 已上架（在售） |
| 库存状态 | 2 | 已售出 |
| 订单状态 | 0 | 待发货 |
| 订单状态 | 1 | 待收货 |
| 订单状态 | 2 | 已完成 |
| 订单状态 | 3 | 已取消 |
| 求购状态 | 0 | 求购中 |
| 求购状态 | 1 | 已完成（被接受） |
| 求购状态 | 2 | 已取消 |
| 通用状态 | 0 | 正常 |
| 通用状态 | 1 | 停用/关闭 |
| 流水类型 | 1 | 充值 |
| 流水类型 | 2 | 提现 |
| 流水类型 | 3 | 购买冻结 |
| 流水类型 | 4 | 购买扣款 |
| 流水类型 | 5 | 出售收入 |
| 流水类型 | 6 | 退款 |
| 流水类型 | 7 | 求购冻结 |
| 流水类型 | 8 | 求购退款 |

### C. 错误响应示例

**401 未认证：**

```json
{
  "code": 401,
  "msg": "Token已过期，请重新登录"
}
```

**403 权限不足：**

```json
{
  "code": 403,
  "msg": "没有权限访问该资源"
}
```

**500 业务异常：**

```json
{
  "code": 500,
  "msg": "该饰品已被购买，无法下单"
}
```

### D. 注意事项

1. **金额精度**：所有金额字段使用 `BigDecimal` 类型，精度为小数点后2位，单位为人民币元。
2. **时间格式**：所有时间字段统一使用 `yyyy-MM-dd HH:mm:ss` 格式。
3. **分页默认值**：未传分页参数时，默认 pageNum=1，pageSize=10。
4. **数据隔离**：前台接口自动按当前登录用户过滤数据，用户只能操作自己的资源。
5. **幂等性**：下单、充值等关键操作建议前端做防重复提交处理。
6. **文件上传**：图片等文件上传接口不在本文档范围内，参考若依文件上传接口。
