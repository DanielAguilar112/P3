package tm;

import java.io.*;
import java.util.*;

/**
 * Represents a Turing Machine with bi-infinite tape.
 * Uses efficient data structures for tape representation and transition lookup.
 * Simulates the TM execution until reaching the halting state.
 * 
 * @author Anup Bhattarai, Daniel Aguilar
 */
public class TuringMachine {
    
    private int numStates;
    private int numSymbols;
    private int haltState;
    private int currentState;
    private int headPosition;
    
    /** 
     * Tape representation using TreeMap for efficient sparse storage.
     * Only stores non-blank (non-zero) cells.
     * Key: position on tape, Value: symbol at that position
     */
    private TreeMap<Integer, Integer> tape;
    
    /** 
     * Transition function stored as 2D array for O(1) lookup.
     * transitions[state][symbol] = Transition
     */
    private Transition[][] transitions;
    
    /** Tracks the leftmost visited position */
    private int minVisited;
    
    /** Tracks the rightmost visited position */
    private int maxVisited;
    
    /**
     * Constructs a Turing Machine by parsing the input file.
     * Initializes the tape, transition function, and starting configuration.
     * 
     * @param filename the name of the file containing TM encoding
     * @throws IOException if file cannot be read
     */
    public TuringMachine(String filename) throws IOException {
        tape = new TreeMap<>();
        currentState = 0; // Start state is always 0
        headPosition = 0; // Start at position 0
        minVisited = 0;
        maxVisited = 0;
        
        parseInputFile(filename);
    }
    
    /**
     * Parses the input file to build the TM configuration.
     * Reads number of states, symbols, transition function, and input string.
     * 
     * @param filename the input file name
     * @throws IOException if file cannot be read
     */
    private void parseInputFile(String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        
        // Read number of states
        numStates = Integer.parseInt(reader.readLine().trim());
        haltState = numStates - 1;
        
        // Read number of symbols in input alphabet
        numSymbols = Integer.parseInt(reader.readLine().trim());
        
        // Total symbols in tape alphabet = {0} ∪ Σ
        int tapeAlphabetSize = numSymbols + 1;
        
        // Initialize transition table
        // Only need transitions for non-halting states
        transitions = new Transition[numStates - 1][tapeAlphabetSize];
        
        // Read transitions
        // Number of transition lines = (numStates - 1) * tapeAlphabetSize
        for (int state = 0; state < numStates - 1; state++) {
            for (int symbol = 0; symbol < tapeAlphabetSize; symbol++) {
                String line = reader.readLine().trim();
                String[] parts = line.split(",");
                
                int nextState = Integer.parseInt(parts[0].trim());
                int writeSymbol = Integer.parseInt(parts[1].trim());
                boolean moveRight = parts[2].trim().equals("R");
                
                transitions[state][symbol] = new Transition(nextState, writeSymbol, moveRight);
            }
        }
        
        // Read input string
        String inputString = reader.readLine();
        if (inputString != null && !inputString.trim().isEmpty()) {
            // Initialize tape with input string
            for (int i = 0; i < inputString.length(); i++) {
                int symbol = Character.getNumericValue(inputString.charAt(i));
                tape.put(i, symbol);
            }
            maxVisited = inputString.length() - 1;
        }
        
        reader.close();
    }
    
    /**
     * Reads the symbol at the current head position.
     * Returns 0 (blank) if position has not been written to.
     * 
     * @return the symbol at current head position
     */
    private int readTape() {
        return tape.getOrDefault(headPosition, 0);
    }
    
    /**
     * Writes a symbol to the current head position.
     * If writing a blank (0), removes the entry to keep tape sparse.
     * 
     * @param symbol the symbol to write
     */
    private void writeTape(int symbol) {
        if (symbol == 0) {
            tape.remove(headPosition); // Remove blanks to keep tape sparse
        } else {
            tape.put(headPosition, symbol);
        }
    }
    
    /**
     * Simulates the Turing Machine until it reaches the halting state.
     * Executes transitions by reading current symbol, writing new symbol,
     * moving the tape head, and transitioning to the next state.
     */
    public void simulate() {
        while (currentState != haltState) {
            // Read current symbol
            int currentSymbol = readTape();
            
            // Get transition for current state and symbol
            Transition t = transitions[currentState][currentSymbol];
            
            // Write symbol to tape
            writeTape(t.getWriteSymbol());
            
            // Move head left or right
            if (t.isMoveRight()) {
                headPosition++;
            } else {
                headPosition--;
            }
            
            // Update visited bounds for output
            if (headPosition < minVisited) {
                minVisited = headPosition;
            }
            if (headPosition > maxVisited) {
                maxVisited = headPosition;
            }
            
            // Transition to next state
            currentState = t.getNextState();
        }
    }
    
    /**
     * Prints the contents of visited tape cells to stdout.
     * Outputs only the range from minVisited to maxVisited,
     * followed by a newline.
     */
    public void printTape() {
        StringBuilder output = new StringBuilder();
        
        for (int i = minVisited; i <= maxVisited; i++) {
            output.append(tape.getOrDefault(i, 0));
        }
        
        System.out.println(output.toString());
    }
}