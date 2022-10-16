package com.lookiero.console.twitter.domain;

import java.time.Instant;

public record Post(String username, String message, Instant timestamp) {
  public Post(String username, String message) {
    this(username, message, Instant.now());
  }

}
