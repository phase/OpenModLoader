package xyz.openmodloader.config;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.google.common.collect.Lists;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigRenderOptions;
import com.typesafe.config.ConfigValueFactory;

/**
 * The base config class.
 * Example:
 * <code>
 * <br>Config config = new Config("example.conf");
 * <br>Config category1 = config.getConfig("category1", "Enables and disables blocks");
 * <br>if (category1.getBoolean("block1", true, "Enables block1"))
 * <br>    enableBlock1();
 * </code>
 */
public class Config {

    /**
     * The config directory.
     */
    public static final File CONFIG_DIR = new File("./config");

    /**
     * Creates a new config within the config directory, using the filename provided.
     * 
     * @param file The name of the config file. Must be a .conf file.
     */
    public Config(String file) {
        this(new File(CONFIG_DIR, file));
    }

    /**
     * Creates a new config using the file provided.
     *
     * @param file The file to use for the config.
     * Must be a .conf file located within the config directory.
     */
    public Config(File file) {
        if (!file.getName().endsWith(".conf")) {
            throw new IllegalArgumentException("Config file has to be a .conf file");
        }
        boolean flag = false;
        File parent = file.getParentFile();
        while (parent != null && !flag) {
            if (parent.equals(CONFIG_DIR)) {
                flag = true;
            }
        }
        if (!flag) {
            throw new IllegalArgumentException("Config file has to be located within ./config or one of its subdirectories");
        }
        this.file = file;
        this.config = ConfigFactory.parseFile(file);
        this.initialHash = System.identityHashCode(config);
        this.name = null;
        this.parent = null;
    }

    /**
     * Gets a boolean from the config.
     * If the property doesn't exist,
     * a new one is added, with the provided
     * default value.
     *
     * @param name The name of the property
     * @param defaultValue The default value of the property.
     * @param comment The comment to set for the property.
     * @return The requested boolean property, or the defaultValue if it doesn't exist.
     */
    public boolean getBoolean(String name, boolean defaultValue, String comment) {
        if (!config.hasPath(name)) {
            config = config.withValue(name, ConfigValueFactory.fromAnyRef(defaultValue, comment));
            if (parent != null) {
                parent.update(this);
            }
        }
        return config.getBoolean(name);
    }

    /**
     * Gets an integer from the config.
     * If the property doesn't exist,
     * a new one is added, with the provided
     * default value.
     *
     * @param name The name of the property
     * @param defaultValue The default value of the property.
     * @param comment The comment to set for the property.
     * @return The requested integer property, or the defaultValue if it doesn't exist.
     */
    public int getInt(String name, int defaultValue, String comment) {
        if (!config.hasPath(name)) {
            config = config.withValue(name, ConfigValueFactory.fromAnyRef(defaultValue, comment));
            if (parent != null) {
                parent.update(this);
            }
        }
        return config.getInt(name);
    }

    /**
     * Gets a long from the config.
     * If the property doesn't exist,
     * a new one is added, with the provided
     * default value.
     *
     * @param name The name of the property
     * @param defaultValue The default value of the property.
     * @param comment The comment to set for the property.
     * @return The requested long property, or the defaultValue if it doesn't exist.
     */
    public long getLong(String name, long defaultValue, String comment) {
        if (!config.hasPath(name)) {
            config = config.withValue(name, ConfigValueFactory.fromAnyRef(defaultValue, comment));
            if (parent != null) {
                parent.update(this);
            }
        }
        return config.getLong(name);
    }

    /**
     * Gets a double from the config.
     * If the property doesn't exist,
     * a new one is added, with the provided
     * default value.
     *
     * @param name The name of the property
     * @param defaultValue The default value of the property.
     * @param comment The comment to set for the property.
     * @return The requested double property, or the defaultValue if it doesn't exist.
     */
    public double getDouble(String name, double defaultValue, String comment) {
        if (!config.hasPath(name)) {
            config = config.withValue(name, ConfigValueFactory.fromAnyRef(defaultValue, comment));
            if (parent != null) {
                parent.update(this);
            }
        }
        return config.getDouble(name);
    }

    /**
     * Gets a string from the config.
     * If the property doesn't exist,
     * a new one is added, with the provided
     * default value.
     *
     * @param name The name of the property
     * @param defaultValue The default value of the property.
     * @param comment The comment to set for the property.
     * @return The requested string property, or the defaultValue if it doesn't exist.
     */
    public String getString(String name, String defaultValue, String comment) {
        if (!config.hasPath(name)) {
            config = config.withValue(name, ConfigValueFactory.fromAnyRef(defaultValue, comment));
            if (parent != null) {
                parent.update(this);
            }
        }
        return config.getString(name);
    }

    /**
     * Gets a subconfig from the config.
     * If the property doesn't exist,
     * a new one is added, with the provided
     * default value.
     *
     * @param name The name of the property
     * @param defaultValue The default value of the property.
     * @param comment The comment to set for the property.
     * @return The requested subconfig property, or the defaultValue if it doesn't exist.
     */
    public Config getConfig(String name, String comment) {
        if (!config.hasPath(name)) {
            config = config.withValue(name, ConfigValueFactory.fromMap(Collections.emptyMap(), comment));
            if (parent != null) {
                parent.update(this);
            }
        }
        return new Config(name, config.getConfig(name), this);
    }

