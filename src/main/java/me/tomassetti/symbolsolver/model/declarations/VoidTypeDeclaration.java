package me.tomassetti.symbolsolver.model.declarations;

import com.github.javaparser.ast.Node;
import me.tomassetti.symbolsolver.model.*;
import me.tomassetti.symbolsolver.model.usages.TypeUsage;

import java.util.Collections;
import java.util.List;

/**
 * Created by federico on 17/08/15.
 */
public class VoidTypeDeclaration implements TypeDeclaration {
    @Override
    public String getQualifiedName() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Context getContext() {
        throw new UnsupportedOperationException();
    }

    @Override
    public TypeUsage getUsage(Node node) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isAssignableBy(TypeUsage typeUsage, TypeSolver typeSolver) {
        return false;
    }

    @Override
    public boolean isTypeVariable() {
        return false;
    }

    @Override
    public boolean canBeAssignedTo(TypeDeclaration other, TypeSolver typeSolver) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isAssignableBy(TypeDeclaration other, TypeSolver typeSolver) {
        throw new UnsupportedOperationException();
    }

    @Override
    public FieldDeclaration getField(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean hasField(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SymbolReference<? extends ValueDeclaration> solveSymbol(String substring, TypeSolver typeSolver) {
        return SymbolReference.unsolved(ValueDeclaration.class);
    }

    @Override
    public SymbolReference<TypeDeclaration> solveType(String substring, TypeSolver typeSolver) {
        return SymbolReference.unsolved(TypeDeclaration.class);
    }

    @Override
    public List<TypeDeclaration> getAllAncestors(TypeSolver typeSolver) {
        return Collections.emptyList();
    }

    @Override
    public String getName() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isField() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isParameter() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isVariable() {
        return false;
    }

    @Override
    public boolean isType() {
        return true;
    }

    @Override
    public boolean isClass() {
        return false;
    }

    @Override
    public boolean isInterface() {
        return false;
    }

    @Override
    public List<TypeParameter> getTypeParameters() {
        return Collections.emptyList();
    }
}
