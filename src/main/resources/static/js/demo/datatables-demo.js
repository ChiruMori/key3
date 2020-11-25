// Call the dataTables jQuery plugin
$(document).ready(function () {
  $('#dataTable').DataTable({
    buttons: [{
      action: () => alert('new'),
      className: 'btn btn-primary',
      text: '新建'
    }]
  });
});