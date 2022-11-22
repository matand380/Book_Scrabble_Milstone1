package test;


import java.util.*;
import java.util.regex.Matcher;

public class Board {

    public static final int doubleWord = 1;
    public static final int tripleWord = 2;
    public static final int doubleLetter = 3;
    public static final int tripleLetter = 4;
    private static final int width = 15;
    private static final int height = 15;
    private static Board boardInstance = null;
    private final Location boardCenter = new Location(7, 7);
    private final Location[] tripleWordLocation = new Location[]{
            new Location(0, 0),
            new Location(7, 0),
            new Location(14, 0),
            new Location(0, 7),
            new Location(14, 7),
            new Location(0, 14),
            new Location(7, 14),
            new Location(14, 14)};
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
            new Location(9, 13)};
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
            new Location(11, 14),};
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
            new Location(7, 7),};
    private int wordCounter = 0;
    private Tile[][] gameGrid; // this is Tiles matrix for every game situation
    private Location[][] grid; //the base grid for the game
    private Map<Location, Integer> bonusTiles; //save in map all the bonus squares


    /*
     * Board C'tor
     * initialize bonus squares
     * initialize special tiles
     * initialize grid
     */
    private Board() {
        bonusTiles = new HashMap<>();
        for (Location loc : tripleWordLocation) {
            bonusTiles.put(loc, tripleWord);
        }
        for (Location loc : tripleLetterLocation) {
            bonusTiles.put(loc, tripleLetter);
        }
        for (Location loc : doubleLetterLocation) {
            bonusTiles.put(loc, doubleLetter);
        }
        for (Location loc : doubleWordLocation) {
            bonusTiles.put(loc, doubleWord);
        }
        grid = new Location[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                grid[i][j] = null;
            }
        }
        gameGrid = new Tile[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                gameGrid[i][j] = null;
            }
        }
    }

    public static Board getBoard() {
        if (boardInstance == null) {
            return boardInstance = new Board();
        } else return boardInstance;
    }

    public int getWordCounter() {
        return wordCounter;
    }

    public void setWordCounter(int wordCounter) {
        this.wordCounter = wordCounter;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Tile[][] getTiles() {
        return gameGrid.clone();
    }

    ///private  helper methods
    private void setTiles(Word word) {
    }

    public boolean boardLegal(Word word) {
        boolean gridLegal = isGridLegal(word);
        boolean firstWord;
        boolean tileConnected;
        boolean notRequireReplacement;

        if (wordCounter == 0) {
            firstWord = firstWordCheck(word);
            return gridLegal && firstWord;
        }
        tileConnected = isTileConnected(word);
        notRequireReplacement = notRequireLetterReplacement(word);
        return gridLegal && tileConnected && notRequireReplacement;
    }

    public boolean dictionaryLegal(Word word) {
        return true;
    }

    public int getScore(Word word) {
        // TODO: 22/11/2022 The function has to remove bonuses that already given
        // TODO: 22/11/2022 resolve the get method in the hashmap
        Location[] checkBonus = wordToLocation(word);
        int sum = 0;
        int DW = 1;
        int TW = 1;

        for (Location l : checkBonus) {
//            Integer y = bonusTiles.get(l);
            if (Arrays.stream(doubleLetterLocation).anyMatch(location -> location.locationCompare(l))) {
                sum += l.getTile().score * 2;
            } else if (Arrays.stream(tripleLetterLocation).anyMatch(location -> location.locationCompare(l))) {
                sum += l.getTile().score * 3;

            } else if (Arrays.stream(doubleWordLocation).anyMatch(location -> location.locationCompare(l))) {
                sum += l.getTile().score;
                DW = 2;

            } else if (Arrays.stream(tripleWordLocation).anyMatch(location -> location.locationCompare(l))) {
                sum += l.getTile().score;
                TW = 3;
            } else {
                sum += l.getTile().score;
            }
        }
        return sum * TW * DW;
    }

    public int tryPlaceWord(Word word) {
        boolean dictionaryLegal = dictionaryLegal(word);
        boolean boardLegal = boardLegal(word);
        if (dictionaryLegal && boardLegal) {
            int sum = 0;
            ArrayList<Word> words = new ArrayList<>(getWords(word));
            for (Word w : words) {
                if (dictionaryLegal(w)) {
                    setWordCounter(getWordCounter() + 1);
                   setTilesOnBoard(w);
                    sum += getScore(w);
                }
            }
            return sum;
        } else return 0;
    }


    // TODO: 17/11/2022 keep working
    public ArrayList<Word> getWords(Word word) {
        ArrayList<Word> temp = new ArrayList<>();
        temp.add(word);
        return temp;
    }


    private void setTilesOnBoard(Word word) {
        // TODO: 22/11/2022 remove tiles of the word from the quantitiesCounter
        int i;
        if (word.isVertical()) {
            for (i = 0; i < word.getTiles().length; i++) {
                if (word.getTiles()[i] == null && gameGrid[word.getRow() + i][word.getCol()] != null) {
                    continue;
                } else {
                    gameGrid[word.getRow() + i][word.getCol()] = word.getTiles()[i];
                }
            }

        } else
            for (i = 0; i < word.getTiles().length; i++) {
                if (word.getTiles()[i] == null && gameGrid[word.getRow()][word.getCol() + i] != null) {
                    continue;
                } else {
                    gameGrid[word.getRow()][word.getCol() + i] = word.getTiles()[i];
                }
            }
    }

    //gets the word without the required replacement (e.g. without R in FARMS)
    private boolean notRequireLetterReplacement(Word word) {
        int i;
        if (word.isVertical()) {
            for (i = 0; i < word.getTiles().length; i++) {
                if (word.getTiles()[i] != null) {
                    if (gameGrid[word.getRow() + i][word.getCol()] != null)
                        return false;
                } else if (word.getTiles()[i] == null && gameGrid[word.getRow() + i][word.getCol()] == null)
                    return false;
            }
        } else
            for (i = 0; i < word.getTiles().length; i++) {
                if (word.getTiles()[i] != null) {
                    if (gameGrid[word.getRow()][word.getCol() + i] != null)
                        return false;
                } else if (word.getTiles()[i] == null && gameGrid[word.getRow()][word.getCol() + i] == null) {
                    return false;
                }
            }
        return true;


    }

    // TODO: 17/11/2022 check again this method in the end
    private boolean isTileConnected(Word word) {
        int currRow = word.getRow(), currCol = word.getCol();
        for (int i = 0; i < word.getTiles().length; i++) {
            if (currRow - 1 >= 0 && gameGrid[currRow - 1][currCol] != null)
                return true;
            if (currRow + 1 >= 0 && gameGrid[currRow + 1][currCol] != null)
                return true;
            if (currCol - 1 >= width && gameGrid[currRow][currCol - 1] != null)
                return true;
            if (currCol + 1 >= width && gameGrid[currRow][currCol + 1] != null) // check
                return true;
            if (word.isVertical()) {
                currRow += 1;
            } else {
                currCol += 1;
            }
        }
        return false;
    }


    private boolean firstWordCheck(Word word) {
        if (gameGrid[7][7] == null) {
            if (word.isVertical() && word.getCol() == 7 && word.getRow() <= 7)
                return word.getRow() + word.getTiles().length >= 7 && word.getRow() + word.getTiles().length < width;
            else if (!word.isVertical() && word.getRow() == 7 && word.getCol() <= 7)
                return word.getCol() + word.getTiles().length >= 7 && word.getCol() + word.getTiles().length < height;

        }
        return false;
    }

    private boolean isGridLegal(Word word) {
        if (word.getRow() >= 0 && word.getCol() >= 0 && word.getRow() < width && word.getCol() < height) {
            if (word.isVertical()) {
                return word.getRow() + word.getTiles().length < width;
            } else return word.getCol() + word.getTiles().length < height;
        } else return false;
    }

    private Location[] wordToLocation(Word word) {
        Location[] temp = new Location[word.getTiles().length];
        if (word.isVertical()) {
            for (int i = 0; i < word.getTiles().length; i++) {
                if (word.getTiles()[i]!=null)
                {
                temp[i] = new Location(word.getRow() + i, word.getCol(), word.getTiles()[i]);
                }
                else if (word.getTiles()[i]==null)
                {
                    temp[i] = new Location(word.getRow() + i, word.getCol(), gameGrid[word.getRow() + i][word.getCol()]);

                }

            }
        } else
            for (int i = 0; i < word.getTiles().length; i++) {
                if (word.getTiles()[i]!=null){
                    temp[i] = new Location(word.getRow(), word.getCol() + i, word.getTiles()[i]);
            }
            else if (word.getTiles()[i]==null)
        {
            temp[i] = new Location(word.getRow(), word.getCol() + i, gameGrid[word.getRow()][word.getCol()+i]);

        }
            }
        return temp;
    }


    //helper class - Location
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

        public void setTile(Tile t) {
            tile = t;
        }

        public boolean isOccupied() {
            return tile != null;
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
* private boolean isValidLocation(int x, int y){
		return x>=0 && y>=0 && x<width && y< height;
	}

* Word is vertical - true/false
* First word has to be row 7 or col 7
* 	private boolean validFirstMove(List<Location> wordPlayed)

* New word has to be connected to existing one or using existing tiles
* doesn't need to replace existing tiles - comparing chars
* getWords -
*   check if dictionaryLegal (meanwhile - always true)
*   return Tile[] of all the new words that has been created from the word we put, includes the word we put
*   return type is ArrayList<Word>
*getScore
*   this check has to be done for every word in the ArrayList from getWords
*   checks first triple/double letters - checkBonusLetter method
*   sum all the letters after bonuses (if exist) - sumTiles method
*   check if word is on triple/double word - checkBonusWord method
*   return sumTiles*checkBonusWord
*
*flow
*   given an optional word:
*       check if it is in the grid
*       return ArrayList of all new words it creates
*       for each word in the ArrayList check if it's dictionaryLegal
*       getScore for every word in the ArrayList
*       only if all words are legal we will sum all of its getScore
*
*
*
*
* public boolean isValidLocation(Location loc) {
		return loc!=null && isValidLocation(loc.getX(), loc.getY());
*
*
* //given a location, return the Location stored in the grids[][] on this board
	private Location getBoardLocation(Location loc){
		if(!isValidLocation(loc))
			return null;
		return grids[loc.getX()][loc.getY()];
*
*
* 	private Tile findAndRemoveSpecialTile(Location loc) //if bonus already given

 */