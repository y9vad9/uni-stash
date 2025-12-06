<div id="section_${sectionNum}" class="section">
  <div class="btn-toolbar">
    <div class="btn-group">
      <button class="btn btn-sm run_section" tabindex="32767"
              title="${_("sect.run")}"
              ><i class="glyphicon glyphicon-play"></i></button>
      <button class="btn btn-sm toggle_latex" tabindex="32767"
              title="${_("sect.toggle")}"
              ><i class="glyphicon glyphicon-random"></i></button>
    </div>
    <div class="btn-group pull-right">
      <button class="btn btn-sm clear_expr" tabindex="32767"
              title="${_("sect.clear")}"
              >C</button>
    </div>
  </div>
  <form>
    <input type="hidden" name="section_number" value="${sectionNum}" />
    <textarea class="form-control" name="task" rows="${rowsCnt}">${exampleString}</textarea>
  </form>
  <div class="res_panel"></div>
  <div class="tex_panel">${_("sect.noresult")}</div>
  <div class="tex_original"></div>
</div>