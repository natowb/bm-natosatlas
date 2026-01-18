package dev.natowb.natosatlas.core.ui.elements;

import dev.natowb.natosatlas.core.settings.SettingsOption;

public class UIElementOptionButton extends UIElementButton {

    private final SettingsOption option;

    public UIElementOptionButton(int id, int x, int y, int w, int h, SettingsOption option) {
        super(id, x, y, w, h, "");
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

