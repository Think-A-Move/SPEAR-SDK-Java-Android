package com.tamrd.spearsdkexample.commandList;

import com.thinkamove.spearnative.speechType.commands.api.CommandList;
import com.thinkamove.spearnative.speechType.commands.model.CommandsFileData;

import java.util.HashMap;
import java.util.Map;

public class DemoCommandList extends CommandList {
    public static final String SWITCH_GRAMMAR = "SWITCH GRAMMAR";
    public static final String SWITCH_LABEL_GRAMMAR = "SWITCH LABEL GRAMMAR";
    public static final String STOP_SPEAR = "STOP SPEAR";

    private static final String KWA_BEK = "KWA BEK";
    private static final String KEI_BEK = "KEI BEK";

    private static final String[] DEMO_COMMANDS = new String[] {
            "ALPHA", "BRAVO", "CHARLIE", "DELTA", "ECHO", "FOXTROT", "GOLF", "HOTEL", "INDIA",
            "JULIET", "KILO", "LIMA", "MIKE", "NOVEMBER", "OSCAR", "PAPA", "QUEBEC", "ROMEO", "SIERRA",
            "TANGO", "UNIFORM", "VICTOR", "WHISKEY", "XRAY", "YANKEE", "ZULU",
            KWA_BEK,
            KEI_BEK,
            SWITCH_GRAMMAR,
            STOP_SPEAR,
            SWITCH_LABEL_GRAMMAR
    };

    /**
     * List of commands to be recognized when AlertsCommandList is active
     * @return String array of commands
     */
    @Override
    public String[] getCommandList() {
        return DEMO_COMMANDS;
    }

    /**
     * Provides a commands map where the key is an input command via voice and value is a command that SpearSdk
     * should return as a result in `onCommitResult(String)`.
     *
     * @return an empty HashMap or HashMap of commands to a resulting command.
     */
    @Override
    public Map<String, String> mapCommands() {
        HashMap<String, String> commandMap = new HashMap<>();

        commandMap.put(KWA_BEK, "QUEBEC");
        commandMap.put(KEI_BEK, "QUEBEC");

        return commandMap;
    }

    /**
     * Provides a file that contains list of commands
     * @return CommandsFileData
     */
    @Override
    public CommandsFileData getCommandsFileData() {
        return null;
    }

    /**
     * Provides label map where the key is a label and value is an array of commands assigned to the label.
     * @return A map of labels and assigned commands.
     */
    @Override
    public Map<String, String[]> getGrammarLabels() {
        return new HashMap<>();
    }
}
