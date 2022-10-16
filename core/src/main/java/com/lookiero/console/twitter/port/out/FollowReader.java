package com.lookiero.console.twitter.port.out;

import java.util.Set;

public interface FollowReader {
  Set<String> followedBy(final String username);
}
