<template>
  <div class="dashboard-container">
    <div class="header">
      <div>
        <p class="welcome">欢迎回来，{{ userStore.userName }}</p>
        <h2>数据看板</h2>
      </div>
    </div>

    <div class="metric-grid">
      <div class="metric-card primary">
        <p class="metric-label">总浏览</p>
        <p class="metric-value">{{ formatNumber(metrics.totalViews) }}</p>
      </div>
      <div class="metric-card accent">
        <p class="metric-label">点赞数</p>
        <p class="metric-value">{{ formatNumber(metrics.totalLikes) }}</p>
      </div>
      <div class="metric-card warning">
        <p class="metric-label">收藏数</p>
        <p class="metric-value">{{ formatNumber(metrics.totalFavorites) }}</p>
      </div>
      <div class="metric-card neutral">
        <p class="metric-label">发布文章</p>
        <p class="metric-value">{{ formatNumber(metrics.totalArticles) }}</p>
      </div>
    </div>

    <div class="grid">
      <el-card class="card" shadow="hover">
        <div class="card-title">
          <div>
            <p class="card-caption">浏览量最高的文章</p>
            <span>热门文章</span>
          </div>
          <el-tag type="success" effect="dark" round>Top {{ hotLimit }}</el-tag>
        </div>
        <el-table
          :data="hotArticles"
          height="360"
          v-loading="loading"
          border
          size="small"
          :row-class-name="rowClassName"
        >
          <el-table-column label="#" width="70">
            <template #default="{ $index }">
              <span :class="['rank-badge', 'rank-' + ($index + 1)]">{{ $index + 1 }}</span>
            </template>
          </el-table-column>
          <el-table-column label="标题" min-width="220">
            <template #default="{ row }">
              <div class="title-cell" :title="row.title">{{ row.title }}</div>
            </template>
          </el-table-column>
          <el-table-column label="浏览" min-width="140">
            <template #default="{ row }">
              <div class="metric-bar">
                <div class="metric-bar__value">{{ formatNumber(row.viewCount) }}</div>
                <div class="metric-bar__track">
                  <div
                    class="metric-bar__fill primary"
                    :style="{ width: percent(row.viewCount, maxView) + '%' }"
                  ></div>
                </div>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="点赞" width="100">
            <template #default="{ row }">{{ formatNumber(row.likeCount) }}</template>
          </el-table-column>
          <el-table-column label="收藏" width="100">
            <template #default="{ row }">{{ formatNumber(row.favoriteCount) }}</template>
          </el-table-column>
        </el-table>
      </el-card>

      <el-card class="card" shadow="hover">
        <div class="card-title">
          <div>
            <p class="card-caption">互动综合（点赞+收藏）</p>
            <span>互动排行榜</span>
          </div>
          <el-tag type="warning" effect="dark" round>Top {{ engagementLimit }}</el-tag>
        </div>
        <el-table
          :data="engagementRank"
          height="360"
          v-loading="loading"
          border
          size="small"
          :row-class-name="rowClassName"
        >
          <el-table-column label="#" width="70">
            <template #default="{ $index }">
              <span :class="['rank-badge', 'rank-' + ($index + 1)]">{{ $index + 1 }}</span>
            </template>
          </el-table-column>
          <el-table-column label="标题" min-width="220">
            <template #default="{ row }">
              <div class="title-cell" :title="row.title">{{ row.title }}</div>
            </template>
          </el-table-column>
          <el-table-column label="互动" min-width="140">
            <template #default="{ row }">
              <div class="metric-bar">
                <div class="metric-bar__value">{{ formatNumber(row.engagementScore || 0) }}</div>
                <div class="metric-bar__track">
                  <div
                    class="metric-bar__fill accent"
                    :style="{ width: percent(row.engagementScore || 0, maxEngagement) + '%' }"
                  ></div>
                </div>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="点赞" width="90">
            <template #default="{ row }">{{ formatNumber(row.likeCount) }}</template>
          </el-table-column>
          <el-table-column label="收藏" width="90">
            <template #default="{ row }">{{ formatNumber(row.favoriteCount) }}</template>
          </el-table-column>
        </el-table>
      </el-card>

      <el-card class="card" shadow="hover">
        <div class="card-title">
          <div>
            <p class="card-caption">评论数量最高</p>
            <span>评论排行榜</span>
          </div>
          <el-tag type="info" effect="dark" round>Top {{ commentLimit }}</el-tag>
        </div>
        <el-table
          :data="commentRank"
          height="360"
          v-loading="loading"
          border
          size="small"
          :row-class-name="rowClassName"
        >
          <el-table-column label="#" width="70">
            <template #default="{ $index }">
              <span :class="['rank-badge', 'rank-' + ($index + 1)]">{{ $index + 1 }}</span>
            </template>
          </el-table-column>
          <el-table-column label="标题" min-width="200">
            <template #default="{ row }">
              <div class="title-cell" :title="row.title">{{ row.title }}</div>
            </template>
          </el-table-column>
          <el-table-column label="评论" min-width="140">
            <template #default="{ row }">
              <div class="metric-bar">
                <div class="metric-bar__value">{{ formatNumber(row.commentCount || 0) }}</div>
                <div class="metric-bar__track">
                  <div
                    class="metric-bar__fill warning"
                    :style="{ width: percent(row.commentCount || 0, maxComment) + '%' }"
                  ></div>
                </div>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="点赞" width="90">
            <template #default="{ row }">{{ formatNumber(row.likeCount) }}</template>
          </el-table-column>
          <el-table-column label="收藏" width="90">
            <template #default="{ row }">{{ formatNumber(row.favoriteCount) }}</template>
          </el-table-column>
        </el-table>
      </el-card>

      <el-card class="card card-wide" shadow="hover">
        <div class="card-title">
          <div>
            <p class="card-caption">分类浏览占比</p>
            <span>分类分布</span>
          </div>
          <el-tag type="info" effect="plain" round>前 {{ categoryLimit }}</el-tag>
        </div>
        <div class="category-section">
          <div class="donut" :style="{ backgroundImage: categoryGradient() }"></div>
          <div class="legend">
            <div
              v-for="(item, idx) in categoryShare"
              :key="item.name"
              class="legend-item"
            >
              <span class="legend-dot" :style="{ background: palette[idx % palette.length] }"></span>
              <span class="legend-name">{{ item.name }}</span>
              <span class="legend-value">{{ formatNumber(item.value) }}</span>
            </div>
            <div v-if="!categoryShare.length" class="legend-empty">暂无数据</div>
          </div>
        </div>
      </el-card>

      <el-card class="card card-wide" shadow="hover">
        <div class="card-title">
          <div>
            <p class="card-caption">高频标签，字号按使用次数缩放</p>
            <span>标签云</span>
          </div>
          <el-tag type="success" effect="plain" round>Top {{ tagLimit }}</el-tag>
        </div>
        <div class="tag-cloud">
          <span
            v-for="(item, idx) in tagCloud"
            :key="item.tagId"
            class="tag-cloud__item"
            :style="tagStyle(item.value, idx)"
          >
            {{ item.name }}
          </span>
          <div v-if="!tagCloud.length" class="legend-empty">暂无数据</div>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useUserStore } from '@/stores/user'
