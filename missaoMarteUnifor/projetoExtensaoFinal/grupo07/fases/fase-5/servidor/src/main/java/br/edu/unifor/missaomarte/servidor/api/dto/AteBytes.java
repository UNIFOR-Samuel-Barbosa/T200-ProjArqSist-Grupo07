package br.edu.unifor.missaomarte.servidor.api.dto;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.nio.charset.StandardCharsets;

/** Limite em bytes UTF-8 (não em caracteres): "é" ocupa 2 bytes, um emoji ocupa 4. */
@Target({ElementType.FIELD, ElementType.RECORD_COMPONENT, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = AteBytes.Validador.class)
public @interface AteBytes {
    int value();

    String message() default "A senha pode ter no máximo 72 bytes.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    class Validador implements ConstraintValidator<AteBytes, String> {
        private int limite;

        @Override
        public void initialize(AteBytes anotacao) {
            limite = anotacao.value();
        }

        @Override
        public boolean isValid(String texto, ConstraintValidatorContext contexto) {
            return texto == null || texto.getBytes(StandardCharsets.UTF_8).length <= limite;
        }
    }
}
