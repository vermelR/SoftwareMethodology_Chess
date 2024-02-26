package chess;

import java.util.ArrayList;
import java.util.HashMap;

class ReturnPiece {
	static enum PieceType {WP, WR, WN, WB, WQ, WK, 
		            BP, BR, BN, BB, BK, BQ};
	static enum PieceFile {a, b, c, d, e, f, g, h};
	
	PieceType pieceType;
	PieceFile pieceFile;
	int pieceRank; 
	public String toString() {
		return ""+pieceFile+pieceRank+":"+pieceType;
	}
	public boolean equals(Object other) {
		if (other == null || !(other instanceof ReturnPiece)) {
			return false;
		}
		ReturnPiece otherPiece = (ReturnPiece)other;
		return pieceType == otherPiece.pieceType &&
				pieceFile == otherPiece.pieceFile &&
				pieceRank == otherPiece.pieceRank;
	}
}

class ReturnPlay {
	enum Message {ILLEGAL_MOVE, DRAW, 
				  RESIGN_BLACK_WINS, RESIGN_WHITE_WINS, 
				  CHECK, CHECKMATE_BLACK_WINS,	CHECKMATE_WHITE_WINS, 
				  STALEMATE};
	
	ArrayList<ReturnPiece> piecesOnBoard;
	Message message;
}

public class Chess {
	
	enum Player { white, black }
	
	static ArrayList<ReturnPiece> startPieceMap; 
	static HashMap<Player, String> pieceColor;
	static Player currPlayer; 
	static HashMap<ReturnPiece, Integer> piecesMoves; 

	static boolean[] enpassant; 

