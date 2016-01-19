/*
 * SonarQube Java
 * Copyright (C) 2012-2016 SonarSource SA
 * mailto:contact AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.java.xml;

import com.google.common.annotations.Beta;
import org.sonar.plugins.java.api.JavaCheck;
import org.w3c.dom.Node;

import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;

import java.io.File;

@Beta
public interface XmlCheckContext {

  File getFile();

  XPathExpression compile(String expression) throws XPathExpressionException;

  Iterable<Node> evaluate(XPathExpression expression, Node node) throws XPathExpressionException;

  Iterable<Node> evaluateOnDocument(XPathExpression expression) throws XPathExpressionException;

  void reportIssueOnFile(JavaCheck check, String message);

  void reportIssue(JavaCheck check, int line, String message);

  void reportIssue(JavaCheck check, Node node, String message);
}
