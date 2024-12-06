package commer.mmr.instock;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class TelaLogin extends AppCompatActivity {

    private EditText entradaEmail, entradaSenha;
    private CheckBox checkBoxLembrarSenha;
    private BancoDeDadosConfig bancoDeDadosConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tela_login);

        // Ajustar padding para barras do sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicializar banco de dados
        bancoDeDadosConfig = new BancoDeDadosConfig(this);

        // Inicializar campos de entrada
        entradaEmail = findViewById(R.id.editTextTextEmail_Input);
        entradaSenha = findViewById(R.id.editText_Senha);
        checkBoxLembrarSenha = findViewById(R.id.checkBox_LembrarSenha);

        // Recuperar as preferências salvas (se houver)
        SharedPreferences preferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String savedEmail = preferences.getString("email", "");
        String savedSenha = preferences.getString("senha", "");
        boolean lembrarSenha = preferences.getBoolean("lembrar_senha", false);

        // Se a opção "lembrar senha" foi marcada anteriormente, preencher os campos
        if (lembrarSenha) {
            entradaEmail.setText(savedEmail);
            entradaSenha.setText(savedSenha);
            checkBoxLembrarSenha.setChecked(true); // Marcar o checkbox "lembrar senha"
        }

        // Configurar botão para navegação para TelaCadastro
        Button buttonCadastro = findViewById(R.id.button_Cadastrar);
        buttonCadastro.setOnClickListener(v -> {
            Intent intent = new Intent(TelaLogin.this, TelaCadastro.class);
            startActivity(intent);
        });

        // Configurar botão "Entrar"
        Button btnEntrar = findViewById(R.id.button_Entrar);
        btnEntrar.setOnClickListener(v -> {
            String email = entradaEmail.getText().toString();
            String senha = entradaSenha.getText().toString();

            // Verificar se os campos estão vazios
            if (email.isEmpty() || senha.isEmpty()) {
                Toast.makeText(TelaLogin.this, "Por favor, preencha todos os campos.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Verificar se o usuário é válido
            if (bancoDeDadosConfig.validateUser(email, senha)) {
                int userId = bancoDeDadosConfig.getUserIdByEmail(email);

                if (userId != -1) { // Verifica se o ID foi recuperado com sucesso
                    // Usar a variável preferences que já foi declarada antes
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putInt("user_id", userId);

                    // Salvar e-mail e senha no SharedPreferences, se o checkbox "Lembrar Senha" estiver marcado
                    if (checkBoxLembrarSenha.isChecked()) {
                        editor.putString("email", email);
                        editor.putString("senha", senha);
                        editor.putBoolean("lembrar_senha", true);  // Salvar que o usuário quer lembrar a senha
                    } else {
                        // Se não marcar, removemos os dados
                        editor.remove("email");
                        editor.remove("senha");
                        editor.putBoolean("lembrar_senha", false);  // Não lembrar senha
                    }

                    editor.apply(); // Salva as preferências

                    Toast.makeText(TelaLogin.this, "Login bem-sucedido!", Toast.LENGTH_SHORT).show();

                    // Redirecionar para a tela de estoque
                    startActivity(new Intent(TelaLogin.this, TelaEstoqueLista.class));
                    finish();
                } else {
                    Toast.makeText(TelaLogin.this, "Erro ao recuperar informações do usuário. Tente novamente.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(TelaLogin.this, "Email ou senha incorretos.", Toast.LENGTH_SHORT).show();
            }
        });

        // Inicializar TextView "Esqueci minha senha"
        TextView textViewEsqueciSenha = findViewById(R.id.textView7_EsqueciSenha);

        // Adicionar ação ao clicar na TextView
        textViewEsqueciSenha.setOnClickListener(v -> {
            Intent intent = new Intent(TelaLogin.this, RecuperarSenha.class);
            startActivity(intent);
        });
    }
}
