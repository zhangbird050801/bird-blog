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
  roles?: RoleItem[]
  permissions?: string[]
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
  status?: number
  roles?: RoleItem[]
}

/**
 * 角色信息
 */
export interface RoleItem {
  id: number
  name: string
  code: string
  description?: string | null
  status: number
  createTime?: string
  updateTime?: string
}

/**
 * 角色查询参数
 */
export interface RoleQueryParams {
  pageNum?: number
  pageSize?: number
  keyword?: string
  status?: number
}

/**
 * 文章排行数据
 */
export interface ArticleRankItem {
  articleId: number
  title: string
  slug: string
  viewCount: number
  likeCount: number
  favoriteCount: number
  engagementScore?: number
  publishedTime?: string
}

export interface CategoryStatItem {
  name: string
  value: number
}

export interface TagStatItem {
  tagId: number
  name: string
  value: number
}

/**
 * 创建角色请求参数
 */
export interface CreateRoleRequest {
  name: string
  code: string
  description?: string
  status: number
}

/**
 * 更新角色请求参数
 */
export interface UpdateRoleRequest {
  name: string
  description?: string
  status: number
}

/**
 * 后台用户表格项
 */
export interface AdminUserItem {
  id: number
  username: string
  nickName: string
  email?: string | null
  phone?: string | null
  status: number
  sex?: number | null
  avatar?: string | null
  createTime?: string
  updateTime?: string
  roles?: RoleItem[]
}

/**
 * 用户查询参数
 */
export interface UserQueryParams {
  pageNum?: number
  pageSize?: number
  keyword?: string
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
  createTime?: string
  updateTime?: string
  children?: MenuItem[]
}

/**
 * 菜单表单数据
 */
export interface MenuFormData {
  id?: number
  name: string
  parentId: number | null
  sort: number
  path: string
  component?: string
  type: 'M' | 'C' | 'F'
  visible: number
  status: number
  perm?: string
  icon?: string
  remark?: string
}

/**
 * 菜单下拉选项
 */
export interface MenuOption {
  value: number
  label: string
  children?: MenuOption[]
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

/**
 * 分页结果
 */
export interface PageResult<T = any> {
  total: number
  rows: T[]
  pageNum: number
  pageSize: number
  totalPages: number
}

/**
 * 分类信息
 */
export interface Category {
  id: number
  name: string
  description?: string | null
  status: number
  count?: number
  creator?: string
  createTime?: string
  updater?: string
  updateTime?: string
  deleted?: boolean
}

/**
 * 分类查询参数
 */
export interface CategoryQueryParams {
  pageNum?: number
  pageSize?: number
  name?: string
  status?: number
}

/**
 * 标签信息
 */
export interface Tag {
  id: number
  name: string
  remark?: string | null
  creator?: string
  createTime?: string
  updater?: string
  updateTime?: string
  deleted?: boolean
}

/**
 * 标签查询参数
 */
export interface TagQueryParams {
  pageNum?: number
  pageSize?: number
  name?: string
}

/**
 * 友链信息
 */
export interface Link {
  id: number
  name: string
  logo?: string | null
  description?: string | null
  url: string
  status: number // 0通过 1未通过 2待审核
  creator?: string
  createTime?: string
  updater?: string
  updateTime?: string
  deleted?: boolean
}

/**
 * 友链查询参数
 */
export interface LinkQueryParams {
  pageNum?: number
  pageSize?: number
  name?: string
  status?: number
}

/**
 * 评论信息
 */
export interface Comment {
  id: number
  type: number // 0文章评论，1友链评论
  articleId?: number | null
  rootId?: number | null
  parentId?: number | null
  fromUserId?: number | null
  fromUserName?: string
  fromUserAvatar?: string
  toUserId?: number | null
  toUserName?: string
  content: string
  status: number // 0正常，1屏蔽
  likeCount?: number
  creator?: string
  createTime?: string
  updater?: string
  updateTime?: string
  deleted?: boolean
}

/**
 * 评论查询参数
 */
export interface CommentQueryParams {
  pageNum?: number
  pageSize?: number
  type?: number
  articleId?: number
  status?: number
  content?: string
  objectKeyword?: string
}
