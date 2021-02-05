package spacegame;

class Building {

    // Resource cost of building
    private final int baseMetalCost;
    private final int baseCrystalCost;
    private final int baseDeuteriumCost;
    private final String name;

    public Building(String name, int metalCost, int crystalCost, int deuteriumCost) {
        this.name = name;
        this.baseMetalCost = metalCost;
        this.baseCrystalCost = crystalCost;
        this.baseDeuteriumCost = deuteriumCost;
    }
    
    public boolean enoughMaterials(Planet planet) {
        return
            planet.getMetal() >= getMetalCost(planet) && 
            planet.getCrystal() >= getCrystalCost(planet) && 
            planet.getDeuterium() >= getDeuteriumCost(planet);
    }

    boolean levelUp(Planet planet) { // Levels up building on planet
        if(enoughMaterials(planet)) {
            planet.addMetal(getMetalCost(planet) * -1);
            planet.addCrystal(getCrystalCost(planet) * -1);
            planet.addDeuterium(getDeuteriumCost(planet) * -1);
            planet.addLevel(this);
            return true;
        } else {
            System.out.println("You do not have enough resources to level up!");
            return false;
        }
    }

    void mineMetal(Planet planet) { // https://www.desmos.com/calculator/19dh9jtgy7 
        int level = planet.getLevel(this);
        int amount = (int) (600 * level * Math.pow(1.2, level) * planet.getMetalMultiplier());

        if(planet.getUnminedMetal() > amount) { // If you have more unmined than needed
            planet.addMetal(amount);
            planet.remUnminedMetal(amount);
        } else { // If you have less, then mine what is available.
            planet.addMetal(planet.getUnminedMetal());
            planet.remUnminedMetal(planet.getUnminedMetal());
        }
    }
    
    void mineCrystal(Planet planet) { // https://www.desmos.com/calculator/26zostshlm
        int level = planet.getLevel(this);
        int amount = (int) (300 * level * Math.pow(1.2, level) * planet.getCrystalMultiplier()); // Efficiency is a deprecated stat.

        if(planet.getUnminedCrystal() > amount) { // If you have more unmined than needed
            planet.addCrystal(amount);
            planet.remUnminedCrystal(amount);
        } else { // If you have less, then mine what is available.
            planet.addCrystal(planet.getUnminedCrystal());
            planet.remUnminedCrystal(planet.getUnminedCrystal());
        }
    }
    
    void mineDeuterium(Planet planet) { // https://www.desmos.com/calculator/y1ucry7rw7
        int level = planet.getLevel(this);
        int amount = (int) (200 * level * Math.pow(1.2, level) * planet.getDeuteriumMultiplier()); // Efficiency is a deprecated stat.

        if(planet.getUnminedDeuterium() > amount) { // If you have more unmined than needed
            planet.addDeuterium(amount);
            planet.remUnminedDeuterium(amount);
        } else { // If you have less, then mine what is available.
            planet.addDeuterium(planet.getUnminedDeuterium());
            planet.remUnminedDeuterium(planet.getUnminedDeuterium());
        }
    }
    
    void printCost(Planet planet) {
        if(getMetalCost(planet) != 0) System.out.println("Metal Cost: " + getMetalCost(planet));
        if(getCrystalCost(planet) != 0) System.out.println("Crystal Cost: " + getCrystalCost(planet));
        if(getDeuteriumCost(planet) != 0) System.out.println("Deuterium Cost: " + getDeuteriumCost(planet));
    }

    int getMetalCost(Planet planet) {return (int) Math.pow(1.5, planet.getLevel(this)) * baseMetalCost;}
    int getCrystalCost(Planet planet) {return (int) Math.pow(1.5, planet.getLevel(this)) * baseCrystalCost;}
    int getDeuteriumCost(Planet planet) {return (int) Math.pow(1.5, planet.getLevel(this)) * baseDeuteriumCost;}

    @Override public String toString() { return name; }
}