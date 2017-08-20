package ro.pub.cs.emergencymobileapp.utils;

public interface Constants {

	public static final String HOST = "192.168.1.102";
	public static final int TCP_PORT = 9999;
    public static final int UDP_PORT = 9998;

	public static final String TYPE_KEY = "TYPE";
	public static final String NO_OF_PEOPLE_KEY = "NO_OF_PEOPLE";
	public static final String PRIORITY_KEY = "PRIORITY";
	public static final String SPECIALIZATION_KEY = "SPECIALIZATION";
	public static final String LATITUDE_KEY = "LATITUDE";
	public static final String LONGITUDE_KEY = "LONGITUDE";
	public static final String INCIDENT_KEY = "INCIDENT";

	public static String  TAG                                = "myapp";

	public static boolean DEBUG                              = true;

	public static int     MAGENTA_POSITION                   = 0;
	public static int     VIOLET_POSITION                    = 1;
	public static int     ORANGE_POSITION                    = 2;
	public static int     RED_POSITION                       = 3;
	public static int     BLUE_POSITION                      = 4;
	public static int     GREEN_POSITION                     = 5;
	public static int     AZURE_POSITION                     = 6;
	public static int     ROSE_POSITION                      = 7;
	public static int     CYAN_POSITION                      = 8;
	public static int     YELLOW_POSITION                    = 9;

	public static int     CAMERA_ZOOM                        = 12;

	public static long    LOCATION_REQUEST_INTERVAL          = 10000;
	public static long    LOCATIION_REQUEST_FASTEST_INTERVAL = 5000;

	public static String  LOCATION_UPDATES_STATUS            = "LocationUpdatesStatus";
	public static String  LAST_LOCATION                      = "LastLocation";

	public static long LOCATION_REFRESH_TIME = 10;
	public static float LOCATION_REFRESH_DISTANCE = 10;

}
