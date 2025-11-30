package tm;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Collections;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Represents a single transition rule for the Turing Machine.
 * Uses a record for concise, immutable data storage (Java 16+ feature).
 * This satisfies the OO principle of having a dedicated class for a TM component.
 */
record Transition(int nextState, int writeSymbol, char move) {
    /**
     * @param nextState The state the machine transitions to.
     * @param writeSymbol The symbol to write to the tape cell (0 is blank).
     * @param move The direction to move the head ('L' or 'R').
     */
}

/**
 * Encapsulates the bi-infinite deterministic Turing Machine (TM) logic.
 * This satisfies the OO principle of having a dedicated class for the TM entity.
 */
class TuringMachine {
    private final int numStates;
    private final int numSymbols;
    private final Transition[][] transitions; // transitions[currentState][currentSymbol]
    private final Map<Long, Integer> tape;  // key: position (long), value: symbol (int)

    private long headPosition = 0;
    private int currentState = 0;
    private long minVisitedIndex = 0;
    private long maxVisitedIndex = 0;

    /**
     * Constructs a TuringMachine instance.
     * @param numStates Total number of states (0 to numStates - 1).
     * @param numSymbols Number of symbols in the input alphabet Sigma (1 to numSymbols).
     * @param transitions The parsed transition function.
     * @param initialInput The input string to place on the tape, centered at index 0.
     */
    public TuringMachine(int numStates, int numSymbols, Transition[][] transitions, String initialInput) {
        this.numStates = numStates;
        this.numSymbols = numSymbols;
        this.transitions = transitions;
        this.tape = new HashMap<>();

        // Initialize the tape with the input string
        for (int i = 0; i < initialInput.length(); i++) {
            // Symbols are 1-based, so '1' is 1, '2' is 2, etc.
            // We assume input symbols are single digits, which is safe since max |Sigma| is 9.
            int symbol = Character.getNumericValue(initialInput.charAt(i));
            if (symbol != 0) { // Do not store explicit blanks (0)
                 tape.put((long) i, symbol);
            }
        }
        // If there's input, the bounds are set by the input length.
        if (initialInput.length() > 0) {
            maxVisitedIndex = initialInput.length() - 1;
        }
        
    }

    /**
     * Runs the TM simulation until the halting state is reached.
     */
    public void run() {
        // Halting state is the state with the largest label: |Q| - 1
        final int haltingState = numStates - 1;
        
        while (currentState != haltingState) {
            // 1. Read the symbol under the head. Blank is 0 (the default value).
            int currentSymbol = tape.getOrDefault(headPosition, 0);

            // 2. Lookup the transition (ensure transition is defined for non-halting state)
            if (currentState >= haltingState) {
                // This should not happen based on problem specs, but good for robustness
                System.err.println("Error: Attempted transition from halting state " + currentState);
                break;
            }

            Transition t = transitions[currentState][currentSymbol];
            if (t == null) {
                System.err.println("Error: No transition found for state " + currentState + " and symbol " + currentSymbol);
                break;
            }

            // 3. Apply the transition
            
            // a. Write symbol
            int writeSymbol = t.writeSymbol();
            if (writeSymbol == 0) {
                // Write 0 (blank) -> remove from the sparse map
                tape.remove(headPosition);
            } else {
                // Write non-blank -> put/update in the map
                tape.put(headPosition, writeSymbol);
            }

            // b. Change state
            currentState = t.nextState();

            // c. Move head
            if (t.move() == 'R') {
                headPosition++;
            } else if (t.move() == 'L') {
                headPosition--;
            } else {
                System.err.println("Error: Invalid move direction: " + t.move());
                break;
            }

            // 4. Update visited bounds
            if (headPosition < minVisitedIndex) {
                minVisitedIndex = headPosition;
            }
            if (headPosition > maxVisitedIndex) {
                maxVisitedIndex = headPosition;
            }
        }
    }

