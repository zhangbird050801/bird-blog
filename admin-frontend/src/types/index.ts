/**
 * 后端统一响应格式
 */
export interface ApiResponse<T = any> {
  code: number
  msg: string
  data: T
}

/**
 * 登录请求参数
 */
export interface LoginRequest {
  userName: string
  password: string
  code: string
  uuid: string
}

/**
 * 登录响应数据
 */
export interface LoginResponse {
  token: string
  refreshToken: string
  userInfo: BackendUserInfo
}

/**
 * 后端返回的原始用户信息结构
 */
export interface BackendUserInfo {
  id: number
  nickName: string
  avatar?: string | null
  sex?: string | null
  email?: string | null
}

/**
 * 验证码响应数据
 */
export interface CaptchaResponse {
  img: string  
  uuid: string
}

/**
 * 用户信息
 */
export interface UserInfo {
  id: number
  nickName: string
  displayName: string
  username?: string
  email?: string
  phone?: string
  sex: 0 | 1 | 2
  avatar?: string
  type?: number
  status?: number
}

/**
 * 菜单/路由项
 */
export interface MenuItem {
  id: number
  menuName: string
  parentId: number | null
  orderNum: number
  path: string
  component?: string
  isFrame: number
  menuType: 'M' | 'C' | 'F' // M目录 C菜单 F按钮
  visible: number
  status: number
  perms?: string
  icon?: string
  remark?: string
  children?: MenuItem[]
}

/**
 * 路由元信息
 */
export interface RouteMeta {
  title: string
  icon?: string
  hidden?: boolean
  alwaysShow?: boolean
  noCache?: boolean
  breadcrumb?: boolean
  affix?: boolean
  activeMenu?: string
  roles?: string[]
  fullPath?: string  // 完整路径，用于菜单高亮
}

/**
 * 后端返回的路由数据格式
 */
export interface RouteData {
  path: string
  component?: string
  name?: string
  redirect?: string
  meta: {
    title: string
    icon?: string
    hidden?: boolean
    alwaysShow?: boolean
    roles?: string[]
  }
  children?: RouteData[]
}
