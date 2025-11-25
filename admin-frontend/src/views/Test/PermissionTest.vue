<template>
  <div class="permission-test">
    <el-card>
      <template #header>
        <h2>权限测试页面</h2>
      </template>

      <div class="user-info">
        <h3>当前用户信息</h3>
        <el-descriptions :column="2" border>
          <el-descriptions-item label="用户ID">{{ userStore.userInfo?.id }}</el-descriptions-item>
          <el-descriptions-item label="昵称">{{ userStore.userInfo?.nickName }}</el-descriptions-item>
          <el-descriptions-item label="邮箱">{{ userStore.userInfo?.email }}</el-descriptions-item>
          <el-descriptions-item label="是否登录">{{ userStore.isLoggedIn ? '是' : '否' }}</el-descriptions-item>
        </el-descriptions>
      </div>

      <div class="roles-info">
        <h3>用户角色</h3>
        <el-tag
          v-for="role in userStore.userInfo?.roles"
          :key="role.id"
          :type="getRoleTagType(role.code)"
          class="role-tag"
        >
          {{ role.name }} ({{ role.code }})
        </el-tag>
        <el-tag v-if="!userStore.userInfo?.roles?.length" type="danger">无角色</el-tag>
      </div>

      <div class="permissions-info">
        <h3>用户权限</h3>
        <el-tag
          v-for="permission in userStore.userInfo?.permissions"
          :key="permission"
          type="info"
          class="permission-tag"
        >
          {{ permission }}
        </el-tag>
        <el-tag v-if="!userStore.userInfo?.permissions?.length" type="warning">无权限</el-tag>
      </div>

      <div class="permission-checks">
        <h3>权限检查结果</h3>
        <el-space direction="vertical" style="width: 100%">
          <el-card>
            <div class="check-item">
              <span class="check-label">超级管理员 (SUPER_ADMIN):</span>
              <el-tag :type="userStore.hasRole('SUPER_ADMIN') ? 'success' : 'danger'">
                {{ userStore.hasRole('SUPER_ADMIN') ? '有权限' : '无权限' }}
              </el-tag>
            </div>
          </el-card>

          <el-card>
            <div class="check-item">
              <span class="check-label">编辑 (EDITOR):</span>
              <el-tag :type="userStore.hasRole('EDITOR') ? 'success' : 'danger'">
                {{ userStore.hasRole('EDITOR') ? '有权限' : '无权限' }}
              </el-tag>
            </div>
          </el-card>

          <el-card>
            <div class="check-item">
              <span class="check-label">审核员 (REVIEWER):</span>
              <el-tag :type="userStore.hasRole('REVIEWER') ? 'success' : 'danger'">
                {{ userStore.hasRole('REVIEWER') ? '有权限' : '无权限' }}
              </el-tag>
            </div>
          </el-card>

          <el-card>
            <div class="check-item">
              <span class="check-label">访客 (VISITOR):</span>
              <el-tag :type="userStore.hasRole('VISITOR') ? 'warning' : 'info'">
                {{ userStore.hasRole('VISITOR') ? '是访客' : '非访客' }}
              </el-tag>
            </div>
          </el-card>

          <el-card>
            <div class="check-item">
              <span class="check-label">可以访问后台:</span>
              <el-tag :type="canAccessAdmin ? 'success' : 'danger'">
                {{ canAccessAdmin ? '是' : '否' }}
              </el-tag>
            </div>
          </el-card>

          <el-card>
            <div class="check-item">
              <span class="check-label">文章管理权限:</span>
              <el-tag :type="userStore.hasPermission('content:article:list') ? 'success' : 'danger'">
                {{ userStore.hasPermission('content:article:list') ? '有权限' : '无权限' }}
              </el-tag>
            </div>
          </el-card>

          <el-card>
            <div class="check-item">
              <span class="check-label">用户管理权限:</span>
              <el-tag :type="userStore.hasPermission('system:user:list') ? 'success' : 'danger'">
                {{ userStore.hasPermission('system:user:list') ? '有权限' : '无权限' }}
              </el-tag>
            </div>
          </el-card>
        </el-space>
      </div>

      <div class="actions">
        <el-button type="primary" @click="refreshUserInfo">刷新用户信息</el-button>
        <el-button type="success" @click="goToDashboard">前往首页</el-button>
        <el-button type="warning" @click="logout">退出登录</el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { useUserStore } from '@/stores/user'
import { useRouter } from 'vue-router'
import { computed } from 'vue'

const userStore = useUserStore()
const router = useRouter()

// 计算是否可以访问后台
const canAccessAdmin = computed(() => {
  return userStore.hasRole('SUPER_ADMIN') ||
         userStore.hasRole('EDITOR') ||
         userStore.hasRole('REVIEWER')
})

// 获取角色标签类型
const getRoleTagType = (roleCode: string) => {
  switch (roleCode) {
    case 'SUPER_ADMIN':
      return 'danger'
    case 'EDITOR':
      return 'success'
    case 'REVIEWER':
      return 'warning'
    case 'VISITOR':
      return 'info'
    default:
      return ''
  }
}

// 刷新用户信息
const refreshUserInfo = async () => {
  try {
    await userStore.fetchCurrentUser(true)
    ElMessage.success('用户信息已刷新')
  } catch (error) {
    ElMessage.error('刷新用户信息失败')
    console.error(error)
  }
}

// 前往首页
const goToDashboard = () => {
  router.push('/dashboard')
}

// 退出登录
const logout = async () => {
  try {
    await userStore.logout()
    router.push('/login')
    ElMessage.success('已退出登录')
  } catch (error) {
    ElMessage.error('退出登录失败')
  }
}
</script>

<style scoped>
.permission-test {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.user-info,
.roles-info,
.permissions-info,
.permission-checks {
  margin-bottom: 20px;
}

.role-tag,
.permission-tag {
  margin-right: 8px;
  margin-bottom: 8px;
}

.check-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 0;
}

.check-label {
  font-weight: bold;
  color: #303133;
}

.actions {
  margin-top: 30px;
  text-align: center;
}

.actions .el-button {
  margin: 0 10px;
}

h2, h3 {
  color: #303133;
  margin-bottom: 15px;
}
</style>