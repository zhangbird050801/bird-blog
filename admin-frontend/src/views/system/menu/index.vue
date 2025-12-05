<template>
  <div class="menu-container">
    <!-- 搜索栏 -->
    <el-card class="search-card" shadow="never">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="菜单名称">
          <el-input
            v-model="searchForm.name"
            placeholder="请输入菜单名称"
            clearable
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-select
            v-model="searchForm.status"
            placeholder="菜单状态"
            clearable
            style="width: 120px"
          >
            <el-option label="正常" :value="0" />
            <el-option label="停用" :value="1" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>
            查询
          </el-button>
          <el-button @click="handleReset">
            <el-icon><Refresh /></el-icon>
            重置
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 操作栏 -->
    <el-card class="toolbar-card" shadow="never">
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>
        新增菜单
      </el-button>
      <el-button @click="toggleExpandAll">
        <el-icon><Sort /></el-icon>
        {{ expandAll ? '折叠' : '展开' }}全部
      </el-button>
    </el-card>

    <!-- 菜单表格 -->
    <el-card class="table-card" shadow="never">
      <el-table
        v-loading="loading"
        :data="menuList"
        row-key="id"
        :default-expand-all="expandAll"
        :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
        border
        style="width: 100%"
      >
        <el-table-column prop="menuName" label="菜单名称" width="180" />
        <el-table-column prop="icon" label="图标" width="80" align="center">
          <template #default="{ row }">
            <component
              v-if="getIconComponent(row.icon)"
              :is="getIconComponent(row.icon)"
              style="width: 18px; height: 18px"
            />
            <span v-else>{{ row.icon || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="orderNum" label="排序" width="80" align="center" />
        <el-table-column prop="perms" label="权限标识" width="180">
          <template #default="{ row }">
            <el-tag v-if="row.perms" size="small">{{ row.perms }}</el-tag>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="component" label="组件路径" min-width="200" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 0 ? 'success' : 'danger'" size="small">
              {{ row.status === 0 ? '正常' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="160" />
        <el-table-column label="操作" width="240" align="center" fixed="right">
          <template #default="{ row }">
            <el-button
              type="primary"
              link
              size="small"
              @click="handleEdit(row)"
            >
              <el-icon><Edit /></el-icon>
              修改
            </el-button>
            <el-button
              type="primary"
              link
              size="small"
              @click="handleAddChild(row)"
            >
              <el-icon><Plus /></el-icon>
              新增
            </el-button>
            <el-button
              type="danger"
              link
              size="small"
              @click="handleDelete(row)"
            >
              <el-icon><Delete /></el-icon>
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="680px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="100px"
      >
        <el-form-item label="上级菜单">
          <el-tree-select
            v-model="formData.parentId"
            :data="menuOptions"
            :props="{ value: 'value', label: 'label', children: 'children' }"
            check-strictly
            :render-after-expand="false"
            placeholder="选择上级菜单"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="菜单类型" prop="type">
          <el-radio-group v-model="formData.type">
            <el-radio label="M">目录</el-radio>
            <el-radio label="C">菜单</el-radio>
            <el-radio label="F">按钮</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="菜单名称" prop="name">
          <el-input v-model="formData.name" placeholder="请输入菜单名称" />
        </el-form-item>
        <el-form-item v-if="formData.type !== 'F'" label="菜单图标">
          <div class="icon-field">
            <el-input
              v-model="formData.icon"
              placeholder="选择或输入图标名称"
              clearable
            >
              <template #prepend>
                <component
                  v-if="getIconComponent(formData.icon)"
                  :is="getIconComponent(formData.icon)"
                  class="icon-preview"
                />
              </template>
              <template #append>
                <el-button type="primary" text @click="openIconPicker">选择图标</el-button>
              </template>
            </el-input>
            <div class="icon-hint">
              <span>支持直接搜索 Element Plus 图标名称</span>
              <el-button
                v-if="formData.icon"
                link
                type="danger"
                size="small"
                @click="clearIcon"
              >
                清除
              </el-button>
            </div>
          </div>
        </el-form-item>
        <el-form-item v-if="formData.type !== 'F'" label="路由地址" prop="path">
          <el-input v-model="formData.path" placeholder="请输入路由地址" />
        </el-form-item>
        <el-form-item v-if="formData.type === 'C'" label="组件路径" prop="component">
          <el-input v-model="formData.component" placeholder="请输入组件路径" />
        </el-form-item>
        <el-form-item v-if="formData.type !== 'M'" label="权限标识">
          <el-input v-model="formData.perm" placeholder="请输入权限标识，如：system:menu:list" />
        </el-form-item>
        <el-form-item label="显示排序" prop="sort">
          <el-input-number v-model="formData.sort" :min="0" controls-position="right" />
        </el-form-item>
        <el-form-item label="显示状态">
          <el-radio-group v-model="formData.visible">
            <el-radio :label="0">显示</el-radio>
            <el-radio :label="1">隐藏</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="菜单状态">
          <el-radio-group v-model="formData.status">
            <el-radio :label="0">正常</el-radio>
            <el-radio :label="1">停用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注">
          <el-input
            v-model="formData.remark"
            type="textarea"
            :rows="3"
            placeholder="请输入备注"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">
          确定
        </el-button>
      </template>
    </el-dialog>

    <!-- 图标选择器 -->
    <el-dialog
      v-model="iconPickerVisible"
      title="选择菜单图标"
      width="760px"
      append-to-body
    >
      <div class="icon-picker-header">
        <el-input
          v-model="iconKeyword"
          placeholder="搜索图标名称（模糊匹配）"
          clearable
          class="icon-search"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
      </div>
      <el-scrollbar height="360px" class="icon-scroll">
        <div class="icon-grid" v-if="filteredIconNames.length">
          <div
            v-for="iconName in filteredIconNames"
            :key="iconName"
            class="icon-item"
            @click="handleIconSelect(iconName)"
          >
            <component :is="iconName" class="icon-preview" />
            <span class="icon-name">{{ iconName }}</span>
          </div>
        </div>
        <div v-else class="icon-empty">未找到匹配的图标</div>
      </el-scrollbar>
      <template #footer>
        <el-button @click="iconPickerVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { menuApi } from '@/api'
import type { MenuItem } from '@/types'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'

// 图标映射表
const iconMap: Record<string, string> = {
  'homepage': 'HomeFilled',
  'dashboard': 'DataLine',
  'folder': 'Folder',
  'file-text': 'Document',
  'grid': 'Grid',
  'tag': 'PriceTag',
  'message-square': 'ChatDotSquare',
  'link': 'Link',
  'setting': 'Setting',
  'tree-table': 'Menu'
}

// 获取图标组件名
const getIconComponent = (iconName?: string) => {
  if (!iconName || iconName === '#') return null
  if (iconMap[iconName]) return iconMap[iconName]
  const pascal = iconName.split(/[-_ ]+/).map(s => s.charAt(0).toUpperCase() + s.slice(1)).join('')
  return pascal
}

// 搜索表单
const searchForm = reactive({
  name: '',
  status: undefined as number | undefined
})

// 菜单列表
const menuList = ref<MenuItem[]>([])
const loading = ref(false)
const expandAll = ref(false)

// 菜单选项（用于选择父菜单）
const menuOptions = ref<any[]>([
  { value: null, label: '主类目', children: [] }
])

// 对话框相关
const dialogVisible = ref(false)
const dialogTitle = computed(() => formData.id ? '编辑菜单' : '新增菜单')
const submitLoading = ref(false)
const formRef = ref<FormInstance>()

// 表单数据
const formData = reactive({
  id: undefined as number | undefined,
  name: '',
  parentId: null as number | null,
  sort: 0,
  path: '',
  component: '',
  type: 'M' as 'M' | 'C' | 'F',
  visible: 0,
  status: 0,
  perm: '',
  icon: '',
  remark: ''
})

// 图标选择器状态
const iconPickerVisible = ref(false)
const iconKeyword = ref('')
const iconNames = Object.keys(ElementPlusIconsVue).filter(name => name !== 'default')
const filteredIconNames = computed(() => {
  const keyword = iconKeyword.value.trim().toLowerCase()
  if (!keyword) return iconNames
  return iconNames.filter(name => name.toLowerCase().includes(keyword))
})

const openIconPicker = () => {
  iconKeyword.value = ''
  iconPickerVisible.value = true
}

const handleIconSelect = (iconName: string) => {
  formData.icon = iconName
  iconPickerVisible.value = false
}

const clearIcon = () => {
  formData.icon = ''
}

// 表单验证规则
const formRules: FormRules = {
  name: [{ required: true, message: '请输入菜单名称', trigger: 'blur' }],
  type: [{ required: true, message: '请选择菜单类型', trigger: 'change' }],
  path: [{ required: true, message: '请输入路由地址', trigger: 'blur' }],
  sort: [{ required: true, message: '请输入显示排序', trigger: 'blur' }]
}

// 查询菜单列表
const getMenuList = async () => {
  loading.value = true
  try {
    const params = {
      name: searchForm.name || undefined,
      status: searchForm.status
    }
    menuList.value = await menuApi.getMenuTree(params)
  } catch (error) {
    console.error('获取菜单列表失败:', error)
    ElMessage.error('获取菜单列表失败')
  } finally {
    loading.value = false
  }
}

// 获取菜单选项
const getMenuOptions = async () => {
  try {
    const options = await menuApi.getMenuOptions()
    menuOptions.value = [
      { value: null, label: '主类目', children: options }
    ]
  } catch (error) {
    console.error('获取菜单选项失败:', error)
  }
}

// 搜索
const handleSearch = () => {
  getMenuList()
}

// 重置
const handleReset = () => {
  searchForm.name = ''
  searchForm.status = undefined
  getMenuList()
}

// 切换展开/折叠
const toggleExpandAll = () => {
  expandAll.value = !expandAll.value
}

// 新增菜单
const handleAdd = () => {
  resetForm()
  dialogVisible.value = true
}

// 新增子菜单
const handleAddChild = (row: MenuItem) => {
  resetForm()
  formData.parentId = row.id
  dialogVisible.value = true
}

// 编辑菜单
const handleEdit = async (row: MenuItem) => {
  try {
    const menu = await menuApi.getMenuDetail(row.id)
    formData.id = menu.id
    formData.name = menu.menuName
    formData.parentId = menu.parentId
    formData.sort = menu.orderNum
    formData.path = menu.path
    formData.component = menu.component || ''
    formData.type = menu.menuType
    formData.visible = menu.visible
    formData.status = menu.status
    formData.perm = menu.perms || ''
    formData.icon = menu.icon || ''
    formData.remark = menu.remark || ''
    dialogVisible.value = true
  } catch (error) {
    console.error('获取菜单详情失败:', error)
    ElMessage.error('获取菜单详情失败')
  }
}

// 删除菜单
const handleDelete = async (row: MenuItem) => {
  try {
    await ElMessageBox.confirm(`确定要删除菜单"${row.menuName}"吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await menuApi.deleteMenu(row.id)
    ElMessage.success('删除成功')
    getMenuList()
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('删除菜单失败:', error)
      ElMessage.error(error.response?.data?.msg || '删除菜单失败')
    }
  }
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    
    submitLoading.value = true
    try {
      const data = {
        name: formData.name,
        parentId: formData.parentId,
        sort: formData.sort,
        path: formData.path,
        component: formData.component || undefined,
        type: formData.type,
        visible: formData.visible,
        status: formData.status,
        perm: formData.perm || undefined,
        icon: formData.icon || undefined,
        remark: formData.remark || undefined
      }
      
      if (formData.id) {
        await menuApi.updateMenu(formData.id, data)
        ElMessage.success('修改成功')
      } else {
        await menuApi.createMenu(data)
        ElMessage.success('新增成功')
      }
      
      dialogVisible.value = false
      getMenuList()
      getMenuOptions()
    } catch (error) {
      console.error('保存菜单失败:', error)
      ElMessage.error('保存菜单失败')
    } finally {
      submitLoading.value = false
    }
  })
}

// 重置表单
const resetForm = () => {
  formData.id = undefined
  formData.name = ''
  formData.parentId = null
  formData.sort = 0
  formData.path = ''
  formData.component = ''
  formData.type = 'M'
  formData.visible = 0
  formData.status = 0
  formData.perm = ''
  formData.icon = ''
  formData.remark = ''
  formRef.value?.clearValidate()
}

// 初始化
onMounted(() => {
  getMenuList()
  getMenuOptions()
})
</script>

<style scoped>
.menu-container {
  padding: 20px;
}

.search-card,
.toolbar-card,
.table-card {
  margin-bottom: 20px;
}

.search-form {
  margin-bottom: -18px;
}

.icon-field {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.icon-preview {
  width: 20px;
  height: 20px;
}

.icon-hint {
  display: flex;
  align-items: center;
  justify-content: space-between;
  color: #909399;
  font-size: 12px;
}

.icon-picker-header {
  margin-bottom: 12px;
}

.icon-search {
  width: 320px;
}

.icon-scroll {
  border: 1px solid #ebeef5;
  border-radius: 8px;
  padding: 12px;
}

.icon-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(120px, 1fr));
  gap: 12px;
}

.icon-item {
  border: 1px solid #ebeef5;
  border-radius: 8px;
  padding: 10px;
  cursor: pointer;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  transition: all 0.2s ease;
  text-align: center;
}

.icon-item:hover {
  border-color: var(--el-color-primary);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.04);
  transform: translateY(-1px);
}

.icon-name {
  font-size: 12px;
  color: #606266;
  word-break: break-all;
}

.icon-empty {
  text-align: center;
  color: #909399;
  padding: 24px 0;
}
</style>
