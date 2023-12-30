package com.g01.connection;

import javax.swing.table.DefaultTableModel;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Vector;

public abstract class Connection implements AutoCloseable {
    protected java.sql.Connection con;

    /**
     * Inicia uma nova conexão à base de dados.
     *
     * @param info as informações relativas à base de dados
     *
     * @throws SQLException se ocorreu um erro na conexão à base de dados
     * */
    public void connect(ConnectionInfo info) throws SQLException {
        con = DriverManager.getConnection(info.getUrl(), info.getUser(), info.getPassword());
    }

    /**
     * Fecha a conexão à base de dadps.
     *
     * @throws SQLException se ocorreu um erro na conexão à base de dados
     * */
    @Override
    public void close() throws SQLException {
        if (con != null)
            con.close();
    }

    /**
     * Cria um {@link DefaultTableModel} com as colunas e dados de um {@link ResultSet}
     *
     * @param resultSet a lista de resultados a converter
     *
     * @return um modelo para ser usado num {@link javax.swing.JTable}
     *
     * @throws SQLException se ocorreu um erro na conexão à base de dados
     */
    public DefaultTableModel getTableModel(ResultSet resultSet) throws SQLException {
        ResultSetMetaData metaData = resultSet.getMetaData();
        Vector<String> columns = new Vector<>();
        Vector<Vector<Object>> data = new Vector<>();

        for (int column = 1; column <= metaData.getColumnCount(); column++)
            columns.add(metaData.getColumnName(column).substring(0, 1).toUpperCase() + metaData.getColumnName(column).substring(1));

        while (resultSet.next()) {
            Vector<Object> vector = new Vector<>();

            for (int i = 1; i <= metaData.getColumnCount(); i++)
                vector.add(resultSet.getObject(i));

            data.add(vector);
        }

        return new DefaultTableModel(data, columns);
    }

    /**
     * Obtém o tuplo com o id associado
     *
     * @param id o id to tuplo
     *
     * @return a informação total do tuplo
     *
     * @throws SQLException se ocorreu um erro na conexão à base de dados
     */
    public abstract ResultSet getEntry(Object id) throws SQLException;

    /**
     * Obtém todos os tuplos de uma tabela ou um conjunto de tabelas
     *
     * @return a informação da base de dados
     *
     * @throws SQLException se ocorreu um erro na conexão à base de dados
     */
    public abstract ResultSet getEntries() throws SQLException;

    /**
     * Apresenta ao utilizador uma janela com os devidos campos a preencher (procedendo à sua inserção ou edição)
     *
     * @param defaults os valores por padrão a serem preenchidos
     *
     * @throws SQLException se ocorreu um erro na conexão à base de dados
     */
    public abstract void form(Object... defaults) throws SQLException;

    /**
     * Apaga o registo com o id especificado da base de dados.
     *
     * @param id do tuplo a apagar
     *
     * @throws SQLException se ocorreu um erro na conexão à base de dados
     * */
    public abstract void delete(Object id) throws SQLException;
}