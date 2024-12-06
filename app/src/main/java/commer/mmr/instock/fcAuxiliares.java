package commer.mmr.instock;

import java.util.Random;

public class fcAuxiliares {
    public static String gerarSenhaAleatoria() {
        int length = 8; // Tamanho da senha
        String caracteres = "0123456789"; // Apenas números, caso queira incluir letras ou caracteres especiais, adicione aqui
        Random random = new Random();
        StringBuilder senha = new StringBuilder();

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(caracteres.length());
            senha.append(caracteres.charAt(index));
        }

        return senha.toString(); // Retorna a senha gerada
    }
}
