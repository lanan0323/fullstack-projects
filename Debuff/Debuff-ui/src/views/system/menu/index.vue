<!-- 菜单管理页面 -->
<template>
   <div class="app-container">
      <!-- 搜索表单 -->
      <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
         <!-- 菜单名称搜索 -->
         <el-form-item label="菜单名称" prop="menuName">
            <el-input
               v-model="queryParams.menuName"
               placeholder="请输入菜单名称"
               clearable
               style="width: 200px"
               @keyup.enter="handleQuery"
            />
         </el-form-item>
         <!-- 状态筛选 -->
         <el-form-item label="状态" prop="status">
            <el-select v-model="queryParams.status" placeholder="菜单状态" clearable style="width: 200px">
               <el-option
                  v-for="dict in sys_normal_disable"
                  :key="dict.value"
                  :label="dict.label"
                  :value="dict.value"
               />
            </el-select>
         </el-form-item>
         <!-- 搜索和重置按钮 -->
         <el-form-item>
            <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
            <el-button icon="Refresh" @click="resetQuery">重置</el-button>
         </el-form-item>
      </el-form>

      <!-- 工具栏按钮组 -->
      <el-row :gutter="10" class="mb8">
         <!-- 新增按钮 -->
         <el-col :span="1.5">
            <el-button
               type="primary"
               plain
               icon="Plus"
               @click="handleAdd"
               v-hasPermi="['system:menu:add']"
            >新增</el-button>
         </el-col>
         <!-- 展开/折叠按钮 -->
         <el-col :span="1.5">
            <el-button 
               type="info"
               plain
               icon="Sort"
               @click="toggleExpandAll"
            >展开/折叠</el-button>
         </el-col>
         <!-- 右侧工具栏 -->
         <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
      </el-row>

      <!-- 菜单树形表格 -->
      <el-table
         v-if="refreshTable"
         v-loading="loading"
         :data="menuList"
         row-key="menuId"
         :default-expand-all="isExpandAll"
         :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
      >
         <!-- 菜单名称列 -->
         <el-table-column prop="menuName" label="菜单名称" :show-overflow-tooltip="true" width="160"></el-table-column>
         <!-- 图标列 -->
         <el-table-column prop="icon" label="图标" align="center" width="100">
            <template #default="scope">
               <!-- SVG图标组件 -->
               <svg-icon :icon-class="scope.row.icon" />
            </template>
         </el-table-column>
         <!-- 排序列 -->
         <el-table-column prop="orderNum" label="排序" width="60"></el-table-column>
         <!-- 权限标识列 -->
         <el-table-column prop="perms" label="权限标识" :show-overflow-tooltip="true"></el-table-column>
         <!-- 组件路径列 -->
         <el-table-column prop="component" label="组件路径" :show-overflow-tooltip="true"></el-table-column>
         <!-- 状态列 -->
         <el-table-column prop="status" label="状态" width="80">
            <template #default="scope">
               <!-- 字典标签显示状态 -->
               <dict-tag :options="sys_normal_disable" :value="scope.row.status" />
            </template>
         </el-table-column>
         <!-- 创建时间列 -->
         <el-table-column label="创建时间" align="center" width="160" prop="createTime">
            <template #default="scope">
               <span>{{ parseTime(scope.row.createTime) }}</span>
            </template>
         </el-table-column>
         <!-- 操作列 -->
         <el-table-column label="操作" align="center" width="210" class-name="small-padding fixed-width">
            <template #default="scope">
               <!-- 行内修改按钮 -->
               <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['system:menu:edit']">修改</el-button>
               <!-- 行内新增子菜单按钮 -->
               <el-button link type="primary" icon="Plus" @click="handleAdd(scope.row)" v-hasPermi="['system:menu:add']">新增</el-button>
               <!-- 行内删除按钮 -->
               <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['system:menu:remove']">删除</el-button>
            </template>
         </el-table-column>
      </el-table>

      <!-- 添加或修改菜单对话框 -->
      <el-dialog :title="title" v-model="open" width="680px" append-to-body>
         <!-- 菜单表单 -->
         <el-form ref="menuRef" :model="form" :rules="rules" label-width="100px">
            <el-row>
               <!-- 上级菜单树形选择 -->
               <el-col :span="24">
                  <el-form-item label="上级菜单">
                     <el-tree-select
                        v-model="form.parentId"
                        :data="menuOptions"
                        :props="{ value: 'menuId', label: 'menuName', children: 'children' }"
                        value-key="menuId"
                        placeholder="选择上级菜单"
                        check-strictly
                     />
                  </el-form-item>
               </el-col>
               <!-- 菜单类型单选(目录/菜单/按钮) -->
               <el-col :span="24">
                  <el-form-item label="菜单类型" prop="menuType">
                     <el-radio-group v-model="form.menuType">
                        <el-radio label="M">目录</el-radio>
                        <el-radio label="C">菜单</el-radio>
                        <el-radio label="F">按钮</el-radio>
                     </el-radio-group>
                  </el-form-item>
               </el-col>
               <!-- 菜单图标选择(非按钮类型显示) -->
               <el-col :span="24" v-if="form.menuType != 'F'">
                  <el-form-item label="菜单图标" prop="icon">
                     <!-- 图标选择弹出框 -->
                     <el-popover
                        placement="bottom-start"
                        :width="540"
                        trigger="click"
                     >
                        <template #reference>
                           <!-- 图标输入框(只读) -->
                           <el-input v-model="form.icon" placeholder="点击选择图标" @blur="showSelectIcon" readonly>
                              <template #prefix>
                                 <!-- 已选图标预览 -->
                                 <svg-icon
                                    v-if="form.icon"
                                    :icon-class="form.icon"
                                    class="el-input__icon"
                                    style="height: 32px;width: 16px;"
                                 />
                                 <!-- 未选图标显示搜索图标 -->
                                 <el-icon v-else style="height: 32px;width: 16px;"><search /></el-icon>
                              </template>
                           </el-input>
                        </template>
                        <!-- 图标选择器组件 -->
                        <icon-select ref="iconSelectRef" @selected="selected" :active-icon="form.icon" />
                     </el-popover>
                  </el-form-item>
               </el-col>
               <!-- 菜单名称输入 -->
               <el-col :span="12">
                  <el-form-item label="菜单名称" prop="menuName">
                     <el-input v-model="form.menuName" placeholder="请输入菜单名称" />
                  </el-form-item>
               </el-col>
               <!-- 显示排序输入 -->
               <el-col :span="12">
                  <el-form-item label="显示排序" prop="orderNum">
                     <el-input-number v-model="form.orderNum" controls-position="right" :min="0" />
                  </el-form-item>
               </el-col>
               <!-- 是否外链(非按钮类型显示) -->
               <el-col :span="12" v-if="form.menuType != 'F'">
                  <el-form-item>
                     <template #label>
                        <span>
                           <!-- 提示图标 -->
                           <el-tooltip content="选择是外链则路由地址需要以`http(s)://`开头" placement="top">
                              <el-icon><question-filled /></el-icon>
                           </el-tooltip>是否外链
                        </span>
                     </template>
                     <!-- 外链单选 -->
                     <el-radio-group v-model="form.isFrame">
                        <el-radio label="0">是</el-radio>
                        <el-radio label="1">否</el-radio>
                     </el-radio-group>
                  </el-form-item>
               </el-col>
               <!-- 路由地址(非按钮类型显示) -->
               <el-col :span="12" v-if="form.menuType != 'F'">
                  <el-form-item prop="path">
                     <template #label>
                        <span>
                           <!-- 提示图标 -->
                           <el-tooltip content="访问的路由地址，如：`user`，如外网地址需内链访问则以`http(s)://`开头" placement="top">
                              <el-icon><question-filled /></el-icon>
                           </el-tooltip>
                           路由地址
                        </span>
                     </template>
                     <el-input v-model="form.path" placeholder="请输入路由地址" />
                  </el-form-item>
               </el-col>
               <!-- 组件路径(仅菜单类型显示) -->
               <el-col :span="12" v-if="form.menuType == 'C'">
                  <el-form-item prop="component">
                     <template #label>
                        <span>
                           <!-- 提示图标 -->
                           <el-tooltip content="访问的组件路径，如：`system/user/index`，默认在`views`目录下" placement="top">
                              <el-icon><question-filled /></el-icon>
                           </el-tooltip>
                           组件路径
                        </span>
                     </template>
                     <el-input v-model="form.component" placeholder="请输入组件路径" />
                  </el-form-item>
               </el-col>
               <!-- 权限标识(非目录类型显示) -->
               <el-col :span="12" v-if="form.menuType != 'M'">
                  <el-form-item>
                     <el-input v-model="form.perms" placeholder="请输入权限标识" maxlength="100" />
                     <template #label>
                        <span>
                           <!-- 提示图标 -->
                           <el-tooltip content="控制器中定义的权限字符，如：@PreAuthorize(`@ss.hasPermi('system:user:list')`)" placement="top">
                              <el-icon><question-filled /></el-icon>
                           </el-tooltip>
                           权限字符
                        </span>
                     </template>
                  </el-form-item>
               </el-col>
               <!-- 路由参数(仅菜单类型显示) -->
               <el-col :span="12" v-if="form.menuType == 'C'">
                  <el-form-item>
                     <el-input v-model="form.query" placeholder="请输入路由参数" maxlength="255" />
                     <template #label>
                        <span>
                           <!-- 提示图标 -->
                           <el-tooltip content='访问路由的默认传递参数，如：`{"id": 1, "name": "ry"}`' placement="top">
                              <el-icon><question-filled /></el-icon>
                           </el-tooltip>
                           路由参数
                        </span>
                     </template>
                  </el-form-item>
               </el-col>
               <!-- 是否缓存(仅菜单类型显示) -->
               <el-col :span="12" v-if="form.menuType == 'C'">
                  <el-form-item>
                     <template #label>
                        <span>
                           <!-- 提示图标 -->
                           <el-tooltip content="选择是则会被`keep-alive`缓存，需要匹配组件的`name`和地址保持一致" placement="top">
                              <el-icon><question-filled /></el-icon>
                           </el-tooltip>
                           是否缓存
                        </span>
                     </template>
                     <!-- 缓存单选 -->
                     <el-radio-group v-model="form.isCache">
                        <el-radio label="0">缓存</el-radio>
                        <el-radio label="1">不缓存</el-radio>
                     </el-radio-group>
                  </el-form-item>
               </el-col>
               <!-- 显示状态(非按钮类型显示) -->
               <el-col :span="12" v-if="form.menuType != 'F'">
                  <el-form-item>
                     <template #label>
                        <span>
                           <!-- 提示图标 -->
                           <el-tooltip content="选择隐藏则路由将不会出现在侧边栏，但仍然可以访问" placement="top">
                              <el-icon><question-filled /></el-icon>
                           </el-tooltip>
                           显示状态
                        </span>
                     </template>
                     <!-- 显示/隐藏单选 -->
                     <el-radio-group v-model="form.visible">
                        <el-radio
                           v-for="dict in sys_show_hide"
                           :key="dict.value"
                           :label="dict.value"
                        >{{ dict.label }}</el-radio>
                     </el-radio-group>
                  </el-form-item>
               </el-col>
               <!-- 菜单状态(所有类型都显示) -->
               <el-col :span="12">
                  <el-form-item>
                     <template #label>
                        <span>
                           <!-- 提示图标 -->
                           <el-tooltip content="选择停用则路由将不会出现在侧边栏，也不能被访问" placement="top">
                              <el-icon><question-filled /></el-icon>
                           </el-tooltip>
                           菜单状态
                        </span>
                     </template>
                     <!-- 正常/停用单选 -->
                     <el-radio-group v-model="form.status">
                        <el-radio
                           v-for="dict in sys_normal_disable"
                           :key="dict.value"
                           :label="dict.value"
                        >{{ dict.label }}</el-radio>
                     </el-radio-group>
                  </el-form-item>
               </el-col>
            </el-row>
         </el-form>
         <!-- 对话框底部按钮 -->
         <template #footer>
            <div class="dialog-footer">
               <!-- 确定按钮 -->
               <el-button type="primary" @click="submitForm">确 定</el-button>
               <!-- 取消按钮 -->
               <el-button @click="cancel">取 消</el-button>
            </div>
         </template>
      </el-dialog>
   </div>
