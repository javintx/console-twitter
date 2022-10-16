package com.lookiero.console.twitter.application;

import com.lookiero.console.twitter.application.commands.ReadingCommand;
import com.lookiero.console.twitter.reading.ViewTimeline;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class ReadingCommandShould {

  private ReadingCommand readingCommand;
  private ViewTimeline viewTimeline;

  @BeforeEach
  public void setUp() {
    viewTimeline = mock(ViewTimeline.class);
    readingCommand = new ReadingCommand(viewTimeline);
  }

  @Test
  void not_matches_wrong_command() {
    assertThat(readingCommand.matches("un known")).isFalse();
  }

  @Test
  void matches_appropriate_command() {
    assertThat(readingCommand.matches("username")).isTrue();
  }

  @Test
  void not_executes_wrong_command() {
    readingCommand.execute("un known");
    verify(viewTimeline, times(0)).from(anyString());
  }

  @Test
  void executes_appropriate_command() {
    readingCommand.execute("username");
    verify(viewTimeline).from("username");
  }
}
