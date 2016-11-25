define(function() {
    /**
     * Represents a player.
     * 
     * @constructor
     * 
     * @param {string} controller - The controller's IP address.
     * @param {number} id - The player's ID number. Can be zero if not playing any games.
     * @param {string} name - The player's name.
     */
    var Player = function(controller, id, name) {
        this.controller = controller;
        this.id = id;
        this.name = name;
        this.game = null;
    };
    
    /**
     * Set the current game played by the player.
     * 
     * @param {object} game - The game.
     */
    Player.prototype.setGame = function(game) {
        this.game = game;
    };

    /**
     * Try to defuse a bomb.
     * 
     * @return Something something...
     */
    Player.prototype.tryDefuse = function() {
        return null;
    };
    
    return Player;
});