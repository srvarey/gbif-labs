package org.gbif.registry;

import org.gbif.api.registry.model.Comment;
import org.gbif.api.registry.model.NetworkEntity;
import org.gbif.api.registry.service.CommentService;
import org.gbif.registry.utils.Comments;

import java.util.List;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class CommentTests {

  @Test
  public static <T extends NetworkEntity> void testAddDelete(CommentService service, T entity) {

    // check there are none on a newly created entity
    List<Comment> comments = service.listComments(entity.getKey());
    assertNotNull("Comment list should be empty, not null when no machine tags exist", comments);
    assertTrue("Comment should be empty when none added", comments.isEmpty());

    // test additions
    service.addComment(entity.getKey(), Comments.newInstance());
    service.addComment(entity.getKey(), Comments.newInstance());
    comments = service.listComments(entity.getKey());
    assertNotNull(comments);
    assertEquals("2 commentss have been added", 2, comments.size());

    // test deletion, ensuring correct one is deleted
    service.deleteComment(entity.getKey(), comments.get(0).getKey());
    comments = service.listComments(entity.getKey());
    assertNotNull(comments);
    assertEquals("1 comment should remain after the deletion", 1, comments.size());
    Comment expected = Comments.newInstance();
    Comment created = comments.get(0);
    expected.setKey(created.getKey());
    expected.setCreated(created.getCreated());
    expected.setModified(created.getModified());
    assertEquals("Created comment does not read as expected", expected, created);
  }
}
