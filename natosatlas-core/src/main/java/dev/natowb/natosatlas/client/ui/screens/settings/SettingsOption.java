package dev.natowb.natosatlas.client.ui.screens.settings;

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
            return Settings.mapRenderMode.name();
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

    DEBUG_INFO("Debug Info") {
        @Override
        public void cycle() {
            Settings.debugInfo = !Settings.debugInfo;
        }

        @Override
        public String getValueLabel() {
            return Settings.debugInfo ? "On" : "Off";
        }
    },
    MINIMAP_ENABLED("Enable") {
        @Override
        public void cycle() {
            Settings.minimapEnabled = !Settings.minimapEnabled;
        }

        @Override
        public String getValueLabel() {
            return Settings.minimapEnabled ? "Yes" : "No";
        }
    },
    MINIMAP_ROTATE("Rotate with player") {
        @Override
        public void cycle() {
            Settings.minimapRotateWithPlayer = !Settings.minimapRotateWithPlayer;
        }

        @Override
        public String getValueLabel() {
            return Settings.minimapRotateWithPlayer ? "On" : "Off";
        }
    },
    MINIMAP_ENTITY_DISPLAY("Entities") {
        @Override
        public void cycle() {
            Settings.EntityDisplayMode m = Settings.minimapEntityDisplayMode;
            switch (m) {
                case Player:
                    Settings.minimapEntityDisplayMode = Settings.EntityDisplayMode.All;
                    break;
                case All:
                    Settings.minimapEntityDisplayMode = Settings.EntityDisplayMode.Nothing;
                    break;
                case Nothing:
                    Settings.minimapEntityDisplayMode = Settings.EntityDisplayMode.Player;
                    break;
            }
        }

        @Override
        public String getValueLabel() {
            return Settings.minimapEntityDisplayMode.name();
        }
    },
    MINIMAP_MAP_RENDER_MODE("Mode") {
        @Override
        public void cycle() {
            Settings.MapRenderMode m = Settings.minimapRenderMode;
            switch (m) {
                case Day:
                    Settings.minimapRenderMode = Settings.MapRenderMode.Night;
                    break;
                case Night:
                    Settings.minimapRenderMode = Settings.MapRenderMode.Cave;
                    break;
                case Cave:
                    Settings.minimapRenderMode = Settings.MapRenderMode.Auto;
                    break;
                case Auto:
                    Settings.minimapRenderMode = Settings.MapRenderMode.Day;
                    break;
            }
        }

        @Override
        public String getValueLabel() {
            return Settings.minimapRenderMode.name();
        }
    },
    MINIMAP_POSITION("Position") {
        @Override
        public void cycle() {
            Settings.MinimapPosition m = Settings.minimapPosition;
            switch (m) {
                case TopLeft:
                    Settings.minimapPosition = Settings.MinimapPosition.TopRight;
                    break;
                case TopRight:
                    Settings.minimapPosition = Settings.MinimapPosition.BottomLeft;
                    break;
                case BottomLeft:
                    Settings.minimapPosition = Settings.MinimapPosition.BottomRight;
                    break;
                case BottomRight:
                    Settings.minimapPosition = Settings.MinimapPosition.TopLeft;
                    break;
            }
        }

        @Override
        public String getValueLabel() {
            return Settings.minimapPosition.name();
        }
    };;

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
