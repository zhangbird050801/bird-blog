<template>
  <el-dialog
    v-model="visible"
    :title="isEdit ? '编辑文章' : '新增文章'"
    width="90%"
    :before-close="handleClose"
    class="article-edit-dialog"
    fullscreen
  >
    <div v-loading="loading" class="edit-container">
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="100px"
        class="article-form"
      >
        <!-- 基本信息 -->
        <el-card class="form-card" shadow="never">
          <template #header>
            <div class="card-header">
              <span>基本信息</span>
            </div>
          </template>

          <el-row :gutter="20">
            <el-col :span="16">
              <el-form-item label="文章标题" prop="title">
                <el-input
                  v-model="formData.title"
                  placeholder="请输入文章标题"
                  maxlength="100"
                  show-word-limit
                />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="URL别名" prop="slug">
                <el-input
                  v-model="formData.slug"
                  placeholder="可选，用于SEO友好的URL"
                  maxlength="50"
                  show-word-limit
                />
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="20">
            <el-col :span="8">
              <el-form-item label="文章分类" prop="categoryId">
                <el-select
                  v-model="formData.categoryId"
                  placeholder="请选择分类"
                  clearable
                  filterable
                  style="width: 100%"
                  @change="handleCategoryChange"
                >
                  <el-option
                    v-for="category in categoryOptions"
                    :key="category.id"
                    :label="category.name"
                    :value="category.id"
                  />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="新建分类">
                <div class="inline-actions">
                  <el-button type="primary" plain @click="openCategoryDialog">新建分类</el-button>
                  <div class="chip-list">
                    <el-tag
                      v-if="pendingNewCategory"
                      closable
                      @close="clearPendingCategory"
                      class="chip"
                    >
                      {{ pendingNewCategory }}<span v-if="pendingNewCategoryRemark">（{{ pendingNewCategoryRemark }}）</span>
                    </el-tag>
                  </div>
                </div>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="文章状态" prop="status">
                <el-select v-model="formData.status" style="width: 100%">
                  <el-option label="已发布" :value="0" />
                  <el-option label="草稿" :value="1" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="是否置顶">
                <el-switch v-model="formData.isTop" />
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="20">
            <el-col :span="24">
              <el-form-item label="标签">
                <el-select
                  v-model="formData.tagIds"
                  multiple
                  filterable
                  clearable
                  :loading="tagLoading"
                  placeholder="选择已有标签"
                  style="width: 100%"
                  @change="handleTagChange"
                >
                  <el-option
                    v-for="tag in tagOptions"
                    :key="tag.id"
                    :label="tag.name"
                    :value="tag.id"
                  />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="新标签">
                <div class="inline-actions">
                  <el-button type="primary" plain @click="openTagDialog">新建标签</el-button>
                  <div class="chip-list">
                    <el-tag
                      v-for="tag in pendingNewTagsDetail"
                      :key="tag.name + tag.remark"
                      closable
                      @close="removePendingTag(tag.name)"
                      class="chip"
                    >
                      {{ tag.name }}<span v-if="tag.remark">（{{ tag.remark }}）</span>
                    </el-tag>
                  </div>
                </div>
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="20">
            <el-col :span="24">
              <el-form-item label="文章摘要" prop="summary">
                <el-input
                  v-model="formData.summary"
                  type="textarea"
                  :rows="3"
                  placeholder="请输入文章摘要，可选"
                  maxlength="200"
                  show-word-limit
                />
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="20">
            <el-col :span="24">
              <el-form-item label="缩略图">
                <div class="thumbnail-upload-container">
                  <div v-if="formData.thumbnail" class="thumbnail-preview" @click="removeThumbnail">
                    <img :src="formData.thumbnail" alt="缩略图" />
                    <div class="thumbnail-overlay">
                      <el-icon><Delete /></el-icon>
                      <span>删除</span>
                    </div>
                  </div>
                  <div v-else class="thumbnail-placeholder" @click="triggerThumbnailUpload">
                    <el-icon class="thumbnail-icon"><Plus /></el-icon>
                    <div class="thumbnail-text">点击上传缩略图</div>
                  </div>
                  <input
                    ref="thumbnailFileInput"
                    type="file"
                    accept="image/*"
                    style="display: none"
                    @change="handleThumbnailFileChange"
                  />
                  <el-input
                    v-model="formData.thumbnail"
                    placeholder="或直接输入缩略图URL"
                    clearable
                    class="thumbnail-url-input"
                  />
                </div>
              </el-form-item>
            </el-col>
          </el-row>
        </el-card>

        <!-- Markdown 编辑器和预览 -->
        <el-card class="form-card editor-card" shadow="never" style="margin-top: 20px;">
          <template #header>
            <div class="card-header">
              <span>文章内容编辑</span>
              <el-text type="info" size="small">左侧编辑，右侧实时预览</el-text>
            </div>
          </template>

          <el-form-item prop="content" style="margin-bottom: 0;">
            <div class="editor-layout">
              <!-- 左侧编辑区 -->
              <div class="editor-panel">
                <div class="editor-header">
                  <el-icon><Edit /></el-icon>
                  <span>编辑区</span>
                </div>
                <div class="editor-content">
                  <MdEditor
                    v-model="formData.content"
                    :height="600"
                    :preview="false"
                    :toolbars="editToolbars"
                    :tab-width="4"
                    :show-code-row-number="true"
                    :toolbars-exclude="[]"
                    :catalog-visible="false"
                    :html-preview="false"
                    :no-upload-img="false"
                    :auto-detect-code="true"
                    @on-upload-img="handleUploadImage"
                    @onChange="handleContentChange"
                  />
                </div>
              </div>

  
              <!-- 右侧预览区 -->
              <div class="preview-panel">
                <div class="preview-header">
                  <el-icon><View /></el-icon>
                  <span>预览区</span>
                </div>
                <div class="preview-content">
                  <div v-if="renderedContent" class="markdown-preview" v-html="renderedContent"></div>
                  <div v-else class="preview-placeholder">
                    <el-empty
                      description="在左侧编辑区输入内容，这里会实时显示预览效果"
                      :image-size="100"
                    />
                  </div>
                </div>
              </div>
            </div>
          </el-form-item>
        </el-card>
      </el-form>
    </div>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleClose">取消</el-button>
        <el-button type="primary" @click="handleSave" :loading="saving">
          {{ isEdit ? '更新' : '保存' }}
        </el-button>
      </div>
    </template>
  </el-dialog>

  <!-- 新建标签弹窗 -->
  <el-dialog v-model="tagDialogVisible" title="新建标签" width="420px">
    <el-form label-width="80px">
      <el-form-item label="名称" required>
        <el-input v-model="tagDialogForm.name" placeholder="请输入标签名称" />
      </el-form-item>
      <el-form-item label="备注">
        <el-input v-model="tagDialogForm.remark" type="textarea" :rows="2" placeholder="可选" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="tagDialogVisible = false">取消</el-button>
      <el-button type="primary" @click="submitTagDialog">确定</el-button>
    </template>
  </el-dialog>

  <!-- 新建分类弹窗 -->
  <el-dialog v-model="categoryDialogVisible" title="新建分类" width="420px">
    <el-form label-width="80px">
      <el-form-item label="名称" required>
        <el-input v-model="categoryDialogForm.name" placeholder="请输入分类名称" />
      </el-form-item>
      <el-form-item label="备注">
        <el-input v-model="categoryDialogForm.remark" type="textarea" :rows="2" placeholder="可选" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="categoryDialogVisible = false">取消</el-button>
      <el-button type="primary" @click="submitCategoryDialog">确定</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive, computed, watch, onMounted, nextTick } from 'vue'
