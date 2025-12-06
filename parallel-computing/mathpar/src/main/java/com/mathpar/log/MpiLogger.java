package com.mathpar.log;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import mpi.MPI;
import mpi.MPIException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Logger wrapper class for logging in MPI Applications.
 * <br/>
 * Adds wrapper methods that add Rank info to log messages
 * <br/>
 * Log Messages hierarchy (from top to bottom): ERROR, WARN, INFO, DEBUG, TRACE
 * <br/>
 * Logging level can be configured via <b>log4j2.properties</b> configuration file in <b>/src/main/resources</b>
 * <br/>
 * We can change level for application using rootLogger.level:
 * rootLogger.level=info - will show info INFO, WARN and ERROR log messages
 * <br/><br/>
 * To all logger to the class and user it - declare logger variable as follows:
 * <br/>
 * class SomeClass {<br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;private final static MpiLogger LOGGER = MpiLogger.getLogger(SomeClass.class);<br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;...<br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;public void someMethod() {<br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;LOGGER.info("Some message to be logged");<br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;}<br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;...<br/>
 * }<br/>
 * <br/>
 * To use logger in MPI applications, log4j2 libraries must be in classpath while running java
 * application, i.e.
 * <br/>
 * bash> mpirun --hostfile hostfile -np 9 java -cp /home/teacher/stemedu/target/classes:/home/teacher/stemedu/target/lib/log4j-api-2.12.1.jar:/home/teacher/dap/target/lib/log4j-core-2.12.1.jar com.mathpar.students.ukma.atsaruk.mpi.HelloWorld
 * <br/><br/>
 * Also applications can be run via <b>runclass.sh</b> script from project root that creates classpath automatically:
 * <br/>
 * bash> ./runclass.sh -np 9 com.mathpar.students.ukma.atsaruk.mpi.HelloWorld
 */
public class MpiLogger {

  private final Logger logger;

  private final static Map<Class, MpiLogger> loggers = new ConcurrentHashMap<>();

  private MpiLogger(Class clazz) {
    logger = LogManager.getLogger(clazz);
  }

  public static MpiLogger getLogger(Class clazz) {
    if (loggers.containsKey(clazz)) {
      return loggers.get(clazz);
    }
    MpiLogger logger = new MpiLogger(clazz);
    loggers.put(clazz, logger);

    return logger;
  }

  private String addRank(String message) {
    try {
      return "Rank[" + MPI.COMM_WORLD.getRank() + "] " + message;
    } catch (MPIException ex) {
      return "Rank[-1] " + message;
    }
  }

  public void trace(String message) {
    logger.trace(addRank(message));
  }

  public void trace(Object obj) {
    logger.trace(addRank(obj.toString()));
  }

  public void trace(String message, Throwable throwable) {
    logger.trace(addRank(message), throwable);
  }

  public void trace(String message, Object... objects) {
    logger.trace(addRank(message), objects);
  }

  public void debug(String message) {
    logger.debug(addRank(message));
  }

  public void debug(Object obj) {
    logger.debug(addRank(obj.toString()));
  }

  public void debug(String message, Throwable throwable) {
    logger.debug(addRank(message), throwable);
  }

  public void debug(String message, Object... objects) {
    logger.debug(addRank(message), objects);
  }

  public void info(String message) {
    logger.info(addRank(message));
  }

  public void info(Object obj) {
    logger.info(addRank(obj.toString()));
  }

  public void info(String message, Throwable throwable) {
    logger.info(addRank(message), throwable);
  }

  public void info(String message, Object... objects) {
    logger.info(addRank(message), objects);
  }

  public void warn(String message) {
    logger.warn(addRank(message));
  }

  public void warn(Object obj) {
    logger.warn(addRank(obj.toString()));
  }

  public void warn(String message, Throwable throwable) {
    logger.warn(addRank(message), throwable);
  }

  public void warn(String message, Object... objects) {
    logger.warn(addRank(message), objects);
  }

  public void error(String message) {
    logger.error(addRank(message));
  }

  public void error(Object obj) {
    logger.error(addRank(obj.toString()));
  }

  public void error(String message, Throwable throwable) {
    logger.error(addRank(message), throwable);
  }

  public void error(String message, Object... objects) {
    logger.error(addRank(message), objects);
  }
}
