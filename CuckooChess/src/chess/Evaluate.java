/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package chess;

/**
 * Position evaluation routines.
 * 
 * @author petero
 */
public class Evaluate {
    static final int[] pieceValue;
    static {
        // Initialize material table
        pieceValue = new int[Piece.nPieceTypes];
        pieceValue[Piece.WKING  ] =     0;
        pieceValue[Piece.WQUEEN ] =  1200;
        pieceValue[Piece.WROOK  ] =   600;
        pieceValue[Piece.WBISHOP] =   400;
        pieceValue[Piece.WKNIGHT] =   400;
        pieceValue[Piece.WPAWN  ] =   100;
        pieceValue[Piece.BKING  ] =     0;
        pieceValue[Piece.BQUEEN ] =  1200;
        pieceValue[Piece.BROOK  ] =   600;
        pieceValue[Piece.BBISHOP] =   400;
        pieceValue[Piece.BKNIGHT] =   400;
        pieceValue[Piece.BPAWN  ] =   100;
        pieceValue[Piece.EMPTY  ] =     0;
    }
    
    /** Piece/square table for king during middle game. */
    static final int[][] kt1 = { {-22,-35,-40,-40,-40,-40,-35,-22 },
		                 {-22,-35,-40,-40,-40,-40,-35,-22 },
		                 {-25,-35,-40,-45,-45,-40,-35,-25 },
		                 {-15,-30,-35,-40,-40,-35,-30,-15 },
		                 {-10,-15,-20,-25,-25,-20,-15,-10 },
		                 {  8,  6, -5,-15,-15, -5,  6,  8 },
		                 { 16, 14, 12,  7,  7, 12, 14, 16 },
		                 { 24, 24, 14, 10, 10, 14, 24, 24 } };
    
    /** Piece/square table for king during end game. */
    static final int[][] kt2 = { {  0,  8, 16, 24, 24, 16,  8,  0 },
				 {  8, 16, 24, 32, 32, 24, 16,  8 },
				 { 16, 24, 32, 40, 40, 32, 24, 16 },
				 { 24, 32, 40, 48, 48, 40, 32, 24},
				 { 24, 32, 40, 48, 48, 40, 32, 24},
				 { 16, 24, 32, 40, 40, 32, 24, 16},
				 {  8, 16, 24, 32, 32, 24, 16,  8 },
				 {  0,  8, 16, 24, 24, 16,  8,  0 } };

    /** Piece/square table for pawns during middle game. */
    static final int[][] pt1 = { {  0,  0,  0,  0,  0,  0,  0,  0 },
				 { 16, 32, 48, 64, 64, 48, 32, 16 },
				 {  6, 24, 40, 56, 56, 40, 24,  6 },
				 {-10,  8, 20, 40, 40, 20,  8,-10 },
				 {-12,  8, 10, 32, 32, 10,  8,-12 },
				 {-12,  8,  4, 10, 10,  4,  8,-12 },
				 {-12,  8,  8, -7, -7,  8,  8,-12 },
				 {  0,  0,  0,  0,  0,  0,  0,  0 } };

    /** Piece/square table for pawns during end game. */
    static final int[][] pt2 = { {  0,  0,  0,  0,  0,  0,  0,  0 },
				 { 50, 80, 90, 90, 90, 90, 80, 50 },
				 { 34, 64, 69, 69, 69, 69, 64, 34 },
				 { 10, 48, 48, 48, 48, 48, 48, 10 },
				 {-18, 22, 22, 22, 22, 22, 22,-18 },
				 {-34,  6,  6,  6,  6,  6,  6,-34 },
				 {-40,  0,  0,  0,  0,  0,  0,-40 },
				 {  0,  0,  0,  0,  0,  0,  0,  0 } };

    /** Piece/square table for knights during middle game. */
    static final int[][] nt1 = { {-50,-40,-30,-20,-20,-30,-40,-50 },
				 {-40,-30,-10,  0,  0,-10,-30,-40 },
				 {-20,  5, 10, 15, 15, 10,  5,-20 },
				 {-17,  0, 10, 20, 20, 10,  0,-17 },
				 {-17,  0,  3, 20, 20,  3,  0,-17 },
				 {-20,-10,  0,  5,  5,  0,-10,-20 },
				 {-40,-30,-10,  0,  0,-10,-30,-40 },
				 {-50,-40,-30,-20,-20,-30,-40,-50 } };

