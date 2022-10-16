package com.lookiero.console.twitter.application;

import com.lookiero.console.twitter.application.commands.WallingCommand;
import com.lookiero.console.twitter.walling.AggregatedTimeline;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class WallingCommandShould {

  private WallingCommand wallingCommand;
  private AggregatedTimeline aggregatedTimeline;

  @BeforeEach
  public void setUp() {
    aggregatedTimeline = mock(AggregatedTimeline.class);
    wallingCommand = new WallingCommand(aggregatedTimeline);
  }

  @Test
  void not_matches_wrong_command() {
    assertThat(wallingCommand.matches("un known")).isFalse();
  }

  @Test
  void matches_appropriate_command() {
    assertThat(wallingCommand.matches("username wall")).isTrue();
  }

  @Test
  void not_executes_wrong_command() {
    wallingCommand.execute("un known");
    verify(aggregatedTimeline, times(0)).view(anyString());
  }

  @Test
  void executes_appropriate_command() {
    wallingCommand.execute("username wall");
    verify(aggregatedTimeline).view("username");
  }
}
