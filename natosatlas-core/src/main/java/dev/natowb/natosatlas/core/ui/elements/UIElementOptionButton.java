package dev.natowb.natosatlas.core.ui.elements;

import dev.natowb.natosatlas.core.settings.SettingsOption;
import dev.natowb.natosatlas.core.ui.layout.UILayout;

public class UIElementOptionButton extends UIElementButton {

    private final SettingsOption option;

    public UIElementOptionButton(SettingsOption option, int x, int y, int w, int h) {
        super(option.ordinal(), x, y, w, h, "");
        this.option = option;
        refreshLabel();
    }

    public UIElementOptionButton(SettingsOption option, UILayout layout, int w, int h) {
        super(option.ordinal(), layout, w, h, "");
        this.option = option;
        refreshLabel();
    }

    public void refreshLabel() {
        this.label = option.getTitle() + ": " + option.getValueLabel();
    }

    public void cycle() {
        option.cycle();
        refreshLabel();
    }
}