import { ElMessage, ElMessageBox, FormInstance, FormRules } from 'element-plus'
import { MdEditor } from 'md-editor-v3'
import { Edit, View, Plus, Delete } from '@element-plus/icons-vue'
import 'md-editor-v3/lib/style.css'
import { useMarkdown } from '@/composables/useMarkdown'
import { updateArticle, createArticle, Article } from '@/api/article'
import { getCategoryPage } from '@/api/category'
import { getTagPage } from '@/api/tag'
import { uploadImage, uploadThumbnail } from '@/api/upload'
import type { UploadResponse } from '@/api/upload'
import type { Category, Tag } from '@/types'

interface Props {
  modelValue: boolean
  article?: Article | null
}

interface Emits {
  (e: 'update:modelValue', value: boolean): void
  (e: 'success'): void
}

const props = withDefaults(defineProps<Props>(), {
  article: null
})

const emit = defineEmits<Emits>()

// 响应式数据
const formRef = ref<FormInstance>()
const loading = ref(false)
const saving = ref(false)
const categoryOptions = ref<Category[]>([])
const tagOptions = ref<Tag[]>([])
const tagLoading = ref(false)
const pendingNewTags = ref<string[]>([])
const pendingNewCategory = ref<string>('')
const pendingNewTagsDetail = ref<{ name: string; remark?: string }[]>([])
const pendingNewCategoryRemark = ref('')
const tagDialogVisible = ref(false)
const categoryDialogVisible = ref(false)
const tagDialogForm = reactive({ name: '', remark: '' })
const categoryDialogForm = reactive({ name: '', remark: '' })
const thumbnailFileInput = ref<HTMLInputElement>()

