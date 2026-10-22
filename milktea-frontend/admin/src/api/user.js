import request from './request'

// ========== 用户服务 API(后端: 成员A, 网关前缀 /api) ==========

// 登录(白名单)
export const login = data => request.post('/user/login', data)

// 注册(白名单)
export const register = data => request.post('/user/register', data)

// 查用户信息
export const getUserInfo = id => request.get(`/user/${id}`)

// 修改密码
export const updatePassword = data => request.put('/user/password', data)

// 修改个人信息
export const updateUserInfo = data => request.put('/user/info', data)

// 员工分页
export const pageEmployee = params => request.get('/employee/page', { params })

// 员工详情
export const getEmployee = id => request.get(`/employee/${id}`)

// 新增员工
export const addEmployee = data => request.post('/employee', data)

// 修改员工
export const updateEmployee = data => request.put('/employee', data)

// 删除员工
export const deleteEmployee = id => request.delete(`/employee/${id}`)

// 员工状态切换
export const toggleEmployeeStatus = (id, status) => request.put(`/employee/${id}/status`, null, { params: { status } })

// 角色列表
export const listRoles = () => request.get('/role/list')

// 角色详情(含menuIds)
export const getRole = id => request.get(`/role/${id}`)

// 新增/修改角色
export const addRole = data => request.post('/role', data)
export const updateRole = data => request.put('/role', data)

// 删除角色
export const deleteRole = id => request.delete(`/role/${id}`)

// 给角色分配菜单
export const assignMenus = (roleId, menuIds) => request.put(`/role/${roleId}/menus`, menuIds)

// 菜单树
export const getMenuTree = () => request.get('/role/menu/tree')
