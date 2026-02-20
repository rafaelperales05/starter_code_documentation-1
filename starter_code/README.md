# ECE 422C Lab 3: Space Station Simulation

**Course:** ECE 422C – Software Design and Implementation II  
**Instructor:** Evan Speight 
**Topics:** Object-Oriented Design, Polymorphism, Exception Handling, Collections, Software Engineering
**Due:** Feb 23, 2026 at 11:59 PM 
## Overview

In this lab, you will implement a discrete-time simulation of a space station populated by various autonomous entities (astronauts, robots, life support systems, etc.). This lab tests your understanding of **inheritance**, **polymorphism**, **abstract classes**, **exception handling**, and the **collections framework**.

## Design Goal

A real space station is a closed system — nothing comes in, nothing goes out. Every watt of power, every calorie of food, and every liter of oxygen must be produced, consumed, and recycled by the systems on board. If any one subsystem fails or falls behind, the whole station is in trouble.

Your goal is to build a simulation where the ecosystem of entities is **self-sustaining**. PowerCells should generate enough energy to keep the station running. MaintenanceBots should keep critical systems operational. Your two custom entities should fill roles that the station needs to survive long-term — perhaps resource recycling, emergency repair, crew coordination, or something else entirely. The precise roles are up to you, but they should contribute to the station's ability to sustain itself.

A well-designed simulation will reach a **dynamic equilibrium**: entity populations will rise and fall, energy will ebb and flow, but the station will keep running indefinitely. A poorly designed one will collapse — entities will starve, populations will crash to zero, or a single type will consume everything and crowd out the rest.

There is no single "correct" balance. Part of the challenge is experimenting with your entity behaviors, energy costs, reproduction thresholds, and other parameters until you find a configuration where the station thrives. When you write your design document, explain what role each of your custom entities plays in keeping the ecosystem stable, and describe what happened when things went wrong along the way.

## Background

The International Space Station houses various autonomous entities that move around, consume resources, reproduce/manufacture new entities, and interact with each other. Your simulation will model this environment with:
- A 2D grid-based world representing the space station modules
- Various entity types with different behaviors
- Energy/resource management
- Command-line interface for controlling the simulation

## Objectives

By completing this lab, you will demonstrate:

1. **Object-Oriented Design**: Proper use of inheritance hierarchies and abstract classes
2. **Polymorphism**: Runtime method dispatch for different entity behaviors
3. **Exception Handling**: Custom exceptions for invalid operations
4. **Collections**: Managing dynamic populations using Java collections
5. **Software Engineering**: Clean code, proper encapsulation, testing

## Building and Running

This lab does not use packages. All `.java` files live in a single directory. Compile and run from the command line:

```bash
# Compile everything
javac *.java

# Run interactively
java Main

# Run with an input file
java Main input_commands.txt
```

## Technical Specifications

### World Model
- **Grid Size**: Configurable NxM rectangular grid (default 76x40)
- **Display Width**: The world display fits within 78 columns (76 interior characters + 2 border characters), so it displays properly on a standard terminal
- **Coordinate System**: (0,0) is top-left, (75,39) is bottom-right
- **Boundaries**: World wraps around (toroidal topology)
- **Occupancy**: Multiple entities can occupy the same location

### Entity Types to Implement

1. **PowerCell** (basic life support) - moves randomly, provides energy
2. **MaintenanceBot** - follows genetic algorithm for movement
3. **Commander** (your custom entity) - implement your own behavior
4. **Engineer** (your custom entity) - implement your own behavior

### Energy System
- All entities start with configurable energy (default 100)
- Movement costs energy: walking costs 2, running costs 5
- All entities pay a rest cost of 1 energy per time step
- Entities die when energy reaches 0
- Some entities can reproduce when energy is sufficient
- PowerCells replenish energy through "solar charging" (+1 per step)

### Commands
Your simulation must support these commands:
- `show` - Display current world state
- `make EntityType [count]` - Create entities
- `step [count]` - Advance simulation time steps
- `seed number` - Set random number seed
- `stats EntityType` - Show statistics for entity type
- `help` - Show simulation overview, entity descriptions, and current parameters
- `?` - Show a brief list of available commands
- `quit` - Exit simulation

