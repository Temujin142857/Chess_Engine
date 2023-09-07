package Engine;

import Chess_Board.Chess_Set.Board;
import Chess_Board.Chess_Set.NotAPawnException;
import Chess_Board.Chess_Set.Pieces_Classes.Piece;
import Chess_Board.Player;

import java.util.ArrayList;
import java.util.Iterator;

public class AlphaBeta extends Thread{
    private char c;
    private Player player;
    private volatile double strength;
    private Node node;
    private int depth;


    public AlphaBeta(Node node, Player player, char c,int depth){
        this.node=node;
        this.player=player;
        this.c=c;
        this.depth=depth;
    }

    @Override
    public void run(){
        boolean maxing=true;
        if (c=='W'){maxing=false;}
        strength= search(node, -10000,10000, depth,maxing);
    }

    public double getStrength(){
        return strength;
    }

    public Node getNode(){
        return node;
    }


    /**
     * performs a minimax search to a given depth, applies alpha beta pruning as it goes
     * added the building of the tree into the search itself, so that it could benifit from the alpha beta pruning
     * @param root original position to be evaluated
     * @param alpha upper bound of sorts
     * @param beta lower bound of sorts
     * @param depth amount of moves into the future to look
     * @param max whether to find the move with max strength or min strength
     * @return returns the strength of the position stored in the root
     */
    public double search(Node root, double alpha, double beta, int depth, boolean max){
        if(depth==0){return root.current_strength;}
        double bestEval;
        double currentEval;
        Iterator<Piece> iterator=root.position.getIterator();
        if(max){
            bestEval = -10000;
            while (iterator.hasNext()) {
                Piece piece=clonePiece(iterator.next());
                if (piece.getName().charAt(0)=='W') {
                    for (int[] move : piece.getPossibleMoves()) {
                        Board nextBoard = new Board(clonePosition(root.position.getPieces()), clonePiece(root.position.getEnPassantable()));
                        if(nextBoard.move(piece.getLocation(), move,player)==0){
                            continue;
                        }
                        Node child=new Node(calculate_strength(nextBoard.getPieces()), nextBoard);
                        root.addChild(child);
                        currentEval = search(child, alpha, beta, depth - 1, false);
                        bestEval = Math.max(bestEval, currentEval);
                        alpha = Math.max(alpha, currentEval);
                        if (beta <= alpha){return bestEval;}
                    }
                }
            }
        }
        else{
            bestEval = 10000;
            while (iterator.hasNext()) {
                Piece piece=clonePiece(iterator.next());
                if (piece.getName().charAt(0)=='B') {
                    for (int[] move : piece.getPossibleMoves()) {
                        Board nextBoard = new Board(clonePosition(root.position.getPieces()), clonePiece(root.position.getEnPassantable()));
                        if(nextBoard.move(piece.getLocation(), move,player)==0){
                            continue;
                        }
                        Node child=new Node(calculate_strength(nextBoard.getPieces()), nextBoard);
                        root.addChild(child);
                        currentEval= search(child,alpha,beta,depth-1,true);
                        bestEval=Math.min(bestEval,currentEval);
                        beta=Math.min(beta,currentEval);
                        if(beta<=alpha){return bestEval;}
                    }
                }
            }
        }
        return bestEval;
    }


    /**
     * handmade evaluation function
     * pretty basic atm, more of a chess knowledge thing
     * not super relavent to the goal of this project, improving my coding
     * @param position
     * @return
     */
    private double calculate_strength(Piece[][] position) {
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
                        else if (j == 1) {
                            strength -= 9;
                        }
                        break;

                    case "BKnight":
                        strength -= 25;
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



    public void getPromotionPiece(int[] location,Board board){
        try {
            board.promote(location,c+"Queen");
        } catch (NotAPawnException e) {
            e.printStackTrace();
        }
    }

    public Piece promotion(int[] location){
        return Piece.makePiece(c+"Queen",location);
    }

    /**
     * needed to clone the position on a board, and have it be an entirely new object
     * @param base takes a 2D piece array
     * @return returns a new obeject with the same values
     */
    private Piece[][] clonePosition(Piece[][] base){
        Piece[][] pieces= new Piece[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                pieces[i][j]=clonePiece(base[i][j]);
            }
        }
        return pieces;
    }

    /**
     * needed to make copies of pieces, but as new objects
     * @param base takes any piece
     * @return a new object with the same values as the original
     */
    private Piece clonePiece(Piece base){
        ArrayList<int[]> possibleMoves=null;
        ArrayList<int[]> blockedMoves=null;
        if(base==null)return null;
        if(base.getName().equals("EMPTY")){return Piece.makePiece("EMPTY",null);}
        if(base.getPossibleMoves()!=null) {
            possibleMoves = new ArrayList<>();
            for (int[] value : base.getPossibleMoves()) {
                possibleMoves.add(value.clone());
            }
        }
        if(base.getBlockedMoves()!=null) {
            blockedMoves = new ArrayList<>();
            for (int[] value : base.getBlockedMoves()) {
                blockedMoves.add(value.clone());
            }
        }
        return Piece.makePiece(base.getName(),base.getLocation().clone(),possibleMoves,blockedMoves);
    }

}
