package com.mathpar.students.OLD.ukma2019.amukhopad.parallels.dispatch;

import com.mathpar.students.OLD.ukma2019.amukhopad.parallels.util.ProcessorUtil;

public class Log {
  private static boolean debug = true;

  public static void driver(String msg) {
    System.out.printf("[\033[0;92mDriver\033[0m] %s\033[0m\n", msg);
  }

  public static void exec(String msg) {
    System.out.printf("[Exec %d] %s\033[0m\n", ProcessorUtil.getCurrentId(), msg);
  }

  public static void debug(String msg) {
    if (debug) {
      System.out.print("\033[0;90m[DEBUG]");
      Log.exec(msg);
    }
  }
}
