import java.util.*
import OregonTrailDate
import Game

fun main(args: Array<String>){
    do {
        val game = Game()
        while (!game.isOver) {
            game.nextTurn()
        }
        println("Do you want to play again? (Y|N)")
    }while(Scanner(System.`in`).next()[0].toLowerCase() == 'y')

}