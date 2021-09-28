package Client;

import common.Exceptions.MustBeNotEmptyException;
import common.MainConsole;

import java.io.Console;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class LoginAsker {
    private Scanner userScanner;

    public LoginAsker(Scanner userScanner) {
        this.userScanner = userScanner;
    }

    public String askQuestion(String question) {
        String answer;
        while (true) {
            try {
                MainConsole.println(question);
                MainConsole.println("Введите +/-");
                answer = userScanner.nextLine().trim();
                if (!answer.equals("+") && !answer.equals("-")) throw new MustBeNotEmptyException();
                if(answer.equals("+")) {
                    return "login";
                } else {
                    return "register";
                }
            } catch (NoSuchElementException exception) {
                MainConsole.println("Ответ не распознан!");
            } catch (MustBeNotEmptyException exception) {
                MainConsole.println("Введите '+' или '-'!");
            } catch (IllegalStateException exception) {
                MainConsole.println("Непредвиденная ошибка!");
                System.exit(0);
            }
        }
    }


    public String askLogin() {
        String login;
        while (true) {
            try {
                MainConsole.println("Введите логин:");
                login = userScanner.nextLine().trim();
                if (login.equals("")) throw new MustBeNotEmptyException();
                break;
            } catch (NoSuchElementException exception) {
                MainConsole.println("Данного логина не существует!");
            } catch (MustBeNotEmptyException exception) {
                MainConsole.println("Имя не может быть пустым!");
            } catch (IllegalStateException exception) {
                MainConsole.println("Непредвиденная ошибка!");
                System.exit(0);
            }
        }
        return login;
    }


    public String askPassword() {
        String password;
        while (true) {
            try {
                MainConsole.println("Введите пароль:");
                Console console = System.console();
                if (console != null) {
                    char[] symbols = console.readPassword();
                    if (symbols == null) continue;
                    password = String.valueOf(symbols);
                } else {
                    password = userScanner.nextLine().trim();
                }

                if (password.equals("")) throw new MustBeNotEmptyException();
                break;
            } catch (NoSuchElementException exception) {
                MainConsole.println("Неверный логин или пароль!");
            } catch (IllegalStateException exception) {
                MainConsole.println("Непредвиденная ошибка!");
                System.exit(0);
            } catch (MustBeNotEmptyException exception) {
                MainConsole.println("Пароль не может быть пустым!");
            }
        }
        return password;
    }



}
