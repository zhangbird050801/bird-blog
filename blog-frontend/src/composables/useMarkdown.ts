import MarkdownIt from 'markdown-it'
import hljs from 'highlight.js'

/**
 * 创建并配置 Markdown 解析器实例
 */
export function useMarkdown() {
  const md: MarkdownIt = new MarkdownIt({
    html: true, // 允许在 Markdown 中使用 HTML 标签
    linkify: true, // 自动将 URL 转换为链接
    typographer: true, // 启用智能排版（引号、破折号等）
    breaks: false, // 将换行符转换为 <br>（设为 false 遵循标准 Markdown）
    
    // 代码高亮配置
    highlight: (str: string, lang: string): string => {
      // 如果指定了语言且 highlight.js 支持该语言
      if (lang && hljs.getLanguage(lang)) {
        try {
          // 使用 highlight.js 进行高亮
          const highlighted = hljs.highlight(str, {
            language: lang,
            ignoreIllegals: true
          }).value
          
          // 返回带行号的代码块
          return `<pre class="hljs"><code class="language-${lang}">${highlighted}</code></pre>`
        } catch (error) {
          console.error('代码高亮失败:', error)
        }
      }
      
      // 如果没有指定语言或高亮失败，返回原始代码
      return `<pre class="hljs"><code>${md.utils.escapeHtml(str)}</code></pre>`
    }
  })

  /**
   * 将 Markdown 文本渲染为 HTML
   * @param content Markdown 格式的文本
   * @returns 渲染后的 HTML 字符串
   */
  const render = (content: string): string => {
    if (!content) return ''
    return md.render(content)
  }

  return {
    render
  }
}
