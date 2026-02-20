/*
 * Do not change these parameters without instructions.
 * If you change them for testing, make sure to change them back.
 */
public class Params {
	public static final int world_width = 76;
	public static final int world_height = 40;
	public static final int walk_energy_cost = 2;
	public static final int run_energy_cost = 5;
	public static final int rest_energy_cost = 1;
	public static final int min_reproduce_energy = 20;
	public static final int refresh_powercell_count = (int) Math.max(1, (world_width * world_height) / 1000.0);
	public static final int solar_energy_amount = 1;
	public static final int start_energy = 100;
}
