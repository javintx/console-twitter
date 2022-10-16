package com.lookiero.console.twitter.posting.services;

import com.lookiero.console.twitter.domain.Post;
import com.lookiero.console.twitter.port.out.PostWriter;
import com.lookiero.console.twitter.posting.PublishMessage;

public class PublishMessageService implements PublishMessage {

  private final PostWriter posts;

  public PublishMessageService(final PostWriter posts) {
    this.posts = posts;
  }

  @Override
  public void toAPersonalTimeline(final Post post) {
    posts.save(post);
  }
}
