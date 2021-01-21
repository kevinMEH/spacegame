import java.util.Map;

public class Expedition {
    
    public enum ExpeditionType {
        ATTACK,
        COLONIZATION,
        TRANSPORT,
        PERMTRANSPORT,
    }
    
    private final ExpeditionType expeditionType;
    public ExpeditionType getExpeditionType() {return expeditionType;}

    private final Planet attacker;
    private final Planet defender;
    private Map<Ship, Integer> attackerFleet;
    private ExpeditionStatus expeditionStatus = ExpeditionStatus.ONGOING;
    private int flightTime;
    private int timeLeft;
    
    private boolean started = false;
    
    private int metalLoot = 0;
    private int crystalLoot = 0;
    private int deuteriumLoot = 0;

    public Expedition(Planet attacker, Planet defender, Map<Ship, Integer> fleet, ExpeditionType expeditionType) {
        this.flightTime = Empire.calcFlightTime(attacker, defender, fleet);
        this.timeLeft = flightTime;
        this.attacker = attacker;
        this.defender = defender;
        attackerFleet = fleet;
        this.expeditionType = expeditionType;
    }

    enum ExpeditionStatus {
        ONGOING,
        SUCCESS,
        FAILURE,
        DRAW,
        EMPTY
    }
    public void attackSequence() { // TODO: Add attack arrangements, etc.
        if(defender.isDestroyed()) {
            expeditionStatus = ExpeditionStatus.EMPTY;
            conclude(attacker, defender);
            return;
        }
        System.out.println("An attack has started!");
        System.out.println("Attacker:");
        System.out.println("Planet " + attacker.getName() + " from " + attacker.getEmpire() + ".");
        System.out.println("Defender:");
        System.out.println("Planet " + defender.getName() + " from " + defender.getEmpire() + ".");
        Map<Ship, Integer> defenderFleet = defender.getShips();
        Map<Defense, Integer> defenderDefenses = defender.getDefenses();

        int leftOverAttackerDamage = 0;
        int leftOverDefenderShipDamage = 0;
        int leftOverDefenderDefenseDamage = 0;
        int rounds = 1;

        while(expeditionStatus == ExpeditionStatus.ONGOING) {
            Game.sleep(4000);
            System.out.println("Round " + rounds);
            int attackerTotalDamage = 0;
            int attackerAntiDefenseDamage = 0; // Extra damage against defenses
            int attackerAntiShipDamage = 0; // Extra damage against ships
            int defenderTotalDamage = 0;
            int defenderAntiShipDamage = 0;
            
            int defenderShipTotalHealth = 0;
            int defenderDefenseTotalHealth = 0;

            int attackerShipTotalDamageTaken = 0;
            int defenderShipTotalDamageTaken = 0;
            int defenderDefenseTotalDamageTaken = 0;

            System.out.println("Attacker ships left:");
            // Get total attack damages and defenses of attacker fleets and defender fleets
            for(Map.Entry<Ship, Integer> entry : attackerFleet.entrySet()) {
                Ship ship = entry.getKey();
                int count = entry.getValue();
                if(count != 0) System.out.println(count + " " + ship.getName());
                attackerTotalDamage = attackerTotalDamage + ship.getAttack() * count;
                if(ship.getAntishipMultiplier() > 1) {
                    attackerAntiShipDamage = (int) (attackerAntiShipDamage + (ship.getAntishipMultiplier() - 1) * (ship.getAttack() * count));
                }
                if(ship.getAntidefenseMultiplier() > 1) {
                    attackerAntiDefenseDamage = (int) (attackerAntiDefenseDamage + (ship.getAntidefenseMultiplier() - 1) * (ship.getAttack() * count));
                }
            }
            System.out.println("Defender ships left:");
            for(Map.Entry<Ship, Integer> entry : defenderFleet.entrySet()) { // Ships weaker when defending
                Ship ship = entry.getKey();
                int count = entry.getValue();
                if(count != 0) System.out.println(count + " " + ship.getName());
                defenderTotalDamage = (int) (0.7 * defenderTotalDamage + ship.getAttack() * count);
                defenderShipTotalHealth = defenderShipTotalHealth + ship.getHealth() * count;
                if(ship.getAntishipMultiplier() > 1) {
                    defenderAntiShipDamage = (int) (0.7 * defenderAntiShipDamage + (ship.getAntidefenseMultiplier() - 1) * (ship.getAttack() * count));
                }
            }
            System.out.println("Defender defenses left:");
            for(Map.Entry<Defense, Integer> entry : defenderDefenses.entrySet()) {
                Defense defense = entry.getKey();
                int count = entry.getValue();
                if(count != 0) System.out.println(count + " " + defense.getName());
                defenderTotalDamage = defenderTotalDamage + defense.getAttack() * count;
                defenderDefenseTotalHealth = defenderDefenseTotalHealth + defense.getHealth() * count;
            }

            attackerShipTotalDamageTaken = attackerShipTotalDamageTaken + (defenderTotalDamage + defenderAntiShipDamage + leftOverAttackerDamage);
            defenderShipTotalDamageTaken = defenderShipTotalDamageTaken + (attackerAntiShipDamage + leftOverDefenderShipDamage);
            defenderDefenseTotalDamageTaken = defenderDefenseTotalDamageTaken + (attackerAntiDefenseDamage + leftOverDefenderDefenseDamage);

            double shipDamagePercentage; // Percentage of attack power that should go to ships
            shipDamagePercentage = Math.max(defenderShipTotalHealth - defenderShipTotalDamageTaken, 0) 
            / ((double) Math.max(defenderShipTotalHealth - defenderShipTotalDamageTaken, 1) 
            + Math.max(defenderDefenseTotalHealth - defenderDefenseTotalDamageTaken, 1)); // Ship Health / Total Health

            defenderShipTotalDamageTaken = (int) (defenderShipTotalDamageTaken + shipDamagePercentage * attackerTotalDamage);
            defenderDefenseTotalDamageTaken = (int) (defenderDefenseTotalDamageTaken + attackerTotalDamage - (shipDamagePercentage * attackerTotalDamage));

            System.out.println();
            System.out.println("Attacker " + attacker.getName() + " has dealt " + defenderShipTotalDamageTaken + " to " + defender.getName() + "'s ships!");
            System.out.println("Attacker " + attacker.getName() + " has dealt " + defenderDefenseTotalDamageTaken + " to " + defender.getName() + "'s defenses!");
            System.out.println("Defender " + defender.getName() + " has dealt " + attackerShipTotalDamageTaken + " to " + attacker.getName() + "'s ships!");

            // Calc damages to both fleets
            for(Map.Entry<Ship, Integer> entry : attackerFleet.entrySet()) {
                Ship ship = entry.getKey();
                int count = entry.getValue();
                while(attackerShipTotalDamageTaken > ship.getHealth() && count > 0) {
                    attackerShipTotalDamageTaken = attackerShipTotalDamageTaken - ship.getHealth();
                    count--;
                }
                entry.setValue(count);
            }
            for(Map.Entry<Ship, Integer> entry : defenderFleet.entrySet()) {
                Ship ship = entry.getKey();
                int count = entry.getValue();
                while(defenderShipTotalDamageTaken > ship.getHealth() && count > 0) {
                    defenderShipTotalDamageTaken = defenderShipTotalDamageTaken - ship.getHealth();
                    count--;
                }
                entry.setValue(count);
            }
            for(Map.Entry<Defense, Integer> entry : defenderDefenses.entrySet()) {
                Defense defense = entry.getKey();
                int count = entry.getValue();
                while(defenderDefenseTotalDamageTaken > defense.getHealth() && count > 0) {
                    defenderDefenseTotalDamageTaken = defenderDefenseTotalDamageTaken - defense.getHealth();
                    count--;
                }
                entry.setValue(count);
            }

            if(attackerShipTotalDamageTaken > 0) leftOverAttackerDamage = attackerShipTotalDamageTaken;
            if(defenderShipTotalDamageTaken > 0) leftOverDefenderShipDamage = defenderShipTotalDamageTaken;
            if(defenderDefenseTotalDamageTaken > 0) leftOverDefenderDefenseDamage = defenderDefenseTotalDamageTaken;

            // Check attack status:
            if(checkDefeated(attackerFleet)) expeditionStatus = ExpeditionStatus.FAILURE;
            if(checkDestroyed(defenderFleet, defenderDefenses)) expeditionStatus = ExpeditionStatus.SUCCESS;
            if(expeditionStatus == ExpeditionStatus.ONGOING && rounds == 9) expeditionStatus = ExpeditionStatus.DRAW;
            rounds++;
        }

        conclude(attacker, defender);
    }
    public boolean checkAttacks(Empire empire) {
        return (expeditionType == ExpeditionType.ATTACK && defender.getEmpire() == empire);
    }
    public boolean checkAttacks(Planet planet) {
        return (expeditionType == ExpeditionType.ATTACK && defender == planet);
    }

