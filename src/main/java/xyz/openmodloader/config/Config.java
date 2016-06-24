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

public class Config {

    private static final File CFG_DIR = new File("./config");

    private final int initialHash;
    private final File file;
    private final Config parent;
    private final String path;
    private com.typesafe.config.Config config;

    public Config(String file) {
        this(new File(CFG_DIR, file));
    }

    public Config(File file) {
        boolean b = false;
        File parent = file.getParentFile();
        while (parent != null && !b)
            if (parent.equals(CFG_DIR))
                b = true;
        if (!b)
            throw new IllegalArgumentException("Config file has to be located within ./config");
        this.file = file;
        this.config = ConfigFactory.parseFile(file);
        this.initialHash = System.identityHashCode(config);
        this.path = null;
        this.parent = null;
    }

    private Config(String path, com.typesafe.config.Config config, Config parent) {
        this.file = null;
        this.config = config;
        this.initialHash = System.identityHashCode(config);
        this.parent = parent;
        this.path = path;
    }

    public boolean getBoolean(String path, boolean defaultValue, String comment) {
        if (!config.hasPath(path)) {
            config = config.withValue(path, ConfigValueFactory.fromAnyRef(defaultValue, comment));
            if (parent != null)
                parent.update(this);
        }
        return config.getBoolean(path);
    }

    public int getInt(String path, int defaultValue, String comment) {
        if (!config.hasPath(path)) {
            config = config.withValue(path, ConfigValueFactory.fromAnyRef(defaultValue, comment));
            if (parent != null)
                parent.update(this);
        }
        return config.getInt(path);
    }

    public long getLong(String path, long defaultValue, String comment) {
        if (!config.hasPath(path)) {
            config = config.withValue(path, ConfigValueFactory.fromAnyRef(defaultValue, comment));
            if (parent != null)
                parent.update(this);
        }
        return config.getLong(path);
    }

    public double getDouble(String path, double defaultValue, String comment) {
        if (!config.hasPath(path)) {
            config = config.withValue(path, ConfigValueFactory.fromAnyRef(defaultValue, comment));
            if (parent != null)
                parent.update(this);
        }
        return config.getDouble(path);
    }

    public String getString(String path, String defaultValue, String comment) {
        if (!config.hasPath(path)) {
            config = config.withValue(path, ConfigValueFactory.fromAnyRef(defaultValue, comment));
            if (parent != null)
                parent.update(this);
        }
        return config.getString(path);
    }

    public Config getConfig(String path, String comment) {
        if (!config.hasPath(path)) {
            config = config.withValue(path, ConfigValueFactory.fromMap(Collections.emptyMap(), comment));
            if (parent != null)
                parent.update(this);
        }
        return new Config(path, config.getConfig(path), this);
    }

    public List<Boolean> getBooleanList(String path, List<Boolean> defaultValue, String comment) {
        if (!config.hasPath(path)) {
            config = config.withValue(path, ConfigValueFactory.fromAnyRef(defaultValue, comment));
            if (parent != null)
                parent.update(this);
        }
        return config.getBooleanList(path);
    }

    public List<Integer> getIntList(String path, List<Integer> defaultValue, String comment) {
        if (!config.hasPath(path)) {
            config = config.withValue(path, ConfigValueFactory.fromAnyRef(defaultValue, comment));
            if (parent != null)
                parent.update(this);
        }
        return config.getIntList(path);
    }

    public List<Long> getLongList(String path, List<Long> defaultValue, String comment) {
        if (!config.hasPath(path)) {
            config = config.withValue(path, ConfigValueFactory.fromAnyRef(defaultValue, comment));
            if (parent != null)
                parent.update(this);
        }
        return config.getLongList(path);
    }

    public List<Double> getDoubleList(String path, List<Double> defaultValue, String comment) {
        if (!config.hasPath(path)) {
            config = config.withValue(path, ConfigValueFactory.fromAnyRef(defaultValue, comment));
            if (parent != null)
                parent.update(this);
        }
        return config.getDoubleList(path);
    }

    public List<String> getStringList(String path, List<String> defaultValue, String comment) {
        if (!config.hasPath(path)) {
            config = config.withValue(path, ConfigValueFactory.fromAnyRef(defaultValue, comment));
            if (parent != null)
                parent.update(this);
        }
        return config.getStringList(path);
    }

    public List<Config> getConfigList(String path, String comment) {
        if (!config.hasPath(path)) {
            config = config.withValue(path, ConfigValueFactory.fromIterable(Collections.emptyList()));
            if (parent != null)
                parent.update(this);
        }
        return Lists.transform(config.getConfigList(path), (c) -> new Config(path, c, this));
    }

    public boolean hasPath(String path) {
        return config.hasPath(path);
    }

    public boolean isEmpty() {
        return config.isEmpty();
    }

    public boolean hasChanged() {
        return System.identityHashCode(config) != initialHash;
    }

    public void save() {
        if (file == null)
            throw new UnsupportedOperationException("Only root configs can be saved!");
        if (hasChanged()) {
            try {
                FileUtils.writeStringToFile(file, config.root().render(ConfigRenderOptions.defaults().setJson(false).setOriginComments(true).setComments(false)));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void update(Config child) {
        this.config = this.config.withValue(child.path, child.config.root());
        if (parent != null)
            parent.update(this);
    }
}
