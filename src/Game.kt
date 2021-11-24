package chess

import chess.Game.board
import chess.Game.currentPlayer
import chess.Game.emptyPlace
import chess.Game.lastMove
import chess.Game.listOfBlackPawns
import chess.Game.listOfWhitePawns
import chess.Game.validMove
import chess.GameStatus.*

object Game {

    val emptyPlace = ChessPiece()

    val listOfWhitePawns = mutableListOf<Pawn>()
    val listOfBlackPawns = mutableListOf<Pawn>()

    // * we are storing the white in 0,1
    val board = MutableList<MutableList<ChessPiece>>(8) { x ->
        when (x) {
            1 -> MutableList<ChessPiece>(8) { y ->
                val whitePawn = Pawn(y, Color.WHITE, x, y)
                listOfWhitePawns.add(whitePawn)
                whitePawn
            }

            6 -> MutableList<ChessPiece>(8) { y ->
                val blackPawn = Pawn(y, Color.BLACK, x, y)
                listOfBlackPawns.add(blackPawn)
                blackPawn
            }
            else -> {
                MutableList<ChessPiece>(8) {
                    emptyPlace
                }
            }
        }
    }

    var currentPlayer = PLAYER.WHITE_PLAYER

    var lastMove = "0000"

    /**
     * Regex to check the move is valid or not
     */
    val validMove = "[a-h][1-8][a-h][1-8]".toRegex()


}

fun main() {
    //print the title of the game
    println(" Pawns-Only Chess")

    //get the player 1 name
    println("First Player's name:")
    PLAYER.WHITE_PLAYER.playerName = readLine()!!

    //get player 2 name
    println("Second Player's name:")
    PLAYER.BLACK_PLAYER.playerName = readLine()!!

    //print the board
    drawTheBoard()

    //start the game
    startTheGame()
}

fun drawTheBoard() {
    for (i in board.size - 1 downTo 0) {
        printDivider()
        println(board[i].joinToString(" | ", "${i + 1} | ", " |"))
    }
    printDivider()
    println("    a   b   c   d   e   f   g   h")
}


fun printDivider() = println("  +---+---+---+---+---+---+---+---+")


/**
 * this fun starts the game with player1 and loops till user input exit cmd
 */
fun startTheGame() {
    //start by player1

    loop@ while (true) {

        if (listOfWhitePawns.size == 0) {
            println("Black Wins!")
            break@loop
        }

        if (listOfBlackPawns.size == 0) {
            println("White Wins!")
            break@loop
        }

        var staleMate = true

        val pawnList = when(currentPlayer){
            PLAYER.WHITE_PLAYER -> listOfWhitePawns
            PLAYER.BLACK_PLAYER -> listOfBlackPawns
        }

        val movieForWard = when (currentPlayer) {
            PLAYER.WHITE_PLAYER -> +1
            PLAYER.BLACK_PLAYER -> -1
        }

        stale@for (pawn in pawnList) {
            val (cFile, cRank) = Pair('a'+pawn.y1, pawn.x1 + 1)
            val listOfPossibleMoves = mutableListOf<String>()
            if (cRank == 2 || cRank == 7) {
                when(currentPlayer){
                    PLAYER.WHITE_PLAYER -> {
                        listOfPossibleMoves.add("$cFile" + "2$cFile" + "3")
                        listOfPossibleMoves.add("$cFile" + "2$cFile" + "4")
                    }
                    PLAYER.BLACK_PLAYER -> {
                        listOfPossibleMoves.add("$cFile" + "7$cFile" + "6")
                        listOfPossibleMoves.add("$cFile" + "7$cFile" + "5")
                    }
                }

            } else {
                listOfPossibleMoves.add("$cFile$cRank$cFile${cRank + movieForWard}")
            }
            when (cFile) {
                'a' -> listOfPossibleMoves.add("$cFile$cRank${cFile + 1}${cRank + movieForWard}")
                'h' -> listOfPossibleMoves.add("$cFile$cRank${cFile - 1}${cRank + movieForWard}")
                in 'b'..'g' -> {
                    listOfPossibleMoves.add("$cFile$cRank${cFile + 1}${cRank + movieForWard}")
                    listOfPossibleMoves.add("$cFile$cRank${cFile - 1}${cRank + movieForWard}")
                }
            }

            for(i in listOfPossibleMoves) {
                val (gameStatus, _) = isAValidMove(i)
                if (gameStatus != INVALID) {
                    staleMate = false
                    break@stale
                }
            }
        }

        if(staleMate){
            println("Stalemate!")
            break@loop
        }


        println("${currentPlayer.playerName}'s turn:")
        val playerMove = readLine()!!

        if (playerMove == "exit") break@loop


        val (moveType, statusMessage) = isAValidMove(playerMove)

        when (moveType) {
            GameStatus.WIN -> {
                moviePiece(playerMove, moveType)
                println(statusMessage)
                break@loop
            }
            GameStatus.INVALID -> {
                println(statusMessage)
                continue@loop
            }
            else -> {
                moviePiece(playerMove, moveType)
                lastMove = playerMove
                currentPlayer = when (currentPlayer) {
                    PLAYER.WHITE_PLAYER -> PLAYER.BLACK_PLAYER
                    PLAYER.BLACK_PLAYER -> PLAYER.WHITE_PLAYER
                }
            }
        }



    }

    println("Bye!")
}