// 上传配置
const uploadAction = ref('/admin/upload/thumbnail')

// 动态获取上传头
const getUploadHeaders = () => {
  const token = localStorage.getItem('token') || ''
  return {
    'Authorization': `Bearer ${token}`
  }
}

// 计算属性
const visible = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value)
})

const isEdit = computed(() => !!props.article?.id)

const { render } = useMarkdown()

const renderedContent = computed(() => {
  return formData.content ? render(formData.content) : ''
})

// 表单数据
const formData = reactive({
  title: '',
  slug: '',
  summary: '',
  content: '',
  categoryId: undefined as number | undefined,
  status: 1, // 默认草稿
  isTop: false,
  thumbnail: '',
  tagIds: [] as Array<number | string>
})

// 表单验证规则
const formRules: FormRules = {
  title: [
    { required: true, message: '请输入文章标题', trigger: 'blur' },
    { min: 2, max: 100, message: '标题长度在 2 到 100 个字符', trigger: 'blur' }
  ],
  content: [
    { required: true, message: '请输入文章内容', trigger: 'blur' },
    { min: 10, message: '文章内容至少需要 10 个字符', trigger: 'blur' }
  ],
  categoryId: [
    {
      validator: (_rule, _value, callback) => {
        if (formData.categoryId || pendingNewCategory.value) {
          callback()
        } else {
          callback(new Error('请选择文章分类或新建一个分类'))
        }
      },
      trigger: 'change'
    }
  ]
}

// 编辑器工具栏配置（精简版，适合分栏显示）
const editToolbars = [
  'bold',
  'underline',
  'italic',
  '|',
  'h1',
  'h2',
  'h3',
  '|',
  'unorderedList',
  'orderedList',
  '|',
  'codeRow',
  'code',
  'link',
  'image',
  'table',
  '|',
  'revoke',
  'next',
  'save'
]

// 完整工具栏配置（如需要全屏时使用）
const fullToolbars = [
  'bold',
  'underline',
  'italic',
  '-',
  'title',
  'strikeThrough',
  'sub',
  'sup',
  'quote',
  'unorderedList',
  'orderedList',
  'task',
  '-',
  'codeRow',
  'code',
  'link',
  'image',
  'table',
  'mermaid',
  'katex',
  '-',
  'revoke',
  'next',
  'save',
  0,
  1,
  2,
  3,
  4,
  5,
  'fullscreen'
]

// 监听弹窗显示状态
watch(visible, (isVisible) => {
  if (isVisible) {
    if (props.article) {
      // 编辑模式：填充表单数据
      Object.assign(formData, {
        title: props.article.title || '',
        slug: props.article.slug || '',
        summary: props.article.summary || '',
        content: props.article.content || '',
        categoryId: props.article.categoryId,
        status: props.article.status,
        isTop: props.article.isTop || false,
        thumbnail: props.article.thumbnail || '',
        tagIds: props.article.tagIds || []
      })
      pendingNewCategory.value = ''
      pendingNewCategoryRemark.value = ''
      pendingNewTags.value = []
      pendingNewTagsDetail.value = []
    } else {
      // 新增模式：重置表单
      resetForm()
    }
  }
})