    /**
     * Prints the content of all visited tape cells from minVisitedIndex to maxVisitedIndex.
     */
    public void printTapeContents() {
        if (minVisitedIndex > maxVisitedIndex) {
             // This happens if the machine never moves (e.g., input "1", 0 steps).
             // In this case, we only print the initial cell or bounds.
             // Given the problem implies movement, we'll ensure at least one cell is shown.
             if (tape.isEmpty() && minVisitedIndex == 0 && maxVisitedIndex == 0) {
                 // Empty tape, 0 moves. Still print the blank content of the current cell.
                 System.out.println(0);
                 return;
             }
        }
        
        StringBuilder output = new StringBuilder();
        // Iterate over the full range of visited indices
        for (long i = minVisitedIndex; i <= maxVisitedIndex; i++) {
            // Get the symbol, defaulting to 0 (blank) if not explicitly stored
            output.append(tape.getOrDefault(i, 0));
        }
        System.out.println(output.toString());
    }
}

/**
 * The main class for the Turing Machine Simulator.
 */
public class TMSimulator {

    /**
     * Entry point of the simulator.
     * @param args The command line arguments, expecting a single file path.
     */
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: java tm.TMSimulator <input_file>");
            return;
        }
        String filename = args[0];

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            // 1. Read Machine Specifications
            
            // Line 1: Total number of states |Q|
            String line = reader.readLine();
            if (line == null) {
                System.err.println("Error: Input file is empty (missing numStates).");
                return;
            }
            int numStates = Integer.parseInt(line.trim()); // 0 is start, numStates - 1 is halt
            
            // Line 2: Number of symbols in Sigma |Σ| (1 to m)
            line = reader.readLine();
            if (line == null) {
                System.err.println("Error: Input file missing numSymbols.");
                return;
            }
            int numSymbols = Integer.parseInt(line.trim());
            
            int tapeAlphabetSize = numSymbols + 1; // |Γ| = |Σ| + 1 (for blank symbol 0)
            int numNonHaltingStates = numStates - 1;
            int totalTransitions = numNonHaltingStates * tapeAlphabetSize;

            // Initialize transition array: transitions[currentState][currentSymbol]
            Transition[][] transitions = new Transition[numNonHaltingStates][tapeAlphabetSize];

            // 2. Parse Transitions
            for (int i = 0; i < totalTransitions; i++) {
                line = reader.readLine();
                if (line == null) {
                    System.err.println("Error: Input file ended prematurely (missing transitions).");
                    return;
                }
                
                // Transition line format: next state, write symbol, move (e.g., 1,1,R)
                String[] parts = line.trim().split(",");
                if (parts.length != 3) {
                    System.err.println("Error: Invalid transition format on line " + (i + 3) + ": " + line);
                    return;
                }
                
                try {
                    int nextState = Integer.parseInt(parts[0].trim());
                    int writeSymbol = Integer.parseInt(parts[1].trim());
                    char move = parts[2].trim().toUpperCase().charAt(0);
                    
                    int currentState = i / tapeAlphabetSize;
                    int currentSymbol = i % tapeAlphabetSize;
                    
                    transitions[currentState][currentSymbol] = new Transition(nextState, writeSymbol, move);

                } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                    System.err.println("Error parsing transition values on line " + (i + 3) + ": " + line + ". " + e.getMessage());
                    return;
                }
            }

            // 3. Read Input String (last line)
            // Read all remaining lines until EOF or a non-null line is found
            StringBuilder inputBuilder = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                // The last line contains the input string (possibly blank/empty)
                inputBuilder.append(line);
            }
            String initialInput = inputBuilder.toString().trim();
            // Handle the case where the input file might contain a blank line as the last line (epsilon input)
            // Since we read until EOF and trim, an empty last line results in an empty string.

            // 4. Create and Run TM
            TuringMachine tm = new TuringMachine(numStates, numSymbols, transitions, initialInput);
            
            // Use system time for performance measurement (optional, but good for efficiency focus)
            long startTime = System.currentTimeMillis();
            
            tm.run();
            
            long endTime = System.currentTimeMillis();
            System.err.println("Simulation time: " + (endTime - startTime) + "ms"); // Print to stderr for debugging/timing
            
            // 5. Output Results
            tm.printTapeContents();

        } catch (IOException e) {
            System.err.println("Error reading file " + filename + ": " + e.getMessage());
        } catch (Exception e) {
            System.err.println("An unexpected error occurred during simulation: " + e.getMessage());
            e.printStackTrace();
        }
    }
}