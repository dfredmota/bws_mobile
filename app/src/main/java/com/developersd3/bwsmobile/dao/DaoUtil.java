package com.developersd3.bwsmobile.dao;


import com.developersd3.bwsmobile.model.Bem;
import com.developersd3.bwsmobile.model.CentroCusto;
import com.developersd3.bwsmobile.model.Colaborador;
import com.developersd3.bwsmobile.model.Fornecedor;
import com.developersd3.bwsmobile.model.MaterialLocado;
import com.developersd3.bwsmobile.model.PrazoLocacao;
import com.developersd3.bwsmobile.model.UserApp;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class DaoUtil {

    Connection con = null;
    PreparedStatement ptmt = null;
    ResultSet rs = null;

    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

    private Connection getConnection() throws SQLException {
        Connection conn = null;
        try {

            try {
                Class.forName("org.postgresql.Driver");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            // production
            String url = "jdbc:postgresql://107.170.29.107:5432/bws_mobile_production?user=deployer&password=1q3e!Q#E";

            //String url = "jdbc:postgresql://107.170.29.107:5432/bws?user=postgres&password=";

            DriverManager.setLoginTimeout(5);

            conn = DriverManager.getConnection(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return conn;
    }

    public Integer insertControleAlocacao(String dataTransferencia, Integer entregadorId, Integer recebedorId, Integer centroCustoId
            , Integer tipoAlocacaoId,String endereco) {

        Integer idControleAlocacao = null;

        try {

            String sql = "insert into controle_alocacoes(data_transferencia,entregador_id,recebedor_id,centro_custo_id,tipo_alocacao_id,endereco,created_at,updated_at) values(?,?,?,?,?,?,?,?) returning id;";

            con = getConnection();

            ptmt = con.prepareStatement(sql);

            ptmt.setDate(1, new java.sql.Date(format.parse(dataTransferencia).getTime()));
            ptmt.setInt(2, entregadorId);
            ptmt.setInt(3, recebedorId);
            ptmt.setInt(4, centroCustoId);
            ptmt.setInt(5,tipoAlocacaoId);
            ptmt.setString(6,endereco);
            ptmt.setDate(7, new java.sql.Date(format.parse(dataTransferencia).getTime()));
            ptmt.setDate(8, new java.sql.Date(format.parse(dataTransferencia).getTime()));


            ResultSet rs = ptmt.executeQuery();

            if (rs.next())
                idControleAlocacao = rs.getInt("id");

            if (idControleAlocacao != null)
                return idControleAlocacao;
            else
                return idControleAlocacao;

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (ptmt != null)
                    ptmt.close();
                if (con != null)
                    con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        return  idControleAlocacao;
    }

    public Integer insertMovimentacao(String dataTransferencia, Integer obraOrigemId, Integer obraDestinoId, Integer responsavelOrigemId
            , Integer responsavelDestinoId) {

        Integer idMovimentacao = null;

        try {

            String sql = "insert into movimentacoes(obra_origem_id,obra_destino_id,responsavel_origem_id,responsavel_destino_id,data_movimentacao) values(?,?,?,?,?) returning id;";

            con = getConnection();

            ptmt = con.prepareStatement(sql);


            ptmt.setInt(1, obraOrigemId);
            ptmt.setInt(2, obraDestinoId);
            ptmt.setInt(3, responsavelOrigemId);
            ptmt.setInt(4, responsavelDestinoId);
            ptmt.setDate(5, new java.sql.Date(format.parse(dataTransferencia).getTime()));


            ResultSet rs = ptmt.executeQuery();

            if (rs.next())
                idMovimentacao = rs.getInt("id");

            if (idMovimentacao != null)
                return idMovimentacao;
            else
                return idMovimentacao;

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (ptmt != null)
                    ptmt.close();
                if (con != null)
                    con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        return  idMovimentacao;
    }

    public void insertBensMovimentacao(Integer movimentacaoId, Integer bemId,Integer qtd) {

        Integer idControleAlocacao = null;

        try {

            String sql = "insert into bens_movimentacao(bem_id,movimentacao_id,quantidade) values(?,?,?);";

            con = getConnection();

            ptmt = con.prepareStatement(sql);

            ptmt.setInt(1, bemId);
            ptmt.setInt(2, movimentacaoId);
            ptmt.setInt(3, qtd);

            ptmt.execute();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (ptmt != null)
                    ptmt.close();
                if (con != null)
                    con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    public void insertBensAlocacao(Integer alocacaoId, Integer bemId,Integer qtdAlocada) {

        Integer idControleAlocacao = null;

        try {

            String sql = "insert into bens_alocacao(bem_id,controle_alocacao_id,created_at,updated_at,quantidade_alocada) values(?,?,?,?,?);";

            con = getConnection();

            ptmt = con.prepareStatement(sql);

            ptmt.setInt(1, bemId);
            ptmt.setInt(2, alocacaoId);
            ptmt.setDate(3, new java.sql.Date(new java.util.Date().getTime()));
            ptmt.setDate(4, new java.sql.Date(new java.util.Date().getTime()));
            ptmt.setInt(5, qtdAlocada);

            ptmt.execute();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (ptmt != null)
                    ptmt.close();
                if (con != null)
                    con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }


    public void atualizaQtdBensDiminui(Integer codigoBem, Integer qtdDiminuir,Integer colaborador) {

        try {

            String sql = "UPDATE bem_colaboradores SET quantidade = (quantidade - "+qtdDiminuir+") WHERE bem_id="+codigoBem+" and colaborador_id="+colaborador+";";

            con = getConnection();

            ptmt = con.prepareStatement(sql);

            ptmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (ptmt != null)
                    ptmt.close();
                if (con != null)
                    con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    public void atualizaQtdBensSoma(Integer codigoBem, Integer qtdDisponivel,Integer colaborador) {

        try {

            String sql = "UPDATE bem_colaboradores SET quantidade = (quantidade + "+qtdDisponivel+") WHERE bem_id="+codigoBem+" and colaborador_id="+colaborador+";";

            con = getConnection();

            ptmt = con.prepareStatement(sql);

            ptmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (ptmt != null)
                    ptmt.close();
                if (con != null)
                    con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    public void insertQtdBemColaborador(Integer codigoBem, Integer qtdAlocada,Integer colaborador) {

        try {

            String sql = "insert into bem_colaboradores(bem_id,colaborador_id,quantidade,created_at,updated_at) values(?,?,?,?,?);";

            con = getConnection();

            ptmt = con.prepareStatement(sql);

            ptmt.setInt(1, codigoBem);
            ptmt.setInt(2, colaborador);
            ptmt.setInt(3, qtdAlocada);
            ptmt.setDate(4, new java.sql.Date(new java.util.Date().getTime()));
            ptmt.setDate(5, new java.sql.Date(new java.util.Date().getTime()));

            ptmt.execute();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (ptmt != null)
                    ptmt.close();
                if (con != null)
                    con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    public void atualizaBemColaborador(Integer codigoBem,Integer idColaborador) {

        try {

            String sql = "UPDATE bens SET colaborador_id = "+idColaborador+" WHERE id="+codigoBem+";";

            con = getConnection();

            ptmt = con.prepareStatement(sql);

            ptmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (ptmt != null)
                    ptmt.close();
                if (con != null)
                    con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    public Boolean temBemNaCarga(Integer colaborador,Integer bemId) throws SQLException {

        Boolean retorno = false;

        con = getConnection();

        String query = "select quantidade  from bem_colaboradores where colaborador_id=" + colaborador + " and bem_id="+bemId+";";

        ptmt = con.prepareStatement(query);

        rs = ptmt.executeQuery();

        if(rs.next())
            retorno = true;

        return retorno;
    }


    public void deletaZerados(Integer colaborador) throws SQLException {

        Integer retorno = 0;

        con = getConnection();

        String queryDel = "delete from bem_colaboradores where quantidade=0";

        ptmt = con.prepareStatement(queryDel);

        ptmt.execute();
    }


    public List<Colaborador> carregaColaboradores() throws SQLException {

        List<Colaborador> lista = new ArrayList<Colaborador>();

        con = getConnection();

        String query = "SELECT colab.cpf as cpf,colab.id as id,colab.nome as nome,colab.funcao_id as funcao_id,colab.data_admissao,colab.data_demissao,"+
        " colab.data_nascimento as data_nascimento,tel.ddd as ddd,tel.numero as numero FROM colaboradores colab, colaborador_telefones tel where colab.id = tel.colaborador_id";

        ptmt = con.prepareStatement(query);

        rs = ptmt.executeQuery();

        while (rs.next()) {

            Integer id             = rs.getInt("id");
            String nome            = rs.getString("nome");
            String cpf             = rs.getString("cpf");
            Date dataNascimento    = rs.getDate("data_nascimento");
            Integer funcaoId       = rs.getInt("funcao_id");
            String  ddd            = rs.getString("ddd");
            String  numero         = rs.getString("numero");

            Colaborador colab = new Colaborador();

            colab.setId(id);
            colab.setNome(nome);
            colab.setCpf(cpf);
            colab.setDataNascimento(dataNascimento);
            colab.setFuncao(funcaoId);
            colab.setTelefone(ddd+numero);

            lista.add(colab);

        }

        return lista;

    }

    public List<Bem> carregaBensControleAlocacao (Integer codigoColab,String date,Integer tipo) throws SQLException {

        List<Bem> lista = new ArrayList<Bem>();

        con = getConnection();

        StringBuilder sql= new StringBuilder();

        sql.append("select bem.id as idBem,insumo.descricao as insumo,ben_aloc.quantidade_alocada as qtd_devolvida from bens as bem,controle_alocacoes as controle ,bens_alocacao as ben_aloc, insumos as insumo ");
        sql.append(" where ben_aloc.controle_alocacao_id = controle.id  and ben_aloc.bem_id = bem.id and ");
        sql.append(" controle.data_transferencia= to_date('"+date+" 00:00:00', 'DD-MM-YYYY HH24:MI:SS') and  bem.insumo_id = insumo.id and controle.recebedor_id="+codigoColab);
        sql.append(" and bem.tipo_bem_id="+tipo);

        ptmt = con.prepareStatement(sql.toString());

        rs = ptmt.executeQuery();

        while (rs.next()) {

            Integer codigoBem             = rs.getInt("idBem");
            String  insumo                = rs.getString("insumo");
            Integer qtd             = rs.getInt("qtd_devolvida");

            Bem bem = new Bem();

            bem.setCodigo(codigoBem);
            bem.setInsumo(insumo);
            bem.setQtdDevolvida(qtd);
            lista.add(bem);

        }

        return lista;

    }

    public List<CentroCusto> carregaCentroDeCustos() throws SQLException {

        List<CentroCusto> lista = new ArrayList<CentroCusto>();

        con = getConnection();

        String query = "SELECT * FROM centro_custos";

        ptmt = con.prepareStatement(query);

        rs = ptmt.executeQuery();

        while (rs.next()) {

            Integer id = rs.getInt("id");
            String descricao = rs.getString("descricao");


            CentroCusto centro = new CentroCusto();

            centro.setId(id);
            centro.setNome(descricao);

            lista.add(centro);

        }

        return lista;

    }


    public UserApp validarLogin(String usuario, String senha) throws SQLException {

        UserApp user = new UserApp();

        con = getConnection();

        String query = "SELECT * FROM usuarios_mobiles where login='" + usuario + "' and senha='" + senha + "'";

        ptmt = con.prepareStatement(query);

        rs = ptmt.executeQuery();

        while (rs.next()) {

            Integer id = rs.getInt("id");
            String login = rs.getString("login");
            String senhaRetorno = rs.getString("senha");
            Integer colaboradorId = rs.getInt("colaborador_id");
            Boolean superUsuario = rs.getBoolean("super_usuario");

            user.setId(id);
            user.setLogin(login);
            user.setSenha(senhaRetorno);
            user.setSuperUsuario(superUsuario);
            user.setColaborador(colaboradorId);

        }

        return user;

    }

    /**
     * Efetua loggin
     *
     * @throws SQLException
     */

    public List<Bem> carregaBens(Integer colaborador) throws SQLException {

        Integer id = null;

        List<Bem> lista = new ArrayList<Bem>();

        con = getConnection();

        String query = "select b.id as id,i.descricao as insumo,b.tipo_bem_id as tipo_bem_id,bc.quantidade as quantidade, " +
                "b.qtd_disponivel as qtd_disponivel  from bens b,bem_colaboradores bc, insumos i where bc.colaborador_id="+colaborador+" and " +
                "b.insumo_id = i.id and bc.bem_id = b.id and bc.quantidade != 0";

        ptmt = con.prepareStatement(query);

        rs = ptmt.executeQuery();


        while (rs.next()) {

            Bem bem = new Bem();

            id = rs.getInt("id");

            bem.setCodigo(id);
            bem.setInsumo(rs.getString("insumo"));
            bem.setTipoBem(rs.getInt("tipo_bem_id"));
            bem.setQuantidade(rs.getInt("quantidade"));

            lista.add(bem);
        }

        return lista;

    }


    public List<MaterialLocado> carregaMateriaisLocados() throws SQLException {

        List<MaterialLocado> lista = new ArrayList<MaterialLocado>();

        con = getConnection();

        String query = "SELECT * FROM materiais";

        ptmt = con.prepareStatement(query);

        rs = ptmt.executeQuery();

        while (rs.next()) {

            Integer id = rs.getInt("id");
            String descricao = rs.getString("descricao");
            Long qtd = rs.getLong("quantidade");

            MaterialLocado materialLocado = new MaterialLocado();

            materialLocado.setId(id.longValue());
            materialLocado.setDescricao(descricao);
            materialLocado.setQtd(qtd);

            lista.add(materialLocado);

        }

        return lista;

    }

    public List<PrazoLocacao> carregaPrazosLocacoes() throws SQLException {

        List<PrazoLocacao> lista = new ArrayList<PrazoLocacao>();

        con = getConnection();

        String query = "SELECT * FROM prazo_locacoes";

        ptmt = con.prepareStatement(query);

        rs = ptmt.executeQuery();

        while (rs.next()) {

            Integer id = rs.getInt("id");
            String descricao = rs.getString("descricao");
            Long qtd = rs.getLong("qtd_dias");

            PrazoLocacao prazo = new PrazoLocacao();

            prazo.setId(id.longValue());
            prazo.setDescricao(descricao);
            prazo.setQtdDias(qtd);

            lista.add(prazo);

        }

        return lista;

    }

    public List<Fornecedor> carregaFornecedores() throws SQLException {

        List<Fornecedor> lista = new ArrayList<Fornecedor>();

        con = getConnection();

        String query = "SELECT * FROM fornecedores";

        ptmt = con.prepareStatement(query);

        rs = ptmt.executeQuery();

        while (rs.next()) {

            Integer id = rs.getInt("id");
            String razaoSocial = rs.getString("razao_social");
            String nomeFantasia = rs.getString("nome_fantasia");
            String telefone = rs.getString("telefone");
            String endereco = rs.getString("endereco");
            String email = rs.getString("email");

            Fornecedor fornecedor = new Fornecedor();

            fornecedor.setId(id.longValue());
            fornecedor.setRazaoSocial(razaoSocial);
            fornecedor.setNomeFantasia(nomeFantasia);
            fornecedor.setTelefone(telefone);
            fornecedor.setEndereco(endereco);
            fornecedor.setEmail(email);

            lista.add(fornecedor);

        }

        return lista;

    }


    public Integer carregaQtdBem(Integer bemId) throws SQLException {

        Integer qtdDisponivel = 0;

        con = getConnection();

        String query = "SELECT qtd_disponivel FROM bens where id="+bemId;

        ptmt = con.prepareStatement(query);

        rs = ptmt.executeQuery();

        while (rs.next()) {

            qtdDisponivel = rs.getInt("qtd_disponivel");

        }

        return qtdDisponivel;

    }


    public Integer insertLocacao(Long fornecedor, String contrato, String valorLocacao, Integer prazoLocacaoId,Integer funcionarioResponsavel) {

        Integer idLocacao = null;

        try {

            String sql = "insert into locacoes(fornecedor_id,contrato,prazo_locacao_id,data_locacao,funcionario_responsavel,valor_locacao) values(?,?,?,?,?,?) returning id;";

            con = getConnection();

            ptmt = con.prepareStatement(sql);


            ptmt.setInt(1, fornecedor.intValue());
            ptmt.setString(2, contrato);
            ptmt.setInt(3, prazoLocacaoId);
            ptmt.setDate(4, new java.sql.Date(new java.util.Date().getTime()));
            ptmt.setInt(5, funcionarioResponsavel);
            ptmt.setString(6, valorLocacao);

            ResultSet rs = ptmt.executeQuery();

            if (rs.next())
                idLocacao = rs.getInt("id");

            if (idLocacao != null)
                return idLocacao;
            else
                return idLocacao;

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (ptmt != null)
                    ptmt.close();
                if (con != null)
                    con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        return  idLocacao;
    }

    public void insertMateriaisLocacao(Integer materialLocadoId, Integer locacaoId,Integer qtdAlocada) {

        Integer idControleAlocacao = null;

        try {

            String sql = "insert into locacao_materiais(material_locado_id,locacao_id,qtd_locada) values(?,?,?);";

            con = getConnection();

            ptmt = con.prepareStatement(sql);

            ptmt.setInt(1, materialLocadoId);
            ptmt.setInt(2, locacaoId);
            ptmt.setInt(3, qtdAlocada);

            ptmt.execute();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (ptmt != null)
                    ptmt.close();
                if (con != null)
                    con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    public void atualizaQtdMaterialLocado(Integer codigoMaterial, Integer qtdDiminuir) {

        try {

            String sql = "UPDATE materiais SET quantidade = (quantidade - "+qtdDiminuir+") WHERE id="+codigoMaterial+";";

            con = getConnection();

            ptmt = con.prepareStatement(sql);

            ptmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (ptmt != null)
                    ptmt.close();
                if (con != null)
                    con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }


}
