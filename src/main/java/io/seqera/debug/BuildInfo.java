package io.seqera.debug;

import java.util.Properties;

/**
 * Registry build info helper
 *
 * @author Paolo Di Tommaso <paolo.ditommaso@gmail.com>
 */
public class BuildInfo {

    private static final Properties properties;

    static {
        final String BUILD_INFO = "/META-INF/build-info.properties";
        properties = new Properties();
        try {
            properties.load( BuildInfo.class.getResourceAsStream(BUILD_INFO) );
        }
        catch( Exception e ) {
            System.err.println("Unable to parse $BUILD_INFO - Cause " + e.getMessage());
        }
    }

    public static Properties getProperties() {
        return properties;
    }

    public static String getVersion() {
        return properties.getProperty("version");
    }

    public static String getCommitId() {
        return properties.getProperty("commitId");
    }

    public static String getName() {
        return properties.getProperty("name");
    }

    public static String getFullVersion() {
        return BuildInfo.getVersion() + "_" + BuildInfo.getCommitId();
    }

}
