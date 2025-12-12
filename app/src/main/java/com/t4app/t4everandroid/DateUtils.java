package com.t4app.t4everandroid;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

public class DateUtils {
    private static final TimeZone UTC = TimeZone.getTimeZone("UTC");
    private static final SimpleDateFormat SDF =
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);

    static {
        SDF.setTimeZone(UTC);
        SDF.setLenient(false);
    }

    public static long toEpochMillis(String iso) {
        if (iso == null) return Long.MIN_VALUE;
        String normalized = normalizeToMillis(iso);
        try {
            return SDF.parse(normalized).getTime();
        } catch (ParseException e) {
            return Long.MIN_VALUE;
        }
    }

    public static boolean isAfter(String aIso, String bIso) {
        return toEpochMillis(aIso) > toEpochMillis(bIso);
    }

    private static String normalizeToMillis(String iso) {
        if (!iso.endsWith("Z")) return iso;

        String base = iso.substring(0, iso.length() - 1);
        int dot = base.indexOf('.');

        if (dot == -1) return base + ".000Z";

        String left = base.substring(0, dot);
        String frac = base.substring(dot + 1);

        String ms;
        if (frac.length() >= 3) ms = frac.substring(0, 3);
        else if (frac.length() == 2) ms = frac + "0";
        else if (frac.length() == 1) ms = frac + "00";
        else ms = "000";

        return left + "." + ms + "Z";
    }
}
