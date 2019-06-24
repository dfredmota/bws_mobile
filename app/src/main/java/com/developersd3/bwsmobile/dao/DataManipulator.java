package com.developersd3.bwsmobile.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import com.developersd3.bwsmobile.activity.LoginAct;
import com.developersd3.bwsmobile.model.Bem;
import com.developersd3.bwsmobile.model.BemAlocacao;
import com.developersd3.bwsmobile.model.CentroCusto;
import com.developersd3.bwsmobile.model.Colaborador;
import com.developersd3.bwsmobile.model.ControleAlocacao;
import com.developersd3.bwsmobile.model.Fornecedor;
import com.developersd3.bwsmobile.model.MaterialLocado;
import com.developersd3.bwsmobile.model.PrazoLocacao;
import com.developersd3.bwsmobile.model.UserApp;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by fred on 29/02/16.
 */
public class DataManipulator {

    private static Context context = null;
    static SQLiteDatabase db       = null;

    private static final String DATABASE_NAME = "bws.db";

    private static final int DATABASE_VERSION = 1;

    static final String TABLE_NAME_BEM = "bens";

    static final String TABLE_NAME_COLABORADOR = "colaborador";

    static final String TABLE_USUARIO = "usuarios_app";

    static final String TABLE_CONTROLE_ALOCACOES = "controle_alocacoes";

    static final String TABLE_CENTRO_CUSTO = "centro_custo";

    static final String TABLE_BENS_ALOCACAO = "bens_alocacao";

    static final String TABLE_MOVIMENTACOES = "movimentacoes";

    static final String TABLE_BENS_MOVIMENTACAO = "bens_movimentacao";

    static final String TABLE_VERSION_APK = "apk_version";

    static final String TABLE_MATERIAL_LOCADO= "materiais_locados";

    static final String TABLE_LOCACAO= "locacoes";

    static final String TABLE_FORNECEDORES= "fornecedores";

    static final String TABLE_PRAZO_LOCACAO= "prazo_locacao";

    private SQLiteStatement insertStmtColaborador;

    private SQLiteStatement insertStmtApkVersion;

    private SQLiteStatement insertStmtBens = null;

    private SQLiteStatement insertStmtUsuarioApp = null;

    private SQLiteStatement insertStmtControleAlocacao = null;

    private SQLiteStatement insertStmtCentroCusto = null;

    private SQLiteStatement insertStmtBensAlocacao = null;

    private SQLiteStatement insertStmtMovimentacao = null;

    private SQLiteStatement insertStmtBensMovimentacao = null;

    private SQLiteStatement insertStmtMaterialLocado = null;

    private SQLiteStatement insertStmtLocacoes = null;

    private SQLiteStatement insertStmtFornecedor = null;

    private SQLiteStatement insertStmtPrazoLocacao = null;

    private SQLiteStatement updateStmtControleAlocacao;

    private SQLiteStatement updateStmtBensControleAlocacao;

    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");


    private static final String INSERT_BENS = "insert into "		+ TABLE_NAME_BEM + " (codigo,insumo," +
            "tipo,quantidade) values (?,?,?,?)";

    private static final String INSERT_COLABORADOR = "insert into "		+ TABLE_NAME_COLABORADOR + " (codigo,nome,cpf," +
            "data_nascimento,funcao,telefone,dataAdmissao,dataDemissao) values (?,?,?,?,?,?,?,?)";

    private static final String INSERT_USUARIO_APP = "insert into "		+ TABLE_USUARIO + " (codigo,login,senha," +
            "super_usuario,colaborador) values (?,?,?,?,?)";

    private static final String INSERT_CONTROLE_ALOCACOES = "insert into "		+ TABLE_CONTROLE_ALOCACOES + " (data_transferencia,entregador_id," +
            "recebedor_id,centro_custo_id,sincronizado) values (?,?,?,?,?)";

    private static final String INSERT_CENTRO_CUSTO = "insert into "+ TABLE_CENTRO_CUSTO + " (codigo,nome) values (?,?)";

    private static final String INSERT_BENS_ALOCACAO = "insert into "+ TABLE_BENS_ALOCACAO +
            " (id_alocacao,bem_id,quantidade,sincronizado) values (?,?,?,?)";

    private static final String INSERT_MOVIMENTACOES = "insert into "+ TABLE_MOVIMENTACOES +
            " (id,obra_origem_id,obra_destino_id,responsavel_origem_id,responsavel_destino_id,data_movimentacao,sincronizado) values (?,?,?,?,?,?,?)";

