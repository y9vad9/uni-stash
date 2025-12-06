define([
  'jquery', 'i18n!nls/msg', 'util', 'graphics', 'filelist'
], function ($, msg, util, Graphics, filelist) {
  'use strict';

  var
      SECTIONS, // All section wrapper
      SECTION_CLONE, // First fresh section for cloning
      textarea, // Current active textarea.
      activeTextArea, // previous active textarea.
      lastAddedSectionId = 0, // ID of last added section
      activeSectionId = 0;         // ID of active (selected) section

  /**
   * Setup data and event handlers.
   * @returns {undefined}
   */
  function init() {
    SECTIONS = $('#sections');
    textarea = $('textarea[name="task"]');
    activeTextArea = textarea.eq(0);
    initButtonRun();
    initButtonSwitchView();
    initButtonAddSection();
    initButtonCleanAllExpr();
    initButtonRemoveSection();
    initTextareaHotkeys();
    initTextareaSwitchActive();
    initClickLatex();

    // Clone first section with all bound events.
    SECTION_CLONE = $('#section_0').clone(true);

    if ($.fn.autosize) {   // Help pages don't load autosize script.
      textarea.autosize(); // Use jQuery plugin http://www.jacklmoore.com/autosize
    }
  }

  /**
   *
   * @param {jQuery} $elInsideSection element inside some section.
   * @returns {Number} ID of section which contains given element.
   */
  function getSectionId($elInsideSection) {
    return parseInt($elInsideSection.parents('.section').
        find('input[name="section_number"]').val(), 10);
  }

  function initButtonRun() {
    $('.run_section').on('click', function () {
      setActiveSection(getSectionId($(this)));
      runActiveSection();
    });
  }

  /**
   * Makes section with given ID active.
   * @param {Number} sectionId ID of section to make active.
   * @returns {undefined}
   */
  function setActiveSection(sectionId) {
    activeSectionId = sectionId;
    // TODO: rewrite this selector.
    activeTextArea.parent().parent().removeClass('active');
    activeTextArea = $('#section_' + sectionId).find('textarea[name="task"]');
    activeTextArea.parent().parent().addClass('active');
  }

  /**
   * Button to toggle input\output modes.
   * @returns {undefined}
   */
  function initButtonSwitchView() {
    $('.toggle_latex').on('click', function () {
      var thisSectionId = getSectionId($(this));
      setActiveSection(thisSectionId);
      toggleInOut(thisSectionId);
    });
  }

  /**
   * Button to add clone of first section under the active section.
   * @returns {undefined}
   */
  function initButtonAddSection() {
    $('.add_section').on('click', function () {
      addEmptySectionAfter(getSectionId($(this)));
    });
  }

  function initButtonCleanAllExpr() {
    $('.clear_expr').on('click', function () {
      setActiveSection(getSectionId($(this)));
      cleanAllExpr();
    });
  }

  function initButtonRemoveSection() {
    $('.remove_section').on('click', function () {
      removeSection(getSectionId($(this)));
    });
  }

  function initTextareaHotkeys() {
    textarea.on('keydown', function (e) {
      var
          $this = $(this),
          isCtrlEnter = e.which === 13 && e.ctrlKey,
          isCtrlDel = e.which === 46 && e.ctrlKey,
          isCtrlAltR = e.which === 82 && e.ctrlKey && e.altKey,
          isCtrlAltC = e.which === 67 && e.ctrlKey && e.altKey;
      if (isCtrlEnter) {
        runActiveSection();
        $this.trigger('blur');
      } else if (isCtrlDel) {
        removeSection(activeSectionId);
      } else if (isCtrlAltC) {
        cleanAllExpr();
      } else if (isCtrlAltR) {
        toggleInOut(activeSectionId);
      }
    });
  }

  /**
   * Highlight active section on focus.
   * @returns {undefined}
   */
  function initTextareaSwitchActive() {
    textarea.on('focus', function () {
      var currentSectionId = parseInt($(this).siblings('input[name=section_number]').val(), 10);
      setActiveSection(currentSectionId);
    });
  }

  function initClickLatex() {
    $('.tex_panel').on('click', function () {
      var currentSectionId = $(this).parent().
          find('input[name="section_number"]').val();
      setActiveSection(currentSectionId);
      showIn(currentSectionId);
      $(this).parent().find('textarea[name="task"]').trigger('focus');
    });
  }

  /**
   * Runs current active section.
   */
  function runActiveSection() {
    util.submitToCalc({sectionId: activeSectionId, task: activeTextArea.val()})
        .done(function (resp) {
          // TODO: remove circular dependencies.
          require('sidebar').loadSpaceAndMemory().done(function () {
            renderAnswer(resp);
          });
        });
  }

  function addSection1(s) {
    // TODO: remove copy-paste.
    var
        newSectNumber = getNextSectionId(),
        newSect = SECTION_CLONE.clone(true),
        newForm = newSect.find('form');

    activeSectionId = newSectNumber;
    newSect.attr('id', 'section_' + newSectNumber);
    newForm.find('input[name="section_number"]').val(newSectNumber);
    newSect.appendTo(SECTIONS);
    newForm.find('textarea[name="task"]').val(s.task || '');
    newSect.find('.res_panel').html('<pre>' + (s.answer || '') + '</pre>');
    newForm.find('textarea[name="task"]').autosize();
  }




  function renderLatex(latexOutput, texLines) {
    latexOutput.empty();
    // Wrap each non-empty line of LaTeX output with <div>.
    latexOutput.append($.map(texLines, function (line) {
      return !line.match(/^\$?\s*\$?$/) ? $('<div>' + line + '</div>') : null;
    }));
    MathJax.Hub.Queue(['Typeset', MathJax.Hub, latexOutput.get(0)]);
  }

  /**
   * Renders output after successfull task calculation.
   * @param {Array} taskResp MathparResponse with task result.
   * @returns {undefined}
   */
  function renderAnswer(taskResp) {
    var
        sectionId = taskResp.sectionId,
        section = $('#section_' + sectionId),
        latexOutput = section.children('.tex_panel'),
        texLines = taskResp && taskResp.latex && taskResp.latex.split('\n\n'),
        latexOriginal = section.children('.tex_original'),
        output = section.children('.res_panel');
    if (util.showError(taskResp, section)) {
      return;
    }
    latexOriginal.text(taskResp.latex);
    renderLatex(latexOutput, texLines);
    output.html('<pre>' + taskResp.result + '</pre>');
    if (taskResp.task.indexOf('\\toFile') >= 0) {
      // TODO: remove circular dependencies.
      require('filelist').update();
    }
    new Graphics(taskResp);
    showOut(sectionId);
  }

  /**
   * Sends request to clear all expressions with \clean() function.
   */
  function cleanAllExpr() {
    util.submitToCalc({
      sectionId: activeSectionId,
      task: '\\clean();'
    }).done(function (data) {
      if (util.showError(data, $('#section_' + activeSectionId))) {
        return;
      }
      alert(msg.global['cleanComplete']);
    });
  }

  /**
   * Sets up next ID for new section.
   * @return next new section ID.
   */
  function getNextSectionId() {
    lastAddedSectionId = lastAddedSectionId + 1;
    return lastAddedSectionId;
  }

  /**
   * Appends new section below given by cloning given node.
   * @param {Number} afterId section ID after which new section is inserted.
   */
  function addEmptySectionAfter(afterId) {
    // TODO: remove copy-paste. (see addSectionAfter)
    var
        newSectNumber = getNextSectionId(),
        newSect = SECTION_CLONE.clone(true),
        newForm = newSect.find('form');

    activeSectionId = newSectNumber;
    newSect.attr('id', 'section_' + newSectNumber);
    newForm.children('input[name="section_number"]').val(newSectNumber);
    newSect.insertAfter('#section_' + afterId);
    newForm.children('textarea[name="task"]').autosize();
  }

  function insertTextToActiveSection(text, back) {
    util.insertTextAtCursor(activeTextArea.get(0), text, back);
  }

  /**
   * Removes section with given ID.
   * @param {Number} sectionId section ID to remove.
   */
  function removeSection(sectionId) {
    if (SECTIONS.children().length > 1) {
      $('#section_' + sectionId).remove();
      activeSectionId = -1;
    }
  }

  /**
   * Toggles view state of section between input\output modes.
   * @param {number} sectionId section ID
   */
  function toggleInOut(sectionId) {
    var section = $('#section_' + sectionId);
    section.children('form').toggle();
    section.children('div.res_panel').toggle();
    section.children('div.tex_panel').toggle();
    section.children('div.graph-additional').toggle();
  }

  function showOut(sectionId) {
    var section = $('#section_' + sectionId);
    section.children('form').hide();
    section.children('div.res_panel').hide();
    section.children('div.tex_panel').show();
    section.children('div.graph-additional').show();
  }

  function showIn(sectionId) {
    var section = $('#section_' + sectionId);
    section.children('form').show();
    section.children('div.res_panel').show();
    section.children('div.tex_panel').hide();
    section.children('div.graph-additional').hide();
  }

  function getActiveSection() {
    return activeSectionId;
  }

  function resizeActiveTextarea() {
    activeTextArea.trigger('resize');
  }

  function isActiveSectionAtLatexMode() {
    return $('#section_' + activeSectionId).children('.tex_panel').is(':visible');
  }

  function getArray() {
    var res = [];
    $('.section').each(function () {
      var $this = $(this);
      res.push({
        'sectionId': Number($this.find('input[name="section_number"]').val()),
        'task': $this.find('textarea[name="task"]').val(),
        'answer': $this.find('.res_panel').text(),
        'latex': $this.find('.tex_original').text()
      });
    });
    return res;
  }

  function getParamString() {
    var arr = getArray(), res = '', ai;
    for (var i = 0, sz = arr.length; i < sz; i++) {
      ai = arr[i];
      res = res +
          'task=' + encodeURIComponent(ai.task) +
          '&answer=' + encodeURIComponent(ai.answer) +
          '&latex=' + encodeURIComponent(ai.latex);
      if (i !== sz - 1) {
        res = res + '&';
      }
    }
    return res;
  }

  function getMathparNotebook() {
    return {sections: getArray()};
  }

  function replaceWith(sections) {
    if (sections.length === 0) {
      return;
    }
    removeAllSections();
    for (var i = 0; i < sections.length; i++) {
      addSection(sections[i]);
    }
  }

  function addSectionAfter(s, after) {
    var
        newSectNumber = getNextSectionId(),
        newSect = SECTION_CLONE.clone(true),
        newForm = newSect.find('form');

    activeSectionId = newSectNumber;
    newSect.attr('id', 'section_' + newSectNumber);
    newForm.find('input[name="section_number"]').val(newSectNumber);
    newSect.insertAfter(after);
    newForm.find('textarea[name="task"]').val(s.task || '');
    newSect.find('.res_panel').html('<pre>' + (s.answer || '') + '</pre>');
    newForm.find('textarea[name="task"]').autosize();
  }

  /**
   * Adds new section.
   * @param {MathparSection} s section object to append.
   * @returns {undefined}
   */
  function addSection(s) {
    // TODO: remove copy-paste.
    var
        newSectNumber = getNextSectionId(),
        newSect = SECTION_CLONE.clone(true),
        newForm = newSect.find('form');

    activeSectionId = newSectNumber;
    newSect.attr('id', 'section_' + newSectNumber);
    newForm.find('input[name="section_number"]').val(newSectNumber);
    newSect.appendTo(SECTIONS);
    newForm.find('textarea[name="task"]').val(s.task || '');
    newSect.find('.res_panel').html('<pre>' + (s.answer || '') + '</pre>');
    newForm.find('textarea[name="task"]').autosize();
  }

  function removeAllSections() {
    SECTIONS.empty();
    lastAddedSectionId = 0;
    activeSectionId = 0;
  }

  return {
    init: init,
    addSectionAfter: addSectionAfter,
    insertTextToActiveSection: insertTextToActiveSection,
    getActiveSection: getActiveSection,
    resizeActiveTextarea: resizeActiveTextarea,
    isActiveSectionAtLatexMode: isActiveSectionAtLatexMode,
    getArray: getArray,
    getParamString: getParamString,
    getMathparNotebook: getMathparNotebook,
    replaceWith: replaceWith
  };
});
