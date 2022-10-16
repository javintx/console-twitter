package com.lookiero.console.twitter.walling;

import com.lookiero.console.twitter.domain.Post;
import com.lookiero.console.twitter.port.out.FollowReader;
import com.lookiero.console.twitter.port.out.PostReader;
import com.lookiero.console.twitter.port.out.TimelineWriter;
import com.lookiero.console.twitter.walling.services.AggregatedTimelineService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.time.Instant.now;
import static java.util.Collections.emptyList;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AggregatedTimelineShould {
  private TimelineWriter timelineWriter;
  private PostReader postReader;
  private FollowReader followReader;
  private AggregatedTimeline aggregatedTimeline;

  @BeforeEach
  public void setUp() {
    timelineWriter = mock(TimelineWriter.class);
    postReader = mock(PostReader.class);
    followReader = mock(FollowReader.class);
    aggregatedTimeline = new AggregatedTimelineService(timelineWriter, postReader, followReader);
  }

  @Test
  void not_view_aggregated_timeline_if_user_has_no_post_nor_followed() {
    aggregatedTimeline.view("username");

    verify(timelineWriter).write(emptyList());
  }

  @Test
  void view_aggregated_timeline_if_user_has_post_but_not_followed() {
    when(postReader.from(Set.of("username")))
      .thenReturn(List.of(new Post("username", "Message")));
    aggregatedTimeline.view("username");

    verify(timelineWriter).write(anyList());
  }

  @Test
  void view_aggregated_timeline_if_user_has_post_and_followed() {
    when(postReader.from(Set.of("username1", "username2", "username3")))
      .thenReturn(new ArrayList<>(List.of(
                    new Post("username1", "Message1", now().minus(Duration.ofMinutes(1))),
                    new Post("username2", "Message2", now().minus(Duration.ofMinutes(2))),
                    new Post("username3", "Message3", now().minus(Duration.ofMinutes(3)))
                                         ))
                 );
    when(followReader.followedBy("username1"))
      .thenReturn(new HashSet<>(Set.of("username2", "username3")));

    aggregatedTimeline.view("username1");

    verify(timelineWriter).write(List.of("username1 - Message1 (1 minute ago)",
                                         "username2 - Message2 (2 minutes ago)",
                                         "username3 - Message3 (3 minutes ago)"));
    verify(timelineWriter).write(anyList());
  }
}
