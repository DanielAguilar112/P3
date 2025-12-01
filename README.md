PROJECT 2: Turing Machine simulator
=================================

Authors:
Anup Bhattara
Daniel Aguilar Carranza

---------------------------------
1. PROJECT OVERVIEW
---------------------------------

This project implements a deterministic bi-infinite Turing Machine simulator in Java.
The simulator reads an encoded Turing Machine description and an input string from a file, initializes an infinitely extendable tape in both directions, and simulates the machine until it reaches the halting state (the state with the largest label).

At the end of the simulation, the program prints all visited tape cells, in order, to stdout.

The simulator is designed for both correctness and performance, as required by the project rubric and the provided large-scale test files.
The core functionalities implemented are:


---------------------------------
2. COMPILATION INSTRUCTIONS
---------------------------------

rm *.class
javac tm/*.java


---------------------------------
3. EXECUTION INSTRUCTIONS
---------------------------------

java tm.TMSimulator <inputfile>
