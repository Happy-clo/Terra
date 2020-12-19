package com.dfsek.terra.api.structures.tokenizer.group;

public class Quotes implements Group {

    @Override
    public char getBegin() {
        return '"';
    }

    @Override
    public char getEnd() {
        return '"';
    }

    @Override
    public boolean ignoreInsideSyntax() {
        return true;
    }
}
