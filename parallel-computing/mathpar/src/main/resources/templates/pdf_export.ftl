\documentclass[12pt,a4]{article}
\usepackage[utf8]{inputenc}
\usepackage[russian]{babel}
\usepackage{amsmath,amsfonts,amssymb,amscd,euscript}
\usepackage[pdftex]{graphicx}
\usepackage[left=1cm,right=1cm,top=1cm,bottom=2cm,bindingoffset=0cm,
    paperwidth=${pageWidth}cm,paperheight=${pageHeight}cm]{geometry}



\begin{document}
<#list sectionsLatex as latex>
${latex}

\hrule
\vspace{\baselineskip}
</#list>


\end{document}