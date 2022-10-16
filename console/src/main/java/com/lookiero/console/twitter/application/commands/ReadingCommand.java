package com.lookiero.console.twitter.application.commands;

import com.lookiero.console.twitter.application.Command;
import com.lookiero.console.twitter.reading.ViewTimeline;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReadingCommand implements Command {
  private static final String REGEX = "^(?<username>(\\S+))$";

  private final ViewTimeline viewTimeline;

  public ReadingCommand(final ViewTimeline viewTimeline) {
    this.viewTimeline = viewTimeline;
  }

  @Override
  public void execute(final String command) {
    Matcher matcher = Pattern.compile(REGEX).matcher(command);
    if (matcher.matches()) {
      String username = matcher.group("username");
      viewTimeline.from(username);
    }
  }

  @Override
  public boolean matches(final String command) {
    return command.matches(REGEX);
  }
}
