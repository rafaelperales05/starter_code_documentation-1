# Lab 3 Design Document: Space Station Simulation

**Name:** Rafael Perales 
**EID:**  Rp37497
**Date:** 2/23/2026

---

## 1. System Overview

This simulation models a space station ecosystem where different entity types interact to create a balanced food chain. The world is a toroidal 76×40 grid where entities move, consume resources, fight, and reproduce. The design goal was to create a self-sustaining ecosystem where no single entity type dominates or goes extinct—PowerCells provide base energy, MaintenanceBots and Engineers act as primary consumers, and Commanders serve as apex predators to maintain population balance.

The simulation successfully achieves stable equilibrium under the right conditions. Each entity has specific movement patterns, energy requirements, and interaction rules that create dependencies. Engineers act as peaceful farmers who occasionally compete with each other and transfer energy to MaintenanceBots, creating a symbiotic relationship. Commanders hunt both Engineers and MaintenanceBots (with preference for Engineers) to regulate their populations. MaintenanceBots use genetic algorithms to evolve better movement patterns over time, making them more efficient energy collectors.

The ecosystem maintains stability when the predator-prey ratios are balanced correctly. Too many Commanders leads to ecosystem collapse as they consume all the producers. Too few Commanders allows MaintenanceBots to overpopulate and deplete PowerCells. The simulation demonstrates emergent behavior where entity populations oscillate but remain within sustainable bounds, mimicking real-world predator-prey dynamics.

---

## 2. Class Descriptions

For each class you implemented, provide:
- A brief description of the class's purpose and responsibilities
- Key design decisions you made and why
- How it interacts with other classes in the system

### 2.1 Commander

Commanders are apex predators that maintain population balance in the ecosystem. They move in straight lines for 4 steps before randomly changing direction, creating an efficient hunting pattern that covers territory systematically. Commanders always fight PowerCells (for base energy), always fight Engineers (primary prey), and rarely hunt MaintenanceBots (only 1/30 times to keep bot populations viable). This selective hunting behavior prevents any one species from dominating.

Commanders reproduce when their energy reaches 150, which requires successfully hunting other entities. Their reproduction threshold is carefully balanced—high enough that they can't reproduce from PowerCells alone but achievable through hunting. This ensures Commanders only thrive when there's sufficient prey, creating natural population oscillation.

Engineers are peaceful farmers that provide stable energy flow to MaintenanceBots while acting as prey for Commanders. They move in an efficient row-based pattern (traveling East across a row, then South one step, then West across the next row) like systematically tending crop fields. This movement pattern maximizes their PowerCell collection efficiency. Engineers always harvest PowerCells and compete with other Engineers (1/5 chance) or Commanders (1/5 chance) for population control, but are peaceful toward MaintenanceBots.

The key innovation is the energy transfer mechanic: when an Engineer with 150+ energy encounters a MaintenanceBot, they transfer 30 energy instead of fighting. This creates a symbiotic farming relationship—Engineers efficiently collect PowerCells and "feed" MaintenanceBots, helping support bot populations without direct competition. Engineers reproduce at 150 energy, making them productive farmers that can sustain both their own population and support bots.

Engineers are essential because they provide a stable, predictable prey base for Commanders while simultaneously supporting MaintenanceBot populations through energy transfer. Without Engineers, the ecosystem becomes more volatile—Commanders would rely entirely on hunting aggressive MaintenanceBots (which always fight back), and MaintenanceBots would lack their symbiotic energy source. Engineers also provide population diversity, making the ecosystem more resilient to random events.total ecosystem collapse. Commanders create a natural check on herbivore populations, forcing them to balance reproduction with survival. The occasional MaintenanceBot hunting (1/30 chance) adds just enough pressure to prevent bot overpopulation without driving them extinct.

### 2.2 Engineer

**Entity.java**: Made significant modifications to support the static runStats architecture. The key design decision was moving reproduce() logic to handle MaintenanceBot gene mutations internally, eliminating the need for helper methods that the autograder would reject. Also moved Engineer energy transfer logic into the solveEncounters() method using instanceof checks, allowing cooperative interactions alongside competitive ones.