import { analyticsApi } from '@/api'
import type { ArticleRankItem, CategoryStatItem, TagStatItem } from '@/types'

const userStore = useUserStore()
const hotArticles = ref<ArticleRankItem[]>([])
const engagementRank = ref<ArticleRankItem[]>([])
const commentRank = ref<ArticleRankItem[]>([])
const categoryShare = ref<CategoryStatItem[]>([])
const tagCloud = ref<TagStatItem[]>([])
const loading = ref(false)
const hotLimit = 10
const engagementLimit = 10
const commentLimit = 10
const categoryLimit = 6
const tagLimit = 20

const metrics = ref({
  totalViews: 0,
  totalLikes: 0,
  totalFavorites: 0,
  totalArticles: 0
})
const maxView = ref(1)
const maxEngagement = ref(1)
const maxComment = ref(1)

const loadData = async () => {
  loading.value = true
  try {
    const [hot, engagement, comments, overview, tags] = await Promise.all([
      analyticsApi.getHotArticles(hotLimit),
      analyticsApi.getEngagementRank(engagementLimit),
      analyticsApi.getCommentRank(commentLimit),
      analyticsApi.getOverview(categoryLimit),
      analyticsApi.getTagCloud(tagLimit)
    ])
    hotArticles.value = hot || []
    engagementRank.value = engagement || []
    commentRank.value = comments || []
    tagCloud.value = tags || []
    maxView.value = Math.max(...(hotArticles.value.map(i => i.viewCount || 0)), 1)
    maxEngagement.value = Math.max(...(engagementRank.value.map(i => i.engagementScore || 0)), 1)
    maxComment.value = Math.max(...(commentRank.value.map(i => i.commentCount || 0)), 1)
    metrics.value = {
      totalViews: overview?.totalViews || 0,
      totalLikes: overview?.totalLikes || 0,
      totalFavorites: overview?.totalFavorites || 0,
      totalArticles: overview?.totalArticles || 0
    }
    categoryShare.value = overview?.categoryViewShare || []
  } catch (error) {
    console.error('加载数据看板失败', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadData()
})

const formatNumber = (val: number | undefined) => {
  if (val === undefined || val === null) return 0
  return Number(val).toLocaleString('zh-CN')
}

const percent = (val: number | undefined, max: number) => {
  if (!val || max === 0) return 0
  return Math.min(100, Math.round((val / max) * 100))
}

const rowClassName = (_: any, index: number) => {
  if (index === 0) return 'row-first'
  if (index === 1) return 'row-second'
  if (index === 2) return 'row-third'
  return ''
}

const palette = ['#50ccd5', '#7aa2ff', '#f59e0b', '#10b981', '#f43f5e', '#9f7aea']
const tagPalette = ['#7aa2ff', '#50ccd5', '#f59e0b', '#f43f5e', '#9f7aea', '#10b981', '#fb7185', '#a855f7']

const categoryGradient = () => {
  const total = categoryShare.value.reduce((sum, item) => sum + (item.value || 0), 0)
  if (total === 0) return 'conic-gradient(#e5e7eb 0deg 360deg)'
  let start = 0
  const segments: string[] = []
  categoryShare.value.forEach((item, idx) => {
    const pct = (item.value / total) * 360
    const end = start + pct
    segments.push(`${palette[idx % palette.length]} ${start}deg ${end}deg`)
    start = end
  })
  return `conic-gradient(${segments.join(',')})`
}

const tagStyle = (value: number, idx: number) => {
  const maxVal = Math.max(...(tagCloud.value.map(i => i.value || 0)), 1)
  const minVal = Math.max(Math.min(...(tagCloud.value.map(i => i.value || 0)), maxVal), 1)
  // 16px ~ 30px 之间
  const size = 16 + ((value - minVal) / (maxVal - minVal || 1)) * 14
  const color = tagPalette[idx % tagPalette.length]
  return {
    fontSize: `${size}px`,
    color,
    borderColor: color
  }
}
</script>

<style scoped>
.dashboard-container {
  padding: 20px;
}

.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
}

