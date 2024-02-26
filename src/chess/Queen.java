package chess;

import java.util.ArrayList;

public class Queen extends Piece {
    public Queen(String location, String color){
        super(location, color);
    }

    public boolean legalMove(String destination, ArrayList<ReturnPiece> pieces){
        int newRank = Integer.parseInt(destination.substring(1));
        char newFile = destination.charAt(0);
        //If file or rank is out of bounds
        if (newFile < 'a' || newFile > 'h' || newRank < 1 || newRank > 8) {
            return false;
        }

        int rankDifference = Math.abs(newRank - this.rank);
        int fileDifference = Math.abs(newFile - this.file);

        if ((rankDifference > 0 && fileDifference == 0) || (rankDifference == 0 && fileDifference > 0)){
            //moving vertical
            if (rankDifference > 0){
                int rankChange = this.rank > newRank ? -1 : 1; //rankChange is to see if it is moving up or down

                //keep moving Queen by one spot
                for (int i = this.rank + rankChange; i != newRank; i+=rankChange){
                    //check to see if any piece exists in path before you reach destination
                    ReturnPiece isObstruction = getPieceAtSquare(this.file, i, pieces);
                    if (isObstruction != null) return false;
                }
            }
            //moving horizontal
            if (fileDifference > 0){
                int fileChange = this.file > newFile ? -1 : 1; //fileChange is to see if it is moving left or right
                //keep moving Queen by one spot
                for (char i = (char) (this.file + fileChange); i != newFile; i+=fileChange){
                    //check to see if any piece exists in path before you reach destination
                    ReturnPiece isObstruction = getPieceAtSquare(i, this.rank, pieces);
                    if (isObstruction != null) return false;
                }
            }
            //now you have to be at destination, so check if there is a piece at the destination
            ReturnPiece destinationPiece = getPieceAtSquare(newFile, newRank, pieces);
            if (destinationPiece != null && !destinationPiece.pieceType.name().startsWith(this.color)) {
                //pieces.remove(destinationPiece); // Remove the captured piece
                return true; // Valid capture move
            }
            else if (destinationPiece != null && destinationPiece.pieceType.name().startsWith(this.color)) return false; //piece at destination is same color as piece being moved
            return true; // Valid non-capture move
        }
        
        //moving diagonally -- basically a bishop implementation
        if (rankDifference == fileDifference){
            char currFile = this.file;
            int currRank = this.rank;
            int rankDirection = Integer.compare(newRank, this.rank); //finds out if it is moving up or down
            int fileDirection = Integer.compare(newFile, this.file); //finds out if it is moving left or right

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
        return false;
    }

    public ReturnPiece getPieceAtSquare(char file, int rank, ArrayList<ReturnPiece> pieces){
        for (ReturnPiece piece : pieces) {
            if (piece.pieceFile.name().charAt(0) == file && piece.pieceRank == rank) {
                return piece;
            }
        }
        return null;
    }
}

