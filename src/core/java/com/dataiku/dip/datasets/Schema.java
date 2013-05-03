package com.dataiku.dip.datasets;

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
        String name;

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