</template>

<script setup name="Menu">
// 导入菜单CRUD API
import { addMenu, delMenu, getMenu, listMenu, updateMenu } from "@/api/system/menu";
// 导入SVG图标组件
import SvgIcon from "@/components/SvgIcon";
// 导入图标选择器组件
import IconSelect from "@/components/IconSelect";

// 获取组件代理
const { proxy } = getCurrentInstance();
// 获取显示/隐藏和正常/禁用字典
const { sys_show_hide, sys_normal_disable } = proxy.useDict("sys_show_hide", "sys_normal_disable");

// 菜单列表(树形结构)
const menuList = ref([]);
// 对话框显示状态
const open = ref(false);
// 加载状态
const loading = ref(true);
// 搜索区域显示状态
const showSearch = ref(true);
// 对话框标题
const title = ref("");
// 上级菜单下拉树选项
const menuOptions = ref([]);
// 是否全部展开
const isExpandAll = ref(false);
// 控制表格刷新(展开/折叠时先销毁再重建)
const refreshTable = ref(true);
// 图标选择器引用
const iconSelectRef = ref(null);

// 表单数据和查询参数及校验规则
const data = reactive({
  form: {},
  queryParams: {
    menuName: undefined,
    visible: undefined
  },
  rules: {
    menuName: [{ required: true, message: "菜单名称不能为空", trigger: "blur" }],
    orderNum: [{ required: true, message: "菜单顺序不能为空", trigger: "blur" }],
    path: [{ required: true, message: "路由地址不能为空", trigger: "blur" }]
  },
});

