package com.lookiero.console.twitter.application.commands;

import com.lookiero.console.twitter.application.Command;
import com.lookiero.console.twitter.domain.Post;
import com.lookiero.console.twitter.posting.PublishMessage;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PostingCommand implements Command {
  private static final String REGEX = "^(?<username>(\\S+)) -> (?<message>(.+))$";

  private final PublishMessage publishMessage;

  public PostingCommand(final PublishMessage publishMessage) {
    this.publishMessage = publishMessage;
  }

  @Override
  public void execute(final String command) {
    Matcher matcher = Pattern.compile(REGEX).matcher(command);
    if (matcher.matches()) {
      Post post = new Post(matcher.group("username"), matcher.group("message"));
      publishMessage.toAPersonalTimeline(post);
    }
  }

  @Override
  public boolean matches(final String command) {
    return command.matches(REGEX);
  }

}
