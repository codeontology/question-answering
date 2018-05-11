package org.codeontology.interpreter.ranking;


import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import org.codeontology.individuals.TypeIndividual;

import java.util.List;
import java.util.Set;

public class RankingCache {

    private static RankingCache instance = new RankingCache();

    private RankingCache() { }

    public static RankingCache getInstance() {
        return instance;
    }

    Table<String, List<Set<TypeIndividual>>, MethodRanking> table = HashBasedTable.create();


    public MethodRanking get(String command, List<Set<TypeIndividual>> types) {
        return table.get(command, types);
    }

    public void put(String command, List<Set<TypeIndividual>> types, MethodRanking ranking) {
        table.put(command, types, ranking);
    }

}
