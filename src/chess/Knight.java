package chess;

import java.util.ArrayList;

public class Knight extends Piece{
    public Knight(String location, String color){
        super(location, color);
    }

    public boolean legalMove(String destination, ArrayList<ReturnPiece> pieces){
        int newRank = Integer.parseInt(destination.substring(1));
        char newFile = destination.charAt(0);
        //If file or rank is out of bounds
        if (newFile < 'a' || newFile > 'h' || newRank < 1 || newRank > 8) {
            return false;
        }
        //calculate differences in rank and file
        int rankDifference = Math.abs(this.rank-newRank);
        int fileDifference = Math.abs(this.file - newFile);
        //Make sure move is valid, then traverse through board to see if destination is occupied
        if ((rankDifference == 2 && fileDifference == 1) || (rankDifference == 1 && fileDifference == 2)){
            // Check if the destination square is occupied
            for (ReturnPiece piece : pieces) {
                if (piece.pieceFile.name().charAt(0) == newFile && piece.pieceRank == newRank) {
                    // If the destination square is occupied by an opponent's piece, eliminate it
                    if (!piece.pieceType.name().startsWith(this.color)) {
                        //pieces.remove(piece);
                        break;
                    }
                }
            }
            return true;
        }

        return false;
    }
}
