package dev.natowb.natosatlas.core.settings;

public enum SettingsOption {

    ENTITY_DISPLAY("Entities") {
        @Override
        public void cycle() {
            Settings.EntityDisplayMode m = Settings.entityDisplayMode;
            switch (m) {
                case Player:
                    Settings.entityDisplayMode = Settings.EntityDisplayMode.All;
                    break;
                case All:
                    Settings.entityDisplayMode = Settings.EntityDisplayMode.Nothing;
                    break;
                case Nothing:
                    Settings.entityDisplayMode = Settings.EntityDisplayMode.Player;
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
            Settings.MapRenderMode m = Settings.mapRenderMode;
            switch (m) {
                case Day:
                    Settings.mapRenderMode = Settings.MapRenderMode.Night;
                    break;
                case Night:
                    Settings.mapRenderMode = Settings.MapRenderMode.Cave;
                    break;
                case Cave:
                    Settings.mapRenderMode = Settings.MapRenderMode.Auto;
                    break;
                case Auto:
                    Settings.mapRenderMode = Settings.MapRenderMode.Day;
                    break;
            }
        }

        @Override
        public String getValueLabel() {
            return  Settings.mapRenderMode.name();
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