	public static ReturnPlay play(String move) {

		/* FILL IN THIS METHOD */

        String[] moveParts = move.split(" "); 
		String source = moveParts[0];

		ReturnPlay curr = new ReturnPlay();
		curr.piecesOnBoard = startPieceMap; 
		if(moveParts.length == 1){
			if(source.equals("resign")){
				if(currPlayer == Player.white) curr.message = ReturnPlay.Message.RESIGN_BLACK_WINS;
				else curr.message = ReturnPlay.Message.RESIGN_WHITE_WINS;
			}else{ 
				curr.message = ReturnPlay.Message.ILLEGAL_MOVE;
			}
			return curr;
		}
		String promotionPiece = "";
		if ((moveParts.length == 3 || moveParts.length == 4) && !moveParts[2].equals("draw?")) promotionPiece = moveParts[2];
		boolean checkPromotion = false;
		
		String destination = moveParts[1];

		if ((source.charAt(0) < 'a' || source.charAt(0) > 'h') || (source.charAt(1) < '1' || source.charAt(1) > '8')){
			curr.message = ReturnPlay.Message.ILLEGAL_MOVE;
			return curr;
		}

		boolean foundPiece = false;
		Pawn isPassant = null;

		for (ReturnPiece piece : curr.piecesOnBoard){
			if (piece.pieceFile.name().equalsIgnoreCase(source.substring(0, 1)) &&
            piece.pieceRank == Integer.parseInt(source.substring(1)))  { 
				foundPiece = true;
				if (!piece.pieceType.name().substring(0, 1).equals(pieceColor.get(currPlayer))){
					curr.message = ReturnPlay.Message.ILLEGAL_MOVE;
					return curr;
				}
				
				if(piece.pieceType == ReturnPiece.PieceType.WP || piece.pieceType == ReturnPiece.PieceType.BP){
					//Create a Pawn object
					Pawn pawn = new Pawn(source, piece.pieceType.name().substring(0, 1), enpassant);
					isPassant = pawn;
					pawn.setActualPiece(piece);

					if(currPlayer == Chess.Player.white) enpassant[0] = false;
					else enpassant[1] = false;

					if (!pawn.legalMove(destination, startPieceMap)){
						curr.message = ReturnPlay.Message.ILLEGAL_MOVE;
						return curr;
					}
					
					if (pawn.isEligibleForPromotion(destination)){
						checkPromotion = true;
						if (promotionPiece.equals("N")) piece.pieceType = (pieceColor.get(currPlayer).equals("W")) ? ReturnPiece.PieceType.WN : ReturnPiece.PieceType.BN;
						else if (promotionPiece.equals("R")) piece.pieceType = (pieceColor.get(currPlayer).equals("W")) ? ReturnPiece.PieceType.WR : ReturnPiece.PieceType.BR;
						else if (promotionPiece.equals("B")) piece.pieceType = (pieceColor.get(currPlayer).equals("W")) ? ReturnPiece.PieceType.WB : ReturnPiece.PieceType.BB;
						else if (promotionPiece.equals("") || promotionPiece.equals("Q")) piece.pieceType = (pieceColor.get(currPlayer).equals("W")) ? ReturnPiece.PieceType.WQ : ReturnPiece.PieceType.BQ;
					}
				} else if(piece.pieceType == ReturnPiece.PieceType.WR || piece.pieceType == ReturnPiece.PieceType.BR){

					Rook rook = new Rook(source, piece.pieceType.name().substring(0, 1));
					if (!rook.legalMove(destination, startPieceMap)){
						curr.message = ReturnPlay.Message.ILLEGAL_MOVE;
						return curr;
					}
				} else if(piece.pieceType == ReturnPiece.PieceType.WN || piece.pieceType == ReturnPiece.PieceType.BN){
					Knight knight = new Knight(source, piece.pieceType.name().substring(0, 1));
					if (!knight.legalMove(destination, startPieceMap)){
						curr.message = ReturnPlay.Message.ILLEGAL_MOVE;
						return curr;
					}
				} else if(piece.pieceType == ReturnPiece.PieceType.WQ || piece.pieceType == ReturnPiece.PieceType.BQ){
					Queen queen = new Queen(source, piece.pieceType.name().substring(0, 1));
					if (!queen.legalMove(destination, startPieceMap)){
						curr.message = ReturnPlay.Message.ILLEGAL_MOVE;
						return curr;
					}
				} else if(piece.pieceType == ReturnPiece.PieceType.WB || piece.pieceType == ReturnPiece.PieceType.BB){
					Bishop bishop = new Bishop(source, piece.pieceType.name().substring(0, 1));
					if (!bishop.legalMove(destination, startPieceMap)){
						curr.message = ReturnPlay.Message.ILLEGAL_MOVE;
						return curr;
					}
				} else{
					King king = new King(source, piece.pieceType.name().substring(0, 1), piecesMoves.get(piece));
					King.piecesMoves = piecesMoves; 
					king.setRp(piece);
					if (!king.legalMove(destination, startPieceMap)){
						curr.message = ReturnPlay.Message.ILLEGAL_MOVE;
						return curr;
					}
					if(king.castleChecker() == true) break;
				}
				
				ReturnPiece pieceAtDestination = getPieceAtSquare(destination, startPieceMap);
				if (pieceAtDestination != null) startPieceMap.remove(pieceAtDestination); 
				
				piece.pieceFile = ReturnPiece.PieceFile.valueOf(destination.charAt(0) + "");
				piece.pieceRank = Integer.parseInt(destination.charAt(1) + "");

				ReturnPiece passantSaved = null; 
				if(Pawn.getActualPasssant() != null && isPassant != null && isPassant.isValidEnpassant()){
					for(ReturnPiece piecee: startPieceMap){ 
						if(piecee == Pawn.getActualPasssant()){
							passantSaved = piecee;
							break;
						}
					}
					startPieceMap.remove(passantSaved);
					Pawn.setActualPasssant(null);
				}
				
				if (isOwnKingInCheck(startPieceMap)){
					piece.pieceFile = ReturnPiece.PieceFile.valueOf(source.substring(0, 1));
					piece.pieceRank = Integer.parseInt(source.substring(1));
					if (checkPromotion) piece.pieceType = (pieceColor.get(currPlayer).equals("W")) ? ReturnPiece.PieceType.WP : ReturnPiece.PieceType.BP;
					if (pieceAtDestination != null) startPieceMap.add(pieceAtDestination);
					if(passantSaved != null){ 
						startPieceMap.add(passantSaved);
						Pawn.setActualPasssant(passantSaved);
					}
					curr.message = ReturnPlay.Message.ILLEGAL_MOVE;
					return curr;
				}

				if(Pawn.getActualPasssant() != null && isPassant != null && isPassant.isValidEnpassant()){
					startPieceMap.remove(Pawn.getActualPasssant());
					Pawn.setActualPasssant(null);
				}

				if (isOpponentKingInCheck(startPieceMap)) curr.message = ReturnPlay.Message.CHECK;
				
				piecesMoves.replace(piece, piecesMoves.get(piece) + 1);
				break;
        	}
		}

		if(!foundPiece){
			curr.message = ReturnPlay.Message.ILLEGAL_MOVE;
			return curr;
		}

		if((moveParts.length == 3 || moveParts.length == 4)){
			if (moveParts[2].equals("draw?") || (moveParts.length == 4 && moveParts[3].equals("draw?"))) {
				curr.message = ReturnPlay.Message.DRAW;
				return curr;
			}
		} 

		if (currPlayer == Player.white) currPlayer = Player.black;
		else currPlayer = Player.white;
		

		return curr;
		
	}
	
