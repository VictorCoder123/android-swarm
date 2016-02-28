// Import core dependencies
var express = require('express');
var Docker = require('dockerode');

var containerRouter = express.Router();
var docker = Docker({host: 'http://129.59.105.238', port: 80});

// Return all existing containers in JSON format
containerRouter.get('/json', function(req, res){
  docker.listContainers(function(err, containers){
    res.json(containers);
  });
});

// Render all containers in html page
containerRouter.get('/', function(req, res){
  res.render('contents/container', {});
});

module.exports = containerRouter;