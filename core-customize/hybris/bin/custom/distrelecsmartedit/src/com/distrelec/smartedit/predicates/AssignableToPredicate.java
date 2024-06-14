package com.distrelec.smartedit.predicates;

import java.util.function.Predicate;

public class AssignableToPredicate implements Predicate<Object> {

    private Class<?> assignableToClass;

    @Override
    public boolean test(Object object) {
        return assignableToClass.isAssignableFrom(object.getClass());
    }

    public Class<?> getAssignableToClass() {
        return assignableToClass;
    }

    public void setAssignableToClass(Class<?> assignableToClass) {
        this.assignableToClass = assignableToClass;
    }
}


