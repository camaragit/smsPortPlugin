var exec = require('cordova/exec');
var safesmsExport = {};

safesmsExport.coolMethod = function (arg0, success, error) {
    exec(success, error, 'smsport', 'coolMethod', [arg0]);
};
safesmsExport.ecoutersms = function (port, success, error) {
    exec(success, error, 'smsport', 'ecoutersms', [port]);
};

safesmsExport.envoyesms = function (numero,message,port, success, error) {
    exec(success, error, 'smsport', 'sendsms', [numero,message,port]);
};
module.exports = safesmsExport;