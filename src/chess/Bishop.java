package chess;

import java.util.ArrayList;

public class Bishop extends Piece {
    public Bishop(String location, String color){
        super(location, color);
    }
    
    public boolean legalMove(String destination, ArrayList<ReturnPiece> pieces){
        int newRank = Integer.parseInt(destination.substring(1));
        char newFile = destination.charAt(0);
        //If file or rank is out of bounds
        if (newFile < 'a' || newFile > 'h' || newRank < 1 || newRank > 8) {
            return false;
        }
        int changeInRank = Math.abs(this.rank-newRank);
        int changeInFile = Math.abs(this.file - newFile);
        if (changeInRank != changeInFile) return false; //make sure it is moving diagonally

        int rankDirection = Integer.compare(newRank, this.rank); //finds out if it is moving up or down
        int fileDirection = Integer.compare(newFile, this.file); //finds out if it is moving left or right
        char currFile = this.file;
        int currRank = this.rank;

        while (currFile != newFile){
            currFile += fileDirection;
            currRank += rankDirection;

            //traverse through board to see if any piece exists at currFile and currRank
            for (ReturnPiece piece : pieces) {
                if (piece.pieceFile.name().charAt(0) == currFile && piece.pieceRank == currRank) {
                    // If the destination square is occupied by an opponent's piece, eliminate it
                    if (!piece.pieceType.name().startsWith(this.color) && currFile == newFile) {
                        //pieces.remove(piece);
                        break;
                    }
                    // If there's any piece in the path and it is not in the destination square, the move is not valid (either piece is same color, or desired destination comes after piece)
                    else return false;
                    
                }
            }
        }
        
        return true;
    }
}
