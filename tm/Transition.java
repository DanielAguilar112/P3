package tm;

/**
 * Represents a single transition in the Turing Machine.
 * Each transition specifies the next state, the symbol to write,
 * and the direction to move the tape head.
 * 
 * @author Anup Bhattarai, Daniel Aguilar
 */
public class Transition {
    
    private int nextState;
    private int writeSymbol;
    
    /** The direction to move: true for Right, false for Left */
    private boolean moveRight;
    
    /**
     * Creates a new transition with the specified parameters.
     * 
     * @param nextState the state to transition to
     * @param writeSymbol the symbol to write on the tape
     * @param moveRight true if moving right, false if moving left
     */
    public Transition(int nextState, int writeSymbol, boolean moveRight) {
        this.nextState = nextState;
        this.writeSymbol = writeSymbol;
        this.moveRight = moveRight;
    }
    
    /**
     * Gets the next state for this transition.
     * 
     * @return the next state number
     */
    public int getNextState() {
        return nextState;
    }
    
    /**
     * Gets the symbol to write for this transition.
     * 
     * @return the symbol to write
     */
    public int getWriteSymbol() {
        return writeSymbol;
    }
    
    /**
     * Checks if this transition moves the head to the right.
     * 
     * @return true if moving right, false if moving left
     */
    public boolean isMoveRight() {
        return moveRight;
    }
}