package com.lookiero.application;

import com.lookiero.console.twitter.application.TwitterConsoleApplication;
import com.lookiero.console.twitter.following.Following;
import com.lookiero.console.twitter.following.services.FollowingService;
import com.lookiero.console.twitter.persistence.InMemoryStorage;
import com.lookiero.console.twitter.port.out.PostReader;
import com.lookiero.console.twitter.port.out.TimelineWriter;
import com.lookiero.console.twitter.posting.PublishMessage;
import com.lookiero.console.twitter.posting.services.PublishMessageService;
import com.lookiero.console.twitter.printer.SystemOutTimelinePrinter;
import com.lookiero.console.twitter.reading.ViewTimeline;
import com.lookiero.console.twitter.reading.services.ViewTimelineService;
import com.lookiero.console.twitter.walling.services.AggregatedTimelineService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Scanner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.startsWith;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class ConsoleTwitterAppIT {

  private static final String SYSTEM_EXIT = "\n";
  private static final String POSTING_SCENARIO = """
    Alice -> I love the weather today
    Bob -> Damn! We lost!
    Bob -> Good game though.
    """;
  private static final String READING_SCENARIO = POSTING_SCENARIO + """
    Alice
    Bob
    """;
  private static final String FOLLOWING_SCENARIO = READING_SCENARIO + """
    Charlie -> I'm in New York today! Anyone want to have a coffee?
    Charlie follows Alice
    Charlie wall
    Charlie follows Bob
    Charlie wall

    """;

  private PublishMessage publishMessage;
  private ViewTimeline viewTimeline;
  private Following following;
  private PostReader postReader;
  private AggregatedTimelineService aggregatedTimeline;
  private PrintStream output;
  private Scanner input;

  @BeforeEach
  public void setUp() {
    output = mock(PrintStream.class);
    TimelineWriter timelineWriter = new SystemOutTimelinePrinter(output);

    InMemoryStorage inMemoryStorage = new InMemoryStorage(new HashMap<>(), new HashMap<>());
    postReader = inMemoryStorage;

    publishMessage = new PublishMessageService(inMemoryStorage);
    viewTimeline = new ViewTimelineService(postReader, timelineWriter);
    following = new FollowingService(inMemoryStorage);
    aggregatedTimeline = new AggregatedTimelineService(timelineWriter, inMemoryStorage, inMemoryStorage);
  }

  @AfterEach
  void close() {
    input.close();
  }

  @Test
  void posting_scenario() {
    input = new Scanner(
      POSTING_SCENARIO + SYSTEM_EXIT
    );
    TwitterConsoleApplication application = new TwitterConsoleApplication(input);
    application.start(publishMessage, viewTimeline, following, aggregatedTimeline);

    assertThat(postReader.from("Alice")).isNotEmpty();
    assertEquals(2, postReader.from("Bob").size());
  }

  @Test
  void reading_scenario() {
    input = new Scanner(
      READING_SCENARIO + SYSTEM_EXIT
    );
    TwitterConsoleApplication application = new TwitterConsoleApplication(input);
    application.start(publishMessage, viewTimeline, following, aggregatedTimeline);

    // The test is too fast to print the time ago
    verify(output).println(startsWith("I love the weather today "));
    verify(output).println(startsWith("Good game though. "));
    verify(output).println(startsWith("Damn! We lost! "));
  }

  @Test
  void following_scenario() {
    input = new Scanner(
      FOLLOWING_SCENARIO + SYSTEM_EXIT
    );
    TwitterConsoleApplication application = new TwitterConsoleApplication(input);
    application.start(publishMessage, viewTimeline, following, aggregatedTimeline);

    verify(output, times(2)).println(startsWith("Charlie - I'm in New York today! Anyone want to have a coffee?"));
    verify(output, times(2)).println(startsWith("Alice - I love the weather today"));
    verify(output).println(startsWith("I love the weather today"));
    verify(output).println(startsWith("Bob - Good game though."));
    verify(output).println(startsWith("Bob - Damn! We lost!"));
    verify(output).println(startsWith("Good game though."));
    verify(output).println(startsWith("Damn! We lost!"));
  }

}
