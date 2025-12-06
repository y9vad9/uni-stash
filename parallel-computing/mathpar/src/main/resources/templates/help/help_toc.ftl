<#assign title = "Math partner Help">
<#assign nav_active = _("navbar.help")>
<#assign change_lang_link = "../../" + _("navbar.changelang_locale") + "/help/">
<#assign inHelp=true>

<#include "../common/header.ftl">

<#include "../common/top_navbar.ftl">
<div class="container well">
  <h1>${_("help.toc_title")}</h1>
  <ul>
    <#list help_toc as topic>
    <li>
      <a href="${topic.filename}">${topic.title}</a>
      <ul>
        <#list (topic.subtopics) as subtopic>
        <li>
          <a href="${topic.filename}##{subtopic.anchor}">${subtopic.title}</a>
        </li>
        </#list>
      </ul>
    </li>
    </#list>
  </ul>
</div>

<#include "../common/mathjax.ftl">
<#include "../common/jsmain.ftl">
<#include "../common/footer.ftl">