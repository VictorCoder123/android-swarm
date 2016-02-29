// Import core dependencies
var express = require('express');
var Docker = require('dockerode');

var containerRouter = express.Router();
var docker = Docker({host: 'http://129.59.105.238', port: 80});

// Render all containers in html page
containerRouter.get('/', function(req, res){
  res.render('contents/container', {});
});

// Return all existing containers in JSON format
containerRouter.get('/json', function(req, res){
  docker.listContainers(function(err, containers){
    res.json(containers);
  });
});

// Stop a container specified by Container ID
containerRouter.post('/:containerID/stop', function(req, res){
  var container = docker.getContainer(req.params.containerID);
  container.stop(function(err, data){
    if(err) console.log(err);
    res.json(data);
  });
});

// Create new container and return container Info in JSON format
containerRouter.post('/create', function(req, res){
  var opts = {
    Image: '1fc48f553b07'  // android-emulator image ID
  }
  docker.createContainer(opts, function(err, newContainer){
    // Start container after creation
    newContainer.start(function(err, data){
      if(err) console.log(err);
      res.json(data);
    });
  });
});

module.exports = containerRouter;