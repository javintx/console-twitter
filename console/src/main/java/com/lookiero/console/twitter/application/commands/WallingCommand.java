package com.lookiero.console.twitter.application.commands;

import com.lookiero.console.twitter.application.Command;
import com.lookiero.console.twitter.walling.AggregatedTimeline;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WallingCommand implements Command {
  private static final String REGEX = "^(?<username>(\\S+)) wall$";

  private final AggregatedTimeline aggregatedTimeline;

  public WallingCommand(final AggregatedTimeline aggregatedTimeline) {
    this.aggregatedTimeline = aggregatedTimeline;
  }

  @Override
  public void execute(final String command) {
    Matcher matcher = Pattern.compile(REGEX).matcher(command);
    if (matcher.matches()) {
      String username = matcher.group("username");
      aggregatedTimeline.view(username);
    }
  }

  @Override
  public boolean matches(final String command) {
    return command.matches(REGEX);
  }
}
