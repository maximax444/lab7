package Server.Commands;

import common.User;

public interface Command {
    String getName();
    String getDescr();
    boolean startExecute(String arg, Object o, User user);
}

