package commer.mmr.instock;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.ArrayList;  // Importando ArrayList

public class ProdutoAdapter extends RecyclerView.Adapter<ProdutoAdapter.ProdutoViewHolder> {
    private List<Produto> produtos;
    private List<Produto> produtosOriginal; // Lista original sem filtro
    private Context context;
    private BancoDeDadosConfig bancoDeDadosConfig;

    public ProdutoAdapter(List<Produto> produtos, Context context) {
        this.produtos = produtos;
        this.produtosOriginal = new ArrayList<>(produtos); // Salvar a lista original
        this.context = context;
        this.bancoDeDadosConfig = new BancoDeDadosConfig(context);
    }

    @NonNull
    @Override
    public ProdutoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.produtos, parent, false);
        return new ProdutoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProdutoViewHolder holder, int position) {
        Produto produto = produtos.get(position);

        holder.textNome.setText(produto.getNome());
        holder.textDescricao.setText(produto.getDescricao());
        holder.textQuantidade.setText(String.valueOf(produto.getQuantidade()));

        // Botão de Editar
        holder.btnEditar.setOnClickListener(v -> {
            Intent intent = new Intent(context, TelaAdicionarItem.class); // Usa a mesma tela
            intent.putExtra("produtoId", produto.getId());
            intent.putExtra("nome", produto.getNome());
            intent.putExtra("descricao", produto.getDescricao());
            intent.putExtra("quantidade", produto.getQuantidade());
            context.startActivity(intent);
        });

        // Botão de Excluir
        holder.btnExcluir.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Excluir Produto")
                    .setMessage("Tem certeza de que deseja excluir este produto?")
                    .setPositiveButton("Sim", (dialog, which) -> {
                        boolean isDeleted = bancoDeDadosConfig.deleteProduct(produto.getId());
                        if (isDeleted) {
                            Toast.makeText(context, "Produto excluído com sucesso!", Toast.LENGTH_SHORT).show();
                            produtos.remove(position);
                            notifyItemRemoved(position);
                        } else {
                            Toast.makeText(context, "Erro ao excluir o produto.", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Não", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return produtos.size();
    }

    // Método para atualizar a lista com os produtos filtrados
    public void updateList(List<Produto> newList) {
        produtos = newList;
        notifyDataSetChanged();
    }

    public static class ProdutoViewHolder extends RecyclerView.ViewHolder {
        TextView textNome, textDescricao, textQuantidade;
        Button btnEditar, btnExcluir;

        public ProdutoViewHolder(@NonNull View itemView) {
            super(itemView);
            textNome = itemView.findViewById(R.id.textNome);
            textDescricao = itemView.findViewById(R.id.textDescricao);
            textQuantidade = itemView.findViewById(R.id.textQuantidade);
            btnEditar = itemView.findViewById(R.id.btnEditar);
            btnExcluir = itemView.findViewById(R.id.btnExcluir);
        }
    }
}
