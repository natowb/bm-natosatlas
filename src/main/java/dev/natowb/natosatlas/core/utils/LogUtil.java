package dev.natowb.natosatlas.core.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public final class LogUtil {

    private static final String NS = "NatosAtlas";
    private static final SimpleDateFormat TIME = new SimpleDateFormat("HH:mm:ss");

    private LogUtil() {
    }

    public static void info(String source, String format, Object... args) {
        System.out.println(prefix("INFO", source) + format(format, args));
    }

    public static void warn(String source, String format, Object... args) {
        System.out.println(prefix("WARN", source) + format(format, args));
    }

    public static void debug(String source, String format, Object... args) {
        System.out.println(prefix("DBUG", source) + format(format, args));
    }

    public static void error(String source, String format, Object... args) {
        System.err.println(prefix("ERRR", source) + format(format, args));
    }

    public static void error(String source, Throwable t, String format, Object... args) {
        System.err.println(prefix("ERROR", source) + format(format, args));
        t.printStackTrace(System.err);
    }

    private static String prefix(String level, String source) {
        String time = TIME.format(new Date());
        return "[" + time + "] [" + NS + "/" + level + "] (" + source + ") ";
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

            sb.append(template.substring(pos, brace));

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
