<template>
  <el-dialog v-model="visible" title="新建标签" width="420px" @close="handleClose">
    <el-form :model="form" label-width="80px" ref="formRef">
      <el-form-item label="名称" prop="name" :rules="[{ required: true, message: '请输入标签名称' }]">
        <el-input v-model="form.name" />
      </el-form-item>
      <el-form-item label="备注">
        <el-input v-model="form.remark" type="textarea" :rows="2" placeholder="可选" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleSubmit">确定</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { computed, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'

const props = defineProps<{
  modelValue: boolean
}>()

const emit = defineEmits<{
  (e: 'update:modelValue', value: boolean): void
  (e: 'submit', payload: { name: string; remark: string }): void
}>()

const visible = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
})

const form = reactive({
  name: '',
  remark: ''
})

const formRef = ref()

const handleClose = () => {
  visible.value = false
}

const handleSubmit = async () => {
  const name = form.name.trim()
  if (!name) {
    ElMessage.warning('请输入标签名称')
    return
  }
  emit('submit', { name, remark: form.remark.trim() })
  visible.value = false
  form.name = ''
  form.remark = ''
}
</script>
