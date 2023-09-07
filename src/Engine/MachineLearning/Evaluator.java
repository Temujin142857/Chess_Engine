package Engine.MachineLearning;

import Chess_Board.Chess_Set.Pieces_Classes.Piece;
import net.sf.javaml.classification.Classifier;
import net.sf.javaml.classification.KNearestNeighbors;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DenseInstance;
import net.sf.javaml.core.Instance;
import net.sf.javaml.tools.data.FileHandler;

import java.io.File;
import java.io.IOException;

public class Evaluator {


    public void train(){
        Dataset data = null;
        double[] values=new double[]{};
        Instance instance = new DenseInstance(values);

        try {
            data = FileHandler.loadDataset(new File("datafile.csv"), 64, ",");
        } catch (IOException e) {
            e.printStackTrace();
        }
        /* Contruct a KNN classifier that uses 5 neighbors to make a
         *decision. */
        Classifier knn = new KNearestNeighbors(5);
        knn.buildClassifier(data);
    }

    public double calculate_strength(Piece[][] position) {
        double strength = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                switch (position[i][j].getName()) {
                    case "EMPTY":
                        continue;
                    case "WPawn":
                        strength += 10;
                        //these two are for if this pawn is defending another pawn
                        if (i + 1 < 8 && position[i + 1][j + 1].getName().equals("WPawn")) {
                            strength += 1;
                        }
                        if (i - 1 > 0 && position[i - 1][j + 1].getName().equals("WPawn")) {
                            strength += 1;
                        }
                        //maybe add isolated pawn detection and such
                        //prioritizes controlling the centre
                        if (j == 3 || j == 4) {
                            if (i == 3 || i == 4)  strength += 5;
                            if (i == 2 || i == 5) strength += 4;
                        }
                        //incentive to get promotion
                        else if (j == 6) {
                            strength += 9;
                        }

                        break;

                    case "WKnight":
                        strength += 25;
                        //the knight is stronger the more moves it has
                        strength += (position[i][j].getPossibleMoves().size());
                        break;

                    case "WBishop":
                        strength += 30;
                        for (int[] possibleMove : position[i][j].getPossibleMoves()) {
                            strength += 0.5;//stronger when it can see lot's of squares
                        }
                        break;

                    case "WRook":
                        strength += 50;
                        break;

                    case "WQueen":
                        strength += 90;
                        break;

                    case "WKing":
                        strength += 900;
                        if (j + 1 < 8) {
                            if (i - 1 >= 0 && position[i - 1][j + 1].getName().equals("WPawn")) {
                                strength += 1;
                            }
                            if (position[i][j + 1].getName().equals("WPawn")) {
                                strength += 1;
                            }
                            if (i + 1 < 8 && position[i + 1][j + 1].getName().equals("WPawn")) {
                                strength += 1;
                            }
                        }
                        //add incentives to keep the king on the back ranks
                        strength += 7 - j;
                        //add incentive to castle

                        //add incentive to use the king to promote pawns
                        break;

                    case "BPawn":
                        strength -= 10;
                        //these two are for if this pawn is defending another pawn
                        if (i + 1 < 8 && position[i + 1][j - 1].getName().equals("BPawn")) {
                            strength -= 1;
                        }
                        if (i - 1 > 0 && position[i - 1][j - 1].getName().equals("BPawn")) {
                            strength -= 1;
                        }
                        //prioritizes controlling the centre
                        if (j == 3 || j == 4) {
                            if (i == 3 || i == 4) strength -= 5;
                            if (i == 2 || i == 5) strength -= 4;
                        }
                        //incentive to get promotion
                        if (j == 1) {
                            strength -= 9;
                        }
                        break;

                    case "BKnight":
                        strength -= 30;
                        //the knight is stronger the more moves it has
                        strength -= (position[i][j].getPossibleMoves().size());
                        break;

                    case "BBishop":
                        strength -= 30;
                        for (int[] possibleMove : position[i][j].getPossibleMoves()) {
                            strength -= 0.5;//slightly stronger when it's controlling a long diagonal
                        }
                        break;

                    case "BRook":
                        strength -= 50;
                        break;

                    case "BQueen":
                        strength -= 90;
                        break;

                    case "BKing":
                        strength -= 900;
                        if (j - 1 >= 0) {
                            if (i - 1 >= 0 && position[i - 1][j - 1].getName().equals("BPawn")) {
                                strength -= 1;
                            }
                            if (position[i][j - 1].getName().equals("BPawn")) {
                                strength -= 1;
                            }
                            if (i + 1 < 8 && position[i + 1][j - 1].getName().equals("BPawn")) {
                                strength -= 1;
                            }
                        }
                        strength -= j;
                        break;
                }
            }
        }
        return strength;
    }
}
