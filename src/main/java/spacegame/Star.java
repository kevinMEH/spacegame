package spacegame;

public class Star extends Coordinates { // Star of a planetary system.

    private final BlackHole blackHole;

    private final Planet[] planets = new Planet[10]; // Array of 10 planets for each sun.

    private final int systemIndex;

    public void initializePlanets() { // Initializes the 10 planets and add to array
        for(int i = 0; i < 10; i++) {
            planets[i] = new Planet(this, i);
        }
    }

    public Star(BlackHole blackHole, int index) { // Make star
        this.blackHole = blackHole;
        // Max coordinate is 45, minimum is -45.
        // This is to give planets space to generate without exceeding the -50 to 50 limit.
        setCoordinate   (
            (int) (Math.random() * 91 - 45), 
            (int) (Math.random() * 91 - 45), 
            (int) (Math.random() * 91 - 45)
        );
        this.systemIndex = index;
    }
    // TODO:
    // IMPORTANT: Make sure 2 suns cannot be on top of one another.
    // Create 27 quadrants (3 x 3 x 3) and have each Sun reside in one of
    // those quadrants to avoid planets stacking on top of each other.
    // This should be easy to do?
    // 50 / 3 - 25 / 3
    // 2 x 50 / 3 - 25 / 3
    // 50 - 25 / 3

    public int getSystemIndex() {return systemIndex;}
    public BlackHole getBlackHole() {return blackHole;}
    public Planet[] getPlanets() {return planets;}
    public Planet getPlanet(int i) { // Takes a parameter from 0 to 9 and then returns the planet at that slot.
        if(i >= 0 && i <= 9) {
            return planets[i];
        } else if(i < 0) {
            System.out.println("ERROR: Parameter " + i + " is out of bounds! Nearest in bound parameter returned (0). (getPlanets())");
            return planets[0];
        } else { // (i > 9)
            System.out.println("ERROR: Parameter " + i + " is out of bounds! Nearest in bound parameter returned (9). (getPlanets())");
            return planets[9];
        }
    }
    public Planet getRandomPlanet() {
        int random = (int) (Math.random() * planets.length);
        return planets[random];
    }

}