    private static final String INSERT_BENS_MOVIMENTACAO = "insert into "+ TABLE_BENS_MOVIMENTACAO +
            " (id,bem_id,movimentacao_id,quantidade) values (?,?,?,?)";

    private static final String INSERT_APK_VERSION = "insert into "+ TABLE_VERSION_APK +
            " (versao) values (?)";

    private static final String INSERT_MATERIAL_LOCADO = "insert into "+ TABLE_MATERIAL_LOCADO +
            " (id,descricao,quantidade) values (?,?,?)";

    private static final String INSERT_LOCACOES = "insert into "+ TABLE_LOCACAO +
            " (id,fornecedor_id,contrato,valor_locacao,prazo_locacao_id,data_locacao) values (?,?,?,?,?,?)";

    private static final String INSERT_FORNECEDORES = "insert into "+ TABLE_FORNECEDORES +
            " (id,razao_social,nome_fantasia,telefone,endereco,email) values (?,?,?,?,?,?)";

    private static final String INSERT_PRAZO_LOCACAO = "insert into "+ TABLE_PRAZO_LOCACAO +
            " (id,descricao,qtd_dias) values (?,?,?)";

    private static final String UPDATE_CONTROLE_ALOCACAO = "update " + TABLE_CONTROLE_ALOCACOES + " set sincronizado=? where id= ?";

    private static final String UPDATE_BENS_CONTROLE_ALOCACAO = "update " + TABLE_BENS_ALOCACAO + " set sincronizado=? where id_alocacao= ?";



    public DataManipulator(Context context) {
        DataManipulator.context = context;

        OpenHelper openHelper = new OpenHelper(DataManipulator.context);
        DataManipulator.db = openHelper.getWritableDatabase();

        // chamar esse método caso crie novas tabelas
        //atualizaBaseDeDados(openHelper);

        // Inicializando os inserts
        this.insertStmtBens = DataManipulator.db.compileStatement(INSERT_BENS);
        this.insertStmtColaborador = DataManipulator.db.compileStatement(INSERT_COLABORADOR);
        this.insertStmtUsuarioApp = DataManipulator.db.compileStatement(INSERT_USUARIO_APP);
        this.insertStmtControleAlocacao = DataManipulator.db.compileStatement(INSERT_CONTROLE_ALOCACOES);
        this.insertStmtCentroCusto = DataManipulator.db.compileStatement(INSERT_CENTRO_CUSTO);
        this.insertStmtBensAlocacao= DataManipulator.db.compileStatement(INSERT_BENS_ALOCACAO);
        this.insertStmtMovimentacao= DataManipulator.db.compileStatement(INSERT_MOVIMENTACOES);
        this.insertStmtBensMovimentacao=DataManipulator.db.compileStatement(INSERT_BENS_MOVIMENTACAO);
        this.insertStmtApkVersion=DataManipulator.db.compileStatement(INSERT_APK_VERSION);
        this.insertStmtLocacoes=DataManipulator.db.compileStatement(INSERT_LOCACOES);
        this.insertStmtMaterialLocado=DataManipulator.db.compileStatement(INSERT_MATERIAL_LOCADO);
        this.insertStmtFornecedor=DataManipulator.db.compileStatement(INSERT_FORNECEDORES);
        this.insertStmtPrazoLocacao=DataManipulator.db.compileStatement(INSERT_PRAZO_LOCACAO);
        this.updateStmtControleAlocacao=DataManipulator.db.compileStatement(UPDATE_CONTROLE_ALOCACAO);
        this.updateStmtBensControleAlocacao=DataManipulator.db.compileStatement(UPDATE_BENS_CONTROLE_ALOCACAO);


    }

    public static void atualizaBaseDeDados(OpenHelper openHelper){

        openHelper.onUpgrade(db, DATABASE_VERSION, DATABASE_VERSION+ 1);
        db.delete(TABLE_NAME_BEM, null, null);
        db.delete(TABLE_NAME_COLABORADOR, null, null);
        db.delete(TABLE_USUARIO, null, null);
        db.delete(TABLE_CONTROLE_ALOCACOES, null, null);
        db.delete(TABLE_CENTRO_CUSTO, null, null);
        db.delete(TABLE_MOVIMENTACOES, null, null);
        db.delete(TABLE_BENS_MOVIMENTACAO, null, null);
        db.delete(TABLE_VERSION_APK, null, null);
        db.delete(TABLE_LOCACAO, null, null);
        db.delete(TABLE_MATERIAL_LOCADO, null, null);
        db.delete(TABLE_FORNECEDORES, null, null);
        db.delete(TABLE_PRAZO_LOCACAO, null, null);


    }

