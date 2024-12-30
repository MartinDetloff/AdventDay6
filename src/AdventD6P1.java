import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

public class AdventD6P1 {
    BufferedReader bufferedReader;
    ArrayList<ArrayList<Character>> mapOfSearchArea = new ArrayList<>();
    ArrayList<Integer> guardInitialPosition = new ArrayList<>();
    ArrayList<Integer> guardCurrentPosition = new ArrayList<>();
    int dimensions = 130;
    int totalCount = 0;
    ArrayList<ArrayList<Integer>> distinctPositions = new ArrayList<>();
    Direction currDirection = Direction.UP;


    // default constructor (Just takes in the buffered reader )
    public AdventD6P1(BufferedReader bufferedReader){
        this.bufferedReader = bufferedReader;
    }

    /**
     * Enum For the direction
     */
    public enum Direction {
        RIGHT, LEFT, UP, DOWN;
    }


    /**
     * Method to find and return the position of the guard.
     * @return the position of the guard as an arraylist
     * @throws IOException just in case :D
     */
    private void findGuard() throws IOException {
        String line = "";
        int lineNum = 0;

        ArrayList<Integer> posOfGuard = new ArrayList<>(); // this is to store the pos of the guard

        while ((line = bufferedReader.readLine()) != null){
            mapOfSearchArea.add(new ArrayList<>());

            goThroughLine(line, lineNum); // go through the line, add it to the list, and find the guard pos

            lineNum++; // increment the line number
        }
    }


    /**
     * Method to find the guard, and add everything else to the list
     * @param line the given line
     */
    private void goThroughLine(String line, int lineNum){
        char[] list = line.toCharArray();
        int colNum = 0;

        for (Character c : list){
            // add it to the map
            mapOfSearchArea.get(lineNum).add(c);

            if (c == '^'){
                // maybe return the position, and go the
                // direction that the arrow is pointing until you hit something
                guardInitialPosition.add(0, lineNum);
                guardInitialPosition.add(1, colNum);

                guardCurrentPosition.add(0, lineNum);
                guardCurrentPosition.add(1, colNum);
                System.out.println("The Guards initial position is row: " + lineNum + " col: " + colNum);
                System.out.println("This is the line ");
                System.out.println(line);
                System.out.println("This is the length of the line: " + line.length());
            }
            colNum++;
        }
    }

    /**
     * Method to follow the path of the guard up
     */
    private boolean followPath(Direction direction){
        int currentRow = guardCurrentPosition.get(0);
        int currentCol = guardCurrentPosition.get(1);
        ArrayList<Integer> directionToGo = directionToGo(currDirection);


        while (mapOfSearchArea.get(currentRow).get(currentCol) != '#'){

            // check if the position is unique or not
            if (!positionAlreadyVisited(currentRow, currentCol)){ // if it hasn't been visited we add it to the arraylist
                ArrayList<Integer> newPosition = new ArrayList<>();
                newPosition.add(currentRow);
                newPosition.add(currentCol);
                distinctPositions.add(newPosition);
            }

            if (weAreGoingToLeave(currDirection, currentRow, currentCol)){
                System.out.println("We just left out of the map in the " + currDirection);
                return true;
            }

            currentRow += directionToGo.get(0); // this is to go up
            currentCol += directionToGo.get(1); // this is to go up
        }

        if (mapOfSearchArea.get(currentRow).get(currentCol) == '#'){
            // rotate the thing 90 degrees
            findDirectionToGoNow(currDirection, currentRow, currentCol);
        }

        System.out.println(currentRow);

        return false;
    }

    /**
     * Method to check if we are going to leave the map
     * @param direction the direction we are going
     * @param currentRow the current row
     * @param currentCol the current col
     * @return true if we are going to leave. False if not
     */
    private boolean weAreGoingToLeave(Direction direction, int currentRow, int currentCol){
        switch (direction){
            case LEFT -> {
                if (currentCol == 0){
                    return true;
                }
            }
            case UP -> {
                if (currentRow == 0){
                    return true;
                }
            }
            case DOWN -> {
                if (currentRow == dimensions - 1){
                    return true;
                }
            }
            case RIGHT -> {
                if (currentCol == dimensions - 1){
                    return true;
                }
            }
        }
     return false;
    }

    /**
     * Method to rotate the direction 90 degrees to find the new direction to go
     * @param direction the current direction
     */
    private void findDirectionToGoNow(Direction direction, int currentRow, int currentCol){
        guardCurrentPosition.clear();
        switch (direction){
            case UP -> {
                guardCurrentPosition = new ArrayList<>(Arrays.asList(currentRow + 1, currentCol));
                currDirection = Direction.RIGHT;
            }
            case DOWN -> {
                guardCurrentPosition = new ArrayList<>(Arrays.asList(currentRow - 1, currentCol));
                currDirection = Direction.LEFT;
            }
            case LEFT -> {
                guardCurrentPosition = new ArrayList<>(Arrays.asList(currentRow, currentCol + 1));
                currDirection = Direction.UP;
            }
            case RIGHT -> {
                guardCurrentPosition = new ArrayList<>(Arrays.asList(currentRow, currentCol - 1));
                currDirection = Direction.DOWN;
            }
        }

    }

    /**
     * Helper method to return the direction that we should go
     * @param direction the direction currently
     * @return the direction
     */
    private ArrayList<Integer> directionToGo(Direction direction){
        switch (direction){
            case UP -> {
                System.out.println("Found to be up");
                return new ArrayList<>(Arrays.asList(-1, 0));
            }
            case DOWN -> {
                System.out.println("Found to be down");
                return new ArrayList<>(Arrays.asList(1, 0));
            }
            case LEFT -> {
                System.out.println("Found to be left");
                return new ArrayList<>(Arrays.asList(0, -1));
            }
            case RIGHT -> {
                System.out.println("Found to be right");
                return new ArrayList<>(Arrays.asList(0, 1));
            }
        }
        System.out.println("RETURNING NULL");
        return null;
    }


    /**
     * Small helper method to check for all the positions
     * @param currentRow the current row
     * @param currentCol the current col
     * @return true if the pos is already visited, false if not
     */
    private boolean positionAlreadyVisited(int currentRow, int currentCol){
        for (ArrayList<Integer> position : distinctPositions){
            if (position.get(0) == currentRow &&
                position.get(1) == currentCol){
                return true;
            }
        }
        return false;
    }


    public static void main(String[] args) throws IOException {
        // first we need to read the file, and find the position of the guard.
        // I think the second puzzle will be using the given positions or something,
        // so it would be nice to on top of counting, store all the positions somewhere
        File file = new File("C:\\Users\\19365\\OneDrive\\Documents\\GitHub\\AdventDay6\\src\\input.txt");

        BufferedReader br = new BufferedReader(new FileReader(file));

        AdventD6P1 adventD6P1 = new AdventD6P1(br);

        adventD6P1.findGuard();

        while (!adventD6P1.followPath(adventD6P1.currDirection)){
            System.out.println(adventD6P1.currDirection);
            System.out.println("Turning");
        }

        System.out.println("This is the distinct positions: " + adventD6P1.distinctPositions);
        System.out.println("This is the length : " + adventD6P1.distinctPositions.size());
    }
}
