<button class="btn btn-info btn-block" data-toggle="collapse" data-target="#kbd_student">
    Student
</button>
<div id="kbd_student" class="kbd collapse in" style="height: auto;">
    <div class="well well-small">
        <form>
            <input id="task-title" class="form-control" type="text"
                   placeholder="<%= msg.student['task title placeholder'] %>">
            <button id="save-notebook-as-task" class="btn btn-default">
                <%= msg.student['save notebook to db'] %></button>
        </form>
        <form>
            <input id="task-id" class="form-control" type="text"
                   placeholder="<%= msg.student['task id placeholder'] %>">
            <button id="import-task-by-id" class="btn btn-default">
                <%= msg.student['load task by id'] %></button>
        </form>
        <button id="show-edu-plan" class="btn btn-default">
            <%= msg.student['show edu plan'] %>
        </button>
        <button id="show-record-book" class="btn btn-default">
            <%= msg.student['show record book'] %>
        </button>
        <button id="task-check" class="btn btn-default">
            <%= msg.student['check'] %>
        </button>
        <button id="task-checkRun"  class="btn btn-sm run_section">
            <%= msg.student['run and check'] %>
        </button>
        <button id="task-giveup" class="btn btn-default">
            <%= msg.student['give up'] %>
        </button>
    </div>
</div>
