package com.logistica.doisv.util.validacao;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CpfCnpjValidator implements ConstraintValidator<CpfCnpj, String> {

    @Override
    public boolean isValid(String valor, ConstraintValidatorContext context) {
        if (valor == null || valor.isBlank()) {
            return true;
        }

        String valorLimpo = valor.replaceAll("[^0-9A-Za-z]", "").toUpperCase();

        return switch (valorLimpo.length()) {
            case 11 -> validarCpf(valorLimpo);
            case 14 -> ehNumerico(valorLimpo) ? validarCnpjNumerico(valorLimpo) : validarCnpjAlfanumerico(valorLimpo);
            default -> false;
        };
    }

    private boolean validarCpf(String cpf) {
        if (cpf.matches("(\\d)\\1{10}")) {
            return false;
        }

        int[] digitos = cpf.chars().map(c -> c - '0').toArray();

        int soma1 = 0;
        for (int i = 0; i < 9; i++) {
            soma1 += digitos[i] * (10 - i);
        }
        int digitoVerificador1 = 11 - (soma1 % 11);
        digitoVerificador1 = (digitoVerificador1 >= 10) ? 0 : digitoVerificador1;

        int soma2 = 0;
        for (int i = 0; i < 10; i++) {
            soma2 += digitos[i] * (11 - i);
        }
        int digitoVerificador2 = 11 - (soma2 % 11);
        digitoVerificador2 = (digitoVerificador2 >= 10) ? 0 : digitoVerificador2;

        return digitos[9] == digitoVerificador1 && digitos[10] == digitoVerificador2;
    }

    private boolean validarCnpjNumerico(String cnpj) {
        if (cnpj.matches("(\\d)\\1{13}")) {
            return false;
        }

        int[] digitos = cnpj.chars().map(c -> c - '0').toArray();
        int[] pesos1 = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
        int[] pesos2 = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};

        int soma1 = 0;
        for (int i = 0; i < 12; i++) {
            soma1 += digitos[i] * pesos1[i];
        }
        int resto1 = soma1 % 11;
        int digitoVerificador1 = (resto1 < 2) ? 0 : (11 - resto1);

        int soma2 = 0;
        for (int i = 0; i < 13; i++) {
            soma2 += digitos[i] * pesos2[i];
        }
        int resto2 = soma2 % 11;
        int digitoVerificador2 = (resto2 < 2) ? 0 : (11 - resto2);

        return digitos[12] == digitoVerificador1 && digitos[13] == digitoVerificador2;
    }

    private boolean validarCnpjAlfanumerico(String cnpj) {
        int[] valores = new int[14];

        for (int i = 0; i < 12; i++) {
            char caractere = cnpj.charAt(i);
            valores[i] = Character.isDigit(caractere) ? (caractere - '0') : (caractere - 'A' + 17);
        }

        int[] pesos1 = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
        int soma1 = 0;
        for (int i = 0; i < 12; i++) {
            soma1 += valores[i] * pesos1[i];
        }
        int resto1 = soma1 % 11;
        int digitoVerificador1 = (resto1 < 2) ? 0 : (11 - resto1);

        valores[12] = digitoVerificador1;

        int[] pesos2 = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
        int soma2 = 0;
        for (int i = 0; i < 13; i++) {
            soma2 += valores[i] * pesos2[i];
        }
        int resto2 = soma2 % 11;
        int digitoVerificador2 = (resto2 < 2) ? 0 : (11 - resto2);

        int dvEsperado1 = cnpj.charAt(12) - '0';
        int dvEsperado2 = cnpj.charAt(13) - '0';

        return digitoVerificador1 == dvEsperado1 && digitoVerificador2 == dvEsperado2;
    }

    private boolean ehNumerico(String texto) {
        return texto.chars().allMatch(Character::isDigit);
    }
}