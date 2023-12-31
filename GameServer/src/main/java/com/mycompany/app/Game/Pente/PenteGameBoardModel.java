package com.mycompany.app.Game.Pente;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

public class PenteGameBoardModel {
    private PenteBoardIdentifierEnum[][] gameBoard;
    private int cols = 19;
    private int rows = 19;
    public PenteGameBoardModel() {
        gameBoard = new PenteBoardIdentifierEnum[this.rows][this.cols];
        for (int i = 0; i < this.rows; i++) {
            for (int j = 0; j < this.cols; j++) {
                gameBoard[i][j] = PenteBoardIdentifierEnum.EMPTY;
            }
        }
    }

    public void setMove(PenteTurn turn) throws Exception{
        try {
            this.checkMove(turn);
        } catch (Exception e) {
            throw e;
        }
        this.gameBoard[turn.posY][turn.posX] = turn.playerNumber;
    }

    // we should rename isTurnOneAction or create an isTurnTwoAction variable
    // we should probably only allow odd numbers of rows/columns with the pro
    // ruleset, or i can add logic to support even numbers of rows/cols but that
    // would get very complicated without an isTurnTwoAction boolean
    private void checkMove(PenteTurn turn) throws InvalidTurnException {
        if (turn.posX >= this.cols || turn.posY >= this.rows || turn.posX < 0 || turn.posY < 0) {
            throw new InvalidTurnException("Location out of bounds.");
        }
        if (gameBoard[turn.posY][turn.posX] != PenteBoardIdentifierEnum.EMPTY) {
            throw new InvalidTurnException("Location already occupied.");
        }
        if (!checkProSpecialRules(turn)) {
            throw new InvalidTurnException("Location does not conform to Pente pro ruleset.");
        }
        if (turn.playerNumber == PenteBoardIdentifierEnum.EMPTY) {
            throw new InvalidTurnException("Player number cannot be set to EMPTY.");
        }
    }

    // this method checks that player 1's first and second move conform to the pente pro ruleset
    private boolean checkProSpecialRules(PenteTurn turn) {
        int proRuleSecondPositionOffset = 3;
        if (turn.isTurnOneAction != null && turn.isTurnOneAction) {
            if (gameBoard[this.rows / 2][this.cols / 2] == PenteBoardIdentifierEnum.EMPTY) {
                if (turn.posX == this.cols / 2 && turn.posY == this.rows / 2) {
                    return true;
                } else {
                    return false;
                }
            } else if (
                turn.posX < this.cols / 2 + proRuleSecondPositionOffset &&
                turn.posX > this.cols / 2 - proRuleSecondPositionOffset &&
                turn.posY < this.rows / 2 + proRuleSecondPositionOffset &&
                turn.posY > this.rows / 2 - proRuleSecondPositionOffset) {
                return false;
            }
        }
        return true;
    }

    // this method removes pieces that were caputred on this turn
    public int removeCaptured(PenteTurn turn) {
        int numCaptured = 0;
        int[][] incrementDirections = {{1,0},{1,1},{0,1},{-1,1},{-1,0},{-1,-1},{0,-1},{1,-1}};
        for (int i = 1; i < 8; i++) {
            numCaptured += removeCapturedInDirection(turn, incrementDirections[i][0],
            incrementDirections[i][1]);
        }
        return numCaptured;
    }

    // helper method for removeCaptured that checks for captures in a specified direction and
    // removes the pieces
    // i.e. x_dir = 1 and y_dir = 0 checks for captures to the right and removes
    // any pieces that were captured from the board. Returns the number of pieces captured
    private int removeCapturedInDirection(PenteTurn turn, int x_dir, int y_dir) {
        int curr_x = turn.posX + x_dir;
        int curr_y = turn.posY + y_dir;
        List<int[]> removalCandidates = new ArrayList<>();
        while (curr_x >= 0 && curr_x < this.cols && curr_y >= 0 && curr_y < this.rows &&
        this.gameBoard[curr_y][curr_x] != PenteBoardIdentifierEnum.EMPTY &&
        this.gameBoard[curr_y][curr_x] != turn.playerNumber) {
            int[] removalCandidate = {curr_x, curr_y};
            removalCandidates.add(removalCandidate);
            curr_x += x_dir;
            curr_y += y_dir;
        }
        if (curr_x >= 0 && curr_x < this.cols && curr_y >= 0 && curr_y < this.rows &&
        this.gameBoard[curr_y][curr_x] == turn.playerNumber) {
            for (int i = 0; i < removalCandidates.size(); i++) {
                gameBoard[removalCandidates.get(i)[1]][removalCandidates.get(i)[0]] =
                PenteBoardIdentifierEnum.EMPTY;
            }
            return removalCandidates.size();
        }
        return 0;
    }