// 监听文章数据变化
watch(() => props.article, (newArticle) => {
  if (newArticle && visible.value) {
    Object.assign(formData, {
      title: newArticle.title || '',
      slug: newArticle.slug || '',
      summary: newArticle.summary || '',
      content: newArticle.content || '',
      categoryId: newArticle.categoryId,
      status: newArticle.status,
      isTop: newArticle.isTop || false,
      thumbnail: newArticle.thumbnail || '',
      tagIds: newArticle.tagIds || []
    })
    pendingNewCategory.value = ''
    pendingNewCategoryRemark.value = ''
    pendingNewTags.value = []
    pendingNewTagsDetail.value = []
  }
}, { deep: true })

/**
 * 重置表单
 */
const resetForm = () => {
  Object.assign(formData, {
    title: '',
    slug: '',
    summary: '',
    content: '',
    categoryId: undefined,
    status: 1,
    isTop: false,
    thumbnail: '',
    tagIds: []
  })
  pendingNewCategory.value = ''
  pendingNewCategoryRemark.value = ''
  pendingNewTags.value = []
  pendingNewTagsDetail.value = []
  formRef.value?.clearValidate()
}

/**
 * 获取分类选项
 */
const getCategoryOptions = async () => {
  try {
    const result = await getCategoryPage({ pageNum: 1, pageSize: 1000 })
    categoryOptions.value = result.rows
    // 如果新建的分类刚被创建，刷新后尝试自动选中
    if (pendingNewCategory.value) {
      const match = categoryOptions.value.find(c => c.name === pendingNewCategory.value)
      if (match) {
        formData.categoryId = match.id
        pendingNewCategory.value = ''
      }
    }
  } catch (error) {
    console.error('获取分类选项失败:', error)
    ElMessage.error('获取分类选项失败')
  }
}

/**
 * 获取标签选项
 */
const getTagOptions = async () => {
  try {
    tagLoading.value = true
    const result = await getTagPage({ pageNum: 1, pageSize: 500 })
    tagOptions.value = result.rows || []
  } catch (error) {
    console.error('获取标签选项失败:', error)
    ElMessage.error('获取标签选项失败')
  } finally {
    tagLoading.value = false
  }
}

/**
 * 处理图片上传
 */
const handleUploadImage = async (files: File[], callback: (urls: string[]) => void) => {
  const res: string[] = []

  for (const file of files) {
    try {
      // 调用图片上传API
      const response = await uploadImage(file, 'article')
      const url = response.url

      // 调试信息
      console.log('上传成功，URL:', url)
      console.log('完整响应:', response)

      // md-editor-v3 期望的回调是直接的URL数组，不是markdown格式
      res.push(url)

      ElMessage.success(`${file.name} 上传成功`)
    } catch (error: any) {
      console.error('图片上传失败:', error)
      ElMessage.error(`${file.name} 上传失败: ${error.message || '未知错误'}`)
    }
  }

  callback(res)
}

/**
 * 保存文章
 */
const handleSave = async () => {
  if (!formRef.value) return

  try {
    await formRef.value.validate()
  } catch {
    ElMessage.warning('请完善必填信息')
    return
  }

  saving.value = true
  try {
    const cleanTagIds = formData.tagIds.filter((v): v is number => typeof v === 'number')
    const articleData = {
      ...formData,
      tagIds: cleanTagIds,
      newCategoryName: pendingNewCategory.value || undefined,
      newCategoryRemark: pendingNewCategoryRemark.value || undefined,
      newTags: Array.from(new Set(pendingNewTags.value.map(name => name.trim()).filter(Boolean))),
      newTagsDetail: pendingNewTagsDetail.value.map(t => ({
        name: t.name.trim(),
        remark: t.remark?.trim() || ''
      }))
    }

    if (isEdit.value) {
      await updateArticle(props.article!.id, articleData)
      ElMessage.success('文章更新成功')
    } else {
      await createArticle(articleData)
      ElMessage.success('文章创建成功')
    }

    await getTagOptions()
    await getCategoryOptions()
    emit('success')
    handleClose()
  } catch (error: any) {
    console.error('保存文章失败:', error)
    ElMessage.error(error.message || '保存文章失败')
  } finally {
    saving.value = false
  }
}

