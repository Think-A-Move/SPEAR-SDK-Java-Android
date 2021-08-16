package com.tamrd.spearsdkexample.commandList;

import com.thinkamove.spearnative.speechType.commands.api.CommandList;
import com.thinkamove.spearnative.speechType.commands.model.CommandsFileData;

import java.util.HashMap;
import java.util.Map;

public class LabelCommandList extends CommandList {

    private static final String[] LABEL_COMMANDS = new String[] {
            "I.have.a.$pet",
            "($action1 light)|$action2",
            "My $pet weight 24.5 lb",
            "Her $vehicle values $integer dollars",
            "CLE stands for cleveland",
            DemoCommandList.STOP_SPEAR
    };

    @Override
    public String[] getCommandList() {
        return LABEL_COMMANDS;
    }

    /**
     * Provides a commands map where the key is an input command via voice and value is a command that SpearSdk
     * should return as a result in `onCommitResult(String)`.
     *
     * @return an empty HashMap or HashMap of commands to a resulting command.
     */
    @Override
    public Map<String, String> mapCommands() {
        return new HashMap<>();
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
        Map<String, String[]> labelMap = new HashMap<>();

        labelMap.put("pet", new String[] {"dog", "cat", "rabbit", "bird"});
        labelMap.put("vehicle", new String[] {"bicycle", "ship", "car", "plane"});
        labelMap.put("action1", new String[] {"turn on", "turn off"});
        labelMap.put("action2", new String[] {"volume up", "volume off"});

        return labelMap;
    }
}
