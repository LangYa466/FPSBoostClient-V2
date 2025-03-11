package net.fpsboost.util;

import net.fpsboost.Client;
import org.apache.logging.log4j.LogManager;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @author LangYa466
 * @since 2/6/2025
 */
@SuppressWarnings({"StringConcatenationArgumentToLogCall", "LoggingSimilarMessage"})
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
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        logger.error(PREFIX + sw.toString());
    }

    public static void debug(String message, Object... args) {
        if (!Client.isDev) return;
        logger.info(PREFIX + message, args);
    }
}
