package com.siondream.superjumper.algorithms;

import com.siondream.superjumper.GenerationDirector;
import com.siondream.superjumper.rooms.Room;
import com.siondream.superjumper.tiles.Tile;
import com.siondream.superjumper.tiles.TileMap;
import com.siondream.superjumper.tiles.TileType;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Generador de mapas usando el algoritmo Drunkard's Walk (Random Walk).
 * Produce niveles orgánicos, irregulares y con estructura de cueva natural.
 * Los "caminantes" se mueven aleatoriamente por el mapa tallando suelo,
 * generando redes de corredores y habitaciones conectadas de forma natural.
 *
 * <h3>Parámetros configurables (via {@code setParameter}):</h3>
 * <ul>
 *   <li>{@code targetFloorRatio} (float, 0.10–0.90, def: 0.40) – % del mapa que será suelo</li>
 *   <li>{@code walkerCount}      (int,   1–8,       def: 1)    – número de caminantes simultáneos</li>
 *   <li>{@code turnChance}       (float, 0–1,       def: 0.35) – probabilidad de cambio de dirección por paso</li>
 *   <li>{@code carveRadius}      (int,   0–2,       def: 0)    – radio del cincel (0=1 tile, 1=3×3, 2=5×5)</li>
 *   <li>{@code borderPadding}    (int,   0–dim/3,   def: 1)    – margen de borde sin tallar</li>
 *   <li>{@code startX}           (int,              def: w/2)  – columna de inicio del walker principal</li>
 *   <li>{@code startY}           (int,              def: h/2)  – fila de inicio del walker principal</li>
 *   <li>{@code maxSteps}         (int,              def: w*h*8)– límite de pasos totales</li>
 *   <li>{@code maxRoomCount}     (int,   0–30,      def: 6)    – habitaciones estampadas durante el walk</li>
 *   <li>{@code roomChance}       (float, 0–1,       def: 0.04) – probabilidad de estampar habitación en cada paso</li>
 *   <li>{@code roomMinSize}      (int,   2–12,      def: 3)    – tamaño mínimo de habitación estampada</li>
 *   <li>{@code roomMaxSize}      (int,   min–20,    def: 7)    – tamaño máximo de habitación estampada</li>
 *   <li>{@code smoothingPasses}  (int,   0–4,       def: 1)    – pasadas de suavizado post-proceso</li>
 *   <li>{@code smoothThreshold}  (int,   1–8,       def: 4)    – vecinos FLOOR mínimos para conservar suelo</li>
 *   <li>{@code minRoomTiles}     (int,   4–200,     def: 12)   – mínimo de tiles para considerar región como Room</li>
 *   <li>{@code minRoomDensity}   (float, 0–1,       def: 0.40) – compacidad mínima para filtrar pasillos</li>
 *   <li>{@code removeIslands}    (boolean,          def: true) – eliminar regiones FLOOR desconectadas</li>
 * </ul>
 */
public class RandomWalkMapGenerator extends GenerationDirector<TileMap> {

	private static final int[][] DIRS = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

	private final int width, height;
	private final List<Room> rooms;

	public RandomWalkMapGenerator(int width, int height, long seed) {
		super(seed);
		this.width = width;
		this.height = height;
		this.rooms = new ArrayList<>();
	}

	@Override
	protected void setupRules() {}

	// ── Entry point ────────────────────────────────────────────────────────────

