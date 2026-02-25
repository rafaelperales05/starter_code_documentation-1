# Lab 3 Design Document: Space Station Simulation

**Name:** Rafael Perales
**EID:** Rp37497
**Date:** 2/25/2026 

---

## 1. System Overview

The simulation is designed to model a space system with 4 different Entity types : PowerCells, Commanders, Engineers, and MaintenaceBots. 
Commanders are the "leaders" and keep the population in control. MaintenanceBots keep the system working, and Engineers provide support to the bots. Finally, PowerCells are the source of food for the other Entities. 

The Ecosystem achieves balance (1000 + steps) with the parameters: 
seed 1
make PowerCell 200
make Engineer 15
make MaintenanceBot 15
make Commander 10 
This aligns with the roles, since engineers support MaintenanceBots while Commanders keep both under population control. 


---

## 2. Class Descriptions


### 2.1 Commander

Commanders serve as the top predators of the space station. They patrol aggressively, moving 6 steps forward before randomly selecting a new direction. Commanders fight all entities they encounter, functioning as population regulators to prevent any species from dominating. They reproduce when their energy reaches 150. This aggressive behavior is essential for maintaining population control over both Engineers and MaintenanceBots.

### 2.2 Engineer

Engineers support and sustain the MaintenanceBot population. When an Engineer encounters a MaintenanceBot and has at least 115 energy, it donates energy to the bot and attempts to escape. If escape fails, the Engineer allows itself to be defeated peacefully. Engineers reproduce at 150 energy, allowing them to maintain a stable population. Their presence is imporant to the ecosystem balance because without Engineers providing energy support to MaintenanceBots, Commander populations grow unchecked and eventually dominate the station. 


### 2.3 Other Classes (if modified or relevant)

N/A - kept logic consistent. 

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

| Entity | Role | Energy Strategy | Reproduction Threshold | Fight Behavior |
|--------|------|-----------------|----------------------|----------------|
| PowerCell | Energy producer | Solar charging (+1/step) | N/A | Never fights |
| MaintenanceBot | Autonomous worker | Algorithm movement, gains energy from combat | 150 | Always fights all entities |
| Commander | Apex predator / Population control | Aggressive patrol (walk + run), gains energy from combat | 150 | Always fights |
| Engineer | Support | Systematic row pattern with rest cycles, transfers energy to MaintenanceBots | 150 | Fights PowerCells and other Engineers |

### 4.2 Balance and Tuning

_Describe the process you went through to balance your ecosystem. Answer questions like:_

- _What initial entity counts did you start with?_
- _What happened in your first attempts? (e.g., all entities died, one type dominated, etc.)_
- _What parameters or behaviors did you adjust to fix it?_
- _What seed value and entity counts produce a stable run of 500+ steps?_ 

I orginally started with 100 Powercells, 10 commanders, 10 engineers, and 10 maintenances. However, I found that commanders were dominating the population and would cause the other Entities to go extinct. In order to tune this, I decreased the reproduction energy of the Commanders. At first, I believed that giving them a high reproduction limit would slow their growth, but it would cause them to dominate. Whenever, I gave the same reproduction minimum to all entities, it was much more stable.  
I used these paramters to achive a stable run of 1500 steps. 
seed 1
make PowerCell 200
make Engineer 15
make MaintenanceBot 15
make Commander 10 


### 4.3 Failure Modes

_Describe at least two failure modes you observed during development — situations where the ecosystem collapsed. For each, explain what caused the collapse and what you changed to prevent it._

1. **Failure mode 1:** Engineers being fully peaceful entities. At first, my intention was to make engineers provide support to both Commanders and MaintennceBots. However, in order to donate energy, they also are given the chance to avoid the fight. Therefore, they had no natural predators and would dominate the population. Allowing them to fight eachother, helped keep the population more stable. 
2. **Failure mode 2:** Commanders are supposed to be "leaders" and provide population control to other entities. Therefore, at first I attempted to make them stronger than other entites and put a high minimum reproduction. However, this did not work out and they would usually cause other entites to be wiped out. To fix this, I made them reproduce at the same energy as other entities . 

---

## 5. Testing
For every function in Entities, I created a corresponding Junit file testing basic behavior as well as edge cases. 
This included empty, null, or wrong inputs.  
---

## 6. Challenges and Lessons Learned

The hardest part of this lab was understanding where to start. Looking at the Entity class initially was overwhelming, and there were so many methods,  relationships, and behaviors to understand. Furthermore, I understand and can explain the pillars of OOP, however I had not yet practiced implementing them in any code. It was confusing at first to come up with abstract classes, or functions like runStats that were going to be overriden by its subclasses.  

I now have a much better understanding of the benefits of OOP. Furthermore, if I had to do this lab again, I would write down all the classes and try to understand how they work togther before attempting to start coding.
