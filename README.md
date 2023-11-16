# tools-management-api
API for tools management system.

### Run application
To run application, you must follow this steps:
- start docker
- go to project deployment folder
- run buildAndRun.sh script

API available on http://localhost:8080/tools-management-api/

Note: you need GIT_ACCESS_TOKEN variable in your environment

How to add environment variable on Mac:
1. open terminal window
2. type sudo su and enter root password
3. type open .zshenv
4. in text editor add to end of file: export GIT_ACCESS_TOKEN=your_token
5. save file and restart your laptop

### Swagger
Swagger available on http://localhost:8080/tools-management-api/swagger-ui/index.html

### API description
API description available on https://miro.com/app/board/uXjVMNc0wnk=/

### Database
Application use Postgres. Database structure available on https://miro.com/app/board/uXjVMNbo-wk=/

### PgAdmin
PaAdmin available on http://localhost:5050

login: admin@admin.com

password: SuperSecret

password for database connection: SuperSecret

## Prometheus
Prometheus available on http://localhost:9090

## Grafana
Grafana available on http://localhost:3000

login: admin

password: SuperSecret

Available dashboards:
- spring-boot 3
- PostgreSQL

## Minio
Minio available on http://localhost:9000

login: tools_management_user

password: SuperSecret

bucket name: personphoto (/tmp folder on laptop)

## File storage service
Swagger available on http://localhost:8070/file-storage-api/swagger-ui/index.html

## Metrics
http://localhost:8080/tools-management-api/actuator

## Healthcheks
http://localhost:8080/tools-management-api/actuator/health

## Other resources
https://www.figma.com

https://miro.com