    // this method checks if a player has won by having enough pieces in a row after a given turn
    public boolean checkNInARow(PenteTurn turn, int n) {
        if (checkNInADirection(turn, n, 1, 1) || checkNInADirection(turn, n, 1, 0) ||
            checkNInADirection(turn, n, 1, -1) || checkNInADirection(turn, n, 0, 1)) {
            return true;
        }
        return false;
    }

    // this helper method checks if a player has enough pieces in a row in a given direction
    // for example passing in x_dir = 1 and y_dir = 0 will check if the player has enough in a row
    // in the right/left direction
    private boolean checkNInADirection(PenteTurn turn, int n, int x_dir, int y_dir) {
        int counter = 1;
        int curr_x = turn.posX + x_dir;
        int curr_y = turn.posY + y_dir;
        while (curr_x >= 0 && curr_x < this.cols && curr_y >= 0 && curr_y < this.rows &&
        this.gameBoard[curr_y][curr_x] == turn.playerNumber) {
            counter += 1;
            curr_x += x_dir;
            curr_y += y_dir;
        }
        curr_x = turn.posX - x_dir;
        curr_y = turn.posY - y_dir;
        while (curr_x >= 0 && curr_x < this.cols && curr_y >= 0 && curr_y < this.rows &&
        this.gameBoard[curr_y][curr_x] == turn.playerNumber) {
            counter += 1;
            curr_x -= x_dir;
            curr_y -= y_dir;
        }
        return counter >= n;
    }

    // right now getters are being used for testing purposes
    public PenteBoardIdentifierEnum getGameBoardValueAtPosition(int posX, int posY) {
        return this.gameBoard[posY][posX];
    }
    public PenteBoardIdentifierEnum[][] getGameBoard() {
        return gameBoard;
    }

    @Override
    public String toString() {
        String buildString = "";

        // initial catch for any error causing cases
        if (this.gameBoard == null || this.gameBoard.length <= 0) {
            return buildString;
        }

        String indent = "   "; // amount of indent to use to allow space for left side row labeling

        char[] colLetters = new char[this.gameBoard[0].length];
        for (int i = 0; i < this.gameBoard[0].length; i++) {
            colLetters[i] = (char)(97 + i);
        }


        buildString = buildString // col identifier letters
        .concat(indent)
        .concat("  ") // center the letters above each col
        .concat(
            Arrays.toString(colLetters)
                .replace(",", "  ")
                .replace("[", " ")
                .replace("]", "")
                .trim()
                .concat("\n")
        );

        for (int i = 0; i < colLetters.length; i++) {
            PenteBoardIdentifierEnum[] row = this.gameBoard[i];

            buildString = buildString // the dashed line above each row
                .concat(indent)
                .concat("----".repeat(row.length)
                    .concat("-"))
                .concat("\n");

            buildString = buildString  // initial row identifier
                .concat(String.valueOf(i))
                .concat(indent.substring(String.valueOf(i).length()));

            for (PenteBoardIdentifierEnum rowValue : row) { // the numbers and their dividers
                buildString = buildString
                    .concat("| ")
                    .concat(String.valueOf(rowValue))
                    .concat(" ");
            }

            buildString = buildString // end divider
                .concat("|\n");
        }
        buildString = buildString // the dashed line above each row
                .concat(indent)
                .concat("----".repeat(this.gameBoard[0].length)
                    .concat("-"))
                .concat("\n");
        return buildString;
    }
    public class InvalidTurnException extends Exception {
        public InvalidTurnException(String e) {
            super(e);
        }
    }
}