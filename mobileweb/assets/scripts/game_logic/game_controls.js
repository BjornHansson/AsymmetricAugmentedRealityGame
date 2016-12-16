define(['jquery', 'game'], function($, game) {
    var instance = null;

    /**
     * Controls the list of games.
     * 
     * @constructor
     * 
     * {string} controller - The controller's IP address. 
     */
    var GameControls = function(controller) {
        instance = this;
        instance.controller = controller; // The controller peer's IP address
        instance.currentGame = new game(controller);
    };

    /**
     * List all games.
     * 
     * @return {array[object]} - A list of games.
     */
    GameControls.prototype.listGames = function() {
        return $.get({
            url: instance.controller + '/games',
            dataType: 'json'
        })
        .done(function(data) {
            console.log(instance);
            instance.currentGame.url = data.actions.currentgame.url;
            return data;
        })
        .fail(function() {
            return [];
        });
    };

    /**
     * Get the current game.
     * 
     * @return {object} - The current game.
     */
    GameControls.prototype.getCurrentGame = function() {
        if (instance.currentGame.id === null) {
            return instance.listGames
            .done(function(data) {
                instance.currentGame = data.actions.currentgame.url;
                return $.get({
                    url: instance.currentGame,
                    dataType: 'json'
                })
                .done(function(data) {
                    instance.currentGame.id = data.gameid
                    instance.currentGame.name = data.name;
                    return instance.currentGame;
                })
                .fail(function() {
                    return null;
                });
            })
            .fail(function() {
                return null;
            });
        } else {
            return $.get({
                url: instance.currentGame.url,
                dataType: 'json'
            })
            .done(function(data) {
                return data;
            })
            .fail(function() {
                return null;
            });
        }
    };

    /**
     * Create a game.
     * 
     * @param {string} name - The game's name.
     * 
     * @return {object} game - The newly created game.
     */
    GameControls.prototype.createGame = function(name) {
        return $.post({
            url: instance.controller + '/games',
            data: { name: name },
            dataType: 'json'
        })
        .done(function(data) {
            instance.currentGame.id = data.gameid;
            instance.currentGame.name = data.name;
            instance.currentGame.url = data.actions.information.url;
            
            return instance.currentGame;
        })
        .fail(function() {
            return null;
        });
    };

    return GameControls;
});