    public List<Bem> selectAllBensByColaboradorAndTipo (String colaborador,String tipo) {

        List<Bem> list = new ArrayList<Bem>();

        Cursor cursor = db.rawQuery("select * from bens where tipo=?", new String [] {tipo});

        //Cursor cursor = db.rawQuery("select * from menu_cardapio where grupo='1'", null);

        try {

            int x = 0;
            if (cursor.moveToFirst()) {
                do {
                    String codigo = cursor.getString(0);
                    String insumo = cursor.getString(1);
                    String tipoRetorno = cursor.getString(2);
                    Integer qtdDisponivel = cursor.getInt(3);

                    Bem bem = new Bem();

                    // colocar todos os campos
                    bem.setCodigo(Integer.parseInt(codigo));
                    bem.setInsumo(insumo);
                    bem.setTipoBem(Integer.parseInt(tipoRetorno));
                    bem.setQuantidade(qtdDisponivel);

                    list.add(bem);

                    x = x + 1;

                } while (cursor.moveToNext());
            }
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            cursor.close();

        }catch(Exception e){}

        return list;
    }


    public List<ControleAlocacao> selectAllControleAlocacoesByNotSincronized () {

        List<ControleAlocacao> lista = new ArrayList<ControleAlocacao>();

        Cursor cursor = db.rawQuery("select * from "+TABLE_CONTROLE_ALOCACOES+" where sincronizado=0", null);

        //Cursor cursor = db.rawQuery("select * from menu_cardapio where grupo='1'", null);

        try {

            int x = 0;
            if (cursor.moveToFirst()) {
                do {

                    Integer  id               = cursor.getInt(0);
                    String   dtTransferencia  = cursor.getString(1);
                    Integer  entregador       = cursor.getInt(2);
                    Integer  recebedor        = cursor.getInt(3);
                    Integer  centroCusto      = cursor.getInt(4);
                    Integer  tipoAlocacao      = cursor.getInt(5);

                    ControleAlocacao con = new ControleAlocacao();

                    con.setId(id);
                    con.setDataTransferencia(dtTransferencia);
                    con.setEntregadorId(entregador);
                    con.setRecebedorId(recebedor);
                    con.setCentroCustoId(centroCusto);
                    con.setTipoAlocacao(tipoAlocacao);

                    lista.add(con);

                } while (cursor.moveToNext());
            }
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            cursor.close();

        }catch(Exception e){}

        return lista;
    }


    public List<BemAlocacao> selectAllBensAlocacaoByNotSincronized (Integer idAlocacaoParam) {

        List<BemAlocacao> lista = new ArrayList<BemAlocacao>();

        Cursor cursor = db.rawQuery("select * from "+TABLE_BENS_ALOCACAO+" where sincronizado=0 and id_alocacao=?", new String [] {idAlocacaoParam.toString()});

        try {

            int x = 0;
            if (cursor.moveToFirst()) {
                do {

                    Integer  idAlocacao       = cursor.getInt(0);
                    Integer  idBem            = cursor.getInt(1);
                    Integer  qtdAlocada        = cursor.getInt(2);

                    BemAlocacao bem = new BemAlocacao();

                    bem.setIdAlocacao(idAlocacao);
                    bem.setIdBem(idBem);
                    bem.setQtdAlocada(qtdAlocada);

                    lista.add(bem);

                } while (cursor.moveToNext());
            }
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            cursor.close();

        }catch(Exception e){}

        return lista;
    }



    public Bem findBemById (Integer codigoParam) {

        Bem bem = new Bem();

        Cursor cursor = db.rawQuery("select * from bens where codigo=?", new String [] {codigoParam.toString()});

        //Cursor cursor = db.rawQuery("select * from menu_cardapio where grupo='1'", null);

        try {

            int x = 0;
            if (cursor.moveToFirst()) {
                do {
                    String codigo = cursor.getString(0);
                    String insumo = cursor.getString(2);

                    bem.setCodigo(Integer.parseInt(codigo));
                    bem.setInsumo(insumo);

                    break;

                } while (cursor.moveToNext());
            }
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            cursor.close();

        }catch(Exception e){}

        return bem;
    }

