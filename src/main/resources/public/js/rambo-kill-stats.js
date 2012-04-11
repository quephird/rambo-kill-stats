$(document).ready( function() {
  $('#chart-select').change(function() {
    var imageUri = $('#chart-select').val();
    $('#chart-image').attr('src', imageUri);
  });
});