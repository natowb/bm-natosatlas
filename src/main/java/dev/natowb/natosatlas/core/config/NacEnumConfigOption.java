package dev.natowb.natosatlas.core.config;

public class NacEnumConfigOption<E extends Enum<E>> implements NacConfigOption {

    private final String key;
    private final Class<E> enumClass;
    private final E defaultValue;

    public NacEnumConfigOption(String key, E defaultValue) {
        this.key = key;
        this.enumClass = defaultValue.getDeclaringClass();
        this.defaultValue = defaultValue;
        NacConfigStorage.setEnum(key, defaultValue);
    }

    @Override
    public String getKey() {
        return key;
    }

    public E getValue() {
        return NacConfigStorage.getEnum(key, enumClass);
    }

    public void setValue(E value) {
        NacConfigStorage.setEnum(key, value);
    }

    @Override
    public void click() {
        NacConfigStorage.cycleEnum(key, enumClass);
    }

    @Override
    public String getValueLabel() {
        return getValue().name();
    }
}
