package yapper.commands;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Set;

import yapper.exceptions.YapperException;

/**
 * A class to contain all default available commands for the Yapper application.
 */
public class CommandList {
    private static final String DUNNO = "...";
    private static final HashSet<String> unaliasableCommands = new HashSet<>(
            Set.of("bind", "unbind", "reset", "help", "clear")
    );
    private LinkedHashMap<String, Command> availableCommands;
    private HashSet<String> newCommandAliases;

    /**
     * Constructor for CommandList class.
     */
    public CommandList() {
        this.availableCommands = new LinkedHashMap<>();
        this.newCommandAliases = new HashSet<>();
        initialiseAvailableCommands();
    }

    /**
     * Sets the list of available commands that can be used by the user.
     */
    public void initialiseAvailableCommands() {
        // Basic commands
        addCommandWithAliases("help", new HelpCommand(this), "?");
        addCommandWithAliases("bind", new BindAliasCommand(this));
        addCommandWithAliases("unbind", new UnbindAliasCommand(this));
        addCommandWithAliases("reset", new ResetAliasCommand(this));
        // List tasks command
        addCommandWithAliases("list", new ListOutTasksCommand(), "ls");
        // Add task commands
        addCommandWithAliases("todo", new AddToDoCommand(), "t", "T");
        addCommandWithAliases("deadline", new AddDeadlineCommand(), "d", "D");
        addCommandWithAliases("event", new AddEventCommand(), "e", "E");
        // Mark/unmark task commands
        addCommandWithAliases("mark", new MarkCommand(), "m");
        addCommandWithAliases("unmark", new UnmarkCommand(), "um");
        // Find command
        addCommandWithAliases("find", new FindTaskCommand());
        // Delete task command
        addCommandWithAliases("delete", new DeleteTaskCommand(), "del");
        addCommandWithAliases("clear", new ClearTaskListCommand());
        // Termination command
        addCommandWithAliases("bye", new TerminationCommand(), "exit", "quit");
    }

    /**
     * Adds default aliases to commands
     */
    public void addCommandWithAliases(String commandName, Command command, String... aliases) {
        this.availableCommands.put(commandName, command);
        for (String alias : aliases) {
            this.availableCommands.put(alias, command);
        }
    }

    /**
     * Creates new aliases for existing commands
     *
     * @param commandToAlias   an existing command name
     * @param alias            the alias the user wants to use to call their specified command
     * @throws YapperException if the command name does not exist or the alias is already in use
     */
    public String bindCustomCommandAlias(String alias, String commandToAlias) throws YapperException {
        if (unaliasableCommands.contains(commandToAlias)) {
            throw new YapperException("I don't recommend doing that.");
        }
        if (!this.availableCommands.containsKey(commandToAlias)) {
            throw new YapperException("No such command exists.");
        }
        if (this.availableCommands.containsKey(alias)) {
            throw new YapperException(String.format("That one's already in use: %s mapped to %s",
                    alias,
                    getCommandToExecute(alias).toString()));
        }
        this.availableCommands.put(alias, this.availableCommands.get(commandToAlias));
        this.newCommandAliases.add(alias);
        return String.format("%s now bound to %s", alias, getCommandToExecute(commandToAlias).toString());
    }

    /**
     * Allow users to unbind an existing alias of a command they had set
     * @param commandName      the alias of the command they had set
     * @return                 a string that reflects the changes
     * @throws YapperException if that command should not be unbound or it does not exist
     */
    public String unbindCustomCommandAlias(String commandName) throws YapperException {
        if (unaliasableCommands.contains(commandName)) {
            throw new YapperException("I REALLY don't recommend doing that.");
        }
        if (!this.newCommandAliases.contains(commandName)) {
            throw new YapperException("That command either doesn't exist or is built-in by default.");
        }
        Command command = this.availableCommands.get(commandName);
        this.newCommandAliases.remove(commandName);
        this.availableCommands.remove(commandName);
        return String.format("%s now unbound from %s.", commandName, command.toString());
    }

    /**
     * Resets command list to default mappings
     */
    public String resetToDefault() {
        this.availableCommands.clear();
        initialiseAvailableCommands();
        return String.format("No more aliases! Command list reset to default mappings.");
    }

    /**
     * Retrieves the corresponding command relating to the user's input.
     *
     * @param commandName      a string representing the name of the command
     * @return                 a command from the hashmap value set
     * @throws YapperException if the command is not found within the hashmap
     */
    public Command getCommandToExecute(String commandName) throws YapperException {
        Command commandToExecute = availableCommands.get(commandName);
        if (commandToExecute == null) {
            throw new YapperException(DUNNO);
        }
        return commandToExecute;
    }

    /**
     * Returns the list of available commands in the command list
     * @return an array of command names
     */
    public String[] getAvailableCommands() {
        return availableCommands.keySet().toArray(new String[0]);
    }
}
