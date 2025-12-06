package com.mathpar.web.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mathpar.func.Page;
import com.mathpar.web.db.dao.DbTasks;
import com.mathpar.web.entity.MathparNotebook;
import com.mathpar.web.entity.TaskInEduPlan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping(value = "/api/tasks")
public class Tasks {
    private DbTasks dbTasks;

    @Autowired
    public void setDbTasks(DbTasks dbTasks) {
        this.dbTasks = dbTasks;
    }

    @RequestMapping(value = "/plan", method = RequestMethod.GET)
    public List<TaskInEduPlan> getPlanForCurrentUser(@PageParam Page page) {
        return dbTasks.getPlanForUser(page.getUserId());
    }

    @RequestMapping(value = "/{taskId}", method = RequestMethod.GET)
    public MathparNotebook getAllSubtasks(@PathVariable("taskId") long taskId) throws IOException {
        return dbTasks.getTasks(taskId);
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public void newTask(@RequestBody MathparNotebook mathparNotebook,
                        @RequestParam("task_title") String taskName)
            throws JsonProcessingException {
        Long taskId = dbTasks.saveAsNewTask(mathparNotebook, taskName);
        // TODO: insert to proper group.
        if (taskId != null) { // If this is a new task.
            dbTasks.insertTaskToEduPlan(1, taskId);
        }
    }

    @RequestMapping(value = "/{taskId}/delete", method = RequestMethod.POST)
    public void deleteTask(@PathVariable("taskId") long taskId) {
        dbTasks.deleteById(taskId);
    }
}
