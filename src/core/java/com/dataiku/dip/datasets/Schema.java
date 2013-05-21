package com.dataiku.dip.datasets;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import com.dataiku.dip.datalayer.Column;
import com.dataiku.dip.utils.ErrorContext;
import com.dataiku.dip.utils.Params;

public class Schema {
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
        ARRAY("array");

        Type(String name) {
            this.name = name;
        }
        private String name;

        public String getName() {
            return name;
        }

        public static Type forName(String name) {
            if (ARRAY_PATTERN.matcher(name).matches()) {
                return Type.ARRAY;
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
        case Types.TINYINT:
            return Type.TINYINT;
        case Types.LONGVARCHAR:
            return Type.STRING;
        case Types.VARCHAR:
            return Type.STRING;
        case Types.NULL:
            return Type.STRING;


        case Types.ARRAY:
        case Types.BINARY:
        case Types.BIT:
        case Types.BLOB:
        case Types.CLOB:
        case Types.DATALINK:
        case Types.DATE:
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
        case Types.SMALLINT:
        case Types.SQLXML:
        case Types.STRUCT:
        case Types.TIME:
        case Types.TIMESTAMP:
        case Types.VARBINARY:
        default:
            throw new IllegalArgumentException("Can't handle SQL type " + sqlType);
        }
    }

    public final static Pattern ARRAY_PATTERN = Pattern.compile("array<([^,>]*)>");

    public Schema() {
    }
    public Schema(Schema other) {
        for (SchemaColumn c : other.columns) {
            columns.add(new SchemaColumn(c.getName(), c.typeName()));
        }
    }
    public Schema(List<SchemaColumn> other) {
        this.columns = other;
    }

    public static Schema fromParams(Params wp) {
        Schema schema = new Schema();
        for (String colIdx : wp.getChildrenAsIntList("columns")) {
            SchemaColumn c = new SchemaColumn();
            c.includeInOutput(true); // TODO
            c.setName(wp.getMandParam("columns." + colIdx + ".name"));
            c.typeName(wp.getMandParam("columns." + colIdx + ".type"));
            schema.columns.add(c);
        }
        return schema;
    }

    public static class SchemaColumn extends Column {
        public SchemaColumn() {}
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
        public String typeName() {
            return typeName;
        }
        public void typeName(String typeName) {
            this.typeName = typeName;
        }
        public boolean includeInOutput() {
            return includeInOutput;
        }
        public void includeInOutput(boolean includeInOutput) {
            this.includeInOutput = includeInOutput;
        }

        private String name;
        private boolean includeInOutput;
        private String typeName;
    }

    public List<SchemaColumn> columns = new ArrayList<SchemaColumn>();
}
