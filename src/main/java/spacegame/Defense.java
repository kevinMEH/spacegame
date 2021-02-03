package spacegame;

public class Defense {

    private final String name;
    private String description;

    private final int metalCost;
    private final int crystalCost;

    private final int health;
    private final int attack;

    private double defensivePower; public double getDefensivePower() {return defensivePower;}
    public void setPower(double power) {this.defensivePower = power;} 

    public Defense(String name, int metalCost, int crystalCost, int health, int attack) {
        this.name = name;
        this.metalCost = metalCost;
        this.crystalCost = crystalCost;
        this.health = health;
        this.attack = attack;
    }

    public void build(Planet planet) {
        if(planet.getEmpire() != Game.player) { // If is bot, sep algorithm
            botBuild(planet);
            return;
        }
        System.out.println("How many " + name + " would you like to build? Type anything other than a number to exit.");
        int amount;
        try {
            amount = Integer.parseInt(Game.scanner.nextLine());
        } catch (Exception e) {
            return;
        }
        if(amount < 1) {
            System.out.println("Number must be 1 or greater!");
            return;
        }
        int totalMetalCost = metalCost * amount;
        int totalCrystalCost = crystalCost * amount;
        if(totalMetalCost > planet.getMetal()) {
            System.out.println("You do not have enough metal to complete this operation!");
            return;
        }
        if(totalCrystalCost > planet.getCrystal()) {
            System.out.print("You do not have enough crystal to complete this operation!");
            return;
        }
        planet.addMetal(totalMetalCost * -1);
        planet.addCrystal(totalCrystalCost * -1);
        planet.setDefenseCount(this, planet.getDefenseCount(this) + amount);
        System.out.println("Successfully built " + amount + " " + name + ".");
    }
    
    public void botBuild(Planet planet) {
        if(metalCost > planet.getMetal()) {
            System.out.println("ERROR: Bot attempted to build ships without enough metal. botBuild()");
            return;
        }
        if(crystalCost > planet.getCrystal()) {
            System.out.println("ERROR: Bot attempted to build ships without enough crystal. botBuild()");
            return;
        }
        planet.addMetal(metalCost * -1);
        planet.addCrystal(crystalCost * -1);
        planet.setDefenseCount(this, planet.getDefenseCount(this) + 1);
    }

    public void printInfo() {
        printCost();
        printStats();
    }

    public void printCost() {
        System.out.println("Metal Cost: " + metalCost);
        System.out.println("Crystal Cost: " + crystalCost);
    }

    public void printStats() {
        System.out.println("Health: " + health);
        System.out.println("Attack: " + attack);
    }

    public void setDescription(String description) {this.description = description;}
    public String getDescription() {return description;}

    public int getMetalCost() {return metalCost;}
    public int getCrystalCost() {return crystalCost;}

    public int getAttack() {return attack;}
    public int getHealth() {return health;}

    @Override public String toString() {return name;}
    public String getName() {return name;}
}
