package com.system.radius.utils;

import com.badlogic.gdx.ApplicationLogger;

public class BombermanLogger implements ApplicationLogger {

  private String tag;

  public BombermanLogger() {
    this("default");
  }

  public BombermanLogger(String tag) {
    this.tag = tag;
  }

  public void info(String message) {
    log(tag, message);
  }

  public void error(String message) {
    error(tag, message);
  }

  public void error(String message, Throwable t) {
    error(tag, message, t);
  }

  public void log(String tag, String message) {
    System.out.println("[" + tag + "] " + message);
  }

  public void log(String tag, String message, Throwable exception) {
    System.out.println("[" + tag + "] " + message);
    exception.printStackTrace(System.out);
  }

  public void error(String tag, String message) {
    System.err.println("[" + tag + "] " + message);
  }

  public void error(String tag, String message, Throwable exception) {
    System.err.println("[" + tag + "] " + message);
    exception.printStackTrace(System.err);
  }

  public void debug(String tag, String message) {
    System.out.println("[" + tag + "] " + message);
  }

  public void debug(String tag, String message, Throwable exception) {
    System.out.println("[" + tag + "] " + message);
    exception.printStackTrace(System.out);
  }

}
