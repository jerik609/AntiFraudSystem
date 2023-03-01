
Won't do for now:

- https://stackoverflow.com/questions/39627170/spring-boot-custom-json-serialization

Will do: 

- we will not serialize empty fields: `@JsonInclude(JsonInclude.Include.NON_EMPTY)`

When constructing the response, we're not supposed to set fields we do not want to send. All fields are String, so that
we ensure they have a default empty value which can be ignored. 

validation seems a bit tricky - ordering determines if it's functional:

https://stackoverflow.com/questions/46923315/spring-boot-pathvariable-can-not-validate

```
@Validated
@RestController
@RequestMapping(path = GreetingResource.BASE_PATH)
public class GreetingResource {

     static final String BASE_PATH = "/greet";
     static final String HELLO_PATH = "/hello";

    @GetMapping
    @RequestMapping(path = HELLO_PATH + "/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, String> greet(@Size(min = 1, max = 5) @PathVariable String name) {
        return Collections.singletonMap("response", String.format("Hi %s !", name));
    }
```