# Project 3: Turing Machine simulator

* Author: Anup Bhattarai, Daniel Aguilar Carranza
* Class: CS361 Section 2
* Semester: Fall 2025

## Overview

This Java application simulates a deterministic bi-infinite Turing Machine. 
The program reads an encoded Turing Machine description and input string from 
a file, simulates the machine's execution until it reaches the halting state, 
and outputs the contents of all visited tape cells to stdout.

## Compiling and Using

To compile, execute the following command in the main project directory:
```
$ javac tm/*.java
```

Run the compiled class with the command:
```
$ java tm.TMSimulator <input_file>
```
