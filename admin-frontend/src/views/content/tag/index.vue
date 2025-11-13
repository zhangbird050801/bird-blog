<template>
  <div class="tag-container">
    <div class="header">
      <h2>标签管理</h2>
      <div class="header-actions">
        <el-button
          type="danger"
          :icon="Delete"
          @click="handleBatchDelete"
          :disabled="selectedIds.length === 0"
        >
          批量删除
        </el-button>
        <el-button type="primary" :icon="Plus" @click="handleAdd">新增标签</el-button>
      </div>
    </div>

    <!-- 搜索表单 -->
    <el-card class="search-card" shadow="never">
      <el-form :inline="true" :model="queryParams" @submit.prevent="handleQuery">
        <el-form-item label="标签名称">
          <el-input
            v-model="queryParams.name"
            placeholder="请输入标签名称"
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
        :data="tagList"
        border
        style="width: 100%"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column prop="id" label="ID" width="80" align="center" />
        <el-table-column prop="name" label="标签名称" min-width="150" />
        <el-table-column prop="remark" label="备注" min-width="200" show-overflow-tooltip />
        <el-table-column prop="creator" label="创建者" width="120" align="center" />
        <el-table-column prop="createTime" label="创建时间" width="180" align="center">
          <template #default="{ row }">
            {{ formatDate(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="updateTime" label="更新时间" width="180" align="center">
          <template #default="{ row }">
            {{ formatDate(row.updateTime) }}
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

    <!-- 新增标签对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="80px"
        @submit.prevent="handleSubmit"
      >
        <el-form-item label="标签名称" prop="name">
          <el-input
            v-model="form.name"
            placeholder="请输入标签名称"
            clearable
            maxlength="50"
            show-word-limit
          />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input
            v-model="form.remark"
            type="textarea"
            :rows="3"
            placeholder="请输入备注（可选）"
            maxlength="200"
            show-word-limit
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
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus, Edit, Delete } from '@element-plus/icons-vue'
import { getTagPage, deleteTag, createTag, updateTag } from '@/api/tag'
import type { Tag, TagQueryParams } from '@/types'
import { useUserStore } from '@/stores/user'

// 响应式数据
const loading = ref(false)
const tagList = ref<Tag[]>([])
const total = ref(0)
const selectedIds = ref<number[]>([])

// 查询参数
const queryParams = reactive<TagQueryParams>({
  pageNum: 1,
  pageSize: 10,
  name: ''
})

// 新增标签对话框
const dialogVisible = ref(false)
const dialogTitle = ref('新增标签')
const submitLoading = ref(false)
const formRef = ref()
const isEdit = ref(false)
const editingId = ref<number | null>(null)

// 表单数据
const form = reactive<Partial<Tag>>({
  name: '',
  remark: ''
})

// 表单验证规则
const rules = {
  name: [
    { required: true, message: '请输入标签名称', trigger: 'blur' },
    { min: 1, max: 50, message: '标签名称长度为 1 到 50 个字符', trigger: 'blur' }
  ],
  remark: [
    { max: 200, message: '备注长度不能超过 200 个字符', trigger: 'blur' }
  ]
}

/**
 * 获取标签列表
 */
const getList = async () => {
  loading.value = true
  try {
    // 调试信息：检查 Token 状态
    const userStore = useUserStore()
    console.log('=== 标签分页查询调试信息 ===')
    console.log('当前 Token 状态:', {
      isLoggedIn: userStore.isLoggedIn,
      token: userStore.token ? 'Token 存在' : 'Token 不存在',
      tokenLength: userStore.token?.length || 0
    })
    console.log('查询参数:', queryParams)

    const result = await getTagPage(queryParams)
    console.log('API返回结果:', result)

    tagList.value = result.rows
    total.value = result.total

    console.log('处理后数据:', {
      标签列表长度: tagList.value.length,
      总数: total.value
    })
  } catch (error) {
    console.error('获取标签列表失败:', error)
    ElMessage.error('获取标签列表失败')
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
 * 重置
 */
const handleReset = () => {
  queryParams.pageNum = 1
  queryParams.pageSize = 10
  queryParams.name = ''
  getList()
}

/**
 * 新增
 */
const handleAdd = () => {
  dialogTitle.value = '新增标签'
  isEdit.value = false
  editingId.value = null
  // 重置表单
  form.name = ''
  form.remark = ''
  dialogVisible.value = true
}

/**
 * 提交表单
 */
const handleSubmit = async () => {
  if (!formRef.value) return

  try {
    await formRef.value.validate()
    submitLoading.value = true

    if (isEdit.value && editingId.value) {
      // 编辑模式
      await updateTag(editingId.value, form)
      ElMessage.success('更新标签成功')
    } else {
      // 新增模式
      await createTag(form)
      ElMessage.success('新增标签成功')
    }

    dialogVisible.value = false
    // 刷新列表
    await getList()
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error(isEdit.value ? '更新标签失败:' : '新增标签失败:', error)
      ElMessage.error(error.message || (isEdit.value ? '更新标签失败' : '新增标签失败'))
    }
  } finally {
    submitLoading.value = false
  }
}

/**
 * 编辑
 */
const handleEdit = (row: Tag) => {
  dialogTitle.value = '编辑标签'
  isEdit.value = true
  editingId.value = row.id

  // 填充表单数据
  form.name = row.name
  form.remark = row.remark || ''

  dialogVisible.value = true
}

/**
 * 删除
 */
const handleDelete = async (row: Tag) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除标签"${row.name}"吗？`,
      '删除确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    await deleteTag(String(row.id))
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
const handleSelectionChange = (selection: Tag[]) => {
  selectedIds.value = selection.map(item => item.id)
}

/**
 * 批量删除
 */
const handleBatchDelete = async () => {
  if (selectedIds.value.length === 0) {
    ElMessage.warning('请选择要删除的标签')
    return
  }

  try {
    await ElMessageBox.confirm(
      `确定要删除选中的 ${selectedIds.value.length} 个标签吗？`,
      '批量删除确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    const ids = selectedIds.value.join(',')
    const result = await deleteTag(ids)
    ElMessage.success(result || '批量删除成功')

    // 清空选择
    selectedIds.value = []
    // 刷新列表
    getList()
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('批量删除失败:', error)
      ElMessage.error(error.message || '批量删除失败')
    }
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
.tag-container {
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

.header-actions {
  display: flex;
  gap: 10px;
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
