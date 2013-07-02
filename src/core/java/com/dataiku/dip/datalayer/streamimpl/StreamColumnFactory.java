package com.dataiku.dip.datalayer.streamimpl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.dataiku.dip.datalayer.Column;
import com.dataiku.dip.datalayer.ColumnFactory;
import com.dataiku.dip.datalayer.memimpl.MemColumn;

public class StreamColumnFactory implements ColumnFactory {
    @Override
    public synchronized Column column(String name) {
        StreamColumn c = map.get(name);
        if (c == null) {
            c = new StreamColumn();
            c.factory = this;
            c.name = name;
            list.add(c);
            map.put(name, c);
        }
        return c;
    }
    @Override
    public synchronized StreamColumn columnAfter(String before, String after) {
        StreamColumn c = map.get(after);
        if (c == null) {
            c = new StreamColumn();
            c.factory = this;
            c.name = after;
            StreamColumn beforeCD = map.get(before);
            if (beforeCD != null) {
                int index = list.indexOf(beforeCD);
                list.add(index + 1, c);
            } else {
                list.add(c);
            }
            map.put(after, c);
        }
        return c;
    }
    @Override
    public Column columnBefore(String after, String newCol) {
        if (after == null) {
            return column(newCol);
        }
        StreamColumn c = map.get(newCol);
        if (c == null) {
            c = new StreamColumn();
            c.factory = this;
            c.name = newCol;
            StreamColumn afterCD = map.get(after);
            if (afterCD != null) {
                int index = list.indexOf(afterCD);
                list.add(index, c);
            } else {
                list.add(c);
            }
            map.put(newCol, c);
        }
        return c;
    }
    @Override
    public Column getColumnAfter(String current) {
        StreamColumn beforeCD = map.get(current);

        if (beforeCD != null) {
            int index = list.indexOf(beforeCD);
            if (index + 1 < list.size()) {
                return list.get(index+1);
            }
        }
        return null;
    }

    @Override
    public synchronized void deleteColumn(String name) {
        list.remove(map.get(name));
        map.remove(name);
    }

    synchronized void renameColumn(StreamColumn sc, String newName) {
        map.remove(sc.getName());
        sc.name = newName;
        map.put(newName, sc);
    }

    List<StreamColumn> list = new ArrayList<StreamColumn>();
    Map<String, StreamColumn> map = new LinkedHashMap<String, StreamColumn>();

    @Override
    public synchronized Iterable<Column> columns() {
        /* Copy because we might modify the collection while iterating */
        List<Column> l = new ArrayList<Column>();
        l.addAll(list);
        return l;
    }
  
}
