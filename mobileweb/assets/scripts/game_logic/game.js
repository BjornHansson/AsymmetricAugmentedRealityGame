define(function() {
    var instance = null;
    
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
        instance = this;
        instance.controller = controller;
        instance.id = id;
        instance.name = name;
        instance.actions = {
                info: null,
                join: null,
                defuse: null,
                defuses: null
        };
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
        return $.post({
            url: instance.actions.join,
            data: { name: player.name },
            dataType: 'json'
        })
        .then(function(data) {
            player.id = data.playerid;

            return player;
        })
        .fail(function() {
            return player;
        });
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
        return $.delete({
            url: instance.actions.info + '/players/' + player.id
        })
        .done(function(data) {
            player.id = 0;
        })
        .fail(function() {
            return player;
        });
    };

    /**
     * Try to defuse a bomb.
     * 
     * @param {object} player - A player who wishes to defuse a bomb.
     * 
     * @return {number} - The ID of the bomb which was defused. If no bomb was defused, return 0. 
     */
    Game.prototype.tryDefuse = function(player) {
        return $.post({
            url: instance.actions.defuse,
            data: {
                player: player.id
            },
            dataType: 'json'
        })
        .done(function(data) {
            return data.defused;
        })
        .fail(function() {
            return 0;
        });
    };

    /**
     * Returns a list of defuse attempts.
     *
     * @return {array} - An array of defuse attempts.
     */
    Game.prototype.listDefuseAttempts = function() {
        return $.get({
            url: instance.actions.defuses,
            dataType: 'json'
        })
        .done(function(data) {
            return data;
        })
        .fail(function() {
            return [];
        });
    };

    return Game;
});