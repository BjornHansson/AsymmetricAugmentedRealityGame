define(['jquery', 'proxy', 'game'], function($, proxy, game) {
    var instance = null;
    var p = new proxy();

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
        instance.currentGame = null;
    };

    /**
     * List all games.
     * 
     * @return {array[object]} - A list of games.
     */
    GameControls.prototype.listGames = function() {
        return $.get({
            url: instance.controller + '/games',
            crossDomain: true,
            dataType: 'json'
        })
        .done(function(data) {
            if (data.actions.currentgame.url) {
                instance.currentGame = new game(instance.controller, 0, null);
                instance.currentGame.actions.info = p.proxy(data.actions.currentgame.url);
            }

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
        if (!instance.currentGame || !instance.currentGame.id) {
            return instance.listGames()
            .then(function(data) {
                return $.get({
                    url: instance.currentGame.actions.info,
                    dataType: 'json'
                })
            })
            .then(function(data2) {
                if (data2.gameid) {
                    instance.currentGame = new game(instance.controller, data2.gameid, data2.name);
                    instance.currentGame.actions.info = p.proxy(data2.actions.information.url);
                    instance.currentGame.actions.join = p.proxy(data2.actions.join.url);
                    instance.currentGame.actions.defuse = p.proxy(data2.actions.defuse.url);
                    instance.currentGame.actions.defuses = p.proxy(data2.actions.defuses.url);
                } else {
                    instance.currentGame = null;
                }

                return instance.currentGame;
            })
            .fail(function() {
                return null;
            });
        } else {
            return $.get({
                url: instance.currentGame.actions.info,
                dataType: 'json'
            })
            .done(function(data) {
                instance.currentGame = new game(instance.controller, data.gameid, data.name);;
                instance.currentGame.actions.info = p.proxy(data.actions.information.url);
                instance.currentGame.actions.join = p.proxy(data.actions.join.url);
                instance.currentGame.actions.defuse = p.proxy(data.actions.defuse.url);
                instance.currentGame.actions.defuses = p.proxy(data.actions.defuses.url);
                
                return instance.currentGame;
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
            contentType: 'application/json',
            data: JSON.stringify({ name: name }),
            dataType: 'json'
        })
        .done(function(data) {
            instance.currentGame = new game(instance.controller, data.gameid, data.name);
            instance.currentGame.actions.info = p.proxy(data.actions.information.url);

            return instance.currentGame;
        })
        .fail(function() {
            return null;
        });
    };

    return GameControls;
});