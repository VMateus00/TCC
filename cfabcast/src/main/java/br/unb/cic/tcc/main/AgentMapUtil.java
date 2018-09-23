package br.unb.cic.tcc.main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import static br.unb.cic.tcc.main.Initializer.ACCEPTORS;
import static br.unb.cic.tcc.main.Initializer.COORDINATOR;
import static br.unb.cic.tcc.main.Initializer.LEANERS;
import static br.unb.cic.tcc.main.Initializer.PROPOSERS;

public class AgentMapUtil {
    public static Map<String, Set<Integer>> createAgentsMap() throws IOException {
        String filePath = "src/config"+System.getProperty("file.separator")+"hosts.config";
        BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath));

        String actualLine = bufferedReader.readLine();
        HashMap<String, Set<Integer>> agentsMap = new HashMap<>();

        while (actualLine != null){
            if(actualLine.contains(COORDINATOR)){
                actualLine = addAgentId(bufferedReader, agentsMap, COORDINATOR);
            }else if(actualLine.contains(PROPOSERS)){
                actualLine = addAgentId(bufferedReader, agentsMap, PROPOSERS);
            }else if(actualLine.contains(ACCEPTORS)){
                actualLine = addAgentId(bufferedReader, agentsMap, ACCEPTORS);
            }else if (actualLine.contains(LEANERS)){
                actualLine = addAgentId(bufferedReader, agentsMap, LEANERS);
            }else {
                actualLine = bufferedReader.readLine();
            }
        }
        return agentsMap;
    }

    private static String addAgentId(BufferedReader bufferedReader, HashMap<String, Set<Integer>> agentsMap, String agentType) throws IOException {
        String actualLine = bufferedReader.readLine();
        while (actualLine != null && !actualLine.startsWith("#")){
            agentsMap.putIfAbsent(agentType, new HashSet<>());
            Set<Integer> integers = agentsMap.get(agentType);
            StringTokenizer stringTokenizer = new StringTokenizer(actualLine, " ");
            integers.add(Integer.valueOf(stringTokenizer.nextToken()));
            actualLine = bufferedReader.readLine();
        }
        return actualLine;
    }
}
