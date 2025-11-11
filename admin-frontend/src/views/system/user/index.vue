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
        <el-form-item label="类型">
          <el-select v-model="searchForm.type" placeholder="全部" clearable style="width: 140px">
            <el-option label="访客" :value="0" />
            <el-option label="作者" :value="1" />
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
      <el-table :data="userList" v-loading="loading" border stripe>
        <el-table-column prop="username" label="用户名" width="160" />
        <el-table-column prop="nickName" label="昵称" width="160" />
        <el-table-column prop="email" label="邮箱" min-width="200" show-overflow-tooltip />
        <el-table-column prop="phone" label="手机号" width="160" />
        <el-table-column prop="type" label="类型" width="100" align="center">
          <template #default="{ row }">
            <el-tag size="small" :type="row.type === 1 ? 'warning' : 'info'">
              {{ row.type === 1 ? '作者' : '访客' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 0 ? 'success' : 'danger'" size="small">
              {{ row.status === 0 ? '正常' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="160" align="center">
          <template #default="{ row }">
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
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Switch } from '@element-plus/icons-vue'
import { userApi } from '@/api'
import type { AdminUserItem, UserQueryParams } from '@/types'

const loading = ref(false)
const userList = ref<AdminUserItem[]>([])

const searchForm = reactive<{ keyword: string; status?: number; type?: number }>({
  keyword: '',
  status: undefined,
  type: undefined
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

async function fetchList() {
  try {
    loading.value = true
    const params: UserQueryParams = {
      keyword: searchForm.keyword || undefined,
      status: searchForm.status,
      type: searchForm.type,
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize
    }
    const page = await userApi.getUserPage(params)
    userList.value = page.rows || []
    pagination.total = page.total || 0
  } catch (error) {
    console.error('获取用户列表失败:', error)
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
  searchForm.type = undefined
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
    await ElMessageBox.confirm(`确定要${actionText}用户 “${row.username}” 吗？`, '提示', {
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

onMounted(() => {
  fetchList()
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
</style>
