<template>
  <div class="link-container">
    <div class="header">
      <h2>友链管理</h2>
      <el-button type="primary" :icon="Plus" @click="handleAdd">新增友链</el-button>
    </div>

    <!-- 搜索表单 -->
    <el-card class="search-card" shadow="never">
      <el-form :inline="true" :model="queryParams" @submit.prevent="handleQuery">
        <el-form-item label="网站名称">
          <el-input
            v-model="queryParams.name"
            placeholder="请输入网站名称"
            clearable
            style="width: 200px"
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="审核状态">
          <el-select
            v-model="queryParams.status"
            placeholder="请选择审核状态"
            clearable
            style="width: 120px"
          >
            <el-option label="审核通过" :value="0" />
            <el-option label="审核不通过" :value="1" />
            <el-option label="待审核" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleQuery">搜索</el-button>
          <el-button :icon="Refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 数据表格 -->
    <el-card class="table-card" shadow="never">
      <el-table
        v-loading="loading"
        :data="linkList"
        border
        style="width: 100%"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column prop="id" label="ID" width="80" align="center" />
        <el-table-column prop="name" label="网站名称" min-width="150" />
        <el-table-column prop="logo" label="Logo" width="100" align="center">
          <template #default="{ row }">
            <el-image
              v-if="row.logo"
              :src="row.logo"
              :preview-src-list="[row.logo]"
              fit="cover"
              style="width: 40px; height: 40px; border-radius: 4px;"
            />
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="url" label="网站地址" min-width="200">
          <template #default="{ row }">
            <el-link :href="row.url" target="_blank" type="primary">
              {{ row.url }}
            </el-link>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip />
        <el-table-column prop="status" label="审核状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="creator" label="创建者" width="120" align="center" />
        <el-table-column prop="createTime" label="创建时间" width="180" align="center">
          <template #default="{ row }">
            {{ formatDate(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" align="center" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" :icon="Edit" @click="handleEdit(row)">
              编辑
            </el-button>
            <el-button link type="danger" :icon="Delete" @click="handleDelete(row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          :current-page="queryParams.pageNum"
          :page-size="queryParams.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus, Edit, Delete } from '@element-plus/icons-vue'
import { getLinkPage, deleteLink } from '@/api/link'
import type { Link, LinkQueryParams } from '@/types'
import { useUserStore } from '@/stores/user'

// 响应式数据
const loading = ref(false)
const linkList = ref<Link[]>([])
const total = ref(0)
const selectedIds = ref<number[]>([])

// 查询参数
const queryParams = reactive<LinkQueryParams>({
  pageNum: 1,
  pageSize: 10,
  name: '',
  status: undefined
})

/**
 * 获取友链列表
 */
const getList = async () => {
  loading.value = true
  try {
    // 调试信息：检查 Token 状态
    const userStore = useUserStore()
    console.log('=== 友链分页查询调试信息 ===')
    console.log('当前 Token 状态:', {
      isLoggedIn: userStore.isLoggedIn,
      token: userStore.token ? 'Token 存在' : 'Token 不存在',
      tokenLength: userStore.token?.length || 0
    })
    console.log('查询参数:', queryParams)

    const result = await getLinkPage(queryParams)
    console.log('API返回结果:', result)

    linkList.value = result.rows
    total.value = result.total

    console.log('处理后数据:', {
      友链列表长度: linkList.value.length,
      总数: total.value
    })
  } catch (error) {
    console.error('获取友链列表失败:', error)
    ElMessage.error('获取友链列表失败')
  } finally {
    loading.value = false
  }
}

/**
 * 搜索
 */
const handleQuery = () => {
  queryParams.pageNum = 1
  getList()
}

/**
 * 重置
 */
const handleReset = () => {
  queryParams.pageNum = 1
  queryParams.pageSize = 10
  queryParams.name = ''
  queryParams.status = undefined
  getList()
}

/**
 * 处理每页显示数量变化
 */
const handleSizeChange = (val: number) => {
  queryParams.pageSize = val
  queryParams.pageNum = 1
  getList()
}

/**
 * 处理当前页码变化
 */
const handleCurrentChange = (val: number) => {
  queryParams.pageNum = val
  getList()
}

/**
 * 新增
 */
const handleAdd = () => {
  ElMessage.info('新增友链功能开发中...')
}

/**
 * 编辑
 */
const handleEdit = (row: Link) => {
  ElMessage.info(`编辑友链功能开发中... ID: ${row.id}`)
}

/**
 * 删除
 */
const handleDelete = async (row: Link) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除友链"${row.name}"吗？`,
      '删除确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    await deleteLink(String(row.id))
    ElMessage.success('删除成功')
    getList()
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
      ElMessage.error(error.message || '删除失败')
    }
  }
}

/**
 * 选择变化
 */
const handleSelectionChange = (selection: Link[]) => {
  selectedIds.value = selection.map(item => item.id)
}

/**
 * 获取状态类型
 */
const getStatusType = (status: number) => {
  switch (status) {
    case 0:
      return 'success'  // 审核通过
    case 1:
      return 'danger'   // 审核不通过
    case 2:
      return 'warning'  // 待审核
    default:
      return 'info'
  }
}

/**
 * 获取状态文本
 */
const getStatusText = (status: number) => {
  switch (status) {
    case 0:
      return '审核通过'
    case 1:
      return '审核不通过'
    case 2:
      return '待审核'
    default:
      return '未知'
  }
}

/**
 * 格式化日期
 */
const formatDate = (date: string | undefined) => {
  if (!date) return '-'
  return new Date(date).toLocaleString('zh-CN')
}

// 页面加载时获取数据
onMounted(() => {
  getList()
})
</script>

<style scoped>
.link-container {
  padding: 20px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.header h2 {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
}

.search-card {
  margin-bottom: 20px;
}

.table-card {
  margin-bottom: 20px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

:deep(.el-card__body) {
  padding: 20px;
}
</style>
