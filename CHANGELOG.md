## [0.9.0] - 2026-01-24

### 🚀 Features

- Add ability to get chunk dirty and savetime flags
- Introduce basic cave building, rendering and layer
- Introduce auto layer mode for day, night, or cave
- Add hasCeiling access to WorldAccess
- *(core)* Limit render radius of entities to 128 (#21)

### 🐛 Bug Fixes

- Fix slime chunk detection logic
- Fix map saving and loading for mutliple dims

### 💼 Other

- Add chunk coords to NAEntity class
- Remove mapupdatescheduler and use new MapUpdater class

### 🚜 Refactor

- Replace NAWorldInfo with a WorldWrapper class
- Move entity access from WorldProvider to WorldWrapper
- Move block related functions to new BlockAccess class
- Move getBiome to WorldWrapper class
- Have a general lookup for specific blocks in BlockAccess
- Create a ChunkWrapper for accessing chunks
- Remove WorldProvider classes and reuse NAChunk getters for all platforms
- Dont create new painters every MapScreen render
- Extract color logic from ChunkRenderer to new ColorEngine class
- Remove the visible region syncing
- Turn WorldWrapper into global WorldAccess

### ⚙️ Miscellaneous Tasks

- Add log levels to logging util
- Remove unused class
- *(project)* Add conventional-commits gradle plugin
- Add git-cliff config
## [0.8.1] - 2026-01-23

### 🐛 Bug Fixes

- Make waypoint creation menu use the player y as default
- Skip glass and render solid block below

### 💼 Other

- Create simple vertical layout helper

### 🚜 Refactor

- *(core)* Refactor Settings Screen enum options to use new Layout
- Rework slider element to use screen events
## [0.8.0] - 2026-01-22

### 🚀 Features

- Add experimental map exporter (#15)

### 🐛 Bug Fixes

- Grid rendering with rotations
- Use correct region path for BTA existing chunks
- Prevent generate chunks from loading chunks

### 🚜 Refactor

- Improve the update and save schedulers to be more safe (#13)
- Complete overhaul on generating existing chunks (#14)

### ⚙️ Miscellaneous Tasks

- Set mod version
## [0.7.0] - 2026-01-22

### 🚀 Features

- Use entity textures for map icons (#7)

### 🐛 Bug Fixes

- Remove commons-lang3 from being included in the jar (#6)
- Resolve strange merge conflict

### 🚜 Refactor

- Update buildscript(s) (#8)
- Combine MapRenderer with the MapUpdateScheudler
- Remove MapPainter god class and use render stages
- Add api to UIScreen for adding and handling ui elements

### ⚙️ Miscellaneous Tasks

- Update readme with requirements.
## [0.6.0] - 2026-01-21

### 🚀 Features

- Add method to build chunk from region file
- Move map updating to worker
- Use single cache isntead of one per region
- Add real save name detection instead of using world name
- Move update handling back to main thread
- Create Waypoints List to cleanup screen code
- Modularize project (#4)
- Add slime chunks overlay
- Merge BTA support (#5)

### 🐛 Bug Fixes

- Center on player when opening map
- Tweak biome color tint amount
- Fix java 8 compatibility in the core
- Dont accept keyboard input until we are enabled
- Make map screen compatible with BTA

### 💼 Other

- Fix waypoint storage location

### 🚜 Refactor

- Rename map/MapRenderer to MapPainter
- Combine chunkProvider and worldProvider
- Rename and move data models
- Merge all world information into one model
- Merge entityProvider into worldProvider
- Move scaled info to UIScreen render parameter
- *(UI)* Move debug info painting to the MapPainter
- Introduce dedicated path management class
- Cleanup logging and profiler
- Split map rendering and map management
- Remove layer management from renderer
- Reduce logic in screen wrappers
- Remove hardcoded color mapping and use translations.

### ⚙️ Miscellaneous Tasks

- Add profiler to mapmanager
- Random file renamings
- Small cleanup using coords instead of seperate ints
- Upload screenshots
- Upload quick banner image
- Bump version
- Add issue templates
- Bump version
