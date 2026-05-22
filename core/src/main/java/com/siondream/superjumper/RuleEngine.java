package com.siondream.superjumper;

import java.util.ArrayList;
import java.util.List;

/**
 * Motor de reglas - evalúa y aplica reglas según prioridad
 */
public class RuleEngine {
    private final List<Rule> rules;

    public RuleEngine() {
        this.rules = new ArrayList<>();
    }

    public void addRule(Rule rule) {
        rules.add(rule);
        rules.sort((a, b) -> Float.compare(b.getPriority(), a.getPriority()));
    }

    public List<Rule> evaluateRules(GenerationContext context) {
        List<Rule> matchedRules = new ArrayList<>();
        for (Rule rule : rules) {
            if (rule.evaluate(context)) {
                matchedRules.add(rule);
            }
        }
        return matchedRules;
    }
}
