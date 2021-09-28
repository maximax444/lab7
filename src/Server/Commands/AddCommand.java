package Server.Commands;


import Server.Program.CollectionManager;
import Server.ResponseOut;

import Server.Server;
import Server.db.DbHandler;
import common.Exceptions.WrongNumberOfElementsException;
import common.ProductSer;
import common.Task.Organization;
import common.Task.Product;
import common.User;

public class AddCommand extends AbstractCommand {

    private CollectionManager collectionManager;

    public AddCommand(CollectionManager collectionManager) {
        super("add", "добавить новый элемент в коллекцию");
        this.collectionManager = collectionManager;
    }


    /**
     * Execute command.
     * @return Command status.
     */
    @Override
    public boolean startExecute(String arg, Object o, User user) {
        try {
            if (!arg.isEmpty() || o == null) throw new WrongNumberOfElementsException("У данной команды не должно быть строкового аргумента, но должен быть передан объект!");
            ProductSer ps = (ProductSer) o;
            Product prod = new Product(
                    collectionManager.generateNextId(),
                    ps.getName(),
                    ps.getCoordinates(),
                    ps.getLDT(),
                    ps.getPrice(),
                    ps.getPartNumber(),
                    ps.getManufactureCost(),
                    ps.getUnitOfMeasure(),
                    new Organization(
                            (int) (long) collectionManager.generateNextId(),
                            ps.getOrgName(),
                            ps.getOrgFullName(),
                            ps.getAnnualTurnover(),
                            ps.getType(),
                            ps.getPostalAddress()
                    )
            );
            DbHandler dbHandler = Server.getDbHandler();

            if (dbHandler.addProductToDb(prod, user.getLogin())) {
                collectionManager.addToCollection(prod);
                ResponseOut.println("Продукт успешно добавлен в коллекцию!");
                return true;
            }

        } catch (WrongNumberOfElementsException exception) {
            ResponseOut.println(exception.getMessage());
        } catch (ClassCastException exception) {
            exception.printStackTrace();
        }
        return false;
    }
}
