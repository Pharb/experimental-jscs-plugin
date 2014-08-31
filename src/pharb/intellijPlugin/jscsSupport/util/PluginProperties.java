package pharb.intellijPlugin.jscsSupport.util;


import com.intellij.ide.util.PropertiesComponent;

import static pharb.intellijPlugin.jscsSupport.util.PluginProperties.GlobalPluginProperties.JSCS_NATIVE_MIN_VERSION_REQUIREMENT;
import static pharb.intellijPlugin.jscsSupport.util.PluginProperties.GlobalPluginProperties.JSCS_PLUGIN_VERSION_LAST_USED;

public class PluginProperties {

    public static final String VERSION = "0.2.0";

    private static PropertiesComponent properties = PropertiesComponent.getInstance();

    /**
     * @return whether or not this {major}.{minor} version is been used the first time.
     */
    public static boolean isVersionFirstUsed() {
        String[] lastVersionUsed = JSCS_PLUGIN_VERSION_LAST_USED.get().split("\\.");
        String[] currentVersion = VERSION.split("\\.");

        return Integer.parseInt(lastVersionUsed[0]) < Integer.parseInt(currentVersion[0])
                || Integer.parseInt(lastVersionUsed[1]) < Integer.parseInt(currentVersion[1]);
    }

    public static void setVersionUsed() {
        JSCS_PLUGIN_VERSION_LAST_USED.set(VERSION);
    }

    /**
     * @param nativeVersionString jscs version as {major}.{minor}.{build}
     * @return whether or not the minimal required jscs version is being used.
     */
    public static boolean isNativeVersionRequirementSatisfied(String nativeVersionString) {
        String[] requiredVersion = JSCS_NATIVE_MIN_VERSION_REQUIREMENT.get().split("\\.");
        String[] nativeVersion = nativeVersionString.split("\\.");


        return Integer.parseInt(nativeVersion[0]) >= Integer.parseInt(requiredVersion[0])
                && Integer.parseInt(nativeVersion[1]) >= Integer.parseInt(requiredVersion[1])
                && Integer.parseInt(nativeVersion[2]) >= Integer.parseInt(requiredVersion[2]);
    }

    public static enum GlobalPluginProperties {

        JSCS_PLUGIN_VERSION_LAST_USED("jscsPluginVersionLastUsed", "0.0.0"),
        JSCS_NATIVE_MIN_VERSION_REQUIREMENT("jscsNativeMinVersionRequirement", "1.6.1");

        private final String key;
        private final String defaultValue;

        private GlobalPluginProperties(String key, String defaultValue) {
            this.key = key;
            this.defaultValue = defaultValue;
        }

        public String get() {
            return properties.getValue(key, defaultValue);
        }

        public void set(String value) {
            properties.setValue(key, value);
        }
    }
}
