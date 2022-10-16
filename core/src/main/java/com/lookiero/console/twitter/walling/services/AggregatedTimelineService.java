package com.lookiero.console.twitter.walling.services;

import com.lookiero.console.twitter.domain.Post;
import com.lookiero.console.twitter.port.out.FollowReader;
import com.lookiero.console.twitter.port.out.PostReader;
import com.lookiero.console.twitter.port.out.TimelineWriter;
import com.lookiero.console.twitter.walling.AggregatedTimeline;

import java.time.Instant;
import java.util.Comparator;
import java.util.Set;

import static com.lookiero.console.twitter.shared.TimeAgoSuffixes.timeAgoSuffix;

public class AggregatedTimelineService implements AggregatedTimeline {
  private static final Comparator<Post> BY_TIME_AGO = (post1, post2) -> post2.timestamp().compareTo(post1.timestamp());
  private static final String USER_MESSAGE_SEPARATOR = " - ";

  private final TimelineWriter timelines;
  private final PostReader posts;
  private final FollowReader followers;

  public AggregatedTimelineService(final TimelineWriter timelines, final PostReader posts, final FollowReader followers) {
    this.timelines = timelines;
    this.posts = posts;
    this.followers = followers;
  }

  @Override
  public void view(final String username) {
    timelines.write(posts.from(aggregatedUsernames(username))
                         .stream()
                         .sorted(BY_TIME_AGO)
                         .map(this::aggregatedMessageWithTimeAgo)
                         .toList());
  }

  private Set<String> aggregatedUsernames(final String username) {
    Set<String> usernames = followers.followedBy(username);
    usernames.add(username);
    return usernames;
  }

  private String aggregatedMessageWithTimeAgo(final Post post) {
    return post.username()
               .concat(USER_MESSAGE_SEPARATOR)
               .concat(post.message())
               .concat(timeAgoSuffix(post.timestamp(), Instant.now()));
  }
}
