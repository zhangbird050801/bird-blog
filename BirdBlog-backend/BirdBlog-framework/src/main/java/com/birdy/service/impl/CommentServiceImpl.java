package com.birdy.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.birdy.constants.SysConstants;
import com.birdy.domain.CommonResult;
import com.birdy.domain.dto.AddCommentRequest;
import com.birdy.domain.entity.Comment;
import com.birdy.domain.entity.User;
import com.birdy.domain.vo.CommentVO;
import com.birdy.enums.HttpCodeEnum;
import com.birdy.mapper.CommentMapper;
import com.birdy.mapper.UserMapper;
import com.birdy.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
    public CommonResult<Void> addComment(AddCommentRequest request, Long userId) {
        // 1. 参数校验
        if (request.getArticleId() == null) {
            return CommonResult.error(HttpCodeEnum.SYSTEM_ERROR, "文章ID不能为空");
        }
        if (!StringUtils.hasText(request.getContent())) {
            return CommonResult.error(HttpCodeEnum.SYSTEM_ERROR, "评论内容不能为空");
        }
        if (request.getContent().length() > 512) {
            return CommonResult.error(HttpCodeEnum.SYSTEM_ERROR, "评论内容不能超过512个字符");
        }

        // 2. 创建评论实体
        Comment comment = new Comment();
        comment.setType(SysConstants.COMMENT_TYPE_ARTICLE);
        comment.setArticleId(request.getArticleId());
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
