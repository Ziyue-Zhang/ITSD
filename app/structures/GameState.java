package structures;

import structures.basic.Unit;
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;

/**
 * This class can be used to hold information about the on-going game.
 * Its created with the GameActor.
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class GameState {

	
	public boolean gameInitalised = false;
	
	public boolean something = false;

	public int[][] allocated = new int[9][5];
	
	public static Unit return_Unit(String name){
		switch (name) {
			case "humanAvatar" : 
				return BasicObjectBuilders.loadUnit(StaticConfFiles.humanAvatar, 0, Unit.class);
			case "comodo_charger" : 
				return BasicObjectBuilders.loadUnit(StaticConfFiles.u_comodo_charger, 1, Unit.class);
			case "azure_herald" : 
				return BasicObjectBuilders.loadUnit(StaticConfFiles.u_azure_herald, 2, Unit.class);
			case "azurite_lion" : 
				return BasicObjectBuilders.loadUnit(StaticConfFiles.u_azurite_lion, 3, Unit.class);
			case "fire_spitter" : 
				return BasicObjectBuilders.loadUnit(StaticConfFiles.u_fire_spitter, 4, Unit.class);
			case "hailstone_golem" : 
				return BasicObjectBuilders.loadUnit(StaticConfFiles.u_hailstone_golem, 5, Unit.class);
			case "ironcliff_guardian" : 
				return BasicObjectBuilders.loadUnit(StaticConfFiles.u_ironcliff_guardian, 6, Unit.class);
			case "pureblade_enforcer" : 
				return BasicObjectBuilders.loadUnit(StaticConfFiles.u_pureblade_enforcer, 7, Unit.class);
			case "silverguard_knight" : 
				return BasicObjectBuilders.loadUnit(StaticConfFiles.u_silverguard_knight, 8, Unit.class);
			default :
		}
		return null;
	}


	public static Unit return_enemy_unit(String name){
		switch (name) {
			case "aiAvatar" : 
				return BasicObjectBuilders.loadUnit(StaticConfFiles.aiAvatar, 9, Unit.class);
			case "blaze_hound" : 
				return BasicObjectBuilders.loadUnit(StaticConfFiles.u_blaze_hound, 10, Unit.class);
			case "bloodshard_golem" : 
				return BasicObjectBuilders.loadUnit(StaticConfFiles.u_bloodshard_golem, 11, Unit.class);
			case "hailstone_golemR" : 
				return BasicObjectBuilders.loadUnit(StaticConfFiles.u_hailstone_golemR, 12, Unit.class);
			case "planar_scout" : 
				return BasicObjectBuilders.loadUnit(StaticConfFiles.u_planar_scout, 13, Unit.class);
			case "pyromancer" : 
				return BasicObjectBuilders.loadUnit(StaticConfFiles.u_pyromancer, 14, Unit.class);
			case "rock_pulveriser" : 
				return BasicObjectBuilders.loadUnit(StaticConfFiles.u_rock_pulveriser, 15, Unit.class);
			case "serpenti" : 
				return BasicObjectBuilders.loadUnit(StaticConfFiles.u_serpenti, 16, Unit.class);
			case "windshrike" : 
				return BasicObjectBuilders.loadUnit(StaticConfFiles.u_windshrike, 17, Unit.class);
			default :
		}
		return null;
	}

}
