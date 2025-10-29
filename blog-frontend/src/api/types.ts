export interface PageResultVO<T> {
	total: number
	rows: T[]
	pageNum: number
	pageSize: number
	totalPages: number
}