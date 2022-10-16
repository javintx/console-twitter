package com.lookiero.console.twitter.persistence;

import com.lookiero.console.twitter.domain.Post;
import com.lookiero.console.twitter.port.out.FollowReader;
import com.lookiero.console.twitter.port.out.FollowWriter;
import com.lookiero.console.twitter.port.out.PostReader;
import com.lookiero.console.twitter.port.out.PostWriter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class InMemoryStorageShould {

  @Nested
  class PostReaderShould {
    private Map<String, List<Post>> postsMap;
    private PostReader postReader;

    @BeforeEach
    public void setUp() {
      postsMap = new HashMap<>();
      postReader = new InMemoryStorage(postsMap, new HashMap<>());
    }

    @Test
    void not_read_if_no_posts_persisted() {
      assertThat(postReader.from("username")).isEmpty();
    }

    @Test
    void not_read_if_user_has_no_posts_persisted() {
      postsMap.put("userName", List.of(new Post("userName", "Message")));
      assertThat(postReader.from("username")).isEmpty();
    }

    @Test
    void read_user_persisted_posts() {
      postsMap.put("username", List.of(
                     new Post("username", "Message1"),
                     new Post("username", "Message2")
                                      )
                  );
      assertThat(postReader.from("username")).size().isEqualTo(2);
    }

    @Test
    void not_read_from_multiple_users_if_no_posts_persisted() {
      assertThat(postReader.from(Set.of("username1", "username2"))).isEmpty();
    }

    @Test
    void not_read_from_multiple_users_if_user_has_no_posts_persisted() {
      postsMap.put("userName", List.of(new Post("userName", "Message")));
      assertThat(postReader.from(Set.of("username1", "username2"))).isEmpty();
    }

    @Test
    void read_from_multiple_users_with_persisted_posts() {
      postsMap.put("username1",
                   List.of(
                     new Post("username1", "Message11"),
                     new Post("username1", "Message12")
                          )
                  );
      postsMap.put("username2",
                   List.of(
                     new Post("username2", "Message21"),
                     new Post("username2", "Message22")
                          )
                  );
      assertThat(postReader.from(Set.of("username1", "username2"))).size().isEqualTo(4);
    }
  }

  @Nested
  class PostWriterShould {
    private Map<String, List<Post>> postsMap;
    private PostWriter postWriter;

    @BeforeEach
    public void setUp() {
      postsMap = new HashMap<>();
      postWriter = new InMemoryStorage(postsMap, new HashMap<>());
    }

    @Test
    void not_save_no_post() {
      postWriter.save(null);

      assertThat(postsMap).isEmpty();
    }

    @Test
    void save_not_null_post() {
      postWriter.save(new Post("username", "message"));

      assertThat(postsMap).isNotEmpty();
    }

    @Test
    void add_post_to_user_posts() {
      postWriter.save(new Post("username", "message1"));
      postWriter.save(new Post("username", "message2"));

      assertThat(postsMap.get("username")).size().isEqualTo(2);
    }

    @Test
    void add_post_to_multiple_users() {
      postWriter.save(new Post("username1", "message1"));
      postWriter.save(new Post("username2", "message1"));

      assertThat(postsMap.get("username1")).size().isEqualTo(1);
      assertThat(postsMap.get("username2")).size().isEqualTo(1);
    }
  }

  @Nested
  class FollowWriterShould {
    private Map<String, Set<String>> followersMap;
    private FollowWriter followWriter;

    @BeforeEach
    public void setUp() {
      followersMap = new HashMap<>();
      followWriter = new InMemoryStorage(new HashMap<>(), followersMap);
    }

    @Test
    void not_follows_a_not_followed() {
      followWriter.follows("knowFollower", null);

      assertThat(followersMap).isEmpty();
    }

    @Test
    void follows_a_followed() {
      followWriter.follows("follower", "followed");

      assertThat(followersMap).isNotEmpty();
    }

    @Test
    void follows_a_more_than_one_user() {
      followWriter.follows("follower", "followed");
      followWriter.follows("follower", "username");

      assertThat(followersMap.get("follower")).contains("followed", "username");
    }
  }

  @Nested
  class FollowReaderShould {
    private Map<String, Set<String>> followersMap;
    private FollowReader followReader;

    @BeforeEach
    public void setUp() {
      followersMap = new HashMap<>();
      followReader = new InMemoryStorage(new HashMap<>(), followersMap);
    }

    @Test
    void not_read_followers_if_no_followings_persisted() {
      assertThat(followReader.followedBy("username")).isEmpty();
    }

    @Test
    void not_read_followers_if_user_has_no_followings_persisted() {
      followersMap.put("userName", Set.of("username"));

      assertThat(followReader.followedBy("username")).isEmpty();
    }

    @Test
    void read_user_persisted_followers() {
      followersMap.put("username", Set.of("username1", "username2"));

      assertThat(followReader.followedBy("username")).size().isEqualTo(2);
    }

  }
}
