var ContainerDetail = React.createClass({
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

var ContainerList = React.createClass({
  getInitialState: function() {
    return {containers: []};
  },
  loadAllContainers: function() {
    $.ajax({
      url: this.props.url,
      dataType: 'json',
      type: 'GET',
      success: function(data) {
        this.setState({containers: data});
      }.bind(this),
      error: function(xhr, status, err) {
        console.error(this.props.url, status, err.toString());
      }.bind(this)
    });
  },
  componentDidMount: function() {
    this.loadAllContainers();
    setInterval(this.loadAllContainers, this.props.pollInterval);
  },
  stopContainer: function(containerID) {
    $.ajax({
      url: '/containers/' + containerID + '/stop',
      dataType: 'json',
      type: 'POST',
      success: function(data) {
        //this.setState({containers: data});
        console.log(data);
      }.bind(this),
      error: function(xhr, status, err) {
        console.error(status, err.toString());
      }.bind(this)
    });
  },
  createContainer: function() {
    $.ajax({
      url: '/containers/create',
      dataType: 'json',
      type: 'POST',
      success: function(data) {
        //this.setState({containers: data});
        console.log(data);
      }.bind(this),
      error: function(xhr, status, err) {
        console.error(status, err.toString());
      }.bind(this)
    });
  },
  render: function() {
    var self = this;
    return (
      <div className="DeviceList">
        <h1>All docker containers</h1>
        <button className="btn btn-default btn-lg" type="button" onClick={self.createContainer}>Add container</button>
        <table className="table">
          <thead>
            <tr>
              <th>ID</th>
              <th>Image</th>
              <th>Status</th>
              <th></th>
            </tr>
          </thead>
          <tbody>
          {this.state.containers.map(function(container){
            return (
              <tr>
                <td>{container.Id.substr(0, 9)}</td>
                <td>{container.Image}</td>
                <td>{container.Status}</td>
                <td><button className="btn btn-danger btn-sm" type="button" onClick={self.stopContainer.bind(self, container.Id)}>Delete</button></td>
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
  ContainerDetail: ContainerDetail,
  ContainerList: ContainerList
}