	public static void addQueens() {
		ReturnPiece whiteQueen = new ReturnPiece();
		whiteQueen.pieceType = ReturnPiece.PieceType.WQ;
		whiteQueen.pieceFile = ReturnPiece.PieceFile.values()[3];
		whiteQueen.pieceRank = 1;
		startPieceMap.add(whiteQueen);
		piecesMoves.put(whiteQueen, 0);

		ReturnPiece blackQueen = new ReturnPiece();
		blackQueen.pieceType = ReturnPiece.PieceType.BQ;
		blackQueen.pieceFile = ReturnPiece.PieceFile.values()[3];
		blackQueen.pieceRank = 8;
		startPieceMap.add(blackQueen);
		piecesMoves.put(blackQueen, 0);
	}


	public static void addKings() {
		ReturnPiece whiteKing = new ReturnPiece();
		whiteKing.pieceType = ReturnPiece.PieceType.WK;
		whiteKing.pieceFile = ReturnPiece.PieceFile.values()[4]; 
		whiteKing.pieceRank = 1;
		startPieceMap.add(whiteKing);
		piecesMoves.put(whiteKing, 0);

		ReturnPiece blackKing = new ReturnPiece();
		blackKing.pieceType = ReturnPiece.PieceType.BK;
		blackKing.pieceFile = ReturnPiece.PieceFile.values()[4];
		blackKing.pieceRank = 8;
		startPieceMap.add(blackKing);
		piecesMoves.put(blackKing, 0);
	}


	public static void addBishops() {
		for (int i = 0; i < 2; i++){
			ReturnPiece whiteBishop = new ReturnPiece();
			whiteBishop.pieceType = ReturnPiece.PieceType.WB;
			whiteBishop.pieceFile = ReturnPiece.PieceFile.values()[i == 0 ? 2 : 5]; 
			whiteBishop.pieceRank = 1;
			startPieceMap.add(whiteBishop);
			piecesMoves.put(whiteBishop, 0);

			ReturnPiece blackBishop = new ReturnPiece();
			blackBishop.pieceType = ReturnPiece.PieceType.BB;
			blackBishop.pieceFile = ReturnPiece.PieceFile.values()[i == 0 ? 2 : 5];
			blackBishop.pieceRank = 8;
			startPieceMap.add(blackBishop);
			piecesMoves.put(blackBishop, 0);
		}
	}

