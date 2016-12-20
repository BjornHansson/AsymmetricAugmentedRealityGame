define(['config'], function(config) {
    var Proxy = function() {};
    
    Proxy.prototype.proxy = function(url) {
        url = url.replace('http://gamemaster.example', config.serverAddress);
        return url;
    };
    
    return Proxy;
});