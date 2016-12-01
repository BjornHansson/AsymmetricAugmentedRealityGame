requirejs.config({
    baseUrl: 'assets/script/lib',
    paths: {
        jquery: 'jquery-3.1.1.min',
        bootstrap: 'bootstrap-3.3.7.min'
    },
    waitSeconds: 2,
    bundles: {
        'game_logic': ['../game_logic/game_control', '../game_logic/game', '../game_logic/player']
    }
});