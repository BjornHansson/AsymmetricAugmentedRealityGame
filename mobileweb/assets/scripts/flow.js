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
                instance.game.join(instance.player).then(function(data2) {
                    instance.player = data2;
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
        
        instance.defuses = 0;
        instance.attempts = 0;
        $('#defusal_attempt_label').text(instance.attempts);
        $('#defuse_counter_label').text(instance.defuses);

        instance.game.listActiveBombs()
        .done(function(data) {
            instance.bombs = _.sortBy(data.active, 'explosion_at');
            currentBombCount = instance.bombs.length;
            now = new Date();
            bang = new Date(instance.bombs[0]['explosion_at']);
            countdown = Math.floor((bang - now)/1000);
            effects.countdown(countdown);
            $('#active_bombs_label').text(instance.bombs.length);

            instance.intervalId = setInterval(function() {
                if (ringsum === 0) { // Fetch new bombs twice a second
                    // First, keep track of current affairs
                    if (instance.bombs.length > 0) {
                        topBomb = instance.bombs[0].id;
                    } else {
                        topBomb = 0;
                    }
                    
                    
                    // Then, fetch a new bomb list
                    instance.game.listActiveBombs()
                    .then(function(bombs) {
                        // OK, we have a list of bombs. Print it.
                        console.log(bombs);
                        
                        // Then, do stuff to it
                        instance.bombs = _.sortBy(bombs.active, 'explosion_at');
                        if (instance.bombs.length > 0 && topBomb != instance.bombs[0].id) {
                            // OK, the list has been updated. Let's do things.
                            bang = new Date(instance.bombs[0]['explosion_at']);
                            countdown = Math.floor((bang - now)/1000);
                            effects.countdown(countdown);
                        }
                    });
                    ringsum = 4;
                } else { // Just update stuff in this cycle
                    ringsum--;
                }

                $('#active_bombs_label').text(instance.bombs.length);
                
                // Update countdown timer
                if (instance.bombs.length === 0) {
                    effects.countdown(0);
                    ringsum = 0; // Fetch a new list of bombs
                }
                
                // Detect lost games
                now = new Date();
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
        instance.game.tryDefuse(instance.player)
        .done(function(data) {
            instance.attempts++;
            $('#defusal_attempt_label').text(instance.attempts);

            if (data.defused > 0) {
                removeBomb(data.defused);
                $('#active_bombs_label').text(instance.bombs.length);
                
                instance.defuses++;
                effects.defuseSuccess();
                $('#defuse_counter_label').text(instance.defuses);
            } else {
                effects.defuseFailure();
            }
        });
    };
    
    function removeBomb(id) {
        console.log('removing ' + id);
        console.log('before:');
        console.log(instance.bombs);
        instance.bombs = _.find(instance.bombs,
                function(bomb) { return bomb.id !== id });
        if (!instance.bombs) {
            instance.bombs = [];
        }
        console.log('after:');
        console.log(instance.bombs);
    }

    return Flow;
});