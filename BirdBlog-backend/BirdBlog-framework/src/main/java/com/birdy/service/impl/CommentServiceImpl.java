package com.birdy.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.birdy.constants.SysConstants;
import com.birdy.domain.CommonResult;
import com.birdy.domain.dto.AddCommentRequest;
import com.birdy.domain.entity.Comment;
import com.birdy.domain.entity.User;
import com.birdy.domain.entity.Article;
import com.birdy.domain.entity.Link;
import com.birdy.domain.vo.CommentVO;
import com.birdy.domain.vo.PageResult;
import com.birdy.enums.HttpCodeEnum;
import com.birdy.mapper.CommentMapper;
import com.birdy.mapper.UserMapper;
import com.birdy.mapper.ArticleMapper;
import com.birdy.mapper.LinkMapper;
import com.birdy.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
* @author Young
* @description 针对表【bg_comment(评论表)】的数据库操作Service实现
* @createDate 2025-10-31 02:34:32
*/
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private LinkMapper linkMapper;

    @Override
    public CommonResult<List<CommentVO>> getArticleComments(Long articleId) {
        // 1. 查询文章的所有评论
        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Comment::getArticleId, articleId)
                .eq(Comment::getType, SysConstants.COMMENT_TYPE_ARTICLE)
                .eq(Comment::getStatus, SysConstants.COMMENT_STATUS_NORMAL)
                .eq(Comment::getDeleted, false)
                .orderByAsc(Comment::getCreateTime);

        List<Comment> comments = list(queryWrapper);

        if (comments.isEmpty()) {
            return CommonResult.success(new ArrayList<>());
        }

        // 2. 获取所有评论用户ID
        List<Long> userIds = comments.stream()
                .flatMap(c -> java.util.stream.Stream.of(c.getFromUserId(), c.getToUserId()))
                .filter(java.util.Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

        // 3. 查询用户信息
        Map<Long, User> userMap = userMapper.selectBatchIds(userIds).stream()
                .collect(Collectors.toMap(User::getId, u -> u));

        // 4. 转换为CommentVO并构建树形结构
        List<CommentVO> commentVOList = comments.stream()
                .map(comment -> toCommentVO(comment, userMap))
                .collect(Collectors.toList());

        // 5. 构建树形结构
        List<CommentVO> rootComments = commentVOList.stream()
                .filter(c -> c.getRootId() == null)
                .collect(Collectors.toList());

        // 6. 为每个根评论添加子评论
        for (CommentVO rootComment : rootComments) {
            List<CommentVO> children = commentVOList.stream()
                    .filter(c -> c.getRootId() != null && c.getRootId().equals(rootComment.getId()))
                    .collect(Collectors.toList());
            rootComment.setChildren(children);
        }

        return CommonResult.success(rootComments);
    }

    @Override
    public CommonResult<List<CommentVO>> getLinkComments(Long linkId) {
        // 1. 查询友链的所有评论
        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Comment::getLinkId, linkId)
                .eq(Comment::getType, SysConstants.COMMENT_TYPE_LINK)
                .eq(Comment::getStatus, SysConstants.COMMENT_STATUS_NORMAL)
                .eq(Comment::getDeleted, false)
                .orderByAsc(Comment::getCreateTime);

        List<Comment> comments = list(queryWrapper);

        if (comments.isEmpty()) {
            return CommonResult.success(new ArrayList<>());
        }

        // 2. 获取所有评论用户ID
        List<Long> userIds = comments.stream()
                .flatMap(c -> java.util.stream.Stream.of(c.getFromUserId(), c.getToUserId()))
                .filter(java.util.Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

        // 3. 查询用户信息
        Map<Long, User> userMap = userMapper.selectBatchIds(userIds).stream()
                .collect(Collectors.toMap(User::getId, u -> u));

        // 4. 转换为CommentVO并构建树形结构
        List<CommentVO> commentVOList = comments.stream()
                .map(comment -> toCommentVO(comment, userMap))
                .collect(Collectors.toList());

        // 5. 构建树形结构
        List<CommentVO> rootComments = commentVOList.stream()
                .filter(c -> c.getRootId() == null)
                .collect(Collectors.toList());

        // 6. 为每个根评论添加子评论
        for (CommentVO rootComment : rootComments) {
            List<CommentVO> children = commentVOList.stream()
                    .filter(c -> c.getRootId() != null && c.getRootId().equals(rootComment.getId()))
                    .collect(Collectors.toList());
            rootComment.setChildren(children);
        }

        return CommonResult.success(rootComments);
    }

    @Override
    public CommonResult<Void> addComment(AddCommentRequest request, Long userId) {
        // 1. 参数校验
        if (!StringUtils.hasText(request.getContent())) {
            return CommonResult.error(HttpCodeEnum.SYSTEM_ERROR, "评论内容不能为空");
        }
        if (request.getContent().length() > 512) {
            return CommonResult.error(HttpCodeEnum.SYSTEM_ERROR, "评论内容不能超过512个字符");
        }

        // 判断评论类型：文章评论或友链评论
        Integer commentType;
        if (request.getLinkId() != null) {
            // 友链评论
            commentType = SysConstants.COMMENT_TYPE_LINK;
        } else if (request.getArticleId() != null) {
            // 文章评论
            commentType = SysConstants.COMMENT_TYPE_ARTICLE;
        } else {
            return CommonResult.error(HttpCodeEnum.SYSTEM_ERROR, "文章ID或友链ID不能为空");
        }

        // 2. 创建评论实体
        Comment comment = new Comment();
        comment.setType(commentType);
        comment.setArticleId(request.getArticleId());
        comment.setLinkId(request.getLinkId());
        comment.setRootId(request.getRootId());
        comment.setParentId(request.getParentId());
        comment.setFromUserId(userId);
        comment.setToUserId(request.getToUserId());
        comment.setContent(request.getContent());
        comment.setStatus(SysConstants.COMMENT_STATUS_NORMAL);
        comment.setLikeCount(0L);
        comment.setDeleted(false);

        // 3. 保存评论
        save(comment);

        return CommonResult.success();
    }

    @Override
    public PageResult<CommentVO> getPageList(Integer pageNum, Integer pageSize, Integer type, Long articleId, Integer status, String content, String objectKeyword) {
        // 创建分页对象
        Page<Comment> page = new Page<>(pageNum, pageSize);

        // 构建查询条件
        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.and(wrapper -> wrapper.isNull(Comment::getDeleted)
                                .or().eq(Comment::getDeleted, false)
                                .or().eq(Comment::getDeleted, 0));

        // 评论类型筛选
        if (type != null) {
            queryWrapper.eq(Comment::getType, type);
        }

        // 文章ID或友链ID筛选
        if (articleId != null) {
            if (type != null) {
                // 如果指定了类型，则筛选对应类型的ID
                if (type != null && type == SysConstants.COMMENT_TYPE_ARTICLE) {
                    queryWrapper.eq(Comment::getArticleId, articleId);
                } else if (type != null && type == SysConstants.COMMENT_TYPE_LINK) {
                    queryWrapper.eq(Comment::getLinkId, articleId);
                }
            } else {
                // 如果没有指定类型，则筛选文章ID或友链ID
                queryWrapper.and(wrapper -> wrapper
                    .eq(Comment::getArticleId, articleId)
                    .or()
                    .eq(Comment::getLinkId, articleId)
                );
            }
        }

        // 关联对象关键词筛选（文章标题或友链名称模糊匹配）
        if (StringUtils.hasText(objectKeyword)) {
            String keyword = objectKeyword.trim();

            // 预先查询匹配的文章ID和友链ID
            LambdaQueryWrapper<Article> articleQuery = new LambdaQueryWrapper<>();
            articleQuery.select(Article::getId)
                    .like(Article::getTitle, keyword)
                    .eq(Article::getStatus, 0);

            List<Long> articleIds = articleMapper.selectList(articleQuery).stream()
                    .map(Article::getId)
                    .collect(Collectors.toList());

            LambdaQueryWrapper<Link> linkQuery = new LambdaQueryWrapper<>();
            linkQuery.select(Link::getId)
                    .like(Link::getName, keyword)
                    .eq(Link::getStatus, 0);

            List<Long> linkIds = linkMapper.selectList(linkQuery).stream()
                    .map(Link::getId)
                    .collect(Collectors.toList());

            // 根据评论类型筛选
            if (type != null && type == SysConstants.COMMENT_TYPE_ARTICLE) {
                // 只搜索文章评论
                if (!articleIds.isEmpty()) {
                    queryWrapper.in(Comment::getArticleId, articleIds);
                } else {
                    queryWrapper.eq(Comment::getId, -1L);
                }
            } else if (type != null && type == SysConstants.COMMENT_TYPE_LINK) {
                // 只搜索友链评论
                if (!linkIds.isEmpty()) {
                    queryWrapper.in(Comment::getLinkId, linkIds);
                } else {
                    queryWrapper.eq(Comment::getId, -1L);
                }
            } else {
                // 未指定类型，搜索文章和友链评论
                if (!articleIds.isEmpty() && !linkIds.isEmpty()) {
                    // 两个都有数据，使用嵌套查询
                    queryWrapper.and(wrapper ->
                        wrapper.and(w -> w.in(Comment::getArticleId, articleIds)
                                          .eq(Comment::getType, SysConstants.COMMENT_TYPE_ARTICLE))
                               .or(w -> w.in(Comment::getLinkId, linkIds)
                                          .eq(Comment::getType, SysConstants.COMMENT_TYPE_LINK))
                    );
                } else if (!articleIds.isEmpty()) {
                    // 只有文章数据
                    queryWrapper.in(Comment::getArticleId, articleIds)
                               .eq(Comment::getType, SysConstants.COMMENT_TYPE_ARTICLE);
                } else if (!linkIds.isEmpty()) {
                    // 只有友链数据
                    queryWrapper.in(Comment::getLinkId, linkIds)
                               .eq(Comment::getType, SysConstants.COMMENT_TYPE_LINK);
                } else {
                    // 如果没有匹配的文章或友链，返回空结果
                    queryWrapper.eq(Comment::getId, -1L);
                }
            }
        }

        // 状态筛选
        if (status != null) {
            queryWrapper.eq(Comment::getStatus, status);
        }

        // 内容模糊查询
        if (StringUtils.hasText(content)) {
            queryWrapper.like(Comment::getContent, content.trim());
        }

        // 按创建时间倒序
        queryWrapper.orderByDesc(Comment::getCreateTime);

        // 执行查询
        page(page, queryWrapper);

        List<Comment> comments = page.getRecords();
        List<CommentVO> commentVOList = new ArrayList<>();

        if (!comments.isEmpty()) {
            // 获取所有用户ID
            List<Long> userIds = comments.stream()
                    .flatMap(c -> Arrays.stream(new Long[]{c.getFromUserId(), c.getToUserId()}))
                    .filter(Objects::nonNull)
                    .distinct()
                    .collect(Collectors.toList());

            // 获取所有文章ID和友链ID
            List<Long> articleIds = comments.stream()
                    .map(Comment::getArticleId)
                    .filter(Objects::nonNull)
                    .distinct()
                    .collect(Collectors.toList());

            List<Long> linkIds = comments.stream()
                    .map(Comment::getLinkId)
                    .filter(Objects::nonNull)
                    .distinct()
                    .collect(Collectors.toList());

            // 查询用户信息
            final Map<Long, User> userMap;
            if (!userIds.isEmpty()) {
                userMap = userMapper.selectBatchIds(userIds).stream()
                        .collect(Collectors.toMap(User::getId, u -> u));
            } else {
                userMap = new HashMap<>();
            }

            // 查询文章信息
            final Map<Long, Article> articleMap;
            if (!articleIds.isEmpty()) {
                articleMap = articleMapper.selectBatchIds(articleIds).stream()
                        .collect(Collectors.toMap(Article::getId, a -> a));
            } else {
                articleMap = new HashMap<>();
            }

            // 查询友链信息
            final Map<Long, Link> linkMap;
            if (!linkIds.isEmpty()) {
                linkMap = linkMapper.selectBatchIds(linkIds).stream()
                        .collect(Collectors.toMap(Link::getId, l -> l));
            } else {
                linkMap = new HashMap<>();
            }

            // 转换为CommentVO并添加用户信息、文章和友链信息
            commentVOList = comments.stream()
                    .map(comment -> toCommentVOWithUserInfo(comment, userMap, articleMap, linkMap))
                    .collect(Collectors.toList());
        }

        // 封装返回结果
        return new PageResult<>(page.getTotal(), commentVOList,
                              (int) page.getCurrent(), (int) page.getSize());
    }

    @Override
    public CommonResult<Void> updateStatus(Long id, Integer status) {
        if (id == null) {
            return CommonResult.error(HttpCodeEnum.SYSTEM_ERROR, "评论ID不能为空");
        }
        if (status == null || (status != 0 && status != 1)) {
            return CommonResult.error(HttpCodeEnum.SYSTEM_ERROR, "状态值不正确");
        }

        Comment comment = getById(id);
        if (comment == null) {
            return CommonResult.error(HttpCodeEnum.SYSTEM_ERROR, "评论不存在");
        }

        comment.setStatus(status);
        boolean updated = updateById(comment);

        if (updated) {
            return CommonResult.success();
        } else {
            return CommonResult.error(HttpCodeEnum.SYSTEM_ERROR, "更新评论状态失败");
        }
    }

    @Override
    public CommonResult<Void> deleteComments(String ids) {
        if (!StringUtils.hasText(ids)) {
            return CommonResult.error(HttpCodeEnum.SYSTEM_ERROR, "评论ID不能为空");
        }

        try {
            List<Long> idList = Arrays.stream(ids.split(","))
                    .map(String::trim)
                    .filter(StringUtils::hasText)
                    .map(Long::parseLong)
                    .collect(Collectors.toList());

            if (idList.isEmpty()) {
                return CommonResult.error(HttpCodeEnum.SYSTEM_ERROR, "评论ID格式不正确");
            }

            // 批量软删除
            boolean deleted = lambdaUpdate()
                    .in(Comment::getId, idList)
                    .set(Comment::getDeleted, true)
                    .update();

            if (deleted) {
                return CommonResult.success();
            } else {
                return CommonResult.error(HttpCodeEnum.SYSTEM_ERROR, "删除评论失败");
            }
        } catch (NumberFormatException e) {
            return CommonResult.error(HttpCodeEnum.SYSTEM_ERROR, "评论ID格式不正确");
        }
    }

      /**
     * 将Comment实体转换为CommentVO（用于管理后台，包含完整信息）
     */
    private CommentVO toCommentVOWithUserInfo(Comment comment, Map<Long, User> userMap, Map<Long, Article> articleMap, Map<Long, Link> linkMap) {
        CommentVO vo = new CommentVO();
        BeanUtil.copyProperties(comment, vo);

        // 设置评论用户信息
        if (comment.getFromUserId() != null) {
            User fromUser = userMap.get(comment.getFromUserId());
            if (fromUser != null) {
                vo.setFromUserName(fromUser.getNickName());
                vo.setFromUserAvatar(fromUser.getAvatar());
            } else {
                vo.setFromUserName("用户" + comment.getFromUserId());
            }
        }

        // 设置被回复用户信息
        if (comment.getToUserId() != null) {
            User toUser = userMap.get(comment.getToUserId());
            if (toUser != null) {
                vo.setToUserName(toUser.getNickName());
            } else {
                vo.setToUserName("用户" + comment.getToUserId());
            }
        }

        // 设置文章信息
        if (comment.getArticleId() != null) {
            Article article = articleMap.get(comment.getArticleId());
            if (article != null) {
                vo.setArticleTitle(article.getTitle());
            }
        }

        // 设置友链信息
        if (comment.getLinkId() != null) {
            Link link = linkMap.get(comment.getLinkId());
            if (link != null) {
                vo.setLinkName(link.getName());
            }
        }

        return vo;
    }

    /**
     * 将Comment实体转换为CommentVO
     */
    private CommentVO toCommentVO(Comment comment, Map<Long, User> userMap) {
        CommentVO vo = new CommentVO();
        BeanUtil.copyProperties(comment, vo);

        // 设置评论用户信息
        User fromUser = userMap.get(comment.getFromUserId());
        if (fromUser != null) {
            vo.setFromUserName(fromUser.getNickName());
            vo.setFromUserAvatar(fromUser.getAvatar());
        }

        // 设置被回复用户信息
        if (comment.getToUserId() != null) {
            User toUser = userMap.get(comment.getToUserId());
            if (toUser != null) {
                vo.setToUserName(toUser.getNickName());
            }
        }

        return vo;
    }
}
