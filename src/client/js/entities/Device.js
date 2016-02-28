var DeviceDetail = React.createClass({
  getInitialState: function() {
    return {data: []};
  },
  componentDidMount: function() {

  },
  render: function() {
    return (
      <div className="commentBox">
        <h1>Comments</h1>
      </div>
    );
  }
});

/*var DeviceForm = React.createClass({

});*/

var DeviceList = React.createClass({
  getInitialState: function() {
    return {devices: []};
  },
  loadAllDevices: function() {
    var allDevices = [
      {Name: 'Nexus5', Memory: '2GB', isRunning: true},
      {Name: 'Nexus6', Memory: '1GB', isRunning: false},
      {Name: 'Xiaomi', Memory: '2GB', isRunning: true}
    ];

    this.setState({devices: allDevices});
  },
  componentDidMount: function() {
    this.loadAllDevices();
    setInterval(this.loadAllDevices, this.props.pollInterval);
  },
  render: function() {
    return (
      <div className="DeviceList">
        <h1>All Android Devices</h1>
        <button className="btn btn-default btn-lg" type="button">Add device</button>
        <table className="table">
          <thead>
            <tr>
              <th>Name</th>
              <th>Memory</th>
              <th>Status</th>
            </tr>
          </thead>
          <tbody>
          {this.state.devices.map(function(device){
            return (
              <tr>
                <td>{device.Name}</td>
                <td>{device.Memory}</td>
                <td>{device.isRunning}</td>
              </tr>
            );
          })}
          </tbody>
        </table>
      </div>
    );
  }
});

module.exports = {
  DeviceDetail: DeviceDetail,
  DeviceList: DeviceList
}