    /** Piece/square table for knights during end game. */
    static final int[][] nt2 = { {-50,-40,-30,-20,-20,-30,-40,-50 },
				 {-40,-30,-10, -5, -5,-10,-30,-40 },
				 {-30,-10,  0, 10, 10,  0,-10,-30 },
				 {-20, -5, 10, 20, 20, 10, -5,-20 },
				 {-20, -5, 10, 20, 20, 10, -5,-20 },
				 {-30,-10,  0, 10, 10,  0,-10,-30 },
				 {-40,-30,-10, -5, -5,-10,-30,-40 },
				 {-50,-40,-30,-20,-20,-30,-40,-50 } };

    /** Piece/square table for bishops during middle game. */
    static final int[][] bt1 = { {  0,  0,  0,  0,  0,  0,  0,  0 },
				 {  0,  8,  4,  4,  4,  4,  8,  0 },
				 {  0,  4,  8,  8,  8,  8,  4,  0 },
				 {  0,  4,  8,  8,  8,  8,  4,  0 },
				 {  0,  4,  8,  8,  8,  8,  4,  0 },
				 {  0,  6,  8,  8,  8,  8,  6,  0 },
				 {  0,  8,  4,  4,  4,  4,  8,  0 },
				 {  0,  0,  0,  0,  0,  0,  0,  0 } };

    /** Piece/square table for queens during middle game. */
    static final int[][] qt1 = { {-10, -5,  0,  0,  0,  0, -5,-10 },
				 { -5,  0,  5,  5,  5,  5,  0, -5 },
				 {  0,  5,  5,  6,  6,  5,  5,  0 },
				 {  0,  5,  6,  6,  6,  6,  5,  0 },
				 {  0,  5,  6,  6,  6,  6,  5,  0 },
				 {  0,  5,  5,  6,  6,  5,  5,  0 },
				 { -5,  0,  5,  5,  5,  5,  0, -5 },
				 {-10, -5,  0,  0,  0,  0, -5,-10 } };

    /** Piece/square table for queens during middle game. */
    static final int[][] rt1 = { {  0,  1,  2,  2,  2,  2,  1,  0 },
				 { 10, 15, 15, 15, 15, 15, 15, 10 },
				 {  0,  0,  0,  0,  0,  0,  0,  0 },
				 {  0,  0,  0,  0,  0,  0,  0,  0 },
				 { -1,  0,  0,  0,  0,  0,  0, -1 },
				 { -2,  0,  0,  0,  0,  0,  0, -2 },
				 { -3,  2,  5,  5,  5,  5,  2, -3 },
				 {  0,  3,  5,  5,  5,  5,  3,  0 } };
    
    static final int[][] distToH1A8 = { { 0, 1, 2, 3, 4, 5, 6, 7 },
					{ 1, 2, 3, 4, 5, 6, 7, 6 },
					{ 2, 3, 4, 5, 6, 7, 6, 5 },
					{ 3, 4, 5, 6, 7, 6, 5, 4 },
					{ 4, 5, 6, 7, 6, 5, 4, 3 },
					{ 5, 6, 7, 6, 5, 4, 3, 2 },
					{ 6, 7, 6, 5, 4, 3, 2, 1 },
					{ 7, 6, 5, 4, 3, 2, 1, 0 } };

    
    /** nPawns[0/1][file] contains the number of white/black pawns on a file. */
    int [][] nPawns;
    
    /**
     * firstPawn[0/1][file] contains the rank of the first (least advanced) pawn for a color/file.
     * If there is no pawn, the value is set to 7/0, ie the promotion row for pawns of that color.
     */
    int [][] firstPawn;

    // Cached variables used by evalPos and the functions it calls.
    int wMtrl;      // Total value of all white pieces and pawns
    int bMtrl;      // Total value of all black pieces and pawns
    int wMtrlPawns; // Total value of all white pawns
    int bMtrlPawns; // Total value of all black pawns
    