/**
 * 处理内容变化
 */
const handleContentChange = (content: string) => {
  formData.content = content
}

/**
 * 触发缩略图上传
 */
const triggerThumbnailUpload = () => {
  thumbnailFileInput.value?.click()
}

/**
 * 处理缩略图文件选择
 */
const handleThumbnailFileChange = async (event: Event) => {
  const target = event.target as HTMLInputElement
  const file = target.files?.[0]

  if (!file) return

  // 文件校验
  const isImage = file.type.startsWith('image/')
  const isLt10M = file.size / 1024 / 1024 < 10

  if (!isImage) {
    ElMessage.error('只能上传图片文件!')
    return
  }
  if (!isLt10M) {
    ElMessage.error('上传图片大小不能超过 10MB!')
    return
  }

  try {
    // 调用缩略图上传API
    const response = await uploadThumbnail(file)
    formData.thumbnail = response.url
    ElMessage.success('缩略图上传成功')
  } catch (error: any) {
    console.error('缩略图上传失败:', error)
    ElMessage.error(`缩略图上传失败: ${error.message || '未知错误'}`)
  } finally {
    // 清空文件选择，允许重复选择同一文件
    target.value = ''
  }
}

/**
 * 缩略图上传前校验（已废弃，改为使用自定义上传）
 */
const beforeThumbnailUpload = (file: File) => {
  return true // 为了兼容性保留
}

/**
 * 缩略图上传成功处理（已废弃，改为使用自定义上传）
 */
const handleThumbnailSuccess = (response: any) => {
  // 已废弃，改为使用自定义上传
}

/**
 * 删除缩略图
 */
const removeThumbnail = () => {
  ElMessageBox.confirm('确定要删除这张缩略图吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(() => {
    formData.thumbnail = ''
    ElMessage.success('缩略图已删除')
  }).catch(() => {
    // 用户取消删除
  })
}


/**
 * 关闭弹窗
 */
const handleClose = () => {
  visible.value = false
  resetForm()
}

/**
 * 标签变化时处理新增标签
 */
const handleTagChange = (values: Array<number | string>) => {
  const selectedIds = values.filter((v): v is number => typeof v === 'number')
  formData.tagIds = selectedIds
}

/**
 * 分类选择/创建
 */
const handleCategoryChange = (value: number | string | undefined) => {
  if (typeof value === 'number') {
    pendingNewCategory.value = ''
    pendingNewCategoryRemark.value = ''
    formData.categoryId = value
  }
}

const openTagDialog = () => {
  tagDialogVisible.value = true
  tagDialogForm.name = ''
  tagDialogForm.remark = ''
}

const submitTagDialog = () => {
  const name = tagDialogForm.name.trim()
  if (!name) {
    ElMessage.warning('请输入标签名称')
    return
  }
  pendingNewTagsDetail.value.push({
    name,
    remark: tagDialogForm.remark.trim() || undefined
  })
  // 让新标签在选择器中可见
  pendingNewTags.value.push(name)
  tagDialogVisible.value = false
}

const removePendingTag = (name: string) => {
  pendingNewTagsDetail.value = pendingNewTagsDetail.value.filter(t => t.name !== name)
}

const openCategoryDialog = () => {
  categoryDialogVisible.value = true
  categoryDialogForm.name = ''
  categoryDialogForm.remark = ''
}

const submitCategoryDialog = () => {
  const name = categoryDialogForm.name.trim()
  if (!name) {
    ElMessage.warning('请输入分类名称')
    return
  }
  pendingNewCategory.value = name
  pendingNewCategoryRemark.value = categoryDialogForm.remark.trim() || ''
  formData.categoryId = undefined
  categoryDialogVisible.value = false
}

