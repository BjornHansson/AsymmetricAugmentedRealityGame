requirejs.config({
    baseUrl: 'assets/scripts',
    paths: {
        config: 'config',
        proxy: 'proxy',
        jquery: 'lib/jquery-3.1.1.min',
        underscore: 'lib/underscore-1.8.3.min',
        bootstrap: 'lib/bootstrap-3.3.7.min',
        effects: 'view_logic/effects',
        flow: 'flow',
        game: 'game_logic/game',
        player: 'game_logic/player',
        game_controls: 'game_logic/game_controls'
    },
    waitSeconds: 2
});

requirejs(['config', 'jquery', 'effects', 'flow', 'game_controls'],
        function(config, $, Effects, Flow, GameControls) {
    var effects = new Effects();
    var flow = new Flow();
    var gameControls = new GameControls(config.serverAddress);

    $('#join_game_button').click(function() { flow.joinGame() });
    $('#start_over_button').click(function() { flow.startOver() });
    $('#disarm_button').click(function() { flow.tryDefuse() });
});