    /** Constructor. */
    public Evaluate() {
        nPawns = new int[2][8];
        firstPawn = new int[2][8];
    }

    // FIXME!!! Optimize speed. Fewer loops over all squares.
    /**
     * Static evaluation of a position.
     * @param pos The position to evaluate.
     * @return The evaluation score, measured in centipawns.
     *         Positive values are good for the side to make the next move.
     */
    final int evalPos(Position pos) {
        computeMaterial(pos);
        int score = wMtrl - bMtrl;

        score += pieceSquareEval(pos);
        score += tradeBonus(pos);
        score += castleBonus(pos);

        score += computePawnStructure(pos);
        score += rookBonus(pos);
        score += kingSafety(pos);
        score += bishopEval(pos, score);
        score = endGameEval(pos, score);

        if (!pos.isWhiteMove())
            score = -score;
        return score;
    }

    /** Compute white_material - black_material. */
    final int material(Position pos) {
        computeMaterial(pos);
        return wMtrl - bMtrl;
    }
    
    final int material(Position pos, boolean white) {
        computeMaterial(pos);
        return white ? wMtrl : bMtrl;
    }

    private final void computeMaterial(Position pos) {
        int wMtrl = 0;
        int bMtrl = 0;
        final int pV = pieceValue[Piece.WPAWN];
        int wMtrlPawns = 0;
        int bMtrlPawns = 0;
        for (int sq = 0; sq < 64; sq++) {
            int p = pos.getPiece(sq);
            if (p == Piece.EMPTY) continue;
            if (Piece.isWhite(p)) {
                wMtrl += pieceValue[p];
                if (p == Piece.WPAWN) {
                    wMtrlPawns += pV;
                }
            } else {
                bMtrl += pieceValue[p];
                if (p == Piece.BPAWN) {
                    bMtrlPawns += pV;
                }
            }
        }
        this.wMtrl = wMtrl;
        this.bMtrl = bMtrl;
        this.wMtrlPawns = wMtrlPawns;
        this.bMtrlPawns = bMtrlPawns;
    }


