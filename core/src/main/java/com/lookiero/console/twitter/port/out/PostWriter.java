package com.lookiero.console.twitter.port.out;

import com.lookiero.console.twitter.domain.Post;

public interface PostWriter {
  void save(final Post post);
}