    public Long findVersaoApkLocal () {

        Long versao = 0l;

        Cursor cursor = db.rawQuery("select * from apk_version",null);

        //Cursor cursor = db.rawQuery("select * from menu_cardapio where grupo='1'", null);

        try {

            int x = 0;
            if (cursor.moveToFirst()) {
                do {
                    versao = cursor.getLong(0);
                    break;

                } while (cursor.moveToNext());
            }
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            cursor.close();

        }catch(Exception e){}

        return versao;
    }


    public List<Colaborador> findAllColaboradores() {

        List<Colaborador> colaborador = new ArrayList<Colaborador>();

        Cursor cursor = db.rawQuery("select * from colaborador", null);

        try {

            int x = 0;
            if (cursor.moveToFirst()) {
                do {
                    String codigo = cursor.getString(0);
                    String nome = cursor.getString(1);
                    String cpf = cursor.getString(2);
                    String dataNascimento = cursor.getString(3);
                    String funcao = cursor.getString(4);
                    String telefone = cursor.getString(5);

                    Colaborador colab = new Colaborador();

                    colab.setId(Integer.parseInt(codigo));
                    colab.setNome(nome);
                    colab.setCpf(cpf);
                    colab.setDataNascimento(format.parse(dataNascimento));
                    colab.setFuncao(Integer.parseInt(funcao));
                    colab.setTelefone(telefone);

                    colaborador.add(colab);

                } while (cursor.moveToNext());
            }
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            cursor.close();

        }catch(Exception e){}

        return colaborador;
    }

    public UserApp findUserLogado() {

        UserApp usuario = new UserApp();

        //Cursor cursor = db.rawQuery("select * from usuarios_app ", null);

        Cursor cursor = db.rawQuery("select * from usuarios_app", null);

        //Cursor cursor = db.rawQuery("select * from usuarios_app where codigo=1", null);

        //Cursor cursor = db.query(TABLE_USUARIO, new String[]{"codigo", "login", "senha","super_usuario","colaborador"}, null, null, null, null, null);

        try {

            int x = 0;
            if (cursor.moveToFirst()) {
                do {
                    String codigo = cursor.getString(0);
                    String login = cursor.getString(1);
                    String senha = cursor.getString(2);
                    String superUsuario = cursor.getString(3);
                    String colaborador = cursor.getString(4);


                    usuario.setId(Integer.parseInt(codigo));
                    usuario.setLogin(login);
                    usuario.setSenha(senha);
                    usuario.setSuperUsuario(new Boolean(superUsuario));
                    usuario.setColaborador(Integer.parseInt(colaborador));

                    break;

                } while (cursor.moveToNext());
            }
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            cursor.close();

        }catch(Exception e){}

        return usuario;
    }


    /**
     * Método responsável por buscar um Colaborador a partir de um ID
     *
     * @author Romulo Augusto romuloaugusto.silva@gmail.com
     * @param idColaborador
     * @return
     */
    public Colaborador findColaborador(Integer idColaborador) {

        Colaborador colaborador = new Colaborador();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME_COLABORADOR + " WHERE codigo=?", new String [] { idColaborador.toString()});

        try {

            int x = 0;
            if (cursor.moveToFirst()) {
                do {

                    String codigo = cursor.getString(0);
                    String nome = cursor.getString(1);
                    String cpf = cursor.getString(2);

                    colaborador.setId(Integer.parseInt(codigo));
                    colaborador.setNome(nome);
                    colaborador.setCpf(cpf);

                    break;

                } while (cursor.moveToNext());
            }
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            cursor.close();

        }catch(Exception e){}

