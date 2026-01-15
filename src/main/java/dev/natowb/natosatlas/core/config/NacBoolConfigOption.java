package dev.natowb.natosatlas.core.config;

public class NacBoolConfigOption implements NacConfigOption {

    private final String key;
    private final boolean defaultValue;

    public NacBoolConfigOption(String key, boolean defaultValue) {
        NacConfigStorage.setBoolean(key, defaultValue);
        this.key = key;
        this.defaultValue = defaultValue;
    }

    @Override
    public String getKey() {
        return key;
    }

    public boolean getValue() {
        return NacConfigStorage.getBoolean(key);
    }

    public void setValue(boolean value) {
        NacConfigStorage.setBoolean(key, value);
    }

    @Override
    public String getValueLabel() {
        return getValue() ? "On" : "Off";
    }

    @Override
    public void click() {
        setValue(!getValue());
    }
}
