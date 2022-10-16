package com.lookiero.console.twitter.reading;

import com.lookiero.console.twitter.domain.Post;
import com.lookiero.console.twitter.port.out.PostReader;
import com.lookiero.console.twitter.port.out.TimelineWriter;
import com.lookiero.console.twitter.reading.services.ViewTimelineService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Duration;
import java.util.List;

import static java.time.Instant.now;
import static java.util.Collections.emptyList;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ViewTimelineShould {

  private ViewTimeline viewTimeline;
  private PostReader postReader;
  private TimelineWriter timelineWriter;

  private static List<Arguments> timelines() {
    return List.of(
      arguments("username", "Message with more than 1 minute", Duration.ofMinutes(5), "Message with more than 1 minute (5 minutes ago)"),
      arguments("username", "Message with 1 minute", Duration.ofMinutes(1), "Message with 1 minute (1 minute ago)"),
      arguments("username", "Message with more than 1 second", Duration.ofSeconds(2), "Message with more than 1 second (2 seconds ago)"),
      arguments("username", "Message with 1 second", Duration.ofSeconds(1), "Message with 1 second (1 second ago)"),
      arguments("username", "Message with more than 1 hour", Duration.ofHours(15), "Message with more than 1 hour (15 hours ago)"),
      arguments("username", "Message with 1 hour", Duration.ofHours(1), "Message with 1 hour (1 hour ago)"),
      arguments("username", "Message with more than 1 day", Duration.ofDays(4), "Message with more than 1 day (4 days ago)"),
      arguments("username", "Message with 1 day", Duration.ofDays(1), "Message with 1 day (1 day ago)")
                  );
  }

  @BeforeEach
  public void setUp() {
    postReader = mock(PostReader.class);
    timelineWriter = mock(TimelineWriter.class);
    viewTimeline = new ViewTimelineService(postReader, timelineWriter);
  }

  @Test
  void show_empty_timeline_from_user_without_timeline() {
    when(postReader.from("userName"))
      .thenReturn(List.of(new Post("userName", "This message should never viewed")));
    when(postReader.from("username"))
      .thenReturn(emptyList());

    viewTimeline.from("username");

    verify(timelineWriter).write(anyList());
  }

  @ParameterizedTest(name = "for {1}")
  @MethodSource("timelines")
  void show_timeline_from_user_with_one_post_from_time_ago(String username, String message, Duration timeAgo, String messageTimeAgo) {
    when(postReader.from(username))
      .thenReturn(List.of(new Post(username, message, now().minus(timeAgo))));

    viewTimeline.from(username);

    verify(timelineWriter).write(List.of(messageTimeAgo));
  }

  @Test
  void show_timeline_from_user_with_more_than_one_post() {
    when(postReader.from("Bob"))
      .thenReturn(List.of(
                    new Post("Bob", "Good game though.", now().minus(Duration.ofMinutes(1))),
                    new Post("Bob", "Damn! We lost!", now().minus(Duration.ofMinutes(2)))
                         )
                 );

    viewTimeline.from("Bob");

    verify(timelineWriter).write(List.of("Good game though. (1 minute ago)", "Damn! We lost! (2 minutes ago)"));
  }

  @Test
  void show_timeline_from_user_with_more_than_one_post_in_chronological_order() {
    when(postReader.from("Bob"))
      .thenReturn(List.of(
                    new Post("Bob", "Damn! We lost!", now().minus(Duration.ofMinutes(2))),
                    new Post("Bob", "Good game though.", now().minus(Duration.ofMinutes(1)))
                         )
                 );

    viewTimeline.from("Bob");

    verify(timelineWriter).write(List.of("Good game though. (1 minute ago)", "Damn! We lost! (2 minutes ago)"));
  }
}
