package Server.Commands;

import Server.Server;
import Server.ResponseOut;
import Server.db.DbHandler;
import common.Exceptions.WrongNumberOfElementsException;
import common.User;

import java.sql.SQLException;

public class RegisterCommand extends AbstractCommand{
    public RegisterCommand() {
        super("register", "регистрация");
    }

    /**
     * Start execute command.
     * @return Command status.
     */
    @Override
    public boolean startExecute(String arg, Object o, User user) {
        try {
            if (!arg.isEmpty()) throw new WrongNumberOfElementsException("Команда: '" + getName() + "' должна использоваться без аргументов");

            DbHandler dbHandler = Server.getDbHandler();

            if (dbHandler.checkUser(user.getLogin())) {
                ResponseOut.println("Аккаунт с таким именем уже существует, попробуйте другое!");
                return false;

            } else {
                dbHandler.registerUser(user.getLogin(), user.getPassword());
                ResponseOut.println("Регистрация прошла успешно!");
                return true;

            }



        } catch (WrongNumberOfElementsException exception) {
            ResponseOut.println(exception.getMessage());
        } catch (SQLException exception) {
            ResponseOut.println("Ошибка БД");
        }

        return false;
    }
}
