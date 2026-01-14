package dev.natowb.natosatlas.stapi.screens;

import dev.natowb.natosatlas.core.NacWaypoints;
import dev.natowb.natosatlas.core.models.NacWaypoint;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.render.Tessellator;

public class WaypointListWidget extends EntryListWidget {

    private final WaypointListScreen parent;

    public WaypointListWidget(WaypointListScreen parent, Minecraft mc) {
        super(mc, parent.width, parent.height, 50, parent.height - 90, 30);
        this.parent = parent;
    }

    @Override
    protected int getEntryCount() {
        return NacWaypoints.getAll().size();
    }

    @Override
    protected void entryClicked(int index, boolean doubleClick) {
        parent.selectedIndex = index;
        parent.updateButtonStates();

        if (doubleClick) {
            parent.openEditScreen(index);
        }
    }

    @Override
    protected boolean isSelectedEntry(int index) {
        return index == parent.selectedIndex;
    }

    @Override
    protected int getEntriesHeight() {
        return NacWaypoints.getAll().size() * 30;
    }

    @Override
    protected void renderBackground() {
        parent.renderBackground();
    }

    @Override
    protected void renderEntry(int index, int x, int y, int mouseX, Tessellator tessellator) {
        NacWaypoint wp = NacWaypoints.getAll().get(index);
        Minecraft mc = (Minecraft) FabricLoader.getInstance().getGameInstance();
        parent.drawTextWithShadow(mc.textRenderer, wp.name, x + 2, y + 1, 0xFFFFFF);
        parent.drawTextWithShadow(mc.textRenderer,
                "X: " + wp.x + "  Y: " + wp.y + "  Z: " + wp.z,
                x + 2, y + 12, 0xA0A0A0);
    }
}
