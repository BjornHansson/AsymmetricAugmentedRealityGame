define(function() {
    /**
     * Represents a game.
     * 
     * @constructor
     * 
     * @param {string} controller - The controller's IP address.
     * @param {number} id - The game's id number.
     * @param {string} name - The game's name.
     */
    var Game = function(controller, id, name) {
        this.controller = controller;
        this.id = id;
        this.name = name;
    };

    /**
     * Join a game.
     * 
     * @param {object} player - A player who wishes to play a game.
     * 
     * @return {object} player - Returns the same object as was passed.
     *          If the player was able to join, its id number is altered.
     */
    Game.prototype.join = function(player) {
        return player;
    };

    /**
     * Leave a game.
     * 
     * @param {object} player - A player who wishes to leave a game.
     * 
     * @return {object} player - Returns the same object as was passed.
     *          If the player was able to join, its id number is set to 0.
     */
    Game.prototype.leave = function(player) {
        return false;
    };
    
    /**
     * Returns a list of defuse attempts.
     *
     * @return {array} - An array of defuse attempts.
     */
    Game.prototype.listDefuseAttempts = function() {
        return [];
    };
    
    return Game;
});