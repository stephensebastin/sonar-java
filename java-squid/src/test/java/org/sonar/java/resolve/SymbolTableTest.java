/*
 * Sonar Java
 * Copyright (C) 2012 SonarSource
 * dev@sonar.codehaus.org
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
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonar.java.resolve;

import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

public class SymbolTableTest {

  @Test
  public void ClassDeclaration() {
    Result result = Result.createFor("declarations/ClassDeclaration");

    Symbol.TypeSymbol typeSymbol = (Symbol.TypeSymbol) result.symbol("Declaration");
    assertThat(typeSymbol.owner()).isSameAs(result.symbol("ClassDeclaration"));
    assertThat(typeSymbol.flags()).isEqualTo(Flags.PRIVATE);
    assertThat(typeSymbol.getSuperclass()).isSameAs(result.symbol("Superclass"));
    assertThat(typeSymbol.getInterfaces()).containsExactly(
        result.symbol("FirstInterface"),
        result.symbol("SecondInterface"));

    typeSymbol = (Symbol.TypeSymbol) result.symbol("Superclass");
    assertThat(typeSymbol.getSuperclass()).isNull(); // FIXME should be java.lang.Object
    assertThat(typeSymbol.getInterfaces()).isEmpty();;
  }

  @Test
  public void AnonymousClassDeclaration() {
    Result result = Result.createFor("declarations/AnonymousClassDeclaration");

    Symbol.TypeSymbol typeSymbol = (Symbol.TypeSymbol) result.symbol("methodInAnonymousClass").owner();
    assertThat(typeSymbol.owner()).isSameAs(result.symbol("method"));
    assertThat(typeSymbol.flags()).isEqualTo(0);
    assertThat(typeSymbol.name).isEqualTo("");
    assertThat(typeSymbol.getSuperclass()).isNull(); // FIXME should be result.symbol("Superclass")
    assertThat(typeSymbol.getInterfaces()).isEmpty();
  }

  @Test
  public void LocalClassDeclaration() {
    Result result = Result.createFor("declarations/LocalClassDeclaration");

    Symbol.TypeSymbol typeSymbol;
    // TODO no forward references here, for the moment considered as a really rare situation
//    typeSymbol = (Symbol.TypeSymbol) result.symbol("Declaration", 14);
//    assertThat(typeSymbol.getSuperclass()).isSameAs(result.symbol("Superclass", 9));

    typeSymbol = (Symbol.TypeSymbol) result.symbol("Declaration", 22);
    assertThat(typeSymbol.getSuperclass()).isSameAs(result.symbol("Superclass", 22 - 2));

    typeSymbol = (Symbol.TypeSymbol) result.symbol("Declaration", 25);
    assertThat(typeSymbol.getSuperclass()).isSameAs(result.symbol("Superclass", 9));
  }

  @Test
  public void InterfaceDeclaration() {
    Result result = Result.createFor("declarations/InterfaceDeclaration");

    Symbol.TypeSymbol typeSymbol = (Symbol.TypeSymbol) result.symbol("Declaration");
    assertThat(typeSymbol.owner()).isSameAs(result.symbol("InterfaceDeclaration"));
    assertThat(typeSymbol.flags()).isEqualTo(Flags.PRIVATE | Flags.INTERFACE);
    assertThat(typeSymbol.getSuperclass()).isNull(); // TODO should it be java.lang.Object?
    assertThat(typeSymbol.getInterfaces()).containsExactly(
        result.symbol("FirstInterface"),
        result.symbol("SecondInterface"));

    Symbol.MethodSymbol methodSymbol = (Symbol.MethodSymbol) result.symbol("method");
    assertThat((methodSymbol.flags() & Flags.ACCESS_FLAGS) == Flags.PUBLIC).isTrue();

    Symbol.VariableSymbol variableSymbol = (Symbol.VariableSymbol) result.symbol("FIRST_CONSTANT");
    assertThat(variableSymbol.flags()).isEqualTo(Flags.PUBLIC);

    variableSymbol = (Symbol.VariableSymbol) result.symbol("SECOND_CONSTANT");
    assertThat(variableSymbol.flags()).isEqualTo(Flags.PUBLIC);

    typeSymbol = (Symbol.TypeSymbol) result.symbol("NestedClass");
    assertThat(typeSymbol.flags()).isEqualTo(Flags.PUBLIC);

    typeSymbol = (Symbol.TypeSymbol) result.symbol("NestedInterface");
    assertThat(typeSymbol.flags()).isEqualTo(Flags.PUBLIC | Flags.INTERFACE);

    typeSymbol = (Symbol.TypeSymbol) result.symbol("NestedEnum");
    assertThat(typeSymbol.flags()).isEqualTo(Flags.PUBLIC | Flags.ENUM);
  }

  @Test
  public void EnumDeclaration() {
    Result result = Result.createFor("declarations/EnumDeclaration");

    Symbol.TypeSymbol typeSymbol = (Symbol.TypeSymbol) result.symbol("Declaration");
    assertThat(typeSymbol.owner()).isSameAs(result.symbol("EnumDeclaration"));
    assertThat(typeSymbol.flags()).isEqualTo(Flags.PRIVATE | Flags.ENUM);
    assertThat(typeSymbol.getSuperclass()).isNull(); // FIXME should be java.lang.Enum
    assertThat(typeSymbol.getInterfaces()).containsExactly(
        result.symbol("FirstInterface"),
        result.symbol("SecondInterface"));

    Symbol.VariableSymbol variableSymbol = (Symbol.VariableSymbol) result.symbol("FIRST_CONSTANT");
    assertThat(variableSymbol.flags()).isEqualTo(Flags.PUBLIC | Flags.ENUM);

    variableSymbol = (Symbol.VariableSymbol) result.symbol("SECOND_CONSTANT");
    assertThat(variableSymbol.flags()).isEqualTo(Flags.PUBLIC | Flags.ENUM);
  }

  @Test
  public void AnnotationTypeDeclaration() {
    Result result = Result.createFor("declarations/AnnotationTypeDeclaration");

    Symbol.TypeSymbol typeSymbol = (Symbol.TypeSymbol) result.symbol("Declaration");
    assertThat(typeSymbol.owner()).isSameAs(result.symbol("AnnotationTypeDeclaration"));
    assertThat(typeSymbol.flags()).isEqualTo(Flags.PRIVATE | Flags.INTERFACE | Flags.ANNOTATION);
    assertThat(typeSymbol.getSuperclass()).isNull(); // TODO should it be java.lang.Object?
    assertThat(typeSymbol.getInterfaces()).isEmpty(); // FIXME should be java.lang.annotation.Annotation

    Symbol.VariableSymbol variableSymbol = (Symbol.VariableSymbol) result.symbol("FIRST_CONSTANT");
    assertThat(variableSymbol.flags()).isEqualTo(Flags.PUBLIC);

    variableSymbol = (Symbol.VariableSymbol) result.symbol("SECOND_CONSTANT");
    assertThat(variableSymbol.flags()).isEqualTo(Flags.PUBLIC);

    Symbol.MethodSymbol methodSymbol = (Symbol.MethodSymbol) result.symbol("value");
    assertThat(methodSymbol.flags()).isEqualTo(Flags.PUBLIC);

    typeSymbol = (Symbol.TypeSymbol) result.symbol("NestedClass");
    assertThat(typeSymbol.flags()).isEqualTo(Flags.PUBLIC);

    typeSymbol = (Symbol.TypeSymbol) result.symbol("NestedInterface");
    assertThat(typeSymbol.flags()).isEqualTo(Flags.PUBLIC | Flags.INTERFACE);

    typeSymbol = (Symbol.TypeSymbol) result.symbol("NestedEnum");
    assertThat(typeSymbol.flags()).isEqualTo(Flags.PUBLIC | Flags.ENUM);

    typeSymbol = (Symbol.TypeSymbol) result.symbol("NestedAnnotationType");
    assertThat(typeSymbol.flags()).isEqualTo(Flags.PUBLIC | Flags.INTERFACE | Flags.ANNOTATION);
  }

  @Test
  public void CompleteHierarchyOfTypes() {
    Result result = Result.createFor("CompleteHierarchyOfTypes");

    Symbol.TypeSymbol typeSymbol = (Symbol.TypeSymbol) result.symbol("Foo");
    assertThat(typeSymbol.getSuperclass()).isSameAs(result.symbol("Baz"));
  }

  @Test
  public void Accessibility() {
    Result result = Result.createFor("Accessibility");

    Symbol.TypeSymbol typeSymbol;
    typeSymbol = (Symbol.TypeSymbol) result.symbol("Target", 14);
    assertThat(typeSymbol.getSuperclass()).isSameAs(result.symbol("Member", 9));

    typeSymbol = (Symbol.TypeSymbol) result.symbol("Target", 29);
    assertThat(typeSymbol.getSuperclass()).isSameAs(result.symbol("Member", 20));
  }

  @Test
  public void Example() {
    Result.createFor("Example");
  }

  @Test
  public void ScopesAndSymbols() {
    Result.createFor("ScopesAndSymbols");
  }

  @Test
  public void TypesOfDeclarations() {
    Result.createFor("TypesOfDeclarations");
  }

  @Test
  public void Labels() {
    Result result = Result.createFor("references/Labels");

    assertThat(result.reference(8, 13)).isSameAs(result.symbol("label", 6));
    assertThat(result.reference(13, 13)).isSameAs(result.symbol("label", 11));
    assertThat(result.reference(18, 16)).isSameAs(result.symbol("label", 16));
  }

  @Test
  public void FieldAccess() {
    Result result = Result.createFor("references/FieldAccess");

    assertThat(result.reference(9, 5)).isSameAs(result.symbol("field"));

    assertThat(result.reference(10, 5)).isSameAs(result.symbol("FieldAccess"));
    assertThat(result.reference(10, 17)).isSameAs(result.symbol("field"));

    // FIXME
//    assertThat(result.reference(11, 5)).isSameAs(/*package "references"*/);

    assertThat(result.reference(13, 5)).isSameAs(result.symbol("FirstStaticNestedClass"));
    assertThat(result.reference(13, 28)).isSameAs(result.symbol("field_in_FirstStaticNestedClass"));

    assertThat(result.reference(14, 5)).isSameAs(result.symbol("FirstStaticNestedClass"));
    assertThat(result.reference(14, 28)).isSameAs(result.symbol("SecondStaticNestedClass"));
    assertThat(result.reference(14, 52)).isSameAs(result.symbol("field_in_SecondStaticNestedClass"));

    assertThat(result.reference(15, 5)).isSameAs(result.symbol("field"));
    assertThat(result.reference(15, 11)).isSameAs(result.symbol("field_in_FirstStaticNestedClass"));

    assertThat(result.reference(16, 5)).isSameAs(result.symbol("field"));
    assertThat(result.reference(16, 11)).isSameAs(result.symbol("field_in_Superclass"));
  }

  @Test
  public void MethodParameterAccess() {
    Result result = Result.createFor("references/MethodParameterAccess");

    result.symbol("param");

    assertThat(result.reference(7, 5)).isSameAs(result.symbol("param"));

    assertThat(result.reference(8, 5)).isSameAs(result.symbol("param"));
    assertThat(result.reference(8, 11)).isSameAs(result.symbol("field"));
  }

  @Test
  public void ExpressionInAnnotation() {
    Result result = Result.createFor("references/ExpressionInAnnotation");

    assertThat(result.reference(3, 19)).isSameAs(result.symbol("ExpressionInAnnotation"));
    assertThat(result.reference(3, 42)).isSameAs(result.symbol("VALUE"));
  }

  @Test
  public void MethodCall() {
    Result result = Result.createFor("references/MethodCall");

    assertThat(result.reference(10, 5)).isSameAs(result.symbol("target"));
  }

}
