package net.fpsboost.util;

import org.apache.logging.log4j.LogManager;

/**
 * @author LangYa466
 * @since 2/6/2025
 */
@SuppressWarnings({"StringConcatenationArgumentToLogCall","LoggingSimilarMessage"})
public class Logger {
    public static final org.apache.logging.log4j.Logger logger = LogManager.getLogger("FPSBoost-Client");
    private static final String PREFIX = "[FPSBoost] ";

    public static void info(String message, Object... args) {
        logger.info(PREFIX + message, args);
    }

    public static void warn(String message, Object... args) {
        logger.warn(PREFIX + message, args);
    }

    public static void error(String message, Object... args) {
        logger.error(PREFIX + message, args);
    }

    public static void error(Exception e) {
        logger.error(PREFIX + e);
    }

    public static void debug(String message, Object... args) {
        logger.debug(PREFIX + message, args);
    }
}