	@Override
	protected TileMap generate() {
		rooms.clear();
		TileMap map = new TileMap(width, height);
		initializeMap(map, TileType.WALL);

		// ── Load ALL parameters here, once ────────────────────────────────────
		int borderPadding = getClampedInt("borderPadding", 1, 0, Math.min(width, height) / 3);
		int minX = borderPadding;
		int minY = borderPadding;
		int maxX = Math.max(minX, width  - 1 - borderPadding);
		int maxY = Math.max(minY, height - 1 - borderPadding);

		WalkConfig cfg        = new WalkConfig();
		cfg.targetFloorRatio  = getClampedFloat("targetFloorRatio", 0.40f, 0.10f, 0.90f);
		cfg.walkerCount       = getClampedInt  ("walkerCount",      1,     1,     8);
		cfg.startX            = getClampedInt  ("startX",           width  / 2, minX, maxX);
		cfg.startY            = getClampedInt  ("startY",           height / 2, minY, maxY);
		cfg.turnChance        = getClampedFloat("turnChance",       0.35f, 0f,   1f);
		cfg.carveRadius       = getClampedInt  ("carveRadius",      0,     0,    2);
		cfg.maxSteps          = getClampedInt  ("maxSteps",         width * height * 8, width * height, width * height * 20);
		cfg.maxRoomCount      = getClampedInt  ("maxRoomCount",     6,     0,    30);
		cfg.roomChance        = getClampedFloat("roomChance",       0.04f, 0f,   1f);
		cfg.roomMinSize       = getClampedInt  ("roomMinSize",      3,     2,    12);
		cfg.roomMaxSize       = getClampedInt  ("roomMaxSize",      7,     cfg.roomMinSize, 20);

		int     smoothingPasses = getClampedInt  ("smoothingPasses", 1,     0,    4);
		int     smoothThreshold = getClampedInt  ("smoothThreshold", 4,     1,    8);
		int     minRoomTiles    = getClampedInt  ("minRoomTiles",    12,    4,    200);
		float   minRoomDensity  = getClampedFloat("minRoomDensity",  0.40f, 0f,   1f);
		boolean removeIslands   = (boolean) parameters.getOrDefault("removeIslands", true);

		// ── Walk ──────────────────────────────────────────────────────────────
		runWalkers(map, cfg, minX, minY, maxX, maxY);

		// ── Post-processing ───────────────────────────────────────────────────
		// Reuse a single buffer across all smoothing passes
		TileType[][] smoothBuffer = new TileType[width][height];
		for (int i = 0; i < smoothingPasses; i++) {
			smoothMap(map, minX, minY, maxX, maxY, smoothThreshold, smoothBuffer);
		}

		if (removeIslands) {
			removeIsolatedRegions(map, cfg.startX, cfg.startY, minX, minY, maxX, maxY);
		}

		if (rooms.isEmpty()) {
			rooms.addAll(extractRooms(map, minX, minY, maxX, maxY, minRoomTiles, minRoomDensity));
		}

		return map;
	}

	// ── Public API ─────────────────────────────────────────────────────────────

	/** Returns the rooms detected or stamped during the last {@link #execute()} call. */
	public List<Room> getRooms() {
		return new ArrayList<>(rooms);
	}

	// ── Internal config ────────────────────────────────────────────────────────

	/** Holds all walker parameters to avoid a long method signature. */
	private static class WalkConfig {
		float targetFloorRatio, turnChance, roomChance;
		int   walkerCount, startX, startY, carveRadius;
		int   maxSteps, maxRoomCount, roomMinSize, roomMaxSize;
	}

	// ── Generation steps ───────────────────────────────────────────────────────

