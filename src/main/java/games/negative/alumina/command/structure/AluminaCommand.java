/*
 *  MIT License
 *
 * Copyright (C) 2023 Negative Games
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package games.negative.alumina.command.structure;

import com.google.common.base.Preconditions;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import games.negative.alumina.command.Command;
import games.negative.alumina.command.Context;
import games.negative.alumina.command.TabContext;
import games.negative.alumina.command.builder.CommandBuilder;
import games.negative.alumina.message.Message;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Represents the implementation of any alumina command.
 */
public class AluminaCommand extends org.bukkit.command.Command {

    /*
     * The message for when the player does not have permission to use the command.
     */
    private static final Message NO_PERMISSION = Message.of("&cYou do not have permission to use this command.");

    /*
     * The message for when a player-only command is used as a console.
     */
    private static final Message CANNOT_USE_AS_CONSOLE = Message.of("&cYou cannot use this command as console.");

    /*
     * The message for when a command is used incorrectly.
     */
    private static final Message USAGE = Message.of("&cUsage: &7/%command% %usage%");


    /*
     * The command executor component.
     */
    private final Command component;

    /*
     * The sub commands of this command.
     */
    private final List<AluminaCommand> subCommands;

    /*
     * The permissions of this command.
     */
    private final Permission[] permissions;

    /*
     * The required parameters of this command.
     */
    private final String[] params;

    /**
     * The shortcuts of this command.
     */
    private final String[] shortcuts;

    /*
     * Whether this command is player-only.
     */
    private final boolean playerOnly;

    /**
     * Whether this command should use smart tab completion.
     */
    private final boolean smartTabComplete;

    /*
     * The parent command of this command.
     */
    private final AluminaCommand parent;

    /**
     * Creates a new alumina command.
     *
     * @param parent  The parent command.
     * @param builder The command builder.
     */
    public AluminaCommand(@Nullable final AluminaCommand parent, @NotNull final CommandBuilder builder) {
        super(builder.getName(), builder.getDescription(), builder.getUsage(), builder.getAliases());

        Preconditions.checkNotNull(builder, "Command builder cannot be null.");

        this.parent = parent;

        Preconditions.checkNotNull(builder.getComponent(), "Command component cannot be null.");

        this.component = builder.getComponent();
        this.subCommands = Lists.newArrayList();
        this.playerOnly = builder.isPlayerOnly();
        this.permissions = builder.getPermissions();
        this.params = builder.getParams();
        this.shortcuts = builder.getShortcuts();
        this.smartTabComplete = builder.isSmartTabComplete();

        if (builder.getUsage() != null)
            this.setUsage(builder.getUsage());

        for (CommandBuilder cmd : builder.getSubCommands()) {
            AluminaCommand subCommand = new AluminaCommand(this, cmd);
            this.subCommands.add(subCommand);
        }
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (checkConsolePlayerCommand(sender) || !checkPermissions(sender, true) || !checkParams(sender, args) || checkSubCommands(sender, args))
            return true;

        Context context = new Context(args, sender);
        component.execute(context);
        return true;
    }

    @NotNull
    @Override
    public List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException {
        TabContext context = new TabContext(sender, args);
        List<String> completions = component.onTabComplete(context);
        if (completions != null) return completions;

        if (!smartTabComplete) return super.tabComplete(sender, alias, args);

        String current = context.current();
        int placement = args.length - 1;

        List<String> result = Lists.newArrayList();

        Multimap<Integer, AluminaCommand> subMap = getRecursive(this, 0);
        if (subMap.isEmpty()) return super.tabComplete(sender, alias, args);

        Collection<AluminaCommand> commands = subMap.get(placement);
        for (AluminaCommand command : commands) {
            if (!checkPermissions(sender, false)) continue;
            
            List<String> match = Lists.newArrayList(command.getName());
            match.addAll(command.getAliases());

            List<String> matched = match.stream().filter(entry -> entry.toLowerCase().contains(current)).toList();
            result.addAll(matched);
        }

        return result;
    }

