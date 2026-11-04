# UrArs

> A data-driven Minecraft server-side modding framework for creating custom entities, skills, items, and gameplay mechanics through configurable scripts.

## Requirements

- Java `25`
- Gradle `9.4.1`
- Minecraft `>26.1`
- Fabric Loader `>0.19.2`

## Implemented Features

- [x] Data-Driven Custom Mob System
- [x] Skill Engine
- [x] Custom File Parser
- [x] Math Expression Parser
- [x] Custom AI Goal System

## Planned Features

- [ ] Custom Mob Equipment
- [ ] Custom Item System
- [ ] Mob Drop Table System
- [ ] Skill Engine Expansion
- [ ] Parser Improvements
- [ ] Architecture Refactoring
- [ ] Unit & Integration Tests
- [ ] User Documentation

## Development

UrArs is currently under active development. Existing systems are implemented, while new gameplay features, integrations, and internal improvements are being developed.

Features and APIs may change during development.

### General Improvement
- [ ] Code Readability Enchantment
  - [ ] Skill
  - [ ] Mob
  - [ ] Parser

### Skill Engine
- [ ] Skill Properties
    - [ ] Hidden in command
    - [ ] Cooldown
    - [ ] Global Cooldown

- [ ] Skill Execution Enhancement
    - [ ] Skill Execution Context Improvement
    - [x] Nested Skill Execution
    - [x] Delayed Skill Execution
    - [x] Repeating Skill Execution
    - [ ] Skill Cancellation
    - [x] Execution State Management

- [ ] Skill Targeting Enhancement
    - [ ] Target Filtering
    - [ ] Target Conditions
    - [ ] Target Sorting
    - [ ] Target Limiting
    - [ ] Target Chaining

- [ ] Skill Mechanics Expansion
    - [x] Entity Mechanics
    - [x] Player Mechanics
    - [ ] World Mechanics
    - [ ] Projectile Mechanics
    - [x] Aura Mechanics
    - [ ] Potion Effect Mechanics
    - [ ] Attribute Mechanics

- [ ] Skill Conditions Expansion
    - [x] Entity Conditions
    - [x] Player Conditions
    - [ ] World Conditions
    - [x] Location Conditions

- [ ] Skill Trigger Expansion
    - [x] Entity Events
    - [ ] Player Events
    - [ ] World Events
    - [ ] Item Events
    - [ ] Custom Events

- [ ] Skill Integration
    - [ ] Custom Mob Integration
    - [ ] Custom Item Integration
    - [ ] Drop Table Integration

### Custom Mob System
- [ ] Mob Properties
    - [ ] ... 

- [ ] BetterModel Compatibility  
    - [x] Model Properties
    - [x] BetterModel Mechanics
    - [ ] BetterModel Conditions
    - [ ] BetterModel Targeter

- [ ] Custom Mob AI Goal
    - [x] Goal Registry
    - [x] Target Goal Registry
    - [x] Annotation-based Goal Registration
    - [x] Configurable Vanilla AI Goals
    - [x] Support Custom Goal Implementation
    - [x] Goal Priority Configuration
    - [ ] Goal Runtime Management

- [ ] Mob Attribute
    - [ ] Attribute Modifier Configuration
    - [ ] Dynamic Attribute Modification
    - [ ] Attribute Reset and Removal

- [ ] Mob Equipment
    - [ ] Equipment Slot Configuration
    - [ ] Equipment Assignment
    - [ ] Equipment Drop Chance

- [ ] Mob Loot
    - [ ] Drop Table Registry
    - [ ] Custom Drop Table Configuration
    - [ ] Multiple Drop Entries
    - [ ] Drop Chance
    - [ ] Min/Max Drop Amount
    - [ ] Conditional Drops
    - [ ] Weighted Random Drops
    - [ ] Drop Table References

### Custom Item System
- [ ] Custom Item Registration
    - [ ] Item Registry
    - [ ] Annotation-based Item Registration
    - [ ] Item Factory
    - [ ] Item Configuration

- [ ] Item Properties
    - [ ] Display Name
    - [ ] Lore
    - [ ] Custom Model Data / Item Model
    - [ ] Item Attributes
    - [ ] Item Rarity
    - [ ] Item Stack Size
    - [ ] Item Durability
    - [ ] ...

- [ ] Item Mechanics
    - [ ] Item Interaction
    - [ ] Item Right Click
    - [ ] Item Left Click
    - [ ] Item Cooldown
    - [ ] Item Skill Integration

- [ ] Item Compatibility
    - [ ] Custom Item Identification
    - [ ] Item Serialization
    - [ ] Item Deserialization
    - [ ] Item Persistence

### Custom File Parser
- [ ] Parser Error Reporting
    - [ ] Detailed Syntax Error
    - [ ] Line and Column Tracking
    - [ ] Error Context Display

- [ ] Parser Validation
    - [ ] Invalid Flag Validation
    - [ ] Invalid Argument Validation
    - [ ] Duplicate Key Validation
    - [ ] Invalid Context Validation

- [ ] Parser Improvement
    - [ ] BufferedString Optimization
    - [ ] Reduce Unnecessary Allocation
    - [ ] Parser Performance Optimization
    - [ ] Improve Parser Readability
    - [ ] Improve Parser Maintainability

### Condition Expression Parser
- [ ] Logical Operators
    - [ ] AND Operator
    - [ ] OR Operator
    - [ ] NOT Operator
    - [ ] Short-Circuit Evaluation

- [ ] Expression Parsing
    - [ ] Pratt Parser Implementation
    - [ ] Operator Precedence
    - [ ] Parenthesized Expressions
    - [ ] Nested Expressions
    - [ ] Comparison Operators
    - [ ] Condition Function Calls

- [ ] Condition Evaluation
    - [ ] Boolean Expression Evaluation
    - [ ] Condition Context Management
    - [ ] Condition Result Handling
    - [ ] Evaluation Error Reporting

- [ ] Integration
    - [ ] Skill Conditions Integration
    - [ ] Target Conditions Integration
    - [ ] Mob Conditions Integration