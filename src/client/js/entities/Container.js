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
  render: function() {
    return (
      <div className="DeviceList">
        <h1>All docker containers</h1>
        <button className="btn btn-default btn-lg" type="button">Add container</button>
        <table className="table">
          <thead>
            <tr>
              <th>ID</th>
              <th>Image</th>
              <th>Status</th>
            </tr>
          </thead>
          <tbody>
          {this.state.containers.map(function(container){
            return (
              <tr>
                <td>{container.Id}</td>
                <td>{container.Image}</td>
                <td>{container.Status}</td>
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