    private Multimap<Integer, AluminaCommand> getRecursive(@NotNull final AluminaCommand parent, final int depth) {
        Preconditions.checkNotNull(parent, "Parent command cannot be null.");

        Multimap<Integer, AluminaCommand> map = ArrayListMultimap.create();

        List<AluminaCommand> list = parent.getSubCommands();
        for (AluminaCommand cmd : list) {
            int next = depth + 1;
            map.put(depth, cmd);

            Multimap<Integer, AluminaCommand> recursive = getRecursive(cmd, next);
            if (recursive.isEmpty()) continue;

            map.putAll(recursive);
        }

        return map;
    }


    /**
     * Checks if the console is using a player-only command
     * @param sender The sender
     * @return Whether the console is using a player-only command
     */
    private boolean checkConsolePlayerCommand(@NotNull final CommandSender sender) {
        Preconditions.checkNotNull(sender, "Sender cannot be null.");

        if (!(sender instanceof Player) && playerOnly) {
            CANNOT_USE_AS_CONSOLE.send(sender);
            return true;
        }

        return false;
    }

    private boolean checkPermissions(@NotNull final CommandSender sender, final boolean message) {
        Preconditions.checkNotNull(sender, "Sender cannot be null.");

        if (this.permissions == null)
            return true;

        for (Permission permission : this.permissions) {
            if (sender.hasPermission(permission))
                return true;
        }

        if (message) NO_PERMISSION.send(sender);
        return false;
    }

    private boolean checkSubCommands(@NotNull final CommandSender sender, @NotNull final String[] args) {
        Preconditions.checkNotNull(sender, "Sender cannot be null.");
        Preconditions.checkNotNull(args, "Arguments cannot be null.");

        if (args.length == 0 || subCommands.isEmpty())
            return false;

        String begin = args[0];
        String[] snippet = Arrays.copyOfRange(args, 1, args.length);

        AluminaCommand subCommand = getAvailableSubCommand(begin);
        return (subCommand != null && subCommand.execute(sender, begin, snippet));
    }

    public AluminaCommand getAvailableSubCommand(@NotNull final String argument) {
        Preconditions.checkNotNull(argument, "Argument cannot be null.");

        for (AluminaCommand subCommand : subCommands) {
            if (subCommand.getName().equalsIgnoreCase(argument) || subCommand.getAliases().contains(argument.toLowerCase()))
                return subCommand;
        }
        return null;
    }

    public boolean checkParams(@NotNull final CommandSender sender, @NotNull final String[] args) {
        Preconditions.checkNotNull(sender, "Sender cannot be null.");
        Preconditions.checkNotNull(args, "Arguments cannot be null.");

        if (this.params == null)
            return true;

        if (args.length < params.length) {
            StringBuilder builder = new StringBuilder();
            for (String param : params) {
                builder.append("<").append(param).append(">").append(" ");
            }

            List<String> parentNames = Lists.newArrayList();
            parentNames.add(getName());

            AluminaCommand search = this;
            while (search.getParent() != null) {
                parentNames.add(search.getParent().getName());
                search = search.getParent();
            }

            Collections.reverse(parentNames);

            StringBuilder parentBuilder = new StringBuilder();

            int iteration = 0;
            for (String parentName : parentNames) {
                if (iteration != 0)
                    parentBuilder.append(" ");

                parentBuilder.append(parentName);

                iteration++;
            }

            USAGE.replace("%command%", parentBuilder.toString()).replace("%usage%", builder.toString()).send(sender);
            return false;
        }

        return true;
    }

    public Command getComponent() {
        return component;
    }

    public List<AluminaCommand> getSubCommands() {
        return subCommands;
    }

    public String[] getParams() {
        return params;
    }

    public Permission[] getPermissions() {
        return permissions;
    }

    public boolean isPlayerOnly() {
        return playerOnly;
    }

    public void addSubCommand(@NotNull AluminaCommand command) {
        this.subCommands.add(command);
    }

    public AluminaCommand getParent() {
        return parent;
    }

    public String[] getShortcuts() {
        return shortcuts;
    }
}
