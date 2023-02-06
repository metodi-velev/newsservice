### REST-based microservice - Newsservice using Spring Boot, Spring Data JPA, Maven, Java8 and H2 DB.
<hr>
<h2>Requests</h2> <br>
<h3>1. GET actual news for a single role/accountID</h3><br>
<ul>
  <li>Returns all news for single role/account ID back, which are new and have not been read yet. New news are defined as 1 day old news relative to the current time.</li>
  <li>Input</li>
  <ul>
        <li>accountId</li>
        <li>rolles</li>
  </ul>
  <li>Output</li>
    <ul>
        <li>news (0 bis x)</li>
  </ul>
</ul>
GET Request Example: <b>http://localhost:8080/news/account/5/role/PUBLISHER</b>
<h3>2. Set read status</h3><br>
<ul>
  <li>Set the read status for a single news</li>
  <li>Input</li>
  <ul>
        <li>accountId</li>
        <li>newsId</li>
  </ul>
  <li>Output</li>
    <ul>
        <li>Ok or error</li>
  </ul>
</ul>
PUT Request in form: <b>http://localhost:8080/news/{newsId}/account/5</b> <br>
PUT Request Example: <b>http://localhost:8080/news/61008630-4b61-4b28-8594-a7f1febc2a33/account/5</b> <br>
PUT Request payload: <br>
<code>
{
        "readDate": "1980-04-09T10:15:30.00Z"
}
</code>
<h3>3. GET picture</h3><br>
<ul>
  <li>Returns a picture for a single news</li>
  <li>Input</li>
  <ul>
        <li>News id</li>
        <li>Picture id</li>
        <li>rolles</li>
  </ul>
  <li>Output</li>
    <ul>
        <li>Picture as file</li>
        <li>Meta data</li>
  </ul>
</ul>
GET Request in form: <b>http://localhost:8080/news/{newsId}/picture/{pictureId}/role/{role}</b> <br>
GET Request Example: <br> <b>http://localhost:8080/news/2983ec85-b044-456f-a1c5-d151b2a1879c/picture/dc5e622c-ec31-47d9-87c7-466454fdecdf/role/reader</b> <br>
<h3>4. CRUD REST enpoints for News resource</h3><br>
<h3>5. CRUD REST enpoints for Role resource</h3><br>
<h3>Description of the Roles</h3><br>
<ul>
  <li><b>Admin</b>: He has the right to see all contents and edit and delete all contents</li> <br>
  <li><b>Reader</b>: He has the right to read all titles and see all photos in the public area</li> <br>
  <li><b>Publisher</b>: he can create news and edit or delete of the contents he had created</li> <br>
</ul>
Every user has own account, which has an unique ID. Every user has a specific role. <br>
News contains simple text or/and photo.


