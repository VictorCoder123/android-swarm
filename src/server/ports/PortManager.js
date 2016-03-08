// Port manager
// TODO: Use ssh2 library to get real unused ports

// Import core dependencies
var Client = require('ssh2').Client;

var PortManager = function (min, max) {
  var all = [];
  for(var i=min; i<max; i++){
    all.push(i);
  }
  this.ports = new Set(all);
}

/**
 * Check if given port is available
 * @param  {Integer}  port
 * @return {Boolean}
 */
PortManager.prototype.isAvailable = function (port) {
  this.ports.has(port);
}

/**
 * Take given port from port pool
 * @param  {Integer}  port
 * @return {Boolean}
 */
PortManager.prototype.takePort = function (port) {
  return this.ports.delete(port);
}

/**
 * Release given port into port pool
 * @param  {Integer} port
 * @return {void}
 */
PortManager.prototype.releasePort = function (port) {
  this.ports.add(port);
}

/**
 * Take random available port from pool
 * @return {Integer} one available port
 */
PortManager.prototype.takeFirstAvailablePort = function () {
  var iter = this.ports.values();
  var port = iter.next().value;
  this.takePort(port);
  return port;
}

module.exports = PortManager;