    /** Compute score based on piece square tables. Positive values are good for white. */
    private final int pieceSquareEval(Position pos) {
        int score = 0;
        final int qV = pieceValue[Piece.WQUEEN];
        final int rV = pieceValue[Piece.WROOK];
        final int bV = pieceValue[Piece.WBISHOP];
        final int nV = pieceValue[Piece.WKNIGHT];
        final int pV = pieceValue[Piece.WPAWN];

        for (int x = 0; x < 8; x++) {
            for (int i = 0; i < 2; i++) {
                nPawns[i][x] = 0;
            }
            firstPawn[0][x] = 7;
            firstPawn[1][x] = 0;
        }
        for (int sq = 0; sq < 64; sq++) {
            int p = pos.getPiece(sq);
            if (p == Piece.EMPTY) continue;
            final int x = Position.getX(sq);
            final int y = Position.getY(sq);
            switch (p) {
                case Piece.WKING:
		{
		    final int k1 = kt1[7-y][x];
		    final int k2 = kt2[7-y][x];
		    final int t1 = qV + 2 * rV + 2 * bV;
		    final int t2 = rV;
		    final int t = bMtrl - bMtrlPawns;
		    final int s = interpolate(t, t2, k2, t1, k1);
		    score += s;
		    break;
		}
                case Piece.BKING:
		{
		    final int k1 = kt1[y][x];
		    final int k2 = kt2[y][x];
		    final int t1 = qV + 2 * rV + 2 * bV;
		    final int t2 = rV;
		    final int t = wMtrl - wMtrlPawns;
		    final int s = interpolate(t, t2, k2, t1, k1);
		    score -= s;
		    break;
		}
                case Piece.WPAWN:
		{
		    final int p1 = pt1[7-y][x];
		    final int p2 = pt2[7-y][x];
		    final int t1 = qV + 2 * rV + 2 * bV;
		    final int t2 = rV;
		    final int t = bMtrl - bMtrlPawns;
		    final int s = interpolate(t, t2, p2, t1, p1) / 2;
		    score += s;
		    nPawns[0][x]++;
		    firstPawn[0][x] = Math.min(firstPawn[0][x], y);
		    break;
		}
                case Piece.BPAWN:
		{
		    final int p1 = pt1[y][x];
		    final int p2 = pt2[y][x];
		    final int t1 = qV + 2 * rV + 2 * bV;
		    final int t2 = rV;
		    final int t = wMtrl - wMtrlPawns;
		    final int s = interpolate(t, t2, p2, t1, p1) / 2;
		    score -= s;
		    nPawns[1][x]++;
		    firstPawn[1][x] = Math.max(firstPawn[1][x], y);
		    break;
		}
                case Piece.WKNIGHT:
		{
		    final int n1 = nt1[7-y][x];
		    final int n2 = nt2[7-y][x];
		    final int t1 = qV + 2 * rV + 1 * bV + 1 * nV + 6 * pV;
		    final int t2 = nV + 8 * pV;
		    final int t = bMtrl;
		    final int s = interpolate(t, t2, n2, t1, n1);
		    score += s;
		    break;
		}
                case Piece.BKNIGHT:
		{
		    final int n1 = nt1[y][x];
		    final int n2 = nt2[y][x];
		    final int t1 = qV + 2 * rV + 1 * bV + 1 * nV + 6 * pV;
		    final int t2 = nV + 8 * pV;
		    final int t = wMtrl;
		    final int s = interpolate(t, t2, n2, t1, n1);
		    score -= s;
		    break;
		}
                case Piece.WBISHOP:
		{
		    score += bt1[7-y][x];
		    break;
		}
                case Piece.BBISHOP:
		{
		    score -= bt1[y][x];
		    break;
		}
                case Piece.WQUEEN:
		{
		    score += qt1[7-y][x];
		    score += rookMobility(pos, x, y);
		    score += bishopMobility(pos, x, y);
		    break;
		}
                case Piece.BQUEEN:
		{
		    score -= qt1[y][x];
		    score -= rookMobility(pos, x, y);
		    score -= bishopMobility(pos, x, y);
		    break;
		}
                case Piece.WROOK:
		{
		    final int r1 = rt1[7-y][x];
		    final int nP = bMtrlPawns / pV;
		    final int s = r1 * Math.min(nP, 6) / 6;
		    score += s;
		    break;
		}
                case Piece.BROOK:
		{
		    final int r1 = rt1[y][x];
		    final int nP = wMtrlPawns / pV;
		    final int s = r1 * Math.min(nP, 6) / 6;
		    score -= s;
		    break;
		}
	    }
        }
        return score;
    }

    /** Implement the "when ahead trade pieces, when behind trade pawns" rule. */
    private final int tradeBonus(Position pos) {
        final int pV = pieceValue[Piece.WPAWN];
        final int qV = pieceValue[Piece.WQUEEN];
        final int rV = pieceValue[Piece.WROOK];
        final int bV = pieceValue[Piece.WBISHOP];
        final int nV = pieceValue[Piece.WKNIGHT];

        final int wM = wMtrl;
        final int bM = bMtrl;
        final int wPawn = wMtrlPawns;
        final int bPawn = bMtrlPawns;
        final int deltaScore = wM - bM;

        int pBonus = 0;
        pBonus += interpolate((deltaScore > 0) ? wPawn : bPawn, 0, -30 * deltaScore / 100, 6 * pV, 0);
        pBonus += interpolate((deltaScore > 0) ? bM : wM, 0, 30 * deltaScore / 100, qV + 2 * rV + 2 * bV + 2 * nV, 0);

        return pBonus;
    }

