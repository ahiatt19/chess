package ui;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import chess.ChessBoard;

import static ui.EscapeSequences.*;

public class ChessBoardUI {
    //Board Dimensions
    private static final int BOARD_SIZE_IN_SQUARES = 8;
    private static final int SQUARE_SIZE_IN_PADDED_CHARS = 1;
    //private static final int LINE_WIDTH_IN_PADDED_CHARS = 1;

    // Padded characters.
    private static final String EMPTY = " ";
    private static final String X = " X ";
    private static final String O = " O ";

    private static Random rand = new Random();

    public static void main(String[] args) {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        drawHeaderFooter(out);

        ChessBoard chessBoard = new ChessBoard();

        drawChessBoard(out, chessBoard);

        drawHeaderFooter(out);

        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_WHITE);
    }

    private static void drawHeaderFooter(PrintStream out) {

        setBlack(out);

        out.print(EMPTY.repeat(3));


        String[] headers = {" a ", " b ", " c ", " d ", " e ", " f ", " g ", " h "};
        for (int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES; ++boardCol) {
            drawHeaderFooter(out, headers[boardCol]);
        }
        out.println();
    }

    private static void drawHeaderFooter(PrintStream out, String headerText) {
        int prefixLength = SQUARE_SIZE_IN_PADDED_CHARS / 2;
        int suffixLength = SQUARE_SIZE_IN_PADDED_CHARS - prefixLength - 1;

        out.print(EMPTY.repeat(prefixLength));
        printHeaderText(out, headerText);
        out.print(EMPTY.repeat(suffixLength));
    }

    private static void printHeaderText(PrintStream out, String player) {
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_MAGENTA);
        out.print(SET_TEXT_BOLD);

        out.print(player);

        setBlack(out);
    }

    private static void printRow(PrintStream out, int row) {
        out.print(EMPTY);

        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_MAGENTA);
        out.print(SET_TEXT_BOLD);

        out.print(row);

        setBlack(out);
        out.print(EMPTY);
    }

    private static void drawChessBoard(PrintStream out, ChessBoard chessBoard) {

        for (int boardRow = 0; boardRow < BOARD_SIZE_IN_SQUARES; ++boardRow) {
            printRow(out, boardRow + 1);

            drawRowOfSquares(out);
            if (boardRow < BOARD_SIZE_IN_SQUARES - 1) {
                // Draw horizontal row separator
                setBlack(out);
            }
            printRow(out, boardRow + 1);
            out.println();
        }
    }

    private static void drawRowOfSquares(PrintStream out) {

        for (int squareRow = 0; squareRow < SQUARE_SIZE_IN_PADDED_CHARS; ++squareRow) {
            for (int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES; ++boardCol) {
                setWhite(out);

                if (squareRow == SQUARE_SIZE_IN_PADDED_CHARS / 2) {
                    int prefixLength = SQUARE_SIZE_IN_PADDED_CHARS / 2;
                    int suffixLength = SQUARE_SIZE_IN_PADDED_CHARS - prefixLength - 1;

                    out.print(EMPTY.repeat(prefixLength));
                    printPlayer(out, rand.nextBoolean() ? X : O);
                    out.print(EMPTY.repeat(suffixLength));
                }
                else {
                    out.print(EMPTY.repeat(SQUARE_SIZE_IN_PADDED_CHARS));
                }
                if (boardCol < BOARD_SIZE_IN_SQUARES - 1) {
                    // Draw vertical column separator.
                    setRed(out);
                    //out.print(EMPTY.repeat(LINE_WIDTH_IN_PADDED_CHARS));
                }
                setBlack(out);
            }

        }
    }

    private static void setWhite(PrintStream out) {
        out.print(SET_BG_COLOR_WHITE);
        out.print(SET_TEXT_COLOR_WHITE);
    }

    private static void setRed(PrintStream out) {
        out.print(SET_BG_COLOR_RED);
        out.print(SET_TEXT_COLOR_RED);
    }

    private static void setBlack(PrintStream out) {
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_BLACK);
    }

    private static void printPlayer(PrintStream out, String player) {
        out.print(SET_BG_COLOR_WHITE);
        out.print(SET_TEXT_COLOR_BLACK);

        out.print(player);

        setWhite(out);
    }
}
