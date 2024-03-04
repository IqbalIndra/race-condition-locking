# Dans Test with Spring boot 

## How to Run
> mvn spring-boot:run

## Access API (Via curl)
- Get Token via login with username and password 
> curl -v -H "Content-Type: application/json" -d "{\"username\":\"dans\", \"password\":\"dns123!@#\"}" localhost:5500/auth/login
- Get Jobs
> curl -v -H "Authorization: Bearer {token}" localhost:5500/dans/jobs
- Get Jobs by Id
> curl -v -H "Authorization: Bearer {token}" localhost:5500/dans/jobs/{id}
