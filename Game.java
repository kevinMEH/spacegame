import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Game { // Main game and cycle
    
    enum GameStatus{
        ONGOING,
        SUCCESS,
        FAILURE,
    }
    
    static GameStatus gameStatus = GameStatus.ONGOING;

    static BlackHole blackHole;

    static List<Empire> aliens = new ArrayList<>();
    static List<Empire> humans = new ArrayList<>();
    
    static int humanSystem = (int) (Math.random() * 9); // Default system they will appear in
    static int alienSystem = (int) (Math.random() * 9);
    
    static List<Expedition> expeditions = new ArrayList<>();
    static List<Expedition> expeditionsToBeRemoved = new ArrayList<>();
    private static void removeFromExpeditions() {
        for(Expedition expedition : expeditionsToBeRemoved) {
            expeditions.remove(expedition);
        }
    }

    static Empire player;
    
    static int days = 0;
    
    public static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) { // TODO: Main game
        printBackstory();
        while(alienSystem == humanSystem) {alienSystem = (int) (Math.random() * 9);} // See if default indices valid
        generate(); // Generates planets
        initializeEmpires();
        for(Empire empire : aliens) empire.forceNewTargetAndCheckGameEnd();
        for(Empire empire : humans) empire.forceNewTargetAndCheckGameEnd();
        
        while(true) { // Day sequence
            System.out.println();
            System.out.println("Day " + days);
            checkAnyExpeditionEvents();
            removeFromExpeditions();
            if(checkGameEnd()) break;
            player.checkAttacks();
            if(checkGameEnd()) break;
            player.playerEmpireStartActions();
            if(checkGameEnd()) break;
            botEmpireStartActions();
            if(checkGameEnd()) break;
            checkCoordinatedAction();
            if(checkGameEnd()) break;
            days++;
            incrementExpeditions();
        }
        System.exit(0);
    }
    
    public static void initializeEmpires() {
        player = new Empire(); // Initializes player's empire
        System.out.println("Welcome to Space Game! Your goal is to conquer all of your enemies and become the most powerful empire in the galaxy.");
        int response;
        while(true) {
            // try {
                System.out.println("How many bots would you like to fight against? Recommended: 3, Max: 8");
                response = Integer.parseInt(scanner.nextLine());
                if(response > 8 || response < 1) {
                    System.out.println("Invalid number! Number must be greater than 1 and less than or equal to 8.");
                    continue;
                }
                break;
            // } catch (InputMismatchException e) {
            //     System.out.println("ERROR: Please enter a valid number!");
            // }
        }
        for(int i = aliens.size(); i < response; i++) {
            new Empire(Affiliation.ALIEN);
        }
        for(int i = humans.size(); i < response; i++) {
            new Empire(Affiliation.HUMAN);
        }
    }
    public static void generate() {
        blackHole = new BlackHole();
        blackHole.initializeStars();
        for(Star star : blackHole.getStars()) {
            star.initializePlanets();
        }
    }
    
    public static void incrementExpeditions() {
        for(Expedition expedition : expeditions) {
            expedition.increment();
        }
    }
    
    public static void checkAnyExpeditionEvents() {
        for(Expedition expedition : expeditions) {
            expedition.checkExpeditionEventStart();
        }
    }

    public static void botEmpireStartActions() {
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
    
    public static void checkCoordinatedAction() {
        if((int) (10 * Math.random()) == 0) {
            Empire human = Game.humans.get(Empire.random.nextInt(Game.humans.size()));
            Empire alien = Game.aliens.get(Empire.random.nextInt(Game.aliens.size()));
            for(Empire empire : aliens) {
                empire.setCoordinatedTarget(human);
            }
            for(Empire empire : humans) {
                empire.setCoordinatedTarget(alien);
            }
        }
    }
    
    public static boolean checkGameEnd() {
        switch(player.getAffilication()) {
            case ALIEN:
            if(humans.isEmpty()) gameStatus = GameStatus.SUCCESS;
            if(!aliens.contains(player)) gameStatus = GameStatus.FAILURE;
            break;
            case HUMAN:
            if(aliens.isEmpty()) gameStatus = GameStatus.SUCCESS;
            if(!humans.contains(player)) gameStatus = GameStatus.FAILURE;
            break;
        }
        if(gameStatus == GameStatus.FAILURE) {
            System.out.println("All of your planets were destroyed! You lost...");
            return true;
        }
        if(gameStatus == GameStatus.SUCCESS) {
            System.out.println("You won! You now rule the galaxy.");
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
    
    public static void printBackstory() {
        System.out.println("The year is 2166. Humanity has finally put aside their differences in race, gender, class and other conflicts.");
        sleep(1000);
        System.out.println("However, big companies have polluted the Earth beyond livable. The average life expectancy is only 34.");
        sleep(1000);
        System.out.println("Most people die a horrible death of poisoning, lung cancer and other cancers of the body.");
        sleep(1000);
        System.out.println("The newly emerged World Socialist Union has finally developed rocket and colonization technology.");
        sleep(1000);
        System.out.println("Humanity finally resettled on another planet, in another solar system. However, there is a problem.");
        sleep(1000);
        System.out.println("Hostile aliens have sensed the presense of humans and they are not happy.");
        sleep(1000);
        System.out.println("Weary of the human tendency to pollute their planet and take over other countries, the aliens declared war on the humans.");
        sleep(1000);
        System.out.println("Your job: defeat the other side. No other option is available. The lives of billions of lives lies on your hands commander.");
        System.out.println();
    }

    public static void sleep(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (Exception e) {
            Thread.currentThread().interrupt();
        }
    }
}