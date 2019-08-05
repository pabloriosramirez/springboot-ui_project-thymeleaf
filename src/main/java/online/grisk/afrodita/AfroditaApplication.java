package online.grisk.afrodita;

import online.grisk.afrodita.domain.entity.Microservice;
import online.grisk.afrodita.domain.entity.Role;
import online.grisk.afrodita.domain.entity.TypeVariable;
import online.grisk.afrodita.domain.entity.Variable;
import online.grisk.afrodita.domain.service.RoleService;
import online.grisk.afrodita.integration.activator.impl.DataintegrationActivatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.HttpMethod;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.client.RestTemplate;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@EnableEurekaClient
@SpringBootApplication
@ImportResource("classpath:integration.cfg.xml")
public class AfroditaApplication {

    @Autowired
    RoleService roleService;

    public static void main(String[] args) {
        SpringApplication.run(AfroditaApplication.class, args);
    }

    @LoadBalanced
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder) {
        return restTemplateBuilder
                .setConnectTimeout(Duration.ofHours(1))
                .setReadTimeout(Duration.ofHours(1))
                .build();
    }

    @Bean
    UUID getUUID() {
        return UUID.randomUUID();
    }

    @Bean
    public String token() {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(getUUID().toString().getBytes());
            BigInteger no = new BigInteger(1, messageDigest);
            String hashtext = no.toString(16);
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        } catch (NoSuchAlgorithmException e) {
            return getUUID().toString();
        }
    }

    @Bean
    public MessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasenames("message");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }

    @Bean
    Microservice serviceActivatorArtemisa() {
        return new Microservice("artemisa", HttpMethod.POST, "/api/artemisa", "artemisa", "GRisk.2019", new HashMap<>());
    }

    @Bean
    public Role roleWithCodeAdmin() {
        return roleService.findByCode("ADMIN");
    }

    @Bean
    public BCryptPasswordEncoder encoderPassword() {
        return new BCryptPasswordEncoder();
    }

    @Autowired
    DataintegrationActivatorService dataintegrationActivatorService;

    @Bean
    public List<Variable> getVariablesBureau() throws Exception {
        List<Variable> variables = dataintegrationActivatorService.getVariableBureau();
        return variables;
    }

    @Bean
    public List<TypeVariable> getTypesVariables() {
        List<TypeVariable> types = new ArrayList<>();
        types.add(new TypeVariable(1L, "Número entero", "NE"));
        types.add(new TypeVariable(2L, "Número decimal", "ND"));
        types.add(new TypeVariable(3L, "Palabra", "PA"));
        return types;
    }
}
