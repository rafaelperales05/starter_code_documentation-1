/**
 * Commander — a custom entity type (your design).
 * 
 * Design a Commander entity that fills a meaningful role in the space
 * station ecosystem. Think about:
 *   - How does it move? (random, patrol pattern, toward/away from others?)
 *   - When does it fight? (always, never, conditionally?)
 *   - When does it reproduce? (at what energy threshold?)
 *   - What niche does it fill that helps the station stay self-sustaining?
 * 
 * Display character: C
 */
public class Commander extends Entity {

    // TODO: Implement this class 
    @Override 
    public void doTimeStep(){};  
    @Override
    public String toString() {
        return "";
    }

    @Override
    public boolean fight(String opp) {
        return false;
    }

}
