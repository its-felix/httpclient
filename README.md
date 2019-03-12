# Basic Concepts
The classes:
- SimpleHTTPClient (as long as the passed headers-Map is safe for reading from different threads)
- Endpoint
- Request
    - FormDataRequestBody (as long as the passed parameter-Collection is safe for reading from different threads)
    - BinaryRequestBody and BinaryFormDataParameter (as long as the given InputStreamSupplier is threadsafe)
- Response

can be safely used in a multithreaded environment. 

# Usage
## Step 1: Creating the HTTPClient
```java
// The SimpleHTTPClient is the only given implementation of HTTPClient
// Except for the headers you can pass it in the constructor it is completely stateless
// so it's safe to use the same instance from all your threads 
HTTPClient client = new SimpleHTTPClient();
```

## Step 2: Creating an Endpoint
```java
// This endpoint represents "https://some-host.com/my/endpoint
Endpoint endpoint = Endpoint.forHost("some-host.com").resolve("my", "endpoint");
```

## Step 3: Creating a Request
### Step 3.1: Basic GET-Request
```java
Endpoint endpoint = ...;

// This creates a simple GET-Request against the endpoint
Request request = endpoint.getRequest().build();
```

### Step 3.2: GET-Request with parameters
```java
Endpoint endpoint = ...;

// The resulting request will use the query string "?my-parameter=some-value&parameter-without-value"
Request request = endpoint.getRequest()
    .parameter("my-parameter", "some-value")
    .parameter("parameter-without-value")
    .build();
```

### Step 3.3: POST-Request uploading a file using FormData
```java
Endpoint endpoint = ...;
File theFileYouWantToUpload = ...;

// This request will upload the given file using the FormData name "file"
// The file will be read when the Request is executed
Request request = endpoint.postRequest()
    .body(new FormDataRequestBody(Collections.singleton(
            FormDataParameter.forFile("file", theFileYouWantToUpload)
    )))
    .build();
```

### Step 3.x: Building a RequestTemplate
You can also build a Template for a Request that you can enrich with some more parameters, headers or another body afterwards.

Example:
```java
Endpoint endpoint = ...;
Request.Template template = endpoint.getRequest()
    .parameter("some-fix-parameter", "value")
    .template();

Request request = template.enrich()
    .parameter("some-parameter", "value")
    .build();
```

## Step 4: Creating a ResponseParser
This library ships with 3 simple implementations of the ResponseParser interface.
### NoOpResponseParser
The ```NoOpResponseParser``` ignores the returned InputStream completely and always returns null
```java
ResponseParser<Void> parser = new NoOpResponseParser();
```

### StringResponseParser
The ```StringResponseParser``` creates a String from the InputStream using the charset returned by the server
```java
ResponseParser<String> parser = new StringResponseParser();
```

### SaveFileResponseParser
The ```SaveFileResponseParser``` saves the returned InputStream to a given File and returns the File-Object on success
```java
ResponseParser<File> parser = new SaveFileResponseParser(new File("somefile.txt"));
```

## Step 5: Executing the request
```java
HTTPClient client = ...;
Request request = ...;
ResponseParser<String> parser = new StringResponseParser();

// Returns a Response-Object or throws an IOException if one occurs 
Response<String> response = request.execute(client, parser);

System.out.println(response.getResponseCode()); // 200
System.out.println(response.getHeaders()); // Map<String, List<String>> containing all Response Headers
System.out.println(response.getContentType()); // "application/json" without charset
System.out.println(response.getContentTypeCharset()); // "UTF-8" (may be null)
System.out.println(response.getValue()); // The value returned by the ResponseParser
```

## Step 6: Repeating a request using a previous Response
You can repeat any request using the Response Object
```java
Response<?> response = ...;
Response<?> newResponse = response.repeat();
```

## Step ?: All in one
```java
// Reading the HTML-Content from "https://www.youtube.com/watch?v=y6120QOlsfU"
Response<String> response = Endpoint.forHost("youtube.com")
    .resolve("watch")
    .getRequest()
    .parameter("v", "y6120QOlsfU")
    .build()
    .execute(new SimpleHTTPClient(), new StringResponseParser());

System.out.println(response.getResponseCode());
System.out.println(response.getValue()); // the returned html
```