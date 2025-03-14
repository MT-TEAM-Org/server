package org.myteam.server.comment.service;

public interface CommentCountService {
    void addCommentCount(Long contentId);

    void minusCommentCount(Long contentId);

    void minusCommentCount(Long contentId, int count);
}
