var Container = require('../entities/Container');
var ContainerList = Container.ContainerList;
var ContainerDetail = Container.ContainerDetail;

ReactDOM.render(
  <ContainerList url="/containers/json" pollInterval={2000} />,
  document.getElementById('content')
);