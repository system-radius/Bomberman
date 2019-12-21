# Bomberman

## Game logic
There are N players (human or AI, tho humans are first priority), that can place bombs in a field. The goal is to kill other players and be the last one standing. (There can be other modes in the future, e.g. team deathmatch, capture the flag)

## Game components
### Board
### Entities (game objects)
- player
- Blocks
  - bomb (blocks players, triggered by other bombs, destroyed after N seconds)
  - soft blocks (blocks that are destroyed after one hit)
  - hard blocks (blocks that are destroyed after N hits)
  - perma blocks (blocks surrounding the stage)
  - power ups (has special effect for players, destroyed if hit / consumed)

---
### Board
What can a board do?
- Load playing field
- Render objects
- Update objects
- Add block/s
- Remove block/s (called every update, remove the blocks that have 0 hit points)

What does a board have?
- Blocks (list; only the game-generated blocks are included in the list, bombs are tracked in the players' lists)
- Players (list; contains the currently active players)
- Lost players (list; may leave the game)
- AIs (list; the list of active AIs, game-controlled players)
- Width (int; the playing width of the board, does not reflect the actual size of the objects but this is represented in the basic unit, may be set)
- Height (int; the playing height of the board, does not reflect the actual size of the object but this is represented in the basic unit, may be set)
- Size (float; multiplicative size of the objects, may be set)

---
### Game Objects
`// GameObject class is an abstract class and cannot do anything on its own, just a convenient way to allow objects to interact with each other`
What can game objects do?
- Move (depending on their velocity)
- Update
- Render
- Receive damage (deducts the hit points for the game object)

What do game objects have?
- X coordinate (float)
- Y coordinate (float)
- X velocity (float; the rate at which the object can move along X-axis)
- Y velocity (float; the rate at which the object can move along Y-axis)
- Hit points (int; the number of hits an object can take before being destroyed)

---
### Player
`// Inherits from the GameObject`
What can a player do?
- Place bomb (on click of a button /  key, place a bomb on the current player's rounded position)
- Remove bomb (called upon a player's bomb being destroyed / triggered, regardless of where the destruction came from)
- Trigger bomb (Used if player has mine bomb type, explode the bomb at will, starting from the first bomb placed)
- Collide (compute the position of the player against the blocks so that they cannot pass them unless destroyed)
- Die (Called if the player is hit by fire, decrease the player's life by 1)
- Win (Called if the player is the victor)
- Lose (Called if the player has lost)
- Idle (low prio, called if the player has not taken any action after a certain amount of time)
- Consume power ups (called if the player collides with a power up)
- Respawn (called after the player has died after N amount of time)
- Activate skill (unique per player)

What does a player have?
- Movement speed (float // precise computation)
- bomb type (enum: normal, mine, pierce, power, all?) // all bomb type is an ability
- Bombs (int, cap?)
- Activated bombs (list)
- Fire power (int, cap?)
- Lives (int)
- State (enum: dying, dead, winning, losing, alive)
- Push bomb (boolean, false by default, activated if the player gets the push_bomb power up)

---
### Blocks
`Inherits from the GameObject`
What can blocks do?
- Collide with player (Basically, if it is a block, it can collide with a player)

What do blocks have?
- `// nothing on its own`

---
#### Bombs
`// Inherits from Block class`
What can bombs do?
- Explode (inflict damage to other objects basing on the range of fire power)

What do bombs have?
- State (enum: counting_down (default), exploding, failed, waiting)
- Fire power (int; the range of the explosion for the bomb, set on create of instance)

---
#### Soft blocks
`// Inherits from Block class`
What can soft blocks do?
- What normal blocks do

What do soft blocks have?
- 1 hit point
- Different sprite than that of other blocks

---
#### Hard blocks
`// Inherits from Block class`
What can hard blocks do?
- What normal blocks do

What do hard blocks have?
- Much more hit points than soft blocks.
- Different sprite than that of other blocks

---
#### Perma blocks
`// if the Block class is not abstract, then perma block and Block are one and the same`
What can perma blocks do?
- What normal blocks do
- Cannot be destroyed (override receive damage method to cancel any damage being received)

What do perma blocks have?
- Infinite hit-points
- Different sprite than that of other blocks

---
#### Power ups
`// Inherits from Block class`
What can power ups do?
- Receives damage from fire
- Gets consumed from collision with player (after consumption, the power up receives damage to remove it from the game)

What do power ups have?
- Effect (enum)
  - add_bomb (+1 to the max limit of bombs a player can carry)
  - add_fire (+1 to the max fire power range of the player)
  - add_speed (+1 to player's movement speed)
  - add_life (+1 to player's life)
  - change_mine (changes the bomb type to mine; triggered explosion, explosion at will)
  - change_pierce (changes the bomb type to pierce; piercing explosion, cannot be blocked)
  - change_power (changes the bomb type to power; less range, AoE explosion, cannot be blocked)
  - push_bomb (enables the player to push bombs)
