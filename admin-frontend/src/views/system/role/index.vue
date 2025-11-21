<template>
  <div class="role-page">
    <el-card shadow="never" class="search-card">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="关键词">
          <el-input
            v-model="searchForm.keyword"
            placeholder="角色名称/编码"
            clearable
            style="width: 220px"
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="全部" clearable style="width: 140px">
            <el-option label="正常" :value="0" />
            <el-option label="禁用" :value="1" />
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

    <el-card shadow="never" class="table-card">
      <div class="table-header">
        <el-button type="primary" @click="handleAdd">
          <el-icon><Plus /></el-icon>
          新增角色
        </el-button>
      </div>

      <el-table :data="roleList" v-loading="loading" border stripe>
        <el-table-column prop="name" label="角色名称" width="160" />
        <el-table-column prop="code" label="角色编码" width="160">
          <template #default="{ row }">
            <el-tag type="info" size="small">{{ row.code }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 0 ? 'success' : 'danger'" size="small">
              {{ row.status === 0 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="200" align="center" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="handleEdit(row)">
              <el-icon><Edit /></el-icon>
              编辑
            </el-button>
            <el-button type="success" link size="small" @click="handlePermission(row)">
              <el-icon><Key /></el-icon>
              权限
            </el-button>
            <el-button
              type="warning"
              link
              size="small"
              @click="handleToggleStatus(row)"
              v-if="row.code !== 'SUPER_ADMIN'"
            >
              <el-icon><Switch /></el-icon>
              {{ row.status === 0 ? '禁用' : '启用' }}
            </el-button>
            <el-button
              type="danger"
              link
              size="small"
              @click="handleDelete(row)"
              v-if="row.code !== 'SUPER_ADMIN'"
            >
              <el-icon><Delete /></el-icon>
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="table-pagination">
        <el-pagination
          background
          layout="total, sizes, prev, pager, next"
          :page-sizes="[10, 20, 50]"
          :total="pagination.total"
          v-model:current-page="pagination.pageNum"
          v-model:page-size="pagination.pageSize"
          @current-change="handlePageChange"
          @size-change="handleSizeChange"
        />
      </div>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      :title="dialogTitle"
      v-model="dialogVisible"
      width="600px"
      @close="handleDialogClose"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="80px"
      >
        <el-form-item label="角色名称" prop="name">
          <el-input v-model="formData.name" placeholder="请输入角色名称" />
        </el-form-item>
        <el-form-item label="角色编码" prop="code">
          <el-input
            v-model="formData.code"
            placeholder="请输入角色编码"
            :disabled="!!formData.id"
          />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input
            v-model="formData.description"
            type="textarea"
            :rows="3"
            placeholder="请输入角色描述"
          />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="formData.status">
            <el-radio :label="0">正常</el-radio>
            <el-radio :label="1">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">
          确定
        </el-button>
      </template>
    </el-dialog>

    <!-- 权限分配对话框 -->
    <el-dialog
      title="权限分配"
      v-model="permissionDialogVisible"
      width="600px"
      @close="handlePermissionDialogClose"
    >
      <el-tree
        ref="permissionTreeRef"
        :data="menuTree"
        :props="treeProps"
        show-checkbox
        node-key="id"
        :default-checked-keys="checkedMenuIds"
        class="permission-tree"
      />
      <template #footer>
        <el-button @click="permissionDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSavePermissions" :loading="savingPermissions">
          保存
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref, nextTick } from 'vue'
import { ElMessage, ElMessageBox, FormInstance, FormRules, TreeInstance } from 'element-plus'
import { Search, Refresh, Plus, Edit, Delete, Switch, Key } from '@element-plus/icons-vue'
import { roleApi } from '@/api'
import type { RoleItem, RoleQueryParams, MenuItem } from '@/types'

const loading = ref(false)
const roleList = ref<RoleItem[]>([])
const menuTree = ref<MenuItem[]>([])
const checkedMenuIds = ref<number[]>([])
const currentRole = ref<RoleItem | null>(null)

const searchForm = reactive<{ keyword: string; status?: number }>({
  keyword: '',
  status: undefined
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

// 表单对话框相关
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref<FormInstance>()
const submitting = ref(false)
const formData = reactive({
  id: undefined as number | undefined,
  name: '',
  code: '',
  description: '',
  status: 0
})

const formRules: FormRules = {
  name: [
    { required: true, message: '请输入角色名称', trigger: 'blur' },
    { min: 2, max: 64, message: '角色名称长度为 2-64 个字符', trigger: 'blur' }
  ],
  code: [
    { required: true, message: '请输入角色编码', trigger: 'blur' },
    { min: 2, max: 32, message: '角色编码长度为 2-32 个字符', trigger: 'blur' },
    { pattern: /^[A-Z_]+$/, message: '角色编码只能包含大写字母和下划线', trigger: 'blur' }
  ],
  description: [
    { max: 256, message: '描述长度不能超过 256 个字符', trigger: 'blur' }
  ]
}

// 权限对话框相关
const permissionDialogVisible = ref(false)
const permissionTreeRef = ref<TreeInstance>()
const savingPermissions = ref(false)

const treeProps = {
  children: 'children',
  label: 'menuName'
}

async function fetchList() {
  try {
    loading.value = true
    const params: RoleQueryParams = {
      keyword: searchForm.keyword || undefined,
      status: searchForm.status,
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize
    }
    const page = await roleApi.getRolePage(params)
    roleList.value = page.rows || []
    pagination.total = page.total || 0
  } catch (error) {
    console.error('获取角色列表失败:', error)
  } finally {
    loading.value = false
  }
}

async function fetchMenuTree() {
  try {
    menuTree.value = await roleApi.getMenuTree()
  } catch (error) {
    console.error('获取菜单树失败:', error)
  }
}

function handleSearch() {
  pagination.pageNum = 1
  fetchList()
}

function handleReset() {
  searchForm.keyword = ''
  searchForm.status = undefined
  pagination.pageNum = 1
  fetchList()
}

function handlePageChange(page: number) {
  pagination.pageNum = page
  fetchList()
}

function handleSizeChange(size: number) {
  pagination.pageSize = size
  pagination.pageNum = 1
  fetchList()
}

function handleAdd() {
  dialogTitle.value = '新增角色'
  formData.id = undefined
  formData.name = ''
  formData.code = ''
  formData.description = ''
  formData.status = 0
  dialogVisible.value = true
}

function handleEdit(row: RoleItem) {
  dialogTitle.value = '编辑角色'
  Object.assign(formData, row)
  dialogVisible.value = true
}

async function handleSubmit() {
  if (!formRef.value) return

  try {
    await formRef.value.validate()
    submitting.value = true

    if (formData.id) {
      await roleApi.updateRole(formData.id, formData)
      ElMessage.success('更新成功')
    } else {
      await roleApi.createRole(formData)
      ElMessage.success('创建成功')
    }

    dialogVisible.value = false
    fetchList()
  } catch (error) {
    if (error !== 'cancel' && error !== 'close') {
      ElMessage.error(formData.id ? '更新失败' : '创建失败')
    }
  } finally {
    submitting.value = false
  }
}

function handleDialogClose() {
  if (formRef.value) {
    formRef.value.resetFields()
  }
}

async function handlePermission(row: RoleItem) {
  currentRole.value = row
  permissionDialogVisible.value = true

  try {
    const menuIds = await roleApi.getRoleMenuIds(row.id)
    checkedMenuIds.value = menuIds
  } catch (error) {
    console.error('获取角色菜单权限失败:', error)
  }
}

async function handleSavePermissions() {
  if (!permissionTreeRef.value || !currentRole.value) return

  try {
    savingPermissions.value = true
    const checkedKeys = permissionTreeRef.value.getCheckedKeys()
    const halfCheckedKeys = permissionTreeRef.value.getHalfCheckedKeys()
    const allMenuIds = [...(checkedKeys as number[]), ...(halfCheckedKeys as number[])]

    await roleApi.updateRoleMenus(currentRole.value.id, allMenuIds)
    ElMessage.success('权限保存成功')
    permissionDialogVisible.value = false
  } catch (error) {
    ElMessage.error('权限保存失败')
  } finally {
    savingPermissions.value = false
  }
}

function handlePermissionDialogClose() {
  currentRole.value = null
  checkedMenuIds.value = []
}

async function handleToggleStatus(row: RoleItem) {
  const targetStatus = row.status === 0 ? 1 : 0
  const actionText = targetStatus === 1 ? '禁用' : '启用'
  try {
    await ElMessageBox.confirm(`确定要${actionText}角色 “${row.name}” 吗？`, '提示', {
      type: 'warning'
    })
    await roleApi.updateRoleStatus(row.id, targetStatus)
    row.status = targetStatus
    ElMessage.success(`${actionText}成功`)
  } catch (error) {
    if (error !== 'cancel' && error !== 'close') {
      ElMessage.error(`${actionText}失败`)
    }
  }
}

async function handleDelete(row: RoleItem) {
  try {
    await ElMessageBox.confirm(`确定要删除角色 “${row.name}” 吗？此操作不可恢复！`, '警告', {
      type: 'warning'
    })
    await roleApi.deleteRole(row.id)
    ElMessage.success('删除成功')
    fetchList()
  } catch (error) {
    if (error !== 'cancel' && error !== 'close') {
      ElMessage.error('删除失败')
    }
  }
}

onMounted(() => {
  fetchList()
  fetchMenuTree()
})
</script>

<style scoped>
.role-page {
  display: flex;
  flex-direction: column;
  gap: 16px;
  padding: 16px;
}

.table-card {
  min-height: 400px;
}

.table-header {
  margin-bottom: 16px;
}

.table-pagination {
  display: flex;
  justify-content: flex-end;
  padding-top: 16px;
}

.permission-tree {
  max-height: 400px;
  overflow-y: auto;
}
</style>