<template>
  <div class="article-container">
    <div class="header">
      <h2>文章管理</h2>
      <el-button type="primary" :icon="Plus" @click="handleAdd">新增文章</el-button>
    </div>

    <!-- 搜索表单 -->
    <el-card class="search-card" shadow="never">
      <el-form :inline="true" :model="queryParams" @submit.prevent="handleQuery">
        <el-form-item label="关键词">
          <el-input
            v-model="queryParams.keyword"
            placeholder="搜索标题或内容"
            clearable
            style="width: 200px"
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="分类">
          <el-select
            v-model="queryParams.categoryId"
            placeholder="请选择分类"
            clearable
            style="width: 150px"
          >
            <el-option
              v-for="category in categoryOptions"
              :key="category.id"
              :label="category.name"
              :value="category.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select
            v-model="queryParams.status"
            placeholder="请选择状态"
            clearable
            style="width: 120px"
          >
            <el-option label="已发布" :value="0" />
            <el-option label="草稿" :value="1" />
          </el-select>
        </el-form-item>
        <el-form-item label="是否置顶">
          <el-select
            v-model="queryParams.isTop"
            placeholder="请选择"
            clearable
            style="width: 100px"
          >
            <el-option label="是" :value="true" />
            <el-option label="否" :value="false" />
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
        :data="articleList"
        border
        style="width: 100%"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column prop="id" label="ID" width="80" align="center" />
        <el-table-column prop="title" label="标题" min-width="200" show-overflow-tooltip />
        <el-table-column prop="categoryName" label="分类" width="120" align="center">
          <template #default="{ row }">
            {{ row.categoryName || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="authorName" label="作者" width="100" align="center">
          <template #default="{ row }">
            {{ row.authorName || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 0 ? 'success' : 'info'">
              {{ row.status === 0 ? '已发布' : '草稿' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="isTop" label="置顶" width="80" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.isTop" type="warning" size="small">置顶</el-tag>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="viewCount" label="浏览量" width="100" align="center" />
        <el-table-column prop="likeCount" label="点赞量" width="100" align="center" />
        <el-table-column prop="commentCount" label="评论数" width="100" align="center" />
        <el-table-column prop="publishedTime" label="发布时间" width="180" align="center">
          <template #default="{ row }">
            {{ formatDate(row.publishedTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="250" align="center" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" :icon="View" @click="handleView(row)">
              查看
            </el-button>
            <el-button link type="primary" :icon="Edit" @click="handleEdit(row)">
              编辑
            </el-button>
            <el-button
              link
              :type="row.status === 1 ? 'success' : 'warning'"
              :icon="row.status === 1 ? 'Check' : 'DocumentCopy'"
              @click="handleTogglePublish(row)"
            >
              {{ row.status === 1 ? '发布' : '草稿' }}
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

    <!-- 文章查看弹窗 -->
    <ArticleView
      v-model="viewDialogVisible"
      :article-id="viewArticleId"
      @edit="handleEditFromView"
    />

    <!-- 文章编辑弹窗 -->
    <ArticleEdit
      v-model="editDialogVisible"
      :article="editArticle"
      @success="handleEditSuccess"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus, Edit, Delete, View, Check, DocumentCopy } from '@element-plus/icons-vue'
import { getArticlePage, deleteArticle, toggleArticleTop, Article, ArticleQueryParams } from '@/api/article'
import { getCategoryPage } from '@/api/category'
import type { Category } from '@/types'
import { useUserStore } from '@/stores/user'
import ArticleView from './ArticleView.vue'
import ArticleEdit from './ArticleEdit.vue'

// 响应式数据
const loading = ref(false)
const articleList = ref<Article[]>([])
const total = ref(0)
const selectedIds = ref<number[]>([])
const categoryOptions = ref<Category[]>([])

// 查看弹窗相关
const viewDialogVisible = ref(false)
const viewArticleId = ref<number | undefined>()

// 编辑弹窗相关
const editDialogVisible = ref(false)
const editArticle = ref<Article | null>(null)

// 查询参数
const queryParams = reactive<ArticleQueryParams>({
  pageNum: 1,
  pageSize: 10,
  keyword: '',
  categoryId: undefined,
  status: undefined,
  isTop: undefined
})

/**
 * 获取文章列表
 */
const getList = async () => {
  loading.value = true
  try {
    const result = await getArticlePage(queryParams)
    articleList.value = result.rows
    total.value = result.total
  } catch (error) {
    console.error('获取文章列表失败:', error)
    ElMessage.error('获取文章列表失败')
  } finally {
    loading.value = false
  }
}

/**
 * 获取分类选项
 */
const getCategoryOptions = async () => {
  try {
    const result = await getCategoryPage({ pageNum: 1, pageSize: 1000 })
    categoryOptions.value = result.rows
  } catch (error) {
    console.error('获取分类选项失败:', error)
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
 * 处理每页条数变化
 */
const handleSizeChange = (size: number) => {
  queryParams.pageSize = size
  queryParams.pageNum = 1  // 切换每页条数时回到第一页
  getList()
}

/**
 * 处理页码变化
 */
const handleCurrentChange = (page: number) => {
  queryParams.pageNum = page
  getList()
}

/**
 * 重置
 */
const handleReset = () => {
  queryParams.pageNum = 1
  queryParams.pageSize = 10
  queryParams.keyword = ''
  queryParams.categoryId = undefined
  queryParams.status = undefined
  queryParams.isTop = undefined
  getList()
}

/**
 * 新增文章
 */
const handleAdd = () => {
  editArticle.value = null // 设置为null表示新增模式
  editDialogVisible.value = true
}

/**
 * 查看文章
 */
const handleView = (row: Article) => {
  viewArticleId.value = row.id
  viewDialogVisible.value = true
}

/**
 * 编辑文章
 */
const handleEdit = (row: Article) => {
  editArticle.value = row
  editDialogVisible.value = true
}

/**
 * 从查看弹窗编辑文章
 */
const handleEditFromView = (article: Article) => {
  handleEdit(article)
}

/**
 * 编辑成功回调
 */
const handleEditSuccess = () => {
  getList() // 重新获取文章列表
}

/**
 * 切换发布状态
 */
const handleTogglePublish = async (row: Article) => {
  const action = row.status === 1 ? '发布' : '设为草稿'
  try {
    await ElMessageBox.confirm(
      `确定要${action}文章"${row.title}"吗？`,
      `${action}确认`,
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    ElMessage.info(`${action}文章功能开发中...`)
    // TODO: 实现发布/草稿功能
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error(`${action}失败:`, error)
      ElMessage.error(error.message || `${action}失败`)
    }
  }
}

/**
 * 删除文章
 */
const handleDelete = async (row: Article) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除文章"${row.title}"吗？删除后无法恢复！`,
      '删除确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    await deleteArticle(String(row.id))
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
const handleSelectionChange = (selection: Article[]) => {
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
  getCategoryOptions()
})
</script>

<style scoped>
.article-container {
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

:deep(.el-table .cell) {
  white-space: nowrap;
}
</style>
