import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class AdventD6P2 {
    BufferedReader bufferedReader;
    ArrayList<ArrayList<Character>> mapOfSearchArea = new ArrayList<>();
    ArrayList<Integer> guardInitialPosition = new ArrayList<>();
    ArrayList<Integer> guardCurrentPosition = new ArrayList<>();
    int dimensions = 10;
    boolean isCycle = false;
    ArrayList<ArrayList<Integer>> distinctPositions = new ArrayList<>();
    Direction currDirection = Direction.UP;
    int totalPos = 0;

    // default constructor (Just takes in the buffered reader )
    public AdventD6P2(BufferedReader bufferedReader){
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
//                System.out.println("The Guards initial position is row: " + lineNum + " col: " + colNum);
//                System.out.println("This is the line ");
//                System.out.println(line);
//                System.out.println("This is the length of the line: " + line.length());
            }
            colNum++;
        }
    }

    /**
     * Method to follow the path of the guard up
     */
    private boolean followPath(Direction direction, Set<String> visitedInThisPath){

        printGrid();

//        Set<String> visitedInThisPath = new HashSet<>();

        int currentRow = guardCurrentPosition.get(0);
        int currentCol = guardCurrentPosition.get(1);



        ArrayList<Integer> directionToGo = directionToGo(currDirection);



//        Set<String> visitedInThisPath = new HashSet<>();
        while (mapOfSearchArea.get(currentRow).get(currentCol) != '#'){

            String key = currentRow + "," + currentCol;
            if (visitedInThisPath.contains(key)) {
                // We have revisited the same position: that's a cycle
                isCycle = true;
                return false;
            }
            visitedInThisPath.add(key);

            // check if the position is unique or not
            if (!positionAlreadyVisited(currentRow, currentCol)){ // if it hasn't been visited we add it to the arraylist
                ArrayList<Integer> newPosition = new ArrayList<>();
                newPosition.add(currentRow);
                newPosition.add(currentCol);
//                System.out.println("Somehow here");
                distinctPositions.add(newPosition);
            }

            if (weAreGoingToLeave(currDirection, currentRow, currentCol)){
//                System.out.println("We just left out of the map in the " + currDirection);
//                System.out.println("We left at the position ROW: " + currentRow + " COL: " + currentCol);
                return true;
            }

            currentRow += directionToGo.get(0); // this is to go up
            currentCol += directionToGo.get(1); // this is to go up
        }

        if (mapOfSearchArea.get(currentRow).get(currentCol) == '#'){
            // rotate the thing 90 degrees
            findDirectionToGoNow(currDirection, currentRow, currentCol);
        }


//        System.out.println(currentRow);

        return false;
    }

    /**
     * Method to search for cycles in the map
     */
    private void checkAllOfThePathForCycles(){

        ArrayList<ArrayList<Integer>> tempPositions = new ArrayList<>(distinctPositions);


        for (ArrayList<Integer> list : tempPositions){
            int iterations = 0;
            guardCurrentPosition = new ArrayList<>(guardInitialPosition);

//            System.out.println("First : " + list.get(0));
//            System.out.println("Second : " + list.get(1));

            currDirection = Direction.UP;

//            System.out.println("This is the current pos ROW: " + list.get(0) + " COL: " + list.get(1));
            if (mapOfSearchArea.get(list.get(0)).get(list.get(1)) == '.'){
                mapOfSearchArea.get(list.get(0)).set(list.get(1), '#');
                printGrid();

                Set<String> visitedInThisPath = new HashSet<>();
                boolean done = false;
                while (!done) {
                    done = followPath(currDirection, visitedInThisPath);
                    // If followPath rotated direction, loop again
                    // but with the same visitedInThisPath
                    if (isCycle) {
                        // handle cycle
                        totalPos++;
                        isCycle = false;
                        break;
                    }
                }

                mapOfSearchArea.get(list.get(0)).set(list.get(1), '.');
                printGrid();
            }
        }
    }

    /**
     * Method to print the grid
     */
    private void printGrid(){
        for (ArrayList<Character> list : mapOfSearchArea){
            for (Character c : list){
                System.out.print(c);
            }
            System.out.println();
        }
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
//                    System.out.println("Leaving at LEFT");
//                    System.out.println("Row : " + currentRow);
//                    System.out.println("Col : " + currentCol);
                    return true;
                }
            }
            case UP -> {
                if (currentRow == 0){
//                    System.out.println("Leaving at UP");
//                    System.out.println("Row : " + currentRow);
//                    System.out.println("Col : " + currentCol);
                    return true;
                }
            }
            case DOWN -> {
                if (currentRow == dimensions - 1){
//                    System.out.println("Leaving at DOWN");
//                    System.out.println("Row : " + currentRow);
//                    System.out.println("Col : " + currentCol);
                    return true;
                }
            }
            case RIGHT -> {
                if (currentCol == dimensions - 1){
//                    System.out.println("Leaving at RIGHT");
//                    System.out.println("Row : " + currentRow);
//                    System.out.println("Col : " + currentCol);
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
//                System.out.println("Found to be up");
                return new ArrayList<>(Arrays.asList(-1, 0));
            }
            case DOWN -> {
//                System.out.println("Found to be down");
                return new ArrayList<>(Arrays.asList(1, 0));
            }
            case LEFT -> {
//                System.out.println("Found to be left");
                return new ArrayList<>(Arrays.asList(0, -1));
            }
            case RIGHT -> {
//                System.out.println("Found to be right");
                return new ArrayList<>(Arrays.asList(0, 1));
            }
        }
//        System.out.println("RETURNING NULL");
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
        File file = new File("C:\\Users\\19365\\OneDrive\\Documents\\GitHub\\AdventDay6\\src\\smallIn.txt");

        BufferedReader br = new BufferedReader(new FileReader(file));

        AdventD6P2 adventD6P2 = new AdventD6P2(br);

        adventD6P2.findGuard();


        while (!adventD6P2.followPath(adventD6P2.currDirection, new HashSet<>())){
            System.out.println(adventD6P2.currDirection);
            System.out.println("Turning");
        }

        System.out.println("This is the distinct positions: " + adventD6P2.distinctPositions);
        System.out.println("This is the length : " + adventD6P2.distinctPositions.size());

        adventD6P2.checkAllOfThePathForCycles();
        System.out.println("Hello " + adventD6P2.totalPos);


    }
}
