package commer.mmr.instock;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class BancoDeDadosConfig extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "instock.db";
    private static final int DATABASE_VERSION = 2;
    //tabela de usuários:
    private static final String TABLE_USERS = "users";
    private static final String COLUMN_USER_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_PASSWORD = "password";

    // Coluna que relaciona produtos ao usuário
    private static final String COLUMN_USER_ID_FK = "user_id";

    //tabela de produtos:
    private static final String TABLE_PRODUCTS = "produtos";
    private static final String COLUMN_PRODUCT_ID = "id";
    private static final String COLUMN_NOME = "nome";
    private static final String COLUMN_DESCRICAO = "descricao";
    private static final String COLUMN_QUANTIDADE = "quantidade";
    private static final String COLUMN_IMAGE_PATH = "image_path";

    // Comandos SQL para criar tabelas:

    private static final String CREATE_TABLE_USERS =
            "CREATE TABLE " + TABLE_USERS + " (" +
                    COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_NAME + " TEXT, " +
                    COLUMN_EMAIL + " TEXT, " +
                    COLUMN_PASSWORD + " TEXT);";

    private static final String CREATE_TABLE_PRODUCTS =
            "CREATE TABLE " + TABLE_PRODUCTS + " (" +
                    COLUMN_PRODUCT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_NOME + " TEXT, " +
                    COLUMN_DESCRICAO + " TEXT, " +
                    COLUMN_QUANTIDADE + " INTEGER, " +
                    COLUMN_USER_ID_FK + " INTEGER, " +
                    "FOREIGN KEY(" + COLUMN_USER_ID_FK + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + "));";

    public BancoDeDadosConfig(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USERS);
        db.execSQL(CREATE_TABLE_PRODUCTS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);
        onCreate(db);
    }

    // Métodos para gerenciar usuários
    public boolean insertUser(String name, String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_PASSWORD, password);

        long result = db.insert(TABLE_USERS, null, values);
        return result != -1;
    }

    public boolean isEmailRegistered(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, null, COLUMN_EMAIL + "=?", new String[]{email}, null, null, null);
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public boolean validateUser(String email, String senha) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_EMAIL + " = ? AND " + COLUMN_PASSWORD + " = ?", new String[]{email, senha});
        boolean isValid = cursor.getCount() > 0;
        cursor.close();
        return isValid;
    }

    // Métodos para gerenciar produtos
    public boolean insertProduct(String nome, String descricao, int quantidade, int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NOME, nome);
        values.put(COLUMN_DESCRICAO, descricao);
        values.put(COLUMN_QUANTIDADE, quantidade);
        values.put(COLUMN_USER_ID_FK, userId);

        long result = db.insert(TABLE_PRODUCTS, null, values);
        return result != -1;
    }

    public List<Produto> getAllProducts() {
        List<Produto> produtos = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_PRODUCTS, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                Produto produto = new Produto(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRICAO)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_QUANTIDADE))
                );
                produtos.add(produto);
            }
            cursor.close();
        }
        return produtos;
    }

    public boolean updateProduct(int id, String nome, String descricao, int quantidade) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NOME, nome);
        values.put(COLUMN_DESCRICAO, descricao);
        values.put(COLUMN_QUANTIDADE, quantidade);

        int rowsAffected = db.update(TABLE_PRODUCTS, values, COLUMN_PRODUCT_ID + "=?", new String[]{String.valueOf(id)});
        return rowsAffected > 0;
    }
    public boolean deleteProduct(int productId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsDeleted = db.delete(TABLE_PRODUCTS, COLUMN_PRODUCT_ID + "=?", new String[]{String.valueOf(productId)});
        return rowsDeleted > 0;
    }
    public List<Produto> getProductsByUserId(int userId) {
        List<Produto> produtos = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_PRODUCTS + " WHERE " + COLUMN_USER_ID_FK + " = ?", new String[]{String.valueOf(userId)});

        if (cursor != null) {
            while (cursor.moveToNext()) {
                Produto produto = new Produto(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRICAO)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_QUANTIDADE))
                );
                produtos.add(produto);
            }
            cursor.close();
        }
        return produtos;
    }
    // Recupera o ID do usuário com base no email
    public int getUserIdByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT " + COLUMN_USER_ID + " FROM " + TABLE_USERS + " WHERE " + COLUMN_EMAIL + " = ?",
                new String[]{email}
        );

        int userId = -1; // Valor padrão caso o email não seja encontrado

        if (cursor != null && cursor.moveToFirst()) {
            userId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USER_ID));
            cursor.close();
        }

        return userId;
    }
    //jogar info para tela user
    public String[] getUserDetailsById(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT " + COLUMN_NAME + ", " + COLUMN_EMAIL + ", " + COLUMN_PASSWORD +
                        " FROM " + TABLE_USERS +
                        " WHERE " + COLUMN_USER_ID + " = ?",
                new String[]{String.valueOf(userId)}
        );

        String[] userDetails = new String[3]; // Array para armazenar Nome, E-mail e Senha
        if (cursor != null && cursor.moveToFirst()) {
            userDetails[0] = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)); // Nome
            userDetails[1] = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)); // E-mail
            userDetails[2] = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD)); // Senha
            cursor.close();
        }
        return userDetails;
    }
    public boolean attNovaSenhaRec(String email, String novaSenha) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PASSWORD, novaSenha);  // Atualiza a senha com a nova senha gerada

        // Atualiza a senha do usuário com o e-mail especificado
        int rowsAffected = db.update(TABLE_USERS, values, COLUMN_EMAIL + " = ?", new String[]{email});

        return rowsAffected > 0;  // Retorna true se a senha foi atualizada
    }

}
