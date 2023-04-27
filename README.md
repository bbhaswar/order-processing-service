# file-processing-service
This service process input file data and store into database. This service also provide different API to read data from data base based on user input.

The service can consume the file from both API as well as a scheduled bacth job. The file consumed via API will be copied to a temp folder and upon processing it will be copied to a processed folder. There is a max file size till it will process after that it will thorw exception. File path and other config information will stoder into bean at the start of application. 

Swagger is also implemented for api documentation.
swagged api is available at http://localhost:8090/swagger-ui/index.html
