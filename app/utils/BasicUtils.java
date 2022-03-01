package utils;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Card;
import structures.basic.Tile;

public class BasicUtils {
    public static void drawHighlightBord(ActorRef out, GameState gameState){
        for(int i = 0; i < 9; i++){
            for(int j = 0; j < 5; j++){
                if(gameState.highlight_board[i][j]==1){
                    Tile tile = BasicObjectBuilders.loadTile(i, j);
                    BasicCommands.drawTile(out, tile, 1);
                }else if(gameState.highlight_board[i][j]==2){
                    Tile tile = BasicObjectBuilders.loadTile(i, j);
                    BasicCommands.drawTile(out, tile, 2);
                }
            }
        }
    }

    public static void highlight_card_off(ActorRef out, GameState gameState) {
        int loc = gameState.getHighlightCard();
        if(loc!=0){
            Card last_card = gameState.getHumanCard(loc);
            BasicCommands.drawCard(out, last_card, loc, 0);
            gameState.setHighlightCard(loc, 0);
            try {Thread.sleep(50);} catch (InterruptedException e) {e.printStackTrace();}
            for(int i = 0; i < 9; i++){
                for(int j = 0; j < 5; j++){
                    if(gameState.highlight_board[i][j]==1 || gameState.highlight_board[i][j]==2){
                        gameState.highlight_board[i][j] = 0;
                        Tile tile = BasicObjectBuilders.loadTile(i, j);
                        BasicCommands.drawTile(out, tile, 0);
                    }
                }
            }
        }
        gameState.select_card = false;
    }

    public static void highlight_unit_off(ActorRef out, GameState gameState){
        for(int i = 0; i < 9; i++){
            for(int j = 0; j < 5; j++){
                if(gameState.highlight_board[i][j]==1 || gameState.highlight_board[i][j]==2){
                    gameState.highlight_board[i][j] = 0;
                    Tile tile = BasicObjectBuilders.loadTile(i, j);
                    BasicCommands.drawTile(out, tile, 0);
                }
            }
        }
        gameState.select = false;
    }
}
