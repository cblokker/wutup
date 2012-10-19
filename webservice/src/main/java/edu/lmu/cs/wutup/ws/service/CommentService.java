package edu.lmu.cs.wutup.ws.service;

import java.util.List;

import edu.lmu.cs.wutup.ws.model.Comment;

public interface CommentService {

    public void addComment(int objectId, Comment comment);

    public void updateComment(int objectId, Comment comment);

    List<Comment> findComments(int objectId, int page, int pageSize);

    void deleteComment(int objectId, int commentId);

}
