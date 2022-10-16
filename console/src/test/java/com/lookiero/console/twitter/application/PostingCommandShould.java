package com.lookiero.console.twitter.application;

import com.lookiero.console.twitter.application.commands.PostingCommand;
import com.lookiero.console.twitter.domain.Post;
import com.lookiero.console.twitter.posting.PublishMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class PostingCommandShould {

  private PostingCommand postingCommand;
  private PublishMessage publishMessage;

  @BeforeEach
  public void setUp() {
    publishMessage = mock(PublishMessage.class);
    postingCommand = new PostingCommand(publishMessage);
  }

  @Test
  void not_matches_wrong_command() {
    assertThat(postingCommand.matches("unknown")).isFalse();
  }

  @Test
  void matches_appropriate_command() {
    assertThat(postingCommand.matches("username -> message")).isTrue();
  }

  @Test
  void not_executes_wrong_command() {
    postingCommand.execute("unknown");
    verify(publishMessage, times(0)).toAPersonalTimeline(any(Post.class));
  }

  @Test
  void executes_appropriate_command() {
    postingCommand.execute("username -> message");
    verify(publishMessage).toAPersonalTimeline(any(Post.class));
  }
}
