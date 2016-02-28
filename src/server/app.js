// Main dependencies
var express = require('express');

// Third party middlewares
var morgan       = require('morgan');
var cookieParser = require('cookie-parser');
var bodyParser   = require('body-parser');
var session      = require('express-session');

// initialise express
var app = express();
var containerRouter = require('./routers/container');
var androidRouter = require('./routers/android');

// Serve static files in localhost:3000/static/assets/hi.png
app.use('/static', express.static(__dirname + '/../client'));
app.set('views', __dirname + '/../client/views');
app.set('view engine', 'jade');

// Set up our express application
app.use(morgan('dev')); // log every request to the console
app.use(cookieParser()); // read cookies (needed for auth)
app.use(bodyParser()); // get information from html forms

// pass req for template rendering
app.use(function(req, res, next){
	res.locals.req = req;
	next();
});

app.use('/containers', containerRouter);
app.use('/androids', androidRouter);


/*app.get('*', function(req, res) {
	// this route will respond to all requests with the contents of your index
	// template. Doing this allows react-router to render the view in the app.
    res.render('index');
});*/



// start the server
var server = app.listen(process.env.PORT || 3000, function() {
	console.log('\nServer ready on port %d\n', server.address().port);
});
