package Client;

import common.Request;
import common.User;

import java.util.Scanner;

public class LoginCreater {
    private Scanner userScanner;
    public LoginCreater (Scanner userScanner) {
        this.userScanner = userScanner;
    }
    public Request loginToSend() {
        LoginAsker loginAsker = new LoginAsker(userScanner);
        return new Request(loginAsker.askQuestion("Вы уже зарегистрированы?"), "", null, new User(loginAsker.askLogin(), loginAsker.askPassword()));
    }
}
