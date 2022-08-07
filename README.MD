# Run With Gradle
```
./gradlew bootRun
```

# Docker
### Build Jar
```
./gradlew build
```

### Build image
```
docker build -t ecom .
```
### Run container
```
docker run -p 8080:8080 -d ecom
```
