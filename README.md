# Informatics Large Practical

This was a coursework project where we were tasked on writing code that parses a geo-json map and controls a virtual drone to pick up coins from virtual stations around campus.

The project required us to create two drones. One which used random movements to move with a limited lookahead and no memory and one that could store any information it needed and could view the entire map.

## Implementation

The random drone is fairly simple moving in any direction that isn't detrimental to its score while preferring directions that give a positive score. The second drone selects the nearest positive valued station and uses A* search to find the optimal path to its target.
