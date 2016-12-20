define(['jquery'], function($) {
    var intervalId = 0;
    
    var Effects = function() {};

    /**
     * Show the defuse failure message.
     */
    Effects.prototype.defuseFailure = function() {
        label = $('#disarm_fail');
        popup(label);
    };

    /**
     * Show the defuse success message.
     */
    Effects.prototype.defuseSuccess = function() {
        label = $('#disarm_success');
        popup(label);
    };

    /**
     * Fade between two views.
     * 
     * @param from - The name of the view that will be hidden
     * @param to - The name of the view that will be shown
     */
    Effects.prototype.fadeBetween = function(from, to) {
        from = $('#' + from);
        to = $('#' + to);

        from.fadeOut(300, function() {
            to.css('display', 'none').removeClass('hidden').fadeIn(300);
        });
    };

    /**
     * Initialize the countdown display.
     * 
     * @param timer - The number of seconds that remain
     */
    Effects.prototype.countdown = function(timer) {
        clearInterval(intervalId);
        var label = $('#countdown');

        label.text(timer);

        intervalId = setInterval(function() {
            if (--timer >= 0) {
                label.text(timer);
            } else {
                clearInterval(intervalId);
            }
        }, 1000);
    };
    
    function popup(o) {
        o.css('display', 'none').removeClass('hidden').fadeIn(300, function(){
            setTimeout(function() {
                o.fadeOut(300);
            }, 800);
        });
    }

    return Effects;
});