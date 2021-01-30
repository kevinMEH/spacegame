package spacegame;

abstract class Coordinates {

    // Max values for coordinates should be from -50 to 50.
    private final int[] coordinate = new int[3];

    public int getX() {return this.coordinate[0];}
    public int getY() {return this.coordinate[1];}
    public int getZ() {return this.coordinate[2];}
    
    private int x;
    private int y;
    private int z;

    public void setCoordinate(int x, int y, int z) {
        coordinate[0] = x;
        coordinate[1] = y;
        coordinate[2] = z;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public static double getDistance(int[] coordinate1, int[] coordinate2) { // Calculate distance from int array.
        return Math.sqrt(
            Math.pow((double) coordinate2[0] - coordinate1[0], 2) + 
            Math.pow((double) coordinate2[1] - coordinate1[1], 2) +
            Math.pow((double) coordinate2[2] - coordinate1[2], 2)
        );
    }

    public int[] getCoordinates() {
        return coordinate;
    }
    
    public String stringCoordinates() {
        return "(" + x + ", " + y + ", " + z + ")";
    }
}
