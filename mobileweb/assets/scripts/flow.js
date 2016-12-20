define(['config', 'jquery', 'underscore', 'effects' ,'game_controls', 'game', 'player'],
        function(config, $, _, e, gc, game, player) {
    var instance = null;
    var effects = new e();

    /**
     * Create a gameflow.
     * 
     * @constructor
     */
    var Flow = function() {
        instance = this;
        instance.game = null;
        instance.player = null;
        instance.bombs = [];
        instance.defuses = 0;
        instance.attempts = 0;

        instance.gc = new gc(config.serverAddress);
    };

    /**
     * Try to join a game.
     */
    Flow.prototype.joinGame = function() {
        instance.player = new player(0, $('#name').val());
        instance.gc.getCurrentGame().done(function(data) {
            if (data) {
                instance.game = data;
                instance.game.join(instance.player).done(function(data) {
                    instance.player = data;
                    effects.fadeBetween('welcome_view', 'defuse_view');
                    instance.playGame();
                });
            } else {
                effects.fadeBetween('welcome_view', 'create_game_view');
            }
        });
    };

    /**
     * Try to create a game.
     */
    Flow.prototype.createGame = function() {
        var name = $('#game_name').val();
        gc.createGame(name).done(function(game) {
            if (game) {
                // Join game
                effects.fadeBetween('create_game_view', 'defuse_view');
                instance.playGame();
            }
        });
    };

    /**
     * Play the game!
     */
    Flow.prototype.playGame = function() {
        var ringsum = 4;
        var now = 0;
        var bang = 0;
        var countdown = 0;
        var currentBombCount = 0;
        var topBomb = 0;

        $('#active_bombs_label').text(instance.bombs.length);
        $('#defusal_attempt_label').text(instance.attempts);
        $('#defuse_counter_label').text(instance.defuses);

        instance.game.listActiveBombs()
        .done(function(data) {
            instance.bombs = _.sortBy(data.active, 'explosion_at');
            now = new Date();
            bang = new Date(instance.bombs[0]['explosion_at']);
            countdown = Math.floor((bang - now)/1000);

            instance.intervalId = setInterval(function() {
                now = new Date();
                if (ringsum === 0) {
                    // Fetch current bombs
                    instance.game.listActiveBombs()
                    .then(function() {
                        // Sort bombs based on remaining time
                        currentBombCount = instance.bombs.length;
                        topBomb = instance.bombs[0].id;
                        instance.bombs = _.sortBy(data.active, 'explosion_at');
                        if (currentBombCount !== instance.bombs.length &&
                                topBomb !== instance.bombs[0].id) {
                            bang = new Date(instance.bombs[0]['explosion_at']);
                            countdown = Math.floor((bang - now)/1000);
                            effects.countdown(countdown);
                        }
                    });
                    ringsum = 4;
                } else {
                    ringsum--;
                }
                if (bang <= now) {
                    // Well, we lost the game.
                    clearInterval(instance.intervalId);
                    $('#final_defuse_count').text(instance.defuses);
                    effects.fadeBetween('defuse_view', 'game_over_view');
                }
            }, 100);

        });
    };

    /**
     * Start over.
     */
    Flow.prototype.startOver = function() {
        effects.fadeBetween('game_over_view', 'welcome_view');
    };

    /**
     * Try to defuse.
     */
    Flow.prototype.tryDefuse = function() {
        instance.game.tryDefuse(player)
        .done(function(data) {
            instance.attempts++;

            if (data.defused > 0) {
                instance.defuses++;
                effects.defuseSuccess();
            } else {
                effects.defuseFailure();
            }
        });
    };

    return Flow;
});