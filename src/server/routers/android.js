// Import core dependencies
var express = require('express');
var Docker = require('dockerode');

var AndroidRouter = express.Router();
var docker = Docker({host: 'http://129.59.105.238', port: 80});

AndroidRouter.get('/', function(req, res){

});

module.exports = AndroidRouter;