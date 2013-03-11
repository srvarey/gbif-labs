package org.gbif.api.registry.service;

import org.gbif.api.registry.model.Comment;

import java.util.List;
import java.util.UUID;

import javax.validation.constraints.NotNull;

public interface CommentService {

  int addComment(@NotNull UUID targetEntityKey, Comment comment);

  void deleteComment(@NotNull UUID targetEntityKey, @NotNull int commentKey);

  List<Comment> listComments(@NotNull UUID targetEntityKey);
}
