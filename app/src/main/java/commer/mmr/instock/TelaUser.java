package commer.mmr.instock;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class TelaUser extends AppCompatActivity {

    private BancoDeDadosConfig bancoDeDadosConfig;
    private EditText editTextEmail, editTextSenha;
    private TextView textNomeUsuario;
    private ImageView btnVoltar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_user);

        // Inicializar banco de dados
        bancoDeDadosConfig = new BancoDeDadosConfig(this);

        // Referenciar os campos do layout
        editTextEmail = findViewById(R.id.editTextTextEmailAddress);
        editTextSenha = findViewById(R.id.editTextTextPassword);
        textNomeUsuario = findViewById(R.id.text_nomedusuario);
        btnVoltar = findViewById(R.id.setinhaVoltar);

        // Configurar o botão de voltar
        btnVoltar.setOnClickListener(v -> onBackPressed());

        // Recuperar o ID do usuário dos SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        int userId = sharedPreferences.getInt("user_id", -1);

        if (userId != -1) {
            // Obter os detalhes do usuário
            String[] userDetails = bancoDeDadosConfig.getUserDetailsById(userId);

            if (userDetails[0] != null && userDetails[1] != null && userDetails[2] != null) {
                // Preencher os campos com as informações do usuário
                textNomeUsuario.setText(userDetails[0]); // Nome do usuário
                editTextEmail.setText(userDetails[1]);   // E-mail
                editTextSenha.setText(userDetails[2]);   // Senha
            } else {
                // Informar erro caso os dados não sejam encontrados
                textNomeUsuario.setText("Erro ao carregar informações do usuário.");
            }
        } else {
            // Se o ID do usuário não estiver disponível
            textNomeUsuario.setText("Usuário não identificado. Faça login novamente.");
        }
    }
}
