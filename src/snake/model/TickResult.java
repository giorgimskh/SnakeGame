package snake.model;

/** What a game tick ended with. Anything other than CONTINUE ends the game. */
public enum TickResult {
    CONTINUE,
    PLAYER_DIED,
    LEVEL_COMPLETE,
    LEVEL_FAILED
}
