package com.lookiero.console.twitter.following.services;

import com.lookiero.console.twitter.following.Following;
import com.lookiero.console.twitter.port.out.FollowWriter;

public class FollowingService implements Following {

  private final FollowWriter followers;

  public FollowingService(final FollowWriter followers) {
    this.followers = followers;
  }

  @Override
  public void between(final String follower, final String followed) {
    if (!follower.equals(followed)) {
      followers.follows(follower, followed);
    }
  }
}