Invalid commands should print an appropriate error message (see tests for exact format).

## Common Mistakes to Avoid

1. **Forgetting edge cases**: Empty world display, invalid entity names
2. **Movement bugs**: Incorrect coordinate wrapping, direction calculations
3. **Energy management**: Not deducting energy costs properly
4. **Collection modification**: ConcurrentModificationException during iteration
5. **Exception handling**: Not properly catching/throwing custom exceptions

## Testing

Five test cases are provided in the `tests/` directory. Each test consists of an input file that gets piped to the program and an expected output file.

You must put all of your `.java` files, the `tests` directory, and the `run_tests.sh` script in the same directory.
To generate expected output from a working solution and then run all tests:

```bash
# First time only: generate expected output from the solution
./run_tests.sh --generate

# Run tests against your implementation
./run_tests.sh
```

The tests cover:

| Test | Description |
|------|-------------|
| **Test1** | Basic commands: entity creation, invalid entity names, one simulation step, stats output |
| **Test2** | World display: verifies the grid renders correctly with proper borders |
| **Test3** | Population dynamics: 20 MaintenanceBots and 50 PowerCells over 100 steps — checks that reproduction and death are working |
| **Test4** | Custom entities: Commander and Engineer creation, stepping, and stats |
| **Test5** | Error handling: malformed commands, bad arguments, missing arguments, invalid counts |

Output must match exactly (including prompts and whitespace). Use the `seed` command to make random behavior deterministic for reproducible testing.

## Checklist

### Core Implementation 
- [ ] `Entity` abstract class with proper encapsulation
- [ ] Basic entity types: `PowerCell`, `MaintenanceBot`
- [ ] Movement methods: `walk()`, `run()` with energy costs
- [ ] `reproduce()` method for creating offspring
- [ ] World display with proper ASCII visualization
- [ ] Command parsing and error handling

### Advanced Features
- [ ] Two custom entity classes with unique behaviors
- [ ] Advanced genetic algorithm for MaintenanceBot
- [ ] Energy transfer/combat system between entities
- [ ] Statistics tracking and display
- [ ] Comprehensive parameter configuration

### Code Quality
- [ ] Proper JavaDoc comments
- [ ] Clean, readable code structure
- [ ] No magic numbers (use constants)
- [ ] Appropriate access modifiers
- [ ] Exception safety

## Submission Requirements

Submit a ZIP file on Canvas containing:
1. All Java source files (no packages — all files in one directory) in a subdirectory named `src`
2. A subdirectory named `Documentation` that contains the results of running javadoc on your code 
3. A brief design document explaining your custom entities and how they contribute to the station's long-term stability (use outline provided), also in the `Documentation` directory 

Finally, submit your code to Gradescope for automatic testing.  Note that some tests you will receive feedback on, and others are "hidden" and you won't see those results until after the due date. 

## Grading Rubric

| Component | Points | Description |
|-----------|--------|-------------|
| Pass all Gradscope Tests | 80% | All hidden and visible test cases pass |
| Javadoc Documentation    | 10% | Javadoc Documentation with proper Details of all classes |
| Design Document          | 10%   | Description including UML diagrams of your design|

## Academic Integrity

This is an individual assignment. While you may discuss high-level concepts with classmates, **all code must be your own work**. Copying code from online sources, previous semesters, or other students constitutes academic dishonesty. If you use an AI coding assistant, you must disclose this in your design document and ensure that the final code is your own work. Plagiarism will be penalized according to the university's academic integrity policies.

## Tips for Success

1. **Start Early**: Begin with the basic Entity class and build incrementally
2. **Test Often**: Run the provided tests frequently as you implement each feature
3. **Read Carefully**: Pay attention to coordinate systems and energy rules
4. **Debug Systematically**: Use print statements or the debugger to step through complex algorithms
5. **Design First**: Plan your entity behaviors before coding
6. **Experiment**: Run long simulations and watch what happens — tune your parameters when populations collapse or explode

Good luck, and may your space station simulation thrive! 🚀
