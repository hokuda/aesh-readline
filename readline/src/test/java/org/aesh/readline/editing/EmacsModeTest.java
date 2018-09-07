/*
 * JBoss, Home of Professional Open Source
 * Copyright 2014 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.aesh.readline.editing;

import org.aesh.readline.terminal.Key;
import org.aesh.readline.tty.terminal.TestConnection;
import org.junit.Test;

/**
 * @author <a href="mailto:stale.pedersen@jboss.org">Ståle W. Pedersen</a>
 */
public class EmacsModeTest {

    @Test
    public void testSimpleMovementAndEdit() {
        TestConnection term = new TestConnection();
        term.setSignalHandler(null);
        term.read("1234");
        term.read(Key.CTRL_D);
        term.assertBuffer("1234");
        term.read(Key.LEFT_2);
        term.read(Key.CTRL_D);
        term.read('5');
        term.assertBuffer("1235");
        term.read(Key.CTRL_A);
        term.read(Key.CTRL_D);
        term.assertBuffer("235");
        term.read(Key.CTRL_F);
        term.read(Key.CTRL_F);
        term.read('6');
        term.assertBuffer("2365");
    }

    @Test
    public void testWordMovementAndEdit() throws Exception {
        TestConnection term = new TestConnection();
        term.read("foo  bar...  Foo-Bar.");
        term.read(Key.META_b);
        term.read(Key.META_d);
        term.assertBuffer("foo  bar...  Foo-.");
        term.read(Key.CTRL_E);
        term.read(Key.LEFT_2);
        term.read("Bar");
        term.assertBuffer("foo  bar...  Foo-Bar.");
        term.read(Key.CTRL_A);
        term.read(Key.META_f);
        term.read(Key.META_f);
        term.read(Key.META_d);
        term.assertBuffer("foo  bar-Bar.");
        term.read(Key.META_b);
        term.read(Key.CTRL_U);
        term.assertBuffer("bar-Bar.");
    }

    @Test
    public void testWordMovementWithEndAndHome() throws Exception {
        TestConnection term = new TestConnection();
        term.read("o  ba");
        term.read(Key.HOME);
        term.read("o");
        term.read(Key.END);
        term.read("r");
        term.assertBuffer("oo  bar");
        term.read(Key.CTRL_A);
        term.read("f");
        term.read(Key.CTRL_E);
        term.read(".");
        term.assertBuffer("foo  bar.");
    }

    @Test
    public void testSwitchingEditModes() throws Exception {
        TestConnection term = new TestConnection();
        term.read("foo  bar...  Foo-Bar.");
        term.read(Key.CTRL_A);
        term.read("A ");
        term.assertBuffer("A foo  bar...  Foo-Bar.");
        term.read(Key.CTRL_E);
        term.read(".");
        term.assertBuffer("A foo  bar...  Foo-Bar..");
        term.read(Key.META_CTRL_J);
        term.read(Key.ESC);
        term.read(Key.ZERO);
        term.read(Key.i);
        term.read("A ");
        term.assertBuffer("A A foo  bar...  Foo-Bar..");
        term.read(Key.ESC);
        term.read(Key.CTRL_E);
        term.read(Key.CTRL_E);
        term.read(".");
        term.assertBuffer("A A foo  bar...  Foo-Bar...");
    }

    @Test
    public void testWordMovementWithArrows() {
        TestConnection term = new TestConnection();
        term.read("/subsyste=fo/ba:add(pro=abc)");
        term.read(Key.CTRL_LEFT);
        term.read(Key.LEFT_2);
        term.read("p");
        term.assertBuffer("/subsyste=fo/ba:add(prop=abc)");
        term.read(Key.META_b);
        term.read(Key.META_b);
        term.read(Key.LEFT_2);
        term.read("r");
        term.assertBuffer("/subsyste=fo/bar:add(prop=abc)");
        term.read(Key.HOME);
        term.read(Key.CTRL_RIGHT);
        term.read("m");
        term.assertBuffer("/subsystem=fo/bar:add(prop=abc)");
        term.read(Key.META_f);
        term.read("o");
        term.assertBuffer("/subsystem=foo/bar:add(prop=abc)");
    }

    @Test
    public void testCharMovementWithArrows() {
        TestConnection term = new TestConnection();
        term.read("foobarfoobarfoo");
        term.read(Key.LEFT_2);
        term.read(Key.LEFT_2);
        term.read(Key.LEFT_2);
        term.read("-");
        term.assertBuffer("foobarfoobar-foo");
        term.read(Key.CTRL_B);
        term.read(Key.CTRL_B);
        term.read(Key.CTRL_B);
        term.read(Key.CTRL_B);
        term.read("-");
        term.assertBuffer("foobarfoo-bar-foo");
        term.read(Key.HOME);
        term.read(Key.RIGHT);
        term.read(Key.RIGHT);
        term.read(Key.RIGHT);
        term.read("-");
        term.assertBuffer("foo-barfoo-bar-foo");
        term.read(Key.CTRL_F);
        term.read(Key.CTRL_F);
        term.read(Key.CTRL_F);
        term.read("-");
        term.assertBuffer("foo-bar-foo-bar-foo");
    }
}
