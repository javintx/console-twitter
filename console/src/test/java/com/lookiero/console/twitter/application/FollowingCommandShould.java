package com.lookiero.console.twitter.application;

import com.lookiero.console.twitter.application.commands.FollowingCommand;
import com.lookiero.console.twitter.following.Following;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class FollowingCommandShould {

  private FollowingCommand followingCommand;
  private Following following;

  @BeforeEach
  public void setUp() {
    following = mock(Following.class);
    followingCommand = new FollowingCommand(following);
  }

  @Test
  void not_matches_wrong_command() {
    assertThat(followingCommand.matches("un known command")).isFalse();
  }

  @Test
  void matches_appropriate_command() {
    assertThat(followingCommand.matches("username1 follows username2")).isTrue();
  }

  @Test
  void not_executes_wrong_command() {
    followingCommand.execute("un known command");
    verify(following, times(0)).between(anyString(), anyString());
  }

  @Test
  void executes_appropriate_command() {
    followingCommand.execute("username1 follows username2");
    verify(following).between("username1", "username2");
  }
}

