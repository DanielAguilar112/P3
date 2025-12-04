package tm;

import java.io.*;

/**
 * Main class for simulating a Turing Machine with bi-infinite tape.
 * Reads TM encoding from file and simulates execution until halting state.
 * 
 * @author Anup Bhattarai, Daniel Aguilar
 */
public class TMSimulator {
    
    /**
     * Main entry point for the TM simulator.
     * Accepts a single command line argument specifying the input file
     * containing the Turing Machine encoding and input string.
     * 
     * @param args command line arguments - expects filename as first argument
     */
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: java tm.TMSimulator <input_file>");
            System.exit(1);
        }
        
        try {
            TuringMachine tm = new TuringMachine(args[0]);
            tm.simulate();
            tm.printTape();
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
            System.exit(1);
        }
    }
}