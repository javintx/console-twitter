package com.lookiero.console.twitter.posting;

import com.lookiero.console.twitter.domain.Post;
import com.lookiero.console.twitter.port.out.PostWriter;
import com.lookiero.console.twitter.posting.services.PublishMessageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class PublishMessageShould {

  private PublishMessage publishMessage;
  private PostWriter postWriter;

  @BeforeEach
  void setUp() {
    postWriter = mock(PostWriter.class);
    publishMessage = new PublishMessageService(postWriter);
  }

  @Test
  void publish_message_to_a_personal_timeline() {
    publishMessage.toAPersonalTimeline(new Post("Alice", "I love the weather today"));

    verify(postWriter).save(any(Post.class));
  }

  @Test
  void publish_messages_from_multiple_users_to_their_personal_timeline() {
    publishMessage.toAPersonalTimeline(new Post("Alice", "I love the weather today"));
    publishMessage.toAPersonalTimeline(new Post("Bob", "Damn! We lost!"));
    publishMessage.toAPersonalTimeline(new Post("Bob", "Good game though."));

    verify(postWriter, times(3)).save(any(Post.class));
  }
}