	private void initializeMap(TileMap map, TileType type) {
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				map.setTile(x, y, type);
			}
		}
	}

	private void runWalkers(TileMap map, WalkConfig cfg, int minX, int minY, int maxX, int maxY) {
		Random rand = context.getRandom();

		int targetFloorTiles = (int) (width * height * cfg.targetFloorRatio);
		int currentFloorTiles = 0;

		int[] walkerX   = new int[cfg.walkerCount];
		int[] walkerY   = new int[cfg.walkerCount];
		int[] walkerDir = new int[cfg.walkerCount];

		// First walker starts at the configured origin; the rest spawn dispersed
		for (int i = 0; i < cfg.walkerCount; i++) {
			if (i == 0) {
				walkerX[i] = cfg.startX;
				walkerY[i] = cfg.startY;
			} else {
				walkerX[i] = clamp(minX + rand.nextInt(maxX - minX + 1), minX, maxX);
				walkerY[i] = clamp(minY + rand.nextInt(maxY - minY + 1), minY, maxY);
			}
			walkerDir[i]       = rand.nextInt(4);
			currentFloorTiles += carveAt(map, walkerX[i], walkerY[i], cfg.carveRadius, minX, minY, maxX, maxY);
		}

		int steps = 0;
		while (currentFloorTiles < targetFloorTiles && steps < cfg.maxSteps) {
			for (int i = 0; i < cfg.walkerCount && currentFloorTiles < targetFloorTiles; i++) {

				if (rand.nextFloat() < cfg.turnChance) {
					walkerDir[i] = rand.nextInt(4);
				}

				// Move – bounce off borders instead of silently clamping
				int nx = walkerX[i] + DIRS[walkerDir[i]][0];
				int ny = walkerY[i] + DIRS[walkerDir[i]][1];
				if (nx < minX || nx > maxX || ny < minY || ny > maxY) {
					walkerDir[i] = rand.nextInt(4); // reflect with new random direction
					nx = clamp(nx, minX, maxX);
					ny = clamp(ny, minY, maxY);
				}
				walkerX[i] = nx;
				walkerY[i] = ny;

				currentFloorTiles += carveAt(map, walkerX[i], walkerY[i], cfg.carveRadius, minX, minY, maxX, maxY);

				// Optionally stamp a room at the walker's current position
				if (cfg.maxRoomCount > 0 && rooms.size() < cfg.maxRoomCount && rand.nextFloat() < cfg.roomChance) {
					StampResult result = stampRoom(map, walkerX[i], walkerY[i],
							cfg.roomMinSize, cfg.roomMaxSize, minX, minY, maxX, maxY, rand);
					if (result != null) {
						rooms.add(result.room);
						currentFloorTiles += result.newlyCarved;
					}
				}
			}
			steps++;
		}
	}

	// ── Carving helpers ────────────────────────────────────────────────────────

	private int carveAt(TileMap map, int x, int y, int radius, int minX, int minY, int maxX, int maxY) {
		int carved = 0;
		for (int dx = -radius; dx <= radius; dx++) {
			for (int dy = -radius; dy <= radius; dy++) {
				int tx = x + dx;
				int ty = y + dy;
				if (tx < minX || tx > maxX || ty < minY || ty > maxY) continue;

				Tile tile = map.getTile(tx, ty);
				if (tile != null && tile.type != TileType.FLOOR) {
					map.setTile(tx, ty, TileType.FLOOR);
					carved++;
				}
			}
		}
		return carved;
	}

	private StampResult stampRoom(TileMap map, int centerX, int centerY, int minSize, int maxSize,
								  int minX, int minY, int maxX, int maxY, Random rand) {
		int roomW  = minSize + rand.nextInt(maxSize - minSize + 1);
		int roomH  = minSize + rand.nextInt(maxSize - minSize + 1);
		int startX = clamp(centerX - roomW / 2, minX, maxX);
		int startY = clamp(centerY - roomH / 2, minY, maxY);
		int endX   = clamp(startX  + roomW - 1, minX, maxX);
		int endY   = clamp(startY  + roomH - 1, minY, maxY);

		if (endX - startX < 1 || endY - startY < 1) return null;

		int newlyCarved = 0;
		for (int x = startX; x <= endX; x++) {
			for (int y = startY; y <= endY; y++) {
				Tile tile = map.getTile(x, y);
				if (tile != null && tile.type != TileType.FLOOR) {
					map.setTile(x, y, TileType.FLOOR);
					newlyCarved++;
				}
			}
		}

		return new StampResult(new Room(startX, startY, endX - startX + 1, endY - startY + 1), newlyCarved);
	}

	private static class StampResult {
		final Room room;
		final int  newlyCarved;
		StampResult(Room room, int newlyCarved) { this.room = room; this.newlyCarved = newlyCarved; }
	}

	// ── Post-processing ────────────────────────────────────────────────────────

	/**
	 * Cellular-automata smoothing pass. Uses a pre-allocated {@code buffer} to
	 * avoid per-pass allocations when {@code smoothingPasses > 1}.
	 */
	private void smoothMap(TileMap map, int minX, int minY, int maxX, int maxY,
						   int threshold, TileType[][] buffer) {
		for (int x = minX; x <= maxX; x++) {
			for (int y = minY; y <= maxY; y++) {
				buffer[x][y] = countFloorNeighbors(map, x, y) >= threshold
						? TileType.FLOOR : TileType.WALL;
			}
		}
		for (int x = minX; x <= maxX; x++) {
			for (int y = minY; y <= maxY; y++) {
				map.setTile(x, y, buffer[x][y]);
			}
		}
	}

	private int countFloorNeighbors(TileMap map, int x, int y) {
		int count = 0;
		for (int dx = -1; dx <= 1; dx++) {
			for (int dy = -1; dy <= 1; dy++) {
				if (dx == 0 && dy == 0) continue;
				Tile neighbor = map.getTile(x + dx, y + dy);
				if (neighbor != null && neighbor.type == TileType.FLOOR) count++;
			}
		}
		return count;
	}

	/**
	 * BFS flood-fill from {@code (seedX, seedY)}. Any FLOOR tile not reachable
	 * from that origin is converted to WALL, ensuring a fully connected map.
	 */
	private void removeIsolatedRegions(TileMap map, int seedX, int seedY,
									   int minX, int minY, int maxX, int maxY) {
		// If the seed is not on a floor tile, search outward for the nearest one
		if (!isFloor(map, seedX, seedY)) {
			outer:
			for (int r = 1; r < Math.max(width, height); r++) {
				for (int dx = -r; dx <= r; dx++) {
					for (int dy = -r; dy <= r; dy++) {
						int tx = clamp(seedX + dx, minX, maxX);
						int ty = clamp(seedY + dy, minY, maxY);
						if (isFloor(map, tx, ty)) { seedX = tx; seedY = ty; break outer; }
					}
				}
			}
		}

		boolean[][] reachable = new boolean[width][height];
		ArrayDeque<int[]> queue = new ArrayDeque<>();
		reachable[seedX][seedY] = true;
		queue.add(new int[]{seedX, seedY});

		while (!queue.isEmpty()) {
			int[] cur = queue.poll();
			int x = cur[0], y = cur[1];
			for (int[] d : DIRS) {
				int nx = x + d[0], ny = y + d[1];
				if (nx < minX || nx > maxX || ny < minY || ny > maxY || reachable[nx][ny]) continue;
				if (isFloor(map, nx, ny)) {
					reachable[nx][ny] = true;
					queue.add(new int[]{nx, ny});
				}
			}
		}

		// Any FLOOR tile not reached becomes a wall
		for (int x = minX; x <= maxX; x++) {
			for (int y = minY; y <= maxY; y++) {
				if (isFloor(map, x, y) && !reachable[x][y]) {
					map.setTile(x, y, TileType.WALL);
				}
			}
		}
	}

	// ── Room extraction ────────────────────────────────────────────────────────

	private List<Room> extractRooms(TileMap map, int minX, int minY, int maxX, int maxY,
									int minRoomTiles, float minDensity) {
		boolean[][] visited    = new boolean[width][height];
		List<Room>  extracted  = new ArrayList<>();

		for (int x = minX; x <= maxX; x++) {
			for (int y = minY; y <= maxY; y++) {
				if (!isFloor(map, x, y) || visited[x][y]) continue;
				Room room = floodFillRoom(map, x, y, visited, minX, minY, maxX, maxY, minRoomTiles, minDensity);
				if (room != null) extracted.add(room);
			}
		}

		return extracted;
	}

	private Room floodFillRoom(TileMap map, int startX, int startY, boolean[][] visited,
							   int minX, int minY, int maxX, int maxY,
							   int minRoomTiles, float minDensity) {
		ArrayDeque<int[]> queue = new ArrayDeque<>();
		queue.add(new int[]{startX, startY});
		visited[startX][startY] = true;

		int count = 0;
		int rMinX = startX, rMaxX = startX;
		int rMinY = startY, rMaxY = startY;

		while (!queue.isEmpty()) {
			int[] cur = queue.poll();
			int x = cur[0], y = cur[1];
			count++;

			rMinX = Math.min(rMinX, x); rMaxX = Math.max(rMaxX, x);
			rMinY = Math.min(rMinY, y); rMaxY = Math.max(rMaxY, y);

			tryQueueFloor(map, x + 1, y, visited, queue, minX, minY, maxX, maxY);
			tryQueueFloor(map, x - 1, y, visited, queue, minX, minY, maxX, maxY);
			tryQueueFloor(map, x, y + 1, visited, queue, minX, minY, maxX, maxY);
			tryQueueFloor(map, x, y - 1, visited, queue, minX, minY, maxX, maxY);
		}

		if (count < minRoomTiles) return null;

		// Density check: long narrow corridors have low tile-count/bounding-area ratio
		int   boundingArea = (rMaxX - rMinX + 1) * (rMaxY - rMinY + 1);
		float density      = (float) count / boundingArea;
		if (density < minDensity) return null;

		return new Room(rMinX, rMinY, rMaxX - rMinX + 1, rMaxY - rMinY + 1);
	}

	private void tryQueueFloor(TileMap map, int x, int y, boolean[][] visited, ArrayDeque<int[]> queue,
							   int minX, int minY, int maxX, int maxY) {
		if (x < minX || x > maxX || y < minY || y > maxY || visited[x][y]) return;
		if (isFloor(map, x, y)) {
			visited[x][y] = true;
			queue.add(new int[]{x, y});
		}
	}

	// ── Utilities ─────────────────────────────────────────────────────────────

	private boolean isFloor(TileMap map, int x, int y) {
		Tile tile = map.getTile(x, y);
		return tile != null && tile.type == TileType.FLOOR;
	}

	private int getClampedInt(String key, int defaultValue, int minValue, int maxValue) {
		Object value = parameters.getOrDefault(key, defaultValue);
		return clamp(value instanceof Number ? ((Number) value).intValue() : defaultValue, minValue, maxValue);
	}

	private float getClampedFloat(String key, float defaultValue, float minValue, float maxValue) {
		Object value = parameters.getOrDefault(key, defaultValue);
		float f = value instanceof Number ? ((Number) value).floatValue() : defaultValue;
		return Math.max(minValue, Math.min(f, maxValue));
	}

	private int clamp(int value, int minValue, int maxValue) {
		return Math.max(minValue, Math.min(value, maxValue));
	}
}
