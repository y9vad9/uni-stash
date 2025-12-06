<#assign title = "Math partner">
<#assign nav_active = _("navbar.about")>
<#assign change_lang_link = "../" + _("navbar.changelang_locale") + "/contact.html">

<#include "common/header.ftl">
<#include "common/top_navbar.ftl">

<div class="container">
  <div class="row">
    <div class="well jumbotron col-md-8 col-md-offset-2">
      <h3>${_("contact.title")}</h3>
      <p>${_("contact.text")}</p>
      <p> &copy; ${_("contact.titleLTD")} 2011 </p>
      <p><b>Email:</b> <a href="mailto:info@mathpar.com">info@mathpar.com</a></p>
    </div>
  </div>
  <div class="row">
    <div class="well col-md-8 col-md-offset-2">
      <h3>${_("contact.thanks")}</h3>
      ${_("contact.grants")}
    </div>
  </div>
</div>

<script src="${path_prefix}js/libs/require.js"></script>
<script>require({baseUrl: '${path_prefix}js/'},['libs/jquery', 'libs/bootstrap']);</script>

<#include "common/footer.ftl">
