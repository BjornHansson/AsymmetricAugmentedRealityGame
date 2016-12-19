requirejs.config({
    baseUrl: 'assets/scripts',
    paths: {
        config: 'config',
        jquery: 'lib/jquery-3.1.1.min',
        bootstrap: 'lib/bootstrap-3.3.7.min',
        effects: 'view_logic/effects',
        game: 'game_logic/game',
        player: 'game_logic/player',
        game_controls: 'game_logic/game_controls'
    },
    waitSeconds: 2
});

requirejs(['config', 'jquery', 'effects', 'game_controls'],
        function(config, $, Effects, GameControls) {
    var effects = new Effects();
    var gameControls = new GameControls(config.serverAddress);
    console.log(config);
    console.log($);
    console.log(effects);
    console.log(gameControls);
    
    $('#join_game_button').click(function() { effects.fadeBetween('welcome_view', 'defuse_view'); });
    $('#disarm_button').click(function() { effects.defuseSuccess() });
    effects.countdown(30);
    gameControls.listGames().done(function (data) { console.log(data) });
    gameControls.createGame({ name: 'lalal' }).done(function (data) { console.log(data) });
});