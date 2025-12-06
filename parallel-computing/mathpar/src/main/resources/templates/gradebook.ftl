<#-- @ftlvariable name="student_name" type="java.lang.String" -->
<#-- @ftlvariable name="gradebookRows" type="java.util.List<com.mathpar.web.db.entity.GradebookRow>" -->
<#assign title = "Grade book of student ${student_name}">
<#assign path_prefix = "../../">

<#include "common/header.ftl">

<div class="container">
    <div class="row">
        <div class="well jumbotron col-md-8 col-md-offset-2">
        <#if gradebookRows?has_content>
            <p>Grade book of student ${student_name}</p>
            <table class="table">
                <thead>
                <tr>
                    <th>Time</th>
                    <th>Test</th>
                    <th>Task #</th>
                    <th>Result</th>
                </tr>
                </thead>
                <tbody>
                    <#list gradebookRows as row>
                    <tr>
                        <td>${row.checkTime?string('dd.MM.yyyy HH:mm:ss')}</td>
                        <td>${row.taskTitle}</td>
                        <td>${row.subtaskNumber}</td>
                        <td>${row.checkResult.description}</td>
                    </tr>
                    </#list>
                </tbody>
            </table>
        <#else>
            <p>There is no grade book records for student ${student_name}.</p>
        </#if>
        </div>
    </div>
</div> <!-- rows container -->
<#include "common/footer.ftl">
