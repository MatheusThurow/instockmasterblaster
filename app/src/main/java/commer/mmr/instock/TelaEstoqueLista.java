package commer.mmr.instock;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class TelaEstoqueLista extends AppCompatActivity {

    private RecyclerView recyclerViewProdutos;
    private ProdutoAdapter produtoAdapter;
    private BancoDeDadosConfig bancoDeDadosConfig;
    private List<Produto> listaProdutosOriginal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_estoque_lista);

        bancoDeDadosConfig = new BancoDeDadosConfig(this);
        recyclerViewProdutos = findViewById(R.id.recyclerViewProdutos);
        recyclerViewProdutos.setLayoutManager(new LinearLayoutManager(this));

        FloatingActionButton btnAdicionarItem = findViewById(R.id.BtnAdicionarItem);
        btnAdicionarItem.setOnClickListener(v -> {
            // Abre a tela de adicionar sem dados (modo de adição)
            Intent intent = new Intent(TelaEstoqueLista.this, TelaAdicionarItem.class);
            startActivity(intent);
        });

        // Configurar o botão "Sair"
        ImageButton btnSair = findViewById(R.id.btnSair);
        btnSair.setOnClickListener(v -> logout());

        // Configurar o botão "usuário"
        ImageButton btnBoneco = findViewById(R.id.btn_boneco);
        btnBoneco.setOnClickListener(v -> {
            // Redirecionar para a TelaUser
            Intent intent = new Intent(TelaEstoqueLista.this, TelaUser.class);
            startActivity(intent);
        });

        // Configurar a SearchView para filtrar os produtos
        SearchView searchView = findViewById(R.id.search_bar);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterProducts(newText); // Chama o método para filtrar
                return false;
            }
        });

        loadProductList();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadProductList();
    }

    private void loadProductList() {
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        int userId = sharedPreferences.getInt("user_id", -1);

        if (userId != -1) {
            List<Produto> produtos = bancoDeDadosConfig.getProductsByUserId(userId);
            listaProdutosOriginal = new ArrayList<>(produtos); // Salvar a lista original
            produtoAdapter = new ProdutoAdapter(produtos, this);
            recyclerViewProdutos.setAdapter(produtoAdapter);
        }
    }

    private void filterProducts(String query) {
        // Filtrar a lista de produtos com base no nome do produto
        List<Produto> filteredList = new ArrayList<>();
        for (Produto produto : listaProdutosOriginal) {
            if (produto.getNome().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(produto);
            }
        }
        produtoAdapter.updateList(filteredList); // Atualizar o adapter com a lista filtrada
    }

    private void logout() {
        // Limpar os dados do usuário nos SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear(); // Remove todos os dados
        editor.apply();

        // Redirecionar para a tela de login
        Intent intent = new Intent(TelaEstoqueLista.this, TelaLogin.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Remove a pilha de atividades anteriores
        startActivity(intent);
        finish();
    }
}