    /**
     * Gets a list of booleans from the config.
     * If the property doesn't exist,
     * a new one is added, with the provided
     * default value.
     *
     * @param name The name of the property
     * @param defaultValue The default value of the property.
     * @param comment The comment to set for the property.
     * @return The requested boolean list property, or the defaultValue if it doesn't exist.
     */
    public List<Boolean> getBooleanList(String name, List<Boolean> defaultValue, String comment) {
        if (!config.hasPath(name)) {
            config = config.withValue(name, ConfigValueFactory.fromAnyRef(defaultValue, comment));
            if (parent != null) {
                parent.update(this);
            }
        }
        return config.getBooleanList(name);
    }

    /**
     * Gets a list of integers from the config.
     * If the property doesn't exist,
     * a new one is added, with the provided
     * default value.
     *
     * @param name The name of the property
     * @param defaultValue The default value of the property.
     * @param comment The comment to set for the property.
     * @return The requested integer list property, or the defaultValue if it doesn't exist.
     */
    public List<Integer> getIntList(String name, List<Integer> defaultValue, String comment) {
        if (!config.hasPath(name)) {
            config = config.withValue(name, ConfigValueFactory.fromAnyRef(defaultValue, comment));
            if (parent != null) {
                parent.update(this);
            }
        }
        return config.getIntList(name);
    }

    /**
     * Gets a list of longs from the config.
     * If the property doesn't exist,
     * a new one is added, with the provided
     * default value.
     *
     * @param name The name of the property
     * @param defaultValue The default value of the property.
     * @param comment The comment to set for the property.
     * @return The requested long list property, or the defaultValue if it doesn't exist.
     */
    public List<Long> getLongList(String name, List<Long> defaultValue, String comment) {
        if (!config.hasPath(name)) {
            config = config.withValue(name, ConfigValueFactory.fromAnyRef(defaultValue, comment));
            if (parent != null) {
                parent.update(this);
            }
        }
        return config.getLongList(name);
    }

    /**
     * Gets a list of doubles from the config.
     * If the property doesn't exist,
     * a new one is added, with the provided
     * default value.
     *
     * @param name The name of the property
     * @param defaultValue The default value of the property.
     * @param comment The comment to set for the property.
     * @return The requested double list property, or the defaultValue if it doesn't exist.
     */
    public List<Double> getDoubleList(String name, List<Double> defaultValue, String comment) {
        if (!config.hasPath(name)) {
            config = config.withValue(name, ConfigValueFactory.fromAnyRef(defaultValue, comment));
            if (parent != null) {
                parent.update(this);
            }
        }
        return config.getDoubleList(name);
    }

    /**
     * Gets a list of strings from the config.
     * If the property doesn't exist,
     * a new one is added, with the provided
     * default value.
     *
     * @param name The name of the property
     * @param defaultValue The default value of the property.
     * @param comment The comment to set for the property.
     * @return The requested string list property, or the defaultValue if it doesn't exist.
     */
    public List<String> getStringList(String name, List<String> defaultValue, String comment) {
        if (!config.hasPath(name)) {
            config = config.withValue(name, ConfigValueFactory.fromAnyRef(defaultValue, comment));
            if (parent != null) {
                parent.update(this);
            }
        }
        return config.getStringList(name);
    }

    /**
     * Gets a list of subconfigs from the config.
     * If the property doesn't exist,
     * a new one is added, with the provided
     * default value.
     *
     * @param name The name of the property
     * @param defaultValue The default value of the property.
     * @param comment The comment to set for the property.
     * @return The requested subconfig list property, or the defaultValue if it doesn't exist.
     */
    public List<Config> getConfigList(String name, String comment) {
        if (!config.hasPath(name)) {
            config = config.withValue(name, ConfigValueFactory.fromIterable(Collections.emptyList()));
            if (parent != null) {
                parent.update(this);
            }
        }
        return Lists.transform(config.getConfigList(name), (c) -> new Config(name, c, this));
    }

    /**
     * Checks whether a property with
     * the specified name exists.
     *
     * @param name The name of the property.
     * @return true, if the property exists.
     */
    public boolean hasProperty(String name) {
        return config.hasPath(name);
    }

    /**
     * Checks if the config is empty.
     *
     * @return true, if the config is empty
     */
    public boolean isEmpty() {
        return config.isEmpty();
    }

    /**
     * Checks if the config has changed since it was loaded.
     *
     * @return true, if the config has changed since it was loaded.
     */
    public boolean hasChanged() {
        return System.identityHashCode(config) != initialHash;
    }

    /**
     * Saves the config to disc.
     */
    public void save() {
        if (file == null) {
            throw new UnsupportedOperationException("Only root configs can be saved!");
        }
        if (hasChanged()) {
            try {
                FileUtils.writeStringToFile(file, config.root().render(ConfigRenderOptions.defaults().setJson(false).setOriginComments(true).setComments(false)));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * INTERNAL.
     */

    private final int initialHash;
    private final File file;
    private final Config parent;
    private final String name;
    private com.typesafe.config.Config config;

    private Config(String name, com.typesafe.config.Config config, Config parent) {
        this.file = null;
        this.config = config;
        this.initialHash = System.identityHashCode(config);
        this.parent = parent;
        this.name = name;
    }

    private void update(Config child) {
        this.config = this.config.withValue(child.name, child.config.root());
        if (parent != null) {
            parent.update(this);
        }
    }
}