// 解构为ref
const { queryParams, form, rules } = toRefs(data);

/** 查询菜单列表 */
function getList() {
  loading.value = true;
  listMenu(queryParams.value).then(response => {
    // 将平铺数据转为树形结构
    menuList.value = proxy.handleTree(response.data, "menuId");
    loading.value = false;
  });
}
/** 查询菜单下拉树结构(用于对话框中选择上级菜单) */
function getTreeselect() {
  menuOptions.value = [];
  listMenu().then(response => {
    // 构建根节点"主类目"
    const menu = { menuId: 0, menuName: "主类目", children: [] };
    menu.children = proxy.handleTree(response.data, "menuId");
    menuOptions.value.push(menu);
  });
}
/** 取消按钮 */
function cancel() {
  open.value = false;
  reset();
}
/** 表单重置 */
function reset() {
  form.value = {
    menuId: undefined,
    parentId: 0,
    menuName: undefined,
    icon: undefined,
    menuType: "M",
    orderNum: undefined,
    isFrame: "1",
    isCache: "0",
    visible: "0",
    status: "0"
  };
  proxy.resetForm("menuRef");
}
/** 展示下拉图标(失焦时重置图标选择器) */
function showSelectIcon() {
  iconSelectRef.value.reset();
}
/** 选择图标回调 */
function selected(name) {
  form.value.icon = name;
}
/** 搜索按钮操作 */
function handleQuery() {
  getList();
}
/** 重置按钮操作 */
function resetQuery() {
  proxy.resetForm("queryRef");
  handleQuery();
}
/** 新增按钮操作 */
function handleAdd(row) {
  reset();
  getTreeselect();
  // 如果有传入行数据则设置父菜单ID
  if (row != null && row.menuId) {
    form.value.parentId = row.menuId;
  } else {
    form.value.parentId = 0;
  }
  open.value = true;
  title.value = "添加菜单";
}
/** 展开/折叠操作(通过销毁重建表格实现) */
function toggleExpandAll() {
  refreshTable.value = false;
  isExpandAll.value = !isExpandAll.value;
  nextTick(() => {
    refreshTable.value = true;
  });
}
/** 修改按钮操作 */
async function handleUpdate(row) {
  reset();
  // 先加载菜单下拉树
  await getTreeselect();
  // 获取菜单详情
  getMenu(row.menuId).then(response => {
    form.value = response.data;
    open.value = true;
    title.value = "修改菜单";
  });
}
/** 提交按钮 */
function submitForm() {
  proxy.$refs["menuRef"].validate(valid => {
    if (valid) {
      if (form.value.menuId != undefined) {
        // 修改菜单
        updateMenu(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功");
          open.value = false;
          getList();
        });
      } else {
        // 新增菜单
        addMenu(form.value).then(response => {
          proxy.$modal.msgSuccess("新增成功");
          open.value = false;
          getList();
        });
      }
    }
  });
}
/** 删除按钮操作 */
function handleDelete(row) {
  proxy.$modal.confirm('是否确认删除名称为"' + row.menuName + '"的数据项?').then(function() {
    return delMenu(row.menuId);
  }).then(() => {
    getList();
    proxy.$modal.msgSuccess("删除成功");
  }).catch(() => {});
}

// 页面加载时获取菜单列表
getList();
</script>
