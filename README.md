[jitpack]: https://img.shields.io/jitpack/v/github/swanis/mcmarket-api-java-wrapper?label=Version&style=for-the-badge

<a href="https://mc-market.org/"><img alt="mc-market logo" align="right" src="https://i.imgur.com/kYnUS4c.png?raw=true" height="200" width="200"></a>

[ ![jitpack][] ](https://jitpack.io/#swanis/mcmarket-api-java-wrapper)
# mcmarket-api-java-wrapper


This is a complete and easy-to-use Java wrapper for the [MC-Market Ultimate API](https://www.mc-market.org/wiki/ultimate-api/) built with Java SE Development Kit 8.

# Sending a request
```java
Client client = new Client(new Token("TOKEN STRING", Token.Type.PRIVATE));
Response<Member> response = client.sendOrWait(new RetrieveYourselfRequest());

//client.send(new RetrieveYourselfRequest()) also works, but in that case you'd have to handle eventual ratelimits yourself with the help of our built-in methods (response.isRatelimited() and response.getMillisecondsToWait()).

if (response.getError() == null) {
    Member member = response.getValue();
    System.out.println(member.getUsername());
} else {
    Error error = response.getError();
    System.out.println(error.getCode() + ": " + error.getMessage());
}
```
A list of requests along with their expected response types can be found [here](REQUESTS.md).

# Sorting
Sorting is possible by passing a SortOptions object into the constructor of supported requests (simply pass null if you don't care about sorting).
```java
//Example printing the 20 top-purchased resources.
Client client = new Client(new Token("TOKEN STRING", Token.Type.PRIVATE));
Response<BasicResource[]> response = client.sendOrWait(new ListPublicResourcesRequest(new SortOptions("purchase_count", Order.DESCENDING, 1)));

if (response.getError() == null) {
    BasicResource[] resources = response.getValue();

    for (BasicResource resource : resources) {
        System.out.println(resource.getTitle());
    }
} else {
    Error error = response.getError();
    System.out.println(error.getCode() + ": " + error.getMessage());
}
```
Sortable fields can be found at the official API documentation [here](https://www.mc-market.org/wiki/v1-endpoints/).

# Jitpack Installation
## Gradle
```groovy
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.swanis:mcmarket-api-java-wrapper:VERSION'
}
```

## Maven
```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependency>
    <groupId>com.github.swanis</groupId>
    <artifactId>mcmarket-api-java-wrapper</artifactId>
    <version>VERSION</version>
</dependency>
```