    /** Score castling ability. */
    final int castleBonus(Position pos) {
        final int qV = pieceValue[Piece.WQUEEN];
        final int rV = pieceValue[Piece.WROOK];
        final int bV = pieceValue[Piece.WBISHOP];

        final int k1 = kt1[7-0][6] - kt1[7-0][4];
        final int k2 = kt2[7-0][6] - kt2[7-0][4];
        final int t1 = qV + 2 * rV + 2 * bV;
        final int t2 = rV;
        final int t = bMtrl - bMtrlPawns;
        final int ks = interpolate(t, t2, k2, t1, k1);

        final int castleValue = ks + rt1[7-0][5] - rt1[7-0][7];
        if (castleValue <= 0) {
            return 0;
        }
        int h1Dist = 100;
        if (pos.h1Castle()) {
            h1Dist = 2;
            if (pos.getPiece(Position.getSquare(5, 0)) != Piece.EMPTY) h1Dist++;
            if (pos.getPiece(Position.getSquare(6, 0)) != Piece.EMPTY) h1Dist++;
        }
        int a1Dist = 100;
        if (pos.a1Castle()) {
            a1Dist = 2;
            if (pos.getPiece(Position.getSquare(3, 0)) != Piece.EMPTY) a1Dist++;
            if (pos.getPiece(Position.getSquare(2, 0)) != Piece.EMPTY) a1Dist++;
            if (pos.getPiece(Position.getSquare(1, 0)) != Piece.EMPTY) a1Dist++;
        }
        final int wBonus = castleValue / Math.min(a1Dist, h1Dist);

        int h8Dist = 100;
        if (pos.h8Castle()) {
            h8Dist = 2;
            if (pos.getPiece(Position.getSquare(5, 7)) != Piece.EMPTY) h8Dist++;
            if (pos.getPiece(Position.getSquare(6, 7)) != Piece.EMPTY) h8Dist++;
        }
        int a8Dist = 100;
        if (pos.a8Castle()) {
            a8Dist = 2;
            if (pos.getPiece(Position.getSquare(3, 7)) != Piece.EMPTY) a8Dist++;
            if (pos.getPiece(Position.getSquare(2, 7)) != Piece.EMPTY) a8Dist++;
            if (pos.getPiece(Position.getSquare(1, 7)) != Piece.EMPTY) a8Dist++;
        }
        final int bBonus = castleValue / Math.min(a8Dist, h8Dist);

        return wBonus - bBonus;
    }

    /** Compute nPawns[][] corresponding to pos. */
    final int computePawnStructure(Position pos) {
        int score = 0;

        // Evaluate double pawns
        int wDouble = 0;
        int bDouble = 0;
        for (int x = 0; x < 8; x++) {
            if (nPawns[0][x] > 1) {
                wDouble += nPawns[0][x] - 1;
            }
            if (nPawns[1][x] > 1) {
                bDouble += nPawns[1][x] - 1;
            }
        }
        score -= (wDouble - bDouble) * 20;

        // Evaluate pawn islands
        int wIslands = 0;
        int bIslands = 0;
        boolean wasPawn = false;
        for (int x = 0; x < 8; x++) {
            if (nPawns[0][x] > 0) {
                if (!wasPawn) {
                    wIslands++;
                    wasPawn = true;
                }
            } else {
                wasPawn = false;
            }
        }
        wasPawn = false;
        for (int x = 0; x < 8; x++) {
            if (nPawns[1][x] > 0) {
                if (!wasPawn) {
                    bIslands++;
                    wasPawn = true;
                }
            } else {
                wasPawn = false;
            }
        }
        score -= (wIslands - bIslands) * 15;
        
        // Evaluate passed pawn bonus
        int passedBonusW = 0;
        int passedBonusB = 0;
        for (int x = 0; x < 8; x++) {
            for (int y = 6; y > 0; y--) {
                if (pos.getPiece(Position.getSquare(x, y)) == Piece.WPAWN) {
                    boolean passed = true;
                    if ((x > 0) && (firstPawn[1][x - 1] >= y + 1)) passed = false;
                    if (           (firstPawn[1][x + 0] >= y + 1)) passed = false;
                    if ((x < 7) && (firstPawn[1][x + 1] >= y + 1)) passed = false;
                    if (passed) {
                        passedBonusW += 20 + y * 4;
                        if ((x > 0) && (pos.getPiece(Position.getSquare(x - 1, y - 1)) == Piece.WPAWN) ||
                            (x < 7) && (pos.getPiece(Position.getSquare(x + 1, y - 1)) == Piece.WPAWN)) {
                            passedBonusW += 15;  // Guarded passed pawn
                        }
                    }
                    break;
                }
            }
            for (int y = 1; y < 7; y++) {
                if (pos.getPiece(Position.getSquare(x, y)) == Piece.BPAWN) {
                    boolean passed = true;
                    if ((x > 0) && (firstPawn[0][x - 1] <= y - 1)) passed = false;
                    if (           (firstPawn[0][x + 0] <= y - 1)) passed = false;
                    if ((x < 7) && (firstPawn[0][x + 1] <= y - 1)) passed = false;
                    if (passed) {
                        passedBonusB += 20 + (7-y) * 4;
                        if ((x > 0) && (pos.getPiece(Position.getSquare(x - 1, y + 1)) == Piece.BPAWN) ||
                            (x < 7) && (pos.getPiece(Position.getSquare(x + 1, y + 1)) == Piece.BPAWN)) {
                            passedBonusB += 15;  // Guarded passed pawn
                        }
                    }
                    break;
                }
            }
        }

        final int qV = pieceValue[Piece.WQUEEN];
        final int rV = pieceValue[Piece.WROOK];
        final int hiMtrl = qV + rV;
        score += interpolate(bMtrl - bMtrlPawns, 0, 2 * passedBonusW, hiMtrl, passedBonusW);
        score -= interpolate(wMtrl - wMtrlPawns, 0, 2 * passedBonusB, hiMtrl, passedBonusB);
        
        return score;
    }
    
