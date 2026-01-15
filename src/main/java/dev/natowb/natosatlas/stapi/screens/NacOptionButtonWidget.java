package dev.natowb.natosatlas.stapi.screens;

import dev.natowb.natosatlas.core.NacSettings;
import dev.natowb.natosatlas.core.config.NacConfigOption;
import dev.natowb.natosatlas.core.config.NacOption;
import net.minecraft.client.gui.widget.ButtonWidget;

public class NacOptionButtonWidget extends ButtonWidget {

    private final NacOption option;

    public NacOptionButtonWidget(int id, int x, int y, int w, int h, NacOption option) {
        super(id, x, y, w, h, "");
        this.option = option;
        refreshLabel();
    }

    public NacOption getOption() {
        return option;
    }

    public void refreshLabel() {
        NacConfigOption cfg = NacSettings.getOption(option);
        this.text = option.getTitle() + ": " + cfg.getValueLabel();
    }
}
