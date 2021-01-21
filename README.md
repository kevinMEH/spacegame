# **Space Game**

by: Kevin from Brooklyn Technical High School

A space game for my AP Computer Science A's marking period 2 project. <br>

The goal of the game is to manage your resources and defeat other empires. <br>

<!-- **DISCLAIMER:** This game is made entirely from the author's imagination and through research. Any references or correlation with other games or projects are entirely and purely coincidental. -->

## **Table of Contents**

- [How To Play](#how-to-play)
- [Bot Documentation](#bot-documentation)
  - [Status](#status)
  - [Status Checks](#status-checks)
  - [Goals](#goals)
  - [Algorithms](#algorithms)
- [The End](#the-end)

## **How To Play**

The goals of Space Game is relatively simple. You manage your resources, fight for a party and try and defeat the opponents. However, the complexity lies in the different approaches that one can choose to take. I will not list any here, just basic information needed to play the game.

---

### **Resources**

**Metal** is an essential resource that is the building blocks of your empire. Anything from buildings to armored ships will require large amounts of metal.

**Crystal** is required for advanced technology and weaponry, powering your mines and the weapons on your ships and defenses.

**Deuterium** is an efficient and clean fuel that is required to power your fleet and other energy intensive operations.

---

### **Develop Your Empire**

Upgrade your buildings and increase your resource output to build ships and defenses. Colonize and transport resources to other planets.

### **Attack Other Empires**

To win the game, you must attack other planets. Build different types of ships to your needs: Some ships are heavily armored and can absorb lots of damage. Some are more effective against other ships. Some are more effective against defenses.

### **Defend Your Empire**

Build defenses to defend your planet against enemies. Defenses are more effective when defending than ships, but are local to your planet and cannot be used for attacking.

## **Bot Documentation**

Below is documentation for the the bot that you play against in Space Game. If you do not wish to learn the behaviors of the bot, do not scroll down.

If you find unintended behavior in the algorithms of the bots or would like to contribute, feel free to open a pull request.

---

### **Status**

While every empire has a primary target, the needs of each planet is different and therefore needs seperate statuses. In this case, the **STATUS** enum will be responsible for indicating the states of the planets:

- **DEVELOP:**

    Planets that are weak in resources or development of defenses or ships will have the `DEVELOP` status, indicating that further development is needed.

- **ALERT:**

    Planets that can potentially be attacked and destroyed will have the `ALERT` status, indicating that the planet should aim to build defenses.

- **DEFENSIVE:**

    Planets that are being attacked or in great danger of being attacked will have the `DEFENSIVE` status, indicating that the planet should actively seek to build defenses.

- **OFFENSIVE:**

    Planets that have the potential resources or ships to attack other planets will have the `OFFENSIVE` status, indicating that the planet should actively seek to build ships and attack.

- **COORDINATED**

    A universal status that tells all parties on the same team to attack a singular target. An attack will always occur unless that planet does not have enough deuterium to start an attack.

>
    The status of the planet determines the planet's next moves.

### **Status Checks**

After every **day**, a status check is performed by the empire for all planets. (The statusCheck has to be performed at the end of the day to prevent planets from obtaining a new status before the enemy has completed their actions.)

    Status checks are responsible for updating the states of the planets.

---

### **Goals**

Every planet has a goal, indicated by the **goalAction** variable of each planet. The goalAction is of type **Action**, and stores a lambda which is executed once all goals are met.

- **metalGoal, crystalGoal, deuteriumGoal:**

    The goal amount of resources that a planet must have in order to complete the action.

- **goalAction:**

    A lambda expression storing the method that should be executed once goals are met.
    For Example: `() -> this.botStartAttack(homePlanet, enemyPlanet)`

- **actionType:**

    The type of action that the lambda is trying to execute, whether its an OFFENSIVE, ATTACK or DEVELOP action. <br>
>
    Goals makes sure that planets plan for the long term and remember what past actions should be completed.

---

### **Algorithms**

The actions of the bot depends on 3 algorithms that represent its actions. (There are other algorithms but they more or less are based on these 3 main algorithms.) The algorithm that is executed depends on the STATUS of the planet. <br>

#### **Defensive**

- Goals: Only offensive goals are executed. (The planet will never set a goal to build defenses.)
- Defensive first calculates the amount of crystal and metal that should be used to build defenses and ships.
- Half that amount will be allocated to build defenses. The rest will be used to upgrade ships.
- If the planet has built enough defenses, the status check will automatically set the status to something else.

#### **Develop**

- Goals: All types of goals are executed.
- Develop first finds the building that needs to be upgraded and tries to upgrade it. (The priority is METAL, CRYSTAL, then DEUTERIUM.)
- If the building could not be upgraded, the planet sets a goal to execute that action and ends the turn.

#### **Offensive**

- Goals: If the goal is offensive or to attack, they're executed.
- Offensive first checks the defensive and potential power of the target's weakest planet. If the bot guages that it is unable to attack, it builds ships until its has enough offensive power to attack.
- If ships could not be built and there is not enough offensive power to start an attack, the planet sets a goal to build ships.
- If there is enough offensive power to attack but not enough deuterium, the planet sets a goal to attack.

#### **Coordinated**

- Goals: Coordinated does not execute any goals.
- Coordinated is a special action. It is sort of a "coordinated attack" on a singular enemy target.
- Essentially, all of the bots on the same team collaborate together to attack a single planet. (This is implemented to advance the game faster.)
- Coordinated first checks if our offensive power is less than the defensive power of the enemy planet. If it is, the planet will seek to build ships until it is greater than the defensive power of the enemy planet.
- Coordinated forces the bot to start an attack even if the bot has a weak fleet. The only time the bot will not seek to attack is if it does not have enough deuterium to complete the operation, in which case it will set a goal to attack.
>
    These algorithms are the backbone for the actions of the bot.

## **The End**

This game was made for a school project. This game is made for educational purposes.
I hope you enjoyed playing this game or exploring the code. Thanks for viewing!

## License
[MIT](/LICENSE.md)
