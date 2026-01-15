package dev.natowb.natosatlas.core.gui;

import dev.natowb.natosatlas.core.NacSettings;
import dev.natowb.natosatlas.core.config.NacConfigOption;
import dev.natowb.natosatlas.core.config.NacOption;

public class NacGuiOptionButton extends NacGuiButton {

    private final NacOption option;

    public NacGuiOptionButton(int id, int x, int y, int w, int h, NacOption option) {
        super(id, x, y, w, h, "");
        this.option = option;
        refreshLabel();
    }

    public NacOption getOption() {
        return option;
    }

    public void refreshLabel() {
        NacConfigOption cfg = NacSettings.getOption(option);
        this.label = option.getTitle() + ": " + cfg.getValueLabel();
    }

    public void cycle() {
        NacConfigOption cfg = NacSettings.getOption(option);
        cfg.click();
        refreshLabel();
    }
}
