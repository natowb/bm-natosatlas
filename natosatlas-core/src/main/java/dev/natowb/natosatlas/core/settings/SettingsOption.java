package dev.natowb.natosatlas.core.settings;

import dev.natowb.natosatlas.core.NatosAtlas;
import dev.natowb.natosatlas.core.map.MapLayerManager;

public enum SettingsOption {

    ENTITY_DISPLAY("Entities") {
        @Override
        public void cycle() {
            Settings.EntityDisplayMode m = Settings.entityDisplayMode;
            switch (m) {
                case All:
                    Settings.entityDisplayMode = Settings.EntityDisplayMode.Player;
                    break;
                case Player:
                    Settings.entityDisplayMode = Settings.EntityDisplayMode.Nothing;
                    break;
                case Nothing:
                    Settings.entityDisplayMode = Settings.EntityDisplayMode.All;
                    break;
            }
        }

        @Override
        public String getValueLabel() {
            return Settings.entityDisplayMode.name();
        }
    },

    MAP_RENDER_MODE("Mode") {
        @Override
        public void cycle() {
            MapLayerManager layers = NatosAtlas.get().layers;
            int layer = layers.getActiveLayer().id;
            layer++;

            if (layer >= layers.getLayers().size()) {
                layer = 0;
            }

            layers.setActiveLayer(layer);
        }

        @Override
        public String getValueLabel() {
            MapLayerManager layers = NatosAtlas.get().layers;
            return layers.getActiveLayer().name;
        }
    },

    MAP_GRID("Grid") {
        @Override
        public void cycle() {
            Settings.mapGrid = !Settings.mapGrid;
        }

        @Override
        public String getValueLabel() {
            return Settings.mapGrid ? "On" : "Off";
        }
    },

    SLIME_CHUNKS("Slimes") {
        @Override
        public void cycle() {
            Settings.showSlimeChunks = !Settings.showSlimeChunks;
        }

        @Override
        public String getValueLabel() {
            return Settings.showSlimeChunks ? "On" : "Off";
        }
    },

    DEBUG_INFO("Debug") {
        @Override
        public void cycle() {
            Settings.debugInfo = !Settings.debugInfo;
        }

        @Override
        public String getValueLabel() {
            return Settings.debugInfo ? "On" : "Off";
        }
    },
    USE_REIMINIMAP_WAYPOINTS("Rei's MM Waypoints") {
        @Override
        public void cycle() {
            Settings.useReiMinimapWaypointStorage = !Settings.useReiMinimapWaypointStorage;
        }

        @Override
        public String getValueLabel() {
            return Settings.useReiMinimapWaypointStorage ? "On" : "Off";
        }
    };

    private final String title;

    SettingsOption(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public abstract void cycle();

    public abstract String getValueLabel();
}
