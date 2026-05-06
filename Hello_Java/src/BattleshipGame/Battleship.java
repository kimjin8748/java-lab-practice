package BattleshipGame;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Random;

/**
 * PA #2 -- Battleship
 *
 * Implement a single-player Battleship game on a 10x10 board.
 *
 * Startup input (one line from stdin):
 *   N MODE FILE_NAME
 *   - N        : number of bombs (positive integer)
 *   - MODE     : d/D (Debug) or r/R (Release)
 *   - FILE_NAME: board file path (may contain spaces)
 *
 * Submit this file as: Battleship.java
 * - Public class name must be exactly "Battleship"
 * - No Korean comments allowed
 * - Must compile cleanly: javac Battleship.java
 */
public class Battleship {

    private static final int  BOARD_SIZE  = 10;
    private static final long RANDOM_SEED =
        Long.parseLong(System.getProperty("seed", "2026"));

    // === Board State ===
    // TODO: declare fields for baseBoard, shot array, shipRef array, score
    //       Suggested types:
    //         char[][]  baseBoard  -- ship characters or ' '
    //         boolean[][] shot     -- true if this cell has been targeted
    //         Ship[][]  shipRef    -- reference to the Ship object at each cell
    //         int       score
    private char[][] baseBoard;
    private boolean[][] shot;
    private Ship[][] shipRef;
    private int score;
    
    // === Entry Point ===

