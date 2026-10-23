// 会话级缓存操作对象（基于 sessionStorage）
const sessionCache = {
  // 设置会话缓存值
  set (key, value) {
    // 检查 sessionStorage 是否可用
    if (!sessionStorage) {
      return
    }
    // 键值都非空时才存储
    if (key != null && value != null) {
      sessionStorage.setItem(key, value)
    }
  },
  // 获取会话缓存值
  get (key) {
    // 检查 sessionStorage 是否可用
    if (!sessionStorage) {
      return null
    }
    // 键为空返回 null
    if (key == null) {
      return null
    }
    return sessionStorage.getItem(key)
  },
  // 以 JSON 格式设置会话缓存
  setJSON (key, jsonValue) {
    // 值非空时序列化为字符串存储
    if (jsonValue != null) {
      this.set(key, JSON.stringify(jsonValue))
    }
  },
  // 以 JSON 格式获取会话缓存
  getJSON (key) {
    const value = this.get(key)
    // 值非空时反序列化为对象返回
    if (value != null) {
      return JSON.parse(value)
    }
  },
  // 移除指定的会话缓存
  remove (key) {
    sessionStorage.removeItem(key);
  }
}
// 本地缓存操作对象（基于 localStorage）
const localCache = {
  // 设置本地缓存值
  set (key, value) {
    // 检查 localStorage 是否可用
    if (!localStorage) {
      return
    }
    // 键值都非空时才存储
    if (key != null && value != null) {
      localStorage.setItem(key, value)
    }
  },
  // 获取本地缓存值
  get (key) {
    // 检查 localStorage 是否可用
    if (!localStorage) {
      return null
    }
    // 键为空返回 null
    if (key == null) {
      return null
    }
    return localStorage.getItem(key)
  },
  // 以 JSON 格式设置本地缓存
  setJSON (key, jsonValue) {
    // 值非空时序列化为字符串存储
    if (jsonValue != null) {
      this.set(key, JSON.stringify(jsonValue))
    }
  },
  // 以 JSON 格式获取本地缓存
  getJSON (key) {
    const value = this.get(key)
    // 值非空时反序列化为对象返回
    if (value != null) {
      return JSON.parse(value)
    }
  },
  // 移除指定的本地缓存
  remove (key) {
    localStorage.removeItem(key);
  }
}

// 导出缓存工具对象
export default {
  // 会话级缓存（关闭浏览器后清除）
  session: sessionCache,
  // 本地持久缓存（需手动清除）
  local: localCache
}
