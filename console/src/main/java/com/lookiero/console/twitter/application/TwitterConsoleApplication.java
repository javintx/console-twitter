package com.lookiero.console.twitter.application;

import com.lookiero.console.twitter.application.commands.FollowingCommand;
import com.lookiero.console.twitter.application.commands.PostingCommand;
import com.lookiero.console.twitter.application.commands.ReadingCommand;
import com.lookiero.console.twitter.application.commands.WallingCommand;
import com.lookiero.console.twitter.following.Following;
import com.lookiero.console.twitter.posting.PublishMessage;
import com.lookiero.console.twitter.reading.ViewTimeline;
import com.lookiero.console.twitter.walling.AggregatedTimeline;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

public class TwitterConsoleApplication {
  private final Scanner input;
  private final List<Command> commands;

  public TwitterConsoleApplication(final Scanner input) {
    this.input = input;
    commands = new ArrayList<>();
  }

  public void start(final PublishMessage publishMessage,
                    final ViewTimeline viewTimeline,
                    final Following following,
                    AggregatedTimeline aggregatedTimeline) {
    initializeCommands(publishMessage, viewTimeline, following, aggregatedTimeline);
    readAndExecuteCommands();
  }

  private void initializeCommands(final PublishMessage publishMessage,
                                  final ViewTimeline viewTimeline,
                                  final Following following,
                                  final AggregatedTimeline aggregatedTimeline) {
    commands.add(new PostingCommand(publishMessage));
    commands.add(new ReadingCommand(viewTimeline));
    commands.add(new FollowingCommand(following));
    commands.add(new WallingCommand(aggregatedTimeline));
  }

  private void readAndExecuteCommands() {
    AtomicBoolean finish = new AtomicBoolean(false);
    do {
      String inputCommand = input.nextLine();
      parseCommand(inputCommand)
        .ifPresentOrElse(command -> command.execute(inputCommand), () -> finish.set(true));
    } while (!finish.get());
  }

  private Optional<Command> parseCommand(final String inputCommand) {
    return commands.stream()
                   .filter(command -> command.matches(inputCommand))
                   .findFirst();
  }

}
