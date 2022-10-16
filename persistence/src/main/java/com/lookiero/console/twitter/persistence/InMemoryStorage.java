package com.lookiero.console.twitter.persistence;

import com.lookiero.console.twitter.domain.Post;
import com.lookiero.console.twitter.port.out.FollowReader;
import com.lookiero.console.twitter.port.out.FollowWriter;
import com.lookiero.console.twitter.port.out.PostReader;
import com.lookiero.console.twitter.port.out.PostWriter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class InMemoryStorage implements PostReader, PostWriter, FollowWriter, FollowReader {
  private final Map<String, List<Post>> posts;
  private final Map<String, Set<String>> followers;

  public InMemoryStorage(final Map<String, List<Post>> posts, final Map<String, Set<String>> followers) {
    this.posts = posts;
    this.followers = followers;
  }

  @Override
  public List<Post> from(final String username) {
    return Optional.ofNullable(posts.get(username))
                   .orElse(new ArrayList<>());
  }

  @Override
  public List<Post> from(final Set<String> usernames) {
    return usernames
      .stream()
      .flatMap(s -> Optional.ofNullable(posts.get(s))
                            .orElse(new ArrayList<>())
                            .stream())
      .toList();
  }

  @Override
  public void save(final Post post) {
    if (post != null) {
      posts.computeIfAbsent(post.username(), username -> new ArrayList<>())
           .add(post);
    }
  }

  @Override
  public void follows(final String follower, final String followed) {
    if (followed != null) {
      followers.computeIfAbsent(follower, s -> new HashSet<>())
               .add(followed);
    }
  }

  @Override
  public Set<String> followedBy(final String username) {
    return Optional.ofNullable(followers.get(username))
                   .orElse(new HashSet<>());
  }
}