	public static void addKnights() {
		for (int i = 0; i < 2; i++){
			ReturnPiece whiteKnight = new ReturnPiece();
			whiteKnight.pieceType = ReturnPiece.PieceType.WN;
			whiteKnight.pieceFile = ReturnPiece.PieceFile.values()[i == 0 ? 1 : 6]; 
			whiteKnight.pieceRank = 1;
			startPieceMap.add(whiteKnight);
			piecesMoves.put(whiteKnight, 0);

			ReturnPiece blackKnight = new ReturnPiece();
			blackKnight.pieceType = ReturnPiece.PieceType.BN;
			blackKnight.pieceFile = ReturnPiece.PieceFile.values()[i == 0 ? 1 : 6];
			blackKnight.pieceRank = 8;
			startPieceMap.add(blackKnight);
			piecesMoves.put(blackKnight, 0);
		}
	}

	public static void addPawns(){
		for (int i = 0; i < 8; i++){
			ReturnPiece whitePawn = new ReturnPiece();
			whitePawn.pieceType = ReturnPiece.PieceType.WP;
			whitePawn.pieceFile = ReturnPiece.PieceFile.values()[i]; 
			whitePawn.pieceRank = 2;
			startPieceMap.add(whitePawn);
			piecesMoves.put(whitePawn, 0);

			ReturnPiece blackPawn = new ReturnPiece();
			blackPawn.pieceType = ReturnPiece.PieceType.BP;
			blackPawn.pieceFile = ReturnPiece.PieceFile.values()[i];
			blackPawn.pieceRank = 7;
			startPieceMap.add(blackPawn);
			piecesMoves.put(blackPawn, 0);
		}
	}

	public static void addRooks(){
		for (int i = 0; i < 2; i++){
			ReturnPiece whiteRook = new ReturnPiece();
			whiteRook.pieceType = ReturnPiece.PieceType.WR;
			whiteRook.pieceFile = ReturnPiece.PieceFile.values()[i == 0 ? 0 : 7]; 
			whiteRook.pieceRank = 1;
			startPieceMap.add(whiteRook);
			piecesMoves.put(whiteRook, 0);

			ReturnPiece blackRook = new ReturnPiece();
			blackRook.pieceType = ReturnPiece.PieceType.BR;
			blackRook.pieceFile = ReturnPiece.PieceFile.values()[i == 0 ? 0 : 7];
			blackRook.pieceRank = 8;
			startPieceMap.add(blackRook);
			piecesMoves.put(blackRook, 0);
		}
	}

