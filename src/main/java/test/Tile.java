package test;

import java.util.Objects;
import java.util.Random;

public class Tile {
    public final char letter;
    public final int score;

    ///C'tor
    private Tile(char letter, int score) {
        this.letter = letter;
        this.score = score;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tile tile)) return false;
        return letter == tile.letter && score == tile.score;
    }

    @Override
    public int hashCode() {
        return Objects.hash(letter, score);
    }


    public static class Bag {
        final int[] defaultQuantities;
        int[] quantitiesCounter;
        Tile[] tilesArray;


        private static Bag bagInstance = null;


        private Bag() {
            this.defaultQuantities = new int[]{9, 2, 2, 4, 12, 2, 3, 2, 9, 1, 1, 4, 2, 6, 8, 2, 1, 6, 4, 6, 4, 2, 2, 1, 2, 1};
            this.quantitiesCounter = defaultQuantities.clone();
            this.tilesArray = new Tile[26];
            tilesArray[0] = new Tile('A', 1);
            tilesArray[1] = new Tile('B', 3);
            tilesArray[2] = new Tile('C', 3);
            tilesArray[3] = new Tile('D', 2);
            tilesArray[4] = new Tile('E', 1);
            tilesArray[5] = new Tile('F', 1);
            tilesArray[6] = new Tile('G', 2);
            tilesArray[7] = new Tile('H', 4);
            tilesArray[8] = new Tile('I', 1);
            tilesArray[9] = new Tile('J', 8);
            tilesArray[10] = new Tile('K', 5);
            tilesArray[11] = new Tile('L', 1);
            tilesArray[12] = new Tile('M', 3);
            tilesArray[13] = new Tile('N', 1);
            tilesArray[14] = new Tile('O', 1);
            tilesArray[15] = new Tile('P', 3);
            tilesArray[16] = new Tile('Q', 10);
            tilesArray[17] = new Tile('R', 1);
            tilesArray[18] = new Tile('S', 1);
            tilesArray[19] = new Tile('T', 1);
            tilesArray[20] = new Tile('U', 1);
            tilesArray[21] = new Tile('V', 4);
            tilesArray[22] = new Tile('W', 4);
            tilesArray[23] = new Tile('X', 8);
            tilesArray[24] = new Tile('Y', 4);
            tilesArray[25] = new Tile('Z', 10);
        }

        public static Bag getBag() {
            if (bagInstance == null) {
                return bagInstance = new Bag();
            } else return bagInstance;

        }

        public Tile getRand() {
            if (size() > 0) {
                int rnd = new Random().nextInt(tilesArray.length);
                if (quantitiesCounter[rnd] != 0) {
                    quantitiesCounter[rnd]--;
                    return tilesArray[rnd];
                }
            }
            return null;

        }

        public Tile getTile(char other) {
            if (size() > 0) {
                if (other >= 'A' && other <= 'Z') {
                    int otherAsInt = other - 'A';
                    if (quantitiesCounter[otherAsInt] != 0) {
                        quantitiesCounter[otherAsInt]--;
                        return tilesArray[otherAsInt];
                    }
                }
                else return null;
            }
            return null;
        }

        public int size() {
            int count = 0;
            for (int quantity : quantitiesCounter) {
                count += quantity;
            }
            return count;
        }

        public int[] getQuantities() {
            return quantitiesCounter.clone();
        }

        public void put(Tile other) {
            if (size() < 98) {
                if (other.letter >= 'A' && other.letter <= 'Z')
                {
                int tileToInt = other.letter - 'A';
                if (quantitiesCounter[tileToInt] < defaultQuantities[tileToInt])
                    quantitiesCounter[tileToInt]++;
                }
            }
        }
    }


}