    public boolean checkDestroyed(Map<Ship, Integer> defenderFleet, Map<Defense, Integer> defenderDefenses) {
        for(Map.Entry<Ship, Integer> entry : defenderFleet.entrySet()) {
            int count = entry.getValue();
            if(count > 0) return false;
        }
        for(Map.Entry<Defense, Integer> entry : defenderDefenses.entrySet()) {
            int count = entry.getValue();
            if(count > 0) return false;
        }
        return true;
    }
    public boolean checkDefeated(Map<Ship, Integer> attackerFleet) {
        for(Map.Entry<Ship, Integer> entry : attackerFleet.entrySet()) {
            int count = entry.getValue();
            if(count > 0) return false;
        }
        return true;
    }
    public void conclude(Planet attacker, Planet defender) {
        switch (expeditionStatus) {
            case SUCCESS -> {
                System.out.println(attacker.getName() + " has successfully destroyed " + defender.getName() + "!");
                defender.destroy();
                loot();
                startReturn();
            }
            case FAILURE -> {
                System.out.println(defender.getName() + " has successfully defended against " + attacker.getName() + "!");
                removeThis();
            }
            case DRAW -> {
                System.out.println(attacker.getName() + " and " + defender.getName() + " were unable to destroy each other.");
                startReturn();
            }
            case ONGOING -> System.out.println("ERROR: attackStatus ONGOING at conclude()!");
            case EMPTY -> {
                System.out.println(attacker.getName() + " arrived at " + defender.getName() + " but it was already destroyed!");
                startReturn();
            }
        }
    }
    public void loot() {
        if(this.attacker.getEmpire() != Game.player) {
            botLoot();
            return;
        }
        int storageSpace = 0;
        for(Map.Entry<Ship, Integer> entry : attackerFleet.entrySet()) {
            storageSpace = storageSpace + entry.getKey().getStorage() * entry.getValue();
        }
        System.out.println("Defender Metal Amount: " + defender.getMetal());
        System.out.println("Defender Crystal Amount: " + defender.getCrystal());
        System.out.println("Defender Deuterium Amount: " + defender.getDeuterium());
        System.out.println("Total amount of storage space you have: " + storageSpace);
        boolean selected = false;
        while(!selected) {
            System.out.println("How much metal would you like to take?");
            int response;
            try {
                response = Integer.parseInt(Game.scanner.nextLine());
            } catch (Exception e) {
                System.out.println("ERROR: Invalid input! Please enter a valid number.");
                continue;
            }
            if(!validNumber(response, storageSpace)) continue;
            metalLoot = response;
            System.out.println("How much crystal would you like to take?");
            try {
                response = Integer.parseInt(Game.scanner.nextLine());
            } catch (Exception e) {
                System.out.println("ERROR: Invalid input! Please enter a valid number.");
                continue;
            }
            if(!validNumber(response, storageSpace - metalLoot)) continue;
            crystalLoot = response;
            System.out.println("How much deuterium would you like to take?");
            try {
                response = Integer.parseInt(Game.scanner.nextLine());
            } catch (Exception e) {
                System.out.println("ERROR: Invalid input! Please enter a valid number.");
                continue;
            }
            if(!validNumber(response, storageSpace - (metalLoot + crystalLoot))) continue;
            deuteriumLoot = response;
            
            selected = true;
        }
        defender.addMetal(metalLoot * -1);
        defender.addCrystal(crystalLoot * -1);
        defender.addDeuterium(deuteriumLoot * -1);
    }
    public void botLoot() {
        metalLoot = defender.getMetal();
        crystalLoot = defender.getCrystal();
        deuteriumLoot = defender.getDeuterium();
        defender.addMetal(defender.getMetal() * -1);
        defender.addCrystal(defender.getCrystal() * -1);
        defender.addDeuterium(defender.getDeuterium() * -1);
    }
    public boolean validNumber(int number, int storageSpace) {
        if(number > storageSpace) {
            System.out.println("ERROR: You cannot take more than you can carry!");
            return false;
        }
        if(number > defender.getMetal()) {
            System.out.println("ERROR: You cannot take more than what the planet has!");
            return false;
        }
        if(number < 0) {
            System.out.println("ERROR: You cannot take negative amounts of resources!");
            return false;
        }
        return true;
    }
    public void transportSequence() {
        if(this.attacker.getEmpire() == Game.player || this.defender.getEmpire() == Game.player) {
            System.out.println("Your ships from " + attacker.getName() + " has arrived at " + defender.getName() + "!");
            System.out.println("Transporting " + metalLoot + " metal, " + crystalLoot + " crystal, " + deuteriumLoot + " deuterium.");
            if(expeditionType == ExpeditionType.PERMTRANSPORT)
                System.out.println("Transporting all ships.");
        }
        defender.addMetal(metalLoot);
        defender.addCrystal(crystalLoot);
        defender.addDeuterium(deuteriumLoot);
        metalLoot = 0;
        crystalLoot = 0;
        deuteriumLoot = 0;
        if(expeditionType == ExpeditionType.TRANSPORT) {
            expeditionStatus = ExpeditionStatus.SUCCESS;
            startReturn();
        } else if(expeditionType == ExpeditionType.PERMTRANSPORT) {
            expeditionStatus = ExpeditionStatus.SUCCESS;
            for(Map.Entry<Ship, Integer> entry : attackerFleet.entrySet()) { // Adds the ships to current planet
                defender.setShipCount(entry.getKey(), defender.getShipCount(entry.getKey()) + entry.getValue());
            }
            removeThis();
        }
    }
    public void colonizationSequence() {
        if(defender.isColonized()) {
            System.out.println("Planet " + defender.getName() + " is colonized and cannot be colonized by " + attacker.getEmpire());
            System.out.println("Fleet returning.");
            expeditionStatus = ExpeditionStatus.DRAW;
            startReturn();
            return;
        }
        if(defender.isDestroyed()) {
            System.out.println("Planet " + defender.getName() + " is destroyed and cannot be colonized by " + attacker.getEmpire());
            System.out.println("Fleet returning.");
            expeditionStatus = ExpeditionStatus.DRAW;
            startReturn();
            return;
        }
        System.out.println("Empire " + attacker.getEmpire() + " is colonizing " + defender.getName() + "!");
        defender.colonize(attacker.getEmpire());
        attacker.getEmpire().addPlanet(defender);
        defender.addMetal(metalLoot);
        defender.addCrystal(crystalLoot);
        defender.addDeuterium(deuteriumLoot);
        metalLoot = 0;
        crystalLoot = 0;
        deuteriumLoot = 0;
        expeditionStatus = ExpeditionStatus.SUCCESS;
        for(Map.Entry<Ship, Integer> entry : attackerFleet.entrySet()) { // Adds the ships to current planet
            defender.setShipCount(entry.getKey(), defender.getShipCount(entry.getKey()) + entry.getValue());
        }
        removeThis();
    }
    public void startReturn() {
        timeLeft = flightTime;
    }
    public void fleetReturn() {
        // If home planet is destroyed, then ships just stay there... along with the resources.
        for(Map.Entry<Ship, Integer> entry : attackerFleet.entrySet()) {
            attacker.setShipCount(entry.getKey(), attacker.getShipCount(entry.getKey()) + entry.getValue());
        }
        attacker.addMetal(metalLoot);
        attacker.addCrystal(crystalLoot);
        attacker.addDeuterium(deuteriumLoot);
        removeThis();
    }
    public void removeThis() {
        Game.expeditionsToBeRemoved.add(this); // Remove current Attack from attacks
    }
    public void increment() {
        if(!started) { // Don't increment if not started. Prevents attacks from starting 1 day early.
            started = true;
            return;
        }
        if(timeLeft > 0) timeLeft--;
    }
    public void checkExpeditionEventStart() { // Better to put it in sep method so it occurs on next day instead of current day.
        if(timeLeft == 0 && expeditionStatus == ExpeditionStatus.ONGOING) expeditionSequence();
        if(timeLeft == 0 && expeditionStatus != ExpeditionStatus.ONGOING) fleetReturn();
    }
    public void expeditionSequence() {
        switch (expeditionType) {
            case ATTACK -> attackSequence();
            case COLONIZATION -> colonizationSequence();
            case PERMTRANSPORT, TRANSPORT -> transportSequence();
        }
    }

    public Map<Ship, Integer> getFleet() {return attackerFleet;}
    public Planet getAttacker() {return this.attacker;}
    public Planet getDefender() {return this.defender;}
    public int getTimeLeft() {return timeLeft;}
}
