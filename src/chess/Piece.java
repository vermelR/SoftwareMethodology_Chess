package chess;

import java.util.*;

public abstract class Piece implements Cloneable {
    char file;
    int rank;
    String color;

    public Piece(String location, String color){
        //can split location of a piece into file and rank over here
        this.file = location.charAt(0);
        this.rank = Integer.parseInt(location.substring(1));
        this.color = color; //Color of piece you pass in anyways will be W or B
    }

    /**
     * Method that checks to see if the move made on a piece from source to destination is valid based on it's PieceType.
     * @param destination represents the destination that the piece should move from source
     * @param pieces , aka the board used for removing pieces that have been killed.
     * @return true if the move is valid so that you can make that move in Chess.play(), otherwise Illegal Move
     */
    public abstract boolean legalMove(String destination, ArrayList<ReturnPiece> pieces);
    
}