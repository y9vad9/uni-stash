<!-- top_navbar.ftl -->
<#if inHelp??>
<#assign nav_path_prefix = "../">
<#else>
<#assign nav_path_prefix = "">
</#if>

<#macro make_nav_link link prefix active title>
<#if active == title>
<li class="active"><a href="">${title}</a></li>
<#else>
<li><a href="${prefix}${link}">${title}</a></li>
</#if>
</#macro>

<div class="navbar navbar-inverse">
  <div class="container">
    <div class="navbar-header">
      <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">
        <span class="icon-bar"></span>
        <span class="icon-bar"></span>
        <span class="icon-bar"></span>
      </button>
      <a class="navbar-brand" href="${nav_path_prefix}welcome.html">
        <img src="${path_prefix}img/logo.png" width="270" height="38" alt="Mathpar logo"></a>
    </div>

    <div class="collapse navbar-collapse">
      <ul class="nav navbar-nav">
          <@make_nav_link prefix=nav_path_prefix link="welcome.html" active=nav_active title=_("navbar.home") />
          <@make_nav_link prefix=nav_path_prefix link="./" active=nav_active title=_("navbar.workbook") />
          <@make_nav_link prefix=nav_path_prefix link="help/" active=nav_active title=_("navbar.help") />
      </ul> <!-- nav -->

      <ul class="nav navbar-nav navbar-right">
          <@make_nav_link prefix=nav_path_prefix link="contact.html" active=nav_active title=_("navbar.about") />
        <li><a href="http://mathpar.com/downloads/MathparHandbook_${_("util.locale")}.pdf">
                 ${_("navbar.handbook")}</a></li>
        <li>
          <a href="${change_lang_link}">
            <img width="20" height="20"
                 src="${path_prefix}img/${_("navbar.changelang_locale")}.png"
                   alt="${_("navbar.changelang")}"> ${_("navbar.changelang")}</a>
        </li>
      </ul> <!-- navbar -->
    </div> <!-- navbar-collapse -->
  </div> <!-- container -->
</div> <!-- navbar -->