    public static void main(String[] args) {
        // TODO:
        //   1. Create a BufferedReader from System.in
        //   2. Call parseStartupLine() -- catch BombInputException / ModeInputException,
        //      print the exception class simple name, and return
        //   3. Create a Battleship instance and call initializeBoard()
        //   4. Call play()
        //   5. Catch IOException: print "IOException" to stdout and return
    	try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            StartupConfig config = parseStartupLine(reader);
            if (config == null) return;
            
            Battleship game = new Battleship();
            game.clearBoard();
            game.initializeBoard(config.fileName);
            game.play(config.bombs, config.mode, reader);
            
        } catch (BombInputException e) {
            System.out.println("BombInputException");
        } catch (ModeInputException e) {
            System.out.println("ModeInputException");
        } catch (IOException e) {
            System.out.println("IOException");
        }
    }

    // === Startup Parsing ===

    /**
     * Reads one non-empty line from reader and parses it as:
     *   N MODE FILE_NAME
     *
     * FILE_NAME is everything after MODE (may contain spaces).
     *
     * @throws BombInputException  if N is missing, not an integer, or <= 0
     * @throws ModeInputException  if MODE is not one of d, D, r, R,
     *                             or if MODE/FILE_NAME tokens are missing
     */
    private static StartupConfig parseStartupLine(BufferedReader reader)
            throws IOException, BombInputException, ModeInputException {
    	String line = reader.readLine();
        if (line == null || line.trim().isEmpty()) {
            throw new BombInputException();
        }
        
        String[] parts = line.trim().split("\\s+", 3);
        
        int bombs;
        try {
            bombs = Integer.parseInt(parts[0]);
        } catch (NumberFormatException e) {
            throw new BombInputException();
        }
        
        if (bombs <= 0) {
            throw new BombInputException();
        }
        
        if (parts.length < 3) {
            throw new ModeInputException();
        }
        
        Mode mode;
        if (parts[1].equals("d") || parts[1].equals("D")) {
            mode = Mode.DEBUG;
        } else if (parts[1].equals("r") || parts[1].equals("R")) {
            mode = Mode.RELEASE;
        } else {
            throw new ModeInputException();
        }
        
        String fileName = parts[2].trim();
        if (fileName.isEmpty()) {
            throw new ModeInputException();
        }
        
        return new StartupConfig(bombs, mode, fileName);
    }

    // === Board Initialisation ===

    /**
     * Initializes all board state to empty / false / null.
     * Resets score to 0.
     */
    private void clearBoard() {
    	baseBoard = new char[BOARD_SIZE][BOARD_SIZE];
        shot = new boolean[BOARD_SIZE][BOARD_SIZE];
        shipRef = new Ship[BOARD_SIZE][BOARD_SIZE];
        score = 0;
        
        for (int r = 0; r < BOARD_SIZE; r++) {
            for (int c = 0; c < BOARD_SIZE; c++) {
                baseBoard[r][c] = ' ';
                shot[r][c] = false;
                shipRef[r][c] = null;
            }
        }
    }

    /**
     * If the file at fileName exists, calls loadBoardFromFile().
     * Otherwise calls generateRandomBoard(new Random(RANDOM_SEED)).
     */
    private void initializeBoard(String fileName) throws IOException {
    	Path path = Paths.get(fileName);
        if (Files.exists(path) && !Files.isDirectory(path)) {
            loadBoardFromFile(path);
        } else {
            generateRandomBoard(new Random(RANDOM_SEED));
        }
    }

    /**
     * Reads a 10-line board file. Each line is exactly 10 characters
     * (space-pad lines shorter than 10). Valid ship characters: A B S D P.
     * Populates baseBoard and shipRef.
     *
     * Ship segments are recognized:
     *   - Horizontal: consecutive same-type characters in the same row (length >= 2)
     *   - Vertical  : consecutive same-type characters in the same column
     *   - Isolated single cell: treated as its own ship object
     */
    private void loadBoardFromFile(java.nio.file.Path path) throws IOException {
    	List<String> lines = Files.readAllLines(path);
        
        for (int r = 0; r < BOARD_SIZE; r++) {
            String line = (r < lines.size()) ? lines.get(r) : "";
            for (int c = 0; c < BOARD_SIZE; c++) {
                char ch = (c < line.length()) ? line.charAt(c) : ' ';
                baseBoard[r][c] = ch;
            }
        }
        
        for (int r = 0; r < BOARD_SIZE; r++) {
            for (int c = 0; c < BOARD_SIZE; c++) {
                if (baseBoard[r][c] != ' ' && shipRef[r][c] == null) {
                    char type = baseBoard[r][c];
                    Ship s = createShipByType(type);
                    markShipSegmentDFS(r, c, type, s);
                }
            }
        }
    }
    private Ship createShipByType(char type) {
        switch (type) {
            case 'A': return new AircraftCarrier();
            case 'B': return new BattleshipShip();
            case 'S': return new Submarine();
            case 'D': return new Destroyer();
            case 'P': return new PatrolBoat();
            default:  return new PatrolBoat(); 
        }
    }
    
    private void markShipSegmentDFS(int r, int c, char type, Ship s) {
        if (r < 0 || r >= BOARD_SIZE || c < 0 || c >= BOARD_SIZE) return;
        if (baseBoard[r][c] != type || shipRef[r][c] != null) return;
        
        shipRef[r][c] = s;
        markShipSegmentDFS(r + 1, c, type, s);
        markShipSegmentDFS(r - 1, c, type, s);
        markShipSegmentDFS(r, c + 1, type, s);
        markShipSegmentDFS(r, c - 1, type, s);
    }

    /**
     * Places all ships randomly using the provided Random instance.
     *
     * Ship placement order (MUST follow exactly for deterministic output):
     *   AircraftCarrier x1, BattleshipShip x2, Submarine x2,
     *   Destroyer x1, PatrolBoat x4
     *
     * Per attempt:
     *   boolean horizontal = rng.nextBoolean();
     *   int row = rng.nextInt(10);
     *   int col = rng.nextInt(10);
     *
     * Retry (call rng again in the same order) if the placement is invalid.
     */
    private void generateRandomBoard(Random rng) {
    	Ship[] shipsToPlace = {
                new AircraftCarrier(),
                new BattleshipShip(), new BattleshipShip(),
                new Submarine(), new Submarine(),
                new Destroyer(),
                new PatrolBoat(), new PatrolBoat(), new PatrolBoat(), new PatrolBoat()
            };
            
            for (Ship s : shipsToPlace) {
                boolean placed = false;
                while (!placed) {
                    boolean horizontal = rng.nextBoolean();
                    int row = rng.nextInt(BOARD_SIZE);
                    int col = rng.nextInt(BOARD_SIZE);
                    
                    if (canPlace(row, col, s.size, horizontal)) {
                        placeShip(s, row, col, horizontal);
                        placed = true;
                    }
                }
            }
    }

    /**
     * Returns true if a ship of the given size can be placed at (row, col)
     * in the given direction without overlapping or touching any existing ship.
     */
    private boolean canPlace(int row, int col, int size, boolean horizontal) {
    	int rEnd = horizontal ? row : row + size - 1;
        int cEnd = horizontal ? col + size - 1 : col;
        
        if (rEnd >= BOARD_SIZE || cEnd >= BOARD_SIZE) {
            return false;
        }
        
        int rMin = Math.max(0, row - 1);
        int rMax = Math.min(BOARD_SIZE - 1, rEnd + 1);
        int cMin = Math.max(0, col - 1);
        int cMax = Math.min(BOARD_SIZE - 1, cEnd + 1);
        
        for (int r = rMin; r <= rMax; r++) {
            for (int c = cMin; c <= cMax; c++) {
                if (baseBoard[r][c] != ' ') {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Places the ship on the board starting at (row, col).
     * Updates baseBoard and shipRef for every cell the ship occupies.
     */
    private void placeShip(Ship ship, int row, int col, boolean horizontal) {
    	for (int i = 0; i < ship.size; i++) {
            int r = horizontal ? row : row + i;
            int c = horizontal ? col + i : col;
            baseBoard[r][c] = ship.type;
            shipRef[r][c] = ship;
        }
    }

    // === Game Loop ===

    /**
     * Main game loop.
     *
     * Repeats until all bombs are used:
     *   - Debug mode  : print board, then read a coordinate
     *   - Release mode: read a coordinate (no board print)
     *
     * After processing each input:
     *   - Valid new coordinate : call shoot(), increment bomb counter
     *   - Invalid / repeated   : print "Try again", do NOT increment counter
     *
     * When all bombs are used: print final board, then "Score N".
     */
    private void play(int bombs, Mode mode, BufferedReader reader) throws IOException {
    	int bombsLeft = bombs;
        
        while (bombsLeft > 0) {
            if (mode == Mode.DEBUG) {
                printBoard();
            }
            
            String line = reader.readLine();
            if (line == null) break; 
            line = line.trim();
            if (line.isEmpty()) continue;
            
            try {
                int[] coords = parseCoordinate(line);
                shoot(coords[0], coords[1]);
                bombsLeft--;
            } catch (HitException e) {
                System.out.println("Try again");
            }
        }
        
        printBoard();
        System.out.println("Score " + score);
    }

    /**
     * Parses a coordinate string (e.g., "A1", "j10").
     *
     * Rules:
     *   - First character must be a letter A-J (case-insensitive)
     *   - Remaining characters must form an integer 1-10
     *   - The cell must not have been shot before
     *
     * Throws HitException for any invalid or repeated input.
     * Returns int[]{row, col} (0-indexed) on success.
     */
    private int[] parseCoordinate(String token) throws HitException {
    	if (token == null || token.length() < 2) {
            throw new HitException();
        }
        
        char cChar = Character.toUpperCase(token.charAt(0));
        if (cChar < 'A' || cChar > 'J') {
            throw new HitException();
        }
        
        int col = cChar - 'A';
        int row;
        try {
            row = Integer.parseInt(token.substring(1)) - 1;
        } catch (NumberFormatException e) {
            throw new HitException();
        }
        
        if (row < 0 || row >= BOARD_SIZE) {
            throw new HitException();
        }
        
        if (shot[row][col]) {
            throw new HitException();
        }
        
        return new int[]{row, col};
    }

    /**
     * Marks (row, col) as shot.
     * Prints "Miss" or "Hit X" (X = uppercase ship character).
     * Updates score by adding ship.size for a hit.
     */
    private void shoot(int row, int col) {
    	shot[row][col] = true;
        char shipChar = baseBoard[row][col];
        
        if (shipChar == ' ') {
            System.out.println("Miss");
        } else {
            System.out.println("Hit " + shipChar);
            Ship s = shipRef[row][col];
            if (s != null) {
                score += s.size;
                s.hits++;
            }
        }
    }

    // === Display ===

    /**
     * Prints the current board state to stdout.
     *
     * Format:
     *   "  A B C D E F G H I J"
     *   "  - - - - - - - - - -"
     *   "1 | <cells...>"
     *   ...
     *   "10 | <cells...>"
     *
     * Cell rendering:
     *   - Not shot, empty  : " "
     *   - Not shot, ship   : ship character (uppercase)
     *   - Shot, empty      : "X"
     *   - Shot, ship       : "X" + ship character (lowercase)   e.g. "Xp", "Xa"
     *
     * Trailing spaces must be stripped from every line.
     */
    private void printBoard() {
    	System.out.println("  A B C D E F G H I J");
        System.out.println("  - - - - - - - - - -");
        
        for (int r = 0; r < BOARD_SIZE; r++) {
            StringBuilder sb = new StringBuilder();
            sb.append(r + 1).append(" | ");
            
            for (int c = 0; c < BOARD_SIZE; c++) {
                if (!shot[r][c]) {
                    if (baseBoard[r][c] == ' ') {
                        sb.append("  ");
                    } else {
                        sb.append(baseBoard[r][c]).append(" ");
                    }
                } else {
                    if (baseBoard[r][c] == ' ') {
                        sb.append("X ");
                    } else {
                        sb.append("X").append(Character.toLowerCase(baseBoard[r][c])).append(" ");
                    }
                }
            }
            
            int len = sb.length();
            while (len > 0 && sb.charAt(len - 1) == ' ') {
                len--;
            }
            System.out.println(sb.substring(0, len));
        }
    }

    // === Inner Types ===

    /** Execution mode. */
    private enum Mode { DEBUG, RELEASE }

    /** Holds parsed startup parameters. */
    private static class StartupConfig {
        final int    bombs;
        final Mode   mode;
        final String fileName;

        StartupConfig(int bombs, Mode mode, String fileName) {
            this.bombs    = bombs;
            this.mode     = mode;
            this.fileName = fileName;
        }
    }

    // === Ship Hierarchy ===

    /**
     * Abstract base class for all ship types.
     * Fields:
     *   type -- single uppercase character identifying the ship (A, B, S, D, P)
     *   size -- number of cells the ship occupies
     *   hits -- number of times this ship has been hit (optional to use)
     */
    private abstract static class Ship {
        final char type;
        final int  size;
        int        hits;

        Ship(char type, int size) {
            this.type = type;
            this.size = size;
            this.hits = 0;
        }
    }

    /** Aircraft Carrier: type='A', size=6, count=1 */
    private static final class AircraftCarrier extends Ship {
        AircraftCarrier() { super('A', 6); }
    }

    /** Battleship: type='B', size=4, count=2 */
    private static final class BattleshipShip extends Ship {
        BattleshipShip() { super('B', 4); }
    }

    /** Submarine: type='S', size=3, count=2 */
    private static final class Submarine extends Ship {
        Submarine() { super('S', 3); }
    }

    /** Destroyer: type='D', size=3, count=1 */
    private static final class Destroyer extends Ship {
        Destroyer() { super('D', 3); }
    }

    /** Patrol Boat: type='P', size=2, count=4 */
    private static final class PatrolBoat extends Ship {
        PatrolBoat() { super('P', 2); }
    }

    // === Exceptions ===

    /** Thrown when N is not a positive integer. */
    private static class BombInputException extends Exception {
        private static final long serialVersionUID = 1L;
    }

    /** Thrown when MODE is not d, D, r, or R. */
    private static class ModeInputException extends Exception {
        private static final long serialVersionUID = 1L;
    }

    /**
     * Thrown when a coordinate is already shot, out of range, or malformed.
     * Caught in the game loop; prints "Try again" without consuming a bomb.
     */
    private static class HitException extends Exception {
        private static final long serialVersionUID = 1L;
    }
}
