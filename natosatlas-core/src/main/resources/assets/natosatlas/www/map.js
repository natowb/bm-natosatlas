const map = L.map("map", {
    crs: L.CRS.Simple,
    minZoom: 0,
    maxZoom: 5,
    zoomControl: true,
}).setView([0, 0], 0);

let tileLayer = L.tileLayer("/tiles/0/0/{x}/{y}.png", {
    tileSize: 512,
    noWrap: true,
    minNativeZoom: 0,
    maxNativeZoom: 0,
    keepBuffer: 50,
}).addTo(map);

document.getElementById("layerSelect").addEventListener("change", (e) => {
    map.removeLayer(tileLayer);
    tileLayer = L.tileLayer(`/tiles/0/${e.target.value}/{x}/{y}.png`, {
        tileSize: 512,
        noWrap: true,
        minNativeZoom: 0,
        maxNativeZoom: 0,
        keepBuffer: 50,
    }).addTo(map);
});

const markers = new Map();

function mcToLeaflet(x, z) {
    return [-z, x];
}

const playerIcon = L.divIcon({
    className: "player-dot",
    iconSize: [14, 14],
    iconAnchor: [7, 7]
});

async function updatePlayers() {
    try {
        const res = await fetch("/players/0");
        const players = await res.json();

        const seen = new Set();

        players.forEach(p => {
            const id = p.name;
            seen.add(id);

            const pos = mcToLeaflet(p.x, p.z);

            if (!markers.has(id)) {
                const marker = L.marker(pos, { icon: playerIcon })
                    .addTo(map)
                    .bindTooltip(p.name, {
                        permanent: true,
                        direction: "top",
                        offset: [0, -10],
                        className: "player-label"
                    });

                markers.set(id, marker);
            } else {
                const marker = markers.get(id);
                marker.setLatLng(pos);
                marker.setTooltipContent(p.name);
            }
        });

        for (const id of markers.keys()) {
            if (!seen.has(id)) {
                markers.get(id).remove();
                markers.delete(id);
            }
        }

    } catch (err) {
        console.error("Failed to update players", err);
    }
}

updatePlayers()