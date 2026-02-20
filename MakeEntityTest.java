import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MakeEntityTest {

    // Helper to read Entity's protected TestEntity population accessor
    static class Probe extends Entity.TestEntity {
        @Override public void doTimeStep() {}
        @Override public boolean fight(String opp) { return false; }
        @Override public String toString() { return "P"; }

        static int populationSize() {
            return getPopulation().size();
        }
    }

    @BeforeEach
    void resetWorld() {
        Entity.clearWorld();
    }

    @Test
    void makeEntity_validType_createsOne() {
        assertDoesNotThrow(() -> Entity.makeEntity("PowerCell"));
        assertEquals(1, Probe.populationSize());
    }

    @Test
    void makeEntity_invalidType_throws() {
        assertThrows(InvalidEntityException.class, () -> Entity.makeEntity("NotAType"));
    }

    @Test
    void makeEntity_lowercaseType_throws() {
        assertThrows(InvalidEntityException.class, () -> Entity.makeEntity("powercell"));
    }

    @Test
    void makeEntity_empty_throws() {
        assertThrows(InvalidEntityException.class, () -> Entity.makeEntity(""));
    }

    @Test
    void makeEntity_null_throws() {
        assertThrows(InvalidEntityException.class, () -> Entity.makeEntity(null));
    }
}