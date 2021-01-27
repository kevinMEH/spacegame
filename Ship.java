public class Ship {

    private final String name;
    private String description;

    private final int metalCost;
    private final int crystalCost;

    private final int health;
    private final int attack;
    private int storage = 1000;
    
    private double offensivePower; public double getOffensivePower() {return offensivePower;}
    private double defensivePower; public double getDefensivePower() {return defensivePower;}
    public void setPower(double offensive, double defensive) {this.offensivePower = offensive; this.defensivePower = defensive;}

    private int levelRequired = 1;

    private double fuelMultiplier = 1; // Multiplied by deuterium cost based on fuel
    private double speedMultiplier = 1; // Speed of the spacecraft
    private double antishipMultiplier = 1; // Multiplier against other ships
    private double antidefenseMultiplier = 1; // Multiplier against defenses

    public Ship(String name, int metalCost, int crystalCost, int health, int attack, int storage) {
        this.name = name;
        this.metalCost = metalCost;
        this.crystalCost = crystalCost;
        this.attack = attack;
        this.health = health;
        this.storage = storage;
    }

    public Ship(String name, int metalCost, int crystalCost, int health, int attack) {
        this.name = name;
        this.metalCost = metalCost;
        this.crystalCost = crystalCost;
        this.attack = attack;
        this.health = health;
    }

    public Ship(String name, int metalCost, int crystalCost, int health, int attack, double fuelMultiplier, double speedMultiplier) {
        this.name = name;
        this.metalCost = metalCost;
        this.crystalCost = crystalCost;
        this.health = health;
        this.attack = attack;
        this.fuelMultiplier = fuelMultiplier;
        this.speedMultiplier = speedMultiplier;
    }

    public void setMultiplier(double antishipMultiplier, double antidefenseMultiplier) {
        this.antishipMultiplier = antishipMultiplier;
        this.antidefenseMultiplier = antidefenseMultiplier;
    }

    public void build(Planet planet) {
        if(planet.getEmpire() != Game.player) { // If is bot, sep algorithm
            botBuild(planet);
            return;
        }
        if(planet.getShipyardLevel() < this.getLevelRequired()) {
            System.out.println("Your shipyard level is not high enough to build this ship!");
            System.out.println("Your shipyard level: " + planet.getShipyardLevel());
            System.out.println("Level required to build " + name +": " + this.getLevelRequired());
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
            System.out.println("You do not have enough crystal to complete this operation!");
            return;
        }
        planet.addMetal(totalMetalCost * -1);
        planet.addCrystal(totalCrystalCost * -1);
        planet.setShipCount(this, planet.getShipCount(this) + amount);
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
        planet.setShipCount(this, planet.getShipCount(this) + 1);
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
        System.out.println("Storage: " + storage);
    }

    public void setDescription(String description) {this.description = description;}
    public String getDescription() {return description;}

    public int getLevelRequired() {return levelRequired;}
    public void setLevelRequired(int i) {levelRequired = i;}

    public int getMetalCost() {return metalCost;}
    public int getCrystalCost() {return crystalCost;}

    public int getAttack() {return attack;}
    public int getHealth() {return health;}
    public int getStorage() {return storage;}

    public double getFuelMultiplier() {return fuelMultiplier;}
    public double getSpeedMultiplier() {return speedMultiplier;}
    public double getAntishipMultiplier() {return antishipMultiplier;}
    public double getAntidefenseMultiplier() {return antidefenseMultiplier;}

    @Override public String toString() {return name;}
    public String getName() {return name;}

}
