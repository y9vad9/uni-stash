<#assign title = "Math partner Help">
<#assign nav_active = _("navbar.help")>
<#assign change_lang_link = "../../" + _("navbar.changelang_locale") + "/help/${curr_page_name}">
<#assign inHelp=true>

<#include "../common/header.ftl">

<#include "../common/top_navbar.ftl">
<div class="container well">
  <div class="loading help"></div>
  <a href="./">${_("help.back_to_toc")}</a>

  ${help_content}

  <a href="./">${_("help.back_to_toc")}</a>
</div>

<#include "../common/mathjax.ftl">
<#include "../common/jsmain.ftl">
<#include "../common/footer.ftl">