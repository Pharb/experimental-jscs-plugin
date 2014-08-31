package pharb.intellijPlugin.jscsSupport.util;


import com.intellij.ide.util.PropertiesComponent;

import static pharb.intellijPlugin.jscsSupport.JscsProjectComponent.VERSION;

public class PluginProperties {

    private static PropertiesComponent properties = PropertiesComponent.getInstance();

    /**
     * @return whether or not this {major}.{minor} version is been used the first time.
     */
    public static boolean isVersionFirstUsed() {
        String[] lastVersionUsed = GlobalPluginProperties.JSCS_PLUGIN_VERSION_LAST_USED.get().split("\\.");
        String[] currentVersion = VERSION.split("\\.");

        return Integer.parseInt(lastVersionUsed[0]) < Integer.parseInt(currentVersion[0])
                || Integer.parseInt(lastVersionUsed[1]) < Integer.parseInt(currentVersion[1]);
    }

    public static enum GlobalPluginProperties {

        JSCS_PLUGIN_VERSION_LAST_USED("jscsPluginVersionLastUsed", "0.0.0"),
        JSCS_NATIVE_MIN_VERSION_REQUIREMENT("jscsNativeMinVersionRequirement", "1.6.0");

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

        public boolean equalsValue(String other) {
            return this.get().equals(other);
        }
    }
}
