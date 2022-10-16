package com.lookiero.console.twitter.shared;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.util.Arrays;

public enum TimeAgoSuffixes {
  DAYS(ChronoUnit.DAYS, " (%d day%s ago)"),
  HOURS(ChronoUnit.HOURS, " (%d hour%s ago)"),
  MINUTES(ChronoUnit.MINUTES, " (%d minute%s ago)"),
  SECONDS(ChronoUnit.SECONDS, " (%d second%s ago)");

  private final ChronoUnit resolution;
  private final String regex;

  TimeAgoSuffixes(final ChronoUnit resolution, final String regex) {
    this.resolution = resolution;
    this.regex = regex;
  }

  public static String timeAgoSuffix(final Instant timestamp, final Instant now) {
    return Arrays.stream(TimeAgoSuffixes.values())
                 .map(timeAgoSuffixes -> timeAgoSuffixes.applyOrEmpty(timestamp, now))
                 .filter(s -> !s.isEmpty())
                 .findFirst()
                 .orElse(" (now)");

  }

  public String applyOrEmpty(final Temporal timestamp, final Temporal now) {
    long timeAgo = resolution.between(timestamp, now);
    if (timeAgo > 0) {
      return String.format(regex, timeAgo, pluralOrEmptyFor(timeAgo));
    }
    return "";
  }

  private String pluralOrEmptyFor(long timeAgo) {
    return timeAgo > 1 ? "s" : "";
  }
}