    /** Compute rook bonus. Rook on open/half-open file. */
    final int rookBonus(Position pos) {
        int score = 0;
        for (int sq = 0; sq < 64; sq++) {
            int p = pos.getPiece(sq);
            if (p == Piece.EMPTY) continue;
            switch (p) {
                case Piece.WROOK:
                {
                    final int x = Position.getX(sq);
                    final int y = Position.getY(sq);
                    if (nPawns[0][x] == 0) { // At least half-open file
                        score += nPawns[1][x] == 0 ? 25 : 12;
                    }
                    score += rookMobility(pos, x, y) / 2;
                    break;
                }
                case Piece.BROOK:
                {
                    final int x = Position.getX(sq);
                    final int y = Position.getY(sq);
                    if (nPawns[1][x] == 0) {
                        score -= nPawns[0][x] == 0 ? 25 : 12;
                    }
                    score -= rookMobility(pos, x, y) / 2;
                    break;
                }
            }
        }
        return score;
    }

    /** Compute king safety for both kings. */
    private final int kingSafety(Position pos) {
        final int qV = pieceValue[Piece.WQUEEN];
        final int rV = pieceValue[Piece.WROOK];
        final int bV = pieceValue[Piece.WBISHOP];
        final int nV = pieceValue[Piece.WKNIGHT];
        final int pV = pieceValue[Piece.WPAWN];
        final int maxM = qV + 2 * rV + 2 * bV + 2 * nV;
        final int minM = rV + bV;
        int score = 0;
        for (int i = 0; i < 2; i++) {
            boolean white = (i == 0);
            final int m = white ? wMtrl - wMtrlPawns : bMtrl - bMtrlPawns;
            if (m <= minM)
                continue;
            int kSq = pos.getKingSq(white);
            int xk = Position.getX(kSq);
            int yk = Position.getY(kSq);
            int safety = 0;
            int halfOpenFiles = 0;
            int yb = white ? 0 : 7;     // king home rank
            int yd = white ? 1 : -1;    // pawn direction
            if (white ? (yk < 2) : (yk >= 6)) {
                int ownPawn = white ? Piece.WPAWN : Piece.BPAWN;
                int otherPawn = white ? Piece.BPAWN : Piece.WPAWN;
                for (int x = xk - 1; x <= xk + 1; x++) {
                    if ((x > 0) && (x < 8)) {
                        safety += pos.getPiece(Position.getSquare(x, yb + 1 * yd)) == ownPawn ? 2 : 0;
                        safety += pos.getPiece(Position.getSquare(x, yb + 2 * yd)) == ownPawn ? 1 : 0;
                        safety -= pos.getPiece(Position.getSquare(x, yb + 1 * yd)) == otherPawn ? 2 : 0;
                        safety -= pos.getPiece(Position.getSquare(x, yb + 2 * yd)) == otherPawn ? 2 : 0;
                        safety -= pos.getPiece(Position.getSquare(x, yb + 3 * yd)) == otherPawn ? 1 : 0;
                        if (nPawns[1-i][x] == 0) halfOpenFiles++;
                    }
                }
                safety = Math.min(safety, 6);
            }
            final int kSafety = interpolate(m, minM, 0, maxM, (safety - 6) * 15 - halfOpenFiles * 20);
            if (white) {
                score += kSafety;
            } else {
                score -= kSafety;
            }
        }
        return score;
    }