.welcome {
  color: #909399;
  margin: 0;
}

h2 {
  margin: 4px 0 0;
}

.grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(320px, 1fr));
  gap: 16px;
}

.card-title {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  margin-bottom: 12px;
  font-weight: 600;
}

.card-title small {
  color: #909399;
  font-weight: normal;
}

.card-wide {
  grid-column: 1 / -1;
}

.card-caption {
  margin: 0;
  font-size: 12px;
  color: #909399;
}

.title-cell {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.rank-badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 24px;
  border-radius: 12px;
  background: #e5e7eb;
  color: #303133;
  font-weight: 700;
}

.rank-1 {
  background: linear-gradient(135deg, #f59e0b, #f97316);
  color: #fff;
}
.rank-2 {
  background: linear-gradient(135deg, #9ca3af, #6b7280);
  color: #fff;
}
.rank-3 {
  background: linear-gradient(135deg, #fbbf24, #f59e0b);
  color: #fff;
}

.metric-bar {
  display: flex;
  align-items: center;
  gap: 8px;
}

.metric-bar__value {
  min-width: 64px;
  text-align: right;
  font-variant-numeric: tabular-nums;
  color: #303133;
}

.metric-bar__track {
  flex: 1;
  height: 8px;
  border-radius: 999px;
  background: #f2f3f5;
  overflow: hidden;
}

.metric-bar__fill {
  height: 100%;
  border-radius: 999px;
}

.metric-bar__fill.primary { background: linear-gradient(90deg, #7aa2ff, #50ccd5); }
.metric-bar__fill.accent { background: linear-gradient(90deg, #10b981, #34d399); }
.metric-bar__fill.warning { background: linear-gradient(90deg, #f59e0b, #f97316); }

.row-first td {
  background: rgba(245, 158, 11, 0.08) !important;
}
.row-second td {
  background: rgba(156, 163, 175, 0.08) !important;
}
.row-third td {
  background: rgba(251, 191, 36, 0.06) !important;
}

.metric-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
  gap: 12px;
  margin-bottom: 16px;
}

.metric-card {
  padding: 14px 16px;
  border-radius: 12px;
  color: #fff;
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.08);
}

.metric-card .metric-label {
  margin: 0;
  font-size: 13px;
  opacity: 0.85;
}

.metric-card .metric-value {
  margin: 4px 0 0;
  font-size: 26px;
  font-weight: 700;
}

.metric-card.primary { background: linear-gradient(135deg, #4f7cff, #50ccd5); }
.metric-card.accent { background: linear-gradient(135deg, #10b981, #34d399); }
.metric-card.warning { background: linear-gradient(135deg, #f59e0b, #f97316); }
.metric-card.neutral { background: linear-gradient(135deg, #6b7280, #9ca3af); }

.category-section {
  display: grid;
  grid-template-columns: 240px 1fr;
  gap: 16px;
  align-items: center;
}

.donut {
  width: 220px;
  height: 220px;
  border-radius: 50%;
  background: conic-gradient(#e5e7eb 0deg 360deg);
  position: relative;
  margin: 0 auto;
}

.donut::after {
  content: '';
  position: absolute;
  inset: 30px;
  background: #fff;
  border-radius: 50%;
  box-shadow: inset 0 0 0 1px #f2f3f5;
}

.legend {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
  gap: 10px 16px;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
}

.legend-dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  display: inline-block;
}

.legend-name {
  flex: 1;
  color: #303133;
}

.legend-value {
  color: #606266;
  font-variant-numeric: tabular-nums;
}

.legend-empty {
  color: #909399;
}

.tag-cloud {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  min-height: 80px;
}

.tag-cloud__item {
  padding: 6px 10px;
  border-radius: 12px;
  border: 1px solid #e5e7eb;
  background: #f8fafc;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
  line-height: 1;
}

.tag-cloud__item:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.06);
}
</style>
