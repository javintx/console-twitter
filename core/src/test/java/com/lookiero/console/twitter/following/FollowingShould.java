package com.lookiero.console.twitter.following;

import com.lookiero.console.twitter.following.services.FollowingService;
import com.lookiero.console.twitter.port.out.FollowWriter;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class FollowingShould {

  @Test
  void follows_between_different_users() {
    FollowWriter followWriter = mock(FollowWriter.class);
    Following following = new FollowingService(followWriter);

    following.between("follower", "followed");

    verify(followWriter).follows("follower", "followed");
  }

  @Test
  void not_follows_between_same_user() {
    FollowWriter followWriter = mock(FollowWriter.class);
    Following following = new FollowingService(followWriter);

    following.between("follower", "follower");

    verify(followWriter, times(0)).follows(anyString(), anyString());
  }

}
