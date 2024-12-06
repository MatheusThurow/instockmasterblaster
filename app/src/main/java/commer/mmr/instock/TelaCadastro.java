package commer.mmr.instock;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView;  // Importar ImageView para referenciar a seta de voltar

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class TelaCadastro extends AppCompatActivity {

    private EditText entradaNome, entradaEmail, entradaSenha, entradaConfirmacaoSenha;
    private BancoDeDadosConfig bancoDeDadosConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tela_cadastro);

        // Ajustar padding para barras do sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicializar banco de dados
        bancoDeDadosConfig = new BancoDeDadosConfig(this);

        // Inicializar os campos de entrada
        entradaNome = findViewById(R.id.EntradaNome_Cadastro);
        entradaEmail = findViewById(R.id.EntradaEmail_Cadastro);
        entradaSenha = findViewById(R.id.EntradaSenha_CadastroConfirmacao_);
        entradaConfirmacaoSenha = findViewById(R.id.EntradaSenha_CadastroConfirmacao);

        // Encontrar o TextView
        TextView textView = findViewById(R.id.possuiCadastro);

        // Definir um OnClickListener para o TextView
        textView.setOnClickListener(v -> {
            // Navegar para a tela de login
            Intent intent = new Intent(TelaCadastro.this, TelaLogin.class);
            startActivity(intent);
        });

        // Configurar o botão "Cadastrar"
        Button btnCadastrar = findViewById(R.id.button_Entrar);
        btnCadastrar.setOnClickListener(v -> {
            String nome = entradaNome.getText().toString();
            String email = entradaEmail.getText().toString();
            String senha = entradaSenha.getText().toString();
            String confirmacaoSenha = entradaConfirmacaoSenha.getText().toString();

            // Verificação de campos vazios
            if (nome.isEmpty() || email.isEmpty() || senha.isEmpty() || confirmacaoSenha.isEmpty()) {
                Toast.makeText(TelaCadastro.this, "Por favor, preencha todos os campos.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Verificação de e-mail válido (contém '@')
            if (!email.contains("@")) {
                Toast.makeText(TelaCadastro.this, "Por favor, insira um e-mail válido.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Verificação de senha (mínimo de 3 caracteres)
            if (senha.length() < 3) {
                Toast.makeText(TelaCadastro.this, "A senha deve ter pelo menos 3 caracteres.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Verificação de correspondência de senhas
            if (!senha.equals(confirmacaoSenha)) {
                Toast.makeText(TelaCadastro.this, "As senhas não coincidem.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Verificação de e-mail já cadastrado
            if (bancoDeDadosConfig.isEmailRegistered(email)) {
                Toast.makeText(TelaCadastro.this, "O e-mail já está cadastrado.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Inserção do usuário no banco de dados
            boolean insertSuccess = bancoDeDadosConfig.insertUser(nome, email, senha);
            if (insertSuccess) {
                // Recupera o ID do usuário recém-cadastrado
                int userId = bancoDeDadosConfig.getUserIdByEmail(email);
                if (userId != -1) {
                    // Salva o ID do usuário nos SharedPreferences
                    SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt("user_id", userId);
                    editor.apply();

                    Toast.makeText(TelaCadastro.this, "Cadastro realizado com sucesso!", Toast.LENGTH_SHORT).show();

                    // Navegar diretamente para a tela de estoque:
                    Intent intent = new Intent(TelaCadastro.this, TelaEstoqueLista.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(TelaCadastro.this, "Erro ao recuperar informações do usuário.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(TelaCadastro.this, "Erro ao cadastrar. Tente novamente.", Toast.LENGTH_SHORT).show();
            }
        });

        // Encontrar o botão de voltar (seta) e configurar o clique
        ImageView setaVoltar = findViewById(R.id.seta_v_t_cadastro);

        // Adicionar a ação de voltar
        setaVoltar.setOnClickListener(v -> {
            // Navegar de volta para a TelaLogin
            Intent intent = new Intent(TelaCadastro.this, TelaLogin.class);
            startActivity(intent);  // Inicia a TelaLogin
            finish();  // Fecha a TelaCadastro para não deixar ela na pilha de atividades
        });
    }
}
