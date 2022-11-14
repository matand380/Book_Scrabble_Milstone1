package test;


import java.util.*;

public class Board {

    private static Board boardInstance = null;

    private static final int width = 15;
    private static final int height = 15;
    public static final int doubleWord = 1;
    public static final int tripleWord = 2;
    public static final int doubleLetter = 3;
    public static final int tripleLetter = 4;

    private Location[][] grid; //the base grid for the game
    private Map<Location, Integer> bonusSquares; //save in map all the bonus squares


    private final Location boardCenter = new Location(7, 7);

    private final Location[] tripleWordLocation = new Location[]{
            new Location(0, 0),
            new Location(7, 0),
            new Location(14, 0),
            new Location(0, 7),
            new Location(14, 7),
            new Location(0, 14),
            new Location(7, 14),
            new Location(14, 14)
    };
    private final Location[] tripleLetterLocation = new Location[]{
            new Location(5, 1),
            new Location(9, 1),
            new Location(1, 5),
            new Location(5, 5),
            new Location(9, 5),
            new Location(13, 5),
            new Location(1, 9),
            new Location(5, 9),
            new Location(9, 9),
            new Location(13, 9),
            new Location(5, 13),
            new Location(9, 13)
    };
    private final Location[] doubleLetterLocation = new Location[]{
            new Location(3, 0),
            new Location(11, 0),
            new Location(6, 2),
            new Location(8, 2),
            new Location(0, 3),
            new Location(7, 3),
            new Location(14, 3),
            new Location(2, 6),
            new Location(6, 6),
            new Location(8, 6),
            new Location(12, 6),
            new Location(3, 7),
            new Location(11, 7),
            new Location(2, 8),
            new Location(6, 8),
            new Location(8, 8),
            new Location(12, 8),
            new Location(0, 11),
            new Location(7, 11),
            new Location(14, 11),
            new Location(6, 12),
            new Location(8, 12),
            new Location(3, 14),
            new Location(11, 14),
    };
    private final Location[] doubleWordLocation = new Location[]{
            new Location(1, 1),
            new Location(1, 13),
            new Location(2, 2),
            new Location(2, 12),
            new Location(3, 3),
            new Location(3, 11),
            new Location(4, 4),
            new Location(4, 10),
            new Location(10, 4),
            new Location(10, 10),
            new Location(11, 3),
            new Location(11, 11),
            new Location(12, 2),
            new Location(12, 12),
            new Location(13, 1),
            new Location(13, 13),
    };

    /*
     * Board C'tor
     * initialize bonus squares
     * initialize special tiles
     * initialize grid
     */
    private Board() {
        bonusSquares = new HashMap<Location, Integer>();
        for (Location loc : tripleWordLocation) {
            bonusSquares.put(loc, tripleWord);
        }
        for (Location loc : tripleLetterLocation) {
            bonusSquares.put(loc, tripleLetter);
        }
        for (Location loc : doubleLetterLocation) {
            bonusSquares.put(loc, doubleLetter);
        }
        for (Location loc : doubleWordLocation) {
            bonusSquares.put(loc, doubleWord);
        }
        grid = new Location[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                grid[i][j] = new Location(i, j);
            }
        }
    }
    public int getWidth() {
        return width;
    }
    public int getHeight() {
        return height;
    }


    public static Board getBoard() {
        if (boardInstance == null) {
            return boardInstance = new Board();
        } else return boardInstance;
    }

    // TODO: 13/11/2022 from here down - need to be implemented
    public Tile[][] getTiles() {
        return null;
    }

    public boolean boardLegal(Word word) {
        return true;
    }

    public boolean dictionaryLegal(Word word) {
        return true;
    }

    public ArrayList<Word> getWords(Word word) {

        return null;
    }

    public int getScore(Word word) {
        return 1;
    }

    public int tryPlaceWord(Word word) {
        return 1;
    }


    public class Location {
        private int x;
        private int y;
        private Tile tile;


        // Construct a location at x,y with no tile.
        public Location(int x, int y) {
            this.x = x;
            this.y = y;
            this.tile = null;
        }

        // Construct a location at x,y with given tile.
        public Location(int x, int y, Tile t) {
            this.x = x;
            this.y = y;
            this.tile = t;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public Tile getTile() {
            return tile;
        }

        public boolean isOccupied() {
            return tile != null;
        }

        public void setTile(Tile t) {
            tile = t;
        }

        public boolean locationCompare(Location other) {
            return other != null && other.x == x && other.y == y;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Location location)) return false;
            return x == location.x && y == location.y && Objects.equals(tile, location.tile);
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y, tile);
        }
    }
}


/*
*Checklist for new words:
*
* Word is in the grid
* Word is vertical - true/false
* First word has to be row 7 or col 7
* New word has to be connected to existing one or using existing tiles
* doesn't need to replace existing tiles - using
*
 */