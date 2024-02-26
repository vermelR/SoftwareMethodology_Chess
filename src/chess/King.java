package chess;

import java.util.ArrayList;
import java.util.HashMap;

public class King extends Piece {
    private int movesMade;
    static HashMap<ReturnPiece, Integer> piecesMoves; 
    private boolean castleChecker; 
    private ReturnPiece rp; 

    public King(String location, String color, int movesMade){
        super(location, color);
        this.movesMade = movesMade;
        castleChecker = false;
    }

    public ReturnPiece getRp() {
        return rp;
    }

    public void setRp(ReturnPiece rp) {
        this.rp = rp;
    }

    public boolean castleChecker(){
        return castleChecker;
    }

    public boolean legalMove(String destination, ArrayList<ReturnPiece> pieces){
        int newRank = Integer.parseInt(destination.substring(1)); 
        char newFile = destination.charAt(0);
    
        if (newFile < 'a' || newFile > 'h' || newRank < 1 || newRank > 8) {
            return false;
        }
        
        if(newFile == this.file && newRank == this.rank) return false;
        
        if(Math.abs(newFile - this.file) <= 1 && Math.abs(newRank - this.rank) <= 1){
             ReturnPiece destinationPiece = getDestinationPiece(pieces, newRank, newFile);
             
             if(destinationPiece != null){
                 

                 if(isCanKill(destinationPiece)){
                     
                     
                     return true;
                 }
                 
                 else return false;
             }else{
             
                 
             return true;
        }
    } else if (Math.abs(newFile - this.file) == 2 && newRank == this.rank && this.movesMade == 0){ 
        if(Chess.isOwnKingInCheck(pieces)) return false; 
        int rightOrLeft = (newFile > this.file) ? 1 : -1; 
        ReturnPiece.PieceFile fileOfRook = ReturnPiece.PieceFile.a; 
        ReturnPiece.PieceFile fileTargetDest = ReturnPiece.PieceFile.c; 
        if(rightOrLeft == 1) { 
            fileOfRook = ReturnPiece.PieceFile.h; 
            fileTargetDest = ReturnPiece.PieceFile.g;
        }
        
        if(!noObstructions(newFile, pieces, this.rank)) return false;
        
        ReturnPiece possibleRook = foundRook(fileOfRook + "" + this.rank, pieces);
        
        if(possibleRook != null && piecesMoves.get(possibleRook) == 0){
            
            this.rp.pieceFile = fileTargetDest;
            if(fileOfRook.equals(ReturnPiece.PieceFile.a)) possibleRook.pieceFile = ReturnPiece.PieceFile.d;
            else {
                
                possibleRook.pieceFile = ReturnPiece.PieceFile.f;
            }
            
            if(Chess.isOwnKingInCheck(pieces)){
                
                this.rp.pieceFile = ReturnPiece.PieceFile.e;
                
                if(rightOrLeft == -1) possibleRook.pieceFile = ReturnPiece.PieceFile.a;
                else possibleRook.pieceFile = ReturnPiece.PieceFile.h; 
                return false;
            }
            this.castleChecker = true; 
            return true;
        }
      }
        //For all other cases, it's an invalid move.
        return false;
    }

    /**
     * Gets the destination piece at the given rank and file.
     * @param pieces list of pieces to use from when finding the piece at the given rank and file.
     * @param newRank represents the rank of the piece at the destination.
     * @param newFile represents the file of the piece at the destination.
     * @return true if there is a destination piece that exists at that newRank and newFile, otherwise return false.
     */
    
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

    /**
     * Method that takes in a destination piece to see if you can kill the destination piece 
    using the source piece.
     * @param destinationPiece //Takes in the destination piece to see if you can kill that destination piece.
     * @return true if the king can kill the piece at destination otherwise return false.
     */
    public boolean isCanKill(ReturnPiece destinationPiece){
        //Check to see if destinationPiece color is different than sourcePiece color(Piece that you are trying to move.)
        return (destinationPiece.pieceType + "").charAt(0) != color.charAt(0);
    }

    /**
     * Method that checks to see if there are not any pieces you will jump over moving the piece from source to destination.
     * @param newFile represents the file at destination
     * @param rank checks to see if there is an obstruction when the king moves sideways in the same rank.
     * @param pieces , aka the board used for removing pieces that have been killed.
     * @return false if there are any obstructions on the board, otherwise true.
     */

    public boolean noObstructions(char newFile, ArrayList<ReturnPiece> pieces, int rank){
        int fileUpdate = Integer.compare(newFile, this.file);

        //Counter used to see whether you can move the piece at source along the path to destination.
        int currentFile = this.file;
        
        //Keep doing while you reach the destination position.
        while(currentFile != newFile){
            //Update the currentFile and rank to reach a new position
            currentFile += fileUpdate;

            for (ReturnPiece piece : pieces) {
                if (piece.pieceFile.name().charAt(0) == currentFile && piece.pieceRank == rank) {
                    //Piece is being jumped over on the path from moving source piece to destination.
                    return false;
                }
            }
        }
        return true; //No obstructions at the end.
    }

    /**
     * Method that checks to see if there is a rook ReturnPiece that exists at that destination position.
     * @param destination position that you use to check to see if a rook of the same color as the king exists at that pos.
     * @param pieces used to find the piece that exists at that position.
     * @return true if you found the rook, else return false.
     */
    public ReturnPiece foundRook(String destination, ArrayList<ReturnPiece> pieces){
        //Get the file and rank from destination.
        char destFile = destination.charAt(0);
        int destRank = Integer.parseInt(destination.charAt(1) + "");
        //Get destination piece
        for (ReturnPiece piece : pieces) {
            //Found piece of file and rank that is a rook
            if (piece.pieceFile.name().charAt(0) == destFile && piece.pieceRank == destRank) {
                if(piece.pieceRank == destRank && (piece.pieceType.name().substring(0, 1).equals(this.color))) return piece;
            }
        }
        return null; //Didn't find the rook at the end.
    }
}
