package com.lookiero.console.twitter.posting;

import com.lookiero.console.twitter.domain.Post;

public interface PublishMessage {
  void toAPersonalTimeline(final Post post);
}
