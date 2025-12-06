<form class="form-inline graph-additional" role="form">
    <div class="form-group">
        <div class="btn-group buttons">
            <button class="btn btn-default btn-replot"
                    title="<%= msg.plots['PlotTooltip']%>"><%=msg.plots['Plot'] %>
            </button>
            <button class="btn btn-default btn-download"
                    title="<%= msg.plots['DownloadTooltip']%>"><%=msg.plots['Download'] %>
            </button>

        </div>
    </div>
    <div class="form-group">
        <div class="checkbox">
            <label title="<%= msg.plots['isBlackWhiteTooltip'] %>">
                <input type="checkbox" class="is-bw"> <%= msg.plots['isBlackWhite'] %>
            </label>
        </div>
    </div>
    <div class="form-group">
        <div class="checkbox">
            <label title="<%= msg.plots['isEqualScaleTooltip'] %>">
                <input type="checkbox" class="is-eqscale"> <%= msg.plots['isEqualScale'] %>
            </label>
        </div>
    </div>
    <div class="form-group">
        <div class="input-group font-size">
            <span class="input-group-addon"
                  title="<%= msg.plots['font-size'] %>">↕A</span>
            <input type="text" class="form-control" value="16">
        </div>
    </div>
    <div class="form-group">
        <div class="input-group line-thickness">
            <span class="input-group-addon"
                  title="<%= msg.plots['line thickness'] %>">—</span>
            <input type="text" class="form-control" value="3">
        </div>
    </div>
    <div class="form-group">
        <div class="input-group axes-thickness">
            <span class="input-group-addon"
                  title="<%= msg.plots['axes thickness'] %>">↑</span>
            <input type="text" class="form-control" value="3">
        </div>
    </div>

    <% if (withParameters) { %>
    <div class="form-group">
        <div class="input-group frames-number">
            <span class="input-group-addon" title="<%= msg.plots['number of frames'] %>"><%= msg.plots['#frames'] %></span>
            <input type="text" class="form-control" value="1">
        </div>
    </div>
    <div class="form-group parameters">
        <div class="btn-group" data-toggle="buttons">
            <label class="btn btn-default active">
                <input type="radio" name="parameters"> <%= ringParameters[0] %> = <span
                    class="val">1.00</span>
            </label>
            <% for (var i = 1; i < ringParameters.length; i++) { %>
            <label class="btn btn-default">
                <input type="radio" name="parameters"> <%= ringParameters[i] %> = <span
                    class="val">1.00</span>
            </label>
            <% } %>
        </div>
    </div>
    <div class="form-group">
        <input type="text" class="parameter-slider">
    </div>
    <% } %>


    <% if (isTablePlot) { %>
    <div class="form-group">
        <div class="btn-group" data-toggle="buttons">
            <label class="btn btn-default">
                <input type="radio" name="linetype"> <%= msg.plots['Cut line'] %>
            </label>
            <label class="btn btn-default active">
                <input type="radio" name="linetype"> <%= msg.plots['Set new line'] %>
            </label>
            <label class="btn btn-default">
                <input type="radio" name="linetype"> <%= msg.plots['Del old line'] %>
            </label>
            <label class="btn btn-default">
                <input type="radio" name="linetype"> <%= msg.plots['Del new line'] %>
            </label>
        </div>
    </div>
    <% } %>
</form>
<img class="plot" src="<%=imgPath%>">
