<template>
  <footer class="footer">
    <div class="footer__content">
      <div>
        <h2>BirdBlog Studio</h2>
        <p>
          记录设计、前端与质感交互的灵感。以 BirdBlog 哲学打造沉浸式阅读体验。
        </p>
      </div>
      <div class="footer__links">
        <div>
          <h3>快速导航</h3>
          <ul>
            <li><a href="/">首页</a></li>
            <li><a href="/links">友链</a></li>
            <li><a href="https://www.birdyweb.top" target="_blank" rel="noopener noreferrer">关于</a></li>
          </ul>
        </div>
        <div>
          <h3>联系</h3>
          <ul>
            <li><a href="mailto:zhangbird050801@gmail.com">zhangbird050801@gmail.com</a></li>
            <li><a href="https://www.birdyweb.top" target="_blank" rel="noopener noreferrer">www.birdyweb.top</a></li>
          </ul>
        </div>
      </div>
    </div>
    <div class="footer__meta">
      <span>© {{ new Date().getFullYear() }} BirdBlog. 保留所有权利。</span>
      <span>由 Birdy 设计与开发</span>
    </div>
  </footer>
</template>

<style scoped>
.footer {
  width: 100%;
  margin-top: clamp(64px, 8vw, 120px);
  padding: clamp(48px, 6vw, 72px) clamp(32px, 8vw, 120px);
  display: flex;
  flex-direction: column;
  gap: clamp(32px, 4vw, 48px);
  background: linear-gradient(135deg, rgba(40, 42, 44, 0.4) 0%, rgba(26, 26, 46, 0.6) 100%);
  backdrop-filter: blur(8px);
  border-top: 1px solid rgba(255, 255, 255, 0.08);
}

body.dark .footer {
  background: linear-gradient(135deg, rgba(26, 26, 46, 0.6) 0%, rgba(18, 18, 32, 0.8) 100%);
  border-top-color: rgba(255, 255, 255, 0.05);
}

.footer__content {
  display: grid;
  grid-template-columns: 1fr auto;
  gap: clamp(32px, 4vw, 64px);
  align-items: start;
}

.footer__content > div:first-child {
  max-width: 480px;
}

.footer__links {
  display: flex;
  gap: clamp(32px, 5vw, 64px);
  flex-wrap: wrap;
}

.footer ul {
  list-style: none;
  padding: 0;
  margin: 0;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.footer ul li a {
  color: rgba(255, 255, 255, 0.75);
  font-size: 14px;
  transition: all 0.2s ease;
  display: inline-block;
  position: relative;
  padding-left: 0;
}

.footer ul li a::before {
  content: '';
  position: absolute;
  left: 0;
  bottom: -2px;
  width: 0;
  height: 2px;
  background: var(--sg-primary);
  transition: width 0.3s ease;
}

.footer ul li a:hover {
  color: #fff;
  padding-left: 8px;
}

.footer ul li a:hover::before {
  width: calc(100% - 8px);
}

.footer__meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 16px;
  padding-top: clamp(24px, 3vw, 32px);
  border-top: 1px solid rgba(255, 255, 255, 0.08);
  font-size: 13px;
  color: rgba(255, 255, 255, 0.5);
}

body.dark .footer__meta {
  border-top-color: rgba(255, 255, 255, 0.05);
}

.footer h2 {
  margin: 0 0 12px;
  font-size: clamp(24px, 3vw, 32px);
  font-weight: 700;
  background: linear-gradient(135deg, #fff 0%, rgba(255, 255, 255, 0.8) 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.footer h2 + p {
  color: rgba(255, 255, 255, 0.65);
  font-size: 15px;
  line-height: 1.6;
  margin: 0;
}

.footer h3 {
  margin-bottom: 16px;
  font-size: 13px;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.15em;
  color: rgba(255, 255, 255, 0.9);
}

/* 响应式优化 */
@media (max-width: 768px) {
  .footer {
    padding: clamp(32px, 6vw, 48px) clamp(20px, 5vw, 32px);
    gap: clamp(24px, 3vw, 32px);
  }

  .footer__content {
    grid-template-columns: 1fr;
    gap: clamp(24px, 3vw, 32px);
  }

  .footer__links {
    gap: clamp(24px, 4vw, 32px);
  }

  .footer__meta {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
}
</style>
