define(function() {
    var instance = null;
    
    /**
     * Represents a player.
     * 
     * @constructor
     * 
     * @param {number} id - The player's ID number. Can be zero if not playing any games.
     * @param {string} name - The player's name.
     */
    var Player = function(id, name) {
        instance = this;
        instance.id = id;
        instance.name = name;
        instance.game = null;
    };
    
    /**
     * Set the current game played by the player.
     * 
     * @param {object} game - The game.
     */
    Player.prototype.setGame = function(game) {
        instance.game = game;
    };
    
    return Player;
});