        return colaborador;
    }


    public List<CentroCusto> findAllCentroDeCustos() {

        List<CentroCusto> listaCentro = new ArrayList<CentroCusto>();

        Cursor cursor = db.rawQuery("select * from centro_custo", null);

        try {

            int x = 0;
            if (cursor.moveToFirst()) {
                do {
                    String codigo = cursor.getString(0);
                    String nome = cursor.getString(1);

                    CentroCusto centro = new CentroCusto();

                    centro.setId(Integer.parseInt(codigo));
                    centro.setNome(nome);

                    listaCentro.add(centro);

                } while (cursor.moveToNext());
            }
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            cursor.close();

        }catch(Exception e){}

        return listaCentro;
    }

    public List<MaterialLocado> findAllMaterialLocado() {

        List<MaterialLocado> listaMateriais = new ArrayList<MaterialLocado>();

        Cursor cursor = db.rawQuery("select * from "+TABLE_MATERIAL_LOCADO, null);

        try {

            int x = 0;
            if (cursor.moveToFirst()) {
                do {
                    Long codigo = cursor.getLong(0);
                    String descricao = cursor.getString(1);
                    Long qtd = cursor.getLong(2);

                    MaterialLocado materialLocado = new MaterialLocado();

                    materialLocado.setId(codigo);
                    materialLocado.setDescricao(descricao);
                    materialLocado.setQtd(qtd);

                    listaMateriais.add(materialLocado);

                } while (cursor.moveToNext());
            }
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            cursor.close();

        }catch(Exception e){}

        return listaMateriais;
    }

    public List<PrazoLocacao> findAllPrazoLocacoes() {

        List<PrazoLocacao> listaPrazos = new ArrayList<PrazoLocacao>();

        Cursor cursor = db.rawQuery("select * from "+TABLE_PRAZO_LOCACAO, null);

        try {

            int x = 0;
            if (cursor.moveToFirst()) {
                do {
                    Long codigo = cursor.getLong(0);
                    String descricao = cursor.getString(1);
                    Long qtdDias = cursor.getLong(2);

                    PrazoLocacao prazo = new PrazoLocacao();

                    prazo.setId(codigo);
                    prazo.setDescricao(descricao);
                    prazo.setQtdDias(qtdDias);

                    listaPrazos.add(prazo);

                } while (cursor.moveToNext());
            }
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            cursor.close();

        }catch(Exception e){}

        return listaPrazos;
    }

    public List<Fornecedor> findAllFornecedores() {

        List<Fornecedor> listaFornecedores = new ArrayList<Fornecedor>();

        Cursor cursor = db.rawQuery("select * from "+TABLE_FORNECEDORES, null);

        try {

            int x = 0;
            if (cursor.moveToFirst()) {
                do {                    Long codigo          = cursor.getLong(0);
                    String razaoSocial   = cursor.getString(1);
                    String nomeFantasia  = cursor.getString(2);
                    String telefone      = cursor.getString(3);
                    String endereco      = cursor.getString(4);
                    String email         = cursor.getString(5);

                    Fornecedor fornecedor = new Fornecedor();

                    fornecedor.setId(codigo);
                    fornecedor.setRazaoSocial(razaoSocial);
                    fornecedor.setNomeFantasia(nomeFantasia);
                    fornecedor.setTelefone(telefone);
                    fornecedor.setEndereco(endereco);
                    fornecedor.setEmail(email);

                    listaFornecedores.add(fornecedor);

                } while (cursor.moveToNext());
            }
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            cursor.close();

        }catch(Exception e){}

        return listaFornecedores;
    }

    public long insertBem(Integer codigo,String insumo,String tipo,Integer qtdDisponivel) {

        this.insertStmtBens.bindString(1, codigo.toString());
        this.insertStmtBens.bindString(2, insumo);
        this.insertStmtBens.bindString(3, tipo);
        this.insertStmtBens.bindString(4, qtdDisponivel.toString());
        long insertFail =  this.insertStmtBens.executeInsert();

        return insertFail;

    }

    public long insertUsuarioApp(Integer codigo, String login,String senha,String superUsuario,Integer colaborador) {

        this.insertStmtUsuarioApp.bindString(1, codigo.toString());
        this.insertStmtUsuarioApp.bindString(2, login);
        this.insertStmtUsuarioApp.bindString(3, senha);
        this.insertStmtUsuarioApp.bindString(4, superUsuario);
        this.insertStmtUsuarioApp.bindString(5, colaborador.toString());

        long insertFail =  this.insertStmtUsuarioApp.executeInsert();

        return insertFail;

    }

    public long insertColaborador(Integer codigo, String nome,String cpf,Date dataNascimento,Integer funcaoId,String telefone) {

        this.insertStmtColaborador.bindString(1, codigo.toString());
        this.insertStmtColaborador.bindString(2, nome);
        this.insertStmtColaborador.bindString(3, cpf);
        this.insertStmtColaborador.bindString(4, format.format(dataNascimento));
        this.insertStmtColaborador.bindString(5, funcaoId.toString());
        this.insertStmtColaborador.bindString(6, telefone);

        long insertFail =  this.insertStmtColaborador.executeInsert();

        return insertFail;

    }

    public long insertControleAlocacao(String dataTransferencia,Integer entregadorId,Integer recebedorId,Integer centroCustoId
    ,Integer tipoAlocacaoId,Integer sincronizado) {

        this.insertStmtControleAlocacao.bindString(1, dataTransferencia);
        this.insertStmtControleAlocacao.bindLong(2, entregadorId);
        this.insertStmtControleAlocacao.bindLong(3, recebedorId);
        this.insertStmtControleAlocacao.bindLong(4, centroCustoId);
        this.insertStmtControleAlocacao.bindLong(5, tipoAlocacaoId);
        this.insertStmtControleAlocacao.bindLong(6, sincronizado);

        long id =  this.insertStmtControleAlocacao.executeInsert();

        return id;

    }

    public long insertMovimentacao(String dataMovimentacao,Integer obraOrigemId,Integer obraDestinoId,Integer responsavelOrigemId
            ,Integer responsavelDestinoId) {

        this.insertStmtMovimentacao.bindLong(1, obraOrigemId);
        this.insertStmtMovimentacao.bindLong(2,   obraDestinoId);
        this.insertStmtMovimentacao.bindLong(3,   responsavelOrigemId);
        this.insertStmtMovimentacao.bindLong(4,   responsavelDestinoId);
        this.insertStmtMovimentacao.bindString(5, dataMovimentacao);


        long id =  this.insertStmtMovimentacao.executeInsert();

        return id;

    }

    public long insertBemMovimentacao(Integer bemId,Integer movimentacaoId,Integer quantidade) {

        this.insertStmtBensMovimentacao.bindLong(1, bemId);
        this.insertStmtBensMovimentacao.bindLong(2, movimentacaoId);
        this.insertStmtBensMovimentacao.bindLong(2, quantidade);

        long insertFail =  this.insertStmtBensMovimentacao.executeInsert();

        return insertFail;

    }

    public long insertBemAlocacao(Integer idAlocacao,Integer bemId,Integer quantidade) {

        this.insertStmtBensAlocacao.bindLong(1, idAlocacao);
        this.insertStmtBensAlocacao.bindLong(2, bemId);
        this.insertStmtBensAlocacao.bindLong(3, quantidade);
        this.insertStmtBensAlocacao.bindLong(4, 0);

        long insertFail =  this.insertStmtBensAlocacao.executeInsert();

        return insertFail;

    }

    public long insertCentroCusto(Integer codigo,String nome) {

        this.insertStmtCentroCusto.bindLong(1, codigo);
        this.insertStmtCentroCusto.bindString(2, nome);

        long insertFail =  this.insertStmtCentroCusto.executeInsert();

        return insertFail;

    }

    public long insertApkVersion(Integer versao) {

        this.insertStmtApkVersion.bindLong(1, versao);

        long insertFail =  this.insertStmtApkVersion.executeInsert();

        return insertFail;

    }

    public long insertMaterialLocado(Long id,String descricao,Long quantidade) {

        this.insertStmtMaterialLocado.bindLong(1, id);
        this.insertStmtMaterialLocado.bindString(2, descricao);
        this.insertStmtMaterialLocado.bindLong(3, quantidade);

        long insertFail =  this.insertStmtMaterialLocado.executeInsert();

        return insertFail;

    }

    public long insertFornecedor(Long id,String razaoSocial,String nomeFantasia,String telefone,String endereco,String email) {

        this.insertStmtFornecedor.bindLong(1, id);
        this.insertStmtFornecedor.bindString(2, razaoSocial);
        this.insertStmtFornecedor.bindString(3, nomeFantasia);
        this.insertStmtFornecedor.bindString(4, telefone);
        this.insertStmtFornecedor.bindString(5, endereco);
        this.insertStmtFornecedor.bindString(6, email);


        long insertFail =  this.insertStmtFornecedor.executeInsert();

        return insertFail;

    }

    public long insertPrazoLocacao(Long id,String descricao,Long qtdDias) {

        this.insertStmtPrazoLocacao.bindLong(1, id);
        this.insertStmtPrazoLocacao.bindString(2, descricao);
        this.insertStmtPrazoLocacao.bindLong(3, qtdDias);

        long insertFail =  this.insertStmtPrazoLocacao.executeInsert();

        return insertFail;
    }

    public void updateControleAlocacaoSincronizada(Integer bem) {

        this.updateStmtControleAlocacao.bindLong(1, 1);
        this.updateStmtControleAlocacao.bindLong(2, bem);
        this.updateStmtControleAlocacao.execute();
    }

    public void updateBensControleAlocacaoSincronizada(Integer idAlocacao) {

        this.updateStmtBensControleAlocacao.bindLong(1, 1);
        this.updateStmtBensControleAlocacao.bindLong(2, idAlocacao);
        this.updateStmtBensControleAlocacao.execute();
    }

    // deleta alocações já sincronizadas
    public boolean deleteAllControleSincronizados() {
        return db.delete(TABLE_CONTROLE_ALOCACOES, "sincronizado = 1", null) > 0;
    }

    public boolean deleteAllBensControleSincronizados() {
        return db.delete(TABLE_BENS_ALOCACAO, "sincronizado = 1", null) > 0;
    }

    public void deleteAllBens() {
        db.delete(TABLE_NAME_BEM, null, null);
    }

    public void deleteAllMateriaisLocados() {
        db.delete(TABLE_MATERIAL_LOCADO, null, null);
    }

    public void deleteAllLocacoes() {
        db.delete(TABLE_LOCACAO, null, null);
    }

    public void deleteAllFornecedores() {
        db.delete(TABLE_FORNECEDORES, null, null);
    }

    public void deleteAllPrazoLocacoes() {
        db.delete(TABLE_PRAZO_LOCACAO, null, null);
    }

    public void deleteAllControleAlocacoes() {
        db.delete(TABLE_CONTROLE_ALOCACOES, null, null);
    }

    public void deleteAllCentroCustos() {
        db.delete(TABLE_CENTRO_CUSTO, null, null);
    }
    public void deleteAllColaboradores() {
        db.delete(TABLE_NAME_COLABORADOR, null, null);
    }

    public void deleteAllUsers() {
        db.delete(TABLE_USUARIO, null, null);
    }

    public void deleteBens(int rowId) {
        db.delete(TABLE_NAME_BEM, null, null);
    }
    public void deleteColaborador(int rowId) {
        db.delete(TABLE_NAME_COLABORADOR, null, null);
    }
    public void deleteUsuario(int rowId) {
        db.delete(TABLE_USUARIO, null, null);
    }

    public void limpaBaseDeDados(){

        deleteAllBens();
        deleteAllUsers();
        deleteAllColaboradores();
        deleteAllCentroCustos();
        deleteAllMateriaisLocados();
        deleteAllLocacoes();
        deleteAllFornecedores();
        deleteAllPrazoLocacoes();

        // deleta alocações já sincronizadas
        deleteAllControleSincronizados();
        deleteAllBensControleSincronizados();

    }

    public void carregaListas(UserApp user){

        DaoUtil dao = new DaoUtil();

        try {

            List<Bem> listaFromPostgres = dao.carregaBens(user.getColaborador());

            List<PrazoLocacao> listaPrazos = dao.carregaPrazosLocacoes();

            List<MaterialLocado> listaMateriais = dao.carregaMateriaisLocados();

            List<Fornecedor> listaFornecedores = dao.carregaFornecedores();

            // limpa a lista


            if (listaMateriais != null && !listaMateriais.isEmpty()) {

                for (MaterialLocado material : listaMateriais) {

                    insertMaterialLocado(material.getId().longValue(), material.getDescricao(), material.getQtd());
                }

            }

            if (listaPrazos != null && !listaPrazos.isEmpty()) {

                for (PrazoLocacao prazo : listaPrazos) {

                    insertPrazoLocacao(prazo.getId(), prazo.getDescricao(), prazo.getQtdDias());
                }

            }

            if (listaFornecedores != null && !listaFornecedores.isEmpty()) {

                for (Fornecedor fornecedor : listaFornecedores) {

                    insertFornecedor(fornecedor.getId(), fornecedor.getRazaoSocial(), fornecedor.getNomeFantasia(), fornecedor.getTelefone(), fornecedor.getEndereco(), fornecedor.getEmail());
                }

            }

            insertUsuarioApp(user.getId(), user.getLogin(), user.getSenha(),
                    user.getSuperUsuario().toString(), user.getColaborador());

            if (listaFromPostgres != null && !listaFromPostgres.isEmpty()) {

                for (Bem bem : listaFromPostgres) {

                    // realizar o insert dos bens no sqllite
                    insertBem(bem.getCodigo(), bem.getInsumo(), bem.getTipoBem().toString(), bem.getQuantidade());

                }
            }

            // carrega lista de colaboradores
            List<Colaborador> listaColaboradores = dao.carregaColaboradores();

            if (listaColaboradores != null && !listaColaboradores.isEmpty()) {

                for (Colaborador colab : listaColaboradores) {
                    insertColaborador(colab.getId(), colab.getNome(), colab.getCpf(), colab.getDataNascimento(),
                            colab.getFuncao(), colab.getTelefone());
                }
            }

            List<CentroCusto> listaCentroCustos = dao.carregaCentroDeCustos();

            if (listaCentroCustos != null && !listaCentroCustos.isEmpty()) {

                for (CentroCusto centroCusto : listaCentroCustos) {
                    insertCentroCusto(centroCusto.getId(), centroCusto.getNome());
                }
            }
        }catch (SQLException e){
            e.printStackTrace();
        }

    }



    private static class OpenHelper extends SQLiteOpenHelper {
        OpenHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            db.execSQL("CREATE TABLE " + TABLE_NAME_BEM	+ " (codigo INTEGER,insumo TEXT,tipo TEXT,quantidade INTEGER)");

            db.execSQL("CREATE TABLE " + TABLE_NAME_COLABORADOR	+ " (codigo INTEGER, nome TEXT, cpf TEXT,data_nascimento TEXT," +
                    "funcao TEXT,telefone TEXT,dataAdmissao TEXT,dataDemissao)");

            db.execSQL("CREATE TABLE " + TABLE_USUARIO	+ " (codigo INTEGER, login TEXT, senha TEXT,super_usuario TEXT," +
                    "colaborador TEXT)");

            db.execSQL("CREATE TABLE " + TABLE_CONTROLE_ALOCACOES	+ " (id INTEGER PRIMARY KEY, data_transferencia TEXT, entregador_id INTEGER,recebedor_id INTEGER," +
                    "centro_custo_id INTEGER,sincronizado INTEGER)");

            db.execSQL("CREATE TABLE " + TABLE_CENTRO_CUSTO	+ " (codigo INTEGER, nome TEXT)");

            db.execSQL("CREATE TABLE " + TABLE_BENS_ALOCACAO	+ " (id_alocacao INTEGER, bem_id INTEGER,quantidade INTEGER,sincronizado INTEGER)");

            db.execSQL("CREATE TABLE " + TABLE_MOVIMENTACOES	+ " (id INTEGER PRIMARY KEY, obra_origem_id INTEGER,obra_destino_id INTEGER,responsavel_origem_id INTEGER, responsavel_destino_id INTEGER,data_movimentacao TEXT, sincronizado INTEGER)");

            db.execSQL("CREATE TABLE " + TABLE_BENS_MOVIMENTACAO	+ " (id INTEGER PRIMARY KEY, bem_id INTEGER,movimentacao_id INTEGER,quantidade INTEGER)");

            db.execSQL("CREATE TABLE " + TABLE_VERSION_APK	+ " (versao INTEGER)");

            db.execSQL("CREATE TABLE " + TABLE_LOCACAO	+ " (id INTEGER PRIMARY KEY ,fornecedor_id INTEGER,contrato TEXT,valor_locacao TEXT ,prazo_locacao_id INTEGER ,data_locacao TEXT)");

            db.execSQL("CREATE TABLE " + TABLE_MATERIAL_LOCADO	+ " (id INTEGER ,descricao TEXT,quantidade TEXT)");

            db.execSQL("CREATE TABLE " + TABLE_FORNECEDORES	+ " (id INTEGER ,razao_social TEXT,nome_fantasia TEXT,telefone TEXT,endereco TEXT,email TEXT)");

            db.execSQL("CREATE TABLE " + TABLE_PRAZO_LOCACAO	+ " (id INTEGER ,descricao TEXT,qtd_dias INTEGER)");


        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_BEM);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_COLABORADOR);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_USUARIO);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTROLE_ALOCACOES);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_CENTRO_CUSTO);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_BENS_ALOCACAO);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_MOVIMENTACOES);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_BENS_MOVIMENTACAO);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_VERSION_APK);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCACAO);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_MATERIAL_LOCADO);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_FORNECEDORES);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRAZO_LOCACAO);
            onCreate(db);
        }



    }

}