const clearPendingCategory = () => {
  pendingNewCategory.value = ''
  pendingNewCategoryRemark.value = ''
}

// 页面加载时获取分类选项
onMounted(() => {
  getCategoryOptions()
  getTagOptions()
})
</script>

<style scoped>
/* 弹窗内快速操作布局 */
.inline-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.chip-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.chip {
  margin: 2px 0;
}

/* 缩略图上传样式 */
.thumbnail-upload-container {
  display: flex;
  gap: 16px;
  align-items: center;
}

.thumbnail-uploader {
  border: 1px dashed var(--el-border-color);
  border-radius: 8px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: var(--el-transition-duration-fast);
}

.thumbnail-uploader:hover {
  border-color: var(--el-color-primary);
}

.thumbnail-preview {
  width: 120px;
  height: 80px;
  position: relative;
  cursor: pointer;
}

.thumbnail-preview img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 6px;
}

.thumbnail-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.6);
  color: white;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s;
  border-radius: 6px;
}

.thumbnail-preview:hover .thumbnail-overlay {
  opacity: 1;
}

.thumbnail-overlay .el-icon {
  font-size: 20px;
  margin-bottom: 4px;
}

.thumbnail-overlay span {
  font-size: 12px;
}

.thumbnail-placeholder {
  width: 120px;
  height: 80px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: var(--el-text-color-placeholder);
}

.thumbnail-icon {
  font-size: 24px;
  color: var(--el-text-color-placeholder);
  margin-bottom: 8px;
}

.thumbnail-text {
  font-size: 12px;
  color: var(--el-text-color-placeholder);
}

.thumbnail-url-input {
  flex: 1;
}
.article-edit-dialog {
  --el-dialog-padding-primary: 20px;
}

.edit-container {
  min-height: 600px;
}

.article-form {
  padding: 0;
}

.form-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 600;
}

.editor-card {
  .el-card__body {
    padding: 0;
  }
}

.editor-layout {
  display: flex;
  height: 700px;
  min-height: 600px;
  width: 100%;
}

.editor-panel {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  width: 50%;
  min-width: 0;
}

.editor-header {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 16px;
  background-color: var(--el-fill-color-lighter);
  border-bottom: 1px solid var(--el-border-color-light);
  font-weight: 500;
  font-size: 14px;
  color: var(--el-text-color-primary);
}

.editor-content {
  flex: 1;
  overflow: hidden;
  width: 100%;
  height: 100%;
}


.preview-panel {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  border-left: 1px solid var(--el-border-color-light);
  width: 50%;
  min-width: 0;
}

.preview-header {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 16px;
  background-color: var(--el-fill-color-lighter);
  border-bottom: 1px solid var(--el-border-color-light);
  font-weight: 500;
  font-size: 14px;
  color: var(--el-text-color-primary);
}

.preview-content {
  flex: 1;
  padding: 16px;
  overflow-y: auto;
  background-color: #fff;
  width: 100%;
  height: 100%;
}

.markdown-preview {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'Helvetica Neue', Helvetica, 'PingFang SC', 'Microsoft YaHei', SimSun, sans-serif;
  font-size: 14px;
  line-height: 1.6;
  color: #333;
  word-wrap: break-word;
}

/* Markdown 预览样式 */
.markdown-preview :deep(h1),
.markdown-preview :deep(h2),
.markdown-preview :deep(h3),
.markdown-preview :deep(h4),
.markdown-preview :deep(h5),
.markdown-preview :deep(h6) {
  margin-top: 24px;
  margin-bottom: 16px;
  font-weight: 600;
  line-height: 1.25;
  color: #24292e;
}

.markdown-preview :deep(h1) {
  font-size: 2em;
  border-bottom: 1px solid #e1e4e8;
  padding-bottom: 0.3em;
}

.markdown-preview :deep(h2) {
  font-size: 1.5em;
  border-bottom: 1px solid #e1e4e8;
  padding-bottom: 0.3em;
}

.markdown-preview :deep(h3) {
  font-size: 1.25em;
}

.markdown-preview :deep(p) {
  margin-top: 0;
  margin-bottom: 16px;
}

