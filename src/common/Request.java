package common;


import java.io.Serializable;

public class Request implements Serializable{
    private String commandName;
    private String commandArgument;
    private Serializable commandObject;
    private User user;

    public Request(String commandName, String commandArgument, Serializable commandObject) {
        this.commandName = commandName;
        this.commandArgument = commandArgument;
        this.commandObject = commandObject;
    }
    public Request(String commandName, String commandArgument, Serializable commandObject, User user) {
        this.commandName = commandName;
        this.commandArgument = commandArgument;
        this.commandObject = commandObject;
        this.user = user;
    }

    public Request(String commandName, String commandArgument) {
        this.commandName = commandName;
        this.commandArgument = commandArgument;
        this.commandObject = null;
    }



    /**
     * @return Command name.
     */
    public String getCommandName() {
        return commandName;
    }

    /**
     * @return Command string argument.
     */
    public String getCommandArgument() {
        return commandArgument;
    }

    /**
     * @return Command object argument.
     */
    public Object getCommandObject() {
        return commandObject;
    }
    /**
     * @return Command name.
     */
    public User getUser() {
        return user;
    }
    /**
     * @return Is this request empty.
     */
    public boolean isEmpty() {
        return commandName.isEmpty() && commandArgument.isEmpty() && commandObject == null;
    }

    @Override
    public String toString() {
        return "Request[" + commandName + ", " + commandArgument + ", " + commandObject + ", " + user + "]";
    }
}