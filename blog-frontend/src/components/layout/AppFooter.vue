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
            <li><a href="/#archive">归档</a></li>
            <li><a href="/#about">关于</a></li>
          </ul>
        </div>
        <div>
          <h3>联系</h3>
          <ul>
            <li><a href="mailto:hello@BirdBlog.design">hello@BirdBlog.design</a></li>
            <li><a href="https://birdyweb.top" target="_blank" rel="noopener">birdyweb.top</a></li>
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
  margin-top: clamp(56px, 7vw, 96px);
  padding: clamp(32px, 5vw, 52px) clamp(24px, 8vw, 120px);
  display: flex;
  flex-direction: column;
  gap: clamp(20px, 3vw, 32px);
}

.footer__content {
  display: grid;
  gap: clamp(16px, 2vw, 32px);
}

.footer__links {
  display: flex;
  gap: clamp(24px, 4vw, 48px);
  flex-wrap: wrap;
}

.footer ul {
  list-style: none;
  padding: 0;
  margin: 0;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.footer__meta {
  display: flex;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: 12px;
  font-size: 13px;
  color: var(--lg-text-secondary);
}

.footer h2 {
  margin: 0 0 8px;
  font-size: clamp(20px, 2.6vw, 28px);
}

.footer h3 {
  margin-bottom: 10px;
  font-size: 14px;
  text-transform: uppercase;
  letter-spacing: 0.12em;
}
</style>
