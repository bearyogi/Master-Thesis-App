
package com.mm.masterthesis.jaas;

import java.io.Serializable;
import java.security.Principal;

public class TypedPrincipal implements Principal, Serializable
{
    protected String name;
    protected int type;
    public static final int USER = 1;
    public static final int DOMAIN = 2;
    public static final int GROUP = 3;
    public static final int UNKNOWN = 4;
    protected static final String[] typeMap;

    public TypedPrincipal(final String name, final int type) {
        if (name == null) {
            throw new NullPointerException("Illegal null name");
        }
        if (type < 1 || type > 4) {
            throw new IllegalArgumentException("Bad type value");
        }
        this.name = name;
        this.type = type;
    }

    public TypedPrincipal(final String s) {
        this(s, 4);
    }

    public String getName() {
        return this.name;
    }

    public int getType() {
        return this.type;
    }

    public String toString() {
        return "TypedPrincipal: " + this.name + " [" + TypedPrincipal.typeMap[this.type] + "]";
    }

    public boolean equals(final Object o) {
        if (o == null) {
            return false;
        }
        if (this == o) {
            return true;
        }
        if (o instanceof TypedPrincipal) {
            final TypedPrincipal typedPrincipal = (TypedPrincipal)o;
            return typedPrincipal.getName().equals(this.getName()) && typedPrincipal.getType() == this.getType();
        }
        return o instanceof Principal && ((Principal)o).getName().equals(this.getName());
    }

    public int hashCode() {
        return this.name.hashCode();
    }

    static {
        typeMap = new String[] { null, "USER", "DOMAIN", "GROUP", "UNKNOWN" };
    }
}