.markdown-preview :deep(blockquote) {
  margin: 0;
  padding: 0 16px;
  color: #6a737d;
  border-left: 4px solid #dfe2e5;
}

.markdown-preview :deep(ul),
.markdown-preview :deep(ol) {
  padding-left: 32px;
  margin-bottom: 16px;
}

.markdown-preview :deep(li) {
  margin-bottom: 4px;
}

.markdown-preview :deep(li > p) {
  margin-bottom: 0;
}

.markdown-preview :deep(code) {
  padding: 2px 4px;
  font-size: 0.9em;
  background-color: #f6f8fa;
  border-radius: 4px;
  font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
}

.markdown-preview :deep(pre) {
  padding: 16px;
  overflow: auto;
  font-size: 0.9em;
  line-height: 1.45;
  background-color: #f6f8fa;
  border-radius: 8px;
  margin-bottom: 16px;
}

.markdown-preview :deep(pre code) {
  display: inline;
  padding: 0;
  background: none;
  font-size: 1em;
}

.markdown-preview :deep(table) {
  border-collapse: collapse;
  border-spacing: 0;
  margin-bottom: 16px;
  width: 100%;
}

.markdown-preview :deep(table th),
.markdown-preview :deep(table td) {
  padding: 8px 12px;
  border: 1px solid #e1e4e8;
  text-align: left;
}

.markdown-preview :deep(table th) {
  font-weight: 600;
  background-color: #f6f8fa;
}

.markdown-preview :deep(table tr:nth-child(2n)) {
  background-color: #f6f8fa;
}

.markdown-preview :deep(a) {
  color: #409eff;
  text-decoration: none;
}

.markdown-preview :deep(a:hover) {
  text-decoration: underline;
}

.markdown-preview :deep(img) {
  max-width: 100%;
  height: auto;
  border-radius: 8px;
  margin: 8px 0;
}

.markdown-preview :deep(hr) {
  height: 1px;
  border: none;
  background-color: #e1e4e8;
  margin: 24px 0;
}

.preview-placeholder {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

:deep(.md-editor) {
  border: none;
  width: 100% !important;
  height: 100% !important;
}

:deep(.md-editor-content) {
  padding: 16px;
  font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
  height: 100% !important;
}

:deep(.md-editor-preview) {
  padding: 16px;
}

/* 图片样式 - 支持调整大小 */
:deep(.md-editor-content img),
:deep(.markdown-preview img) {
  max-width: 100%;
  height: auto;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  cursor: pointer;
}

:deep(.md-editor-content img:hover),
:deep(.markdown-preview img:hover) {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
  transform: translateY(-2px);
}

/* 可调整大小的图片容器 */
:deep(.resizable-image) {
  position: relative;
  display: inline-block;
  margin: 8px 0;
}

:deep(.resizable-image img) {
  width: 100%;
  height: auto;
  display: block;
}

/* 图片调整手柄 */
:deep(.resizable-handle) {
  position: absolute;
  bottom: 0;
  right: 0;
  width: 12px;
  height: 12px;
  background: var(--el-color-primary);
  cursor: nwse-resize;
  border-radius: 50%;
  opacity: 0;
  transition: opacity 0.3s ease;
}

:deep(.resizable-image:hover .resizable-handle) {
  opacity: 1;
}

:deep(.el-card__body) {
  padding: 20px;
}


/* 响应式调整 */
@media (max-width: 1200px) {
  .editor-layout {
    flex-direction: column;
    height: auto;
  }

  .editor-panel {
    border-left: none;
    border-right: none;
    border-bottom: 1px solid var(--el-border-color-light);
    width: 100%;
  }

  .preview-panel {
    border-left: none;
    width: 100%;
  }

  .editor-content {
    height: 400px;
  }

  .preview-content {
    height: 400px;
    max-height: 500px;
  }
}

@media (max-width: 768px) {
  :deep(.el-col) {
    margin-bottom: 16px;
  }

  .editor-layout {
    height: auto;
  }

  .editor-content {
    height: 300px;
  }

  .preview-content {
    height: 300px;
    max-height: 400px;
  }
}
</style>
