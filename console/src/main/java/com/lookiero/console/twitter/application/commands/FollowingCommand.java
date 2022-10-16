package com.lookiero.console.twitter.application.commands;

import com.lookiero.console.twitter.application.Command;
import com.lookiero.console.twitter.following.Following;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FollowingCommand implements Command {
  private static final String REGEX = "^(?<follower>(\\S+)) follows (?<followed>(.+))$";
  private final Following following;

  public FollowingCommand(final Following following) {
    this.following = following;
  }

  @Override
  public void execute(final String command) {
    Matcher matcher = Pattern.compile(REGEX).matcher(command);
    if (matcher.matches()) {
      following.between(matcher.group("follower"), matcher.group("followed"));
    }
  }

  @Override
  public boolean matches(final String command) {
    return command.matches(REGEX);
  }
}
