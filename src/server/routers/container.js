
// Import core dependencies
var express = require('express');
var Docker = require('dockerode');
var Q = require('q');

// Import customized dependencies
var PortManager = require('../ports/PortManager');


var containerRouter = express.Router();
var docker = Docker({host: 'http://129.59.105.238', port: 80});
var portManager = new PortManager(6900, 6950);
var portMap = {}; // Map container ID to its port

/**
 * Return promise with description of all existing images
 * @param  {Docker} docker
 * @return {Promise}
 */
var getImages = function (docker) {
  var deferred = Q.defer();
  docker.listImages(function(err, data){
    if(err) deferred.reject(err);
    else deferred.resolve(data);
  })
  return deferred.promise;
}

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

// Remove a container specified by Container ID
containerRouter.post('/:containerID/remove', function(req, res){
  var container = docker.getContainer(req.params.containerID);
  var opts = { force: true };
  container.remove(opts, function(err, data){
    if(err) {
      console.log(err);
    }
    else{
      var port = portMap[container.id];
      portManager.releasePort(port);
      res.json(data);
    }

  });
});

// Create new container and return container Info in JSON format
containerRouter.post('/create', function(req, res){
  getImages(docker)
    .then(function(images){
      var target_images = images.filter(function(image){
        var tags = image.RepoTags;
        var imageExists = false;
        var re = /android-swarm/;
        // Test regex on every tag of current image
        tags.forEach(function(tag){
          if(re.test(tag)) imageExists = true;
        });
        if(imageExists) return true;
      });
      // Return android swarm image or NOT FOUND error
      if(target_images.length == 0)
        throw new Error('Android Swarm Image does not exist.');
      else{
        // Get the first matched image
        var android_swarm_image = target_images[0];
        return android_swarm_image;
      }
    })
    .then(function(android_swarm_image){
      // Get first available port from server to connect container
      var port = portManager.takeFirstAvailablePort();
      console.log(port);
      var opts = {
        Image: android_swarm_image.Id,   // android-swarm image ID
        ExposedPorts: {
          '6901/tcp': {},
          '5901/tcp': {}
          //'6000/tcp': {}
        },
        HostConfig: {
          PortBindings: { "6901/tcp": [{ "HostPort": port.toString() }] },
        }
      };
      docker.createContainer(opts, function(err, newContainer){
        // Start container after creation
        newContainer.start(function(err, data){
          if(err) {
            portManager.releasePort(port);
            throw err;
          }
          portMap[newContainer.id] = port; // Add used port to map
          res.json(data);
        });

      });
    })
    .catch(function(err){
      err = err instanceof Error ? err : new Error(err);
      console.log(err);
      res.json(err);
    });

});

module.exports = containerRouter;







