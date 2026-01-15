package dev.natowb.natosatlas.core.config;

public class NacFloatConfigOption implements NacConfigOption {

    private final String key;
    private final float defaultValue;

    public NacFloatConfigOption(String key, float defaultValue) {
        NacConfigStorage.setFloat(key, defaultValue);
        this.key = key;
        this.defaultValue = defaultValue;
    }

    @Override
    public String getKey() {
        return key;
    }

    public float getValue() {
        return NacConfigStorage.getFloat(key);
    }

    public void setValue(float value) {
        NacConfigStorage.setFloat(key, value);
    }

    @Override
    public String getValueLabel() {
        return String.format("%.2f", getValue());
    }

    @Override
    public void click() {
    }
}
