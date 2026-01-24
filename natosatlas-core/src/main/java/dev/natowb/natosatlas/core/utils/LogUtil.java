package dev.natowb.natosatlas.core.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public final class LogUtil {

    public enum LogLevel {
        ERROR(0),
        WARN(1),
        INFO(2),
        DEBUG(3),
        TRACE(4);

        final int priority;
        LogLevel(int p) { this.priority = p; }
    }

    private static LogLevel currentLevel = LogLevel.DEBUG;

    public static void setLoggingLevel(LogLevel level) {
        currentLevel = level;
        log(LogLevel.INFO, "Logging level set to {}", level);
    }

    private static boolean shouldLog(LogLevel level) {
        return level.priority <= currentLevel.priority;
    }

    private static final String MOD = "NatosAtlas";
    private static final SimpleDateFormat TIME = new SimpleDateFormat("HH:mm:ss");

    private static final String ANSI_BLUE  = "\u001B[34m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_CYAN  = "\u001B[36m";
    private static final String ANSI_RESET = "\u001B[0m";

    private static final boolean SUPPORTS_ANSI = detectAnsiSupport();

    private static final String BLUE  = SUPPORTS_ANSI ? ANSI_BLUE  : "";
    private static final String GREEN = SUPPORTS_ANSI ? ANSI_GREEN : "";
    private static final String CYAN  = SUPPORTS_ANSI ? ANSI_CYAN  : "";
    private static final String RESET = SUPPORTS_ANSI ? ANSI_RESET : "";

    private LogUtil() {}

    private static boolean detectAnsiSupport() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            return System.getenv("TERM") != null ||
                    System.getenv("WT_SESSION") != null ||
                    System.getenv("ConEmuANSI") != null ||
                    System.getenv("ANSICON") != null;
        }
        String term = System.getenv("TERM");
        return term != null && !term.equals("dumb");
    }

    private static void log(LogLevel level, String format, Object... args) {
        if (!shouldLog(level)) return;
        String msg = prefix(level.name()) + format(format, args) + RESET;
        if (level == LogLevel.ERROR) System.err.println(msg);
        else System.out.println(msg);
    }

    public static void info(String format, Object... args) {
        log(LogLevel.INFO, format, args);
    }

    public static void warn(String format, Object... args) {
        log(LogLevel.WARN, format, args);
    }

    public static void debug(String format, Object... args) {
        log(LogLevel.DEBUG, format, args);
    }

    public static void trace(String format, Object... args) {
        log(LogLevel.TRACE, format, args);
    }

    public static void error(String format, Object... args) {
        log(LogLevel.ERROR, format, args);
    }

    private static String prefix(String level) {
        String time   = TIME.format(new Date());
        String thread = Thread.currentThread().getName();

        String timePart   = BLUE  + "[" + time   + "]" + RESET;
        String threadPart = GREEN + "[" + thread + "/" + level + "]" + RESET;
        String modPart    = CYAN  + "[" + MOD    + "]" + RESET;

        return timePart + " " + threadPart + " " + modPart + " ";
    }

    private static String format(String template, Object... args) {
        if (template == null) return "null";
        if (args == null || args.length == 0) return template;

        StringBuilder sb = new StringBuilder(template.length() + 32);
        int argIndex = 0;
        int pos = 0;

        while (true) {
            int brace = template.indexOf("{}", pos);
            if (brace == -1) {
                sb.append(template.substring(pos));
                break;
            }
            sb.append(template, pos, brace);
            if (argIndex < args.length) {
                Object arg = args[argIndex++];
                sb.append(arg == null ? "null" : arg.toString());
            } else {
                sb.append("{}");
            }
            pos = brace + 2;
        }

        return sb.toString();
    }
}
