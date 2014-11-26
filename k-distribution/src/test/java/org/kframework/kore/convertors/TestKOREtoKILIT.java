// Copyright (c) 2014 K Team. All Rights Reserved.

package org.kframework.kore.convertors;

import org.junit.Test;

import static org.junit.Assert.*;

import org.kframework.kil.Sources;
import org.kframework.kil.visitors.BasicVisitor;
import org.kframework.kore.outer.Definition;
import org.kframework.parser.outer.Outer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TestKOREtoKILIT extends BaseTest {

    static final String ROOT = "src/test/resources/reverse-convertor-tests/";
    static final String TO_KORE_ROOT = "src/test/resources/convertor-tests/";

    @Test
    public void testConfiguration() throws IOException {
        org.kframework.kil.Definition kilDef = parse(
                new File(ROOT + "configuration.k").getAbsoluteFile(), "TEST");

        KILtoKORE toKore = new KILtoKORE();
        org.kframework.kore.outer.Definition koreDef = toKore.apply(kilDef);

        KOREtoKIL toKil = new KOREtoKIL();
        org.kframework.kil.Definition kilDef1 = toKil.convertDefinition(koreDef);

        final int[] configurations = {0};
        BasicVisitor configurationCounter = new BasicVisitor(null) {
            @Override
            public Void visit(org.kframework.kil.Configuration conf, Void _void) {
                configurations[0]++;
                return _void;
            }
        };
        configurationCounter.visitNode(kilDef1);

        final int[] imports = {0};
        BasicVisitor importCounter = new BasicVisitor(null) {
            @Override
            public Void visit(org.kframework.kil.Import _import, Void _void) {
                imports[0]++;
                return _void;
            }
        };
        importCounter.visitNode(kilDef1);

        assertEquals(configurations[0], 1);
        assertEquals(imports[0], 1);
    }

    @Test
    public void testSimpleRule() throws IOException {
        org.kframework.kil.Definition kilDef = parse(
                new File(ROOT + "simpleRule.k").getAbsoluteFile(), "TEST");

        KILtoKORE toKore = new KILtoKORE();
        org.kframework.kore.outer.Definition koreDef = toKore.apply(kilDef);

        KOREtoKIL toKil = new KOREtoKIL();
        org.kframework.kil.Definition kilDef1 = toKil.convertDefinition(koreDef);

        assertEquals(countRules(kilDef1), 1);
    }

    @Test
    public void testRules() throws IOException {
        org.kframework.kil.Definition kilDef = parse(
                new File(ROOT + "syntaxWithOR.k").getAbsoluteFile(), "TEST");

        KILtoKORE toKore = new KILtoKORE();
        org.kframework.kore.outer.Definition koreDef = toKore.apply(kilDef);

        KOREtoKIL toKil = new KOREtoKIL();
        org.kframework.kil.Definition kilDef1 = toKil.convertDefinition(koreDef);

        final int[] syntaxct = {0};
        BasicVisitor syntaxCounter = new BasicVisitor(null) {
            @Override
            public Void visit(org.kframework.kil.Syntax syntax, Void _void) {
                syntaxct[0]++;
                return _void;
            }
        };
        syntaxCounter.visitNode(kilDef1);
        assertEquals(2, syntaxct[0]);
    }

    @Test
    public void testSimpleSyntax() throws IOException {
        org.kframework.kil.Definition kilDef = parse(
                new File(ROOT + "simpleSyntax.k").getAbsoluteFile(), "TEST");

        KILtoKORE toKore = new KILtoKORE();
        org.kframework.kore.outer.Definition koreDef = toKore.apply(kilDef);

        KOREtoKIL toKil = new KOREtoKIL();
        org.kframework.kil.Definition kilDef1 = toKil.convertDefinition(koreDef);

        final int[] syntaxct = {0};
        BasicVisitor syntaxCounter = new BasicVisitor(null) {
            @Override
            public Void visit(org.kframework.kil.Syntax syntax, Void _void) {
                syntaxct[0]++;
                return _void;
            }
        };
        syntaxCounter.visitNode(kilDef1);

        assertEquals(1, syntaxct[0]);
    }

    @Test
    public void testBubble() {
        String pgm = "module PGM " +
                "configuration <k> .K </k> " +
                "endmodule";
        org.kframework.kil.Definition kilDef = parseAndTranslateBack(pgm);

        List<String> sentences = new ArrayList<>();
        BasicVisitor variableCounter = new BasicVisitor(null) {
            @Override
            public Void visit(org.kframework.kil.StringSentence string, Void _void) {
                sentences.add(string.getContent());
                return _void;
            }
        };
        variableCounter.visitNode(kilDef);
        assertEquals(sentences.size(), 1);
        assertEquals(sentences.get(0), "<k> .K </k>");
    }

    @Test
    public void testListConversion() {
        String pgm = "module PGM " +
                "syntax UserLst ::= List{Elem, \",\"} " +
                "syntax NotYourUsualList ::= UserLst " +
                "syntax AnotherList ::= List{Elem2, \"!\"} " +
                "endmodule";
        org.kframework.kil.Definition kilDef = parseAndTranslateBack(pgm);

        final int[] lists = {0};
        BasicVisitor variableCounter = new BasicVisitor(null) {
            @Override
            public Void visit(org.kframework.kil.UserList lst, Void _void) {
                lists[0]++;
                return _void;
            }
        };
        variableCounter.visitNode(kilDef);
        assertEquals(lists[0], 2);
    }

    @Test
    public void contextTest() throws IOException {
        org.kframework.kil.Definition kilDef = kilToKoreTest("context.k");

        final int[] contexts = {0};
        BasicVisitor contextCounter = new BasicVisitor(null) {
            @Override
            public Void visit(org.kframework.kil.Context ctx, Void _void) {
                contexts[0]++;
                return _void;
            }
        };
        contextCounter.visitNode(kilDef);
        assertEquals(contexts[0], 2);
    }

    public org.kframework.kil.Definition kilToKoreTest(String fileName) throws IOException {
        Definition kilDef = new KILtoKORE().apply(
                parse(new File(TO_KORE_ROOT + fileName).getAbsoluteFile(), "TEST"));
        KOREtoKIL toKil = new KOREtoKIL();
        return toKil.convertDefinition(kilDef);
    }

    public org.kframework.kil.Definition parseAndTranslateBack(String pgm) {
        org.kframework.kil.Definition kilDef = new org.kframework.kil.Definition();
        kilDef.setItems(Outer.parse(Sources.generatedBy(TestKOREtoKILIT.class), pgm, null));

        KILtoKORE toKore = new KILtoKORE();
        Definition koreDef = toKore.apply(kilDef);
        KOREtoKIL toKil = new KOREtoKIL();
        return toKil.convertDefinition(koreDef);
    }

    public int countRules(org.kframework.kil.Definition kilDef) {
        final int[] rules = {0};
        BasicVisitor ruleCounter = new BasicVisitor(null) {
            @Override
            public Void visit(org.kframework.kil.Rule conf, Void _void) {
                rules[0]++;
                return _void;
            }
        };
        ruleCounter.visitNode(kilDef);
        return rules[0];
    }
}