requirejs.config({
    baseUrl: 'assets/scripts/lib',
    paths: {
        jquery: 'jquery-3.1.1.min',
        bootstrap: 'bootstrap-3.3.7.min'
    },
    waitSeconds: 2,
    bundles: {
        'game_logic': ['../game_logic/game_control', '../game_logic/game', '../game_logic/player'],
        'view_logic': ['../view_logic/effects']
    }
});