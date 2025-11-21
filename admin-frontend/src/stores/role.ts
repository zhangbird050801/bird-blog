import { defineStore } from 'pinia'
import { ref } from 'vue'
import { roleApi } from '@/api'
import type { RoleItem, RoleQueryParams, CreateRoleRequest, UpdateRoleRequest } from '@/types'

export const useRoleStore = defineStore('role', () => {
  // 状态
  const roleList = ref<RoleItem[]>([])
  const loading = ref(false)
  const total = ref(0)

  // 获取角色列表
  async function fetchRoleList(params?: RoleQueryParams) {
    try {
      loading.value = true
      const response = await roleApi.getRolePage(params)
      roleList.value = response.rows || []
      total.value = response.total || 0
      return response
    } catch (error) {
      console.error('获取角色列表失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  // 获取所有角色（不分页）
  async function fetchAllRoles() {
    try {
      const roles = await roleApi.getRoleList()
      return roles || []
    } catch (error) {
      console.error('获取所有角色失败:', error)
      throw error
    }
  }

  // 创建角色
  async function createRole(data: CreateRoleRequest) {
    try {
      await roleApi.createRole(data)
      return true
    } catch (error) {
      console.error('创建角色失败:', error)
      throw error
    }
  }

  // 更新角色
  async function updateRole(id: number, data: UpdateRoleRequest) {
    try {
      await roleApi.updateRole(id, data)
      return true
    } catch (error) {
      console.error('更新角色失败:', error)
      throw error
    }
  }

  // 更新角色状态
  async function updateRoleStatus(id: number, status: number) {
    try {
      await roleApi.updateRoleStatus(id, status)
      return true
    } catch (error) {
      console.error('更新角色状态失败:', error)
      throw error
    }
  }

  // 删除角色
  async function deleteRole(id: number) {
    try {
      await roleApi.deleteRole(id)
      return true
    } catch (error) {
      console.error('删除角色失败:', error)
      throw error
    }
  }

  // 获取角色菜单权限ID列表
  async function getRoleMenuIds(roleId: number) {
    try {
      const menuIds = await roleApi.getRoleMenuIds(roleId)
      return menuIds || []
    } catch (error) {
      console.error('获取角色菜单权限失败:', error)
      throw error
    }
  }

  // 更新角色菜单权限
  async function updateRoleMenus(roleId: number, menuIds: number[]) {
    try {
      await roleApi.updateRoleMenus(roleId, menuIds)
      return true
    } catch (error) {
      console.error('更新角色菜单权限失败:', error)
      throw error
    }
  }

  // 根据ID获取角色信息
  function getRoleById(id: number): RoleItem | undefined {
    return roleList.value.find(role => role.id === id)
  }

  // 根据编码获取角色信息
  function getRoleByCode(code: string): RoleItem | undefined {
    return roleList.value.find(role => role.code === code)
  }

  // 重置状态
  function resetState() {
    roleList.value = []
    loading.value = false
    total.value = 0
  }

  return {
    // 状态
    roleList,
    loading,
    total,

    // 方法
    fetchRoleList,
    fetchAllRoles,
    createRole,
    updateRole,
    updateRoleStatus,
    deleteRole,
    getRoleMenuIds,
    updateRoleMenus,
    getRoleById,
    getRoleByCode,
    resetState
  }
})