/**
 * checks whether the user given movie is valid or not
 * @param playerMove we need to pass the player move as a string
 * @param currentPlayer current player 1 or 2
 * @return a Pair of boolean and string, boolean containing whether the player move is valid
 * and string contains the error msg
 */
fun isAValidMove(playerMove: String): Pair<GameStatus, String> {

    var statusMessage = "Invalid Input"

    /**
     * we check whether the input is correct using regex
     * checks that is has the proper format alphabet and number
     */
    if (!playerMove.matches(validMove)) {
        return Pair(INVALID, statusMessage)
    }


    // * get the current coordinates
    val (cFile, cRank) = Pair(playerMove[0], playerMove[1].digitToInt())
    // * get the future coordinates
    val (fFile, fRank) = Pair(playerMove[2], playerMove[3].digitToInt())


    // * convert the coordinates to list accessible indexes
    val (y1, x1) = Pair(cFile.digitToInt(18) - 10, cRank - 1)
    val (y2, x2) = Pair(fFile.digitToInt(18) - 10, fRank - 1)


    // ! check for a piece is available in the giver coordinates
    if (board[x1][y1].toString() == " " || board[x1][y1].toString() != currentPlayer.color.colorChar) {
        statusMessage = "No ${currentPlayer.color.stringName} pawn at $cFile$cRank"
        return Pair(INVALID, statusMessage)
    }

    val movieForWard = when (currentPlayer) {
        PLAYER.WHITE_PLAYER -> +1
        PLAYER.BLACK_PLAYER -> -1
    }

    val captureColor = when (currentPlayer) {
        PLAYER.WHITE_PLAYER -> PLAYER.BLACK_PLAYER.color
        PLAYER.BLACK_PLAYER -> PLAYER.WHITE_PLAYER.color
    }


    return when {
        // * wining condition
        fRank == 8 || fRank == 1 -> {
            val regex = when (currentPlayer) {
                PLAYER.WHITE_PLAYER -> {
                    "$cFile" + "7$cFile" + "8"
                }
                PLAYER.BLACK_PLAYER -> {
                    "$cFile" + "2$cFile" + "1"
                }
            }
            statusMessage = "${currentPlayer.color.stringName} Wins!"
            if (playerMove.matches(regex.toRegex())) Pair(WIN, statusMessage)
            else Pair(INVALID, statusMessage)
        }


        // * moving forward
        cFile == fFile -> {
            if (board[x2][y2].toString() != " ") {
                Pair(INVALID, statusMessage)
            } else {
                val regex = when (currentPlayer) {
                    PLAYER.WHITE_PLAYER -> {
                        if (cRank == 2)
                            "$cFile" + "2$cFile" + "(3|4)"
                        else
                            "$cFile$cRank$cFile[${cRank + movieForWard}]"
                    }
                    PLAYER.BLACK_PLAYER -> {
                        if (cRank == 7)
                            "$cFile" + "7$cFile" + "(5|6)"
                        else
                            "$cFile$cRank$cFile[${cRank - 1}]"
                    }
                }

                if (playerMove.matches(regex.toRegex())) Pair(FORWARD, statusMessage)
                else Pair(INVALID, statusMessage)
            }
        }

        // * capturing
        fFile == cFile - 1 || fFile == cFile + 1 -> {
            // * check for En passant
            when {
                board[x2][y2].toString() == " " -> {
                    // * get the current coordinates
                    val (cF, _) = Pair(lastMove[0], lastMove[1].digitToInt())
                    // * get the future coordinates
                    val (_, fR) = Pair(lastMove[2], lastMove[3].digitToInt())
                    val possibleForNPass = when (currentPlayer) {
                        PLAYER.WHITE_PLAYER -> "${cF}7${cF}5".toRegex()
                        PLAYER.BLACK_PLAYER -> "${cF}2${cF}4".toRegex()
                    }

                    val nPass = "(${cF + 1}|${cF - 1})$fR$cF${fR + movieForWard}".toRegex()

                    if (lastMove.matches(possibleForNPass) && playerMove.matches(nPass)) {
                        Pair(NPASS, statusMessage)
                    } else
                        Pair(INVALID, statusMessage)

                }

                board[x2][y2].toString() == captureColor.colorChar -> {
                    val regexCapture = when (cFile) {
                        'a' -> "$cFile$cRank${cFile + 1}${cRank + movieForWard}"
                        'h' -> "$cFile$cRank${cFile - 1}${cRank + movieForWard}"
                        in 'b'..'g' -> "$cFile$cRank(${cFile + 1}|${cFile - 1})${cRank + movieForWard}"
                        else -> {
                            ""
                        }
                    }
                    if (playerMove.matches(regexCapture.toRegex()))
                        Pair(CAPTURE, statusMessage)
                    else Pair(INVALID, statusMessage)
                }
                else -> {
                    Pair(INVALID, statusMessage)
                }
            }
        }

        // * invalid move
        else -> {
            return Pair(INVALID, statusMessage)
        }
    }

}

