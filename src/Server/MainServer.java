package Server;
import Server.db.DbHandler;
import common.MainConsole;
import common.Request;
import common.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import Server.Commands.*;
import Client.AskManager;
import Server.Program.CollectionManager;
import Server.Program.CommandManager;
import Server.Program.Console;
import Server.Program.FileManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Scanner;

public class MainServer {

    public static Logger logger = LogManager.getLogger("ServerLogger");
    public static int port = 3578;
    public static final int CONNECTION_TIMEOUT = 60 * 1000;
    private static String dbUser = "s311695";
    private static String dbHost = "pg:5432";
    private static String dbPassword;
    private static String dbAddress = "jdbc:postgresql://" + dbHost + "/studs";


    public static void main(String[] args) throws InterruptedException {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        if (args.length != 2) {
            System.out.println("Должены быть передаты порт и пароль БД");
            System.exit(0);
        }
        port = Integer.parseInt(args[0]);
        dbPassword = args[1];


        Scanner scanner = new Scanner(System.in);

        AskManager askManager = new AskManager(scanner);
        Console console = new Console(scanner, askManager);






        CollectionManager collectionManager = new CollectionManager();
        CommandManager commandManager = new CommandManager(
                new HelpCommand(),
                new LoginCommand(),
                new RegisterCommand(),
                new InfoCommand(collectionManager),
                new ShowCommand(collectionManager),
                new AddCommand(collectionManager),

                new UpdateCommand(collectionManager),
                new RemoveByIdCommand(collectionManager),
                new ClearCommand(collectionManager),
                new ExecuteScriptCommand(console),
                new ExitCommand(),
                /*new SaveCommand(collectionManager),*/
                new RemoveGreaterCommand(collectionManager),
                new RemoveLowerCommand(collectionManager),
                new HistoryCommand(),
                new MinByManufacturerCommand(collectionManager),
                new CountByPriceCommand(collectionManager),
                new FilterStartsWithNameCommand(collectionManager)
        );
        console.setCommandManager(commandManager);




        ServerConsole serverConsole = new ServerConsole(scanner);
        RequestIn requestIn = new RequestIn(commandManager);
        Thread threadForReceiveFromTerminal = new Thread() {
            @Override
            public void run() {
                while (true) {
                    User user = new User("admin", "1234");
                    Request req = serverConsole.handle(1, user);
                    MainConsole.println(requestIn.handle(req).getResponseBody());
                }

            }
        };
        Thread startReceiveFromServerTerminal = new Thread(threadForReceiveFromTerminal);
        startReceiveFromServerTerminal.start();

        DbHandler dbHandler = new DbHandler(dbAddress, dbUser, dbPassword, collectionManager);


        Server server = new Server(port, CONNECTION_TIMEOUT, requestIn, dbHandler);
        collectionManager.addDbHandlerToCM();
        dbHandler.connectionToDb();
        collectionManager.loadCollection();
        server.run();
    }
}
