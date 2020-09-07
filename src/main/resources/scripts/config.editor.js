let configEditor = {
	tilesets: [{
		metadata: {
			name: 'service',
			path: '/tiles/service.png',
			size: {
				width: 128,
				height: 96,
			},
			tile_size: {
				width: 64,
				height: 48,
			}
		},
		content: [
			{name: 'selected', x: 0, y: 0},
			{name: 'cheating-tile', x: 1, y: 0},
		]
	}, {
		metadata: {
			name: 'tileset-1',
			path: '/tiles/demo-tileset.png',
			size: {
				width: 256,
				height: 144,
			},
			tile_size: {
				width: 64,
				height: 48,
			}
		},
		content: [
			{name: 'empty', x: 0, y: 0},
			{name: 'water', x: 1, y: 0},
			{name: 'grass', x: 2, y: 0},
			{name: 'grass:flowered', x: 3, y: 0},
			{name: 'dirt', x: 4, y: 0},
			{name: 'forest', x: 0, y: 1},
			{name: 'forest:destroyed', x: 1, y: 1},
			{name: 'grass:dirty', x: 2, y: 1},
			{name: 'gold-ore', x: 3, y: 1},
		]
	}],
	map: {
		metadata: {
			size: {
				width: 9,
				height: 9
			},
			origin: {
				x: 4,
				y: 0
			}
		}
	}
};

configEditor;