private fun moviePiece(playerMove: String, gameStatus: GameStatus) {

    val (cFile, cRank) = Pair(playerMove[0], playerMove[1].digitToInt())
    val (fFile, fRank) = Pair(playerMove[2], playerMove[3].digitToInt())

    val (y1, x1) = Pair(cFile.digitToInt(18) - 10, cRank - 1)
    val (y2, x2) = Pair(fFile.digitToInt(18) - 10, fRank - 1)


    when (gameStatus) {
        FORWARD -> {
            moveForward(x1, y1, x2, y2)
            drawTheBoard()
        }
        CAPTURE -> {
            // * remove the opposite colour pawn from list first
            when (currentPlayer) {
                PLAYER.WHITE_PLAYER -> {
                    val pawnToRemove = listOfBlackPawns.find { pawn ->
                        pawn.x1 == x2 && pawn.y1 == y2
                    }
                    listOfBlackPawns.remove(pawnToRemove)
                }
                PLAYER.BLACK_PLAYER -> {
                    val pawnToRemove = listOfWhitePawns.find { pawn ->
                        pawn.x1 == x2 && pawn.y1 == y2
                    }
                    listOfWhitePawns.remove(pawnToRemove)
                }
            }

            // * move the pawn forward
            moveForward(x1, y1, x2, y2)

            drawTheBoard()

        }

        NPASS -> {
            val (fF, fR) = Pair(lastMove[2], lastMove[3].digitToInt())
            val (y3, x3) = Pair(fF.digitToInt(18) - 10, fR - 1)

            // * first remove the npassed peice
            when (currentPlayer) {
                PLAYER.WHITE_PLAYER -> {
                    val pawnToRemove = listOfBlackPawns.find { pawn ->
                        pawn.x1 == x3 && pawn.y1 == y3
                    }
                    listOfBlackPawns.remove(pawnToRemove)
                }
                PLAYER.BLACK_PLAYER -> {
                    val pawnToRemove = listOfWhitePawns.find { pawn ->
                        pawn.x1 == x3 && pawn.y1 == y3
                    }
                    listOfWhitePawns.remove(pawnToRemove)
                }
            }

            // * move current player piece forward
            moveForward(x1, y1, x2, y2)

            // * make the npassed place empty
            board[x3][y3] = emptyPlace

            drawTheBoard()

        }
    }
}

private fun moveForward(x1: Int, y1: Int, x2: Int, y2: Int) {
    val p = board[x1][y1] as Pawn
    p.x1 = x2
    p.y1 = y2
    board[x2][y2] = p
    board[x1][y1] = emptyPlace
}

