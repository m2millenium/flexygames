<!DOCTYPE html>
<html>
<head>
    <meta name='layout' content='main'/>
    <title>Grails Runtime Exception</title>
</head>
<body>
<h1>An error occurred</h1>
<p>${exception?.message}</p>
<% if (exception && exception.stackTrace) { %>
<pre><% exception.stackTrace.each { out.println it } %></pre>
<% } %>
</body>
</html>
