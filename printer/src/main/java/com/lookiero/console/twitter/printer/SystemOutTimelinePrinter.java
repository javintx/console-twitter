package com.lookiero.console.twitter.printer;

import com.lookiero.console.twitter.port.out.TimelineWriter;

import java.io.PrintStream;
import java.util.List;

public class SystemOutTimelinePrinter implements TimelineWriter {
  private final PrintStream printStream;

  public SystemOutTimelinePrinter(final PrintStream printStream) {
    this.printStream = printStream;
  }

  @Override
  public void write(final List<String> messages) {
    messages.forEach(printStream::println);
  }
}
