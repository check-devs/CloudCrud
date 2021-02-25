# CloudCrud

The **CloudCrud** project is based on **GCP Spanner** database and **REST API**.
You are able to use this project by sending requests and getting responces.

Using this project, you can retrieve different sets of person's data like their details, 
personal information or posts that they've posted.
<br>### At the same time you can get person with details or person with posts.<br>
For example, you can use person and posts for outputting different cards with person's data 
or use person and their details for the profile page or the dashboard.

*Shorcuts*: 


          person with details = pwd

          person with posts = pwp
          
          person's details = pd
          
*Structure of person*:

          {
             "id": 1,
             "firstName": "Denys",
             "lastName": "Matsenko",
             "email": "idanchik47@gmail.com"
          }
          
*Structure of person's details*:

          {
             "detailsId": 1,
             "userId": 1,
             "address": "some address",
             "phoneNumber": "3548590348",
          }         
          
          
**To retrieve person with posts by id** - curl https://cloudcrud.herokuapp.com/pwp/1

**To retrieve person with details by id** - curl https://cloudcrud.herokuapp.com/pwd/1

**To retrieve a person by id** - curl https://cloudcrud.herokuapp.com/persons/1

**To retrieve person's details by id** - curl https://cloudcrud.herokuapp.com/pd/1

**To retrieve person's posts by id** - curl https://cloudcrud.herokuapp.com/posts/1

**Test endpoints using Swagger UI** - https://cloudcrud.herokuapp.com/swagger-ui.html

### You can manipulate perons' data with different request methods (POST, GET, DELETE, PUT)
