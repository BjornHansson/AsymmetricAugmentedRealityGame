requirejs.config({
    baseUrl: 'assets/scripts',
    paths: {
        jquery: 'lib/jquery-3.1.1.min',
        bootstrap: 'lib/bootstrap-3.3.7.min',
        effects: 'view_logic/effects',
        game: 'game_logic/game',
        player: 'game_logic/player',
        game_controls: 'game_logic/game_controls'
    },
    waitSeconds: 2
});