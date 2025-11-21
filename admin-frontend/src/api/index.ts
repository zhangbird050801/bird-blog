import http from './http'
import type {
  LoginRequest,
  LoginResponse,
  CaptchaResponse,
  MenuItem,
  RouteData,
  BackendUserInfo,
  AdminUserItem,
  UserQueryParams,
  PageResult,
  RoleItem,
  RoleQueryParams,
  CreateRoleRequest,
  UpdateRoleRequest
} from '@/types'

/**
 * 认证相关 API
 */
export const authApi = {
  /**
   * 获取验证码
   */
  getCaptcha(): Promise<CaptchaResponse> {
    return http.get('/admin/auth/captcha')
  },

  /**
   * 登录
   */
  login(data: LoginRequest): Promise<LoginResponse> {
    return http.post('/admin/auth/login', data)
  },

  /**
   * 退出登录
   */
  logout(): Promise<void> {
    return http.post('/admin/auth/logout')
  },

  /**
   * 刷新 Token
   */
  refreshToken(refreshToken: string): Promise<LoginResponse> {
    return http.post('/admin/auth/refresh-token', null, {
      params: { refreshToken }
    })
  }
}

/**
 * 菜单相关 API
 */
export const menuApi = {
  /**
   * 获取当前用户的路由菜单
   */
  getRoutes(): Promise<RouteData[]> {
    return http.get('/admin/menus/routes')
  },

  /**
   * 获取菜单树形列表（用于菜单管理页面）
   * @param params 查询参数
   */
  getMenuTree(params?: { name?: string; status?: number }): Promise<MenuItem[]> {
    return http.get('/admin/menus', { params })
  },

  /**
   * 获取菜单下拉选项（用于选择父菜单）
   */
  getMenuOptions(): Promise<Array<{ value: number; label: string; children?: any[] }>> {
    return http.get('/admin/menus/options')
  },

  /**
   * 获取菜单详情（用于编辑表单）
   */
  getMenuDetail(id: number): Promise<MenuItem> {
    return http.get(`/admin/menus/${id}/form`)
  },

  /**
   * 新增菜单
   */
  createMenu(data: any): Promise<void> {
    return http.post('/admin/menus', data)
  },

  /**
   * 修改菜单
   */
  updateMenu(id: number, data: any): Promise<void> {
    return http.put(`/admin/menus/${id}`, data)
  },

  /**
   * 删除菜单
   */
  deleteMenu(id: number): Promise<void> {
    return http.delete(`/admin/menus/${id}`)
  }
}

/**
 * 用户信息相关 API
 */
export const userApi = {
  /**
   * 获取当前登录用户信息
   */
  getCurrentUser(): Promise<BackendUserInfo> {
    return http.get('/admin/users/me')
  },

  /**
   * 分页查询用户
   */
  getUserPage(params?: UserQueryParams): Promise<PageResult<AdminUserItem>> {
    return http.get('/admin/users/page', params)
  },

  /**
   * 更新用户状态
   */
  updateUserStatus(id: number, status: number): Promise<void> {
    return http.put(`/admin/users/${id}/status`, { status })
  },

  /**
   * 更新用户角色
   */
  updateUserRoles(userId: number, roleIds: number[]): Promise<void> {
    return http.put(`/admin/users/${userId}/roles`, { roleIds })
  }
}

/**
 * 角色相关 API
 */
export const roleApi = {
  /**
   * 分页查询角色
   */
  getRolePage(params?: RoleQueryParams): Promise<PageResult<RoleItem>> {
    return http.get('/admin/roles/page', params)
  },

  /**
   * 获取所有角色列表（不分页，用于下拉选择）
   */
  getRoleList(): Promise<RoleItem[]> {
    return http.get('/admin/roles/list').catch(error => {
      console.error('获取角色列表失败，使用模拟数据:', error)
      // 临时返回模拟数据，等后端API实现后再删除
      return [
        { id: 1, name: '超级管理员', code: 'SUPER_ADMIN', description: '拥有系统所有权限', status: 0 },
        { id: 2, name: '编辑', code: 'EDITOR', description: '可以管理文章、分类、标签、评论', status: 0 },
        { id: 3, name: '审核员', code: 'REVIEWER', description: '负责审核友链申请和内容', status: 0 }
      ] as RoleItem[]
    })
  },

  /**
   * 创建角色
   */
  createRole(data: CreateRoleRequest): Promise<void> {
    return http.post('/admin/roles', data)
  },

  /**
   * 更新角色
   */
  updateRole(id: number, data: UpdateRoleRequest): Promise<void> {
    return http.put(`/admin/roles/${id}`, data)
  },

  /**
   * 更新角色状态
   */
  updateRoleStatus(id: number, status: number): Promise<void> {
    return http.put(`/admin/roles/${id}/status`, { status })
  },

  /**
   * 删除角色
   */
  deleteRole(id: number): Promise<void> {
    return http.delete(`/admin/roles/${id}`)
  },

  /**
   * 获取菜单树（用于权限分配）
   */
  getMenuTree(): Promise<MenuItem[]> {
    return http.get('/admin/roles/menus/tree')
  },

  /**
   * 获取角色的菜单权限ID列表
   */
  getRoleMenuIds(roleId: number): Promise<number[]> {
    return http.get(`/admin/roles/${roleId}/menu-ids`)
  },

  /**
   * 更新角色菜单权限
   */
  updateRoleMenus(roleId: number, menuIds: number[]): Promise<void> {
    return http.put(`/admin/roles/${roleId}/menus`, { menuIds })
  }
}
