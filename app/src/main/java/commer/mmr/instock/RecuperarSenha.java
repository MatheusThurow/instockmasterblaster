package commer.mmr.instock;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class RecuperarSenha extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_recuperar_senha);

        // Configurar insets para adaptar o layout
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Referenciar os componentes do layout
        EditText editTextEmail = findViewById(R.id.editTextTextEmailAddress2); // Substitua pelo ID correto do campo de texto no layout
        Button buttonRecuperarSenha = findViewById(R.id.button_recuperar_senha_tela); // Substitua pelo ID correto do botão no layout

        // Configurar o clique no botão
        buttonRecuperarSenha.setOnClickListener(v -> {
            String emailUsuario = editTextEmail.getText().toString();

            // Verificar se o campo está vazio
            if (emailUsuario.isEmpty()) {
                Toast.makeText(this, "Por favor, insira um e-mail válido!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Instanciar a classe do banco de dados
            BancoDeDadosConfig dbHelper = new BancoDeDadosConfig(this);

            // Verificar se o e-mail existe no banco de dados
            if (dbHelper.isEmailRegistered(emailUsuario)) {
                // E-mail registrado, gerar a senha aleatória e enviar o e-mail de recuperação

                // Gerar senha aleatória de 8 dígitos
                String novaSenha = fcAuxiliares.gerarSenhaAleatoria();

                // Atualizar a senha no banco de dados
                boolean senhaAlterada = dbHelper.attNovaSenhaRec(emailUsuario, novaSenha);

                if (senhaAlterada) {
                    // Criar a mensagem do e-mail com a senha gerada
                    String assunto = "Redefinição de Senha - InStock App";
                    String mensagem = "Olá,\n\nSua nova senha temporária é: " + novaSenha
                            + "\n\nPor favor, altere sua senha assim que possível.";

                    // Enviar o e-mail em segundo plano utilizando o AsyncTask
                    new AsyncTaskSegundoPlano(this).execute(emailUsuario, assunto, mensagem);

                    Toast.makeText(this, "E-mail de recuperação enviado com a nova senha!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Erro ao atualizar a senha no banco de dados.", Toast.LENGTH_SHORT).show();
                }
            } else {
                // E-mail não registrado no banco de dados
                Toast.makeText(this, "E-mail não registrado. Verifique e tente novamente.", Toast.LENGTH_SHORT).show();
            }
        });
        // Configurar a setinha de voltar para a TelaLogin
        findViewById(R.id.seta_v_t_rec_senha).setOnClickListener(v -> {
            // Voltar para a TelaLogin
            Intent intent = new Intent(RecuperarSenha.this, TelaLogin.class);
            startActivity(intent);
            finish();  // Finaliza a tela atual
        });
    }
}
