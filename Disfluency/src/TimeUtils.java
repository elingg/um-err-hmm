import java.util.Calendar;
import java.text.SimpleDateFormat;

public class TimeUtils {
  public static final String DATE_FORMAT_NOW = "HH:mm:ss MM-dd-yyyy";

  public static String now() {
    Calendar cal = Calendar.getInstance();
    SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
    return sdf.format(cal.getTime());

  }

  public static void  main(String arg[]) {
    System.out.println("Now : " + TimeUtils.now());
  }
}

