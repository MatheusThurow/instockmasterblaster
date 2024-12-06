package commer.mmr.instock;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;

public class TelaAdicionarItem extends AppCompatActivity {

    private EditText editNomeProduto, editQuantidadeProduto;
    private TextInputLayout textInputLayoutDescricao;
    private BancoDeDadosConfig bancoDeDadosConfig;
    private boolean isEditing = false; // Indica se estamos editando um produto
    private int produtoId; // ID do produto para edição
    private int userId; // ID do usuário logado

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_adicionar_item);

        // Recuperar o userId dos SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        userId = sharedPreferences.getInt("user_id", -1); // -1 indica falha ao obter o ID

        if (userId == -1) {
            Toast.makeText(this, "Erro ao identificar o usuário. Faça login novamente.", Toast.LENGTH_SHORT).show();
            finish(); // Fecha a tela e força o usuário a refazer o login
            return;
        }

        // Inicialize os campos
        editNomeProduto = findViewById(R.id.editTextText);
        textInputLayoutDescricao = findViewById(R.id.textInputLayout);
        editQuantidadeProduto = findViewById(R.id.editTextNumber2);

        bancoDeDadosConfig = new BancoDeDadosConfig(this);

        // Receber dados via Intent
        Intent intent = getIntent();
        produtoId = intent.getIntExtra("produtoId", -1);
        if (produtoId != -1) {
            // Modo de edição
            isEditing = true;
            String nome = intent.getStringExtra("nome");
            String descricao = intent.getStringExtra("descricao");
            int quantidade = intent.getIntExtra("quantidade", 0);

            // Preencher os campos com os dados do produto
            editNomeProduto.setText(nome);
            if (textInputLayoutDescricao.getEditText() != null) {
                textInputLayoutDescricao.getEditText().setText(descricao);
            }
            editQuantidadeProduto.setText(String.valueOf(quantidade));

            // Atualizar o texto do botão
            Button buttonSalvar = findViewById(R.id.button_Salvar_item);
            buttonSalvar.setText("Salvar Alterações");

            // Definir o evento de clique para salvar alterações
            buttonSalvar.setOnClickListener(v -> salvarAlteracoes());
        } else {
            // Modo de adição
            Button buttonSalvar = findViewById(R.id.button_Salvar_item);
            buttonSalvar.setOnClickListener(v -> adicionarNovoItem());
        }

        // Botão voltar
        ImageView btnVoltar = findViewById(R.id.seta_v_t_add_item);
        btnVoltar.setOnClickListener(v -> onBackPressed());
    }

    private void adicionarNovoItem() {
        String nome = editNomeProduto.getText().toString().trim();
        String descricao = textInputLayoutDescricao.getEditText() != null
                ? textInputLayoutDescricao.getEditText().getText().toString().trim()
                : "";
        int quantidade;

        // Validação dos campos
        if (nome.isEmpty() || descricao.isEmpty()) {
            Toast.makeText(this, "Por favor, preencha todos os campos.", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            quantidade = Integer.parseInt(editQuantidadeProduto.getText().toString().trim());
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Por favor, insira uma quantidade válida.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Inserir no banco de dados
        boolean isInserted = bancoDeDadosConfig.insertProduct(nome, descricao, quantidade, userId);
        if (isInserted) {
            Toast.makeText(this, "Produto adicionado com sucesso!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Erro ao adicionar o produto.", Toast.LENGTH_SHORT).show();
        }
    }

    private void salvarAlteracoes() {
        String novoNome = editNomeProduto.getText().toString().trim();
        String novaDescricao = textInputLayoutDescricao.getEditText() != null
                ? textInputLayoutDescricao.getEditText().getText().toString().trim()
                : "";
        int novaQuantidade;

        // Validação dos campos
        if (novoNome.isEmpty() || novaDescricao.isEmpty()) {
            Toast.makeText(this, "Por favor, preencha todos os campos.", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            novaQuantidade = Integer.parseInt(editQuantidadeProduto.getText().toString().trim());
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Quantidade inválida!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Atualizar no banco de dados
        boolean isUpdated = bancoDeDadosConfig.updateProduct(produtoId, novoNome, novaDescricao, novaQuantidade);
        if (isUpdated) {
            Toast.makeText(this, "Produto atualizado com sucesso!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Erro ao atualizar o produto. Tente novamente.", Toast.LENGTH_SHORT).show();
        }
    }
}
