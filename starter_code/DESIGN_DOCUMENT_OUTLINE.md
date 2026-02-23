# Lab 3 Design Document: Space Station Simulation

**Name:** Rafael Perales 
**EID:**  Rp37497
**Date:** 2/23/2026

---

## 1. System Overview

_In 2–3 paragraphs, describe your simulation at a high level. What does it do? What is the design goal you were working toward (self-sustaining ecosystem)? Did your final simulation achieve a stable equilibrium, and if so, under what conditions (entity counts, seed values, number of steps)?_

---

## 2. Class Descriptions

For each class you implemented, provide:
- A brief description of the class's purpose and responsibilities
- Key design decisions you made and why
- How it interacts with other classes in the system

### 2.1 Commander

_What role does the Commander play in your space station ecosystem? How does it move, when does it fight, and under what conditions does it reproduce? Why did you choose this behavior — what problem does it solve for the station's long-term survival?_

### 2.2 Engineer

_Same as above for your Engineer entity. How does its behavior differ from the Commander's, and why do both roles need to exist? What happens to the ecosystem if you remove Engineers entirely?_

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

| Entity | Role | Energy Strategy | Reproduction Threshold | Fight Behavior |
|--------|------|-----------------|----------------------|----------------|
| PowerCell | Energy producer | Solar charging (+1/step) | N/A | Never fights |
| MaintenanceBot | | | |Always Fight |
| Commander | Keep Population Stable |  | | Always Fight |
| Engineer | | | | Never Fight |

### 4.2 Balance and Tuning

_Describe the process you went through to balance your ecosystem. Answer questions like:_

- _What initial entity counts did you start with?_
- _What happened in your first attempts? (e.g., all entities died, one type dominated, etc.)_
- _What parameters or behaviors did you adjust to fix it?_
- _What seed value and entity counts produce a stable run of 500+ steps?_

### 4.3 Failure Modes

_Describe at least two failure modes you observed during development — situations where the ecosystem collapsed. For each, explain what caused the collapse and what you changed to prevent it._

1. **Failure mode 1:** _description_
2. **Failure mode 2:** _description_

---

## 5. Testing

_Describe how you tested your implementation beyond the 5 provided tests. What edge cases did you consider? What commands did you run manually to verify behavior? If you wrote any additional test input files, describe what they test._

---

## 6. Challenges and Lessons Learned

_What was the hardest part of this lab? What would you do differently if you started over? What did you learn about object-oriented design from this experience?_
