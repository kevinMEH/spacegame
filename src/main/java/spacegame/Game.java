package spacegame;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Game { // Main game and cycle

    enum GameStatus {
        ONGOING,
        SUCCESS,
        FAILURE,
    }

    static final Scanner scanner = new Scanner(System.in);
    static final Random random = new Random();

    static GameStatus gameStatus = GameStatus.ONGOING;

    static BlackHole blackHole;

    static List<Empire> aliens = new ArrayList<>();
    static List<Empire> humans = new ArrayList<>();

    static int humanSystem = random.nextInt(9); // Default system they will appear in
    static int alienSystem = random.nextInt(9);
    
    static { while(alienSystem == humanSystem) alienSystem = random.nextInt(9); }

    static List<Expedition> expeditions = new ArrayList<>();
    static List<Expedition> expeditionsToBeRemoved = new ArrayList<>();
    private static void removeFromExpeditions() {
        for(Expedition expedition : expeditionsToBeRemoved) {
            expeditions.remove(expedition);
        }
    }

    static Empire player;

    private static int days = 0;

    public static void main(String[] args) {
        printBackstory();
        promptTutorial();
        while(alienSystem == humanSystem) {alienSystem = Game.random.nextInt(9);} // See if default indices valid
        blackHole = new BlackHole(); // Generating blackHole + stars + planets
        initializeEmpires();
        for(Empire empire : aliens) empire.forceNewTarget();
        for(Empire empire : humans) empire.forceNewTarget();

        while(true) { // Day sequence
            System.out.println();
            System.out.println("Day " + days);
            checkAnyExpeditionEvents();
            removeFromExpeditions();
            if(checkGameEnd()) break;
            player.checkAttacks();
            player.playerEmpireStartActions();
            if(checkGameEnd()) break;
            botEmpireStartActions();
            if(checkGameEnd()) break;
            checkCoordinatedAction();
            days++;
            incrementExpeditions();
        }
        System.exit(0);
    }

    private static void initializeEmpires() {
        player = new Empire(); // Initializes player's empire
        System.out.println("Welcome to Space Game! Your goal is to conquer all of your enemies and become the most powerful empire in the galaxy.");
        int response;
        while(true) {
            System.out.println("How many bots would you like to fight against? Recommended: 3, Max: 8");
            response = Integer.parseInt(scanner.nextLine());
            if(response > 8 || response < 1) {
                System.out.println("Invalid number! Number must be greater than 1 and less than or equal to 8.");
                continue;
            }
            break;
        }
        for(int i = aliens.size(); i < response; i++) {
            new Empire(Affiliation.ALIEN);
        }
        for(int i = humans.size(); i < response; i++) {
            new Empire(Affiliation.HUMAN);
        }
    }

    private static void incrementExpeditions() {
        for(Expedition expedition : expeditions) {
            expedition.increment();
        }
    }

    private static void checkAnyExpeditionEvents() {
        for(Expedition expedition : expeditions) {
            expedition.checkExpeditionEventStart();
        }
    }

    private static void botEmpireStartActions() {
        for(Empire empire : Game.humans) {
            if(checkGameEnd()) return;
            if(empire != Game.player) {
                empire.botStartActions();
                empire.newTarget();
                // 1/15 chance to pick new enemy target
                // Does not pick a new target if has been attacked recently.
                // (Because attacker will be primary target.)
                empire.statusCheck();
                empire.setDaysSinceLastAttack(empire.getDaysSinceLastAttack() + 1);
            }
        }
        for(Empire empire : Game.aliens) {
            if(empire != Game.player) {
                empire.botStartActions();
                empire.newTarget();
                // 1/15 chance to pick new enemy target
                // Does not pick a new target if has been attacked recently.
                // (Because attacker will be primary target.)
                empire.statusCheck();
                empire.setDaysSinceLastAttack(empire.getDaysSinceLastAttack() + 1);
            }
        }
    }

    private static void checkCoordinatedAction() {
        if(Game.random.nextInt(10) == 0) {
            Empire human = Game.humans.get(Game.random.nextInt(Game.humans.size()));
            Empire alien = Game.aliens.get(Game.random.nextInt(Game.aliens.size()));
            for(Empire empire : aliens) {
                empire.setCoordinatedTarget(human);
            }
            for(Empire empire : humans) {
                empire.setCoordinatedTarget(alien);
            }
        }
    }

    private static boolean checkGameEnd() {
        switch (player.getAffiliation()) {
            case ALIEN -> {
                if (humans.isEmpty()) gameStatus = GameStatus.SUCCESS;
                if (!aliens.contains(player)) gameStatus = GameStatus.FAILURE;
            }
            case HUMAN -> {
                if (aliens.isEmpty()) gameStatus = GameStatus.SUCCESS;
                if (!humans.contains(player)) gameStatus = GameStatus.FAILURE;
            }
        }
        if(gameStatus == GameStatus.FAILURE) {
            typewriter("All of your planets were destroyed! You lost...");
            return true;
        }
        if(gameStatus == GameStatus.SUCCESS) {
            typewriter("You won! You now rule the galaxy.");
            return true;
        }
        // If game ongoing do nothing...
        return false;
    }

    public static List<Empire> allEmpires() {
        List<Empire> result = new ArrayList<>(aliens);
        result.addAll(humans);
        return result;
    }

    private static void printBackstory() {
        typewriter("The year is 2166. Humanity has finally put aside their differences in race, gender, class and other conflicts.");
        typewriter("However, big companies have polluted the Earth beyond livable. The average life expectancy is only 34.");
        typewriter("Most people die a horrible death of poisoning, lung cancer and other cancers of the body.");
        typewriter("The newly emerged World Socialist Union has finally developed rocket and colonization technology.");
        typewriter("Humanity finally resettled on another planet, in another solar system. However, there is a problem.");
        typewriter("Hostile aliens have sensed the presence of humans and they are not happy.");
        typewriter("Weary of the human tendency to pollute their planet and take over other countries, the aliens declared war on the humans.");
        typewriter("Your job: defeat the other side. No other option is available. The lives of billions of lives lies on your hands commander.");
        typewriter();
    }

    private static void promptTutorial() {
        typewriter("Would you like to take a look at the tutorial?");
        String response = scanner.nextLine();
        if(response.equalsIgnoreCase("yes")) {
            typewriter("There are 3 basic resources in Space Game: Metal, Crystal, and Deuterium.");
            typewriter("Metal is used for almost everything: from construction of building to armored ships. Metal is responsible for the structural integrity of everything you build.");
            typewriter("Crystal is used for building circuits and weaponry. The most powerful weapons are made from large amounts of crystal.");
            typewriter("Deuterium is the fuel of the universe. Clean and efficient, without deuterium you cannot launch attacks with your ships.");
            typewriter();
            typewriter("You have 3 types of mines, Metal Mines, Crystal Mines, Deuterium Mines, each producing their respective resource. Level up these buildings to produce more resources.");
            typewriter();
            typewriter("In order to win the game, you must defeat all your enemies. Check your map by typing \"map\" in the action selection menu to see the locations of you, your allies and your enemies.");
            typewriter("In order to attack your enemies, you need to build ships. There are many different types of ships that you can build, each different from the other. Some ships can deal extra damage against defenses, some ships can deal extra damage against other ships.");
            typewriter("Defend your planet by building ships and defenses. Ships are less effective when defending.");
            typewriter("To build different types of ships and defenses, you must level up your shipyard. You can check the level that your ships and defenses require by typing its name in the build action menu.");
            typewriter();
            typewriter("That's all there is to the game! The game is relatively simple, but the complexity lies in all the different approaches that you can choose to take.");
            typewriter("The bots in the game, both allies and enemies are very smart. You have a very real chance of losing. Have fun!");
            typewriter();
        }
    }

    public static void typewriter(String sentence) { //Typewriter animation effect
        for(int i = 0; i < sentence.length(); i++) {
            System.out.print(sentence.charAt(i));
            try {
                Thread.sleep(10);
            } catch (Exception e) {
                Thread.currentThread().interrupt(); //Interrupts thread if its doing something else.
            }
        }
        System.out.println();
        try {
            Thread.sleep(400); //Sleeps for 400 milliseconds afterwards.
        } catch (Exception e) {
            Thread.currentThread().interrupt();
        }
    }
    public static void typewriter() { //Line break and wait
        System.out.println();
        try {
            Thread.sleep(400); //Sleeps for 400 milliseconds afterwards.
        } catch (Exception e) {
            Thread.currentThread().interrupt();
        }
    }
    public static void typewriter(int sleep) { //Line break and wait
        System.out.println();
        try {
            Thread.sleep(sleep); //Sleeps for 400 milliseconds afterwards.
        } catch (Exception e) {
            Thread.currentThread().interrupt();
        }
    }
}