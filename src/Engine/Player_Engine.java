package Engine;


import Chess_Board.Chess_Set.NotAPawnException;
import Chess_Board.Chess_Set.Pieces_Classes.Piece;
import Chess_Board.Player;
import Engine.MachineLearning.Evaluator;
import Chess_Board.Chess_Set.Board;
import Chess_Board.GUI;


import java.util.ArrayList;
import java.util.HashMap;

public class Player_Engine extends Player{
    private final String filename="moves.csv";
    private final int batchSize=0;
    private final HashMap<String,double[]> pieceValues=new HashMap<>(){};
    private Evaluator evaluator;
    private int depth;

    /**
     * Constructor
     * @param colour the colour which this player will be using
     */
    public Player_Engine(char colour,GUI gui,int depth) {
        super(colour,gui);
        evaluator=new Evaluator();
        this.depth=depth;
    }

    /**
     * might be useful if I get machine learning operational
     * @param colour
     * @param gui
     * @param evaluator
     */
    public Player_Engine(char colour, GUI gui, Evaluator evaluator){
        super(colour,gui);
        this.evaluator=evaluator;
    }


    /**
     * this function creates all the possible moves as nodes
     * it performs a search for each of those moves to determine it's strength
     * then picks the best one and returns it
     * it uses a unique thread for each possible move, and collects all the results once they've finished the calculations
     * I didn't use any alphabeta pruning for this level, as it might be tricky to interact with the multithreading
     * @param board the current board
     * @return the best move in the position, as an int[][] made up of exactly two int[]
     *         the first int[] represents the location of the piece to move
     *         the second int[] represents the square to move the piece to
     */
    public int[][] getNextMove(Board board){
        Node root=buildTreeFirstLayer(new Node(0,new Board(clonePosition(board.getPieces()),clonePiece(board.getEnPassantable()))));
        double bstrength=10000;
        if(super.getColour()=='W'){bstrength=-10000;}
        Node best=new Node(bstrength);
        long time=System.currentTimeMillis();
        int i=0;
        ArrayList<AlphaBeta> threads=new ArrayList<>();
        for (Node child:root.children) {
            //root is the current position
            //each child is the board after the engine makes a move
            //the alpha beta search immediately goes a layer down,
            //therefore we want the min value, since the layer being analysed is made by the opponent
            //we are essentially manually doing the first layer of the minimax here, that's why it's flipped from normal
            threads.add(new AlphaBeta(child,this,super.getColour(),depth));
            threads.get(i).start();
            i++;
        }
        for (AlphaBeta thread:threads) {
            try {
                thread.join();
                double str=thread.getStrength();
                if(super.getColour()=='W'){
                    if(best.current_strength<=str){
                        if(best.piece!=null){System.out.println(best.current_strength+": "+best.piece[0]+","+best.piece[1]+" before");}
                        best=thread.getNode();
                        best.current_strength=str;
                    }
                }
                else{
                    if(best.current_strength>=str){
                        if(best.piece!=null){System.out.println(best.current_strength+": "+best.piece[0]+","+best.piece[1]+" before");}
                        best=thread.getNode();
                        best.current_strength=str;
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println(System.currentTimeMillis()-time);
        return new int[][]{best.piece, best.goTo};
    }


    /**
     * makes the first layer of the possible move tree
     * @param root the current position
     * @return the same current position with all the possible moves as children
     *  considered making it void, honestly works either way
     */
    public Node buildTreeFirstLayer(Node root){
        Piece[][] pieces= clonePosition(root.position.getPieces());
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (pieces[i][j].getName().charAt(0)==super.getColour()) {
                    for (int[] move : pieces[i][j].getPossibleMoves()) {
                        Board nextBoard=new Board(clonePosition(pieces),clonePiece(root.position.getEnPassantable()));
                        if(nextBoard.move(new int[]{i,j},move,this)==0){continue;}
                        root.addChild(new Node(evaluator.calculate_strength(nextBoard.getPieces()),nextBoard,new int[]{i,j},move));
                    }
                }
            }
        }
        return root;
    }


    /**
     * there's so few times you'd want a knight instead of a queen I decided to save some time and just
     * make it return queen
     * @param location
     * @param board
     */
    @Override
    public void getPromotionPiece(int[] location,Board board){
        try {
            board.promote(location,super.getColour()+"Queen");
        } catch (NotAPawnException e) {
            e.printStackTrace();
        }
    }

    public Piece promotion(int[] location){
        return Piece.makePiece(super.getColour()+"Queen",location);
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
