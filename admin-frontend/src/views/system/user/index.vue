<template>
  <div class="user-page">
    <el-card shadow="never" class="search-card">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="关键词">
          <el-input
            v-model="searchForm.keyword"
            placeholder="用户名/昵称/邮箱/手机号"
            clearable
            style="width: 220px"
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="全部" clearable style="width: 140px">
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

    <el-card shadow="never" class="table-card">
      <el-table :data="userList" v-loading="loading" border stripe :default-sort="{ prop: 'createTime', order: 'descending' }">
        <el-table-column prop="username" label="用户名" width="160" sortable />
        <el-table-column prop="nickName" label="昵称" width="160" sortable />
        <el-table-column prop="email" label="邮箱" min-width="200" show-overflow-tooltip sortable />
        <el-table-column prop="phone" label="手机号" width="160" sortable />
        <el-table-column prop="status" label="状态" width="100" align="center" sortable>
          <template #default="{ row }">
            <el-tag :type="row.status === 0 ? 'success' : 'danger'" size="small">
              {{ row.status === 0 ? '正常' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" sortable />
        <el-table-column label="角色" width="200" align="center">
          <template #default="{ row }">
            <div class="role-tags">
              <el-tag
                v-for="role in row.roles"
                :key="role.id"
                size="small"
                :type="getRoleTagType(role.code)"
                class="role-tag"
              >
                {{ role.name }}
              </el-tag>
              <span v-if="!row.roles || row.roles.length === 0" class="no-roles">暂无角色</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="240" align="center" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="handleAssignRoles(row)">
              <el-icon><User /></el-icon>
              分配角色
            </el-button>
            <el-button type="primary" link size="small" @click="handleToggleStatus(row)">
              <el-icon><Switch /></el-icon>
              {{ row.status === 0 ? '停用' : '启用' }}
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

    <!-- 角色分配对话框 -->
    <el-dialog
      title="分配角色"
      v-model="roleDialogVisible"
      width="900px"
      top="3vh"
      :close-on-click-modal="false"
      draggable
      @close="handleRoleDialogClose"
    >
      <div class="role-assignment">
        <div class="user-info">
          <el-avatar :src="currentUser?.avatar" :size="40">
            {{ currentUser?.nickName?.charAt(0) || currentUser?.username?.charAt(0) }}
          </el-avatar>
          <div class="user-details">
            <div class="username">{{ currentUser?.username }}</div>
            <div class="nickname">{{ currentUser?.nickName }}</div>
          </div>
        </div>

        <el-divider />

        <div class="role-selection">
          <div class="section-title">选择角色</div>
          <el-checkbox-group v-model="selectedRoleIds" class="role-checkbox-group">
            <el-checkbox
              v-for="role in allRoles"
              :key="role.id"
              :label="role.id"
              :disabled="role.status === 1"
              class="role-checkbox"
            >
              <div class="role-item">
                <div class="role-name">{{ role.name }}</div>
                <div class="role-code">{{ role.code }}</div>
                <div class="role-description">{{ role.description }}</div>
              </div>
            </el-checkbox>
          </el-checkbox-group>
        </div>
      </div>

      <template #footer>
        <el-button @click="roleDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSaveRoles" :loading="savingRoles">
          保存
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Switch, User } from '@element-plus/icons-vue'
import { userApi, roleApi } from '@/api'
import type { AdminUserItem, UserQueryParams, RoleItem } from '@/types'

const loading = ref(false)
const userList = ref<AdminUserItem[]>([])
const allRoles = ref<RoleItem[]>([])

const searchForm = reactive<{ keyword: string; status?: number }>({
  keyword: '',
  status: undefined
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

// 角色分配相关
const roleDialogVisible = ref(false)
const currentUser = ref<AdminUserItem | null>(null)
const selectedRoleIds = ref<number[]>([])
const savingRoles = ref(false)

async function fetchList() {
  try {
    loading.value = true
    const params: UserQueryParams = {
      keyword: searchForm.keyword || undefined,
      status: searchForm.status,
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize
    }
    const page = await userApi.getUserPage(params)
    userList.value = page.rows || []
    pagination.total = page.total || 0
  } catch (error) {
    console.error('获取用户列表失败，使用模拟数据:', error)
    // 临时使用模拟数据，等后端API实现后再删除
    const mockRoles = [
      { id: 1, name: '超级管理员', code: 'SUPER_ADMIN', description: '拥有系统所有权限', status: 0 },
      { id: 2, name: '编辑', code: 'EDITOR', description: '可以管理文章、分类、标签、评论', status: 0 }
    ]
    userList.value = [
      {
        id: 1,
        username: 'admin',
        nickName: '超级管理员',
        email: 'admin@birdblog.local',
        phone: '18800000000',
        status: 0,
        sex: 0,
        avatar: 'https://youke1.picui.cn/s1/2025/10/31/690455356db0f.jpg',
        createTime: '2025-10-24 23:15:53',
        updateTime: '2025-10-31 14:21:40',
        roles: [mockRoles[0]]
      },
      {
        id: 2,
        username: 'author1',
        nickName: '小鹤',
        email: 'author1@birdblog.local',
        phone: '18800000001',
        status: 0,
        sex: 0,
        avatar: 'https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif',
        createTime: '2025-10-24 23:15:53',
        updateTime: '2025-10-31 13:05:04',
        roles: [mockRoles[1]]
      }
    ]
    pagination.total = 2
  } finally {
    loading.value = false
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

async function handleToggleStatus(row: AdminUserItem) {
  const targetStatus = row.status === 0 ? 1 : 0
  const actionText = targetStatus === 1 ? '停用' : '启用'
  try {
    await ElMessageBox.confirm(`确定要${actionText}用户 "${row.username}" 吗？`, '提示', {
      type: 'warning'
    })
    await userApi.updateUserStatus(row.id, targetStatus)
    row.status = targetStatus
    ElMessage.success(`${actionText}成功`)
  } catch (error) {
    if (error !== 'cancel' && error !== 'close') {
      ElMessage.error(`${actionText}失败`)
    }
  }
}

async function fetchRoles() {
  try {
    const roles = await roleApi.getRoleList()
    allRoles.value = roles || []
    console.log('获取到的角色列表:', allRoles.value) // 添加调试日志
  } catch (error) {
    console.error('获取角色列表失败:', error)
  }
}

function getRoleTagType(code: string) {
  switch (code) {
    case 'SUPER_ADMIN':
      return 'danger'
    case 'EDITOR':
      return 'warning'
    case 'REVIEWER':
      return 'success'
    default:
      return 'info'
  }
}

async function handleAssignRoles(row: AdminUserItem) {
  currentUser.value = row
  selectedRoleIds.value = row.roles?.map(role => role.id) || []
  roleDialogVisible.value = true
}

async function handleSaveRoles() {
  if (!currentUser.value) return

  try {
    savingRoles.value = true
    await userApi.updateUserRoles(currentUser.value.id, selectedRoleIds.value)

    // 更新用户角色信息
    const updatedUser = userList.value.find(user => user.id === currentUser.value?.id)
    if (updatedUser) {
      updatedUser.roles = allRoles.value.filter(role => selectedRoleIds.value.includes(role.id))
    }

    ElMessage.success('角色分配成功')
    roleDialogVisible.value = false
  } catch (error) {
    ElMessage.error('角色分配失败')
  } finally {
    savingRoles.value = false
  }
}

function handleRoleDialogClose() {
  currentUser.value = null
  selectedRoleIds.value = []
}

onMounted(() => {
  fetchList()
  fetchRoles()
})
</script>

<style scoped>
.user-page {
  display: flex;
  flex-direction: column;
  gap: 16px;
  padding: 16px;
}

.table-card {
  min-height: 400px;
}

.table-pagination {
  display: flex;
  justify-content: flex-end;
  padding-top: 16px;
}

.role-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
}

.role-tag {
  margin: 0;
}

.no-roles {
  color: var(--el-text-color-secondary);
  font-size: 12px;
}

.role-assignment {
  padding: 20px 0;
  min-height: 500px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-details {
  flex: 1;
}

.username {
  font-weight: 600;
  font-size: 16px;
  color: var(--el-text-color-primary);
}

.nickname {
  font-size: 14px;
  color: var(--el-text-color-regular);
  margin-top: 2px;
}

.section-title {
  font-weight: 600;
  font-size: 16px;
  margin-bottom: 16px;
  color: var(--el-text-color-primary);
  display: flex;
  align-items: center;
  gap: 8px;
}

.section-title::before {
  content: '';
  width: 4px;
  height: 16px;
  background-color: var(--el-color-primary);
  border-radius: 2px;
}

.role-checkbox-group {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
  max-height: 70vh;
  overflow-y: auto;
  padding: 12px;
  margin-top: 12px;
}

/* 当角色较少时使用单列 */
.role-checkbox-group:has(:nth-child(3):last-child) {
  grid-template-columns: 1fr;
}

/* 响应式设计：当屏幕较小时使用单列 */
@media (max-width: 1200px) {
  .role-checkbox-group {
    grid-template-columns: 1fr;
    gap: 14px;
    max-height: 65vh;
  }

  .role-item {
    min-height: 70px;
    padding: 16px;
    gap: 12px;
  }
}

@media (max-width: 768px) {
  .role-checkbox-group {
    gap: 12px;
    max-height: 60vh;
  }

  .role-item {
    min-height: 60px;
    padding: 12px;
    gap: 10px;
    flex-direction: column;
    align-items: flex-start;
  }

  .role-name {
    font-size: 15px;
    min-width: auto;
  }

  .role-code {
    font-size: 12px;
  }

  .role-description {
    font-size: 12px;
  }
}

.role-checkbox {
  width: 100%;
  margin-right: 0;
}

.role-checkbox :deep(.el-checkbox__label) {
  width: 100%;
  padding-left: 8px;
}

.role-item {
  width: 100%;
  padding: 20px;
  background-color: var(--el-bg-color);
  min-height: 80px;
  display: flex;
  align-items: center;
  gap: 16px;
}

.role-name {
  font-weight: 600;
  font-size: 16px;
  color: var(--el-text-color-primary);
  flex: 0 0 auto;
  min-width: 120px;
}

.role-code {
  font-size: 13px;
  color: var(--el-color-primary);
  font-family: 'Courier New', monospace;
  background-color: var(--el-color-primary-light-9);
  padding: 4px 8px;
  border-radius: 4px;
  flex: 0 0 auto;
}

.role-description {
  font-size: 13px;
  color: var(--el-text-color-secondary);
  line-height: 1.4;
  flex: 1;
  min-width: 0;
}
</style>
