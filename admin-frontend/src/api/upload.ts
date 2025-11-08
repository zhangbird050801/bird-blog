import http from '@/api/http'

export interface UploadResponse {
  url: string
  key: string
  fileName: string
}

/**
 * 上传图片
 * @param file 图片文件
 * @param businessName 业务名称
 */
export function uploadImage(file: File, businessName?: string): Promise<UploadResponse> {
  const formData = new FormData()
  formData.append('file', file)
  if (businessName) {
    formData.append('businessName', businessName)
  }

  return http.post('/admin/upload/image', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

/**
 * 上传缩略图
 * @param file 图片文件
 */
export function uploadThumbnail(file: File): Promise<UploadResponse> {
  const formData = new FormData()
  formData.append('file', file)

  return http.post('/admin/upload/thumbnail', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}