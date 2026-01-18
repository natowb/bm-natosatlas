package dev.natowb.natosatlas.core.settings;

public enum SettingsOption {

    ENTITY_DISPLAY("Entities") {
        @Override
        public void cycle() {
            var m = Settings.entityDisplayMode;
            Settings.entityDisplayMode =
                    m == Settings.EntityDisplayMode.ALL ? Settings.EntityDisplayMode.ONLY_PLAYER :
                            m == Settings.EntityDisplayMode.ONLY_PLAYER ? Settings.EntityDisplayMode.NONE :
                                    Settings.EntityDisplayMode.ALL;
        }

        @Override
        public String getValueLabel() {
            return Settings.entityDisplayMode.name();
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

    DEBUG_INFO("Debug") {
        @Override
        public void cycle() {
            Settings.debugInfo = !Settings.debugInfo;
        }

        @Override
        public String getValueLabel() {
            return Settings.debugInfo ? "On" : "Off";
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
