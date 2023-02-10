package antifraud.resources;

import antifraud.dto.Result;
import antifraud.dto.Transaction;
import antifraud.service.AntiFraudService;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/antifraud")
@RequiredArgsConstructor
public class AntiFraudResource {

    private final AntiFraudService service;

    @PostMapping("/transaction")
    public ResponseEntity<Result> postTransaction(@RequestBody @Valid Transaction transaction) {
        final var result = service.verifyTransaction(transaction);
        return new ResponseEntity<>(new Result(result.getName()), HttpStatus.OK);
    }

    @ControllerAdvice
    private static class AntiFraudResourceExceptionHandler extends ResponseEntityExceptionHandler {

        // own exception handler, will need this later, so leaving commented code here
//        @ExceptionHandler(InvalidTransactionAmountException.class)
//        public ResponseEntity<Result> handleRuntimeException(InvalidTransactionAmountException e, WebRequest request) {
//            return new ResponseEntity<>(new Result("Error: " + e + " while processing request: " + request), HttpStatus.BAD_REQUEST);
//        }




    }

}
