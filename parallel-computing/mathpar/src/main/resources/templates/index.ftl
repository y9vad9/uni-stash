<#assign title = "Math partner">
<#assign nav_active = _("navbar.workbook")>
<#assign change_lang_link = "../" + _("navbar.changelang_locale") + "/">
<#include "common/header.ftl">

<#include "common/sidebar.ftl">
<#include "common/top_navbar.ftl">

<!-- index.ftl -->
<div class="container main">
    <div id="edu-plan" class="modal" tabindex="-1" role="dialog">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal"><span
                            aria-hidden="true">&times;</span><span
                            class="sr-only">Close</span></button>
                    <h4 class="modal-title" id="myModalLabel">Edu plan</h4>
                </div>
                <div id="edu-plan-content" class="modal-body">
                    HeyHey
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-default" data-dismiss="modal">
                        Close
                    </button>
                </div>
            </div>
        </div>
    </div>

    <!-- sidebar and sections -->
    <div id="global_log"></div>
    <div class="row">
        <!-- sections -->
        <div id="sections" class="col-md-12 sections">
            <div id="section_0" class="section">
                <div class="btn-toolbar">
                    <div class="btn-group">
                        <button class="btn btn-sm run_section" tabindex="32767"
                                title="${_("sect.run")}"
                                ><i class="glyphicon glyphicon-play"></i></button>
                        <button class="btn btn-sm toggle_latex" tabindex="32767"
                                title="${_("sect.toggle")}"
                                ><i class="glyphicon glyphicon-random"></i></button>
                        <button class="btn btn-sm add_section" tabindex="32767"
                                title="${_("sect.add")}"
                                ><i class="glyphicon glyphicon-plus"></i></button>
                    </div>
                    <div class="btn-group pull-right">
                        <button class="btn btn-sm clear_expr" tabindex="32767"
                                title="${_("sect.clear")}"
                                >C
                        </button>
                        <button class="btn btn-sm remove_section" tabindex="32767"
                                title="${_("sect.delete")}"
                                ><i class="glyphicon glyphicon-remove"></i></button>
                    </div>
                </div>
                <!-- /button toolbar -->
                <form>
                    <input type="hidden" name="section_number" value="0"/>
                    <textarea class="form-control" name="task" rows="1"></textarea>
                </form>
                <div class="res_panel"></div>
                <div class="tex_panel">${_("sect.noresult")}</div>
                <div class="tex_original"></div>
            </div>
            <!-- /section_0 -->
        </div>
        <!-- /sections -->
    </div>
    <!-- /row main -->
</div> <!-- /container -->

<#include "common/mathjax.ftl">
<#include "common/jsmain.ftl">
<#include "common/footer.ftl">
