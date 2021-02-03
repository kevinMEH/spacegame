package spacegame;

class Building {

    // Resource cost of building
    private final Planet planet; // Planet that the building is on.
    private final int baseMetalCost;
    private final int baseCrystalCost;
    private final int baseDeuteriumCost;
    private int level = 0; // TODO: Add level cap
    private final String name;

    public Building(String name, Planet planet, int metalCost, int crystalCost, int deuteriumCost) {
        this.planet = planet;
        this.baseMetalCost = metalCost;
        this.baseCrystalCost = crystalCost;
        this.baseDeuteriumCost = deuteriumCost;
        this.name = name;
    }
    
    public boolean enoughMaterials() {
        return(planet.getMetal() >= getMetalCost() && 
        planet.getCrystal() >= getCrystalCost() && 
        planet.getDeuterium() >= getDeuteriumCost());
    }

    public boolean levelUp() { // Levels up building on planet
        if(enoughMaterials()) {
            planet.addMetal(getMetalCost() * -1);
            planet.addCrystal(getCrystalCost() * -1);
            planet.addDeuterium(getDeuteriumCost() * -1);
            level++;
            return true;
        } else {
            if(level == 0) System.out.println("You do not have enough resources to build this building!");
            else System.out.println("You do not have enough resources to level up!");
            return false;
        }
    }

    public void mineMetal() { // https://www.desmos.com/calculator/19dh9jtgy7 
        int amount = (int) (600 * level * Math.pow(1.2, level) * planet.getMetalMultiplier());

        if(planet.getUnminedMetal() > amount) { // If you have more unmined than needed
            planet.addMetal(amount);
            planet.remUnminedMetal(amount);
        } else { // If you have less, then mine what is available.
            planet.addMetal(planet.getUnminedMetal());
            planet.remUnminedMetal(planet.getUnminedMetal());
        }
    }
    
    public void mineCrystal() { // https://www.desmos.com/calculator/26zostshlm
        int amount = (int) (300 * level * Math.pow(1.2, level) * planet.getCrystalMultiplier()); // Efficiency is a deprecated stat.

        if(planet.getUnminedCrystal() > amount) { // If you have more unmined than needed
            planet.addCrystal(amount);
            planet.remUnminedCrystal(amount);
        } else { // If you have less, then mine what is available.
            planet.addCrystal(planet.getUnminedCrystal());
            planet.remUnminedCrystal(planet.getUnminedCrystal());
        }
    }
    
    public void mineDeuterium() { // https://www.desmos.com/calculator/y1ucry7rw7
        int amount = (int) (200 * level * Math.pow(1.2, level) * planet.getDeuteriumMultiplier()); // Efficiency is a deprecated stat.

        if(planet.getUnminedDeuterium() > amount) { // If you have more unmined than needed
            planet.addDeuterium(amount);
            planet.remUnminedDeuterium(amount);
        } else { // If you have less, then mine what is available.
            planet.addDeuterium(planet.getUnminedDeuterium());
            planet.remUnminedDeuterium(planet.getUnminedDeuterium());
        }
    }
    
    public void printCost() {
        if(getMetalCost() != 0) System.out.println("Metal Cost: " + getMetalCost());
        if(getCrystalCost() != 0) System.out.println("Crystal Cost: " + getCrystalCost());
        if(getDeuteriumCost() != 0) System.out.println("Deuterium Cost: " + getDeuteriumCost());
    }

    public int getMetalCost() {return (int) Math.pow(1.5, level) * baseMetalCost;}
    public int getCrystalCost() {return (int) Math.pow(1.5, level) * baseCrystalCost;}
    public int getDeuteriumCost() {return (int) Math.pow(1.5, level) * baseDeuteriumCost;}

    public int getLevel() { return level; }
    public boolean isBuilt() { return level != 0; }

    @Override public String toString() { return name; }
}