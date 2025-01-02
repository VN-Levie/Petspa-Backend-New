package vn.aptech.petspa.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.regex.Pattern;

public final class ZDebug {
    public class TIME_COST {
        public static final long BUA_VC_X3 = 1000 * 60 * 60 * 1; // 1 hour
        public static final long BUA_VC_X5 = 1000 * 60 * 60 * 1; // 1 hour
        public static final long BUA_VC_SPEED = 1000 * 60 * 60 * 1; // 1 hour
        public static final long ONE_HOUR = 1000 * 60 * 60 * 1; // 1 hour
        public static final long ONE_THOUSAND_HOUR = 1000L * 60 * 60 * 1000; // 1000 hours
        // 10000 giờ
        public static final long TEN_THOUSAND_HOUR = 1000L * 60 * 60 * 10000; // 10000 hours

        // 10p
        public static final long TEN_MINUTE = 1000 * 60 * 10;
    }

    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_BLACK = "\u001B[30m";
    private static final String ANSI_RED = "\u001B[31m";
    // dark red
    private static final String ANSI_DARK_RED = "\u001B[31;2m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_BLUE = "\u001B[34m";
    private static final String ANSI_PURPLE = "\u001B[35m";
    private static final String ANSI_CYAN = "\u001B[36m";
    private static final String ANSI_WHITE = "\u001B[37m";

    // instance
    private static ZDebug instance = null;

    public static ZDebug gI() {
        if (instance == null) {
            instance = new ZDebug();
        }
        return instance;
    }

    private static final Random rand;
    static {
        rand = new Random();
        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }

    public static boolean checkNumInt(String num) {
        return Pattern.compile("^[0-9]+$").matcher(num).find();
    }

    public static int UnsignedByte(byte b) {
        int ch = b;
        if (ch < 0) {
            return ch + 256;
        }
        return ch;
    }

    public static String parseString(String str, String wall) {
        return str.contains(wall) ? str.substring(str.indexOf(wall) + 1) : null;
    }

    public static boolean CheckString(String str, String c) {
        return Pattern.compile(c).matcher(str).find();
    }

    public static int nextInt(int from, int to) {
        return from + rand.nextInt(to - from);
    }

    public static int nextInt(int max) {
        return rand.nextInt(max);
    }

    public static int nextInt(int[] percen) {
        int next = nextInt(1000), i;
        for (i = 0; i < percen.length; i++) {
            if (next < percen[i])
                return i;
            next -= percen[i];
        }
        return i;
    }

    public static int currentTimeSec() {
        return (int) System.currentTimeMillis() / 1000;
    }

    public static String replace(String text, String regex, String replacement) {
        return text.replace(regex, replacement);
    }

    public static String strDate() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM hh:mm:ss");
        String strDate = formatter.format(date);
        strDate = ANSI_CYAN + "[" + strDate + "] " + ANSI_RESET;
        return strDate;
    }

    public static String strDate2() {
        try {
            Date date = new Date(System.currentTimeMillis());
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM hh:mm:ss");
            String strDate = formatter.format(date);
            strDate = "[" + strDate + "]: ";
            return strDate;
        } catch (Exception e) {
            System.out.println("strDate2: " + e);
        }
        return null;

    }

    public void log(String message) {
        try {
            message = message.substring(0, 1).toUpperCase() + message.substring(1);

            System.out.println(strDate() + ANSI_BLACK + message + ANSI_RESET);

        } catch (Exception e) {
            System.out.println("Util.gI().log: " + e.getCause());
        }

    }

    public void error(String message) {
        try {

            if (message == null || message.equals("") || message.equals(" ") || message.equals("null")) {
                message = "error_null";
            }
            message = message.substring(0, 1).toUpperCase() + message.substring(1);

            System.out.println(strDate() + ANSI_RED + message + ANSI_RESET);

        } catch (Exception e) {
            System.out.println("Uitl.error: " + e.getCause());
        }

    }

    public void info(String message) {
        try {
            if (message == null || message.equals("") || message.equals(" ") || message.equals("null")) {
                message = "info_null";
            }
            message = message.substring(0, 1).toUpperCase() + message.substring(1);

            System.out.println(strDate() + ANSI_BLUE + message + ANSI_RESET);

        } catch (Exception e) {
            System.out.println("Util.gI().info: " + e.getCause());
        }
    }

    public void debug(String message) {
        try {

            message = message.substring(0, 1).toUpperCase() + message.substring(1);
            System.out.println(strDate() + ANSI_DARK_RED + message + ANSI_RESET);

        } catch (Exception e) {
            System.out.println("Util.gI().debug: " + e.getCause());
        }
    }

    // sucess
    public void success(String message) {
        try {

            if (message == null || message.equals("") || message.equals(" ") || message.equals("null")) {
                message = "success_null";
            }
            message = message.substring(0, 1).toUpperCase() + message.substring(1);

            System.out.println(strDate() + ANSI_GREEN + message + ANSI_RESET);

        } catch (Exception e) {
            System.out.println("Util.gI().success: " + e.getCause());
        }
    }

    public void ZigDebug(String message) {

        message = message.substring(0, 1).toUpperCase() + message.substring(1);
        System.out.println(strDate() + ANSI_PURPLE + message + ANSI_RESET);

    }

    public static int random(int i, int j) {
        return rand.nextInt(j - i) + i;

    }

    public void logException(String string, Exception e) {
        try {
            // Kiểm tra chuỗi đầu vào và gán giá trị mặc định nếu cần
            if (string == null || string.trim().isEmpty() || string.equals("null")) {
                string = "error_null";
            }

            // Viết hoa ký tự đầu tiên
            string = string.substring(0, 1).toUpperCase() + string.substring(1);

            System.out.println(strDate() + ANSI_DARK_RED + string + ": " + e.getMessage());
            e.printStackTrace(); // In ra toàn bộ stack trace
            System.out.println(ANSI_RESET);

        } catch (Exception ex) {
            // In thông tin lỗi trong quá trình xử lý exception
            System.out.println("Uitl.logException: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public void logException(Exception e) {
        try {

            System.out.println(strDate() + ANSI_DARK_RED + ": " + e.getLocalizedMessage() + ANSI_RESET);

        } catch (Exception ex) {
            System.out.println("Uitl.logException: " + ex.getCause());
        }
    }

    public int randomNumber(int i, int j) {
        return rand.nextInt(j - i) + i;
    }

    // number format
    public static String numberFormat(int number) {
        return String.format("%,d", number);
    }

}
