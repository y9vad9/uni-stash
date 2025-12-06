/* global require */
define([
  'jquery', 'util', 'login_form', 'filelist', 'sections', 'i18n!nls/msg',
  'tpl!student_panel', 'tpl!edu_plan'
], function ($, U, loginForm, filelist, sections, msg, tplStudentPanel, eduPlanTpl) {
  'use strict';

  var space = '', // current working SPACE.
          studentPanelPlaceholder = $('#student-panel-placeholder'),
          currTaskId = -1; // fill this when the task has loaded.

  // TODO: move students panel code to separate file.

  // TODO: remove explicit init().
  function init() {
    // For fixed sidebar with Bootstrap affix jQuery plugin
    $('#keyboards').affix({
      offset: {
        top: 52,
        bottom: 1
      }
    });
    loadSpaceAndMemory();
    require('filelist').init();
    loginForm.init();
    initButtonKeyboards();
    initButtonExportPdf();
    initButtonExportTxt();
    initImportTxt();
    initUploadForm();
  }

  /**
   * Buttons which insert text commands.
   * @returns {undefined}
   */
  function initButtonKeyboards() {
    $('.kbd').on('click', '.btn.btn-xs', function () {
      var data = $(this).data();
      if (data.inserts && !sections.isActiveSectionAtLatexMode()) {
        sections.insertTextToActiveSection(data.inserts, data.back);
        sections.resizeActiveTextarea();
      }
    });
  }

  function loadSpaceAndMemory() {
    return $.ajax({
      url: U.url('/api/space-memory'),
      type: 'POST',
      contentType: 'application/json'
    }).done(function (data) {
      space = data.space;
      $('#space').html(data.space);
      $('#memory').html(data.memory);
    });
  }

  /**
   * @returns {String} current working SPACE.
   */
  function getSpace() {
    return space;
  }

  /**
   * @param {Boolean} isGraph3d true if its called for 3D plots.
   * @returns {String[]} array with variables names starting from 2nd (for 2D plots) or 3rd element (for 3D plots).
   */
  function getSpaceParameters(isGraph3d) {
    return space ? space.substring(space.indexOf('[') + 1, space.indexOf(']'))
            .split(',').slice(isGraph3d ? 2 : 1) : [];
  }

  function exportAs(format) {
    var form = $('#export_settings');
    form.find('input[name="format"]').val(format);
    $.ajax({
      url: U.url('/api/export'),
      type: 'POST',
      dataType: 'json',
      // TODO: rewrite this. POST as JSON
      data: form.serialize() + '&' + sections.getParamString()
    }).done(function (data) {
      // TODO: hackish way to export LaTeX source along with
      if (format === 'pdf') {
        // If exporting PDF, export source .tex too.
        window.open(U.url('/api/export?format=' + 'tex' +
            '&filename=' + data.filename.replace(/\.pdf$/, '.tex')));
      }
      if (U.showError(data, $('#section_' + sections.getActiveSection()))) {
        return;
      }
      window.open(U.url('/api/export?format=' + format +
              '&filename=' + data.filename));
    });
  }

  function initButtonExportPdf() {
    $('#export_pdf').on('click', function (ev) {
      ev.preventDefault();
      exportAs('pdf');
    });
  }

  function initButtonExportTxt() {
    $('#export_txt').on('click', function (ev) {
      ev.preventDefault();
      exportAs('txt');
    });
  }

  function initImportTxt() {
    $('#import-form').find('input[type="file"]').on('change', function (ev) {
      var $this = $(this);
      ev.preventDefault();
      $('#import-form').ajaxSubmit({
        success: function (text) {
          var sectionsArr = text.result.split('"==="'),
                  splitRes, task, answer, res = [];
          for (var i = 0; i < sectionsArr.length; i++) {
            splitRes = sectionsArr[i].split('"---"');
            task = $.trim(splitRes[0]);
            answer = splitRes[1];
            if (task) {
              res.push({'task': task, 'answer': answer});
            }
          }
          sections.replaceWith(res);
          $this.replaceWith($this.val('').clone(true)); // Clear file input.
        }
      });
    });
  }

  function initUploadForm() {
    $('#upload-form').find('input[type="file"]').on('change', function (ev) {
      $('#upload-form').ajaxSubmit({
        success: function (resp) {
          require('filelist').render(resp);
        }
      });
    });
  }

  U.sub(U.topic.LOGIN_SUCCESS, function (topic, data) {
    if (data.userRole === U.userRole.STUDENT) {
      studentPanelPlaceholder.html(tplStudentPanel({msg: msg}));
    }
  });

  U.sub(U.topic.LOGOUT_SUCCESS, function (topic, data) {
    studentPanelPlaceholder.empty();
  });

  // TODO: !! Refactor edu* stuff and move to separate module.

  studentPanelPlaceholder.on('click', '#import-task-by-id', function (ev) {
    var taskId = $('#task-id').val();

    $.ajax({
      url: U.url('/api/tasks/' + taskId),
      method: 'GET',
      dataType: 'json',
      contentType: 'application/json'
    }).done(function (data) {
      currTaskId = taskId;
      sections.replaceWith(data.sections);
    }).fail(function () {
      console.log('Fail to get task by id');
    });
    return false;
  });

  function showEduPlan() {
    $.ajax({
      url: U.url('/api/tasks/plan'),
      type: 'GET',
      dataType: 'json'
    }).done(function (data) {
      $('#edu-plan-content').html(eduPlanTpl({msg: msg, rows: data}));
      $('#edu-plan').modal('show');
    }).fail(function () {
      console.log('Failed to get edu-plan.');
    });
  }

  studentPanelPlaceholder.on('click', '#show-edu-plan', function () {
    showEduPlan();
  });

  $('#edu-plan').on('click', '.delete', function (ev) {
    var taskId = $(this).data('task-id');
    if (!confirm('Are you sure to delete task with ID = ' + taskId + '?')) {
      return;
    }
    $.ajax({
      url: U.url('/api/tasks/' + taskId + '/delete'),
      type: 'POST'
    }).done(function () {
      alert('Deleted');
      showEduPlan();
    }).fail(function () {
      console.log('Failed to delete task');
    });
  });

  studentPanelPlaceholder.on('click', '#save-notebook-as-task', function (ev) {
    ev.preventDefault();
    var taskName = $('#task-title').val();

    if (!taskName) {
      alert('Enter task name.');
      return;
    }

    console.log('Task name', taskName);

    $.ajax({
      url: U.url('/api/tasks/?task_title=' + taskName),
      type: 'POST',
      contentType: 'application/json',
      data: JSON.stringify(sections.getMathparNotebook())
    }).done(function (data) {
      alert('Successfully saved notebook as new task.');
      console.log('Successfully saved notebook as new task.');
    }).fail(function () {
      alert('Error saving task!');
      console.log('Failed to save notebook as new task.');
    });
  });

  function getTaskInfo() {
    var sectAll = sections.getArray(),
            activeId = sections.getActiveSection(),
            activeSectionIndex = -1,
            isActiveSectionTask = false,
            solutionSections = [],
            taskSection = null,
            taskSectionId = -1,
            subtaskNumber = -1,
            i = 0;

    for (i = 0; i < sectAll.length; i++) {
      if (sectAll[i].task.indexOf('TASK') !== -1) {
        subtaskNumber++;
        taskSectionId = sectAll[i].sectionId;
      }
      if (sectAll[i].sectionId === activeId) {
        activeSectionIndex = i;
        break;
      }
    }

    taskSection = $('#section_' + taskSectionId);
    isActiveSectionTask = sectAll[activeSectionIndex].task.indexOf('TASK') !== -1;

    if (!isActiveSectionTask) { // search up until the task
      for (i = activeSectionIndex; i >= 0; i--) {
        if (sectAll[i].task.indexOf('TASK') !== -1) {
          break;
        }
        solutionSections.unshift(sectAll[i]);
      }
    }
    // search down until the next task
    for (i = activeSectionIndex + 1; i < sectAll.length; i++) {
      if (sectAll[i].task.indexOf('TASK') !== -1) {
 //       if(i !== activeSectionIndex + 1){} else {solutionSections.push(sectAll[i]);}
        break;
      }
      solutionSections.push(sectAll[i]);
    }
    if(solutionSections.length<1){   if(activeId<0) {activeId=0;}
      if(activeSectionIndex>-1) {solutionSections.push(sectAll[activeSectionIndex]);}
      else {solutionSections.push(sectAll[0]);}
    }
    return {
      currentSection: $('#section_' + activeId),
      subtaskNumber: subtaskNumber,
      solutionSections: solutionSections,
      taskSection: taskSection
    };
  }

  studentPanelPlaceholder.on('click', '#show-record-book', function (ev) {
    ev.preventDefault();
    window.open('../view/gradebook/');
  });

  studentPanelPlaceholder.on('click', '#task-check', function (ev) {
    var taskInfo = getTaskInfo();
    ev.preventDefault();
    $.ajax({
      url: U.url('/api/check'),
      type: 'POST',
      contentType: 'application/json',
      dataType: 'json',
      data: JSON.stringify({
        taskId: currTaskId,
        subtaskNumber: taskInfo.subtaskNumber,
        userSolutionSections: taskInfo.solutionSections
      })
    }).done(function (data) {
      var resPanel = taskInfo.currentSection.find('.res_panel > pre'),
              latexPanel = taskInfo.currentSection.find('.tex_panel');
      resPanel.text(resPanel.text() + '\n' + data.result);
      latexPanel.html( '<div>' + data.result + '</div>');
      MathJax.Hub.Queue(['Typeset', MathJax.Hub, latexPanel.get(0)]);
    }).fail(function () {
      console.log('Check fail');
    });
  });


studentPanelPlaceholder.on('click', '#task-checkRun', function (ev) {
  var taskInfo = getTaskInfo();
  sections.runActiveSection();
    var taskInfo = getTaskInfo();
    ev.preventDefault();
    $.ajax({
      url: U.url('/api/check'),
      type: 'POST',
      contentType: 'application/json',
      dataType: 'json',
      data: JSON.stringify({
        taskId: currTaskId,
        subtaskNumber: taskInfo.subtaskNumber,
        userSolutionSections: taskInfo.solutionSections
      })
    }).done(function (data) {
      var resPanel = taskInfo.currentSection.find('.res_panel > pre'),
              latexPanel = taskInfo.currentSection.find('.tex_panel');
      resPanel.text(resPanel.text() + '\n' + data.result);
      latexPanel.html( '<div>' + data.result + '</div>');
      MathJax.Hub.Queue(['Typeset', MathJax.Hub, latexPanel.get(0)]);
    }).fail(function () {
      console.log('Check fail');
    });
  });

  studentPanelPlaceholder.on('click', '#task-giveup', function (ev) {
    var taskInfo = getTaskInfo();
    ev.preventDefault();
    $.ajax({
      url: U.url('/api/giveup'),
      type: 'POST',
      contentType: 'application/json',
      dataType: 'json',
      data: JSON.stringify({
        taskId: currTaskId,
        subtaskNumber: taskInfo.subtaskNumber
      })
    }).done(function (data) {
      var s = data.sections,
              resPanel = taskInfo.currentSection.find('.res_panel > pre'),
              latexPanel = taskInfo.currentSection.find('.tex_panel');
      for (var i = s.length - 1; i >= 0; i--) {
        sections.addSectionAfter(s[i], taskInfo.taskSection);
      }
      // TODO: i18n messsages.
      resPanel.text(resPanel.text() + '\n' +
              'The reference solution has been inserted for this task (in ' + s.length + ' section(s)).');
      latexPanel.html(latexPanel.html() +
              '<div>The reference solution has been inserted for this task (in ' + s.length + ' section(s)).</div>');
      MathJax.Hub.Queue(['Typeset', MathJax.Hub, latexPanel.get(0)]);
    }).fail(function () {
      console.log('Giveup fail');
    });
  });

  // export public module functions
  return {
    init: init,
    getSpace: getSpace,
    loadSpaceAndMemory: loadSpaceAndMemory,
    getSpaceParameters: getSpaceParameters
  };
});
