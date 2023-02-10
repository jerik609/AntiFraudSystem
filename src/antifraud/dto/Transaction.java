package antifraud.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Positive;

// https://github.com/projectlombok/lombok/issues/1563#issuecomment-363460962
/*
    Details from the issue report:
    Just that I understand everything, as we are hitting the same bug. The change basically means that lombok is not adding
    anymore the @ConstructorProperties annotation to the generated constructors. And this one was used by the jackson when
    there was only an all-args constructor available. And since @Data and @Builder both add only the all-args constructor,
    the jackson has no way to instantiate the object.
    Solution one would be setting the property as described above (seems easy, at least for Java 1.8).
    Second would be to explicitly add @NoArgsConstructor (and unfortunately also @AllArgsConstructor if you are using @Builder)
    to all classes annotated with @Data or/and @Builder (I confirmed this works as jackson goes for no-arg constructor most likely).
    Is there any kind of property we can use to generate both types of constructors with @Data or/and @Budiler? Then I would not
    care about @ConstructorProperties at all. Or I am wrong here somehow?
 */
// making the field private would interfere with validation, producing the error:
//    o.s.w.s.m.m.a.HttpEntityMethodProcessor  : No match for [*/*], supported: []
//    .m.m.a.ExceptionHandlerExceptionResolver : Resolved [org.springframework.http.converter.HttpMessageNotReadableException:
//        JSON parse error: Cannot construct instance of `antifraud.dto.Transaction` (although at least one Creator exists): cannot deserialize from Object value (no delegate- or property-based Creator);
//        nested exception is com.fasterxml.jackson.databind.exc.MismatchedInputException: Cannot construct instance of `antifraud.dto.Transaction` (although at least one Creator exists): cannot deserialize from Object value (no delegate- or property-based Creator) at [Source: (PushbackInputStream); line: 2, column: 5]]
// to resolve it, I had to add the no argument constructor (and force final field initialization) - the reason is the finding above
// of course this is far from ideal - we should not provide a no args constructor for fields, which cannot be modified by user - ending up with a "default-initialized object"

@Data
@NoArgsConstructor(force = true)
public class Transaction {
    @Positive
    private final long amount;
}
