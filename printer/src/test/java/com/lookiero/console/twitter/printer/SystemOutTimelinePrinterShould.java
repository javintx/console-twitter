package com.lookiero.console.twitter.printer;

import com.lookiero.console.twitter.port.out.TimelineWriter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.PrintStream;
import java.util.List;

import static java.util.Collections.emptyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class SystemOutTimelinePrinterShould {

  @Nested
  class TimelineWriterShould {
    private PrintStream printStream;
    private TimelineWriter timelineWriter;

    @BeforeEach
    public void setUp() {
      printStream = mock(PrintStream.class);
      timelineWriter = new SystemOutTimelinePrinter(printStream);
    }

    @Test
    void not_write_is_has_no_messages() {
      timelineWriter.write(emptyList());

      verify(printStream, times(0)).println();
    }

    @Test
    void write_a_message() {
      timelineWriter.write(List.of("Message"));

      verify(printStream).println("Message");
    }

    @Test
    void write_some_messages() {
      timelineWriter.write(List.of("Message1", "Message2", "Message3"));

      verify(printStream).println("Message1");
      verify(printStream).println("Message2");
      verify(printStream).println("Message3");
    }
  }
}
