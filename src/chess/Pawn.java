package chess;

import java.util.ArrayList;

public class Pawn extends Piece {
    //String promotionPiece;
    boolean[] enpassantSide;
    private ReturnPiece possiblePassant; //Piece that is a possible passant, honestly it is the current piece.
    private static ReturnPiece actualPasssant = null; //Piece to use to keep track of which one to enpassant.

    public Pawn(String location, String color, boolean[] enpassantSide){
        super(location, color);
        //this.promotionPiece = promotionPiece;
        this.enpassantSide = enpassantSide; //Sets the boolean array of two sides.
    }
    /*public Pawn(String location, String color){
        super(location, color);
    }*/

    public static ReturnPiece getActualPasssant() {
        return actualPasssant;
    }

    public static void setActualPasssant(ReturnPiece actualPasssant) {
        Pawn.actualPasssant = actualPasssant;
    }

    public void setActualPiece(ReturnPiece actualPiece) {
        this.possiblePassant = actualPiece;
    }

    public boolean legalMove(String destination, ArrayList<ReturnPiece> pieces){
        int newRank = Integer.parseInt(destination.substring(1));
        char newFile = destination.charAt(0);
        //If file or rank is out of bounds
        if (newFile < 'a' || newFile > 'h' || newRank < 1 || newRank > 8) {
            return false;
        }
        //If the pawn is in one of the middle 4 rows, can only move one space forward ->
        //Represents second move of pawn onwards --> white cannot move backwards or more than 2 spaces up, black cannot move backwards or more than 2 spaces down
        else if (this.color.charAt(0) == 'W' && ((this.rank > 2 && ((newRank- this.rank) > 1)) || newRank < this.rank)) return false;
        else if (this.color.charAt(0) == 'B' && ((this.rank < 7 && ((this.rank - newRank) > 1)) || newRank > this.rank)) return false;
        //Pawn's first move -> Can only move one or two tiles forward.
        else if (Math.abs(newRank - this.rank) > 2) return false;
        //Pawn went up and/or moves sideways or not moved at all.
        else if (newRank == this.rank || (newFile-this.file > 1)) return false;
        //Check to see if there are any pieces you will jump over on the path of source piece
        //moving to destination.
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
        ReturnPiece destinationPiece = getDestinationPiece(pieces, newRank, newFile);
        //If the destinationPiece is not null, that means it exists in the Chess board
        if(destinationPiece != null){
            //Call the isCanKill() method to figure out whether you can kill.
            return isCanKill(destinationPiece, pieces);
        }
                //If you can kill, then it's a valid move.
        //Otherwise
        else{
            //Enpassant logic
            //If pawn had been moved two pieces forward
            if((this.color.equals("B") && newRank-this.rank == -2) || (this.color.equals("W") && newRank-this.rank == 2)){
                //Set the enpassant for the currentPlayer's color to be true.
                if(this.color.equals("B")) enpassantSide[1] = true;
                else enpassantSide[0] = true;
                actualPasssant = possiblePassant; //This pawn represents the actual pawn the opponent can perform enpassant on.
                return true;
            }else{
                //If you moved the pawn one tile forward, then that's fine.
                if(((this.color.equals("B") && newRank-this.rank == -1) || (this.color.equals("W") && newRank-this.rank == 1)) && this.file == newFile) {
                    actualPasssant = null; //Current player didnt' use their pawn to take down the opponent's pawn through enpassant, so hence you missed the opportunity to enpassant.
                    return true;
                }
                if(newFile == this.file && newRank == this.rank) return false; //If you haven't moved the piece at all, then that's not a valid move.
            }
            //Check to see if the other side of enpassant is true or not so that we
            //can perform enpassant on the other side's pawn that is eligible for enpassant.

            if (this.color.equals("W")) {
                if (enpassantSide[1] == true) {
                    if (isValidEnpassant()) {
                        return true;
                    }
                }
            } else {
                if (enpassantSide[0] == true) {
                    if (isValidEnpassant()) {
                        return true;
                    }
                }
            }

            //If the destination is diagonal to the source and enpassant is not valid, you can't move the pawn diagonally at an empty position.
             if(Math.abs(newRank-this.rank) == 1 && Math.abs(newFile-this.file) == 1) {
                return false;
             }
        }
        return true;
    }
    /**
     * Method that checks to see if the move is a valid enpassant move.
     * @return true if it's a valid enpassant move, and false if it's not.
     */
    public boolean isValidEnpassant() {
        return Math.abs(actualPasssant.pieceFile.name().charAt(0) - this.file) == 1
            && actualPasssant.pieceRank == this.rank
            && !actualPasssant.pieceType.name().substring(0, 1).equals(this.color);
    }
    

    private ReturnPiece getDestinationPiece(ArrayList<ReturnPiece> pieces, int newRank, char newFile) {
        //Get destination piece.
        ReturnPiece destinationPiece = null; //Have a variable that acts as a reference to the destination piece.
        for(ReturnPiece piece : pieces){
            //If destination piece file and rank is the same as the newFile and newRank respectively
            if(piece.pieceFile.name().equalsIgnoreCase(newFile + "") && piece.pieceRank == newRank){
                //Looked for the right destination piece then.
                destinationPiece = piece;
            }
        }
        return destinationPiece;
    }

    //Method that takes in a destination piece and figures out whether you can kill that piece or not.
    public boolean isCanKill(ReturnPiece destination, ArrayList<ReturnPiece> boardPieces){
        //If both source and destination pieces make a diagonal (destination is one up, one down from source) AND are of different colors
            //Then you can kill.
            int diffInRank = Math.abs(destination.pieceRank-this.rank);
            int diffInFile = Math.abs(destination.pieceFile.name().charAt(0)-this.file);

            if(diffInRank == 1 && diffInFile == 1 && destination.pieceType.name().charAt(0) != this.color.charAt(0)){
            //Remove destination piece from the ArrayList
            //boardPieces.remove(destination);
            //Return true to allow the piece to move at that position.
            return true;
        }
        //Otherwise you can't.
        return false;
    }

    /**
     * Checks to see if the pawn is eligible for promotion only if the destination piece that pawn is moving at
     * is in the first (if pawn is black) or last row (if pawn is white).
     * @param destination represents the destination to move pawn at.
     * @return true if the pawn is eligible for pawn promotion, false if not
     */
    public boolean isEligibleForPromotion(String destination){
        int newRank = Integer.parseInt(destination.substring(1));
        if ((this.color.charAt(0) == 'W' && newRank == 8) || (this.color.charAt(0) == 'B' && newRank == 1)) return true;
        else return false;
    }
}