    /** Compute bishop evaluation. */
    private final int bishopEval(Position pos, int oldScore) {
        int score = 0;
        boolean whiteDark = false;
        boolean whiteLight = false;
        boolean blackDark = false;
        boolean blackLight = false;
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                int p = pos.getPiece(Position.getSquare(x, y));
                switch (p) {
                    case Piece.WBISHOP:
                        if (Position.darkSquare(x, y))
                            whiteDark = true;
                        else
                            whiteLight = true;
                        score += bishopMobility(pos, x, y) * 2;
                        break;
                    case Piece.BBISHOP:
                        if (Position.darkSquare(x, y))
                            blackDark = true;
                        else
                            blackLight = true;
                        score -= bishopMobility(pos, x, y) * 2;
                        break;
                }
            }
        }
        int numWhite = (whiteDark ? 1 : 0) + (whiteLight ? 1 : 0);
        int numBlack = (blackDark ? 1 : 0) + (blackLight ? 1 : 0);
        
        // Bishop pair bonus
        final int pV = pieceValue[Piece.WPAWN];
        if (numWhite == 2) {
            final int numPawns = wMtrlPawns / pV;
            score += 20 + (8 - numPawns) * 2;
        }
        if (numBlack == 2) {
            final int numPawns = bMtrlPawns / pV;
            score -= 20 + (8 - numPawns) * 2;
        }

        // FIXME!!! Bad bishop

        if ((numWhite == 1) && (numBlack == 1) && (whiteDark != blackDark)) {
            final int penalty = (oldScore + score) / 2;
            final int qV = pieceValue[Piece.WQUEEN];
            final int rV = pieceValue[Piece.WROOK];
            final int bV = pieceValue[Piece.WBISHOP];
            final int loMtrl = 2 * bV;
            final int hiMtrl = 2 * (qV + rV + bV);
            int mtrl = wMtrl + bMtrl - wMtrlPawns - bMtrlPawns;
            score -= interpolate(mtrl, loMtrl, penalty, hiMtrl, 0);
        }

        return score;
    }
    
    /** Count the number of pseudo-legal moves for a bishop of given color on square (x0,y0). */
    final int bishopMobility(Position pos, int x0, int y0) {
        int mobility = 0;
        mobility += dirMobility(pos, x0, y0, -1, -1);
        mobility += dirMobility(pos, x0, y0, -1,  1);
        mobility += dirMobility(pos, x0, y0,  1, -1);
        mobility += dirMobility(pos, x0, y0,  1,  1);
        return mobility;
    }

    /** Count the number of pseudo-legal moves for a rook of given color on square (x0,y0). */
    final int rookMobility(Position pos, int x0, int y0) {
        int mobility = 0;
        mobility += dirMobility(pos, x0, y0,  0, -1);
        mobility += dirMobility(pos, x0, y0,  0,  1);
        mobility += dirMobility(pos, x0, y0, -1,  0);
        mobility += dirMobility(pos, x0, y0,  1,  0);
        return mobility;
    }

    private int dirMobility(Position pos, int x0, int y0, int dx, int dy) {
        int mobility = 0;
        int x = x0 + dx;
        int y = y0 + dy;
        while ((x >= 0) && (x < 8) && (y >= 0) && (y < 8)) {
            int p = pos.getPiece(Position.getSquare(x, y));
            if (p == Piece.EMPTY) {
                mobility++;
            } else {
                break;
            }
            x += dx;
            y += dy;
        }
        return mobility;
    }

    /** Implements special knowledge for some endgame situations. */
    private final int endGameEval(Position pos, int oldScore) {
        int score = oldScore;
        final int pV = pieceValue[Piece.WPAWN];
        final int rV = pieceValue[Piece.WROOK];
        final int bV = pieceValue[Piece.WBISHOP];
        final int nV = pieceValue[Piece.WKNIGHT];
        final int wMtrlNoPawns = this.wMtrl - wMtrlPawns;
        final int bMtrlNoPawns = this.bMtrl - bMtrlPawns;

        boolean handled = false;
        if ((wMtrlPawns + bMtrlPawns == 0) && (wMtrlNoPawns < rV) && (bMtrlNoPawns < rV)) {
            // King + minor piece vs king + minor piece is a draw
            score /= 50;
            handled = true;
        }
        if (!handled) {
            if (bMtrlPawns == 0) {
                if (wMtrlNoPawns - bMtrlNoPawns > bV) {
                    int wKnights = pos.nPieces(Piece.WKNIGHT);
                    int wBishops = pos.nPieces(Piece.WBISHOP);
                    if ((wKnights == 2) && (wMtrlNoPawns == 2 * nV) && (bMtrlNoPawns == 0)) {
                        score /= 50;    // KNNK is a draw
                    } else if ((wKnights == 1) && (wBishops == 1) && (wMtrlNoPawns == nV + bV) && (bMtrlNoPawns == 0)) {
                        score /= 10;
                        score += nV + bV + 300;
                        final int kSq = pos.getKingSq(false);
                        final int x = Position.getX(kSq);
                        final int y = Position.getY(kSq);
                        if (bishopOnDark(pos)) {
                            score += (7 - distToH1A8[7-y][7-x]) * 10;
                        } else {
                            score += (7 - distToH1A8[7-y][x]) * 10;
                        }
                    } else {
                        score += 300;       // Enough excess material, should win
                    }
                    handled = true;
                }
            }
        }
        if (!handled) {
            if ((score > 0) && (wMtrlPawns == 0) && (wMtrlNoPawns <= bMtrlNoPawns + bV)) {
                score /= 8;         // Too little excess material, probably draw
                handled = true;
            }
        }
        if (!handled) {
            if (wMtrlPawns == 0) {
                if (bMtrlNoPawns - wMtrlNoPawns > bV) {
                    int bKnights = pos.nPieces(Piece.BKNIGHT);
                    int bBishops = pos.nPieces(Piece.BBISHOP);
                    if ((bKnights == 2) && (bMtrlNoPawns == 2 * nV) && (wMtrlNoPawns == 0)) {
                        score /= 50;    // KNNK is a draw
                    } else if ((bKnights == 1) && (bBishops == 1) && (bMtrlNoPawns == nV + bV) && (wMtrlNoPawns == 0)) {
                        score /= 10;
                        score -= nV + bV + 300;
                        final int kSq = pos.getKingSq(true);
                        final int x = Position.getX(kSq);
                        final int y = Position.getY(kSq);
                        if (bishopOnDark(pos)) {
                            score -= (7 - distToH1A8[7-y][7-x]) * 10;
                        } else {
                            score -= (7 - distToH1A8[7-y][x]) * 10;
                        }
                    } else {
                        score -= 300;       // Enough excess material, should win
                    }
                    handled = true;
                }
            }
        }
        if (!handled) {
            if ((score < 0) && (bMtrlPawns == 0) && (bMtrlNoPawns <= wMtrlNoPawns + bV)) {
                score /= 8;         // Too little excess material, probably draw
                handled = true;
            }
        }

        // FIXME!!! Implement end game knowledge or EGTB for kpk
        // FIXME!!! Bishop + a|h pawn is draw if bad bishop and other king controls promotion square
        return score;
    }

    /**
     * Decide if there is a bishop on a white square.
     * Note that this method assumes that there is at most one bishop.
     */
    private static final boolean bishopOnDark(Position pos) {
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                int p = pos.getPiece(Position.getSquare(x, y));
                if ((p == Piece.WBISHOP) || (p == Piece.BBISHOP))
                    return Position.darkSquare(x, y);
            }
        }
        return false;
    }

    /**
     * Interpolate between (x1,y1) and (x2,y2).
     * If x < x1, return y1, if x > x2 return y2. Otherwise, use linear interpolation.
     */
    static final int interpolate(int x, int x1, int y1, int x2, int y2) {
        if (x < x1) {
            return y1;
        } else if (x > x2) {
            return y2;
        } else {
            return (x - x1) * (y2 - y1) / (x2 - x1) + y1;
        }
    }
}
