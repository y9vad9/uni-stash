<#assign title = "Math partner">
<#assign nav_active = _("navbar.home")>
<#assign change_lang_link = "../" + _("navbar.changelang_locale") + "/welcome.html">

<#include "common/header.ftl">
<#include "common/top_navbar.ftl">

<div class="container">
  <div class="row">
    <div class="well jumbotron col-md-8 col-md-offset-2">
      <h2>${_("home.intro_title")}</h2>
      <p>${_("home.intro_text")}</p>
      <p>
        <a id="to_workbook" href="./" class="btn btn-lg btn-primary">
          ${_("home.intro_to_workbook")} <b>&rarr;</b></a>
      </p>
      <p class="footer muted">${_("home.intro_email")}</p>
    </div>
  </div>

  <div class="row"> <!-- row #1 -->
    <div class="well col-md-4">
      <h3>${_("home.s01_title")}</h3>
      <ul>
        <li><a href="help/01znak.html#0">${_("home.s01_ss01")}</a></li>
        <li><a href="help/01znak.html#1">${_("home.s01_ss02")}</a></li>
        <li><a href="help/01znak.html#2">${_("home.s01_ss03")}</a></li>
        <li><a href="help/01znak.html#3">${_("home.s01_ss04")}</a></li>
        <li><a href="help/01znak.html#4">${_("home.s01_ss05")}</a></li>
      </ul>
    </div>

    <div class="well col-md-4">
      <h3>${_("home.s02_title")}</h3>
      <ul>
        <li><a href="help/02plot.html#0">${_("home.s02_ss01")}</a></li>
        <li><a href="help/02plot.html#1">${_("home.s02_ss02")}</a></li>
      </ul>
    </div>

    <div class="well col-md-4">
      <h3>${_("home.s03_title")}</h3>
      <ul>
        <li><a href="help/03paradigma.html#0">${_("home.s03_ss01")}</a></li>
        <li><a href="help/03paradigma.html#1">${_("home.s03_ss02")}</a></li>
        <li><a href="help/03paradigma.html#2">${_("home.s03_ss03")}</a></li>
        <li><a href="help/03paradigma.html#3">${_("home.s03_ss04")}</a></li>
        <li><a href="help/03paradigma.html#4">${_("home.s03_ss05")}</a></li>
        <li><a href="help/03paradigma.html#5">${_("home.s03_ss06")}</a></li>
      </ul>
    </div>
  </div> <!-- end of row #1 -->

  <div class="row"> <!-- row #2 -->
    <div class="well col-md-4">
      <h3>${_("home.s04_title")}</h3>
      <ul>
        <li><a href="help/04funk1var.html#0">${_("home.s04_ss01")}</a></li>
        <li><a href="help/04funk1var.html#1">${_("home.s04_ss02")}</a></li>
        <li><a href="help/04funk1var.html#2">${_("home.s04_ss03")}</a></li>
        <li><a href="help/04funk1var.html#3">${_("home.s04_ss04")}</a></li>
        <li><a href="help/04funk1var.html#4">${_("home.s04_ss05")}</a></li>
        <li><a href="help/04funk1var.html#5">${_("home.s04_ss06")}</a></li>
      </ul>
    </div>

    <div class="well col-md-4">
      <h3>${_("home.s05_title")}</h3>
      <ul>
        <li><a href="en/help/05series.html">${_("home.s05_ss01")}</a></li>
      </ul>
    </div>

    <div class="well col-md-4">
      <h3>${_("home.s06_title")}</h3>
      <ul>
        <li><a href="help/06dequation.html#0">${_("home.s06_ss01")}</a></li>
        <li><a href="help/06dequation.html#1">${_("home.s06_ss02")}</a></li>
      </ul>
    </div>
  </div> <!-- end of row #2 -->

  <div class="row">
    <div class="well col-md-4">
      <h3>${_("home.s07_title")}</h3>
      <ul>
        <li><a href="help/07polynomial.html#0">${_("home.s07_ss01")}</a></li>
        <li><a href="help/07polynomial.html#1">${_("home.s07_ss02")}</a></li>
        <li><a href="help/07polynomial.html#2">${_("home.s07_ss03")}</a></li>
      </ul>
    </div>

    <div class="well col-md-4"> <!-- row #3 -->
      <h3>${_("home.s08_title")}</h3>
      <ul>
        <li><a href="help/08matrix.html#0">${_("home.s08_ss01")}</a></li>
        <li><a href="help/08matrix.html#1">${_("home.s08_ss02")}</a></li>
        <li><a href="help/08matrix.html#2">${_("home.s08_ss03")}</a></li>
        <li><a href="help/08matrix.html#3">${_("home.s08_ss04")}</a></li>
        <li><a href="help/08matrix.html#4">${_("home.s08_ss05")}</a></li>
        <li><a href="help/08matrix.html#5">${_("home.s08_ss06")}</a></li>
        <li><a href="help/08matrix.html#6">${_("home.s08_ss07")}</a></li>
        <li><a href="help/08matrix.html#7">${_("home.s08_ss08")}</a></li>
        <li><a href="help/08matrix.html#8">${_("home.s08_ss09")}</a></li>
      </ul>
    </div>

    <div class="well col-md-4">
      <h3>${_("home.s09_title")}</h3>
      <ul>
        <li><a href="help/09mstv.html#0">${_("home.s09_ss01")}</a></li>
        <li><a href="help/09mstv.html#1">${_("home.s09_ss02")}</a></li>
      </ul>
    </div>
  </div> <!-- end of row #3 -->

  <div class="row"> <!-- row #4 -->
    <div class="well col-md-4">
      <h3>${_("home.s10_title")}</h3>
      <ul>
        <li><a href="help/10procedure.html#0">${_("home.s10_ss01")}</a></li>
        <li><a href="help/10procedure.html#1">${_("home.s10_ss02")}</a></li>
      </ul>
    </div>

    <div class="well col-md-4">
      <h3>${_("home.s11_title")}</h3>
      <ul>
        <li><a href="help/11superComp.html#0">${_("home.s11_ss01")}</a></li>
        <li><a href="help/11superComp.html#1">${_("home.s11_ss02")}</a></li>
      </ul>
    </div>
  </div> <!-- end of row #4 -->
</div> <!-- rows container -->

<footer class="container"><p class="muted">Mathpar.com 2015.</p>
</footer>

<script src="${path_prefix}js/libs/require.js"></script>
<script>
  require({
    baseUrl: '${path_prefix}js/',
    paths: {
      'jquery': 'libs/jquery',
      'jquery.cookie': 'libs/jquery.cookie',
      'bootstrap': 'libs/bootstrap',
    },
    shim: {
      'bootstrap': ['jquery'],
    },
  }, ['jquery', 'bootstrap', 'jquery.cookie'], function($) {
    $(function() {
      $('#to_workbook').on('click', function() {
        $.cookie('Mathpar.firstVisit', 'false', {path: '/'});
      });
    });
  });
</script>
<#include "common/footer.ftl">
