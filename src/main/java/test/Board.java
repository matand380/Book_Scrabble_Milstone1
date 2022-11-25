package test;


import java.util.*;
import java.util.regex.Matcher;

public class Board {

    public static final int doubleWord = 1;
    public static final int tripleWord = 2;
    public static final int doubleLetter = 3;
    public static final int tripleLetter = 4;

    public static final int centerBonus = 5;
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
            new Location(13, 13),};

    private final Location[] centerBonusLocation = new Location[]{
            new Location(7, 7)
    };
    public  ArrayList<Word> tilesOnBoard = new ArrayList<Word>();
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
        for (Location loc : centerBonusLocation) {
            bonusTiles.put(loc, centerBonus);
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
        Location[] checkBonus = wordToLocation(word);
        int sum = 0;
        int DW = 1;
        int TW = 1;
        for (Location l : checkBonus) {

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
            } else if (Arrays.stream(centerBonusLocation).anyMatch(location -> location.locationCompare(l)) && bonusTiles.containsValue(centerBonus)) {
                sum += l.getTile().score;
                DW = 2;
                bonusTiles.remove(centerBonusLocation[0]);

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
                    sum += getScore(w);
                }
            }
            return sum;
        } else return 0;
    }


    public ArrayList<Word> getWords(Word word) {
        ArrayList<Word> temp = new ArrayList<>();
        ArrayList<Word> wordsArr = new ArrayList<>();
        Tile[] newWord = new Tile[word.getTiles().length];
        if (word.isVertical()) {
            for (int i = 0; i < word.getTiles().length; i++) {
                if (word.getTiles()[i] != null)
                    newWord[i] = word.getTiles()[i];
                else
                    newWord[i] = gameGrid[word.getRow() + i][word.getCol()];
            }
            Word currentWord = new Word(newWord, word.getRow(), word.getCol(), word.isVertical());
            temp.add(currentWord);
            for (int i = 0; i < word.getTiles().length; i++) {
                if (word.getTiles()[i] != null) {
                    if (gameGrid[word.getRow() + i][word.getCol() - 1] != null || gameGrid[word.getRow() + i][word.getCol() + 1] != null) {
                        temp.add(horizontalWord(word.getRow() + i, word.getCol(), word.getTiles()[i]));
                    }
                }
            }
            if (gameGrid[word.getRow() + word.getTiles().length][word.getCol()] != null || gameGrid[word.getRow() - 1][word.getCol()] != null) {
                Word tempW = checkPartOfVertical(word.getRow(), word.getCol(), word);
                if (tempW != word)
                    temp.add(tempW);
            }
        } else {
            for (int i = 0; i < word.getTiles().length; i++) {
                if (word.getTiles()[i] != null)
                    newWord[i] = word.getTiles()[i];
                else
                    newWord[i] = gameGrid[word.getRow()][word.getCol() + i];
            }
            Word currentWord = new Word(newWord, word.getRow(), word.getCol(), word.isVertical());
            temp.add(currentWord);
            for (int i = 0; i < word.getTiles().length; i++) {
                if (word.getTiles()[i] != null) {
                    if (gameGrid[word.getRow() + 1][word.getCol() + i] != null || gameGrid[word.getRow() - 1][word.getCol() + i] != null) {
                        temp.add(verticalWord(word.getRow(), word.getCol() + i, word.getTiles()[i]));
                    }
                }
            }
            if (gameGrid[word.getRow()][word.getCol() + word.getTiles().length] != null || gameGrid[word.getRow()][word.getCol() - 1] != null) {
                Word tempW = checkPartOfHorizontal(word.getRow(), word.getCol(), word);
                if (tempW != word)
                    temp.add(tempW);
            }
        }
        for (Word w : temp) {
            if (!tilesOnBoard.contains(w)) {
                setTilesOnBoard(w);
                wordsArr.add(w);
            }
        }
        return wordsArr;

    }


    private Word verticalWord(int row, int col, Tile tile) {
        int currentCol = row;
        int rowBegin;
        while (currentCol - 1 >= 0 && gameGrid[currentCol - 1][col] != null) {
            currentCol--;
        }
        rowBegin = currentCol;
        if (gameGrid[currentCol][col] == null) {
            currentCol++;
        }
        ArrayList<Tile> temp = new ArrayList<>();
        while (currentCol <= 14 && currentCol < row && gameGrid[currentCol][col] != null) {
            temp.add(gameGrid[currentCol][col]);
            currentCol++;
        }

        temp.add(tile);
        currentCol = row + 1;
        while (currentCol <= 14 && gameGrid[currentCol][col] != null) {
            temp.add(gameGrid[currentCol][col]);
            currentCol++;
        }
        Tile[] tiles = new Tile[(temp.size())];
        for (int i = 0; i < temp.size(); i++) {
            tiles[i] = temp.get(i);
        }
        return new Word(tiles, rowBegin, col, true);
    }

    private Word horizontalWord(int row, int col, Tile tile) {
        int currentCol = col;
        int colBegin;
        while (currentCol - 1 >= 0 && gameGrid[row][currentCol - 1] != null) {
            currentCol--;
        }
        colBegin = currentCol;
        if (gameGrid[row][currentCol] == null) {
            currentCol++;
        }
        ArrayList<Tile> temp = new ArrayList<>();
        while (currentCol <= 14 && currentCol < col && gameGrid[row][currentCol] != null) {
            temp.add(gameGrid[row][currentCol]);
            currentCol++;
        }

        temp.add(tile);
        currentCol = col + 1;
        while (currentCol <= 14 && gameGrid[row][currentCol] != null) {
            temp.add(gameGrid[row][currentCol]);
            currentCol++;
        }
        Tile[] tiles = new Tile[(temp.size())];
        for (int i = 0; i < temp.size(); i++) {
            tiles[i] = temp.get(i);
        }
        return new Word(tiles, row, colBegin, false);
    }

    private Word checkPartOfHorizontal(int row, int col, Word word) {
        Word newWord = horizontalWord(row, col, word.getTiles()[0]);
        if (newWord.getTiles().length != word.getTiles().length) {
            return newWord;
        }
        return word;
    }

    private Word checkPartOfVertical(int row, int col, Word word) {
        Word newWord = verticalWord(row, col, word.getTiles()[0]);
        if (newWord.getTiles().length != word.getTiles().length) {
            return newWord;
        }
        return word;
    }

    private void setTilesOnBoard(Word word) {
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
                if (word.getTiles()[i] != null) {
                    temp[i] = new Location(word.getRow() + i, word.getCol(), word.getTiles()[i]);
                } else if (word.getTiles()[i] == null) {
                    temp[i] = new Location(word.getRow() + i, word.getCol(), gameGrid[word.getRow() + i][word.getCol()]);

                }

            }
        } else
            for (int i = 0; i < word.getTiles().length; i++) {
                if (word.getTiles()[i] != null) {
                    temp[i] = new Location(word.getRow(), word.getCol() + i, word.getTiles()[i]);
                } else if (word.getTiles()[i] == null) {
                    temp[i] = new Location(word.getRow(), word.getCol() + i, gameGrid[word.getRow()][word.getCol() + i]);

                }
            }
        return temp;
    }


    //helper class - Location
    public  class Location {
        private  int x;
        private  int y;
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
            if (o == null || getClass() != o.getClass()) return false;
            Location location = (Location) o;
            return x == location.x && y == location.y && Objects.equals(tile, location.tile);
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y, tile);
        }
    }
}