**Main.java**: Implements a command-line interface with extensive Javadoc documentation. The runTypeStats() method uses reflection to dispatch to class-specific static runStats methods in Commander, Engineer, and MaintenanceBot classes. This allows each entity type to display customized statistics while maintaining a clean interface. All error handling checks for edge cases like invalid class names, missing commands, and invalid numeric inputs.hat happens to the ecosystem if you remove Engineers entirely?_

### 2.3 Other Classes (if modified or relevant)

_If you made significant design decisions in your implementation of Main, or if you added any helper classes, describe them here._

---

## 3. UML Class Diagrams

_Provide a UML class diagram for every class you implemented. Each diagram must show:_

- _Class name_
- _Fields (with visibility: `+` public, `-` private, `#` protected)_
- _Methods (with visibility, parameters, and return types)_
- _Inheritance relationships (solid arrow to parent class)_
- _Any associations or dependencies between your classes and the provided classes_

_Include at minimum:_

- [ ] Commander UML diagram
- [ ] Engineer UML diagram
- [ ] A diagram showing the inheritance hierarchy (Entity ← Commander, Entity ← Engineer, Entity ← PowerCell, Entity ← MaintenanceBot)

_You may draw these by hand and scan them, use a tool like draw.io or Lucidchart, or produce them in any other legible format._

---

## 4. Ecosystem Design

### 4.1 Entity Roles

_Fill in the table below describing what ecological role each entity type plays in your simulation._

