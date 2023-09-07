package Engine;

import java.util.ArrayList;
import Chess_Board.Chess_Set.Board;

public class Node {
    public ArrayList<Node> children=new ArrayList<>();
    public double current_strength;
    public Board position;
    public int[] rootMove;
    public int[] piece;
    public int[] goTo;
    private ArrayList<Node> leaves;

    public Node(double s){
        this.current_strength=s;
    }

    public Node(double s,Board b){
        this.current_strength=s;
        this.position=b;
    }

    public Node(double s, Board b,int[] piece,int[] goesTo){
        this.current_strength=s;
        this.position=b;
        this.piece=piece;
        this.goTo=goesTo;
    }

    public ArrayList<Node> getLeaves(){
        setLeaves(this);
        return leaves;
    }

    private void setLeaves(Node node){
        if(leaves==null)leaves=new ArrayList<>();
        else{
            if (node.children.size()==0){leaves.add(node);}
            else{
                for (Node child:node.children) {
                    setLeaves(child);
                }
            }
        }
    }


    public void setRootMove(int[] rootMove) {
        this.rootMove = rootMove;
    }

    public void addChild(Node child){
        children.add(child);
    }

    public void removeChild(Node child){
        children.remove(child);
    }

}
