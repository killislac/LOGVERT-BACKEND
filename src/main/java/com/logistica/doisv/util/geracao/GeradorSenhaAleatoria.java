package com.logistica.doisv.util.geracao;

import java.security.SecureRandom;

public class GeradorSenhaAleatoria {
    private static final String LETRAS_MAIUSCULAS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LETRAS_MINUSCULAS = "abcdefghijklmnopqrstuvwxyz";
    private static final String NUMEROS = "0123456789";
    private static final String SIMBOLOS = "!@#$%^&*()-_=+<>?";

    private static final String TODOS_CARACTERES =
            LETRAS_MAIUSCULAS + LETRAS_MINUSCULAS + NUMEROS + SIMBOLOS;

    private static final SecureRandom RANDOM = new SecureRandom();

    public static String gerarSenha() {
        StringBuilder senha = new StringBuilder(12);

        for (int i = 0; i < 12; i++) {
            int index = RANDOM.nextInt(TODOS_CARACTERES.length());
            senha.append(TODOS_CARACTERES.charAt(index));
        }

        return senha.toString();
    }
}
