import java.io.*;
import java.util.*;

public class GuardGallivant {

    // We represent the grid as a 2D array of characters.
    // The guard's starting (row, col) and initial Direction are stored separately.

    private static char[][] map;        // 2D map from input
    private static int rows, cols;      // Dimensions of the map
    private static int startRow, startCol;    // Guard's starting row/col
    private static Direction startDir;        // Guard's starting direction

    // Directions in puzzle: ^ (up), v (down), < (left), > (right)
    // We'll track them as an enum with convenient rotation and deltas
    enum Direction {
        UP, DOWN, LEFT, RIGHT
    }

    public static void main(String[] args) throws IOException {
        // 1) Read the puzzle input from file (or standard input)
        //    For demonstration, let's assume you pass a file path via args or edit the code:
        File inputFile = new File("C:\\Users\\19365\\OneDrive\\Documents\\GitHub\\AdventDay6\\src\\input.txt");  // Change to your path if needed
        // If you want to read from standard input, use: BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        BufferedReader br = new BufferedReader(new FileReader(inputFile));

        List<String> lines = new ArrayList<>();
        String line;
        while ((line = br.readLine()) != null) {
            lines.add(line);
        }
        br.close();

        rows = lines.size();
        cols = lines.get(0).length();
        map = new char[rows][cols];

        // Parse lines into map
        // Also find the guard's starting position and direction
        startRow = -1;
        startCol = -1;
        startDir = Direction.UP; // default, will change once we see ^,v,<,>
        for (int r = 0; r < rows; r++) {
            char[] rowChars = lines.get(r).toCharArray();
            for (int c = 0; c < cols; c++) {
                map[r][c] = rowChars[c];
                // Identify guard
                if (rowChars[c] == '^') {
                    startRow = r;
                    startCol = c;
                    startDir = Direction.UP;
                    map[r][c] = '.'; // treat it as free for future pathing
                } else if (rowChars[c] == 'v') {
                    startRow = r;
                    startCol = c;
                    startDir = Direction.DOWN;
                    map[r][c] = '.';
                } else if (rowChars[c] == '<') {
                    startRow = r;
                    startCol = c;
                    startDir = Direction.LEFT;
                    map[r][c] = '.';
                } else if (rowChars[c] == '>') {
                    startRow = r;
                    startCol = c;
                    startDir = Direction.RIGHT;
                    map[r][c] = '.';
                }
            }
        }

        // --------------------
        // Part 1: Simulate guard's movement without any extra obstacle.
        //         Count distinct positions visited.
        // --------------------
        Set<String> visitedPositionsPart1 = new HashSet<>();
        simulateGuard(map, startRow, startCol, startDir, visitedPositionsPart1, false);
        int part1Answer = visitedPositionsPart1.size();
        System.out.println("Part 1: Distinct positions visited before leaving map = " + part1Answer);


        // --------------------
        // Part 2: For each possible cell that is '.', try placing '#'
        //         If simulateGuard detects a loop, count it.
        //         EXCLUDE the guard's start cell from consideration (puzzle requirement).
        // --------------------
        int part2Answer = 0;

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                // Must skip the guard's actual start
                if (r == startRow && c == startCol) {
                    continue;
                }
                // Only consider placing an obstacle on '.' cells
                if (map[r][c] == '.') {
                    // Temporarily place obstacle
                    map[r][c] = '#';

                    // We'll run a new simulation to see if it loops
                    // We don't need to track visited positions for counting them,
                    // only to see if we detect a loop
                    Set<String> visitedPositions = new HashSet<>();
                    boolean foundLoop = simulateGuard(map, startRow, startCol, startDir, visitedPositions, true);

                    if (foundLoop) {
                        part2Answer++;
                    }

                    // Revert it
                    map[r][c] = '.';
                }
            }
        }

        System.out.println("Part 2: Number of positions that cause a loop = " + part2Answer);
    }

    /**
     * Simulates the guard's movement from the given (row, col, dir) on the given 'map'.
     * A single run of this method ends when the guard leaves the map or we detect a loop.
     *
     * @param trackVisitedLoop if true, we are checking for loops (Part 2 scenario).
     * @return true if we found a loop, false if guard left the map.
     */
    private static boolean simulateGuard(char[][] map,
                                         int startRow, int startCol, Direction startDir,
                                         Set<String> visitedPositions,
                                         boolean trackVisitedLoop) {
        // We'll keep a local (row, col, dir)
        int row = startRow;
        int col = startCol;
        Direction dir = startDir;

        // For Part 1, we also want to collect distinct positions visited
        // (row,col) ignoring direction. We'll store them in visitedPositions if trackVisitedLoop==false
        // or store the direction as well if trackVisitedLoop==true
        // Actually, for Part 2 loop detection, we must store (row,col,dir) to detect a cycle.

        // We'll do an infinite loop with a break condition
        while (true) {
            // 1) If we're out of bounds, guard leaves -> no loop
            if (!inBounds(row, col)) {
                return false;
            }

            // 2) For Part 2 loop detection, we track (row,col,dir).
            //    For Part 1 distinct-positions, we only track (row,col).
            String stateKey;
            if (trackVisitedLoop) {
                stateKey = row + "," + col + "," + dir;
            } else {
                stateKey = row + "," + col;
            }

            // If we've seen this exact stateKey before, that's a loop.
            if (visitedPositions.contains(stateKey)) {
                // If trackVisitedLoop==true, we found a loop.
                // If trackVisitedLoop==false, we won't interpret it as a loop for Part 1.
                return trackVisitedLoop;
            }
            visitedPositions.add(stateKey);

            // 3) Check if something is in front of us
            if (frontIsBlocked(map, row, col, dir)) {
                // Turn right 90 degrees
                dir = rotateRight(dir);
            } else {
                // Move forward
                row += dRow(dir);
                col += dCol(dir);
            }
        }
    }

    /**
     * Returns true if the guard is about to step into a '#' when moving forward from (row,col) in 'dir'.
     * If (row,col) is at the edge and next step is out of bounds, we consider that "not blocked" by '#' but
     * the guard will eventually leave the map on the next iteration's bounds check.
     */
    private static boolean frontIsBlocked(char[][] map, int row, int col, Direction dir) {
        int nr = row + dRow(dir);
        int nc = col + dCol(dir);
        if (!inBounds(nr, nc)) {
            // Not blocked by an obstacle, but out of bounds -> the guard will step out
            return false;
        }
        // If next cell is '#' => blocked
        return (map[nr][nc] == '#');
    }

    private static int dRow(Direction d) {
        switch (d) {
            case UP:    return -1;
            case DOWN:  return  1;
            case LEFT:  return  0;
            case RIGHT: return  0;
        }
        return 0; // unreachable
    }

    private static int dCol(Direction d) {
        switch (d) {
            case UP:    return  0;
            case DOWN:  return  0;
            case LEFT:  return -1;
            case RIGHT: return  1;
        }
        return 0; // unreachable
    }

    private static Direction rotateRight(Direction d) {
        switch (d) {
            case UP:    return Direction.RIGHT;
            case DOWN:  return Direction.LEFT;
            case LEFT:  return Direction.UP;
            case RIGHT: return Direction.DOWN;
        }
        return d; // unreachable
    }

    private static boolean inBounds(int r, int c) {
        return (r >= 0 && r < rows && c >= 0 && c < cols);
    }
}