	public static boolean isOwnKingInCheck(ArrayList<ReturnPiece> pieces){
		String ownKingDestination = "";
		for (ReturnPiece piece : pieces){
			if (piece.pieceType.name().substring(0, 1).equals(pieceColor.get(currPlayer)) && piece.pieceType.name().substring(1).equals("K")){
				ownKingDestination += piece.pieceFile.toString();
				ownKingDestination += Integer.toString(piece.pieceRank);
			}
		}

		if (pieceColor.get(currPlayer).equals("B")){
			for (ReturnPiece piece : pieces){
				String sourceOfPiece = "";
				sourceOfPiece += piece.pieceFile.toString();
				sourceOfPiece += Integer.toString(piece.pieceRank);
				if (piece.pieceType == ReturnPiece.PieceType.WN){
					Knight knight = new Knight(sourceOfPiece, piece.pieceType.name().substring(0, 1));
					if (knight.legalMove(ownKingDestination, pieces)) return true;
				}
				else if (piece.pieceType == ReturnPiece.PieceType.WB){
					Bishop bishop = new Bishop(sourceOfPiece, piece.pieceType.name().substring(0, 1));
					if (bishop.legalMove(ownKingDestination, pieces)) return true;
				}
				else if (piece.pieceType == ReturnPiece.PieceType.WR){
					Rook rook = new Rook(sourceOfPiece, piece.pieceType.name().substring(0, 1));
					if (rook.legalMove(ownKingDestination, pieces)) return true;
				}
				else if (piece.pieceType == ReturnPiece.PieceType.WP){
					Pawn pawn = new Pawn(sourceOfPiece, piece.pieceType.name().substring(0, 1), enpassant);
					if (pawn.legalMove(ownKingDestination, pieces)) return true;
				}
				else if (piece.pieceType == ReturnPiece.PieceType.WQ){
					Queen queen = new Queen(sourceOfPiece, piece.pieceType.name().substring(0, 1));
					if (queen.legalMove(ownKingDestination, pieces)) return true;
				}
				else if (piece.pieceType == ReturnPiece.PieceType.WK){
					King king = new King(sourceOfPiece, piece.pieceType.name().substring(0, 1), piecesMoves.get(piece));
					if (king.legalMove(ownKingDestination, pieces)) return true;
				}
			}
		}

		else{
			for (ReturnPiece piece : pieces){
				String sourceOfPiece = "";
				sourceOfPiece += piece.pieceFile.toString();
				sourceOfPiece += Integer.toString(piece.pieceRank);
				if (piece.pieceType == ReturnPiece.PieceType.BN){
					Knight knight = new Knight(sourceOfPiece, piece.pieceType.name().substring(0, 1));
					if (knight.legalMove(ownKingDestination, pieces)) return true;
				}
				else if (piece.pieceType == ReturnPiece.PieceType.BB){
					Bishop bishop = new Bishop(sourceOfPiece, piece.pieceType.name().substring(0, 1));
					if (bishop.legalMove(ownKingDestination, pieces)) return true;
				}
				else if (piece.pieceType == ReturnPiece.PieceType.BR){
					Rook rook = new Rook(sourceOfPiece, piece.pieceType.name().substring(0, 1));
					if (rook.legalMove(ownKingDestination, pieces)) return true;
				}
				else if (piece.pieceType == ReturnPiece.PieceType.BP){
					Pawn pawn = new Pawn(sourceOfPiece, piece.pieceType.name().substring(0, 1), enpassant);
					if (pawn.legalMove(ownKingDestination, pieces)) return true;
				}
				else if (piece.pieceType == ReturnPiece.PieceType.BQ){
					Queen queen = new Queen(sourceOfPiece, piece.pieceType.name().substring(0, 1));
					if (queen.legalMove(ownKingDestination, pieces)) return true;
				}
				else if (piece.pieceType == ReturnPiece.PieceType.BK){
					King king = new King(sourceOfPiece, piece.pieceType.name().substring(0, 1), piecesMoves.get(piece));
					if (king.legalMove(ownKingDestination, pieces)) return true;
				}
			}
		}
		return false;
	}

