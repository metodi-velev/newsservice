# REST-based microservice called Newsservice
REST-based microservice - Newsservice using Spring Boot, Spring Data JPA, Spring Security, Java8, Maven, MapStruct, Lombok, Bean Validation API, JUnit 5, Mockito and H2 in-memory DB.
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
GET Request in form: <b>http://localhost:8080/news/account/{accountId}/role/{role}</b> <br>
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
PUT Request in form: <b>http://localhost:8080/news/{newsId}/account/{accountId}</b> <br>
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
<h3>4. POST i.e. upload a picture</h3><br>
<ul>
  <li>Uploads a picture - possible only for users with role PUBLISHER</li>
  <li>Input</li>
  <ul>
        <li>Image as MultipartFile request parameter</li>
  </ul>
  <li>Output</li>
    <ul>
        <li>Ok or error</li>
  </ul>
</ul>
POST Request in form: <b>http://localhost:8080/picture/upload</b> <br>
POST Request Example: <b>http://localhost:8080/picture/upload</b> <br>
Content-Type: <b>multipart/form-data</b> <br>
Request parameter: <b>image</b> <br>
Payload: image = Photo.jpg <br>
The payload could be entered as key-value pair <b>Key=image, Value=Photo.jpg</b> in Postman in the body tab under <b>form-data</b> <br> 
<h3>5. CRUD REST endpoints for News resource</h3><br>
<h3>6. CRUD REST endpoints for Role resource</h3><br>
<h3>Description of the Roles</h3><br>
<ul>
  <li><b>Admin</b>: He has the right to see all contents and edit and delete all contents</li> <br>
  <li><b>Reader</b>: He has the right to read all titles and see all photos in the public area</li> <br>
  <li><b>Publisher</b>: he can create news and edit or delete of the contents he had created</li> <br>
</ul>
Every user has own account, which has an unique ID. Every user has a specific role. <br>
News contains simple text or/and photo. <br>
All of the non-public endpoints are secured using basic authentication i.e. using base64 encoded username:password as Authorization header in the http request.
For example for the user lisa: Authorization=Basic bGlzYTpsaXNh

---

## H2-Console available at `http://localhost:8080/h2-console`, no login required
<ul>
    <li>Enter the following sql query to get all `usernames` with their assigned `roles`: </li>
</ul>

```sql
SELECT u.username, r.role_name FROM "user" u
    inner join user_role ur
        on u.id = ur.user_id
    inner join role r
        on ur.role_id = r.id;
```