
Won't do for now:

- https://stackoverflow.com/questions/39627170/spring-boot-custom-json-serialization

Will do: 

- we will not serialize empty fields: `@JsonInclude(JsonInclude.Include.NON_EMPTY)`

When constructing the response, we're not supposed to set fields we do not want to send. All fields are String, so that
we ensure they have a default empty value which can be ignored. 
