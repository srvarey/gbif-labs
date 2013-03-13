package org.gbif.registry.persistence.mapper;

import org.gbif.api.registry.model.Comment;

public interface CommentMapper {

  int createComment(Comment comment);
}
