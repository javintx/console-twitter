package com.lookiero.console.twitter.application;

public interface Command {

  void execute(final String command);

  boolean matches(final String command);
}
