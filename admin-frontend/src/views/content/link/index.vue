<template>
  <div class="link-container">
    <div class="header">
      <h2>友链管理</h2>
      <div class="header-actions">
        <el-button
          type="danger"
          :icon="Delete"
          @click="handleBatchDelete"
          :disabled="selectedIds.length === 0"
        >
          批量删除
        </el-button>
        <el-button type="primary" :icon="Plus" @click="handleAdd">新增友链</el-button>
      </div>
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
        :default-sort="{ prop: 'createTime', order: 'descending' }"
      >
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column prop="id" label="ID" width="80" align="center" sortable />
        <el-table-column prop="name" label="网站名称" min-width="150" sortable />
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
        <el-table-column prop="url" label="网站地址" min-width="200" sortable>
          <template #default="{ row }">
            <el-link :href="row.url" target="_blank" type="primary">
              {{ row.url }}
            </el-link>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip />
        <el-table-column prop="status" label="审核状态" width="100" align="center" sortable>
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="creator" label="创建者" width="120" align="center" sortable />
        <el-table-column prop="createTime" label="创建时间" width="180" align="center" sortable>
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

    <!-- 新增友链对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      :close-on-click-modal="false"
      @close="handleDialogClose"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="80px"
        @submit.prevent="handleSubmit"
      >
        <el-form-item label="网站名称" prop="name">
          <el-input
            v-model="form.name"
            placeholder="请输入网站名称"
            clearable
            maxlength="50"
            show-word-limit
          />
        </el-form-item>
        <el-form-item label="网站地址" prop="url">
          <el-input
            v-model="form.url"
            placeholder="请输入网站地址，以 http:// 或 https:// 开头"
            clearable
          />
        </el-form-item>
        <el-form-item label="网站Logo" prop="logo">
          <el-input
            v-model="form.logo"
            placeholder="请输入网站Logo地址（可选）"
            clearable
          />
          <div class="form-help-text">支持 http:// 或 https:// 开头的图片链接</div>
        </el-form-item>
        <el-form-item label="网站描述" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="4"
            placeholder="请输入网站描述"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
        <el-form-item label="审核状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :label="0">审核通过</el-radio>
            <el-radio :label="1">审核不通过</el-radio>
            <el-radio :label="2">待审核</el-radio>
          </el-radio-group>
          <div class="form-help-text">管理员添加的友链建议直接设为审核通过</div>
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
import { getLinkPage, deleteLink, createLink, updateLink } from '@/api/link'
import type { Link, LinkQueryParams } from '@/types'
import { useUserStore } from '@/stores/user'

// 响应式数据
const loading = ref(false)
const linkList = ref<Link[]>([])
const total = ref(0)
const selectedIds = ref<number[]>([])

// 新增友链对话框
const dialogVisible = ref(false)
const dialogTitle = ref('新增友链')
const submitLoading = ref(false)
const formRef = ref()
const isEdit = ref(false)
const editingId = ref<number | null>(null)

// 查询参数
const queryParams = reactive<LinkQueryParams>({
  pageNum: 1,
  pageSize: 10,
  name: '',
  status: undefined
})

// 表单数据
const form = reactive<Partial<Link>>({
  name: '',
  url: '',
  logo: '',
  description: '',
  status: 0 // 默认审核通过
})

// 表单验证规则
const rules = {
  name: [
    { required: true, message: '请输入网站名称', trigger: 'blur' },
    { min: 1, max: 50, message: '网站名称长度为 1 到 50 个字符', trigger: 'blur' }
  ],
  url: [
    { required: true, message: '请输入网站地址', trigger: 'blur' },
    {
      pattern: /^https?:\/\/.+/i,
      message: '网站地址必须以 http:// 或 https:// 开头',
      trigger: 'blur'
    }
  ],
  description: [
    { required: true, message: '请输入网站描述', trigger: 'blur' },
    { min: 1, max: 500, message: '网站描述长度为 1 到 500 个字符', trigger: 'blur' }
  ],
  logo: [
    {
      pattern: /^(https?:\/\/.+|)$/,
      message: 'Logo地址必须以 http:// 或 https:// 开头',
      trigger: 'blur'
    }
  ],
  status: [
    { required: true, message: '请选择审核状态', trigger: 'change' }
  ]
}

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
  dialogTitle.value = '新增友链'
  isEdit.value = false
  editingId.value = null
  // 重置表单
  resetForm()
  dialogVisible.value = true
}

/**
 * 重置表单
 */
const resetForm = () => {
  form.name = ''
  form.url = ''
  form.logo = ''
  form.description = ''
  form.status = 0
  isEdit.value = false
  editingId.value = null
  if (formRef.value) {
    formRef.value.resetFields()
  }
}

/**
 * 提交表单
 */
const handleSubmit = async () => {
  if (!formRef.value) return

  try {
    // 表单验证
    await formRef.value.validate()
    submitLoading.value = true

    let result: string
    if (isEdit.value && editingId.value) {
      // 编辑模式
      result = await updateLink(editingId.value, form)
      ElMessage.success(result || '更新友链成功')
    } else {
      // 新增模式
      result = await createLink(form)
      ElMessage.success(result || '新增友链成功')
    }

    dialogVisible.value = false
    // 刷新列表
    await getList()
  } catch (error: any) {
    console.error(isEdit.value ? '更新友链失败:' : '新增友链失败:', error)
    ElMessage.error(error.message || (isEdit.value ? '更新友链失败' : '新增友链失败'))
  } finally {
    submitLoading.value = false
  }
}

/**
 * 对话框关闭处理
 */
const handleDialogClose = () => {
  dialogVisible.value = false
  resetForm()
}

/**
 * 编辑
 */
const handleEdit = (row: Link) => {
  dialogTitle.value = '编辑友链'
  isEdit.value = true
  editingId.value = row.id

  // 填充表单数据
  form.name = row.name
  form.url = row.url
  form.logo = row.logo || ''
  form.description = row.description || ''
  form.status = row.status

  dialogVisible.value = true
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
 * 批量删除
 */
const handleBatchDelete = async () => {
  if (selectedIds.value.length === 0) {
    ElMessage.warning('请选择要删除的友链')
    return
  }

  try {
    await ElMessageBox.confirm(
      `确定要删除选中的 ${selectedIds.value.length} 个友链吗？`,
      '批量删除确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    const ids = selectedIds.value.join(',')
    const result = await deleteLink(ids)
    ElMessage.success(result || '批量删除成功')

    // 清空选择
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

.form-help-text {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
  line-height: 1.4;
}
</style>
