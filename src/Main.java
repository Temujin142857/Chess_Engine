import Chess_Board.Chess_Set.Board;
import Chess_Board.GUI;
import DatabaseHandler.Streamer;
import Engine.Player_Engine;

import java.io.IOException;

public class Main { //main for the project, work in progress; play not finish.

    public static void main(String[] args) {
        play();
        //dataTest();
        //translatePGN("master_games.pgn","datafile");
        /**
        Player_Engine engine=new Player_Engine('W');
        int[][] move=engine.getNextMove(new Board());
        System.out.println(move[0][0]+","+move[0][1]+" goes to "+move[1][0]+","+move[1][1]);
        System.out.println("final answer, lock it in");
         */

    }

    //runs the game
    private static void play(){
        GUI gui= new GUI();
        gui.launch();
    }



    private static void translatePGN(String filename1, String filename2){
        Streamer streamer=new Streamer();
        streamer.pgnTodata(filename1,filename2);
    }

}
