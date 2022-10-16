package com.lookiero.console.twitter.reading.services;

import com.lookiero.console.twitter.domain.Post;
import com.lookiero.console.twitter.port.out.PostReader;
import com.lookiero.console.twitter.port.out.TimelineWriter;
import com.lookiero.console.twitter.reading.ViewTimeline;

import java.time.Instant;
import java.util.Comparator;

import static com.lookiero.console.twitter.shared.TimeAgoSuffixes.timeAgoSuffix;

public class ViewTimelineService implements ViewTimeline {

  private static final Comparator<Post> BY_TIME_AGO = (post1, post2) -> post2.timestamp().compareTo(post1.timestamp());
  private final PostReader posts;
  private final TimelineWriter timelines;

  public ViewTimelineService(final PostReader posts, final TimelineWriter timelines) {
    this.posts = posts;
    this.timelines = timelines;
  }

  @Override
  public void from(final String username) {
    timelines.write(posts.from(username)
                         .stream()
                         .sorted(BY_TIME_AGO)
                         .map(this::messageWithTimeAgo)
                         .toList());
  }

  private String messageWithTimeAgo(final Post post) {
    return post.message().concat(timeAgoSuffix(post.timestamp(), Instant.now()));
  }

}
