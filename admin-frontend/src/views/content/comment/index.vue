<template>
  <div class="comment-container">
    <div class="header">
      <h2>评论管理</h2>
      <div>
        <el-button
          type="danger"
          :icon="Delete"
          :disabled="selectedIds.length === 0"
          @click="handleBatchDelete"
        >
          批量删除
        </el-button>
      </div>
    </div>

    <!-- 搜索表单 -->
    <el-card class="search-card" shadow="never">
      <el-form :inline="true" :model="queryParams" @submit.prevent="handleQuery">
        <el-form-item label="评论类型">
          <el-select
            v-model="queryParams.type"
            placeholder="请选择评论类型"
            clearable
            style="width: 120px"
          >
            <el-option label="文章评论" :value="0" />
            <el-option label="友链评论" :value="1" />
          </el-select>
        </el-form-item>
        <el-form-item label="关联对象">
          <el-input
            v-model="queryParams.objectKeyword"
            placeholder="请输入文章标题或友链名称"
            clearable
            style="width: 200px"
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-select
            v-model="queryParams.status"
            placeholder="请选择状态"
            clearable
            style="width: 120px"
          >
            <el-option label="正常" :value="0" />
            <el-option label="屏蔽" :value="1" />
          </el-select>
        </el-form-item>
        <el-form-item label="评论内容">
          <el-input
            v-model="queryParams.content"
            placeholder="请输入评论内容"
            clearable
            style="width: 200px"
            @keyup.enter="handleQuery"
          />
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
        :data="commentList"
        border
        style="width: 100%"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column prop="id" label="ID" width="80" align="center" />
        <el-table-column prop="type" label="类型" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.type === 0 ? 'primary' : 'success'">
              {{ row.type === 0 ? '文章评论' : '友链评论' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="关联对象" width="200" align="center">
          <template #default="{ row }">
            <span v-if="row.type === 0 && row.articleTitle">{{ row.articleTitle }}</span>
            <span v-else-if="row.type === 1 && row.linkName">{{ row.linkName }}</span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="content" label="评论内容" min-width="300" show-overflow-tooltip />
        <el-table-column prop="fromUserName" label="评论用户" width="120" align="center">
          <template #default="{ row }">
            <span v-if="row.fromUserName">{{ row.fromUserName }}</span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="toUserName" label="回复用户" width="120" align="center">
          <template #default="{ row }">
            <span v-if="row.toUserName">{{ row.toUserName }}</span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="likeCount" label="点赞数" width="80" align="center">
          <template #default="{ row }">
            <span v-if="row.likeCount">{{ row.likeCount }}</span>
            <span v-else>0</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 0 ? 'success' : 'danger'">
              {{ row.status === 0 ? '正常' : '屏蔽' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" align="center">
          <template #default="{ row }">
            {{ formatDate(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" align="center" fixed="right">
          <template #default="{ row }">
            <el-button
              link
              :type="row.status === 0 ? 'warning' : 'success'"
              @click="handleToggleStatus(row)"
            >
              {{ row.status === 0 ? '屏蔽' : '恢复' }}
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
import { Search, Refresh, Delete } from '@element-plus/icons-vue'
import { getCommentPage, updateCommentStatus, deleteComments } from '@/api/comment'
import type { Comment, CommentQueryParams } from '@/types'
import { useUserStore } from '@/stores/user'

// 响应式数据
const loading = ref(false)
const commentList = ref<Comment[]>([])
const total = ref(0)
const selectedIds = ref<number[]>([])

// 查询参数
const queryParams = reactive<CommentQueryParams>({
  pageNum: 1,
  pageSize: 10,
  type: undefined,
  articleId: undefined,
  status: undefined,
  content: '',
  objectKeyword: ''
})

/**
 * 获取评论列表
 */
const getList = async () => {
  loading.value = true
  try {
    // 调试信息：检查 Token 状态
    const userStore = useUserStore()
    console.log('=== 评论分页查询调试信息 ===')
    console.log('当前 Token 状态:', {
      isLoggedIn: userStore.isLoggedIn,
      token: userStore.token ? 'Token 存在' : 'Token 不存在',
      tokenLength: userStore.token?.length || 0
    })
    console.log('查询参数:', queryParams)

    const result = await getCommentPage(queryParams)
    console.log('API返回结果:', result)

    commentList.value = result.rows
    total.value = result.total

    console.log('处理后数据:', {
      评论列表长度: commentList.value.length,
      总数: total.value
    })
  } catch (error) {
    console.error('获取评论列表失败:', error)
    ElMessage.error('获取评论列表失败')
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
  queryParams.type = undefined
  queryParams.articleId = undefined
  queryParams.status = undefined
  queryParams.content = ''
  queryParams.objectKeyword = ''
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
 * 切换评论状态
 */
const handleToggleStatus = async (row: Comment) => {
  const newStatus = row.status === 0 ? 1 : 0
  const action = newStatus === 0 ? '恢复' : '屏蔽'

  try {
    await ElMessageBox.confirm(
      `确定要${action}这条评论吗？`,
      `${action}确认`,
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    await updateCommentStatus(row.id, newStatus)
    ElMessage.success(`${action}成功`)
    getList()
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error(`${action}失败:`, error)
      ElMessage.error(error.message || `${action}失败`)
    }
  }
}

/**
 * 删除单条评论
 */
const handleDelete = async (row: Comment) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除这条评论吗？`,
      '删除确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    await deleteComments(String(row.id))
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
 * 批量删除评论
 */
const handleBatchDelete = async () => {
  try {
    await ElMessageBox.confirm(
      `确定要删除选中的 ${selectedIds.value.length} 条评论吗？`,
      '批量删除确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    const ids = selectedIds.value.join(',')
    await deleteComments(ids)
    ElMessage.success('批量删除成功')
    selectedIds.value = []
    getList()
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('批量删除失败:', error)
      ElMessage.error(error.message || '批量删除失败')
    }
  }
}

/**
 * 选择变化
 */
const handleSelectionChange = (selection: Comment[]) => {
  selectedIds.value = selection.map(item => item.id)
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
.comment-container {
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
