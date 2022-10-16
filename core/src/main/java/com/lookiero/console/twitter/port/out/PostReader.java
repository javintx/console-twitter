package com.lookiero.console.twitter.port.out;

import com.lookiero.console.twitter.domain.Post;

import java.util.List;
import java.util.Set;

public interface PostReader {
  List<Post> from(final String username);

  List<Post> from(final Set<String> usernames);
}
