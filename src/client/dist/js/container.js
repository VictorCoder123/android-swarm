(function e(t,n,r){function s(o,u){if(!n[o]){if(!t[o]){var a=typeof require=="function"&&require;if(!u&&a)return a(o,!0);if(i)return i(o,!0);var f=new Error("Cannot find module '"+o+"'");throw f.code="MODULE_NOT_FOUND",f}var l=n[o]={exports:{}};t[o][0].call(l.exports,function(e){var n=t[o][1][e];return s(n?n:e)},l,l.exports,e,t,n,r)}return n[o].exports}var i=typeof require=="function"&&require;for(var o=0;o<r.length;o++)s(r[o]);return s})({1:[function(require,module,exports){
'use strict';

var ContainerDetail = React.createClass({
  displayName: 'ContainerDetail',

  getInitialState: function getInitialState() {
    return { data: [] };
  },
  componentDidMount: function componentDidMount() {},
  render: function render() {
    return React.createElement(
      'div',
      { className: 'commentBox' },
      React.createElement(
        'h1',
        null,
        'Comments'
      )
    );
  }
});

/*var DeviceForm = React.createClass({

});*/

var ContainerList = React.createClass({
  displayName: 'ContainerList',

  getInitialState: function getInitialState() {
    return { containers: [] };
  },
  loadAllContainers: function loadAllContainers() {
    $.ajax({
      url: this.props.url,
      dataType: 'json',
      type: 'GET',
      success: function (data) {
        this.setState({ containers: data });
      }.bind(this),
      error: function (xhr, status, err) {
        console.error(this.props.url, status, err.toString());
      }.bind(this)
    });
  },
  componentDidMount: function componentDidMount() {
    this.loadAllContainers();
    setInterval(this.loadAllContainers, this.props.pollInterval);
  },
  stopContainer: function stopContainer(containerID) {
    $.ajax({
      url: 'containers/' + containerID + '/stop',
      dataType: 'json',
      type: 'POST',
      success: function (data) {
        //this.setState({containers: data});
        console.log(data);
      }.bind(this),
      error: function (xhr, status, err) {
        console.error(status, err.toString());
      }.bind(this)
    });
  },
  createContainer: function createContainer() {
    $.ajax({
      url: 'containers/create',
      dataType: 'json',
      type: 'POST',
      success: function (data) {
        //this.setState({containers: data});
        console.log(data);
      }.bind(this),
      error: function (xhr, status, err) {
        console.error(status, err.toString());
      }.bind(this)
    });
  },
  render: function render() {
    var self = this;
    return React.createElement(
      'div',
      { className: 'DeviceList' },
      React.createElement(
        'h1',
        null,
        'All docker containers'
      ),
      React.createElement(
        'button',
        { className: 'btn btn-default btn-lg', type: 'button', onClick: self.createContainer },
        'Add container'
      ),
      React.createElement(
        'table',
        { className: 'table' },
        React.createElement(
          'thead',
          null,
          React.createElement(
            'tr',
            null,
            React.createElement(
              'th',
              null,
              'ID'
            ),
            React.createElement(
              'th',
              null,
              'Image'
            ),
            React.createElement(
              'th',
              null,
              'Status'
            ),
            React.createElement('th', null)
          )
        ),
        React.createElement(
          'tbody',
          null,
          this.state.containers.map(function (container) {
            return React.createElement(
              'tr',
              null,
              React.createElement(
                'td',
                null,
                container.Id.substr(0, 9)
              ),
              React.createElement(
                'td',
                null,
                container.Image
              ),
              React.createElement(
                'td',
                null,
                container.Status
              ),
              React.createElement(
                'td',
                null,
                React.createElement(
                  'button',
                  { className: 'btn btn-danger btn-sm', type: 'button', onClick: self.stopContainer.bind(self, container.Id) },
                  'Delete'
                )
              )
            );
          })
        )
      )
    );
  }
});

module.exports = {
  ContainerDetail: ContainerDetail,
  ContainerList: ContainerList
};

},{}],2:[function(require,module,exports){
'use strict';

var Container = require('../entities/Container');
var ContainerList = Container.ContainerList;
var ContainerDetail = Container.ContainerDetail;

ReactDOM.render(React.createElement(ContainerList, { url: 'containers/json', pollInterval: 2000 }), document.getElementById('content'));

},{"../entities/Container":1}]},{},[2]);
