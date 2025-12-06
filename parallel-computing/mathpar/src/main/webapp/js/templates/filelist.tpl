<% if (filelist && filelist.length > 0) { %>
<table class="table table-condensed">
  <thead>
    <tr>
      <th>#</th>
      <th>Filename</th>
      <td></td>
      <th></th>
    </tr>
  </thead>
  <tbody>
<% for (var i = 0; i < filelist.length; i++) { %>
    <tr>
      <td><%= i + 1 %></td>
      <td><a href="#" class="filename"><%= filelist[i] %></a></td>
      <td><a href="#" class="download"><i class="glyphicon glyphicon-download-alt"></i></a></td>
      <td><a href="#" class="delete">&times;</a></td>
    </tr>
<% } %>
  </tbody>
</table>
<% } else { %>
<p><%= msg.files['no files'] %></p>
<% } %>