| Entity | Role | Energy Strategy | Reproduction Threshold |ever reproduces | Never fights (always consumed) |
| MaintenanceBot | Primary consumer / Evolver | Harvest PowerCells, receive Engineer transfers | 150 energy | Always fights (aggressive) |
| Commander | Apex predator / Population control | Hunt Engineers & MaintenanceBots | 150 energy | Selective hunter (always vs E/*, 1/30 vs M) |
| Engineer | Farmer / Symbiotic supporter | Efficiently harvest PowerCells, transfer to bots | 150 energy | Peaceful (1/5 vs E/C, never vs M) Fight |
| Commander | Keep Population Stable |  | | Always Fight |
| Engineer | | | | Never Fight |

The balancing process involved extensive experimentation with initial population ratios and behavioral parameters. I started with roughly equal counts of each entity type (10-15 of each), which immediately led to collapse—either Commanders overhunted and starved, or MaintenanceBots exploded in population.

The first issue was Commander aggression. Initial designs had Commanders hunting MaintenanceBots at the same rate as Engineers (always or 50%), which decimated the bot population since bots always fight back (risky for Commanders). I adjusted this to 1/30 MaintenanceBot hunting, giving bots breathing room while still preventing overpopulation. The second key adjustment was the Engineer-MaintenanceBot energy transfer, creating a stable food web where Engineers "farm" PowerCells and share energy with bots.

Movement patterns were also critical. Engineers initially moved randomly, making them inefficient farmers. The row-based systematic pattern dramatically improved their energy collection, letting them sustain both reproduction and energy transfer. Commander movement (straight lines with periodic turns) provides efficient hunting coverage without being too aggressive.

For stable 500+ step runs, a good starting configuration is: 10-12 Engineers, 8-10 MaintenanceBots, 3-4 Commanders, and let PowerCells spawn naturally. The key ratio is approximately 3:2:1 (Engineers:Bots:Commanders). Seeds in the 1000-5000 range tend to produce good initial distributions that avoid early clustering deaths.pe dominated, etc.)_
- _What parameters or behaviors did you adjust to fix it?_
- _What seed value and entity counts produce a stable run of 500+ steps?_

### 4.3 Failure Modes

_Describe at least two failure modes you observed during development — situations where the ecosystem collapsed. For each, explain what caused the collapse and what you changed to prevent it._
 Commander Starvation Cascade** - When Commanders hunted MaintenanceBots too aggressively (50% fight rate), they would deplete both bot and Engineer populations simultaneously. Since both prey types fight back, Commanders would take heavy damage in fights. Once prey populations dropped below critical mass, Commanders couldn't find enough food and would die off from rest energy costs. This left Engineers to multiply unchecked until PowerCells couldn't sustain them, causing total collapse. **Fix:** Reduced MaintenanceBot hunting to 1/30 chance and made Engineers never fight Commanders directly. This ensures Commanders always have a "safe" prey option (Engineers) while still providing population pressure on bots.

2. **Failure mode 2: MaintenanceBot Explosion** - MaintenanceBots reproduce at 150 energy and always fight (aggressive), making them very successful in early game. Without sufficient predation pressure, they would multiply rapidly, consuming all PowerCells faster than respawn rates. Eventually the bot population would peak around 60-80 entities, then crash simultaneously as PowerCells became scarce. The genetic algorithm couldn't save them since no genes help when there's zero food. **Fix:** Increased Commander population in starting configurations and added the Engineer-bot energy transfer. This creates a negative feedback loop—when bots become numerous, they encounter more Commanders (who hunt them 1/30 times) and more Engineers (who help sustain them), stabilizing the population through both predation and symbiosis.
2. **Failure mode 2:** _description_

---

Beyond the 5 provided tests, I extensively tested edge cases and ecosystem dynamics. I ran long simulations (1000+ timesteps) with various starting configurations to verify stability, watching for population oscillations versus complete collapse. I tested null and invalid inputs for all Main commands to ensure proper error handling (lowercase class names, negative numbers, nonexistent classes).

For ecosystem testing, I used the stats command frequently to monitor population ratios. I ran simulations with only two entity types (e.g., just Engineers and PowerCells) to verify each entity's independent behavior before testing interactions. I tested the genetic algorithm by running extended MaintenanceBot-only simulations and verifying gene percentages evolved over time (checked with runStats MaintenanceBot).

Edge cases tested include: (1) Empty world scenarios where all entities die—verified graceful degradation; (2) Single-entity encounters to verify fight logic worked correctly for each pairing (M vs E, C vs E, E vs M with energy transfer); (3) Massive initial populations (50+ entities) to test performance and verify no crashes; (4) Seed values that create clustering (entities spawning in same locations) to test encounter resolution; (5) Zero energy scenarios to verify entities die correctly. The junit tests provided verification for core functionality like makeEntity validation, reproduce energy splitting, and getInstances filtering.

The hardest part was balancing the ecosystem to prevent boom-bust cycles. Creating entities that interact in interesting ways is easy, but achieving stable equilibrium required dozens of test runs and careful parameter tuning. I initially underestimated how sensitive the system is to small changes—adjusting Commander hunting rates from 1/10 to 1/30 made the difference between collapse and stability.

The second major challenge was working within autograder constraints. The requirement that Entity not have any new non-abstract methods forced creative solutions like embedding MaintenanceBot gene mutation logic directly in Entity.reproduce() and moving Engineer energy transfer into Entity.solveEncounters(). This taught me to work with inheritance constraints rather than fighting them—sometimes the "clean" design (separate helper methods) isn't possible, and you need to consolidate logic creatively.

If I started over, I'd begin with ecosystem design before implementation. I jumped into coding entity behaviors without fully thinking through the food web structure, leading to multiple rewrites. I'd sketch out the energy flow diagram first: PowerCells → Bots/Engineers → Commanders, with Engineers also supporting Bots. Starting with this high-level design would have saved time.

The key OOP lesson was the power of polymorphism and abstract methods. Having each entity implement doTimeStep() and fight() differently lets the simulation loop treat all entities uniformly (Entity.worldTimeStep() just calls doTimeStep() on each) while producing complex emergent behavior. The abstract fight() method was particularly elegant—entities don't know what they're encountering until runtime, and their decision (true/false) combines with their opponent's decision to determine outcomes. This demonstrates how simple abstractions can model sophisticated real-world interactions.? If you wrote any additional test input files, describe what they test._

---

## 6. Challenges and Lessons Learned

_What was the hardest part of this lab? What would you do differently if you started over? What did you learn about object-oriented design from this experience?_
