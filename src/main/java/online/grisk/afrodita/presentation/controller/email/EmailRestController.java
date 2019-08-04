package online.grisk.afrodita.presentation.controller.email;

import online.grisk.afrodita.domain.dto.ResetPassDTO;
import online.grisk.afrodita.domain.dto.UserDTO;
import online.grisk.afrodita.integration.activator.impl.EmailActivatorService;
import online.grisk.afrodita.presentation.controller.BasicRestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.Map;

@RestController
public class EmailRestController extends BasicRestController {

    @Autowired
    EmailActivatorService emailActivatorService;

    @PostMapping(value = "/v1/rest/user/register-by-login")
    public HttpEntity<?> createdUserAdminByLogin(@Valid @RequestBody UserDTO userDTO, Errors errors) throws Exception {
        if (errors.hasErrors()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        this.verifyParameters(userDTO.toMap());
        Map response = emailActivatorService.invokeEmailRegisterByLogin(userDTO.toMap(), createHeadersWithAction("sendEmailRegisterUser"));
        return new ResponseEntity<>(response, HttpStatus.valueOf(Integer.parseInt(response.getOrDefault("status", "500").toString())));
    }

    @PostMapping(value = "/v1/rest/user/reset-by-login")
    public HttpEntity<?> resetPassByLogin(@NotEmpty @Payload @RequestBody Map payload, @NotEmpty @Headers @RequestHeader Map headers) throws Exception {
        this.verifyParameters(payload);
        Map response = emailActivatorService.invokeEmailResetPassByLogin(payload, createHeadersWithAction("sendEmailResetPassword"));
        return new ResponseEntity<>(response, HttpStatus.valueOf(Integer.parseInt(response.getOrDefault("status", "500").toString())));

    }

    @PostMapping(value = "/v1/rest/user/post-reset-by-login")
    public HttpEntity<?> postResetPassByLogin(@Valid @RequestBody ResetPassDTO resetPassDTO, Errors errors) throws Exception {
        if (errors.hasErrors()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        this.verifyParameters(resetPassDTO.toMap());
        Map response = emailActivatorService.invokePostResetPassByLogin(resetPassDTO.toMap(), createHeadersWithAction("postResetPassword"));
        return new ResponseEntity<>(response, HttpStatus.valueOf(Integer.parseInt(response.getOrDefault("status", "500").toString())));
    }


}