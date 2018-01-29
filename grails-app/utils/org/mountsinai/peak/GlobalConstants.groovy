package org.mountsinai.peak

/**
 * Created by rajan on 5/17/16.
 */
public final class GlobalConstants {

    private static boolean isProd = true
    private static String AUTH_URL_BASE_DEV = "http://msh-dev01.mountsinai.org/MSHSauth"
    private static String AUTH_URL_BASE_PROD = "http://msh-app03.mountsinai.org:9080/MSHSauth"
    
    protected static String AUTH_SERVICE_ENDPOINT = "/service"


    protected static getAuthURLBase() {
        if(isProd) {
            return AUTH_URL_BASE_PROD
        } else {
            return AUTH_URL_BASE_DEV
        }
    }

}
