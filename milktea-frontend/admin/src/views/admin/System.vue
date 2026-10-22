<template>
  <!-- 负责人: 成员A | 后端: milktea-user /employee + /role -->
  <div>
    <el-tabs v-model="activeTab" @tab-change="handleTabChange">
      <!-- ================= 员工管理 Tab ================= -->
      <el-tab-pane label="员工管理" name="staff">
        <div class="toolbar">
          <el-input
            v-model="empQuery.keyword"
            placeholder="按姓名/手机号搜索"
            clearable
            style="width: 220px"
            @keyup.enter="loadEmployees"
            @clear="loadEmployees"
          />
          <el-button type="primary" :icon="Search" @click="loadEmployees">查询</el-button>
          <el-button type="success" :icon="Plus" @click="openEmpDialog()">新增员工</el-button>
        </div>

        <el-table :data="employees" v-loading="empLoading" border stripe>
          <el-table-column prop="employeeNo" label="工号" width="100" />
          <el-table-column prop="realName" label="姓名" width="110" />
          <el-table-column label="性别" width="70" align="center">
            <template #default="{ row }">
              {{ genderText(row.gender) }}
            </template>
          </el-table-column>
          <el-table-column prop="position" label="职位" width="120" />
          <el-table-column prop="phone" label="手机号" width="130" />
          <el-table-column prop="roleName" label="角色" width="120" />
          <el-table-column label="状态" width="90" align="center">
            <template #default="{ row }">
              <el-tag :type="row.status === 1 ? 'success' : 'danger'">
                {{ row.status === 1 ? '启用' : '禁用' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" min-width="200" align="center">
            <template #default="{ row }">
              <el-button link type="primary" @click="openEmpDialog(row)">编辑</el-button>
              <el-button link :type="row.status === 1 ? 'warning' : 'success'" @click="toggleStatus(row)">
                {{ row.status === 1 ? '禁用' : '启用' }}
              </el-button>
              <el-popconfirm title="确定删除该员工吗?" @confirm="removeEmployee(row.id)">
                <template #reference>
                  <el-button link type="danger">删除</el-button>
                </template>
              </el-popconfirm>
            </template>
          </el-table-column>
        </el-table>

        <el-pagination
          class="pager"
          v-model:current-page="empQuery.pageNum"
          v-model:page-size="empQuery.pageSize"
          :total="empTotal"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next"
          @size-change="loadEmployees"
          @current-change="loadEmployees"
        />
      </el-tab-pane>

      <!-- ================= 角色权限 Tab ================= -->
      <el-tab-pane label="角色权限" name="role">
        <div class="toolbar">
          <el-button type="success" :icon="Plus" @click="openRoleDialog()">新增角色</el-button>
        </div>

        <el-table :data="roles" v-loading="roleLoading" border stripe>
          <el-table-column prop="roleName" label="角色名" width="160" />
          <el-table-column prop="roleCode" label="编码" width="160" />
          <el-table-column prop="description" label="描述" min-width="200" />
          <el-table-column label="状态" width="90" align="center">
            <template #default="{ row }">
              <el-tag :type="row.status === 1 ? 'success' : 'danger'">
                {{ row.status === 1 ? '启用' : '禁用' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" min-width="220" align="center">
            <template #default="{ row }">
              <el-button link type="primary" @click="openRoleDialog(row)">编辑</el-button>
              <el-button link type="success" @click="openPermDialog(row)">分配权限</el-button>
              <el-popconfirm title="确定删除该角色吗?" @confirm="removeRole(row.id)">
                <template #reference>
                  <el-button link type="danger">删除</el-button>
                </template>
              </el-popconfirm>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>

    <!-- ================= 员工新增/编辑弹窗 ================= -->
    <el-dialog
      v-model="empDialogVisible"
      :title="empForm.id ? '编辑员工' : '新增员工'"
      width="480px"
    >
      <el-form :model="empForm" label-width="80px">
        <el-form-item label="工号">
          <el-input v-model="empForm.employeeNo" placeholder="如 EMP003" />
        </el-form-item>
        <el-form-item label="姓名" required>
          <el-input v-model="empForm.realName" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="性别">
          <el-radio-group v-model="empForm.gender">
            <el-radio :value="1">男</el-radio>
            <el-radio :value="2">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="职位">
          <el-input v-model="empForm.position" placeholder="如 店长/店员" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="empForm.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="初始密码" v-if="!empForm.id">
          <el-input v-model="empForm.password" type="password" show-password placeholder="默认 123456" />
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="empForm.roleId" placeholder="请选择角色" clearable style="width: 100%">
            <el-option
              v-for="r in roles"
              :key="r.id"
              :label="r.roleName"
              :value="r.id"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="empDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="empSaving" @click="saveEmployee">保存</el-button>
      </template>
    </el-dialog>

    <!-- ================= 角色新增/编辑弹窗 ================= -->
    <el-dialog
      v-model="roleDialogVisible"
      :title="roleForm.id ? '编辑角色' : '新增角色'"
      width="440px"
    >
      <el-form :model="roleForm" label-width="80px">
        <el-form-item label="角色名" required>
          <el-input v-model="roleForm.roleName" placeholder="如 门店店长" />
        </el-form-item>
        <el-form-item label="编码" required>
          <el-input v-model="roleForm.roleCode" placeholder="如 STORE_MANAGER" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="roleForm.description" type="textarea" :rows="2" placeholder="角色职责描述" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="roleDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="roleSaving" @click="saveRole">保存</el-button>
      </template>
    </el-dialog>

    <!-- ================= 分配权限弹窗 ================= -->
    <el-dialog v-model="permDialogVisible" title="分配权限" width="420px">
      <el-tree
        ref="permTreeRef"
        :data="menuTree"
        :props="{ label: 'name', children: 'children' }"
        node-key="id"
        show-checkbox
        default-expand-all
        :default-checked-keys="checkedMenuIds"
      />
      <template #footer>
        <el-button @click="permDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="permSaving" @click="savePerms">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Search, Plus } from '@element-plus/icons-vue'
import {
  pageEmployee, addEmployee, updateEmployee, deleteEmployee,
  toggleEmployeeStatus, listRoles, addRole, updateRole,
  deleteRole, assignMenus, getMenuTree, getRole
} from '../../api/user'

const activeTab = ref('staff')

// ==================== 员工管理 ====================
const employees = ref([])
const empTotal = ref(0)
const empLoading = ref(false)
const empQuery = reactive({ pageNum: 1, pageSize: 10, keyword: '' })

const genderText = g => (g === 1 ? '男' : g === 2 ? '女' : '未知')

const loadEmployees = async () => {
  empLoading.value = true
  try {
    const res = await pageEmployee(empQuery)
    employees.value = res.data.list || []
    empTotal.value = res.data.total || 0
  } finally {
    empLoading.value = false
  }
}

// ==================== 员工弹窗 ====================
const empDialogVisible = ref(false)
const empSaving = ref(false)
const empForm = reactive({
  id: null, employeeNo: '', realName: '', gender: 1,
  position: '', phone: '', password: '', roleId: null
})

const openEmpDialog = row => {
  if (row) {
    Object.assign(empForm, {
      id: row.id, employeeNo: row.employeeNo, realName: row.realName,
      gender: row.gender || 1, position: row.position, phone: row.phone,
      password: '', roleId: row.roleId
    })
  } else {
    Object.assign(empForm, {
      id: null, employeeNo: '', realName: '', gender: 1,
      position: '', phone: '', password: '123456', roleId: null
    })
  }
  empDialogVisible.value = true
}

const saveEmployee = async () => {
  if (!empForm.realName) {
    ElMessage.warning('请填写姓名')
    return
  }
  empSaving.value = true
  try {
    if (empForm.id) {
      await updateEmployee(empForm)
    } else {
      await addEmployee(empForm)
    }
    ElMessage.success('保存成功')
    empDialogVisible.value = false
    loadEmployees()
  } finally {
    empSaving.value = false
  }
}

const toggleStatus = async row => {
  await toggleEmployeeStatus(row.id, row.status === 1 ? 0 : 1)
  ElMessage.success('操作成功')
  loadEmployees()
}

const removeEmployee = async id => {
  await deleteEmployee(id)
  ElMessage.success('删除成功')
  loadEmployees()
}

// ==================== 角色管理 ====================
const roles = ref([])
const roleLoading = ref(false)

const loadRoles = async () => {
  roleLoading.value = true
  try {
    const res = await listRoles()
    roles.value = res.data || []
  } finally {
    roleLoading.value = false
  }
}

// ==================== 角色弹窗 ====================
const roleDialogVisible = ref(false)
const roleSaving = ref(false)
const roleForm = reactive({ id: null, roleName: '', roleCode: '', description: '' })

const openRoleDialog = row => {
  if (row) {
    Object.assign(roleForm, {
      id: row.id, roleName: row.roleName,
      roleCode: row.roleCode, description: row.description
    })
  } else {
    Object.assign(roleForm, { id: null, roleName: '', roleCode: '', description: '' })
  }
  roleDialogVisible.value = true
}

const saveRole = async () => {
  if (!roleForm.roleName || !roleForm.roleCode) {
    ElMessage.warning('请填写角色名和编码')
    return
  }
  roleSaving.value = true
  try {
    if (roleForm.id) {
      await updateRole(roleForm)
    } else {
      await addRole(roleForm)
    }
    ElMessage.success('保存成功')
    roleDialogVisible.value = false
    loadRoles()
  } finally {
    roleSaving.value = false
  }
}

const removeRole = async id => {
  await deleteRole(id)
  ElMessage.success('删除成功')
  loadRoles()
}

// ==================== 分配权限 ====================
const permDialogVisible = ref(false)
const permSaving = ref(false)
const permTreeRef = ref(null)
const menuTree = ref([])
const checkedMenuIds = ref([])
const currentRoleId = ref(null)

const openPermDialog = async row => {
  currentRoleId.value = row.id
  // 加载菜单树 + 该角色已勾选的菜单
  const [treeRes, roleRes] = await Promise.all([getMenuTree(), getRole(row.id)])
  menuTree.value = treeRes.data || []
  // 只回显叶子节点，避免父节点自动全选子节点
  const allIds = roleRes.data?.menuIds || []
  checkedMenuIds.value = filterLeafIds(menuTree.value, allIds)
  permDialogVisible.value = true
}

// 从全选中筛出无子节点的叶子 id
const filterLeafIds = (nodes, allIds) => {
  const leafIds = []
  const walk = list => {
    for (const n of list) {
      if (n.children && n.children.length) {
        walk(n.children)
      } else if (allIds.includes(n.id)) {
        leafIds.push(n.id)
      }
    }
  }
  walk(nodes)
  return leafIds
}

const savePerms = async () => {
  const checked = permTreeRef.value.getCheckedKeys()
  const halfChecked = permTreeRef.value.getHalfCheckedKeys()
  permSaving.value = true
  try {
    await assignMenus(currentRoleId.value, [...checked, ...halfChecked])
    ElMessage.success('权限分配成功')
    permDialogVisible.value = false
  } finally {
    permSaving.value = false
  }
}

// ==================== 初始化 ====================
const handleTabChange = tab => {
  if (tab === 'role') loadRoles()
}

onMounted(() => {
  loadEmployees()
  loadRoles() // 角色下拉框也要用
})
</script>

<style scoped>
.toolbar {
  display: flex;
  gap: 10px;
  margin-bottom: 16px;
}
.pager {
  margin-top: 16px;
  justify-content: flex-end;
}
</style>
