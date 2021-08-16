package com.tamrd.spearsdkexample.helper;

import com.thinkamove.spearnative.speechType.commands.api.CommandList;

import java.util.Arrays;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UIHelper {
    public static String getLabelCommands(CommandList labelCommandList) {
        Pattern pattern = Pattern.compile("\\$[A-Za-z][0-9A-Za-z'\\-_]*");
        Map<String, String[]> labelMap = labelCommandList.getGrammarLabels();
        StringBuilder stringBuilder = new StringBuilder();
        for (String command : labelCommandList.getCommandList()) {
            stringBuilder.append("\n");
            String resultCommand = command;
            Matcher matcher = pattern.matcher(command);
            while (matcher.find()) {
                String label = matcher.group();
                switch (label) {
                    case "$integer":
                        resultCommand = resultCommand.replace(label, "[any integer number]");
                        break;
                    case "$real":
                        resultCommand = resultCommand.replace(label, "[any real number]");
                        break;
                    case "$digit":
                        resultCommand = resultCommand.replace(label, "[any digit number]");
                        break;
                    default:
                        String[] associatedCommands = labelMap.get(label.substring(1));
                        resultCommand = resultCommand.replace(label, Arrays.toString(associatedCommands));
                        break;
                }
            }

            stringBuilder.append(resultCommand);
            stringBuilder.append(",");
        }

        return stringBuilder.toString().substring(1, stringBuilder.length() - 1);
    }
}
