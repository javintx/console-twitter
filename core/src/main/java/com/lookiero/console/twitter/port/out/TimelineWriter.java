package com.lookiero.console.twitter.port.out;

import java.util.List;

public interface TimelineWriter {
  void write(final List<String> messages);
}
