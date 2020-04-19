# Currency Exchange Rates

<h3>REST API Level 3 demo project with CRUD operations</h3>

<ul>
  <li>Java</li>
  <li>Spring</li>
  <li>PostgreSQL</li>
  <li>Vanilla JS</li>
  <li>Bootstrap</li>
</ul>

Front-end (located in <b>src</b>) is generic and adapts to changes made to the Currency model on back-end:
<ul>
  <li>Adding, renaming or deleting fields</li>
  <li>Enabling or disabling following HTTP methods - POST, PUT, DELETE</li>
</ul>

Define the following environmental variables in both Spring Boot and JUnit run configurations for database connectivity:
<ul>
  <li>DATASOURCE_URL</li>
  <li>DATASOURCE_USERNAME</li>
  <li>DATASOURCE_PASSWORD</li>
</ul>

Add the following to VM options to disable the illegal reflective access warning:
<ul>
  <li>--add-opens java.base/java.lang=ALL-UNNAMED</li>
</ul>
