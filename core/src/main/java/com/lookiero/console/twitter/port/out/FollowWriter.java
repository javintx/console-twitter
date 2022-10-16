package com.lookiero.console.twitter.port.out;

public interface FollowWriter {
  void follows(final String follower, final String followed);
}
