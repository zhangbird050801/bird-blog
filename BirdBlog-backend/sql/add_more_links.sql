-- 添加更多友链数据
-- 执行日期：2025-10-30
-- 使用Google Favicon服务自动获取网站logo

-- 插入新的友链数据（使用真实可用的logo URL）
INSERT INTO `bg_link` (`id`, `name`, `logo`, `description`, `url`, `status`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES 
(9008, 'Vue.js', 'https://www.google.com/s2/favicons?sz=64&domain=vuejs.org', 'Vue.js 官方文档', 'https://vuejs.org', 0, 'admin', NOW(), 'admin', NOW(), b'0'),
(9009, 'React', 'https://www.google.com/s2/favicons?sz=64&domain=react.dev', 'React 官方文档', 'https://react.dev', 0, 'admin', NOW(), 'admin', NOW(), b'0'),
(9010, 'TypeScript', 'https://www.google.com/s2/favicons?sz=64&domain=typescriptlang.org', 'TypeScript 官方文档', 'https://www.typescriptlang.org', 0, 'admin', NOW(), 'admin', NOW(), b'0'),
(9011, 'Node.js', 'https://www.google.com/s2/favicons?sz=64&domain=nodejs.org', 'Node.js 官方网站', 'https://nodejs.org', 0, 'admin', NOW(), 'admin', NOW(), b'0'),
(9012, 'MDN Web Docs', 'https://www.google.com/s2/favicons?sz=64&domain=developer.mozilla.org', 'Web 开发文档', 'https://developer.mozilla.org', 0, 'admin', NOW(), 'admin', NOW(), b'0'),
(9013, 'Docker Hub', 'https://www.google.com/s2/favicons?sz=64&domain=hub.docker.com', 'Docker 镜像仓库', 'https://hub.docker.com', 0, 'admin', NOW(), 'admin', NOW(), b'0'),
(9014, 'Redis', 'https://www.google.com/s2/favicons?sz=64&domain=redis.io', 'Redis 官方文档', 'https://redis.io', 0, 'admin', NOW(), 'admin', NOW(), b'0'),
(9015, 'MongoDB', 'https://www.google.com/s2/favicons?sz=64&domain=mongodb.com', 'MongoDB 官方网站', 'https://www.mongodb.com', 0, 'admin', NOW(), 'admin', NOW(), b'0'),
(9016, 'PostgreSQL', 'https://www.google.com/s2/favicons?sz=64&domain=postgresql.org', 'PostgreSQL 数据库', 'https://www.postgresql.org', 0, 'admin', NOW(), 'admin', NOW(), b'0'),
(9017, 'Nginx', 'https://www.google.com/s2/favicons?sz=64&domain=nginx.org', 'Nginx 官方网站', 'https://nginx.org', 0, 'admin', NOW(), 'admin', NOW(), b'0'),
(9018, 'Kubernetes', 'https://www.google.com/s2/favicons?sz=64&domain=kubernetes.io', 'Kubernetes 容器编排', 'https://kubernetes.io', 0, 'admin', NOW(), 'admin', NOW(), b'0'),
(9019, 'Git', 'https://www.google.com/s2/favicons?sz=64&domain=git-scm.com', 'Git 版本控制', 'https://git-scm.com', 0, 'admin', NOW(), 'admin', NOW(), b'0'),
(9020, 'VS Code', 'https://www.google.com/s2/favicons?sz=64&domain=code.visualstudio.com', 'Visual Studio Code', 'https://code.visualstudio.com', 0, 'admin', NOW(), 'admin', NOW(), b'0'),
(9021, 'JetBrains', 'https://www.google.com/s2/favicons?sz=64&domain=jetbrains.com', 'JetBrains IDE工具', 'https://www.jetbrains.com', 0, 'admin', NOW(), 'admin', NOW(), b'0'),
(9022, 'LeetCode', 'https://www.google.com/s2/favicons?sz=64&domain=leetcode.cn', '力扣算法题库', 'https://leetcode.cn', 0, 'admin', NOW(), 'admin', NOW(), b'0'),
(9023, 'Gitee', 'https://www.google.com/s2/favicons?sz=64&domain=gitee.com', 'Gitee 代码托管', 'https://gitee.com', 0, 'admin', NOW(), 'admin', NOW(), b'0'),
(9024, 'CSDN', 'https://www.google.com/s2/favicons?sz=64&domain=csdn.net', 'CSDN 技术社区', 'https://www.csdn.net', 0, 'admin', NOW(), 'admin', NOW(), b'0'),
(9025, '博客园', 'https://www.google.com/s2/favicons?sz=64&domain=cnblogs.com', '博客园开发者社区', 'https://www.cnblogs.com', 0, 'admin', NOW(), 'admin', NOW(), b'0'),
(9026, 'SegmentFault', 'https://www.google.com/s2/favicons?sz=64&domain=segmentfault.com', '思否技术社区', 'https://segmentfault.com', 0, 'admin', NOW(), 'admin', NOW(), b'0'),
(9027, 'V2EX', 'https://www.google.com/s2/favicons?sz=64&domain=v2ex.com', 'V2EX 创意工作者社区', 'https://www.v2ex.com', 0, 'admin', NOW(), 'admin', NOW(), b'0'),
(9028, 'Vite', 'https://www.google.com/s2/favicons?sz=64&domain=vitejs.dev', 'Vite 构建工具', 'https://vitejs.dev', 0, 'admin', NOW(), 'admin', NOW(), b'0'),
(9029, 'Tailwind CSS', 'https://www.google.com/s2/favicons?sz=64&domain=tailwindcss.com', 'Tailwind CSS框架', 'https://tailwindcss.com', 0, 'admin', NOW(), 'admin', NOW(), b'0'),
(9030, 'Element Plus', 'https://www.google.com/s2/favicons?sz=64&domain=element-plus.org', 'Element Plus组件库', 'https://element-plus.org', 0, 'admin', NOW(), 'admin', NOW(), b'0'),
(9031, 'Ant Design', 'https://www.google.com/s2/favicons?sz=64&domain=ant.design', 'Ant Design组件库', 'https://ant.design', 0, 'admin', NOW(), 'admin', NOW(), b'0'),
(9032, 'Webpack', 'https://www.google.com/s2/favicons?sz=64&domain=webpack.js.org', 'Webpack 打包工具', 'https://webpack.js.org', 0, 'admin', NOW(), 'admin', NOW(), b'0');

-- 同时更新原有的7个友链的logo为Google Favicon服务
UPDATE `bg_link` SET `logo` = 'https://www.google.com/s2/favicons?sz=64&domain=spring.io' WHERE `id` = 9001;
UPDATE `bg_link` SET `logo` = 'https://www.google.com/s2/favicons?sz=64&domain=dev.mysql.com' WHERE `id` = 9002;
UPDATE `bg_link` SET `logo` = 'https://www.google.com/s2/favicons?sz=64&domain=github.com' WHERE `id` = 9003;
UPDATE `bg_link` SET `logo` = 'https://www.google.com/s2/favicons?sz=64&domain=www.oschina.net' WHERE `id` = 9004;
UPDATE `bg_link` SET `logo` = 'https://www.google.com/s2/favicons?sz=64&domain=www.infoq.cn' WHERE `id` = 9005;
UPDATE `bg_link` SET `logo` = 'https://www.google.com/s2/favicons?sz=64&domain=juejin.cn' WHERE `id` = 9006;
UPDATE `bg_link` SET `logo` = 'https://www.google.com/s2/favicons?sz=64&domain=stackoverflow.com' WHERE `id` = 9007;

-- 验证插入结果
SELECT COUNT(*) as '友链总数' FROM `bg_link` WHERE `status` = 0 AND `deleted` = b'0';
SELECT id, name, description, url FROM `bg_link` WHERE `status` = 0 AND `deleted` = b'0' ORDER BY id;

