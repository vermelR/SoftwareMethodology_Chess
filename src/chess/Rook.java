package chess;

import java.util.ArrayList;

public class Rook extends Piece {
    public Rook(String location, String color){
        super(location, color);
    }
    
    public boolean legalMove(String destination, ArrayList<ReturnPiece> pieces){
        int newRank = Integer.parseInt(destination.substring(1));
        char newFile = destination.charAt(0);
        //If file or rank is out of bounds
        if (newFile < 'a' || newFile > 'h' || newRank < 1 || newRank > 8) {
            return false;
        }
        //If file changes but not rank or vice versa
        if((Math.abs(newFile - this.file) > 0 && newRank-this.rank == 0) || (newFile - this.file == 0 && Math.abs(newRank-this.rank) > 0)){
            //It's a valid move so far.
            //If there are obstacles/pieces that interfere in the path to move source piece to destination
            //then that's an obstruction.
            if(!noObstructions(newFile, newRank, pieces)) return false; 
            //Get destination piece.
            ReturnPiece destinationPiece = null; //Have a variable that acts as a reference to the destination piece.
            for(ReturnPiece piece : pieces){
                //If destination piece file and rank is the same as the newFile and newRank respectively
                if(piece.pieceFile.name().equalsIgnoreCase(newFile + "") && piece.pieceRank == newRank){
                    //Looked for the right destination piece then.
                    destinationPiece = piece;
                }
            }
            //If the piece at destination exists
            if(destinationPiece != null){
                //Check to see if you can kill the piece by calling isCanKill() method.
                //If you can kill
                if(isCanKill(destinationPiece)){
                    //Just remove the destination piece from the arraylist
                    //pieces.remove(destinationPiece);
                    return true;
                }
                //Otherwise, you can't kill your own piece, that is an illegal move
                else return false;
            }else{
            //Otherwise
                //You are free to move the rook at that position.
            return true;
            }
        }
        //For all other edge cases, it's going to be an invalid move.
        return false;
    }

    //Method that takes in a destination piece to see if you can kill the destination piece 
    //using the source piece.
    public boolean isCanKill(ReturnPiece destinationPiece){
        //Check to see if destinationPiece color is different than sourcePiece color(Piece that you are trying to move.)
        return (destinationPiece.pieceType + "").charAt(0) != color.charAt(0);
    }

    /**
     * Method that checks to see if there are not any pieces you will jump over moving the piece from source to destination.
     * @param newFile represents the file at destination
     * @param newRank represents the rank at destination
     * @param pieces , aka the board used for removing pieces that have been killed.
     * @return false if there are any obstructions on the board, otherwise true.
     */

    public boolean noObstructions(char newFile, int newRank, ArrayList<ReturnPiece> pieces){
        int fileUpdate = Integer.compare(newFile, file);
        int rankUpdate = Integer.compare(newRank, rank);

        //Counters used to see whether you can move the piece at source along the path to destination.
        int currentFile = this.file;
        int currentRank = this.rank;
        
        //Keep doing while you reach the destination position.
        while(currentFile != newFile || currentRank != newRank){
            //Update the currentFile and rank to reach a new position
            currentFile += fileUpdate;
            currentRank += rankUpdate;

            //If you are at the destination position, you reached it.
            if(currentFile == newFile && currentRank == newRank) break;

            for (ReturnPiece piece : pieces) {
                if (piece.pieceFile.name().charAt(0) == currentFile && piece.pieceRank == currentRank) {
                    //Piece is being jumped over on the path from moving source piece to destination.
                    return false;
                }
            }
        }
        return true; //No obstructions at the end.
    }
}
