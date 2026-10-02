# Snake Game

A Snake game with five themed levels, written in Java Swing. It runs on the desktop, and in the
browser through [CheerpJ](https://cheerpj.com/).

## Controls

In the menu, click a level card, or use Up/Down to move between cards and Enter or Space to pick one.

| Key | Action |
| --- | --- |
| Arrow keys or W A S D | Steer the snake |
| P or Space | Pause / resume |
| Esc | Back to the main menu |

When a game ends, a dialog offers **Next Level** (after a win), **Retry** or **Menu**.

## Rules

- The edges of the board wrap around: leave on one side and you come back on the other.
- You die if you run into your own body, a visible bomb, or the AI snake's body.
- A red apple is worth 10 points. A green **2x** apple gives no points itself, but doubles
  every apple's points for 10 seconds.
- You need **300 points** to complete a level.

## Levels

| Level | Theme | What's different |
| --- | --- | --- |
| 1 | Desert | Time attack: you have 3 minutes, and the level is judged when the time runs out. |
| 2 | Grass | Faster snake. The level is won as soon as you reach 300 points. |
| 3 | Ocean | The apple vanishes after 4 seconds and reappears elsewhere a second later. |
| 4 | Forest | Even faster, and a bomb appears and moves around the board. |
| 5 | Space | An AI snake competes for the apples. Make it crash into you (+50), a bomb (+30) or itself (+30) for bonus points. A head-on collision counts as the AI's crash. |

The high score is kept while the game is open and starts at 0 on every launch.

## How to run

Requires Java 8 or newer.

### Windows

```bat
run.bat
```

### macOS / Linux

```bash
bash run.sh
```

The script compiles `src/` to `bin/`, copies `src/sounds/` and `src/fonts/` to `bin/`, and launches the game.

### Runnable JAR

```bash
bash build-jar.sh      # or build-jar.bat on Windows
java -jar dist/SnakeGame.jar
```

The build also copies the jar to `docs/SnakeGame.jar`.

## Playing in the browser

`docs/index.html` loads the CheerpJ runtime and runs the game from `docs/SnakeGame.jar`. To publish
on GitHub Pages, run the jar build, commit `docs/SnakeGame.jar`, and serve the `docs/` folder.

## Sounds

`src/sounds/background.wav` is the background music. Sound effects are generated in code. To use
your own, add `eat.wav`, `multiplier.wav`, `gameover.wav` or `levelcomplete.wav` to `src/sounds/`.
Sounds are loaded from the classpath. If Java can't find an audio output, the console says
"Sound disabled" once and the game runs silently.

## Credits

The menu uses the [Fredoka](https://fonts.google.com/specimen/Fredoka) font by The Fredoka Project
Authors, licensed under the SIL Open Font License 1.1 (`src/fonts/OFL.txt`).
