package com.dataiku.dip.datasets;

import java.io.Serializable;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.dataiku.dip.utils.ErrorContext;
import com.dataiku.dip.utils.Pair;

public class Schema implements Serializable {
    private static final long serialVersionUID = 1L;

    public static enum Type {
        /** 8 bits */
        TINYINT("tinyint"),
        /** 16 bits */
        SMALLINT("smallint"),
        /** 32 bits */
        INT("int"),
        /** 64 bits */
        BIGINT("bigint"),
        /** 32 bits */
        FLOAT("float"),
        /** 64 bits */
        DOUBLE("double"),
        BOOLEAN("boolean"),
        /** No size limitation */
        STRING("string"),

        DATE("date"),

        ARRAY("array"),
        MAP("map");

        Type(String name) {
            this.name = name;
        }
        private String name;

        public String getName() {
            return name;
        }

        public boolean isPrimitive() {
            switch(this)  {
            case ARRAY:
            case MAP:
                return false;
            default:
                return true;
            }
        }

        public static Type forName(String name) {
            if (ARRAY_PATTERN.matcher(name).matches()) {
                return Type.ARRAY;
            }
            if (MAP_PATTERN.matcher(name).matches()) {
                return Type.MAP;
            }
            for (Type t : Type.values()) {
                if (t.name.equals(name)) {
                    return t;
                }
            }
            throw ErrorContext.iae("Type not found: " +name);
        }

    }

    public static Type fromSQLType(int sqlType) {
        switch (sqlType) {

        case Types.BIGINT:
            return Type.BIGINT;
        case Types.BOOLEAN:
            return Type.BOOLEAN;
        case Types.CHAR:
            return Type.STRING;
        case Types.DECIMAL:
            return Type.FLOAT;
        case Types.DOUBLE:
            return Type.DOUBLE;
        case Types.FLOAT:
            return Type.FLOAT;
        case Types.INTEGER:
            return Type.INT;
        case Types.SMALLINT:
            return Type.SMALLINT;
        case Types.TINYINT:
            return Type.TINYINT;
        case Types.LONGVARCHAR:
            return Type.STRING;
        case Types.VARCHAR:
            return Type.STRING;
        case Types.NULL:
            return Type.STRING;
        case Types.DATE:
        case Types.TIMESTAMP:
            return Type.DATE;

        case Types.TIME:
            //???
            return  Type.STRING;

        case Types.ARRAY:
        case Types.BINARY:
        case Types.BIT:
        case Types.BLOB:
        case Types.CLOB:
        case Types.DATALINK:
        case Types.DISTINCT:
        case Types.JAVA_OBJECT:
        case Types.LONGNVARCHAR:
        case Types.LONGVARBINARY:
        case Types.NCHAR:
        case Types.NCLOB:
        case Types.NUMERIC:
        case Types.NVARCHAR:
        case Types.OTHER:
        case Types.REAL:
        case Types.REF:
        case Types.ROWID:
        case Types.SQLXML:
        case Types.STRUCT:

        case Types.VARBINARY:
        default:
            return Type.STRING; // defaulting to string
            //throw new IllegalArgumentException("Can't handle SQL type " + sqlType);
        }
    }

    public final static Pattern ARRAY_PATTERN = Pattern.compile("array<\\s*([^,>]*)\\s*>");
    public final static Pattern MAP_PATTERN = Pattern.compile("map<\\s*([^,>]*)\\s*,\\s*([^,>]*)\\s*>");

    public static String arrayContent(String type) {
        Matcher matcher = ARRAY_PATTERN.matcher(type);
        return matcher.matches() ? matcher.group(1) : null;
    }

    public static Pair<String, String> mapContent(String type) {
        Matcher matcher = MAP_PATTERN.matcher(type);
        return matcher.matches() ? new Pair<String, String>(matcher.group(1), matcher.group(2)) : null;
    }

    public Schema() {
    }
    public Schema(Schema other) {
        for (SchemaColumn c : other.columns) {
            columns.add(new SchemaColumn(c));
        }
    }

    public Schema(List<SchemaColumn> other) {
        this.columns = other;
    }

    public static class SchemaColumn implements Serializable {
        private static final long serialVersionUID = 1L;

        public SchemaColumn() {}
        public SchemaColumn(SchemaColumn other) {
            this.name = other.name;
            this.typeName = other.typeName;
            this.comment = other.comment;
            this.maxLength = other.maxLength;
        }
        public SchemaColumn(String name, String type) {
            this.name = name;
            this.typeName = type;
        }

        public String getName() {
            return name;
        }
        public SchemaColumn setName(String name) {
            this.name = name;
            return this;
        }
        public Type getType() {
            return Type.forName(typeName);
        }

        public java.lang.reflect.Type getJavaType() {

            Type type = getType();
            switch (type) {
                case TINYINT:
                    return Byte.class;

                case SMALLINT:
                    return Short.class;
                case INT:
                    return Integer.class;
                case BIGINT:
                    return Long.class;
                case FLOAT:
                    return Float.class;
                case DOUBLE:
                    return Double.class;
                case BOOLEAN:
                    return Boolean.class;
                case STRING:
                    return String.class;
                case DATE:
                    return String.class;
                case ARRAY:
                    return String.class;
                case MAP:
                    return String.class;
                default:
                    return Object.class;
            }
        }
        
        public String getTypeName() {
            return typeName;
        }
        public void setTypeName(String type) {
            this.typeName = type;
        }

        public String getComment() {
            return comment;
        }
        public void setComment(String comment) {
            this.comment = comment;
        }
        
        public int getMaxLength() {
            return maxLength;
        }
        public void setMaxLength(int maxLength) {
            this.maxLength = maxLength;
        }

        private String name;
        private String typeName;
        private String comment;
        private int maxLength = 1000;
    }

    public boolean isUserModified() {
        return userModified;
    }
    public void setUserModified(boolean userModified) {
        this.userModified = userModified;
    }

    public List<SchemaColumn> getColumns() {
        return columns;
    }

    public void addColumn(String name, String type) {
        SchemaColumn column = new SchemaColumn(name, type);
        columns.add(column);
    }

    public SchemaColumn getColumn(String name) {
        for (SchemaColumn col : getColumns()) {
            if (col.getName().equals(name)) {
                return col;
            }
        }
        return null;
    }

    private boolean userModified;
    private List<SchemaColumn> columns = new ArrayList<SchemaColumn>();
}
