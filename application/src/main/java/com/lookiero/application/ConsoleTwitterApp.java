package com.lookiero.application;

import com.lookiero.console.twitter.application.TwitterConsoleApplication;
import com.lookiero.console.twitter.following.services.FollowingService;
import com.lookiero.console.twitter.persistence.InMemoryStorage;
import com.lookiero.console.twitter.port.out.TimelineWriter;
import com.lookiero.console.twitter.posting.services.PublishMessageService;
import com.lookiero.console.twitter.printer.SystemOutTimelinePrinter;
import com.lookiero.console.twitter.reading.services.ViewTimelineService;
import com.lookiero.console.twitter.walling.services.AggregatedTimelineService;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Scanner;

public class ConsoleTwitterApp {
  public static void main(final String... args) {
    final InMemoryStorage storage = inMemoryStorage();
    final TimelineWriter timelineWriter = new SystemOutTimelinePrinter(System.out);

    consoleApplication()
      .start(
        new PublishMessageService(storage),
        new ViewTimelineService(storage, timelineWriter),
        new FollowingService(storage),
        new AggregatedTimelineService(timelineWriter, storage, storage));
  }

  private static InMemoryStorage inMemoryStorage() {
    return new InMemoryStorage(
      new HashMap<>(7, 0.65f),
      new HashMap<>(3, 0.85f)
    );
  }

  private static TwitterConsoleApplication consoleApplication() {
    return new TwitterConsoleApplication(new Scanner(System.in, Charset.defaultCharset()));
  }
}
