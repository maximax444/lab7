package Server.Commands;

import Server.Program.CollectionManager;
import Server.ResponseOut;
import Server.Server;
import Server.db.DbHandler;
import common.Exceptions.WrongNumberOfElementsException;
import common.User;

import java.sql.SQLException;


public class LoginCommand extends AbstractCommand{
    private CollectionManager collectionManager;
    public LoginCommand() {
        super("login", "авторизация");
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
                ResponseOut.println(user.getLogin() + ", Вы действительно существуете! Сейчас проверим ваш пароль!");
                if (dbHandler.checkUserPass(user.getLogin(), user.getPassword())) {
                    ResponseOut.println(user.getLogin() + ", Добро пожаловать!");
                    return true;
                } else {
                    ResponseOut.println("Пароль неверный!");
                    return false;
                }

            } else {
                ResponseOut.println("Такого пользователя не существует!");
                return false;
            }



        } catch (WrongNumberOfElementsException exception) {
            ResponseOut.println(exception.getMessage());
        } catch (SQLException exception) {
            ResponseOut.println("Ошибка БД");
        }

        return false;
    }


}
