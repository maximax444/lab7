package Server.db;

import Server.Hasher;
import Server.Program.CollectionManager;
import common.Exceptions.CoordinatesException;
import common.MainConsole;
import common.Task.*;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.TreeSet;

public class DbHandler {
    private String dbAdress;
    private String dbLogin;
    private String dbPassword;
    private Connection dbConnection;
    private CollectionManager collectionManager;
    private static final String ADD_USER_REQUEST = "INSERT INTO PROG_USERS (login, password) VALUES (?, ?)";
    private static final String GET_USERS_REQUEST = "SELECT * FROM PROG_USERS";
    private static final String GET_COLLECTION = "SELECT * FROM COLLECTION";
    private static final String ADD_COLLECTION_ELEMENT = "INSERT INTO COLLECTION (id, name, coorX, coorY, time, price, partNumber,manufactureCost, unitOfMeasure, orId, orName, orFullName, annualTurnover, orType, zipCode, locX, locY, locName, author) VALUES (?, ?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    private static final String DELITE_ELEMENT = "DELETE FROM COLLECTION WHERE id = ?";
    public DbHandler(String dbAdress, String dbLogin, String dbPassword, CollectionManager collectionManager) {
        this.dbAdress = dbAdress;
        this.dbLogin = dbLogin;
        this.dbPassword = dbPassword;
        this.collectionManager = collectionManager;
    }

    public void connectionToDb() {
        try {
            dbConnection = DriverManager.getConnection(dbAdress, dbLogin, dbPassword);
            MainConsole.println("База данных подключена!");

        } catch (SQLException throwables) {
            MainConsole.println("Ошибка при подключении базы данных!");
        }

    }
    public synchronized boolean registerUser(String login, String password) throws SQLException {
        PreparedStatement addStatement = dbConnection.prepareStatement(ADD_USER_REQUEST);
        addStatement.setString(1, login);
        addStatement.setString(2, Hasher.hashPassword(password));
        addStatement.executeUpdate();
        addStatement.close();
        return true;
    }
    public synchronized boolean checkUser(String login) throws SQLException {

        try {
            PreparedStatement getStatement = dbConnection.prepareStatement(GET_USERS_REQUEST);
            ResultSet rs = getStatement.executeQuery();
            while (rs.next()) {
                String iterLogin = rs.getString("login");
                MainConsole.println(iterLogin);
                MainConsole.println(login);
                if (iterLogin.equals(login)) {

                    return true;
                }
            }
            getStatement.close();
        } catch (SQLException throwables) {
            MainConsole.println("Ошибка базы данных!");
            throwables.printStackTrace();
        }

        return false;
    }
    public synchronized boolean checkUserPass(String login, String password) throws SQLException {

        try {
            PreparedStatement getStatement = dbConnection.prepareStatement(GET_USERS_REQUEST);
            ResultSet rs = getStatement.executeQuery();
            while (rs.next()) {
                String iterLogin = rs.getString("login");
                String iterPass = rs.getString("password");
                if (iterLogin.equals(login) && iterPass.equals(Hasher.hashPassword(password))) {
                    return true;
                }
            }
            getStatement.close();
        } catch (SQLException throwables) {
            MainConsole.println("Ошибка базы данных!");
            throwables.printStackTrace();
        }

        return false;
    }

    public synchronized TreeSet<Product> loadCollectionFromDB() {
        TreeSet<Product> coll = new TreeSet<>();
        try {
            PreparedStatement getStatement = dbConnection.prepareStatement(GET_COLLECTION);
            ResultSet rs = getStatement.executeQuery();
            while (rs.next()) {
                Product p = extractProductFromResult(rs);
                collectionManager.addToCollection(p);
            }
            getStatement.close();
        } catch (SQLException throwables) {
            MainConsole.println("Ошибка при загрузке коллекции!");
        }
        return coll;
    }
    public synchronized Product extractProductFromResult(ResultSet rs) throws SQLException {
        Product prod = null;
        try {
            prod = new Product(
                    (long) rs.getInt("id"),
                    rs.getString("name"),
                    new Coordinates(rs.getFloat("coorX"), (double) rs.getFloat("coorY")),
                    (LocalDateTime) rs.getDate("time").toLocalDate().atStartOfDay(),
                    rs.getInt("price"),
                    rs.getString("partNumber"),
                    rs.getFloat("manufactureCost"),
                    UnitOfMeasure.valueOf(rs.getString("unitOfMeasure")),
                    new Organization(
                            rs.getInt("orId"),
                            rs.getString("orName"),
                            rs.getString("orFullName"),
                            (long) rs.getInt("annualTurnover"),
                            OrganizationType.valueOf(rs.getString("orType")),
                            new Address(
                                rs.getString("zipCode"),
                                new Location(
                                        rs.getFloat("locX"),
                                        rs.getFloat("locY"),
                                        rs.getString("locName")
                                )
                            )
                    )
            );
        } catch (CoordinatesException e) {
            e.printStackTrace();
        }
        return prod;
    }
    public synchronized boolean addProductToDb(Product p, String author) {
        try {
            PreparedStatement addStatement = dbConnection.prepareStatement(ADD_COLLECTION_ELEMENT);

            addStatement.setInt(1, p.getId().intValue());
            addStatement.setString(2, p.getName());
            addStatement.setFloat(3, p.getCoordinates().getX());
            addStatement.setFloat(4, (float) p.getCoordinates().getY().doubleValue());
            addStatement.setDate(5, java.sql.Date.valueOf(p.getCreationDate().toLocalDate()));
            addStatement.setInt(6, p.getPrice());
            addStatement.setString(7, p.getPartNumber());
            addStatement.setFloat(8, (float)p.getManufactureCost());
            addStatement.setString(9, p.getUnitOfMeasure().toString());
            addStatement.setInt(10, p.getManufacturer().getId());
            addStatement.setString(11, p.getManufacturer().getName());
            addStatement.setString(12, p.getManufacturer().getFullName());
            addStatement.setInt(13, p.getManufacturer().getAnnualTurnover().intValue());
            addStatement.setString(14, p.getManufacturer().getType().toString());
            addStatement.setString(15, p.getManufacturer().getPostalAddress().getZipCode());
            addStatement.setFloat(16, p.getManufacturer().getPostalAddress().getTown().getX());
            addStatement.setFloat(17, p.getManufacturer().getPostalAddress().getTown().getY());
            addStatement.setString(18, p.getManufacturer().getPostalAddress().getTown().getName());
            addStatement.setString(19, author);

            addStatement.executeUpdate();
            addStatement.close();
        } catch (SQLException throwables) {
            MainConsole.println("Ошибка добавления товара в БД!");
            throwables.printStackTrace();
            return false;
        }
        return true;
    }
    public synchronized boolean removeFromDb(int id, String author) {
        try {
            PreparedStatement checkStatement = dbConnection.prepareStatement(GET_COLLECTION);
            ResultSet rs = checkStatement.executeQuery();
            while (rs.next()) {
                int iterId = rs.getInt("id");
                String iterAuthor = rs.getString("author");
                if (iterId == id && iterAuthor.equals(author)) {
                    PreparedStatement removeStatement = dbConnection.prepareStatement(DELITE_ELEMENT);
                    removeStatement.setInt(1, id);
                    removeStatement.executeUpdate();
                    removeStatement.close();
                    return true;
                }
            }
            checkStatement.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }
}
