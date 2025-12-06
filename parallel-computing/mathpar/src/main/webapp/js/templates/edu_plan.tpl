<table class="table">
    <thead>
    <tr>
        <th><%= msg.eduplan['id'] %></th>
        <th><%= msg.eduplan['task title'] %></th>
        <th><%= msg.eduplan['delete task'] %></th>
    </tr>
    </thead>
    <tbody>
    <% for (var i = 0; i < rows.length; i++) { %>
    <tr>
        <td><%- rows[i].id %></td>
        <td><%- rows[i].taskName %></td>
        <td><a href="#" class="delete" data-task-id="<%- rows[i].id %>">&times;</a></td>
    </tr>
    <% } %>
    </tbody>
</table>