	public static boolean isOpponentKingInCheck(ArrayList<ReturnPiece> pieces){
		String opponentKingDestination = "";
		//traverse through board to find opponent's king's square
		for (ReturnPiece piece : pieces){
			if (!piece.pieceType.name().substring(0, 1).equals(pieceColor.get(currPlayer)) && piece.pieceType.name().substring(1).equals("K")){
				opponentKingDestination += piece.pieceFile.toString();
				opponentKingDestination += Integer.toString(piece.pieceRank);
			}
		}

		if (pieceColor.get(currPlayer).equals("W")){
			for (ReturnPiece piece : pieces){
				String sourceOfPiece = "";
				sourceOfPiece += piece.pieceFile.toString();
				sourceOfPiece += Integer.toString(piece.pieceRank);
				if (piece.pieceType == ReturnPiece.PieceType.WN){
					Knight knight = new Knight(sourceOfPiece, piece.pieceType.name().substring(0, 1));
					if (knight.legalMove(opponentKingDestination, pieces)) return true;
				}
				else if (piece.pieceType == ReturnPiece.PieceType.WB){
					Bishop bishop = new Bishop(sourceOfPiece, piece.pieceType.name().substring(0, 1));
					if (bishop.legalMove(opponentKingDestination, pieces)) return true;
				}
				else if (piece.pieceType == ReturnPiece.PieceType.WR){
					Rook rook = new Rook(sourceOfPiece, piece.pieceType.name().substring(0, 1));
					if (rook.legalMove(opponentKingDestination, pieces)) return true;
				}
				else if (piece.pieceType == ReturnPiece.PieceType.WP){
					Pawn pawn = new Pawn(sourceOfPiece, piece.pieceType.name().substring(0, 1), enpassant);
					if (pawn.legalMove(opponentKingDestination, pieces)) return true;
				}
				else if (piece.pieceType == ReturnPiece.PieceType.WQ){
					Queen queen = new Queen(sourceOfPiece, piece.pieceType.name().substring(0, 1));
					if (queen.legalMove(opponentKingDestination, pieces)) return true;
				}
				else if (piece.pieceType == ReturnPiece.PieceType.WK){
					King king = new King(sourceOfPiece, piece.pieceType.name().substring(0, 1), piecesMoves.get(piece));
					if (king.legalMove(opponentKingDestination, pieces)) return true;
				}
			}
		}

		else{
			for (ReturnPiece piece : pieces){
				String sourceOfPiece = "";
				sourceOfPiece += piece.pieceFile.toString();
				sourceOfPiece += Integer.toString(piece.pieceRank);
				if (piece.pieceType == ReturnPiece.PieceType.BN){
					Knight knight = new Knight(sourceOfPiece, piece.pieceType.name().substring(0, 1));
					if (knight.legalMove(opponentKingDestination, pieces)) return true;
				}
				else if (piece.pieceType == ReturnPiece.PieceType.BB){
					Bishop bishop = new Bishop(sourceOfPiece, piece.pieceType.name().substring(0, 1));
					if (bishop.legalMove(opponentKingDestination, pieces)) return true;
				}
				else if (piece.pieceType == ReturnPiece.PieceType.BR){
					Rook rook = new Rook(sourceOfPiece, piece.pieceType.name().substring(0, 1));
					if (rook.legalMove(opponentKingDestination, pieces)) return true;
				}
				else if (piece.pieceType == ReturnPiece.PieceType.BP){
					Pawn pawn = new Pawn(sourceOfPiece, piece.pieceType.name().substring(0, 1), enpassant);
					if (pawn.legalMove(opponentKingDestination, pieces)) return true;
				}
				else if (piece.pieceType == ReturnPiece.PieceType.BQ){
					Queen queen = new Queen(sourceOfPiece, piece.pieceType.name().substring(0, 1));
					if (queen.legalMove(opponentKingDestination, pieces)) return true;
				}
				else if (piece.pieceType == ReturnPiece.PieceType.BK){
					King king = new King(sourceOfPiece, piece.pieceType.name().substring(0, 1), piecesMoves.get(piece));
					if (king.legalMove(opponentKingDestination, pieces)) return true;
				}
			}
		}
		return false;
	}

	public static ReturnPiece getPieceAtSquare(String destination, ArrayList<ReturnPiece> pieces){
		for (ReturnPiece piece : pieces){
			if (piece.pieceFile.name().equalsIgnoreCase(destination.substring(0, 1)) &&
            piece.pieceRank == Integer.parseInt(destination.substring(1))) return piece;
		}
		return null;
	}

	public static void start() {
		
		startPieceMap = new ArrayList<>();
		piecesMoves = new HashMap<>();

		addPawns();
		addRooks();
		addKnights();
		addBishops();
		addKings();
		addQueens();

		currPlayer = Player.white;
		pieceColor = new HashMap<>();
		pieceColor.put(Player.white, "W");
		pieceColor.put(Player.black, "B");
		enpassant = new boolean[2]; 
	}

}
