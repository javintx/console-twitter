package com.lookiero.console.twitter.application;

import com.lookiero.console.twitter.domain.Post;
import com.lookiero.console.twitter.following.Following;
import com.lookiero.console.twitter.port.out.FollowReader;
import com.lookiero.console.twitter.port.out.PostReader;
import com.lookiero.console.twitter.port.out.TimelineWriter;
import com.lookiero.console.twitter.posting.PublishMessage;
import com.lookiero.console.twitter.reading.ViewTimeline;
import com.lookiero.console.twitter.reading.services.ViewTimelineService;
import com.lookiero.console.twitter.walling.AggregatedTimeline;
import com.lookiero.console.twitter.walling.services.AggregatedTimelineService;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import static java.time.Instant.now;
import static java.util.Collections.emptyList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TwitterConsoleApplicationShould {

  private static Scanner inputWithUserPostingCommand() {
    return new Scanner("Alice -> I love the weather today\n\n");
  }

  private static Scanner inputWithUserFollowingCommand() {
    return new Scanner("Alice follows Bob\n\n");
  }

  private static Scanner inputWithUserReadingCommand() {
    return new Scanner("Alice\n\n");
  }

  private static Scanner inputWithMultipleUserPostingCommands() {
    return new Scanner("Alice -> I love the weather today\nBob -> Damn! We lost!\nBob -> Good game though.\n\n");
  }

  private static Scanner inputWithUserWallingCommand() {
    return new Scanner("Alice wall\n\n");
  }

  @Test
  void read_posting_command_from_console() {
    PublishMessage publishMessage = mock(PublishMessage.class);

    new TwitterConsoleApplication(inputWithUserPostingCommand())
      .start(publishMessage, null, null, null);

    verify(publishMessage).toAPersonalTimeline(any(Post.class));
  }

  @Test
  void read_multiple_posting_commands_from_console() {
    PublishMessage publishMessage = mock(PublishMessage.class);

    new TwitterConsoleApplication(inputWithMultipleUserPostingCommands())
      .start(publishMessage, null, null, null);

    verify(publishMessage, times(3)).toAPersonalTimeline(any(Post.class));
  }

  @Test
  void read_reading_command_from_console() {
    ViewTimeline viewTimeline = mock(ViewTimeline.class);

    new TwitterConsoleApplication(inputWithUserReadingCommand())
      .start(null, viewTimeline, null, null);

    verify(viewTimeline).from("Alice");
  }

  @Test
  void write_reading_command_empty_result_to_console_when_user_has_no_posts() {
    PostReader postReader = mock(PostReader.class);
    when(postReader.from("Alice")).thenReturn(emptyList());
    TimelineWriter timelineWriter = mock(TimelineWriter.class);
    ViewTimeline viewTimeline = new ViewTimelineService(postReader, timelineWriter);

    new TwitterConsoleApplication(inputWithUserReadingCommand())
      .start(null, viewTimeline, null, null);

    verify(timelineWriter).write(emptyList());
  }

  @Test
  void write_reading_command_result_to_console_when_user_has_posts() {
    PostReader postReader = mock(PostReader.class);
    when(postReader.from("Alice")).thenReturn(List.of(new Post("Alice", "Message", now().minus(Duration.ofMinutes(1)))));
    TimelineWriter timelineWriter = mock(TimelineWriter.class);
    ViewTimeline viewTimeline = new ViewTimelineService(postReader, timelineWriter);

    new TwitterConsoleApplication(inputWithUserReadingCommand())
      .start(null, viewTimeline, null, null);

    verify(timelineWriter).write(List.of("Message (1 minute ago)"));
  }

  @Test
  void read_following_command_from_console() {
    Following following = mock(Following.class);

    new TwitterConsoleApplication(inputWithUserFollowingCommand())
      .start(null, null, following, null);

    verify(following).between(anyString(), anyString());
  }

  @Test
  void read_walling_command_from_console() {
    AggregatedTimeline aggregatedTimeline = mock(AggregatedTimeline.class);

    new TwitterConsoleApplication(inputWithUserWallingCommand())
      .start(null, null, null, aggregatedTimeline);

    verify(aggregatedTimeline).view("Alice");
  }

  @Test
  void write_walling_command_empty_result_to_console_when_user_has_no_posts_nor_followed() {
    FollowReader followReader = mock(FollowReader.class);
    when(followReader.followedBy("Alice")).thenReturn(new HashSet<>());
    PostReader postReader = mock(PostReader.class);
    when(postReader.from(Set.of("Alice"))).thenReturn(emptyList());
    TimelineWriter timelineWriter = mock(TimelineWriter.class);
    AggregatedTimeline aggregatedTimeline = new AggregatedTimelineService(timelineWriter, postReader, followReader);

    new TwitterConsoleApplication(inputWithUserWallingCommand())
      .start(null, null, null, aggregatedTimeline);

    verify(timelineWriter).write(emptyList());
  }

  @Test
  void write_walling_command_empty_result_to_console_when_user_has_posts_but_followed() {
    FollowReader followReader = mock(FollowReader.class);
    when(followReader.followedBy("Alice")).thenReturn(new HashSet<>());
    PostReader postReader = mock(PostReader.class);
    when(postReader.from(Set.of("Alice"))).thenReturn(List.of(new Post("Alice", "Message", now().minus(Duration.ofMinutes(1)))));
    TimelineWriter timelineWriter = mock(TimelineWriter.class);
    AggregatedTimeline aggregatedTimeline = new AggregatedTimelineService(timelineWriter, postReader, followReader);

    new TwitterConsoleApplication(inputWithUserWallingCommand())
      .start(null, null, null, aggregatedTimeline);

    verify(timelineWriter).write(List.of("Alice - Message (1 minute ago)"));
  }

  @Test
  void write_walling_command_empty_result_to_console_when_user_has_posts_and_followed() {
    FollowReader followReader = mock(FollowReader.class);
    when(followReader.followedBy("Alice")).thenReturn(new HashSet<>(Set.of("Bob")));
    PostReader postReader = mock(PostReader.class);
    when(postReader.from(Set.of("Alice", "Bob"))).thenReturn(List.of(new Post("Alice", "Message", now().minus(Duration.ofMinutes(1))),
                                                                     new Post("Bob", "Message2", now().minus(Duration.ofMinutes(5)))));
    TimelineWriter timelineWriter = mock(TimelineWriter.class);
    AggregatedTimeline aggregatedTimeline = new AggregatedTimelineService(timelineWriter, postReader, followReader);

    new TwitterConsoleApplication(inputWithUserWallingCommand())
      .start(null, null, null, aggregatedTimeline);

    verify(timelineWriter).write(List.of("Alice - Message (1 minute ago)", "Bob - Message2 (5 minutes ago)"));
  }
}
