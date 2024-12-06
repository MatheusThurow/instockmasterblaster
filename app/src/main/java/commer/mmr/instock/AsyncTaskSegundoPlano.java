package commer.mmr.instock;

import android.os.AsyncTask;
import android.widget.Toast;
import android.content.Context;

public class AsyncTaskSegundoPlano extends AsyncTask<String, Void, Boolean> {
    private Context context;

    public AsyncTaskSegundoPlano(Context context) {
        this.context = context;
    }

    @Override
    protected Boolean doInBackground(String... params) {
        String emailDestinatario = params[0];
        String assunto = params[1];
        String mensagem = params[2];

        EnviarEmail enviarEmail = new EnviarEmail();
        return enviarEmail.enviar(emailDestinatario, assunto, mensagem);
    }

    @Override
    protected void onPostExecute(Boolean sucesso) {
        if (sucesso) {
            Toast.makeText(context, "E-mail de recuperação enviado com sucesso!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Erro ao enviar o e-mail. Tente novamente.", Toast.LENGTH_SHORT).show();
        }
    }
}
