var Device = require('../entities/Device');
var DeviceList = Device.DeviceList;
var DeviceDetail = Device.DeviceDetail;

ReactDOM.render(
  <DeviceList />,
  document.getElementById('content')
);