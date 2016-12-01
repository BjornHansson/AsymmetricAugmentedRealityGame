define(function() {
    /**
     * Controls the list of games.
     * 
     * @constructor
     * 
     * {string} controller - The controller's IP address. 
     */
    var GameControls = function(controller) {
        this.controller; // The controller peer's IP address
    };

    /**
     * List all games.
     * 
     * @return {array[object]} - A list of games.
     */
    GameControls.prototype.listGames = function() {
        return [];
    };

    /**
     * Get the current game.
     * 
     * @return {object} - The current game.
     */
    GameControls.prototype.getCurrentGame = function() {
        var game = null;

        return game;
    };
    
    /**
     * Create a game.
     * 
     * @param {string} name - The game's name.
     * 
     * @return {object} game - The newly created game.
     */
    GameControls.prototype.createGame = function(name) {
        var game = null;

        return game;
    };

    return GameControls;
});