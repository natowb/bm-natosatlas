package dev.natowb.natosatlas.stapi.screens;

import dev.natowb.natosatlas.core.NacSettings;
import dev.natowb.natosatlas.core.config.NacConfigOption;
import dev.natowb.natosatlas.core.config.NacOption;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;

public class SettingsScreen extends Screen {

    private static final NacOption[] OPTIONS = {
            NacOption.MAP_RENDERER,
            NacOption.ENTITY_DISPLAY,
            NacOption.MAP_GRID,
            NacOption.DEBUG_INFO
    };

    private final Screen parent;
    protected String title = "Settings";

    public SettingsScreen(Screen parent) {
        this.parent = parent;
    }

    @Override
    public void init() {
        int index = 0;

        for (NacOption opt : OPTIONS) {
            int x = this.width / 2 - 155 + (index % 2) * 160;
            int y = this.height / 6 + 24 * (index / 2);

            this.buttons.add(new NacOptionButtonWidget(
                    opt.getId(),
                    x, y,
                    150, 20,
                    opt
            ));

            index++;
        }

        this.buttons.add(new ButtonWidget(
                200,
                this.width / 2 - 100,
                this.height / 6 + 168,
                200, 20,
                "Done"
        ));
    }

    @Override
    protected void buttonClicked(ButtonWidget button) {
        if (!button.active) return;

        if (button.id < 100 && button instanceof NacOptionButtonWidget) {
            NacOptionButtonWidget optButton = (NacOptionButtonWidget) button;
            NacConfigOption cfg = NacSettings.getOption(optButton.getOption());

            cfg.click();          // toggle or cycle
            optButton.refreshLabel();
        }

        if (button.id == 200) {
            NacSettings.save();
            this.minecraft.setScreen(parent);
        }
    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        this.renderBackground();
        this.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 20, 0xFFFFFF);
        super.render(mouseX, mouseY, delta);
    }
}
