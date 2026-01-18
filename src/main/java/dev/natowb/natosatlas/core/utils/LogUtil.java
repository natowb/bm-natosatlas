package dev.natowb.natosatlas.core.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public final class LogUtil {

    private static final String MOD = "NatosAtlas";
    private static final SimpleDateFormat TIME = new SimpleDateFormat("HH:mm:ss");

    private static final String BLUE  = "\u001B[34m";
    private static final String GREEN = "\u001B[32m";
    private static final String CYAN  = "\u001B[36m";
    private static final String RESET = "\u001B[0m";

    private LogUtil() {}

    public static void info(String format, Object... args) {
        System.out.println(prefix("INFO") + format(format, args) + RESET);
    }

    public static void warn(String format, Object... args) {
        System.out.println(prefix("WARN") + format(format, args) + RESET);
    }

    public static void debug(String format, Object... args) {
        System.out.println(prefix("DBUG") + format(format, args) + RESET);
    }

    public static void error(String format, Object... args) {
        System.err.println(prefix("ERRR") + format(format, args) + RESET);
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
