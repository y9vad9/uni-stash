<#-- @ftlvariable name="gradebooksListRows" type="java.util.List<com.mathpar.web.db.entity.GradebookListRow>" -->
<#assign title = "Grade books">
<#assign path_prefix = "../../">

<#include "common/header.ftl">

<div class="container">
    <div class="row">
        <div class="well jumbotron col-md-8 col-md-offset-2">
        <#if gradebooksListRows?has_content>
            <table class="table">
                <thead>
                <tr>
                    <th>ID</th>
                    <th>Email</th>
                    <th>Name</th>
                    <th>Group</th>
                    <th>Grade book</th>
                </tr>
                </thead>
                <tbody>
                    <#list gradebooksListRows as row>
                    <tr>
                        <td>${row.studentId}</td>
                        <td>${row.studentEmail}</td>
                        <td>${row.studentName}</td>
                        <td>${row.groupName}</td>
                        <td><a href="./${row.studentId}">Grade book</a></td>
                    </tr>
                    </#list>
                </tbody>
            </table>
        <#else>
            <p>There is no grade books.</p>
        </#if>
        </div>
    </div>
</div> <!-- rows container -->
<#include "common/footer.ftl">
